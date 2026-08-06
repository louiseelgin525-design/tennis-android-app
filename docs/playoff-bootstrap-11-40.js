/*
 * Reliable full-playoff loader for tennis-android-app
 * Version 2.1.0
 *
 * Purpose:
 * - never leave the old "participants formed" placeholder after groups finish;
 * - load the full 11–40 player module even when script order/cache differs;
 * - render again after Firebase updates, tab switches and page restoration;
 * - show a useful diagnostic instead of silently falling back.
 */
(function (global) {
    'use strict';

    const VERSION = '2.1.0';
    const FORMAT = 'groups_playoff';
    const MIN_PLAYERS = 11;
    const MAX_PLAYERS = 40;
    const SCRIPT_VERSION = '2.1.0';

    let loadingPromise = null;
    let renderTimer = 0;
    let observer = null;
    let rendering = false;

    function asArray(value) {
        if (Array.isArray(value)) return value.filter(Boolean);
        if (value && typeof value === 'object') return Object.values(value).filter(Boolean);
        return [];
    }

    function getDraft() {
        try {
            if (typeof clubData !== 'undefined' && clubData && clubData.draft) return clubData.draft;
        } catch (_) {}
        try {
            if (global.clubData && global.clubData.draft) return global.clubData.draft;
        } catch (_) {}
        return null;
    }

    function getContainer() {
        return document.getElementById('active-tournament-container');
    }

    function playerCount(draft) {
        return asArray(draft && draft.playoffParticipants).length ||
            Number(draft && draft.playersCount) ||
            0;
    }

    function playoffVisible(container, draft) {
        if (!container || !draft) return false;
        if (container.querySelector('.pf-playoff-root')) return true;
        if (container.querySelector('.gp-playoff-placeholder, .gp-playoff-loading, .gp-playoff-error')) return true;
        const active = document.querySelector('.gp-tab.active');
        if (active && String(active.textContent || '').trim() === 'Плей-офф') return true;
        return draft.currentStage === 'playoff';
    }

    function baseUrl() {
        const current = document.currentScript;
        if (current && current.src) return new URL('.', current.src).href;
        const scripts = Array.from(document.scripts || []);
        const own = scripts.find(script => /playoff-bootstrap-11-40\.js/i.test(script.src || ''));
        if (own && own.src) return new URL('.', own.src).href;
        return new URL('./', document.baseURI).href;
    }

    function ensureStylesheet() {
        const existing = Array.from(document.querySelectorAll('link[rel="stylesheet"]'))
            .find(link => /playoff-full-11-40\.css/i.test(link.href || ''));
        if (existing) return;
        const link = document.createElement('link');
        link.rel = 'stylesheet';
        link.href = new URL(`playoff-full-11-40.css?v=${SCRIPT_VERSION}`, baseUrl()).href;
        document.head.appendChild(link);
    }

    function showLoading(container) {
        if (!container || container.querySelector('.pf-playoff-root')) return;
        container.querySelectorAll('.gp-playoff-placeholder, .gp-playoff-error').forEach(node => node.remove());
        let card = container.querySelector('.gp-playoff-loading');
        if (!card) {
            card = document.createElement('div');
            card.className = 'gp-playoff-placeholder gp-playoff-loading';
            card.innerHTML = `
                <div class="gp-placeholder-icon">🏆</div>
                <strong>Формируется сетка плей-офф…</strong>
                <span>Участники и маршруты матчей загружаются автоматически.</span>`;
            container.appendChild(card);
        }
    }

    function showError(container, error, draft) {
        if (!container) return;
        const count = playerCount(draft);
        const message = error && error.message ? error.message : String(error || 'неизвестная ошибка');
        container.querySelectorAll('.gp-playoff-placeholder, .gp-playoff-loading, .gp-playoff-error').forEach(node => node.remove());
        const card = document.createElement('div');
        card.className = 'gp-playoff-placeholder gp-playoff-error';
        card.innerHTML = `
            <div class="gp-placeholder-icon">⚠️</div>
            <strong>Не удалось открыть сетку плей-офф</strong>
            <span>Игроков: ${count}. Ошибка: ${escapeHtml(message)}</span>
            <button type="button" class="gp-primary-button" data-playoff-retry>Повторить загрузку</button>`;
        card.querySelector('[data-playoff-retry]').addEventListener('click', function () {
            loadingPromise = null;
            ensureAndRender(true);
        });
        container.appendChild(card);
    }

    function escapeHtml(value) {
        return String(value == null ? '' : value)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#039;');
    }

    function loadFullModule(forceReload) {
        if (global.FullPlayoff11to40 && typeof global.FullPlayoff11to40.renderForDraft === 'function') {
            return Promise.resolve(global.FullPlayoff11to40);
        }
        if (loadingPromise && !forceReload) return loadingPromise;

        ensureStylesheet();

        loadingPromise = new Promise((resolve, reject) => {
            const oldScripts = Array.from(document.scripts || [])
                .filter(script => /playoff-full-11-40\.js/i.test(script.src || ''));

            if (forceReload) {
                oldScripts.forEach(script => script.remove());
                try {
                    delete global.__FULL_PLAYOFF_11_40_INSTALLED__;
                    delete global.FullPlayoff11to40;
                } catch (_) {}
            }

            const script = document.createElement('script');
            script.src = new URL(
                `playoff-full-11-40.js?v=${SCRIPT_VERSION}${forceReload ? `&r=${Date.now()}` : ''}`,
                baseUrl()
            ).href;
            script.async = false;
            script.onload = function () {
                const api = global.FullPlayoff11to40;
                if (!api || typeof api.renderForDraft !== 'function') {
                    reject(new Error('файл загружен, но модуль FullPlayoff11to40 не зарегистрирован'));
                    return;
                }
                resolve(api);
            };
            script.onerror = function () {
                reject(new Error(`не загружается файл ${script.src}`));
            };
            document.head.appendChild(script);
        }).finally(() => {
            setTimeout(() => { loadingPromise = null; }, 1000);
        });

        return loadingPromise;
    }

    function renderWithApi(api, container, draft) {
        const count = playerCount(draft);
        if (count < MIN_PLAYERS || count > MAX_PLAYERS) {
            throw new Error(`поддерживаются турниры от ${MIN_PLAYERS} до ${MAX_PLAYERS} игроков, получено ${count}`);
        }
        if (!draft.groupStageCompleted) {
            throw new Error('групповой этап ещё не завершён');
        }

        if (typeof api.ensureBracketForDraft === 'function') {
            const ensured = api.ensureBracketForDraft(draft);
            if (!ensured || !ensured.bracket) {
                throw new Error('модуль не смог сформировать объект сетки');
            }
        }

        const rendered = api.renderForDraft(container, draft);
        if (!rendered || !container.querySelector('.pf-playoff-root')) {
            throw new Error('модуль вернул пустой результат вместо сетки');
        }

        container.querySelectorAll('.gp-playoff-placeholder, .gp-playoff-loading, .gp-playoff-error')
            .forEach(node => node.remove());
        return true;
    }

    async function ensureAndRender(forceReload) {
        if (rendering) return false;

        const draft = getDraft();
        const container = getContainer();
        if (!draft || draft.format !== FORMAT || !draft.groupStageCompleted || !container) return false;
        if (!playoffVisible(container, draft)) return false;

        rendering = true;
        try {
            showLoading(container);
            const api = await loadFullModule(!!forceReload);
            return renderWithApi(api, container, draft);
        } catch (error) {
            console.error('[playoff-bootstrap-11-40]', error);
            showError(container, error, draft);
            return false;
        } finally {
            rendering = false;
        }
    }

    function schedule(delay) {
        clearTimeout(renderTimer);
        renderTimer = setTimeout(() => ensureAndRender(false), Number(delay) || 0);
    }

    function install() {
        ensureStylesheet();

        document.addEventListener('click', event => {
            if (event.target && event.target.closest && event.target.closest('.gp-tab')) {
                schedule(0);
                setTimeout(() => ensureAndRender(false), 120);
                setTimeout(() => ensureAndRender(false), 500);
            }
        }, true);

        global.addEventListener?.('load', () => schedule(0));
        global.addEventListener?.('pageshow', () => schedule(0));
        document.addEventListener('visibilitychange', () => {
            if (!document.hidden) schedule(0);
        });

        const container = getContainer();
        if (container && typeof MutationObserver !== 'undefined') {
            observer = new MutationObserver(() => {
                const draft = getDraft();
                if (!draft || draft.format !== FORMAT || !draft.groupStageCompleted) return;
                if (container.querySelector('.gp-playoff-placeholder, .gp-playoff-loading, .gp-playoff-error') &&
                    !container.querySelector('.pf-playoff-root')) {
                    schedule(25);
                }
            });
            observer.observe(container, { childList: true, subtree: false });
        }

        [0, 50, 150, 400, 900, 1800, 3500].forEach(delay => {
            setTimeout(() => ensureAndRender(false), delay);
        });
    }

    global.PlayoffBootstrap1140 = {
        VERSION,
        ensureAndRender,
        render: function (container, draft) {
            if (container && draft) {
                showLoading(container);
                setTimeout(() => ensureAndRender(false), 0);
                return true;
            }
            return false;
        }
    };

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', install, { once: true });
    } else {
        install();
    }
})(typeof window !== 'undefined' ? window : globalThis);
