/*
 * Groups + Playoff patch for tennis-android-app
 * Version: 1.0.3 (2026-08-05)
 *
 * This file is intentionally standalone. It extends the existing monolithic
 * application without replacing its Firebase schema or legacy tournament modes.
 */
(function (global) {
    'use strict';

    const PATCH_VERSION = '1.0.3';
    const FORMAT = 'groups_playoff';
    const GROUPS_TAB = 'groups';
    const PLAYOFF_TAB = 'playoff';
    const MAIN_PREFIX = 'M|';
    const TIE_PREFIX = 'T|';

    function asNumber(value, fallback) {
        const parsed = Number(value);
        return Number.isFinite(parsed) ? parsed : fallback;
    }

    function uniqueToken(prefix) {
        const head = prefix || 'id';
        try {
            if (typeof crypto !== 'undefined' && crypto.getRandomValues) {
                const values = new Uint32Array(3);
                crypto.getRandomValues(values);
                return `${head}-${Date.now()}-${Array.from(values).map(v => v.toString(36)).join('')}`;
            }
        } catch (_) {}
        return `${head}-${Date.now()}-${Math.random().toString(36).slice(2, 12)}`;
    }

    function normalizeArray(value) {
        if (Array.isArray(value)) return value.filter(item => item !== null && item !== undefined);
        if (value && typeof value === 'object') return Object.values(value).filter(item => item !== null && item !== undefined);
        return [];
    }

    function escapeHtml(value) {
        return String(value == null ? '' : value)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#039;');
    }

    function isPlayedScore(score) {
        if (score === 'W' || score === 'L') return true;
        const match = String(score || '').match(/^\s*(\d+)\s*:\s*(\d+)\s*$/);
        return !!match && Number(match[1]) !== Number(match[2]);
    }

    function reverseScore(score) {
        if (score === 'W') return 'L';
        if (score === 'L') return 'W';
        const match = String(score || '').match(/^\s*(\d+)\s*:\s*(\d+)\s*$/);
        return match ? `${match[2]}:${match[1]}` : score;
    }

    function scoreKey(playerA, playerB) {
        const first = Math.min(Number(playerA), Number(playerB));
        const second = Math.max(Number(playerA), Number(playerB));
        return `${first}_${second}`;
    }

    function parseOrientedScore(playerA, playerB, rawScore) {
        if (!isPlayedScore(rawScore)) return null;
        const first = Math.min(Number(playerA), Number(playerB));
        const aIsFirst = Number(playerA) === first;

        if (rawScore === 'W' || rawScore === 'L') {
            const firstWon = rawScore === 'W';
            return {
                aWon: aIsFirst ? firstWon : !firstWon,
                aGames: null,
                bGames: null,
                technical: true
            };
        }

        const match = String(rawScore).match(/^\s*(\d+)\s*:\s*(\d+)\s*$/);
        const firstGames = Number(match[1]);
        const secondGames = Number(match[2]);
        return {
            aWon: aIsFirst ? firstGames > secondGames : secondGames > firstGames,
            aGames: aIsFirst ? firstGames : secondGames,
            bGames: aIsFirst ? secondGames : firstGames,
            technical: false
        };
    }

    function groupCountForPlayers(playerCount) {
        const count = Math.floor(asNumber(playerCount, 0));
        if (count < 3) return 0;
        return Math.floor(count / 3);
    }

    /**
     * Snake distribution. For N=13 and four groups, the thirteenth seed goes
     * to group 4 because the fourth row continues in the reverse direction.
     */
    function buildSnakeGroups(seedOrder) {
        const order = Array.isArray(seedOrder) ? seedOrder.slice() : [];
        const groupCount = groupCountForPlayers(order.length);
        if (!groupCount) return [];

        const groups = Array.from({ length: groupCount }, (_, index) => ({
            id: index + 1,
            order: index,
            players: []
        }));

        order.forEach((playerIndex, rankIndex) => {
            const row = Math.floor(rankIndex / groupCount);
            const position = rankIndex % groupCount;
            const groupIndex = row % 2 === 0 ? position : groupCount - 1 - position;
            groups[groupIndex].players.push(Number(playerIndex));
        });

        return groups;
    }

    function shuffleBucket(items, randomFn) {
        const output = items.slice();
        const rng = typeof randomFn === 'function' ? randomFn : Math.random;
        for (let index = output.length - 1; index > 0; index--) {
            const swapIndex = Math.floor(Math.max(0, Math.min(0.999999999, rng())) * (index + 1));
            const temp = output[index];
            output[index] = output[swapIndex];
            output[swapIndex] = temp;
        }
        return output;
    }

    /** Sort descending by frozen rating and randomize equal-rating buckets once. */
    function buildSeedOrder(seedRatings, randomFn) {
        const rows = normalizeArray(seedRatings).map((item, index) => ({
            playerIndex: asNumber(item.playerIndex, index),
            rating: asNumber(item.rating, 0)
        }));
        rows.sort((a, b) => b.rating - a.rating || a.playerIndex - b.playerIndex);

        const order = [];
        let cursor = 0;
        while (cursor < rows.length) {
            let end = cursor + 1;
            while (end < rows.length && rows[end].rating === rows[cursor].rating) end++;
            const bucket = rows.slice(cursor, end).map(row => row.playerIndex);
            order.push(...(bucket.length > 1 ? shuffleBucket(bucket, randomFn) : bucket));
            cursor = end;
        }
        return order;
    }

    function createGroupMatches(groups, tournamentId, createdAt) {
        const tournamentKey = String(tournamentId || uniqueToken('tournament'));
        const timestamp = asNumber(createdAt, Date.now());
        const matches = [];
        normalizeArray(groups).forEach(group => {
            const players = normalizeArray(group.players).map(Number);
            for (let firstIndex = 0; firstIndex < players.length; firstIndex++) {
                for (let secondIndex = firstIndex + 1; secondIndex < players.length; secondIndex++) {
                    const p1 = Math.min(players[firstIndex], players[secondIndex]);
                    const p2 = Math.max(players[firstIndex], players[secondIndex]);
                    matches.push({
                        id: `${tournamentKey}:group:${group.id}:${p1}-${p2}`,
                        stage: 'group',
                        groupId: Number(group.id),
                        p1,
                        p2,
                        scoreKey: `${p1}_${p2}`,
                        createdAt: timestamp + matches.length
                    });
                }
            }
        });
        return matches;
    }

    function compareGameRatios(left, right) {
        const leftInfinity = left.gamesLost === 0 && left.gamesWon > 0;
        const rightInfinity = right.gamesLost === 0 && right.gamesWon > 0;
        if (leftInfinity || rightInfinity) {
            if (leftInfinity && rightInfinity) return 0;
            return leftInfinity ? -1 : 1;
        }

        const leftZero = left.gamesLost === 0 && left.gamesWon === 0;
        const rightZero = right.gamesLost === 0 && right.gamesWon === 0;
        if (leftZero || rightZero) {
            if (leftZero && rightZero) return 0;
            return leftZero ? 1 : -1;
        }

        const crossLeft = left.gamesWon * right.gamesLost;
        const crossRight = right.gamesWon * left.gamesLost;
        if (crossLeft === crossRight) return 0;
        return crossLeft > crossRight ? -1 : 1;
    }

    function groupTieKey(groupId, indices) {
        return `group:${Number(groupId)}:${indices.map(Number).sort((a, b) => a - b).join(',')}`;
    }

    function rankTieByResults(indices, getResult, depth) {
        const members = indices.map(Number);
        if (members.length <= 1) return { ordered: members.slice(), unresolvedGroups: [] };
        if (depth > members.length + 4) {
            const fallback = members.slice().sort((a, b) => a - b);
            return { ordered: fallback, unresolvedGroups: [fallback] };
        }

        const stats = members.map(index => ({ index, miniWins: 0, gamesWon: 0, gamesLost: 0 }));
        const byPlayer = new Map(stats.map(item => [item.index, item]));
        for (let first = 0; first < members.length; first++) {
            for (let second = first + 1; second < members.length; second++) {
                const p1 = members[first];
                const p2 = members[second];
                const result = getResult(p1, p2);
                if (!result) continue;
                if (result.aWon) byPlayer.get(p1).miniWins++;
                else byPlayer.get(p2).miniWins++;
                if (!result.technical) {
                    byPlayer.get(p1).gamesWon += result.aGames;
                    byPlayer.get(p1).gamesLost += result.bGames;
                    byPlayer.get(p2).gamesWon += result.bGames;
                    byPlayer.get(p2).gamesLost += result.aGames;
                }
            }
        }

        stats.sort((a, b) => {
            if (a.miniWins !== b.miniWins) return b.miniWins - a.miniWins;
            const ratioOrder = compareGameRatios(a, b);
            if (ratioOrder !== 0) return ratioOrder;
            return a.index - b.index;
        });

        const buckets = [];
        stats.forEach(item => {
            const previous = buckets[buckets.length - 1];
            const same = previous &&
                previous[0].miniWins === item.miniWins &&
                compareGameRatios(previous[0], item) === 0;
            if (same) previous.push(item);
            else buckets.push([item]);
        });

        if (buckets.length === 1 && buckets[0].length === members.length) {
            const fallback = members.slice().sort((a, b) => a - b);
            return { ordered: fallback, unresolvedGroups: [fallback] };
        }

        const ordered = [];
        const unresolvedGroups = [];
        buckets.forEach(bucket => {
            const bucketMembers = bucket.map(item => item.index);
            if (bucketMembers.length === 1) {
                ordered.push(bucketMembers[0]);
            } else {
                const nested = rankTieByResults(bucketMembers, getResult, depth + 1);
                ordered.push(...nested.ordered);
                unresolvedGroups.push(...nested.unresolvedGroups);
            }
        });
        return { ordered, unresolvedGroups };
    }

    function calculateStandingsForIndices(indices, getResult) {
        const members = indices.map(Number);
        const wins = new Map(members.map(index => [index, 0]));
        for (let first = 0; first < members.length; first++) {
            for (let second = first + 1; second < members.length; second++) {
                const p1 = members[first];
                const p2 = members[second];
                const result = getResult(p1, p2);
                if (!result) continue;
                const winner = result.aWon ? p1 : p2;
                wins.set(winner, (wins.get(winner) || 0) + 1);
            }
        }

        const byWins = new Map();
        members.forEach(index => {
            const value = wins.get(index) || 0;
            if (!byWins.has(value)) byWins.set(value, []);
            byWins.get(value).push(index);
        });

        const ordered = [];
        const unresolvedGroups = [];
        Array.from(byWins.keys()).sort((a, b) => b - a).forEach(winCount => {
            const tied = byWins.get(winCount);
            if (tied.length === 1) {
                ordered.push(tied[0]);
                return;
            }
            const ranked = rankTieByResults(tied, getResult, 0);
            ordered.push(...ranked.ordered);
            unresolvedGroups.push(...ranked.unresolvedGroups);
        });
        return { ordered, unresolvedGroups, wins };
    }

    function latestTieRound(extraMatches, groupId, indices) {
        const key = groupTieKey(groupId, indices);
        const matches = normalizeArray(extraMatches).filter(match =>
            !match.voided &&
            Number(match.groupId) === Number(groupId) &&
            String(match.resolutionKey) === key
        );
        if (!matches.length) return null;
        const round = Math.max(...matches.map(match => Math.max(1, asNumber(match.round, 1))));
        return {
            key,
            round,
            matches: matches.filter(match => Math.max(1, asNumber(match.round, 1)) === round)
        };
    }

    function replaceUnresolvedGroups(parentOrdered, subgroupArrays, resolver, parentIndices) {
        const subgroupByMember = new Map();
        subgroupArrays.forEach(group => group.forEach(index => subgroupByMember.set(Number(index), group)));
        const handled = new Set();
        const ordered = [];
        const unresolvedGroups = [];
        const parentKey = parentIndices.map(Number).sort((a, b) => a - b).join(',');

        parentOrdered.forEach(index => {
            const group = subgroupByMember.get(Number(index));
            if (!group) {
                ordered.push(Number(index));
                return;
            }
            const key = group.map(Number).sort((a, b) => a - b).join(',');
            if (handled.has(key)) return;
            handled.add(key);
            if (key === parentKey) {
                ordered.push(...group.map(Number));
                unresolvedGroups.push(group.map(Number));
                return;
            }
            const nested = resolver(group.map(Number));
            ordered.push(...nested.ordered);
            unresolvedGroups.push(...nested.unresolvedGroups);
        });
        return { ordered, unresolvedGroups };
    }

    /**
     * Additional matches follow the already used round-robin rule: only the
     * latest complete extra round for the exact tied subgroup is decisive.
     * Earlier rounds remain stored as separate historical matches.
     */
    function resolveExtraTieGroup(indices, context) {
        const members = indices.map(Number);
        const manualKey = groupTieKey(context.groupId, members);
        const manualOrder = context.manualOrders && context.manualOrders[manualKey];
        if (Array.isArray(manualOrder) && manualOrder.length === members.length) {
            const expected = members.slice().sort((a, b) => a - b);
            const actual = manualOrder.map(Number).slice().sort((a, b) => a - b);
            if (expected.every((value, index) => value === actual[index])) {
                return { ordered: manualOrder.map(Number), unresolvedGroups: [] };
            }
        }

        const latest = latestTieRound(context.extraMatches, context.groupId, members);
        if (!latest) {
            const fallback = members.slice().sort((a, b) => a - b);
            return { ordered: fallback, unresolvedGroups: [fallback] };
        }

        const expectedMatches = members.length * (members.length - 1) / 2;
        const played = latest.matches.filter(match => isPlayedScore(match.score));
        if (latest.matches.length !== expectedMatches || played.length !== expectedMatches) {
            const fallback = members.slice().sort((a, b) => a - b);
            return { ordered: fallback, unresolvedGroups: [fallback] };
        }

        const byPair = new Map();
        played.forEach(match => byPair.set(scoreKey(match.p1, match.p2), match));
        const roundResult = calculateStandingsForIndices(members, (playerA, playerB) => {
            const match = byPair.get(scoreKey(playerA, playerB));
            return match ? parseOrientedScore(playerA, playerB, match.score) : null;
        });
        if (!roundResult.unresolvedGroups.length) {
            return { ordered: roundResult.ordered, unresolvedGroups: [] };
        }

        return replaceUnresolvedGroups(
            roundResult.ordered,
            roundResult.unresolvedGroups,
            subgroup => resolveExtraTieGroup(subgroup, context),
            members
        );
    }

    function calculateGroupStandings(group, scores, extraMatches, manualOrders) {
        const groupId = Number(group.id);
        const players = normalizeArray(group.players).map(Number);
        const base = calculateStandingsForIndices(players, (playerA, playerB) =>
            parseOrientedScore(playerA, playerB, (scores || {})[scoreKey(playerA, playerB)])
        );

        const unresolvedByMember = new Map();
        base.unresolvedGroups.forEach(tied => tied.forEach(index => unresolvedByMember.set(Number(index), tied)));
        const handled = new Set();
        const ordered = [];
        const unresolvedRaw = [];
        base.ordered.forEach(index => {
            const tied = unresolvedByMember.get(Number(index));
            if (!tied) {
                ordered.push(Number(index));
                return;
            }
            const key = groupTieKey(groupId, tied);
            if (handled.has(key)) return;
            handled.add(key);
            const resolved = resolveExtraTieGroup(tied, {
                groupId,
                extraMatches: normalizeArray(extraMatches).filter(match => Number(match.groupId) === groupId),
                manualOrders: manualOrders || {}
            });
            ordered.push(...resolved.ordered);
            unresolvedRaw.push(...resolved.unresolvedGroups);
        });

        const standings = ordered.map(index => ({ index, points: base.wins.get(index) || 0 }));
        const positionByIndex = new Map(standings.map((item, index) => [item.index, index + 1]));
        const unresolvedGroups = unresolvedRaw.map(indices => {
            const places = indices.map(index => positionByIndex.get(Number(index))).filter(Boolean);
            return {
                groupId,
                groupKey: groupTieKey(groupId, indices),
                indices: indices.map(Number),
                startPlace: Math.min(...places),
                endPlace: Math.max(...places)
            };
        });
        return { standings, unresolvedGroups };
    }

    function calculateAllGroupStandings(draft) {
        const scores = draft && draft.matchScores ? draft.matchScores : {};
        const extraMatches = normalizeArray(draft && draft.groupTieBreakMatches);
        const manualOrders = draft && draft.groupManualTieOrders ? draft.groupManualTieOrders : {};
        return normalizeArray(draft && draft.groups).map(group => ({
            group,
            resolution: calculateGroupStandings(group, scores, extraMatches, manualOrders)
        }));
    }

    function resolveActiveRecord(draft, activeValue) {
        if (typeof activeValue !== 'string') return null;
        if (activeValue.startsWith(MAIN_PREFIX)) {
            const id = activeValue.slice(MAIN_PREFIX.length);
            const match = normalizeArray(draft.groupMatches).find(item => String(item.id) === id);
            return match ? { kind: 'main', match } : null;
        }
        if (activeValue.startsWith(TIE_PREFIX)) {
            const id = activeValue.slice(TIE_PREFIX.length);
            const match = normalizeArray(draft.groupTieBreakMatches).find(item => String(item.id) === id);
            return match ? { kind: 'tie', match } : null;
        }
        return null;
    }

    function recordIsPlayed(draft, record) {
        if (!record) return true;
        if (record.match && record.match.voided) return true;
        if (record.kind === 'main') {
            return isPlayedScore((draft.matchScores || {})[record.match.scoreKey]);
        }
        return isPlayedScore(record.match.score);
    }

    function enumerateGroupOptions(candidates, busyPlayers, maxSlots) {
        const options = [{ chosen: [], cost: 0 }];
        const maxCount = Math.max(0, Number(maxSlots) || 0);
        const busy = new Set(busyPlayers || []);

        function walk(start, chosen, used, cost) {
            if (chosen.length > 0) options.push({ chosen: chosen.slice(), cost });
            if (chosen.length >= maxCount) return;
            for (let index = start; index < candidates.length; index++) {
                const candidate = candidates[index];
                if (busy.has(candidate.p1) || busy.has(candidate.p2) || used.has(candidate.p1) || used.has(candidate.p2)) continue;
                used.add(candidate.p1);
                used.add(candidate.p2);
                chosen.push(candidate);
                walk(index + 1, chosen, used, cost + candidate.cost);
                chosen.pop();
                used.delete(candidate.p1);
                used.delete(candidate.p2);
            }
        }
        walk(0, [], new Set(), 0);

        const bestBySignature = new Map();
        options.forEach(option => {
            const signature = option.chosen.map(item => item.activeValue).sort().join('|');
            const previous = bestBySignature.get(signature);
            if (!previous || option.cost < previous.cost) bestBySignature.set(signature, option);
        });
        return Array.from(bestBySignature.values());
    }

    /**
     * Selects a maximum-cardinality set of disjoint matches. Since players are
     * partitioned into groups of at most four, dynamic programming by group is
     * exact and inexpensive.
     */
    function selectDisjointCandidates(candidates, busyPlayers, slotCount) {
        const limit = Math.max(0, Number(slotCount) || 0);
        if (!limit) return [];
        const byGroup = new Map();
        normalizeArray(candidates).forEach(candidate => {
            const key = Number(candidate.groupId);
            if (!byGroup.has(key)) byGroup.set(key, []);
            byGroup.get(key).push(candidate);
        });

        let dp = new Map([[0, { cost: 0, chosen: [] }]]);
        Array.from(byGroup.keys()).sort((a, b) => a - b).forEach(groupId => {
            const groupCandidates = byGroup.get(groupId).slice().sort((a, b) => a.cost - b.cost || a.activeValue.localeCompare(b.activeValue));
            const options = enumerateGroupOptions(groupCandidates, busyPlayers, limit);
            const next = new Map();
            dp.forEach((state, count) => {
                options.forEach(option => {
                    const nextCount = count + option.chosen.length;
                    if (nextCount > limit) return;
                    const nextCost = state.cost + option.cost;
                    const previous = next.get(nextCount);
                    if (!previous || nextCost < previous.cost) {
                        next.set(nextCount, { cost: nextCost, chosen: state.chosen.concat(option.chosen) });
                    }
                });
            });
            dp = next;
        });

        const bestCount = Math.max(...Array.from(dp.keys()));
        return (dp.get(bestCount) || { chosen: [] }).chosen;
    }

    function refreshActiveGroupMatches(draft) {
        if (!draft) return;
        const playersCount = asNumber(draft.playersCount, 0);
        const requestedTables = Math.max(1, asNumber(draft.tablesCount, 1));
        const effectiveTables = Math.max(1, Math.min(requestedTables, Math.max(1, Math.floor(playersCount / 2))));
        const withdrawn = new Set(normalizeArray(draft.withdrawnPlayers).map(Number));
        const active = Array.isArray(draft.activeGroupMatches) ? draft.activeGroupMatches.slice(0, effectiveTables) : [];
        while (active.length < effectiveTables) active.push(null);

        for (let index = 0; index < active.length; index++) {
            const record = resolveActiveRecord(draft, active[index]);
            if (!record || recordIsPlayed(draft, record) || withdrawn.has(Number(record.match.p1)) || withdrawn.has(Number(record.match.p2))) {
                active[index] = null;
            }
        }

        const seenPlayers = new Set();
        const seenActiveValues = new Set();
        for (let index = 0; index < active.length; index++) {
            const value = active[index];
            const record = resolveActiveRecord(draft, value);
            if (!record) continue;
            const p1 = Number(record.match.p1);
            const p2 = Number(record.match.p2);
            if (seenActiveValues.has(value) || seenPlayers.has(p1) || seenPlayers.has(p2)) {
                active[index] = null;
                continue;
            }
            seenActiveValues.add(value);
            seenPlayers.add(p1);
            seenPlayers.add(p2);
        }

        const busyPlayers = new Set();
        const activeValues = new Set();
        active.forEach(value => {
            const record = resolveActiveRecord(draft, value);
            if (!record) return;
            activeValues.add(value);
            busyPlayers.add(Number(record.match.p1));
            busyPlayers.add(Number(record.match.p2));
        });

        const emptySlots = [];
        active.forEach((value, index) => { if (!value) emptySlots.push(index); });
        if (!emptySlots.length) {
            draft.activeGroupMatches = active;
            return;
        }

        const matchesPlayed = new Map();
        for (let player = 0; player < playersCount; player++) matchesPlayed.set(player, 0);
        normalizeArray(draft.groupMatches).forEach(match => {
            if (isPlayedScore((draft.matchScores || {})[match.scoreKey])) {
                matchesPlayed.set(Number(match.p1), (matchesPlayed.get(Number(match.p1)) || 0) + 1);
                matchesPlayed.set(Number(match.p2), (matchesPlayed.get(Number(match.p2)) || 0) + 1);
            }
        });
        normalizeArray(draft.groupTieBreakMatches).forEach(match => {
            if (!match.voided && isPlayedScore(match.score)) {
                matchesPlayed.set(Number(match.p1), (matchesPlayed.get(Number(match.p1)) || 0) + 1);
                matchesPlayed.set(Number(match.p2), (matchesPlayed.get(Number(match.p2)) || 0) + 1);
            }
        });

        const lastFinished = new Set(normalizeArray(draft.lastFinishedPlayers).map(Number));
        const candidates = [];
        normalizeArray(draft.groupMatches).forEach(match => {
            const activeValue = `${MAIN_PREFIX}${match.id}`;
            if (activeValues.has(activeValue) || isPlayedScore((draft.matchScores || {})[match.scoreKey])) return;
            const p1 = Number(match.p1);
            const p2 = Number(match.p2);
            if (withdrawn.has(p1) || withdrawn.has(p2)) return;
            const restPenalty = (lastFinished.has(p1) ? 10000 : 0) + (lastFinished.has(p2) ? 10000 : 0);
            const loadCost = ((matchesPlayed.get(p1) || 0) + (matchesPlayed.get(p2) || 0)) * 100;
            candidates.push({
                activeValue,
                kind: 'main',
                groupId: Number(match.groupId),
                p1,
                p2,
                cost: restPenalty + loadCost + asNumber(match.createdAt, 0) / 1e13
            });
        });
        normalizeArray(draft.groupTieBreakMatches).forEach(match => {
            if (match.voided) return;
            const activeValue = `${TIE_PREFIX}${match.id}`;
            if (activeValues.has(activeValue) || isPlayedScore(match.score)) return;
            const p1 = Number(match.p1);
            const p2 = Number(match.p2);
            if (withdrawn.has(p1) || withdrawn.has(p2)) return;
            const restPenalty = (lastFinished.has(p1) ? 10000 : 0) + (lastFinished.has(p2) ? 10000 : 0);
            const loadCost = ((matchesPlayed.get(p1) || 0) + (matchesPlayed.get(p2) || 0)) * 100;
            candidates.push({
                activeValue,
                kind: 'tie',
                groupId: Number(match.groupId),
                p1,
                p2,
                cost: restPenalty + loadCost - 25 + asNumber(match.createdAt, 0) / 1e13
            });
        });

        const selected = selectDisjointCandidates(candidates, busyPlayers, emptySlots.length);
        selected.forEach((candidate, index) => {
            active[emptySlots[index]] = candidate.activeValue;
        });
        draft.activeGroupMatches = active;
    }

    function getGroupForPlayer(draft, playerIndex) {
        return normalizeArray(draft && draft.groups).find(group => normalizeArray(group.players).map(Number).includes(Number(playerIndex))) || null;
    }

    function getGroupMatch(draft, matchId) {
        return normalizeArray(draft && draft.groupMatches).find(match => String(match.id) === String(matchId)) || null;
    }

    function getGroupTieMatch(draft, matchId) {
        return normalizeArray(draft && draft.groupTieBreakMatches).find(match => String(match.id) === String(matchId)) || null;
    }

    function groupProgress(draft) {
        const matches = normalizeArray(draft && draft.groupMatches);
        const played = matches.filter(match => isPlayedScore((draft.matchScores || {})[match.scoreKey])).length;
        return { played, total: matches.length, allPlayed: matches.length > 0 && played === matches.length };
    }

    function buildPlayoffParticipants(draft, resolutions) {
        const byGroup = resolutions || calculateAllGroupStandings(draft);
        const seedRatingByIndex = new Map(normalizeArray(draft.seedRatings).map(item => [Number(item.playerIndex), asNumber(item.rating, 0)]));
        const maxSize = Math.max(0, ...byGroup.map(item => item.resolution.standings.length));
        const participants = [];
        for (let place = 1; place <= maxSize; place++) {
            byGroup.slice().sort((a, b) => Number(a.group.id) - Number(b.group.id)).forEach(item => {
                const standing = item.resolution.standings[place - 1];
                if (!standing) return;
                participants.push({
                    playerIndex: Number(standing.index),
                    groupId: Number(item.group.id),
                    groupPlace: place,
                    groupWins: Number(standing.points) || 0,
                    seedRating: seedRatingByIndex.get(Number(standing.index)) || 0
                });
            });
        }
        return participants;
    }

    function updateGroupStageState(draft) {
        const progress = groupProgress(draft);
        if (!progress.allPlayed) {
            draft.groupStageCompleted = false;
            if (draft.currentStage === PLAYOFF_TAB) draft.currentStage = GROUPS_TAB;
            delete draft.playoffParticipants;
            delete draft.playoffBracket;
            delete draft.groupHistorySnapshot;
            return { completed: false, progress, unresolvedGroups: [] };
        }

        const resolutions = calculateAllGroupStandings(draft);
        const unresolvedGroups = resolutions.flatMap(item => item.resolution.unresolvedGroups);
        const pendingExtra = normalizeArray(draft.groupTieBreakMatches).filter(match => !match.voided && !isPlayedScore(match.score));
        if (unresolvedGroups.length || pendingExtra.length) {
            draft.groupStageCompleted = false;
            if (draft.currentStage === PLAYOFF_TAB) draft.currentStage = GROUPS_TAB;
            delete draft.playoffParticipants;
            delete draft.playoffBracket;
            delete draft.groupHistorySnapshot;
            return { completed: false, progress, unresolvedGroups, pendingExtra };
        }

        const participants = buildPlayoffParticipants(draft, resolutions);
        draft.groupStageCompleted = true;
        draft.groupCompletedAt = draft.groupCompletedAt || Date.now();
        draft.playoffParticipants = participants;
        draft.playoffBracket = draft.playoffBracket || {
            status: 'awaiting_approved_playoff_spec',
            createdAt: Date.now(),
            participants: participants.map(item => ({ ...item })),
            matches: []
        };
        draft.groupHistorySnapshot = {
            format: FORMAT,
            tournamentId: draft.tournamentId,
            completedAt: draft.groupCompletedAt,
            playerFieldsJson: draft.playerFieldsJson || '[]',
            seedRatings: normalizeArray(draft.seedRatings).map(item => ({ ...item })),
            seedOrder: normalizeArray(draft.seedOrder).map(Number),
            groups: normalizeArray(draft.groups).map(group => ({ id: Number(group.id), order: Number(group.order), players: normalizeArray(group.players).map(Number) })),
            matches: normalizeArray(draft.groupMatches).map(match => {
                const score = (draft.matchScores || {})[match.scoreKey] || null;
                const result = parseOrientedScore(match.p1, match.p2, score);
                return {
                    id: match.id,
                    stage: 'group',
                    groupId: Number(match.groupId),
                    p1: Number(match.p1),
                    p2: Number(match.p2),
                    score,
                    winner: result ? (result.aWon ? Number(match.p1) : Number(match.p2)) : null,
                    loser: result ? (result.aWon ? Number(match.p2) : Number(match.p1)) : null,
                    technical: !!(match.technical || (result && result.technical)),
                    createdAt: match.createdAt || null,
                    completedAt: match.completedAt || null
                };
            }),
            additionalMatches: normalizeArray(draft.groupTieBreakMatches).map(match => ({ ...match })),
            standings: resolutions.map(item => ({
                groupId: Number(item.group.id),
                standings: item.resolution.standings.map((standing, index) => ({
                    place: index + 1,
                    playerIndex: Number(standing.index),
                    wins: Number(standing.points) || 0
                }))
            }))
        };
        return { completed: true, progress, unresolvedGroups: [], participants, resolutions };
    }

    function initializeGroupsPlayoffDraft(draft) {
        draft.format = FORMAT;
        draft.tournamentFormat = FORMAT;
        draft.currentStage = GROUPS_TAB;
        draft.groupStageCompleted = false;
        draft.groupCreatedAt = Date.now();
        draft.tournamentId = draft.tournamentId || uniqueToken('tournament');
        draft.seedOrder = buildSeedOrder(draft.seedRatings || []);
        draft.groups = buildSnakeGroups(draft.seedOrder);
        draft.groupMatches = createGroupMatches(draft.groups, draft.tournamentId, draft.groupCreatedAt);
        draft.groupTieBreakMatches = [];
        draft.groupManualTieOrders = {};
        draft.activeGroupMatches = [];
        draft.matchScores = {};
        draft.lastFinishedPlayers = [];
        delete draft.playoffScores;
        delete draft.playoffParticipants;
        delete draft.playoffBracket;
        delete draft.groupHistorySnapshot;
        refreshActiveGroupMatches(draft);
        return draft;
    }

    function resetGroupStageDependencies(draft, groupId, options) {
        const target = Number(groupId);
        const settings = options || {};
        const manual = { ...(draft.groupManualTieOrders || {}) };
        Object.keys(manual).forEach(key => {
            if (key.startsWith(`group:${target}:`)) delete manual[key];
        });
        draft.groupManualTieOrders = manual;

        if (settings.voidExtraMatches) {
            const reason = settings.voidReason || 'main_group_result_changed';
            draft.groupTieBreakMatches = normalizeArray(draft.groupTieBreakMatches).map(match => {
                if (Number(match.groupId) !== target || match.voided) return { ...match };
                return {
                    ...match,
                    voided: true,
                    voidedAt: Date.now(),
                    voidReason: reason
                };
            });
        }

        draft.groupStageCompleted = false;
        draft.currentStage = GROUPS_TAB;
        delete draft.groupCompletedAt;
        delete draft.playoffParticipants;
        delete draft.playoffBracket;
        delete draft.groupHistorySnapshot;
    }

    function invalidateGroupResolution(draft, groupId) {
        resetGroupStageDependencies(draft, groupId, {
            voidExtraMatches: true,
            voidReason: 'main_group_result_changed'
        });
    }

    // ---------------------------- Browser integration ----------------------------

    function browserAvailable() {
        return typeof window !== 'undefined' && typeof document !== 'undefined';
    }

    function getPlayerFields(draft) {
        try {
            const parsed = JSON.parse(draft.playerFieldsJson || '[]');
            return Array.isArray(parsed) ? parsed : [];
        } catch (_) {
            return [];
        }
    }

    function playerNamesForDraft(draft) {
        return getPlayerFields(draft).map((field, index) => {
            try {
                return decodeName(field.name).trim() || `Игрок ${index + 1}`;
            } catch (_) {
                return `Игрок ${index + 1}`;
            }
        });
    }

    function activeTableIndexFor(draft, activeValue) {
        const active = Array.isArray(draft && draft.activeGroupMatches) ? draft.activeGroupMatches : [];
        return active.findIndex(value => value === activeValue);
    }

    function scoreDisplayForPlayer(playerIndex, opponentIndex, rawScore) {
        if (!isPlayedScore(rawScore)) return { text: '-', className: '' };
        if (rawScore === 'W' || rawScore === 'L') {
            const isFirst = Number(playerIndex) < Number(opponentIndex);
            const won = rawScore === 'W' ? isFirst : !isFirst;
            return { text: won ? 'W' : 'L', className: won ? 'win' : 'lost' };
        }
        const oriented = Number(playerIndex) < Number(opponentIndex) ? rawScore : reverseScore(rawScore);
        const parts = String(oriented).split(':').map(Number);
        return { text: oriented, className: parts[0] > parts[1] ? 'win' : 'lost' };
    }

    function createElement(tag, className, html) {
        const element = document.createElement(tag);
        if (className) element.className = className;
        if (html != null) element.innerHTML = html;
        return element;
    }

    function showStatusCard(container, draft, state) {
        const card = createElement('div', 'gp-status-card');
        if (state.completed) {
            card.innerHTML = `
                <div class="gp-status-icon gp-status-success">✓</div>
                <div class="gp-status-copy">
                    <strong>Групповой этап завершён</strong>
                    <span>Все группы сыграны. Можно переходить к плей-офф.</span>
                </div>
            `;
            if (typeof isAdmin !== 'undefined' && isAdmin) {
                const button = createElement('button', 'gp-primary-button', 'Перейти к плей-офф');
                button.addEventListener('click', () => switchGroupsPlayoffTab(PLAYOFF_TAB));
                card.appendChild(button);
            }
        } else if (state.progress.allPlayed && state.pendingExtra && state.pendingExtra.length) {
            card.innerHTML = `
                <div class="gp-status-icon gp-status-warning">${state.pendingExtra.length}</div>
                <div class="gp-status-copy">
                    <strong>Идут дополнительные матчи</strong>
                    <span>Осталось внести результатов: ${state.pendingExtra.length}</span>
                </div>
            `;
        } else if (state.progress.allPlayed && state.unresolvedGroups.length) {
            card.innerHTML = `
                <div class="gp-status-icon gp-status-warning">!</div>
                <div class="gp-status-copy">
                    <strong>Нужно определить места</strong>
                    <span>Все основные матчи сыграны, но осталось неразрешимое равенство.</span>
                </div>
            `;
            if (typeof isAdmin !== 'undefined' && isAdmin) {
                const button = createElement('button', 'gp-secondary-button', 'Завершить групповой этап');
                button.addEventListener('click', openGroupTieResolutionModal);
                card.appendChild(button);
            }
        } else {
            card.innerHTML = `
                <div class="gp-status-icon">${state.progress.played}</div>
                <div class="gp-status-copy">
                    <strong>Групповой этап</strong>
                    <span>Сыграно матчей: ${state.progress.played} из ${state.progress.total}</span>
                </div>
            `;
        }
        container.appendChild(card);
    }

    function openGroupMainScoreDialog(match, tableIndex) {
        openAdminScoreDialog(Number(match.p1), Number(match.p2), match.scoreKey, tableIndex == null ? null : Number(tableIndex));
        activeScoreMatch.type = 'groupMain';
        activeScoreMatch.groupId = Number(match.groupId);
        activeScoreMatch.matchId = match.id;
    }

    function openGroupTieScoreDialog(match, tableIndex) {
        const key = scoreKey(match.p1, match.p2);
        openAdminScoreDialog(Number(match.p1), Number(match.p2), key, tableIndex == null ? null : Number(tableIndex));
        activeScoreMatch.type = 'groupTieBreak';
        activeScoreMatch.groupId = Number(match.groupId);
        activeScoreMatch.matchId = match.id;
    }

    function renderAdditionalMatches(groupContainer, draft, groupId, playerNames) {
        const matches = normalizeArray(draft.groupTieBreakMatches)
            .filter(match => Number(match.groupId) === Number(groupId) && !match.voided)
            .sort((a, b) => asNumber(a.createdAt, 0) - asNumber(b.createdAt, 0));
        if (!matches.length) return;

        const block = createElement('div', 'gp-extra-block');
        block.appendChild(createElement('div', 'gp-extra-title', 'Дополнительные матчи'));
        matches.forEach((match, index) => {
            const activeValue = `${TIE_PREFIX}${match.id}`;
            const tableIndex = activeTableIndexFor(draft, activeValue);
            const played = isPlayedScore(match.score);
            const row = createElement('button', `gp-extra-row${tableIndex >= 0 ? ' active' : ''}`);
            row.type = 'button';
            row.innerHTML = `
                <span class="gp-extra-number">${index + 1}</span>
                <span class="gp-extra-names">${escapeHtml(playerNames[Number(match.p1)])} — ${escapeHtml(playerNames[Number(match.p2)])}</span>
                <span class="gp-extra-score">${played ? escapeHtml(match.score) : (tableIndex >= 0 ? `Стол ${tableIndex + 1}` : 'Ожидается')}</span>
            `;
            if (typeof isAdmin !== 'undefined' && isAdmin && (played || tableIndex >= 0)) {
                row.addEventListener('click', () => openGroupTieScoreDialog(match, tableIndex >= 0 ? tableIndex : null));
            } else {
                row.disabled = true;
            }
            block.appendChild(row);
        });
        groupContainer.appendChild(block);
    }

    function renderGroupTable(container, draft, group, playerNames, resolution) {
        const section = createElement('section', 'gp-group-section');
        const heading = createElement('h3', 'gp-group-title', `<span class="gp-group-icon">👥</span> Группа ${Number(group.id)}`);
        section.appendChild(heading);

        const wrapper = createElement('div', 'table-scroll-wrapper gp-table-wrapper');
        const table = createElement('table', 'rr-grid gp-grid');
        const players = normalizeArray(group.players).map(Number);
        const positions = new Map(resolution.standings.map((item, index) => [Number(item.index), index + 1]));
        const unresolvedLabels = new Map();
        resolution.unresolvedGroups.forEach(tie => {
            const label = tie.startPlace === tie.endPlace ? String(tie.startPlace) : `${tie.startPlace}–${tie.endPlace}`;
            tie.indices.forEach(index => unresolvedLabels.set(Number(index), label));
        });

        const head = createElement('tr');
        head.innerHTML = '<th>№</th><th class="player-col-header">Участник</th>' +
            players.map((_, index) => `<th>${index + 1}</th>`).join('') +
            '<th>О</th><th>М</th>';
        table.appendChild(head);

        players.forEach((playerIndex, rowIndex) => {
            const row = createElement('tr');
            const withdrawn = new Set(normalizeArray(draft.withdrawnPlayers).map(Number)).has(playerIndex);
            let html = `<td>${rowIndex + 1}</td><td class="player-col-header${withdrawn ? ' gp-withdrawn' : ''}">${escapeHtml(playerNames[playerIndex] || `Игрок ${playerIndex + 1}`)}</td>`;

            players.forEach((opponentIndex, columnIndex) => {
                if (playerIndex === opponentIndex) {
                    html += '<td class="diagonal-cell"></td>';
                    return;
                }
                const key = scoreKey(playerIndex, opponentIndex);
                const match = normalizeArray(draft.groupMatches).find(item => Number(item.groupId) === Number(group.id) && item.scoreKey === key);
                const rawScore = (draft.matchScores || {})[key];
                const activeValue = match ? `${MAIN_PREFIX}${match.id}` : '';
                const tableIndex = activeValue ? activeTableIndexFor(draft, activeValue) : -1;
                const scoreInfo = scoreDisplayForPlayer(playerIndex, opponentIndex, rawScore);
                const activeClass = tableIndex >= 0 ? ' active' : '';
                const content = tableIndex >= 0 ? String(tableIndex + 1) : scoreInfo.text;
                const scoreClass = tableIndex >= 0 ? '' : ` ${scoreInfo.className}`;
                const clickable = typeof isAdmin !== 'undefined' && isAdmin && match && (tableIndex >= 0 || isPlayedScore(rawScore));
                html += `<td class="score-cell${activeClass}${scoreClass}${clickable ? ' gp-clickable' : ''}" data-match-id="${match ? escapeHtml(match.id) : ''}" data-table-index="${tableIndex}">${escapeHtml(content)}</td>`;
            });

            const standing = resolution.standings.find(item => Number(item.index) === playerIndex);
            const place = unresolvedLabels.get(playerIndex) || positions.get(playerIndex) || '-';
            html += `<td class="gp-points">${standing ? Number(standing.points) : 0}</td><td>${escapeHtml(place)}</td>`;
            row.innerHTML = html;

            if (typeof isAdmin !== 'undefined' && isAdmin) {
                row.querySelectorAll('.score-cell.gp-clickable').forEach(cell => {
                    cell.addEventListener('click', () => {
                        const match = getGroupMatch(draft, cell.getAttribute('data-match-id'));
                        if (!match) return;
                        const tableIndex = Number(cell.getAttribute('data-table-index'));
                        openGroupMainScoreDialog(match, tableIndex >= 0 ? tableIndex : null);
                    });
                });
            }
            table.appendChild(row);
        });

        wrapper.appendChild(table);
        section.appendChild(wrapper);
        renderAdditionalMatches(section, draft, group.id, playerNames);
        container.appendChild(section);
    }

    function renderGroupsTab(container, draft, playerNames) {
        const state = updateGroupStageState(draft);
        showStatusCard(container, draft, state);
        const calculations = calculateAllGroupStandings(draft);
        calculations.forEach(item => renderGroupTable(container, draft, item.group, playerNames, item.resolution));
    }

    function renderPlayoffTab(container, draft, playerNames) {
        /* FULL_PLAYOFF_11_40_BRIDGE_V203 */
        if (global.FullPlayoff11to40 && typeof global.FullPlayoff11to40.renderForDraft === 'function') {
            try {
                if (global.FullPlayoff11to40.renderForDraft(container, draft)) return;
            } catch (error) {
                console.error('[groups-playoff] full playoff bridge failed', error);
            }
        }
        if (!draft.groupStageCompleted) {
            container.appendChild(createElement('div', 'gp-playoff-placeholder', `
                <div class="gp-placeholder-icon">🔒</div>
                <strong>Плей-офф пока недоступен</strong>
                <span>Сначала нужно сыграть все матчи в группах и определить места.</span>
            `));
            return;
        }

        const participants = normalizeArray(draft.playoffParticipants);
        const card = createElement('div', 'gp-playoff-placeholder');
        card.innerHTML = `
            <div class="gp-placeholder-icon">🏆</div>
            <strong>Участники плей-офф сформированы</strong>
            <span>Полная схема сетки, маршруты победителей и матчи за места будут подключены после утверждения второй части ТЗ.</span>
        `;
        const list = createElement('div', 'gp-playoff-seeds');
        participants.forEach((participant, index) => {
            list.appendChild(createElement('div', 'gp-playoff-seed', `
                <span class="gp-seed-number">${index + 1}</span>
                <span class="gp-seed-name">${escapeHtml(playerNames[Number(participant.playerIndex)] || `Игрок ${Number(participant.playerIndex) + 1}`)}</span>
                <span class="gp-seed-source">Г${Number(participant.groupId)} · ${Number(participant.groupPlace)} место</span>
            `));
        });
        card.appendChild(list);

        if (typeof isAdmin !== 'undefined' && isAdmin) {
            card.appendChild(createElement('div', 'gp-reset-note',
                'Сетка плей-офф пока не реализована, поэтому этот тестовый турнир нельзя завершить обычным способом. Его можно удалить без сохранения в историю и без изменения рейтинга.'
            ));
            const resetButton = createElement('button', 'gp-danger-button', 'Удалить текущий турнир и создать новый');
            resetButton.type = 'button';
            resetButton.addEventListener('click', discardGroupsPlayoffTournament);
            card.appendChild(resetButton);
        }

        container.appendChild(card);
    }

    async function discardGroupsPlayoffTournament() {
        if (typeof isAdmin !== 'undefined' && !isAdmin) {
            showToast('Удалить турнир может только организатор');
            return;
        }
        if (typeof clubData === 'undefined' || !clubData.draft || clubData.draft.format !== FORMAT) return;

        const accepted = window.confirm(
            'Удалить текущий турнир и перейти к созданию нового?\n\n' +
            'Результаты этого тестового турнира не попадут в историю, а рейтинг игроков не изменится.'
        );
        if (!accepted) return;

        showLoader(true);
        try {
            await db.ref(`clubs/${activeClubId}/draft`).remove();
            clubData.draft = null;
            setViewTab(GROUPS_TAB);
            try { activeScoreMatch = null; } catch (_) {}
            showLoader(false);
            if (typeof global.updateActiveTournament === 'function') global.updateActiveTournament();
            showToast('Текущий турнир удалён. Можно создать новый.');
        } catch (error) {
            showLoader(false);
            showToast('Не удалось удалить турнир: ' + error.message);
        }
    }

    const viewTabByClub = new Map();

    function currentViewTab(draft) {
        let key = 'default';
        try { key = String(activeClubId || 'default'); } catch (_) {}
        return viewTabByClub.get(key) || (draft.currentStage === PLAYOFF_TAB ? PLAYOFF_TAB : GROUPS_TAB);
    }

    function setViewTab(tab) {
        let key = 'default';
        try { key = String(activeClubId || 'default'); } catch (_) {}
        viewTabByClub.set(key, tab);
    }

    function refreshGroupsPlayoffView(draft) {
        try {
            if (typeof clubData !== 'undefined' && draft) clubData.draft = draft;
            renderGroupsPlayoffTournament();
        } catch (error) {
            console.error('[groups-playoff] immediate render failed', error);
        }
    }

    function renderGroupsPlayoffTournament() {
        const container = document.getElementById('active-tournament-container');
        if (!container || typeof clubData === 'undefined' || !clubData.draft) return;
        const draft = clubData.draft;
        const playerNames = playerNamesForDraft(draft);
        container.innerHTML = '';

        const header = createElement('div', 'active-header-card');
        const broadcastIcon = (typeof SVG_ICONS !== 'undefined' && SVG_ICONS.broadcast) ? SVG_ICONS.broadcast : '🏓';
        header.innerHTML = `
            <div class="active-header-badge">${broadcastIcon}</div>
            <div class="active-header-info">
                <div class="active-header-title">${escapeHtml(draft.name || 'Турнир')}</div>
                <div class="active-header-desc">${playerNames.length} игроков • ${Number(draft.tablesCount) || 1} стола • до ${Number(draft.winsToWin) || 2} побед</div>
            </div>
        `;
        container.appendChild(header);

        const activeTab = currentViewTab(draft);
        const tabs = createElement('div', 'gp-tabs');
        const groupsButton = createElement('button', `gp-tab${activeTab === GROUPS_TAB ? ' active' : ''}`, 'Группы');
        const playoffButton = createElement('button', `gp-tab${activeTab === PLAYOFF_TAB ? ' active' : ''}`, 'Плей-офф');
        groupsButton.type = 'button';
        playoffButton.type = 'button';
        groupsButton.addEventListener('click', () => switchGroupsPlayoffTab(GROUPS_TAB));
        playoffButton.addEventListener('click', () => switchGroupsPlayoffTab(PLAYOFF_TAB));
        tabs.appendChild(groupsButton);
        tabs.appendChild(playoffButton);
        container.appendChild(tabs);

        if (activeTab === PLAYOFF_TAB) renderPlayoffTab(container, draft, playerNames);
        else renderGroupsTab(container, draft, playerNames);
    }

    async function switchGroupsPlayoffTab(tab) {
        if (typeof clubData === 'undefined' || !clubData.draft || clubData.draft.format !== FORMAT) return;
        const draft = clubData.draft;
        if (tab === PLAYOFF_TAB && !draft.groupStageCompleted) {
            showToast('Плей-офф откроется после завершения группового этапа');
            return;
        }
        setViewTab(tab);
        if (tab === PLAYOFF_TAB && draft.currentStage !== PLAYOFF_TAB && typeof isAdmin !== 'undefined' && isAdmin) {
            const updated = { ...draft, currentStage: PLAYOFF_TAB };
            try {
                await db.ref(`clubs/${activeClubId}/draft`).set(updated);
                refreshGroupsPlayoffView(updated);
            } catch (error) {
                showToast('Не удалось сохранить переход: ' + error.message);
            }
        }
        renderGroupsPlayoffTournament();
    }

    function ensureTieModal() {
        let overlay = document.getElementById('gp-tie-modal');
        if (overlay) return overlay;
        overlay = createElement('div', 'gp-modal-overlay');
        overlay.id = 'gp-tie-modal';
        overlay.innerHTML = `
            <div class="gp-modal-card" role="dialog" aria-modal="true" aria-labelledby="gp-tie-title">
                <div class="gp-modal-handle"></div>
                <h3 id="gp-tie-title">Не удалось определить места</h3>
                <div id="gp-tie-summary" class="gp-tie-summary"></div>
                <div id="gp-tie-manual" class="gp-tie-manual" style="display:none;"></div>
                <div id="gp-tie-actions" class="gp-modal-actions">
                    <button type="button" class="gp-primary-button" id="gp-create-extra">Создать дополнительные матчи</button>
                    <button type="button" class="gp-secondary-button" id="gp-manual-order">Указать места вручную</button>
                    <button type="button" class="gp-text-button" id="gp-continue">Продолжить турнир</button>
                </div>
            </div>
        `;
        overlay.addEventListener('click', event => { if (event.target === overlay) closeGroupTieModal(); });
        document.body.appendChild(overlay);
        return overlay;
    }

    function unresolvedStateForDraft(draft) {
        const calculations = calculateAllGroupStandings(draft);
        const unresolved = calculations.flatMap(item => item.resolution.unresolvedGroups);
        return { calculations, unresolved };
    }

    function openGroupTieResolutionModal() {
        if (typeof clubData === 'undefined' || !clubData.draft) return;
        const draft = clubData.draft;
        const playerNames = playerNamesForDraft(draft);
        const state = unresolvedStateForDraft(draft);
        if (!state.unresolved.length) {
            showToast('Все места уже определены');
            return;
        }

        const overlay = ensureTieModal();
        const summary = overlay.querySelector('#gp-tie-summary');
        const manual = overlay.querySelector('#gp-tie-manual');
        const actions = overlay.querySelector('#gp-tie-actions');
        summary.innerHTML = '';
        manual.innerHTML = '';
        manual.style.display = 'none';
        actions.style.display = 'flex';

        state.unresolved.forEach(tie => {
            const names = tie.indices.map(index => playerNames[Number(index)]).join(', ');
            summary.appendChild(createElement('div', 'gp-tie-card', `
                <strong>Группа ${Number(tie.groupId)} · места ${Number(tie.startPlace)}–${Number(tie.endPlace)}</strong>
                <span>${escapeHtml(names)}</span>
            `));
        });

        overlay.querySelector('#gp-create-extra').onclick = createGroupTieBreakMatches;
        overlay.querySelector('#gp-manual-order').onclick = showGroupManualOrderEditor;
        overlay.querySelector('#gp-continue').onclick = closeGroupTieModal;
        overlay.style.display = 'flex';
    }

    function closeGroupTieModal() {
        const overlay = document.getElementById('gp-tie-modal');
        if (overlay) overlay.style.display = 'none';
    }

    async function createGroupTieBreakMatches() {
        if (typeof clubData === 'undefined' || !clubData.draft) return;
        const draft = { ...clubData.draft };
        draft.groupTieBreakMatches = normalizeArray(draft.groupTieBreakMatches).map(match => ({ ...match }));
        const state = unresolvedStateForDraft(draft);
        let created = 0;

        state.unresolved.forEach(tie => {
            const existingForKey = draft.groupTieBreakMatches.filter(match => match.resolutionKey === tie.groupKey);
            if (existingForKey.some(match => !match.voided && !isPlayedScore(match.score))) return;
            const nextRound = existingForKey.reduce((max, match) => Math.max(max, asNumber(match.round, 0)), 0) + 1;
            const indices = tie.indices.map(Number);
            for (let first = 0; first < indices.length; first++) {
                for (let second = first + 1; second < indices.length; second++) {
                    const p1 = Math.min(indices[first], indices[second]);
                    const p2 = Math.max(indices[first], indices[second]);
                    draft.groupTieBreakMatches.push({
                        id: `${draft.tournamentId || 'tournament'}:group-tie:${tie.groupId}:${nextRound}:${p1}-${p2}:${uniqueToken('m')}`,
                        stage: 'group_tiebreak',
                        groupId: Number(tie.groupId),
                        resolutionKey: tie.groupKey,
                        round: nextRound,
                        p1,
                        p2,
                        score: null,
                        createdAt: Date.now() + created
                    });
                    created++;
                }
            }
        });

        if (!created) {
            showToast('Сначала внесите результаты уже созданных дополнительных матчей');
            return;
        }
        draft.groupManualTieOrders = { ...(draft.groupManualTieOrders || {}) };
        state.unresolved.forEach(tie => { delete draft.groupManualTieOrders[tie.groupKey]; });
        refreshActiveGroupMatches(draft);
        showLoader(true);
        try {
            await db.ref(`clubs/${activeClubId}/draft`).set(draft);
            closeGroupTieModal();
            refreshGroupsPlayoffView(draft);
            showLoader(false);
            showToast(`Создано дополнительных матчей: ${created}`);
        } catch (error) {
            showLoader(false);
            showToast('Ошибка создания матчей: ' + error.message);
        }
    }

    function showGroupManualOrderEditor() {
        if (typeof clubData === 'undefined' || !clubData.draft) return;
        const draft = clubData.draft;
        const playerNames = playerNamesForDraft(draft);
        const state = unresolvedStateForDraft(draft);
        const overlay = ensureTieModal();
        const manual = overlay.querySelector('#gp-tie-manual');
        const actions = overlay.querySelector('#gp-tie-actions');
        manual.innerHTML = '';

        state.unresolved.forEach((tie, tieIndex) => {
            const block = createElement('div', 'gp-manual-block');
            block.innerHTML = `<strong>Группа ${Number(tie.groupId)} · распределите места ${Number(tie.startPlace)}–${Number(tie.endPlace)}</strong>`;
            tie.indices.forEach((_, offset) => {
                const row = createElement('label', 'gp-manual-row');
                const place = Number(tie.startPlace) + offset;
                const options = tie.indices.map(index => `<option value="${Number(index)}">${escapeHtml(playerNames[Number(index)])}</option>`).join('');
                row.innerHTML = `<span>${place} место</span><select data-tie-index="${tieIndex}">${options}</select>`;
                const select = row.querySelector('select');
                select.value = String(tie.indices[offset]);
                block.appendChild(row);
            });
            manual.appendChild(block);
        });
        const save = createElement('button', 'gp-primary-button', 'Сохранить места');
        save.type = 'button';
        save.addEventListener('click', saveGroupManualOrders);
        manual.appendChild(save);
        actions.style.display = 'none';
        manual.style.display = 'flex';
    }

    async function saveGroupManualOrders() {
        if (typeof clubData === 'undefined' || !clubData.draft) return;
        const draft = { ...clubData.draft };
        draft.groupManualTieOrders = { ...(draft.groupManualTieOrders || {}) };
        const state = unresolvedStateForDraft(draft);
        const overlay = ensureTieModal();

        for (let tieIndex = 0; tieIndex < state.unresolved.length; tieIndex++) {
            const tie = state.unresolved[tieIndex];
            const selects = Array.from(overlay.querySelectorAll(`select[data-tie-index="${tieIndex}"]`));
            const order = selects.map(select => Number(select.value));
            const expected = tie.indices.map(Number).sort((a, b) => a - b);
            const actual = order.slice().sort((a, b) => a - b);
            if (order.length !== expected.length || new Set(order).size !== expected.length || expected.some((value, index) => value !== actual[index])) {
                showToast('Каждый спорный игрок должен быть выбран ровно один раз');
                return;
            }
            draft.groupManualTieOrders[tie.groupKey] = order;
        }

        const completion = updateGroupStageState(draft);
        refreshActiveGroupMatches(draft);
        showLoader(true);
        try {
            await db.ref(`clubs/${activeClubId}/draft`).set(draft);
            closeGroupTieModal();
            refreshGroupsPlayoffView(draft);
            showLoader(false);
            showToast(completion.completed ? 'Групповой этап завершён' : 'Итоговые места сохранены');
        } catch (error) {
            showLoader(false);
            showToast('Ошибка сохранения мест: ' + error.message);
        }
    }

    function updateFormatSheet() {
        const sheet = document.getElementById('format-select-sheet');
        if (!sheet) return;
        const buttons = sheet.querySelectorAll('button');
        if (buttons[0]) {
            buttons[0].setAttribute('onclick', "selectTournamentFormat('rr')");
            buttons[0].textContent = '🔄 Каждый с каждым';
        }
        if (buttons[1]) {
            buttons[1].setAttribute('onclick', "selectTournamentFormat('groups_playoff')");
            buttons[1].textContent = '🏆 Группы + плей-офф';
        }
    }

    function createTournamentForElevenPlus(originalCreate) {
        return function () {
            const count = Array.isArray(window.draftSetupPlayersList) ? window.draftSetupPlayersList.length : 0;

            if (typeof isAdmin === 'undefined' || !isAdmin || !firebase.auth().currentUser) {
                showToast('Создание турниров доступно только организатору');
                return;
            }
            const nameInput = document.getElementById('draft-name-input');
            const name = nameInput ? nameInput.value.trim() : '';
            if (!name) {
                showToast('Укажите название турнира!');
                return;
            }
            if (!window.draftSetup || Number(window.draftSetup.tablesCount) < 1) {
                showToast('Укажите количество столов!');
                return;
            }
            if (!Array.isArray(window.draftSetupPlayersList) || window.draftSetupPlayersList.length < 2) {
                showToast('Укажите как минимум 2 участников!');
                return;
            }
            for (let index = 0; index < window.draftSetupPlayersList.length; index++) {
                if (window.draftSetupPlayersList[index] == null) {
                    showToast(`Заполните участника под номером ${index + 1}!`);
                    return;
                }
            }

            showLoader(true);
            const selectedPlayers = clubData.players
                .filter(player => selectedPlayerIdsForDraft.has(player.id))
                .sort((a, b) => asNumber(b.rating, 0) - asNumber(a.rating, 0));
            if (selectedPlayers.length !== window.draftSetupPlayersList.length) {
                showLoader(false);
                showToast('Не удалось сопоставить всех выбранных игроков. Откройте список и выберите их повторно.');
                return;
            }

            const playerFields = selectedPlayers.map((player, index) => ({ id: index, name: safe_encode(player.fullName) }));
            const seedRatings = selectedPlayers.map((player, index) => ({
                playerIndex: index,
                playerId: String(player.id),
                rating: asNumber(player.rating, 0)
            }));
            const averageRating = selectedPlayers.reduce((sum, player) => sum + asNumber(player.rating, 0), 0) / selectedPlayers.length;
            const fixedK = averageRating < 250 ? 0.20 : (averageRating < 350 ? 0.25 : 0.30);
            const draftObj = {
                name,
                playersCount: selectedPlayers.length,
                tablesCount: Number(window.draftSetup.tablesCount),
                winsToWin: Math.max(1, asNumber(window.draftSetup.winsToWin, 2)),
                isListGenerated: true,
                fixedK,
                ratingApplied: false,
                historySaved: false,
                playerFieldsJson: JSON.stringify(playerFields),
                seedRatings,
                matchScores: {},
                withdrawnPlayers: [],
                lastFinishedPlayers: [],
                activeRoundRobinMatches: [],
                activeGroupMatches: []
            };

            if (selectedPlayers.length <= 10) {
                draftObj.format = 'rr';
                draftObj.tournamentFormat = 'rr';
                refreshActiveRoundRobinMatches(draftObj, draftObj.playersCount, draftObj.tablesCount);
                window._saveDraftToFirebase(draftObj);
                return;
            }

            showLoader(false);
            window._pendingDraftObj = draftObj;
            const description = document.getElementById('format-sheet-desc');
            if (description) description.textContent = `${selectedPlayers.length} участников — выберите формат`;
            const overlay = document.getElementById('format-select-sheet');
            updateFormatSheet();
            if (overlay) {
                overlay.style.display = 'flex';
                setTimeout(() => {
                    const panel = overlay.querySelector('div');
                    if (panel) panel.style.transform = 'translateY(0)';
                }, 10);
            }
        };
    }

    function formatSelector(originalSelector) {
        return function (format) {
            const draft = window._pendingDraftObj;
            if (!draft || (format !== 'rr' && format !== FORMAT)) {
                return originalSelector.apply(this, arguments);
            }
            window.closeFormatSelectSheet();
            showLoader(true);
            if (format === 'rr') {
                draft.format = 'rr';
                draft.tournamentFormat = 'rr';
                delete draft.playoffScores;
                delete draft.groups;
                delete draft.groupMatches;
                delete draft.groupTieBreakMatches;
                delete draft.groupManualTieOrders;
                delete draft.activeGroupMatches;
                refreshActiveRoundRobinMatches(draft, draft.playersCount, draft.tablesCount);
            } else {
                initializeGroupsPlayoffDraft(draft);
            }
            window._saveDraftToFirebase(draft);
            window._pendingDraftObj = null;
        };
    }

    function scoreSubmitter(originalSubmit) {
        return function (score) {
            if (!activeScoreMatch || (activeScoreMatch.type !== 'groupMain' && activeScoreMatch.type !== 'groupTieBreak')) {
                return originalSubmit.apply(this, arguments);
            }
            showLoader(true);
            const draft = { ...clubData.draft };
            draft.matchScores = { ...(draft.matchScores || {}) };
            draft.groupMatches = normalizeArray(draft.groupMatches).map(match => ({ ...match }));
            draft.groupTieBreakMatches = normalizeArray(draft.groupTieBreakMatches).map(match => ({ ...match }));

            if (activeScoreMatch.type === 'groupMain') {
                const matchIndex = draft.groupMatches.findIndex(match => String(match.id) === String(activeScoreMatch.matchId));
                if (matchIndex < 0) {
                    showLoader(false);
                    showToast('Матч группы не найден');
                    return;
                }
                const match = draft.groupMatches[matchIndex];
                const finalScore = Number(activeScoreMatch.f) > Number(activeScoreMatch.s) ? reverseScore(score) : score;
                draft.matchScores[match.scoreKey] = finalScore;
                match.score = finalScore;
                match.completedAt = Date.now();
                const result = parseOrientedScore(match.p1, match.p2, finalScore);
                if (result) {
                    match.winner = result.aWon ? Number(match.p1) : Number(match.p2);
                    match.loser = result.aWon ? Number(match.p2) : Number(match.p1);
                }
                invalidateGroupResolution(draft, match.groupId);
            } else {
                const matchIndex = draft.groupTieBreakMatches.findIndex(match => String(match.id) === String(activeScoreMatch.matchId));
                if (matchIndex < 0) {
                    showLoader(false);
                    showToast('Дополнительный матч не найден');
                    return;
                }
                const match = draft.groupTieBreakMatches[matchIndex];
                if (match.voided) {
                    showLoader(false);
                    showToast('Этот дополнительный матч был аннулирован после изменения результата группы');
                    return;
                }
                const finalScore = Number(activeScoreMatch.f) > Number(activeScoreMatch.s) ? reverseScore(score) : score;
                match.score = finalScore;
                match.completedAt = Date.now();
                const result = parseOrientedScore(match.p1, match.p2, finalScore);
                if (result) {
                    match.winner = result.aWon ? Number(match.p1) : Number(match.p2);
                    match.loser = result.aWon ? Number(match.p2) : Number(match.p1);
                }
                draft.groupManualTieOrders = { ...(draft.groupManualTieOrders || {}) };
                Object.keys(draft.groupManualTieOrders).forEach(key => {
                    if (key.startsWith(`group:${Number(match.groupId)}:`)) delete draft.groupManualTieOrders[key];
                });
                draft.groupStageCompleted = false;
                draft.currentStage = GROUPS_TAB;
                delete draft.groupCompletedAt;
                delete draft.playoffParticipants;
                delete draft.playoffBracket;
                delete draft.groupHistorySnapshot;
            }

            draft.lastFinishedPlayers = [Number(activeScoreMatch.f), Number(activeScoreMatch.s)];
            if (activeScoreMatch.index != null && Array.isArray(draft.activeGroupMatches)) {
                draft.activeGroupMatches[Number(activeScoreMatch.index)] = null;
            }
            refreshActiveGroupMatches(draft);
            const completion = updateGroupStageState(draft);

            db.ref(`clubs/${activeClubId}/draft`).set(draft)
                .then(() => {
                    closeAdminScoreModal();
                    refreshGroupsPlayoffView(draft);
                    showLoader(false);
                    if (completion.completed) showToast('Групповой этап завершён');
                    else if (completion.progress.allPlayed && completion.unresolvedGroups.length && !(completion.pendingExtra && completion.pendingExtra.length)) {
                        showToast('Все матчи сыграны. Нужно определить места.');
                        setTimeout(openGroupTieResolutionModal, 100);
                    } else showToast('Счет сохранен!');
                })
                .catch(error => {
                    showLoader(false);
                    showToast('Ошибка сохранения: ' + error.message);
                });
        };
    }

    function withdrawalSubmitter(originalWithdrawal) {
        return function (slotIndex) {
            if (!activeScoreMatch || (activeScoreMatch.type !== 'groupMain' && activeScoreMatch.type !== 'groupTieBreak')) {
                return originalWithdrawal.apply(this, arguments);
            }
            const target = Number(slotIndex) === 0 ? Number(activeScoreMatch.f) : Number(activeScoreMatch.s);
            const names = playerNamesForDraft(clubData.draft);
            if (!confirm(`Вы действительно хотите снять игрока "${names[target]}" с текущего турнира? Несыгранные матчи в его группе будут засчитаны как поражения.`)) return;

            showLoader(true);
            const draft = { ...clubData.draft };
            draft.withdrawnPlayers = Array.from(new Set(normalizeArray(draft.withdrawnPlayers).map(Number).concat([target])));
            draft.matchScores = { ...(draft.matchScores || {}) };
            draft.groupMatches = normalizeArray(draft.groupMatches).map(match => ({ ...match }));
            draft.groupTieBreakMatches = normalizeArray(draft.groupTieBreakMatches).map(match => ({ ...match }));
            const winsToWin = Math.max(1, asNumber(draft.winsToWin, 2));

            draft.groupMatches.forEach(match => {
                if (Number(match.p1) !== target && Number(match.p2) !== target) return;
                if (isPlayedScore(draft.matchScores[match.scoreKey])) return;
                const targetIsFirst = Number(match.p1) === target;
                const resultScore = targetIsFirst ? `0:${winsToWin}` : `${winsToWin}:0`;
                draft.matchScores[match.scoreKey] = resultScore;
                match.score = resultScore;
                match.completedAt = Date.now();
                match.winner = targetIsFirst ? Number(match.p2) : Number(match.p1);
                match.loser = target;
                match.technical = true;
            });
            draft.groupTieBreakMatches.forEach(match => {
                if (match.voided) return;
                if (Number(match.p1) !== target && Number(match.p2) !== target) return;
                if (isPlayedScore(match.score)) return;
                const targetIsFirst = Number(match.p1) === target;
                match.score = targetIsFirst ? `0:${winsToWin}` : `${winsToWin}:0`;
                match.completedAt = Date.now();
                match.winner = targetIsFirst ? Number(match.p2) : Number(match.p1);
                match.loser = target;
                match.technical = true;
            });

            const group = getGroupForPlayer(draft, target);
            if (group) resetGroupStageDependencies(draft, group.id, { voidExtraMatches: false });
            draft.activeGroupMatches = (Array.isArray(draft.activeGroupMatches) ? draft.activeGroupMatches : []).map(value => {
                const record = resolveActiveRecord(draft, value);
                return record && (Number(record.match.p1) === target || Number(record.match.p2) === target) ? null : value;
            });
            refreshActiveGroupMatches(draft);
            const completion = updateGroupStageState(draft);

            db.ref(`clubs/${activeClubId}/draft`).set(draft)
                .then(() => {
                    closeAdminScoreModal();
                    refreshGroupsPlayoffView(draft);
                    showLoader(false);
                    showToast(completion.completed ? 'Игрок снят. Групповой этап завершён.' : 'Игрок снят с турнира');
                })
                .catch(error => {
                    showLoader(false);
                    showToast('Ошибка сохранения: ' + error.message);
                });
        };
    }

    function installBrowserHooks() {
        if (!browserAvailable()) return;
        if (global.__GROUPS_PLAYOFF_PATCH_INSTALLED__) return;
        global.__GROUPS_PLAYOFF_PATCH_INSTALLED__ = PATCH_VERSION;

        updateFormatSheet();
        const originalUpdate = global.updateActiveTournament;
        const originalCreate = global.submitCreateTournament;
        const originalSelect = global.selectTournamentFormat;
        const originalSubmitScore = global.submitQuickScore;
        const originalWithdrawal = global.submitPlayerWithdrawal;

        if (typeof originalUpdate === 'function') {
            global.updateActiveTournament = function () {
                if (typeof clubData !== 'undefined' && clubData.draft && clubData.draft.format === FORMAT && clubData.draft.isListGenerated) {
                    return renderGroupsPlayoffTournament();
                }
                return originalUpdate.apply(this, arguments);
            };
        }
        if (typeof originalCreate === 'function') global.submitCreateTournament = createTournamentForElevenPlus(originalCreate);
        if (typeof originalSelect === 'function') global.selectTournamentFormat = formatSelector(originalSelect);
        if (typeof originalSubmitScore === 'function') global.submitQuickScore = scoreSubmitter(originalSubmitScore);
        if (typeof originalWithdrawal === 'function') global.submitPlayerWithdrawal = withdrawalSubmitter(originalWithdrawal);

        global.switchGroupsPlayoffTab = switchGroupsPlayoffTab;
        global.openGroupTieResolutionModal = openGroupTieResolutionModal;
        global.closeGroupTieModal = closeGroupTieModal;
        global.discardGroupsPlayoffTournament = discardGroupsPlayoffTournament;

        setTimeout(() => {
            updateFormatSheet();
            try {
                if (typeof clubData !== 'undefined' && clubData.draft && clubData.draft.format === FORMAT) {
                    global.updateActiveTournament();
                }
            } catch (error) {
                console.error('[groups-playoff] initial render failed', error);
            }
        }, 0);
    }

    const api = {
        PATCH_VERSION,
        FORMAT,
        scoreKey,
        isPlayedScore,
        reverseScore,
        parseOrientedScore,
        groupCountForPlayers,
        buildSeedOrder,
        buildSnakeGroups,
        createGroupMatches,
        groupTieKey,
        calculateGroupStandings,
        calculateAllGroupStandings,
        selectDisjointCandidates,
        refreshActiveGroupMatches,
        groupProgress,
        buildPlayoffParticipants,
        updateGroupStageState,
        initializeGroupsPlayoffDraft,
        resetGroupStageDependencies,
        invalidateGroupResolution,
        discardGroupsPlayoffTournament
    };

    if (typeof module !== 'undefined' && module.exports) module.exports = api;
    global.GroupsPlayoff = api;
    installBrowserHooks();
})(typeof window !== 'undefined' ? window : globalThis);
