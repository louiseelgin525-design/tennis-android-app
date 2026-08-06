/*
 * Full Groups + Playoff bracket for tennis-android-app
 * Version: 2.0.2 (2026-08-06)
 *
 * Adds one pan/zoom bracket field and exact-place classification for 11–40 players.
 * The approved 12-player / 4x3 template is reproduced exactly as P-M1…P-M26.
 * Other sizes use a deterministic full-classification bracket: group winners receive
 * the first byes, every loss routes to a placement branch, and every final place is played.
 */
(function (global) {
    'use strict';

    const VERSION = '2.0.0';
    const UI_VERSION = '2.0.2';
    const FORMAT = 'groups_playoff';
    const MIN_PLAYERS = 11;
    const MAX_PLAYERS = 40;
    const MATCH_PREFIX = 'P-M';
    const CARD_W = 236;
    const CARD_H = 112;
    const X_GAP = 310;
    const Y_GAP = 138;
    const PADDING = 48;

    const viewportState = new Map();
    let renderGuard = false;
    let persistTimer = null;
    let observer = null;
    let enhanceFrame = 0;

    function arr(value) {
        if (Array.isArray(value)) return value.filter(Boolean);
        if (value && typeof value === 'object') return Object.values(value).filter(Boolean);
        return [];
    }

    function clone(value) {
        if (typeof structuredClone === 'function') {
            try { return structuredClone(value); } catch (_) {}
        }
        return JSON.parse(JSON.stringify(value));
    }

    function num(value, fallback) {
        const parsed = Number(value);
        return Number.isFinite(parsed) ? parsed : fallback;
    }

    function esc(value) {
        return String(value == null ? '' : value)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#039;');
    }

    function unique(prefix) {
        return `${prefix || 'id'}-${Date.now()}-${Math.random().toString(36).slice(2, 10)}`;
    }

    function scorePlayed(score) {
        if (score === 'W' || score === 'L') return true;
        const match = String(score || '').match(/^\s*(\d+)\s*:\s*(\d+)\s*$/);
        return !!match && Number(match[1]) !== Number(match[2]);
    }

    function scoreResult(score, p1, p2) {
        if (!scorePlayed(score)) return null;
        if (score === 'W') return { winner: Number(p1), loser: Number(p2), technical: true };
        if (score === 'L') return { winner: Number(p2), loser: Number(p1), technical: true };
        const parts = String(score).split(':').map(Number);
        return parts[0] > parts[1]
            ? { winner: Number(p1), loser: Number(p2), technical: false }
            : { winner: Number(p2), loser: Number(p1), technical: false };
    }

    function sourceSeed(participant, position) {
        return {
            type: 'seed',
            playerIndex: Number(participant.playerIndex),
            seedPosition: Number(position),
            groupId: Number(participant.groupId),
            groupPlace: Number(participant.groupPlace),
            label: `Г${Number(participant.groupId)} · ${Number(participant.groupPlace)} место`
        };
    }

    function sourceMatch(matchId, outcome) {
        return {
            type: 'match',
            matchId: String(matchId),
            outcome: outcome === 'L' ? 'L' : 'W',
            label: `${outcome === 'L' ? 'Проигравший' : 'Победитель'} ${String(matchId).replace('P-', '')}`
        };
    }

    function sourceLabel(source) {
        if (!source) return 'Участник не определён';
        if (source.type === 'seed') return source.label || `Позиция ${source.seedPosition}`;
        return source.label || `${source.outcome === 'L' ? 'Проигравший' : 'Победитель'} ${source.matchId}`;
    }

    function participantKey(participant) {
        return `${Number(participant.groupId)}:${Number(participant.groupPlace)}`;
    }

    function buildParticipantSeedOrder(participants) {
        const input = arr(participants).map(item => ({
            playerIndex: Number(item.playerIndex),
            groupId: Number(item.groupId),
            groupPlace: Number(item.groupPlace),
            groupWins: Number(item.groupWins) || 0,
            seedRating: Number(item.seedRating) || 0
        }));
        const groupIds = Array.from(new Set(input.map(item => item.groupId))).sort((a, b) => a - b);
        const maxPlace = Math.max(0, ...input.map(item => item.groupPlace));
        const output = [];
        for (let place = 1; place <= maxPlace; place++) {
            let row = groupIds
                .map(groupId => input.find(item => item.groupId === groupId && item.groupPlace === place))
                .filter(Boolean);
            if (place > 1 && row.length > 1) {
                const shift = Math.floor(row.length / 2);
                row = row.slice(shift).concat(row.slice(0, shift));
            }
            output.push(...row);
        }
        const seen = new Set(output.map(participantKey));
        input.forEach(item => { if (!seen.has(participantKey(item))) output.push(item); });
        return output;
    }

    function exact12Positions(participants) {
        const byKey = new Map(arr(participants).map(item => [participantKey(item), item]));
        const keys = [
            '1:1', '2:1', '3:1', '4:1',
            '3:2', '4:2', '1:2', '2:2',
            '3:3', '4:3', '1:3', '2:3'
        ];
        const result = keys.map(key => byKey.get(key));
        return result.every(Boolean) ? result : null;
    }

    function makeMatchFactory(bracket) {
        return function createMatch(options) {
            const number = bracket.matches.length + 1;
            const id = `${MATCH_PREFIX}${number}`;
            const match = {
                id,
                number,
                stage: options.stage || 'placement',
                title: options.title || '',
                placeStart: Number(options.placeStart) || 1,
                placeEnd: Number(options.placeEnd) || Number(options.placeStart) || 2,
                sources: [clone(options.source1), clone(options.source2)],
                p1: null,
                p2: null,
                score: null,
                winner: null,
                loser: null,
                technical: false,
                tableIndex: null,
                readyAt: null,
                createdAt: bracket.createdAt + number,
                completedAt: null,
                level: Number(options.level) || 0,
                yKey: Number(options.yKey) || number,
                layout: options.layout ? clone(options.layout) : null
            };
            bracket.matches.push(match);
            return match;
        };
    }

    function maxSourceLevel(source, matchById) {
        if (!source || source.type !== 'match') return -1;
        const match = matchById.get(String(source.matchId));
        return match ? Number(match.level) || 0 : -1;
    }

    function pairSourcesForPartition(sources) {
        const remaining = sources.slice();
        const pairs = [];
        while (remaining.length >= 2) {
            const first = remaining.shift();
            let partnerIndex = remaining.length - 1;
            if (first && first.type === 'seed') {
                for (let i = remaining.length - 1; i >= 0; i--) {
                    const candidate = remaining[i];
                    if (!(candidate && candidate.type === 'seed' && Number(candidate.groupId) === Number(first.groupId))) {
                        partnerIndex = i;
                        break;
                    }
                }
            }
            const second = remaining.splice(partnerIndex, 1)[0];
            pairs.push([first, second]);
        }
        return { pairs, bye: remaining.length ? remaining[0] : null };
    }

    function classificationLabel(startPlace, endPlace, count, preliminary) {
        if (preliminary) return 'Предварительный раунд';
        if (startPlace === 1) {
            if (count === 2) return 'Финал';
            if (count === 4) return '1/2 финала';
            if (count === 8) return '1/4 финала';
            if (count === 16) return '1/8 финала';
            if (count === 32) return '1/16 финала';
            return `Основная ветка · места 1–${endPlace}`;
        }
        if (count === 2) return `За ${startPlace}–${endPlace} места`;
        return `Распределение ${startPlace}–${endPlace} мест`;
    }

    function rankSourcesRecursive(bracket, createMatch, sources, startPlace, endPlace, finalSources, path) {
        const list = sources.slice();
        const count = list.length;
        if (count === 0) return;
        if (count === 1) {
            finalSources.push({ place: startPlace, source: clone(list[0]) });
            return;
        }

        const split = pairSourcesForPartition(list);
        const upperSources = [];
        const lowerSources = [];
        if (split.bye) upperSources.push(split.bye);
        const matchById = new Map(bracket.matches.map(match => [match.id, match]));
        const pairCount = split.pairs.length;
        split.pairs.forEach((pair, index) => {
            const sourceLevel = Math.max(maxSourceLevel(pair[0], matchById), maxSourceLevel(pair[1], matchById));
            const yKey = startPlace + ((index + 1) * (count / (pairCount + 1)));
            const match = createMatch({
                source1: pair[0],
                source2: pair[1],
                stage: startPlace === 1 ? 'main' : 'placement',
                title: classificationLabel(startPlace, endPlace, count, false),
                placeStart: startPlace,
                placeEnd: endPlace,
                level: sourceLevel + 1,
                yKey
            });
            upperSources.push(sourceMatch(match.id, 'W'));
            lowerSources.push(sourceMatch(match.id, 'L'));
        });

        const upperCount = upperSources.length;
        rankSourcesRecursive(
            bracket,
            createMatch,
            upperSources,
            startPlace,
            startPlace + upperCount - 1,
            finalSources,
            `${path}W`
        );
        rankSourcesRecursive(
            bracket,
            createMatch,
            lowerSources,
            startPlace + upperCount,
            endPlace,
            finalSources,
            `${path}L`
        );
    }

    function buildGenericBracket(participants, tournamentId) {
        const seeded = buildParticipantSeedOrder(participants);
        const bracket = {
            version: VERSION,
            algorithm: 'full_classification_v1',
            template: `generic_${seeded.length}`,
            tournamentId: tournamentId || unique('tournament'),
            playerCount: seeded.length,
            createdAt: Date.now(),
            updatedAt: Date.now(),
            status: 'active',
            participants: seeded.map((item, index) => ({ ...item, position: index + 1 })),
            matches: [],
            finalSources: [],
            activeMatches: [],
            completedAt: null
        };
        const createMatch = makeMatchFactory(bracket);
        const seeds = seeded.map((item, index) => sourceSeed(item, index + 1));
        const winners = seeds.filter(source => Number(source.groupPlace) === 1);
        const others = seeds.filter(source => Number(source.groupPlace) !== 1);
        const prelim = pairSourcesForPartition(others);
        const upperSources = winners.slice();
        const lowerSources = [];
        if (prelim.bye) upperSources.push(prelim.bye);
        const prelimCount = prelim.pairs.length;
        prelim.pairs.forEach((pair, index) => {
            const match = createMatch({
                source1: pair[0],
                source2: pair[1],
                stage: 'preliminary',
                title: classificationLabel(1, seeded.length, seeded.length, true),
                placeStart: 1,
                placeEnd: seeded.length,
                level: 0,
                yKey: 1 + ((index + 1) * (seeded.length / (prelimCount + 1)))
            });
            upperSources.push(sourceMatch(match.id, 'W'));
            lowerSources.push(sourceMatch(match.id, 'L'));
        });
        const upperEnd = upperSources.length;
        rankSourcesRecursive(bracket, createMatch, upperSources, 1, upperEnd, bracket.finalSources, 'U');
        rankSourcesRecursive(bracket, createMatch, lowerSources, upperEnd + 1, seeded.length, bracket.finalSources, 'D');
        assignGenericLayout(bracket);
        return bracket;
    }

    function buildExact12Bracket(participants, tournamentId) {
        const ordered = exact12Positions(participants);
        if (!ordered) return buildGenericBracket(participants, tournamentId);
        const bracket = {
            version: VERSION,
            algorithm: 'approved_12_v1',
            template: 'approved_12_4x3',
            tournamentId: tournamentId || unique('tournament'),
            playerCount: 12,
            createdAt: Date.now(),
            updatedAt: Date.now(),
            status: 'active',
            participants: ordered.map((item, index) => ({ ...item, position: index + 1 })),
            matches: [],
            finalSources: [],
            activeMatches: [],
            completedAt: null
        };
        const create = makeMatchFactory(bracket);
        const S = position => sourceSeed(ordered[position - 1], position);
        const M = (source1, source2, title, placeStart, placeEnd, layout) => create({
            source1, source2, title, stage: placeStart === 1 ? 'main' : 'placement', placeStart, placeEnd,
            level: Math.round((layout.x - PADDING) / X_GAP), yKey: layout.y / Y_GAP, layout
        });

        const m1 = M(S(9), S(8), '1/8 финала', 1, 12, { x: 48, y: 70 });
        const m2 = M(S(5), S(12), '1/8 финала', 1, 12, { x: 48, y: 226 });
        const m3 = M(S(11), S(6), '1/8 финала', 1, 12, { x: 48, y: 382 });
        const m4 = M(S(7), S(10), '1/8 финала', 1, 12, { x: 48, y: 538 });
        const m5 = M(S(1), sourceMatch(m1.id, 'W'), '1/4 финала', 1, 8, { x: 358, y: 70 });
        const m6 = M(sourceMatch(m2.id, 'W'), S(4), '1/4 финала', 1, 8, { x: 358, y: 226 });
        const m7 = M(S(3), sourceMatch(m3.id, 'W'), '1/4 финала', 1, 8, { x: 358, y: 382 });
        const m8 = M(sourceMatch(m4.id, 'W'), S(2), '1/4 финала', 1, 8, { x: 358, y: 538 });
        const m9 = M(sourceMatch(m5.id, 'W'), sourceMatch(m6.id, 'W'), '1/2 финала', 1, 4, { x: 668, y: 148 });
        const m10 = M(sourceMatch(m7.id, 'W'), sourceMatch(m8.id, 'W'), '1/2 финала', 1, 4, { x: 668, y: 460 });
        const m11 = M(sourceMatch(m9.id, 'W'), sourceMatch(m10.id, 'W'), 'Финал · 1–2 места', 1, 2, { x: 978, y: 304 });
        const m12 = M(sourceMatch(m8.id, 'L'), sourceMatch(m1.id, 'L'), 'Распределение мест', 3, 12, { x: 358, y: 840 });
        const m13 = M(sourceMatch(m7.id, 'L'), sourceMatch(m2.id, 'L'), 'Распределение мест', 3, 12, { x: 358, y: 996 });
        const m14 = M(sourceMatch(m6.id, 'L'), sourceMatch(m3.id, 'L'), 'Распределение мест', 3, 12, { x: 358, y: 1152 });
        const m15 = M(sourceMatch(m5.id, 'L'), sourceMatch(m4.id, 'L'), 'Распределение мест', 3, 12, { x: 358, y: 1308 });
        const m16 = M(sourceMatch(m12.id, 'W'), sourceMatch(m13.id, 'W'), 'Распределение 3–8 мест', 3, 8, { x: 668, y: 918 });
        const m17 = M(sourceMatch(m14.id, 'W'), sourceMatch(m15.id, 'W'), 'Распределение 3–8 мест', 3, 8, { x: 668, y: 1230 });
        const m18 = M(sourceMatch(m9.id, 'L'), sourceMatch(m16.id, 'W'), 'За 3–6 места', 3, 6, { x: 978, y: 840 });
        const m19 = M(sourceMatch(m10.id, 'L'), sourceMatch(m17.id, 'W'), 'За 3–6 места', 3, 6, { x: 978, y: 1152 });
        const m20 = M(sourceMatch(m18.id, 'W'), sourceMatch(m19.id, 'W'), 'За 3–4 места', 3, 4, { x: 1288, y: 918 });
        const m21 = M(sourceMatch('P-M22', 'L'), sourceMatch('P-M23', 'L'), 'За 11–12 места', 11, 12, { x: 978, y: 1780 });
        const m22 = M(sourceMatch(m12.id, 'L'), sourceMatch(m13.id, 'L'), 'За 9–12 места', 9, 12, { x: 668, y: 1624 });
        const m23 = M(sourceMatch(m14.id, 'L'), sourceMatch(m15.id, 'L'), 'За 9–12 места', 9, 12, { x: 668, y: 1780 });
        const m24 = M(sourceMatch(m22.id, 'W'), sourceMatch(m23.id, 'W'), 'За 9–10 места', 9, 10, { x: 978, y: 1624 });
        const m25 = M(sourceMatch(m16.id, 'L'), sourceMatch(m17.id, 'L'), 'За 7–8 места', 7, 8, { x: 978, y: 1468 });
        const m26 = M(sourceMatch(m18.id, 'L'), sourceMatch(m19.id, 'L'), 'За 5–6 места', 5, 6, { x: 1288, y: 1074 });

        bracket.finalSources = [
            { place: 1, source: sourceMatch(m11.id, 'W') },
            { place: 2, source: sourceMatch(m11.id, 'L') },
            { place: 3, source: sourceMatch(m20.id, 'W') },
            { place: 4, source: sourceMatch(m20.id, 'L') },
            { place: 5, source: sourceMatch(m26.id, 'W') },
            { place: 6, source: sourceMatch(m26.id, 'L') },
            { place: 7, source: sourceMatch(m25.id, 'W') },
            { place: 8, source: sourceMatch(m25.id, 'L') },
            { place: 9, source: sourceMatch(m24.id, 'W') },
            { place: 10, source: sourceMatch(m24.id, 'L') },
            { place: 11, source: sourceMatch(m21.id, 'W') },
            { place: 12, source: sourceMatch(m21.id, 'L') }
        ];
        return bracket;
    }

    function assignGenericLayout(bracket) {
        const levels = new Map();
        bracket.matches.forEach(match => {
            const level = Number(match.level) || 0;
            if (!levels.has(level)) levels.set(level, []);
            levels.get(level).push(match);
        });
        levels.forEach((matches, level) => {
            matches.sort((a, b) => a.yKey - b.yKey || a.number - b.number);
            const usedRows = [];
            matches.forEach(match => {
                let y = Math.max(PADDING, match.yKey * Y_GAP);
                while (usedRows.some(existing => Math.abs(existing - y) < CARD_H + 20)) y += CARD_H + 22;
                usedRows.push(y);
                match.layout = { x: PADDING + level * X_GAP, y };
            });
        });
    }

    function buildBracket(participants, tournamentId) {
        const list = arr(participants);
        if (list.length === 12) return buildExact12Bracket(list, tournamentId);
        return buildGenericBracket(list, tournamentId);
    }

    function matchMap(bracket) {
        return new Map(arr(bracket && bracket.matches).map(match => [String(match.id), match]));
    }

    function resolveSource(source, byId) {
        if (!source) return null;
        if (source.type === 'seed') return Number(source.playerIndex);
        const match = byId.get(String(source.matchId));
        if (!match || !scorePlayed(match.score)) return null;
        return source.outcome === 'L' ? Number(match.loser) : Number(match.winner);
    }

    function refreshParticipants(bracket) {
        const byId = matchMap(bracket);
        let changed = false;
        arr(bracket.matches).sort((a, b) => a.number - b.number).forEach(match => {
            const p1 = resolveSource(match.sources && match.sources[0], byId);
            const p2 = resolveSource(match.sources && match.sources[1], byId);
            if (match.p1 !== p1) { match.p1 = p1; changed = true; }
            if (match.p2 !== p2) { match.p2 = p2; changed = true; }
            if (p1 != null && p2 != null && !scorePlayed(match.score) && !match.readyAt) {
                match.readyAt = Date.now();
                changed = true;
            }
            if ((p1 == null || p2 == null) && match.readyAt && !scorePlayed(match.score)) {
                match.readyAt = null;
                changed = true;
            }
        });
        return changed;
    }

    function descendantsOf(bracket, matchId) {
        const result = new Set();
        let expanded = true;
        while (expanded) {
            expanded = false;
            arr(bracket.matches).forEach(match => {
                if (result.has(match.id) || match.id === matchId) return;
                const depends = arr(match.sources).some(source => source && source.type === 'match' && (String(source.matchId) === String(matchId) || result.has(String(source.matchId))));
                if (depends) {
                    result.add(match.id);
                    expanded = true;
                }
            });
        }
        return result;
    }

    function clearMatchResult(match) {
        match.score = null;
        match.winner = null;
        match.loser = null;
        match.technical = false;
        match.completedAt = null;
        match.tableIndex = null;
        match.readyAt = null;
    }

    function clearDescendants(bracket, matchId) {
        const descendants = descendantsOf(bracket, matchId);
        arr(bracket.matches).forEach(match => {
            if (descendants.has(match.id)) clearMatchResult(match);
        });
        bracket.activeMatches = arr(bracket.activeMatches).map(id => descendants.has(String(id)) ? null : id);
    }

    function applyMatchScore(match, score, technical) {
        match.score = score;
        const result = scoreResult(score, match.p1, match.p2);
        match.winner = result ? result.winner : null;
        match.loser = result ? result.loser : null;
        match.technical = !!technical || !!(result && result.technical);
        match.completedAt = Date.now();
        match.tableIndex = null;
    }

    function autoResolveWithdrawals(draft, bracket) {
        const withdrawn = new Set(arr(draft.withdrawnPlayers).map(Number));
        if (!withdrawn.size) return false;
        let changed = false;
        let again = true;
        while (again) {
            again = false;
            refreshParticipants(bracket);
            arr(bracket.matches).forEach(match => {
                if (scorePlayed(match.score) || match.p1 == null || match.p2 == null) return;
                const firstOut = withdrawn.has(Number(match.p1));
                const secondOut = withdrawn.has(Number(match.p2));
                if (!firstOut && !secondOut) return;
                if (firstOut && secondOut) {
                    const firstSeed = seedPositionForPlayer(bracket, match.p1);
                    const secondSeed = seedPositionForPlayer(bracket, match.p2);
                    applyMatchScore(match, firstSeed <= secondSeed ? 'W' : 'L', true);
                    match.doubleWithdrawal = true;
                } else {
                    applyMatchScore(match, firstOut ? 'L' : 'W', true);
                }
                changed = true;
                again = true;
            });
        }
        return changed;
    }

    function seedPositionForPlayer(bracket, playerIndex) {
        const item = arr(bracket.participants).find(participant => Number(participant.playerIndex) === Number(playerIndex));
        return item ? Number(item.position) : 9999;
    }

    function playoffProgress(bracket) {
        const matches = arr(bracket && bracket.matches);
        const played = matches.filter(match => scorePlayed(match.score)).length;
        return { played, total: matches.length, complete: matches.length > 0 && played === matches.length };
    }

    function resolveFinalStandings(bracket) {
        const byId = matchMap(bracket);
        return arr(bracket.finalSources)
            .slice()
            .sort((a, b) => Number(a.place) - Number(b.place))
            .map(item => ({ place: Number(item.place), playerIndex: resolveSource(item.source, byId) }));
    }

    function updateCompletion(bracket) {
        const progress = playoffProgress(bracket);
        const standings = resolveFinalStandings(bracket);
        const allPlaces = standings.length === Number(bracket.playerCount) && standings.every(item => item.playerIndex != null);
        if (progress.complete && allPlaces) {
            bracket.status = 'completed';
            bracket.completedAt = bracket.completedAt || Date.now();
        } else {
            bracket.status = 'active';
            bracket.completedAt = null;
        }
        bracket.updatedAt = Date.now();
        return { ...progress, standings, allPlaces };
    }

    function effectiveTableCount(draft) {
        const players = Math.max(2, Number(draft.playersCount) || arr(draft.playoffParticipants).length || 2);
        return Math.max(1, Math.min(Number(draft.tablesCount) || 1, Math.floor(players / 2)));
    }

    function refreshActiveMatches(draft, bracket) {
        refreshParticipants(bracket);
        autoResolveWithdrawals(draft, bracket);
        refreshParticipants(bracket);
        const tableCount = effectiveTableCount(draft);
        let active = Array.isArray(bracket.activeMatches) ? bracket.activeMatches.slice(0, tableCount) : [];
        while (active.length < tableCount) active.push(null);
        const byId = matchMap(bracket);
        const busy = new Set();
        for (let table = 0; table < active.length; table++) {
            const match = byId.get(String(active[table] || ''));
            if (!match || scorePlayed(match.score) || match.p1 == null || match.p2 == null || busy.has(Number(match.p1)) || busy.has(Number(match.p2))) {
                active[table] = null;
                continue;
            }
            match.tableIndex = table;
            busy.add(Number(match.p1));
            busy.add(Number(match.p2));
        }
        arr(bracket.matches).forEach(match => {
            if (!active.includes(match.id)) match.tableIndex = null;
        });
        const lastFinished = new Set(arr(draft.lastFinishedPlayers).map(Number));
        const candidates = arr(bracket.matches)
            .filter(match => !scorePlayed(match.score) && match.p1 != null && match.p2 != null && !active.includes(match.id))
            .filter(match => !busy.has(Number(match.p1)) && !busy.has(Number(match.p2)))
            .sort((a, b) => {
                const aRest = (lastFinished.has(Number(a.p1)) || lastFinished.has(Number(a.p2))) ? 1 : 0;
                const bRest = (lastFinished.has(Number(b.p1)) || lastFinished.has(Number(b.p2))) ? 1 : 0;
                return aRest - bRest || num(a.readyAt, a.createdAt) - num(b.readyAt, b.createdAt) || a.number - b.number;
            });
        for (let table = 0; table < active.length; table++) {
            if (active[table]) continue;
            const index = candidates.findIndex(match => !busy.has(Number(match.p1)) && !busy.has(Number(match.p2)));
            if (index < 0) continue;
            const match = candidates.splice(index, 1)[0];
            active[table] = match.id;
            match.tableIndex = table;
            busy.add(Number(match.p1));
            busy.add(Number(match.p2));
        }
        bracket.activeMatches = active;
        updateCompletion(bracket);
        return bracket;
    }

    function ensureBracketForDraft(draft) {
        if (!draft || draft.format !== FORMAT || !draft.groupStageCompleted) return { bracket: null, changed: false };
        const count = Number(draft.playersCount) || arr(draft.playoffParticipants).length;
        if (count < MIN_PLAYERS || count > MAX_PLAYERS) return { bracket: null, changed: false, unsupported: true };
        let changed = false;
        let bracket = draft.playoffBracket;
        if (!bracket || bracket.version !== VERSION || !arr(bracket.matches).length || Number(bracket.playerCount) !== count) {
            bracket = buildBracket(draft.playoffParticipants, draft.tournamentId);
            draft.playoffBracket = bracket;
            changed = true;
        }
        if (refreshParticipants(bracket)) changed = true;
        if (autoResolveWithdrawals(draft, bracket)) changed = true;
        refreshParticipants(bracket);
        const before = JSON.stringify(bracket.activeMatches || []);
        refreshActiveMatches(draft, bracket);
        if (before !== JSON.stringify(bracket.activeMatches || [])) changed = true;
        updateCompletion(bracket);
        return { bracket, changed };
    }

    function playerNames(draft) {
        try {
            return JSON.parse(draft.playerFieldsJson || '[]').map((field, index) => {
                try {
                    if (typeof decodeName === 'function') return decodeName(field.name).trim() || `Игрок ${index + 1}`;
                } catch (_) {}
                return String(field && field.name || `Игрок ${index + 1}`);
            });
        } catch (_) {
            return [];
        }
    }

    function persistDraft(draft, silent) {
        if (!draft || typeof db === 'undefined' || typeof activeClubId === 'undefined') return Promise.resolve();
        if (persistTimer) clearTimeout(persistTimer);
        return new Promise(resolve => {
            persistTimer = setTimeout(() => {
                db.ref(`clubs/${activeClubId}/draft`).set(draft)
                    .then(() => resolve())
                    .catch(error => {
                        if (!silent && typeof showToast === 'function') showToast('Ошибка сохранения плей-офф: ' + error.message);
                        resolve();
                    });
            }, 20);
        });
    }

    function activeTabIsPlayoff() {
        const button = document.querySelector('.gp-tab.active');
        return !!button && button.textContent.trim() === 'Плей-офф';
    }

    function bracketCanvasSize(bracket) {
        let width = 1200;
        let height = 900;
        arr(bracket.matches).forEach(match => {
            const layout = match.layout || { x: PADDING + (Number(match.level) || 0) * X_GAP, y: (Number(match.yKey) || match.number) * Y_GAP };
            width = Math.max(width, layout.x + CARD_W + 120);
            height = Math.max(height, layout.y + CARD_H + 160);
        });
        return { width, height };
    }

    function stateKey(draft) {
        let club = 'default';
        try { club = String(activeClubId || 'default'); } catch (_) {}
        return `${club}:${draft.tournamentId || draft.name || 'tournament'}`;
    }

    function getViewState(draft) {
        const key = stateKey(draft);
        if (!viewportState.has(key)) viewportState.set(key, { scale: 1, x: 18, y: 18, initialized: false });
        return viewportState.get(key);
    }

    function createSvgLine(svg, from, to, outcome) {
        const x1 = from.x + CARD_W;
        const y1 = from.y + CARD_H / 2;
        const x2 = to.x;
        const y2 = to.y + (outcome === 'L' ? CARD_H * 0.68 : CARD_H * 0.32);
        const mid = x1 + Math.max(34, (x2 - x1) / 2);
        const path = document.createElementNS('http://www.w3.org/2000/svg', 'path');
        path.setAttribute('d', `M ${x1} ${y1} C ${mid} ${y1}, ${mid} ${y2}, ${x2} ${y2}`);
        path.setAttribute('class', outcome === 'L' ? 'pf-route pf-route-loser' : 'pf-route pf-route-winner');
        svg.appendChild(path);
    }

    function matchStateClass(match, active) {
        if (scorePlayed(match.score)) return ' completed';
        if (active) return ' playing';
        if (match.p1 != null && match.p2 != null) return ' ready';
        return ' locked';
    }

    function scoreForRow(match, playerIndex) {
        if (!scorePlayed(match.score)) return '—';
        if (match.score === 'W') return Number(playerIndex) === Number(match.p1) ? 'W' : 'L';
        if (match.score === 'L') return Number(playerIndex) === Number(match.p1) ? 'L' : 'W';
        const parts = String(match.score).split(':');
        return Number(playerIndex) === Number(match.p1) ? parts[0] : parts[1];
    }

    function unresolvedHint(match) {
        const missing = arr(match.sources).filter((source, index) => (index === 0 ? match.p1 : match.p2) == null);
        if (!missing.length) return 'Матч готов к проведению';
        return 'Матч станет доступен после завершения: ' + missing.map(sourceLabel).join(' и ');
    }

    function renderMatchCard(match, names, active, movementFlag) {
        const card = document.createElement('button');
        card.type = 'button';
        card.className = `pf-match-card${matchStateClass(match, active)}`;
        card.dataset.matchId = match.id;
        const layout = match.layout || { x: PADDING + (Number(match.level) || 0) * X_GAP, y: (Number(match.yKey) || match.number) * Y_GAP };
        card.style.left = `${layout.x}px`;
        card.style.top = `${layout.y}px`;
        const p1Name = match.p1 == null ? sourceLabel(match.sources && match.sources[0]) : (names[Number(match.p1)] || `Игрок ${Number(match.p1) + 1}`);
        const p2Name = match.p2 == null ? sourceLabel(match.sources && match.sources[1]) : (names[Number(match.p2)] || `Игрок ${Number(match.p2) + 1}`);
        card.innerHTML = `
            <span class="pf-match-head">
                <span><strong>М${match.number}</strong>${match.title ? ` · ${esc(match.title)}` : ''}</span>
                ${active ? `<span class="pf-table-badge">${Number(match.tableIndex) + 1}</span>` : ''}
            </span>
            <span class="pf-player-row${match.winner === match.p1 ? ' winner' : ''}">
                <span class="pf-player-name">${esc(p1Name)}</span><span class="pf-player-score">${esc(scoreForRow(match, match.p1))}</span>
            </span>
            <span class="pf-player-row${match.winner === match.p2 ? ' winner' : ''}">
                <span class="pf-player-name">${esc(p2Name)}</span><span class="pf-player-score">${esc(scoreForRow(match, match.p2))}</span>
            </span>`;
        card.addEventListener('click', event => {
            event.preventDefault();
            if (movementFlag.moved) return;
            handleMatchCardClick(match.id);
        });
        return card;
    }

    function applyTransform(canvas, state, immediate) {
        state.pendingCanvas = canvas;
        state.pendingTransform = `translate3d(${state.x}px, ${state.y}px, 0) scale(${state.scale})`;
        if (immediate) {
            if (state.transformFrame) cancelAnimationFrame(state.transformFrame);
            state.transformFrame = 0;
            if (canvas && canvas.isConnected) canvas.style.transform = state.pendingTransform;
            return;
        }
        if (state.transformFrame) return;
        state.transformFrame = requestAnimationFrame(() => {
            state.transformFrame = 0;
            const target = state.pendingCanvas;
            if (target && target.isConnected) target.style.transform = state.pendingTransform;
        });
    }

    function bracketRenderSignature(draft, bracket) {
        const matches = arr(bracket && bracket.matches).map(match => [
            match.id,
            match.p1,
            match.p2,
            match.score || '',
            match.tableIndex == null ? null : Number(match.tableIndex),
            match.winner,
            match.loser
        ]);
        return JSON.stringify({
            tournamentId: draft && draft.tournamentId,
            name: draft && draft.name,
            status: bracket && bracket.status,
            active: arr(bracket && bracket.activeMatches),
            withdrawn: arr(draft && draft.withdrawnPlayers),
            matches
        });
    }

    function fitBracket(viewport, canvas, draft, bracket) {
        const state = getViewState(draft);
        const size = bracketCanvasSize(bracket);
        const scaleX = Math.max(0.18, (viewport.clientWidth - 24) / size.width);
        const scaleY = Math.max(0.18, (viewport.clientHeight - 24) / size.height);
        state.scale = Math.min(1, scaleX, scaleY);
        state.x = (viewport.clientWidth - size.width * state.scale) / 2;
        state.y = (viewport.clientHeight - size.height * state.scale) / 2;
        state.initialized = true;
        applyTransform(canvas, state);
    }

    function focusMatches(viewport, canvas, draft, bracket, matches, readableScale) {
        const list = arr(matches);
        if (!list.length) return fitBracket(viewport, canvas, draft, bracket);
        const state = getViewState(draft);
        const xs = list.map(match => (match.layout || {}).x || 0);
        const ys = list.map(match => (match.layout || {}).y || 0);
        const centerX = (Math.min(...xs) + Math.max(...xs) + CARD_W) / 2;
        const centerY = (Math.min(...ys) + Math.max(...ys) + CARD_H) / 2;
        state.scale = readableScale == null ? Math.max(0.72, Math.min(1, state.scale || 1)) : readableScale;
        state.x = viewport.clientWidth / 2 - centerX * state.scale;
        state.y = viewport.clientHeight / 2 - centerY * state.scale;
        state.initialized = true;
        applyTransform(canvas, state);
    }

    function installPanZoom(viewport, canvas, draft, bracket, movementFlag) {
        const state = getViewState(draft);
        const pointers = new Map();
        let dragStart = null;
        let pinchStart = null;
        let lastTap = 0;

        function localPoint(event) {
            const rect = viewport.getBoundingClientRect();
            return { x: event.clientX - rect.left, y: event.clientY - rect.top };
        }

        function beginDrag(point) {
            dragStart = { x: point.x, y: point.y, tx: state.x, ty: state.y };
        }

        viewport.addEventListener('pointerdown', event => {
            if (event.button != null && event.button !== 0) return;
            if (event.cancelable) event.preventDefault();
            try { viewport.setPointerCapture(event.pointerId); } catch (_) {}
            const point = localPoint(event);
            pointers.set(event.pointerId, point);
            viewport.classList.add('is-interacting');
            if (pointers.size === 1) {
                movementFlag.moved = false;
                beginDrag(point);
                pinchStart = null;
            } else if (pointers.size === 2) {
                const pts = Array.from(pointers.values());
                const distance = Math.hypot(pts[1].x - pts[0].x, pts[1].y - pts[0].y);
                const center = { x: (pts[0].x + pts[1].x) / 2, y: (pts[0].y + pts[1].y) / 2 };
                pinchStart = {
                    distance: Math.max(1, distance),
                    scale: state.scale,
                    center,
                    tx: state.x,
                    ty: state.y
                };
                dragStart = null;
            }
        }, { passive: false });

        viewport.addEventListener('pointermove', event => {
            if (!pointers.has(event.pointerId)) return;
            if (event.cancelable) event.preventDefault();
            const point = localPoint(event);
            pointers.set(event.pointerId, point);

            if (pointers.size === 1 && dragStart) {
                const dx = point.x - dragStart.x;
                const dy = point.y - dragStart.y;
                if (Math.abs(dx) + Math.abs(dy) > 4) movementFlag.moved = true;
                state.x = dragStart.tx + dx;
                state.y = dragStart.ty + dy;
                applyTransform(canvas, state);
                return;
            }

            if (pointers.size >= 2 && pinchStart) {
                movementFlag.moved = true;
                const pts = Array.from(pointers.values()).slice(0, 2);
                const distance = Math.hypot(pts[1].x - pts[0].x, pts[1].y - pts[0].y);
                const center = { x: (pts[0].x + pts[1].x) / 2, y: (pts[0].y + pts[1].y) / 2 };
                const nextScale = Math.max(0.18, Math.min(2.2, pinchStart.scale * (distance / pinchStart.distance)));
                const worldX = (pinchStart.center.x - pinchStart.tx) / pinchStart.scale;
                const worldY = (pinchStart.center.y - pinchStart.ty) / pinchStart.scale;
                state.scale = nextScale;
                state.x = center.x - worldX * nextScale;
                state.y = center.y - worldY * nextScale;
                applyTransform(canvas, state);
            }
        }, { passive: false });

        function release(event) {
            pointers.delete(event.pointerId);
            try { viewport.releasePointerCapture(event.pointerId); } catch (_) {}
            if (pointers.size >= 2) {
                const pts = Array.from(pointers.values()).slice(0, 2);
                const distance = Math.hypot(pts[1].x - pts[0].x, pts[1].y - pts[0].y);
                const center = { x: (pts[0].x + pts[1].x) / 2, y: (pts[0].y + pts[1].y) / 2 };
                pinchStart = { distance: Math.max(1, distance), scale: state.scale, center, tx: state.x, ty: state.y };
                dragStart = null;
            } else if (pointers.size === 1) {
                pinchStart = null;
                beginDrag(Array.from(pointers.values())[0]);
            } else {
                dragStart = null;
                pinchStart = null;
                viewport.classList.remove('is-interacting');
                setTimeout(() => { movementFlag.moved = false; }, 120);
            }
        }

        viewport.addEventListener('pointerup', release);
        viewport.addEventListener('pointercancel', release);
        viewport.addEventListener('lostpointercapture', event => {
            if (pointers.has(event.pointerId)) release(event);
        });

        viewport.addEventListener('wheel', event => {
            event.preventDefault();
            const oldScale = state.scale;
            const factor = Math.exp(-event.deltaY * 0.0015);
            const nextScale = Math.max(0.18, Math.min(2.2, oldScale * factor));
            const rect = viewport.getBoundingClientRect();
            const px = event.clientX - rect.left;
            const py = event.clientY - rect.top;
            const worldX = (px - state.x) / oldScale;
            const worldY = (py - state.y) / oldScale;
            state.scale = nextScale;
            state.x = px - worldX * nextScale;
            state.y = py - worldY * nextScale;
            applyTransform(canvas, state);
        }, { passive: false });

        viewport.addEventListener('click', event => {
            if (movementFlag.moved) return;
            if (event.target.closest('.pf-match-card, .pf-control')) return;
            const now = Date.now();
            if (now - lastTap < 340) {
                const active = arr(bracket.activeMatches).map(id => matchMap(bracket).get(String(id))).filter(Boolean);
                focusMatches(viewport, canvas, draft, bracket, active.length ? active : bracket.matches.slice(0, 4), 1);
                lastTap = 0;
            } else {
                lastTap = now;
            }
        });
    }

    function renderBracketInto(container, draft, bracket) {
        const signature = bracketRenderSignature(draft, bracket);
        const existing = container.querySelector('.pf-playoff-root');
        if (existing && existing.dataset.renderSignature === signature) return;
        const old = container.querySelector('.gp-playoff-placeholder');
        if (old) old.remove();
        container.querySelectorAll('.pf-playoff-root').forEach(node => node.remove());
        const names = playerNames(draft);
        const progress = playoffProgress(bracket);
        const root = document.createElement('section');
        root.className = 'pf-playoff-root';
        root.dataset.renderSignature = signature;
        const finishReady = progress.complete &&
            bracket.status === 'completed' &&
            resolveFinalStandings(bracket).length === Number(bracket.playerCount) &&
            resolveFinalStandings(bracket).every(item => item.playerIndex != null);
        root.innerHTML = `
            <div class="pf-status-row${finishReady ? ' pf-status-row-complete' : ''}">
                <div class="pf-status-copy">
                    <div><strong>Плей-офф</strong> · сыграно ${progress.played} из ${progress.total} матчей</div>
                    <div class="pf-status-sub">${finishReady
                        ? 'Все места определены. На следующем экране можно проверить рейтинг и бонусы за 1–3 места.'
                        : `Все места 1–${Number(bracket.playerCount)} определяются в одной сетке`}</div>
                </div>
                ${finishReady ? '<button type="button" class="gp-primary-button pf-status-finish-button">Завершить турнир</button>' : ''}
            </div>`;
        const viewport = document.createElement('div');
        viewport.className = 'pf-viewport';
        const canvas = document.createElement('div');
        canvas.className = 'pf-canvas';
        const size = bracketCanvasSize(bracket);
        canvas.style.width = `${size.width}px`;
        canvas.style.height = `${size.height}px`;
        const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
        svg.setAttribute('class', 'pf-routes');
        svg.setAttribute('width', String(size.width));
        svg.setAttribute('height', String(size.height));
        svg.setAttribute('viewBox', `0 0 ${size.width} ${size.height}`);
        const byId = matchMap(bracket);
        arr(bracket.matches).forEach(match => {
            const target = match.layout;
            arr(match.sources).forEach(source => {
                if (!source || source.type !== 'match') return;
                const previous = byId.get(String(source.matchId));
                if (previous && previous.layout) createSvgLine(svg, previous.layout, target, source.outcome);
            });
        });
        canvas.appendChild(svg);
        const movementFlag = { moved: false };
        const activeIds = new Set(arr(bracket.activeMatches).filter(Boolean).map(String));
        arr(bracket.matches).forEach(match => canvas.appendChild(renderMatchCard(match, names, activeIds.has(String(match.id)), movementFlag)));
        viewport.appendChild(canvas);

        const controls = document.createElement('div');
        controls.className = 'pf-controls';
        controls.innerHTML = `
            <button type="button" class="pf-control" data-action="active" title="К текущим матчам">◎</button>
            <button type="button" class="pf-control" data-action="fit" title="Показать сетку целиком">▣</button>`;
        viewport.appendChild(controls);
        root.appendChild(viewport);

        container.appendChild(root);
        const finishButton = root.querySelector('.pf-status-finish-button');
        if (finishButton) finishButton.addEventListener('click', openFullFinishModal);

        const state = getViewState(draft);
        applyTransform(canvas, state, true);
        installPanZoom(viewport, canvas, draft, bracket, movementFlag);
        controls.querySelector('[data-action="fit"]').addEventListener('click', event => {
            event.stopPropagation();
            fitBracket(viewport, canvas, draft, bracket);
        });
        controls.querySelector('[data-action="active"]').addEventListener('click', event => {
            event.stopPropagation();
            const active = arr(bracket.activeMatches).map(id => byId.get(String(id))).filter(Boolean);
            const ready = arr(bracket.matches).filter(match => !scorePlayed(match.score) && match.p1 != null && match.p2 != null);
            focusMatches(viewport, canvas, draft, bracket, active.length ? active : ready.slice(0, 4), 1);
        });
        requestAnimationFrame(() => {
            if (!state.initialized) {
                const active = arr(bracket.activeMatches).map(id => byId.get(String(id))).filter(Boolean);
                const ready = arr(bracket.matches).filter(match => !scorePlayed(match.score) && match.p1 != null && match.p2 != null);
                focusMatches(viewport, canvas, draft, bracket, active.length ? active : ready.slice(0, 4), 1);
            }
        });
    }

    function enhanceCurrentView() {
        if (renderGuard || typeof document === 'undefined') return;
        if (typeof clubData === 'undefined' || !clubData.draft || clubData.draft.format !== FORMAT) return;
        if (!activeTabIsPlayoff()) return;
        const container = document.getElementById('active-tournament-container');
        if (!container) return;
        renderGuard = true;
        try {
            const draft = clubData.draft;
            const ensured = ensureBracketForDraft(draft);
            if (ensured.unsupported) {
                const old = container.querySelector('.gp-playoff-placeholder');
                if (old) old.innerHTML = `<strong>Этот размер пока не поддерживается</strong><span>Поддерживаются турниры от ${MIN_PLAYERS} до ${MAX_PLAYERS} участников.</span>`;
                return;
            }
            if (!ensured.bracket) return;
            renderBracketInto(container, draft, ensured.bracket);
            if (ensured.changed) persistDraft(draft, true);
        } finally {
            renderGuard = false;
        }
    }

    function scheduleEnhance() {
        if (enhanceFrame) return;
        enhanceFrame = requestAnimationFrame(() => {
            enhanceFrame = 0;
            enhanceCurrentView();
        });
    }

    function getCurrentPlayoffMatch(matchId) {
        if (typeof clubData === 'undefined' || !clubData.draft || !clubData.draft.playoffBracket) return null;
        return arr(clubData.draft.playoffBracket.matches).find(match => String(match.id) === String(matchId)) || null;
    }

    function openConfiguredScoreDialog(match, tableIndex) {
        let opener = null;
        try { if (typeof openAdminScoreDialog === 'function') opener = openAdminScoreDialog; } catch (_) {}
        if (!opener && typeof global.openAdminScoreDialog === 'function') opener = global.openAdminScoreDialog;
        if (!opener) {
            if (typeof showToast === 'function') showToast('Окно ввода результата не найдено');
            return;
        }
        opener(Number(match.p1), Number(match.p2), `playoff:${match.id}`, tableIndex == null ? null : Number(tableIndex));
        try {
            const names = typeof clubData !== 'undefined' && clubData.draft ? playerNames(clubData.draft) : [];
            const title = document.getElementById('admin-score-match-title');
            if (title) {
                title.textContent = `${names[Number(match.p1)] || `Игрок ${Number(match.p1) + 1}`}  VS  ${names[Number(match.p2)] || `Игрок ${Number(match.p2) + 1}`}`;
                title.classList.add('pf-score-opponents');
            }
        } catch (_) {}
        try {
            activeScoreMatch = {
                type: 'fullPlayoff',
                matchId: String(match.id),
                f: Number(match.p1),
                s: Number(match.p2),
                index: tableIndex == null ? null : Number(tableIndex)
            };
        } catch (_) {}
    }

    function handleMatchCardClick(matchId) {
        const match = getCurrentPlayoffMatch(matchId);
        if (!match) return;
        const admin = typeof isAdmin === 'undefined' ? true : !!isAdmin;
        if (!admin) return;
        if (match.p1 == null || match.p2 == null) {
            if (typeof showToast === 'function') showToast(unresolvedHint(match));
            return;
        }
        if (!scorePlayed(match.score) && match.tableIndex == null) {
            if (typeof showToast === 'function') showToast('Матч готов и будет назначен после освобождения стола');
            return;
        }
        openConfiguredScoreDialog(match, match.tableIndex);
    }

    function closeScoreModalSafe() {
        try {
            if (typeof closeAdminScoreModal === 'function') closeAdminScoreModal();
            else if (global.closeAdminScoreModal) global.closeAdminScoreModal();
        } catch (_) {
            const modal = document.getElementById('admin-score-modal');
            if (modal) modal.style.display = 'none';
        }
    }

    async function savePlayoffScore(score) {
        if (typeof clubData === 'undefined' || !clubData.draft) return;
        let active = null;
        try { active = activeScoreMatch; } catch (_) {}
        if (!active || active.type !== 'fullPlayoff') return;
        const draft = clone(clubData.draft);
        const bracket = draft.playoffBracket;
        const match = arr(bracket.matches).find(item => String(item.id) === String(active.matchId));
        if (!match) {
            if (typeof showToast === 'function') showToast('Матч плей-офф не найден');
            return;
        }
        const descendants = descendantsOf(bracket, match.id);
        const playedDownstream = arr(bracket.matches).filter(item => descendants.has(item.id) && scorePlayed(item.score));
        if (scorePlayed(match.score) && playedDownstream.length) {
            const accepted = global.confirm('Изменение этого результата удалит результаты зависимых матчей плей-офф. Продолжить?');
            if (!accepted) return;
            clearDescendants(bracket, match.id);
        }
        if (typeof showLoader === 'function') showLoader(true);
        applyMatchScore(match, score, false);
        draft.lastFinishedPlayers = [Number(match.p1), Number(match.p2)];
        bracket.activeMatches = arr(bracket.activeMatches).map(id => String(id) === String(match.id) ? null : id);
        refreshParticipants(bracket);
        autoResolveWithdrawals(draft, bracket);
        refreshActiveMatches(draft, bracket);
        updateCompletion(bracket);
        draft.currentStage = 'playoff';
        draft.playoffScores = Object.fromEntries(arr(bracket.matches).filter(item => scorePlayed(item.score)).map(item => [item.id, item.score]));
        try {
            await db.ref(`clubs/${activeClubId}/draft`).set(draft);
            clubData.draft = draft;
            closeScoreModalSafe();
            if (typeof showLoader === 'function') showLoader(false);
            scheduleEnhance();
            if (typeof showToast === 'function') showToast(bracket.status === 'completed' ? 'Плей-офф завершён. Все места определены.' : 'Счёт сохранён!');
            if (bracket.status === 'completed') {
                const autoOpenKey = `${draft.tournamentId || draft.id || 'tournament'}:${bracket.completedAt || 'completed'}`;
                if (global.__FULL_PLAYOFF_FINISH_AUTO_OPENED__ !== autoOpenKey) {
                    global.__FULL_PLAYOFF_FINISH_AUTO_OPENED__ = autoOpenKey;
                    setTimeout(openFullFinishModal, 180);
                }
            }
        } catch (error) {
            if (typeof showLoader === 'function') showLoader(false);
            if (typeof showToast === 'function') showToast('Ошибка сохранения: ' + error.message);
        }
    }

    async function withdrawFromPlayoff(slotIndex) {
        if (typeof clubData === 'undefined' || !clubData.draft) return;
        let active = null;
        try { active = activeScoreMatch; } catch (_) {}
        if (!active || active.type !== 'fullPlayoff') return;
        const target = Number(slotIndex) === 0 ? Number(active.f) : Number(active.s);
        const names = playerNames(clubData.draft);
        if (!global.confirm(`Вы действительно хотите снять игрока «${names[target] || `Игрок ${target + 1}`}»? Его оставшиеся матчи будут завершены технически.`)) return;
        const draft = clone(clubData.draft);
        draft.withdrawnPlayers = Array.from(new Set(arr(draft.withdrawnPlayers).map(Number).concat([target])));
        const bracket = draft.playoffBracket;
        const current = arr(bracket.matches).find(item => String(item.id) === String(active.matchId));
        if (current && !scorePlayed(current.score)) applyMatchScore(current, Number(current.p1) === target ? 'L' : 'W', true);
        bracket.activeMatches = arr(bracket.activeMatches).map(id => current && String(id) === String(current.id) ? null : id);
        draft.lastFinishedPlayers = current ? [Number(current.p1), Number(current.p2)] : [];
        refreshParticipants(bracket);
        autoResolveWithdrawals(draft, bracket);
        refreshActiveMatches(draft, bracket);
        updateCompletion(bracket);
        if (typeof showLoader === 'function') showLoader(true);
        try {
            await db.ref(`clubs/${activeClubId}/draft`).set(draft);
            clubData.draft = draft;
            closeScoreModalSafe();
            if (typeof showLoader === 'function') showLoader(false);
            scheduleEnhance();
            if (typeof showToast === 'function') showToast('Игрок снят с турнира');
        } catch (error) {
            if (typeof showLoader === 'function') showLoader(false);
            if (typeof showToast === 'function') showToast('Ошибка сохранения: ' + error.message);
        }
    }

    function scoreCoefficient(score) {
        if (score === 'W' || score === 'L') return 0;
        const parts = String(score || '').split(':').map(Number);
        const winnerScore = Math.max(parts[0], parts[1]);
        const loserScore = Math.min(parts[0], parts[1]);
        if (winnerScore === 2 && loserScore === 1) return 0.8;
        if (winnerScore === 3 && loserScore === 2) return 0.8;
        if (winnerScore === 3 && loserScore === 1) return 0.9;
        return 1;
    }

    function calculateMatchImpactByPlayers(first, second, score, K) {
        if (!scorePlayed(score) || score === 'W' || score === 'L') return { p1Delta: 0, p2Delta: 0, winner: null, loser: null };
        const parts = String(score).split(':').map(Number);
        const p1Won = parts[0] > parts[1];
        const winner = p1Won ? first : second;
        const loser = p1Won ? second : first;
        const diff = Number(winner.rating || 200) - Number(loser.rating || 200);
        const baseDelta = diff < 100 ? (100 - diff) / 10 : 0;
        const coeff = scoreCoefficient(score);
        const winnerDelta = (winner.tournamentsPlayed || 0) <= 5
            ? Math.round(baseDelta * 100) / 100
            : Math.round(baseDelta * K * coeff * 100) / 100;
        const loserDelta = (loser.tournamentsPlayed || 0) <= 5
            ? Math.round(baseDelta * 0.5 * 100) / 100
            : Math.round(baseDelta * K * coeff * 100) / 100;
        return {
            p1Delta: p1Won ? winnerDelta : -loserDelta,
            p2Delta: p1Won ? -loserDelta : winnerDelta,
            winner: p1Won ? 0 : 1,
            loser: p1Won ? 1 : 0
        };
    }

    function collectTournamentMatches(draft) {
        const matches = [];
        const scores = draft.matchScores || {};
        arr(draft.groupMatches).forEach(match => {
            const score = scores[match.scoreKey] || match.score;
            if (!scorePlayed(score)) return;
            matches.push({
                id: match.id,
                stage: 'group',
                groupId: Number(match.groupId),
                p1: Number(match.p1), p2: Number(match.p2), score,
                technical: !!match.technical || score === 'W' || score === 'L',
                completedAt: match.completedAt || match.createdAt || 0
            });
        });
        arr(draft.groupTieBreakMatches).forEach(match => {
            if (match.voided || !scorePlayed(match.score)) return;
            matches.push({
                id: match.id, stage: 'group_tiebreak', groupId: Number(match.groupId),
                p1: Number(match.p1), p2: Number(match.p2), score: match.score,
                technical: !!match.technical || match.score === 'W' || match.score === 'L',
                completedAt: match.completedAt || match.createdAt || 0
            });
        });
        arr(draft.playoffBracket && draft.playoffBracket.matches).forEach(match => {
            if (!scorePlayed(match.score)) return;
            matches.push({
                id: match.id, stage: 'playoff', matchNumber: Number(match.number), title: match.title,
                p1: Number(match.p1), p2: Number(match.p2), score: match.score,
                technical: !!match.technical || match.score === 'W' || match.score === 'L',
                completedAt: match.completedAt || match.createdAt || 0
            });
        });
        return matches.sort((a, b) => num(a.completedAt, 0) - num(b.completedAt, 0) || String(a.id).localeCompare(String(b.id)));
    }

    function calculateFullResult(draft, applyBonuses) {
        const names = playerNames(draft);
        const players = names.map(name => {
            const found = arr(clubData.players).find(player => String(player.fullName).toLowerCase() === String(name).toLowerCase());
            return found ? { ...found } : { id: null, fullName: name, rating: 200, tournamentsPlayed: 0, wins: 0, losses: 0 };
        });
        const K = Number(draft.fixedK) || 0.20;
        const deltas = {};
        const wins = {};
        const losses = {};
        players.forEach(player => {
            const key = String(player.fullName).toLowerCase();
            deltas[key] = 0;
            wins[key] = 0;
            losses[key] = 0;
        });
        const matchDetails = [];
        collectTournamentMatches(draft).forEach(match => {
            const first = players[match.p1];
            const second = players[match.p2];
            if (!first || !second) return;
            const firstKey = String(first.fullName).toLowerCase();
            const secondKey = String(second.fullName).toLowerCase();
            const before1 = Number(first.rating);
            const before2 = Number(second.rating);
            let impact = { p1Delta: 0, p2Delta: 0, winner: null };
            if (!match.technical) impact = calculateMatchImpactByPlayers(first, second, match.score, K);
            deltas[firstKey] += impact.p1Delta;
            deltas[secondKey] += impact.p2Delta;
            const result = scoreResult(match.score, match.p1, match.p2);
            if (result) {
                const winnerKey = String(players[result.winner].fullName).toLowerCase();
                const loserKey = String(players[result.loser].fullName).toLowerCase();
                wins[winnerKey]++;
                losses[loserKey]++;
            }
            matchDetails.push({
                ...match,
                p1Name: first.fullName,
                p2Name: second.fullName,
                p1RatingBefore: before1,
                p2RatingBefore: before2,
                p1Delta: impact.p1Delta,
                p2Delta: impact.p2Delta,
                p1RatingAfter: before1 + impact.p1Delta,
                p2RatingAfter: before2 + impact.p2Delta
            });
        });
        const final = resolveFinalStandings(draft.playoffBracket);
        const standings = final.map((item, index) => ({ index: Number(item.playerIndex), points: final.length - index }));
        if (applyBonuses && standings.length >= 3) {
            const bonuses = [1.5, 1.0, 0.5];
            bonuses.forEach((bonus, index) => {
                const player = players[standings[index].index];
                if (player) deltas[String(player.fullName).toLowerCase()] += bonus;
            });
        }
        Object.keys(deltas).forEach(key => { deltas[key] = Math.round(deltas[key] * 100) / 100; });
        return { standings, deltas, wins, losses, matchDetails, playerNames: names, players, K };
    }

    function fillFinishModal(result) {
        const tbody = document.getElementById('finish-tourney-ratings-tbody');
        if (!tbody) return;
        tbody.innerHTML = '';
        result.standings.forEach((item, index) => {
            const name = result.playerNames[item.index];
            const player = result.players[item.index] || { rating: 200 };
            const delta = result.deltas[String(name).toLowerCase()] || 0;
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${index + 1}</td>
                <td>${esc(name)}</td>
                <td>${Number(player.rating).toFixed(1)} → <strong>${(Number(player.rating) + delta).toFixed(1)}</strong></td>
                <td style="text-align:right;font-weight:800;color:${delta >= 0 ? 'var(--apple-green)' : 'var(--apple-red)'}">${delta >= 0 ? '+' : ''}${delta.toFixed(2)}</td>`;
            tbody.appendChild(row);
        });
    }

    function prepareCompatibilityMatches(draft, result) {
        draft.tieBreakMatches = result.matchDetails
            .filter(match => match.stage !== 'group')
            .map(match => ({
                id: match.id,
                p1: Number(match.p1), p2: Number(match.p2), score: match.score,
                technical: !!match.technical,
                stage: match.stage,
                groupId: match.groupId || null,
                matchNumber: match.matchNumber || null
            }));
    }

    function openFullFinishModal() {
        if (typeof clubData === 'undefined' || !clubData.draft || !clubData.draft.playoffBracket) return;
        const completion = updateCompletion(clubData.draft.playoffBracket);
        if (!completion.complete || !completion.allPlaces) {
            if (typeof showToast === 'function') showToast('Сначала нужно сыграть все обязательные матчи плей-офф');
            return;
        }
        const checkbox = document.getElementById('finish-bonuses-checkbox');
        const applyBonuses = checkbox ? checkbox.checked : true;
        const result = calculateFullResult(clubData.draft, applyBonuses);
        global.calculatedResult = result;
        global.__FULL_PLAYOFF_RESULT__ = result;
        prepareCompatibilityMatches(clubData.draft, result);
        fillFinishModal(result);
        const subtitle = document.getElementById('finish-tourney-subtitle');
        if (subtitle) subtitle.textContent = `Определены все ${result.standings.length} мест. Проверьте изменение рейтинга и бонусы за призовые места.`;
        const modal = document.getElementById('tournament-finish-modal');
        if (modal) modal.style.display = 'flex';
    }

    function safeEncode(value) {
        try { if (typeof safe_encode === 'function') return safe_encode(value); } catch (_) {}
        return encodeURIComponent(String(value));
    }

    function buildStructuredScore(match) {
        if (match.score === 'W') return 'W:L';
        if (match.score === 'L') return 'L:W';
        return match.score;
    }

    async function finalizeFullTournament() {
        const result = global.__FULL_PLAYOFF_RESULT__;
        if (!result || typeof clubData === 'undefined' || !clubData.draft) return;
        if (typeof showLoader === 'function') showLoader(true);
        const draft = clubData.draft;
        const names = result.playerNames;
        const updatedPlayers = arr(clubData.players).map(player => {
            const key = String(player.fullName).toLowerCase();
            if (!(key in result.deltas)) return player;
            const place = result.standings.findIndex(item => String(names[item.index]).toLowerCase() === key) + 1;
            return {
                ...player,
                rating: Math.max(0, Number(player.rating || 0) + (result.deltas[key] || 0)),
                tournamentsPlayed: Number(player.tournamentsPlayed || 0) + 1,
                wins: Number(player.wins || 0) + (result.wins[key] || 0),
                losses: Number(player.losses || 0) + (result.losses[key] || 0),
                goldMedals: Number(player.goldMedals || 0) + (place === 1 ? 1 : 0),
                silverMedals: Number(player.silverMedals || 0) + (place === 2 ? 1 : 0),
                bronzeMedals: Number(player.bronzeMedals || 0) + (place === 3 ? 1 : 0)
            };
        });
        const structuredRatings = result.standings.map(item => {
            const name = names[item.index];
            const player = result.players[item.index] || { rating: 200 };
            const delta = result.deltas[String(name).toLowerCase()] || 0;
            return `${safeEncode(name)}::${Number(player.rating).toFixed(1)}::${(Number(player.rating) + delta).toFixed(1)}::${delta.toFixed(2)}`;
        }).join(';;');
        const structuredMatches = result.matchDetails.map(match => {
            const d1 = match.technical ? 0 : match.p1Delta;
            const d2 = match.technical ? 0 : match.p2Delta;
            return `${safeEncode(match.p1Name)}::${safeEncode(match.p2Name)}::${buildStructuredScore(match)}::${d1 >= 0 ? '+' : ''}${d1.toFixed(2)}::${d2 >= 0 ? '+' : ''}${d2.toFixed(2)}`;
        }).join(';;');
        const standingsText = result.standings.map((item, index) => {
            const medal = index === 0 ? '🥇 ' : index === 1 ? '🥈 ' : index === 2 ? '🥉 ' : '';
            return `${index + 1}. ${medal}${names[item.index]}`;
        }).join('\n');
        const ratingText = result.standings.map(item => {
            const name = names[item.index];
            const player = result.players[item.index] || { rating: 200 };
            const delta = result.deltas[String(name).toLowerCase()] || 0;
            return `${name}: ${Number(player.rating).toFixed(1)} → ${(Number(player.rating) + delta).toFixed(1)} (${delta >= 0 ? '+' : ''}${delta.toFixed(2)})`;
        }).join('\n');
        const timestamp = Date.now();
        const playerIds = {};
        names.forEach(name => {
            const player = arr(clubData.players).find(item => String(item.fullName).toLowerCase() === String(name).toLowerCase());
            if (player) playerIds[name] = String(player.id);
        });
        const applyBonuses = document.getElementById('finish-bonuses-checkbox')?.checked || false;
        let seasonId = null;
        try {
            if (typeof activeSeason !== 'undefined' && activeSeason && activeSeason.status === 'active' && timestamp >= activeSeason.startAt && timestamp < activeSeason.endAt) seasonId = activeSeason.id;
        } catch (_) {}
        const seasonScores = { ...(draft.matchScores || {}) };
        arr(draft.groupMatches).forEach(match => {
            if (match && match.technical && match.scoreKey) delete seasonScores[match.scoreKey];
        });
        const historyEntry = {
            id: timestamp,
            name: draft.name || 'Турнир',
            dateText: new Date(timestamp).toLocaleDateString('ru-RU') + ' ' + new Date(timestamp).toLocaleTimeString('ru-RU', { hour: '2-digit', minute: '2-digit' }),
            playersCount: names.length,
            winnerName: names[result.standings[0].index],
            standingsText,
            ratingText,
            structuredRatings,
            structuredMatches,
            tournamentFormat: FORMAT,
            groupsPlayoffData: {
                version: VERSION,
                groups: clone(draft.groupHistorySnapshot || null),
                playoff: clone(draft.playoffBracket),
                matches: result.matchDetails.map(match => ({ ...match }))
            },
            seasonCalculationSource: {
                seasonId,
                matchScores: seasonScores,
                additionalMatches: result.matchDetails.filter(match => match.stage !== 'group').map(match => ({
                    id: match.id, p1: Number(match.p1), p2: Number(match.p2), score: match.score,
                    technical: !!match.technical
                })),
                finalStandings: result.standings.map(item => ({ index: item.index, points: item.points })),
                playerNames: names,
                playerIds,
                K: result.K,
                applyPlaceBonuses: applyBonuses
            }
        };
        const updatedHistory = [historyEntry, ...arr(clubData.history)];
        try {
            await db.ref(`clubs/${activeClubId}/players`).set(JSON.stringify(updatedPlayers));
            await db.ref(`clubs/${activeClubId}/history`).set(JSON.stringify(updatedHistory));
            await db.ref(`clubs/${activeClubId}/draft`).remove();
            try {
                if (typeof activeSeason !== 'undefined' && activeSeason && typeof updateSeasonAfterTournament === 'function') {
                    await updateSeasonAfterTournament(historyEntry, seasonScores, names, result.K, applyBonuses);
                }
            } catch (_) {}
            const modal = document.getElementById('tournament-finish-modal');
            if (modal) modal.style.display = 'none';
            global.calculatedResult = null;
            global.__FULL_PLAYOFF_RESULT__ = null;
            if (typeof showLoader === 'function') showLoader(false);
            if (typeof showToast === 'function') showToast('Турнир успешно сохранён в историю!');
        } catch (error) {
            if (typeof showLoader === 'function') showLoader(false);
            if (typeof showToast === 'function') showToast('Ошибка сохранения: ' + error.message);
        }
    }

    function installHooks() {
        if (typeof document === 'undefined') return;
        if (global.__FULL_PLAYOFF_11_40_INSTALLED__) return;
        global.__FULL_PLAYOFF_11_40_INSTALLED__ = UI_VERSION;

        const previousUpdate = global.updateActiveTournament;
        if (typeof previousUpdate === 'function') {
            global.updateActiveTournament = function () {
                try {
                    const draft = typeof clubData !== 'undefined' ? clubData.draft : null;
                    const container = document.getElementById('active-tournament-container');
                    const existing = container && container.querySelector('.pf-playoff-root');
                    if (draft && draft.format === FORMAT && activeTabIsPlayoff() && existing) {
                        const ensured = ensureBracketForDraft(draft);
                        if (ensured.bracket) {
                            const signature = bracketRenderSignature(draft, ensured.bracket);
                            if (existing.dataset.renderSignature === signature) return;
                            renderBracketInto(container, draft, ensured.bracket);
                            if (ensured.changed) persistDraft(draft, true);
                            return;
                        }
                    }
                } catch (error) {
                    console.error('[full-playoff] stable render check failed', error);
                }
                const result = previousUpdate.apply(this, arguments);
                enhanceCurrentView();
                scheduleEnhance();
                return result;
            };
        }

        const previousSwitch = global.switchGroupsPlayoffTab;
        if (typeof previousSwitch === 'function') {
            global.switchGroupsPlayoffTab = function () {
                const result = previousSwitch.apply(this, arguments);
                if (result && typeof result.finally === 'function') result.finally(scheduleEnhance);
                else scheduleEnhance();
                return result;
            };
        }

        const previousScore = global.submitQuickScore;
        global.submitQuickScore = function (score) {
            let active = null;
            try { active = activeScoreMatch; } catch (_) {}
            if (active && active.type === 'fullPlayoff') return savePlayoffScore(score);
            return typeof previousScore === 'function' ? previousScore.apply(this, arguments) : undefined;
        };

        const previousWithdrawal = global.submitPlayerWithdrawal;
        global.submitPlayerWithdrawal = function (slotIndex) {
            let active = null;
            try { active = activeScoreMatch; } catch (_) {}
            if (active && active.type === 'fullPlayoff') return withdrawFromPlayoff(slotIndex);
            return typeof previousWithdrawal === 'function' ? previousWithdrawal.apply(this, arguments) : undefined;
        };

        const previousFinishOpen = global.openTournamentFinishModal;
        global.openTournamentFinishModal = function () {
            if (typeof clubData !== 'undefined' && clubData.draft && clubData.draft.format === FORMAT && clubData.draft.playoffBracket?.status === 'completed') {
                return openFullFinishModal();
            }
            return typeof previousFinishOpen === 'function' ? previousFinishOpen.apply(this, arguments) : undefined;
        };

        const previousFinalize = global.submitFinalizeTournament;
        global.submitFinalizeTournament = function () {
            if (global.__FULL_PLAYOFF_RESULT__ && typeof clubData !== 'undefined' && clubData.draft?.format === FORMAT) return finalizeFullTournament();
            return typeof previousFinalize === 'function' ? previousFinalize.apply(this, arguments) : undefined;
        };

        const checkbox = document.getElementById('finish-bonuses-checkbox');
        if (checkbox) checkbox.addEventListener('change', () => {
            const modal = document.getElementById('tournament-finish-modal');
            if (modal && modal.style.display === 'flex' && typeof clubData !== 'undefined' && clubData.draft?.format === FORMAT && clubData.draft.playoffBracket?.status === 'completed') {
                openFullFinishModal();
            }
        });

        const container = document.getElementById('active-tournament-container');
        if (container && typeof MutationObserver !== 'undefined') {
            observer = new MutationObserver(() => {
                if (renderGuard) return;
                if (activeTabIsPlayoff() && !container.querySelector('.pf-playoff-root')) scheduleEnhance();
            });
            observer.observe(container, { childList: true, subtree: false });
        }
        scheduleEnhance();
    }

    const api = {
        VERSION,
        MIN_PLAYERS,
        MAX_PLAYERS,
        buildParticipantSeedOrder,
        buildGenericBracket,
        buildExact12Bracket,
        buildBracket,
        refreshParticipants,
        refreshActiveMatches,
        playoffProgress,
        resolveFinalStandings,
        descendantsOf,
        updateCompletion,
        calculateFullResult
    };

    if (typeof module !== 'undefined' && module.exports) module.exports = api;
    global.FullPlayoff11to40 = api;
    installHooks();
})(typeof window !== 'undefined' ? window : globalThis);
