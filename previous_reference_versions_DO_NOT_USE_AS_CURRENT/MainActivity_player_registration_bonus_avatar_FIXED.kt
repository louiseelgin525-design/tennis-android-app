package com.example.tennistourney

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt
import java.util.Locale
import java.util.Date
import java.text.SimpleDateFormat
import java.net.URLEncoder
import java.net.URLDecoder

// === ЦВЕТА ===
val BgLight = Color(0xFFF2F4F7)
val CardWhite = Color(0xFFFFFFFF)
val TextDark = Color(0xFF1D1D1F)
val TextGray = Color(0xFF86868B)
val AppleBlue = Color(0xFF007AFF)
val BorderGray = Color(0xFFE5E5EA)
val SwipeDeleteRed = Color(0xFFFF3B30)

val GreenBadgeBg = Color(0xFFE4F8EB)
val GreenBadgeText = Color(0xFF24A148)
val BlueBadgeBg = Color(0xFFE3F2FD)
val BlueBadgeText = Color(0xFF007AFF)

val CellWonBg = Color(0xFFE3F2FD)
val CellWonText = Color(0xFF007AFF)
val CellLostBg = Color(0xFFFFEBEE)
val CellLostText = Color(0xFFFF3B30)

// Цвета для подиума и плей-офф
val GoldBg = Color(0xFFFFF8E1)
val GoldBorder = Color(0xFFFFC107)
val SilverBg = Color(0xFFF5F5F7)
val SilverBorder = Color(0xFFC7C7CC)
val BronzeBg = Color(0xFFFFF0E6)
val BronzeBorder = Color(0xFFFF9F0A)


private var _BackIcon: ImageVector? = null
val BackIcon: ImageVector
    get() {
        if (_BackIcon != null) return _BackIcon!!

        _BackIcon = ImageVector.Builder(
            name = "BackIcon",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(TextDark)) {
                moveTo(20f, 11f)
                horizontalLineTo(7.83f)
                lineTo(13.42f, 5.41f)
                lineTo(12f, 4f)
                lineTo(4f, 12f)
                lineTo(12f, 20f)
                lineTo(13.42f, 18.59f)
                lineTo(7.83f, 13f)
                horizontalLineTo(20f)
                verticalLineTo(11f)
                close()
            }
        }.build()

        return _BackIcon!!
    }

private var _SearchIcon: ImageVector? = null
val SearchIcon: ImageVector
    get() {
        if (_SearchIcon != null) return _SearchIcon!!

        _SearchIcon = ImageVector.Builder(
            name = "SearchIcon",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(TextDark)) {
                moveTo(9.5f, 4f)
                curveTo(6.46f, 4f, 4f, 6.46f, 4f, 9.5f)
                curveTo(4f, 12.54f, 6.46f, 15f, 9.5f, 15f)
                curveTo(10.78f, 15f, 11.96f, 14.56f, 12.9f, 13.82f)
                lineTo(18.09f, 19f)
                lineTo(19.5f, 17.59f)
                lineTo(14.32f, 12.4f)
                curveTo(14.85f, 11.55f, 15f, 10.55f, 15f, 9.5f)
                curveTo(15f, 6.46f, 12.54f, 4f, 9.5f, 4f)
                close()
                moveTo(9.5f, 6f)
                curveTo(11.43f, 6f, 13f, 7.57f, 13f, 9.5f)
                curveTo(13f, 11.43f, 11.43f, 13f, 9.5f, 13f)
                curveTo(7.57f, 13f, 6f, 11.43f, 6f, 9.5f)
                curveTo(6f, 7.57f, 7.57f, 6f, 9.5f, 6f)
                close()
            }
        }.build()

        return _SearchIcon!!
    }

private var _PlayersIcon: ImageVector? = null
val PlayersIcon: ImageVector
    get() {
        if (_PlayersIcon != null) return _PlayersIcon!!

        _PlayersIcon = ImageVector.Builder(
            name = "PlayersIcon",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(AppleBlue)) {
                moveTo(9f, 11f)
                curveTo(11.21f, 11f, 13f, 9.21f, 13f, 7f)
                curveTo(13f, 4.79f, 11.21f, 3f, 9f, 3f)
                curveTo(6.79f, 3f, 5f, 4.79f, 5f, 7f)
                curveTo(5f, 9.21f, 6.79f, 11f, 9f, 11f)
                close()
                moveTo(9f, 13f)
                curveTo(6.33f, 13f, 1f, 14.34f, 1f, 17f)
                verticalLineTo(19f)
                horizontalLineTo(17f)
                verticalLineTo(17f)
                curveTo(17f, 14.34f, 11.67f, 13f, 9f, 13f)
                close()
                moveTo(17.5f, 11f)
                curveTo(19.43f, 11f, 21f, 9.43f, 21f, 7.5f)
                curveTo(21f, 5.57f, 19.43f, 4f, 17.5f, 4f)
                curveTo(16.83f, 4f, 16.21f, 4.19f, 15.68f, 4.52f)
                curveTo(16.5f, 5.58f, 17f, 6.9f, 17f, 8.33f)
                curveTo(17f, 9.23f, 16.82f, 10.09f, 16.49f, 10.86f)
                curveTo(16.81f, 10.95f, 17.15f, 11f, 17.5f, 11f)
                close()
                moveTo(17.85f, 13.02f)
                curveTo(18.95f, 13.73f, 20f, 14.88f, 20f, 17f)
                verticalLineTo(19f)
                horizontalLineTo(23f)
                verticalLineTo(17f)
                curveTo(23f, 14.86f, 20.54f, 13.58f, 17.85f, 13.02f)
                close()
            }
        }.build()

        return _PlayersIcon!!
    }

private var _TrashIcon: ImageVector? = null
val TrashIcon: ImageVector
    get() {
        if (_TrashIcon != null) return _TrashIcon!!

        _TrashIcon = ImageVector.Builder(
            name = "TrashIcon",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(SwipeDeleteRed)) {
                moveTo(6f, 19f)
                curveTo(6f, 20.1f, 6.9f, 21f, 8f, 21f)
                horizontalLineTo(16f)
                curveTo(17.1f, 21f, 18f, 20.1f, 18f, 19f)
                verticalLineTo(7f)
                horizontalLineTo(6f)
                verticalLineTo(19f)
                close()
                moveTo(8f, 9f)
                horizontalLineTo(16f)
                verticalLineTo(19f)
                horizontalLineTo(8f)
                verticalLineTo(9f)
                close()
                moveTo(15.5f, 4f)
                lineTo(14.5f, 3f)
                horizontalLineTo(9.5f)
                lineTo(8.5f, 4f)
                horizontalLineTo(5f)
                verticalLineTo(6f)
                horizontalLineTo(19f)
                verticalLineTo(4f)
                horizontalLineTo(15.5f)
                close()
            }
        }.build()

        return _TrashIcon!!
    }


private var _HistoryIcon: ImageVector? = null
val HistoryIcon: ImageVector
    get() {
        if (_HistoryIcon != null) return _HistoryIcon!!

        _HistoryIcon = ImageVector.Builder(
            name = "HistoryIcon",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(AppleBlue)) {
                moveTo(7f, 2f)
                horizontalLineTo(9f)
                verticalLineTo(4f)
                horizontalLineTo(15f)
                verticalLineTo(2f)
                horizontalLineTo(17f)
                verticalLineTo(4f)
                horizontalLineTo(19f)
                curveTo(20.1f, 4f, 21f, 4.9f, 21f, 6f)
                verticalLineTo(20f)
                curveTo(21f, 21.1f, 20.1f, 22f, 19f, 22f)
                horizontalLineTo(5f)
                curveTo(3.9f, 22f, 3f, 21.1f, 3f, 20f)
                verticalLineTo(6f)
                curveTo(3f, 4.9f, 3.9f, 4f, 5f, 4f)
                horizontalLineTo(7f)
                verticalLineTo(2f)
                close()
                moveTo(5f, 9f)
                verticalLineTo(20f)
                horizontalLineTo(19f)
                verticalLineTo(9f)
                horizontalLineTo(5f)
                close()
                moveTo(7f, 12f)
                horizontalLineTo(17f)
                verticalLineTo(14f)
                horizontalLineTo(7f)
                verticalLineTo(12f)
                close()
                moveTo(7f, 16f)
                horizontalLineTo(14f)
                verticalLineTo(18f)
                horizontalLineTo(7f)
                verticalLineTo(16f)
                close()
            }
        }.build()

        return _HistoryIcon!!
    }

@Composable
fun AppBackButton(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(CardWhite)
            .border(1.dp, BorderGray, RoundedCornerShape(14.dp))
    ) {
        Icon(
            imageVector = BackIcon,
            contentDescription = "Назад",
            tint = TextDark,
            modifier = Modifier.size(22.dp)
        )
    }
}

val AppTypography = Typography(
    displayLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 32.sp, letterSpacing = (-0.5).sp),
    titleLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 18.sp),
    bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 15.sp),
    labelLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 16.sp)
)

data class PlayerField(val id: Int, var name: String)
data class ClubPlayer(
    val id: Int,
    val fullName: String,
    val rating: Double = 100.0,
    val tournamentsPlayed: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val goldMedals: Int = 0,
    val silverMedals: Int = 0,
    val bronzeMedals: Int = 0,
    val avatarUri: String = "",
    val opponentStats: String = ""
)
data class RatingChange(val fullName: String, val oldRating: Double, val newRating: Double, val tournamentsBefore: Int, val tournamentsAfter: Int, val placeBonus: Double = 0.0)
data class RatingApplyResult(val players: List<ClubPlayer>, val changes: List<RatingChange>)
data class TournamentHistoryEntry(
    val id: Long,
    val name: String,
    val dateText: String,
    val playersCount: Int,
    val winnerName: String,
    val standingsText: String,
    val ratingText: String
)

data class PlayerStat(val index: Int, val name: String, val points: Int)
data class PlayoffMatch(val id: Int, val p1: String, val p2: String, val isFinal: Boolean = false)
data class ActiveTableMatch(val tableNumber: Int, val player1: Int, val player2: Int)
data class TieStat(val playerIndex: Int, val miniPoints: Int, val setRatio: Double)

private const val PLAYERS_PREFS_NAME = "club_players_storage"
private const val PLAYERS_PREFS_KEY = "players"
private const val HISTORY_PREFS_NAME = "tournament_history_storage"
private const val HISTORY_PREFS_KEY = "history"
private const val HISTORY_ENTRY_SEPARATOR = "\u001E"
private const val HISTORY_FIELD_SEPARATOR = "\u001F"


private fun normalizePlayerName(rawName: String): String =
    rawName
        .trim()
        .replace(Regex("\\s+"), " ")

private fun roundRating(value: Double): Double =
    kotlin.math.round(value * 10.0) / 10.0

private fun formatRatingForStorage(value: Double): String =
    "%.1f".format(Locale.US, roundRating(value))

private fun formatRating(value: Double): String {
    val rounded = roundRating(value)
    return if (rounded % 1.0 == 0.0) {
        rounded.toInt().toString()
    } else {
        "%.1f".format(Locale.US, rounded)
    }
}

private fun formatDelta(value: Double): String {
    val rounded = roundRating(value)
    val sign = if (rounded > 0) "+" else ""
    return sign + formatRating(rounded)
}

private fun safeDecode(value: String): String =
    try {
        URLDecoder.decode(value, "UTF-8")
    } catch (_: Exception) {
        value
    }

private fun safeEncode(value: String): String =
    try {
        URLEncoder.encode(value, "UTF-8")
    } catch (_: Exception) {
        value
    }

private fun opponentKey(name: String): String =
    normalizePlayerName(name).lowercase()

private fun decodeOpponentStats(raw: String): MutableMap<String, Pair<Int, Int>> {
    if (raw.isBlank()) return mutableMapOf()

    return safeDecode(raw)
        .split(";;")
        .mapNotNull { row ->
            val parts = row.split("::")
            if (parts.size != 3) return@mapNotNull null

            val wins = parts[1].toIntOrNull() ?: 0
            val losses = parts[2].toIntOrNull() ?: 0
            parts[0] to (wins to losses)
        }
        .toMap()
        .toMutableMap()
}

private fun encodeOpponentStats(stats: Map<String, Pair<Int, Int>>): String =
    safeEncode(
        stats
            .toList()
            .sortedByDescending { (_, value) -> value.first + value.second }
            .joinToString(";;") { (name, value) ->
                "$name::${value.first}::${value.second}"
            }
    )

private fun updateOpponentStat(
    raw: String,
    opponentName: String,
    won: Boolean
): String {
    val stats = decodeOpponentStats(raw)
    val key = normalizePlayerName(opponentName)
    val current = stats[key] ?: (0 to 0)

    stats[key] = if (won) {
        current.first + 1 to current.second
    } else {
        current.first to current.second + 1
    }

    return encodeOpponentStats(stats)
}

private fun saveBitmapAvatar(
    context: Context,
    bitmap: Bitmap,
    playerId: Int
): String {
    val dir = File(context.filesDir, "avatars")
    if (!dir.exists()) dir.mkdirs()

    val file = File(dir, "player_${playerId}_${System.currentTimeMillis()}.jpg")
    FileOutputStream(file).use { stream ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 88, stream)
    }

    return file.absolutePath
}

@Composable
private fun rememberAvatarBitmap(uriOrPath: String): Bitmap? {
    val context = LocalContext.current

    return remember(uriOrPath) {
        if (uriOrPath.isBlank()) {
            null
        } else {
            try {
                if (uriOrPath.startsWith("/") || uriOrPath.startsWith("file:")) {
                    BitmapFactory.decodeFile(uriOrPath.removePrefix("file://"))
                } else {
                    context.contentResolver.openInputStream(Uri.parse(uriOrPath)).use { input ->
                        BitmapFactory.decodeStream(input)
                    }
                }
            } catch (_: Exception) {
                null
            }
        }
    }
}

private fun loadClubPlayers(context: Context): List<ClubPlayer> {
    val saved = context
        .getSharedPreferences(PLAYERS_PREFS_NAME, Context.MODE_PRIVATE)
        .getString(PLAYERS_PREFS_KEY, "")
        .orEmpty()

    if (saved.isBlank()) return emptyList()

    return saved
        .split(";;")
        .mapNotNull { row ->
            val parts = row.split("|")
            if (parts.size < 3) return@mapNotNull null

            val id = parts[0].toIntOrNull() ?: return@mapNotNull null
            val fullName = safeDecode(parts[1]).trim()
            val rating = parts[2].replace(",", ".").toDoubleOrNull() ?: 100.0
            val tournamentsPlayed = parts.getOrNull(3)?.toIntOrNull() ?: 0
            val wins = parts.getOrNull(4)?.toIntOrNull() ?: 0
            val losses = parts.getOrNull(5)?.toIntOrNull() ?: 0
            val gold = parts.getOrNull(6)?.toIntOrNull() ?: 0
            val silver = parts.getOrNull(7)?.toIntOrNull() ?: 0
            val bronze = parts.getOrNull(8)?.toIntOrNull() ?: 0
            val avatarUri = parts.getOrNull(9)?.let(::safeDecode).orEmpty()
            val opponentStats = parts.getOrNull(10).orEmpty()

            if (fullName.isBlank()) {
                null
            } else {
                ClubPlayer(
                    id = id,
                    fullName = fullName,
                    rating = rating,
                    tournamentsPlayed = tournamentsPlayed,
                    wins = wins,
                    losses = losses,
                    goldMedals = gold,
                    silverMedals = silver,
                    bronzeMedals = bronze,
                    avatarUri = avatarUri,
                    opponentStats = opponentStats
                )
            }
        }
        .sortedBy { it.fullName.lowercase() }
}

private fun saveClubPlayers(
    context: Context,
    players: List<ClubPlayer>
) {
    val encoded = players
        .distinctBy { it.fullName.lowercase() }
        .sortedBy { it.fullName.lowercase() }
        .joinToString(";;") { player ->
            listOf(
                player.id.toString(),
                safeEncode(player.fullName),
                formatRatingForStorage(player.rating),
                player.tournamentsPlayed.toString(),
                player.wins.toString(),
                player.losses.toString(),
                player.goldMedals.toString(),
                player.silverMedals.toString(),
                player.bronzeMedals.toString(),
                safeEncode(player.avatarUri),
                player.opponentStats
            ).joinToString("|")
        }

    context
        .getSharedPreferences(PLAYERS_PREFS_NAME, Context.MODE_PRIVATE)
        .edit()
        .putString(PLAYERS_PREFS_KEY, encoded)
        .apply()
}

private fun rememberPlayersFromTournament(
    context: Context,
    existingPlayers: List<ClubPlayer>,
    playerNames: List<String>
): List<ClubPlayer> {
    val normalizedExisting = existingPlayers
        .associateBy { it.fullName.lowercase() }
        .toMutableMap()

    var nextId = (existingPlayers.maxOfOrNull { it.id } ?: 0) + 1

    playerNames
        .map(::normalizePlayerName)
        .filter { it.isNotBlank() }
        .forEach { fullName ->
            val key = fullName.lowercase()

            if (!normalizedExisting.containsKey(key)) {
                normalizedExisting[key] = ClubPlayer(
                    id = nextId++,
                    fullName = fullName,
                    rating = 100.0,
                    tournamentsPlayed = 0,
                    wins = 0,
                    losses = 0
                )
            }
        }

    return normalizedExisting
        .values
        .sortedBy { it.fullName.lowercase() }
}

private fun playerSuggestions(
    query: String,
    clubPlayers: List<ClubPlayer>,
    limit: Int = 5
): List<ClubPlayer> {
    val cleanQuery = normalizePlayerName(query).lowercase()

    if (cleanQuery.length < 3) return emptyList()

    return clubPlayers
        .filter { player ->
            player.fullName.lowercase().contains(cleanQuery)
        }
        .sortedWith(
            compareBy<ClubPlayer> {
                !it.fullName.lowercase().startsWith(cleanQuery)
            }.thenBy { it.fullName.lowercase() }
        )
        .take(limit)
}

private fun encodeHistoryField(value: String): String =
    URLEncoder.encode(value, "UTF-8")

private fun decodeHistoryField(value: String): String =
    URLDecoder.decode(value, "UTF-8")

private fun loadTournamentHistory(context: Context): List<TournamentHistoryEntry> {
    val saved = context
        .getSharedPreferences(HISTORY_PREFS_NAME, Context.MODE_PRIVATE)
        .getString(HISTORY_PREFS_KEY, "")
        .orEmpty()

    if (saved.isBlank()) return emptyList()

    return saved
        .split(HISTORY_ENTRY_SEPARATOR)
        .mapNotNull { row ->
            val parts = row.split(HISTORY_FIELD_SEPARATOR)
            if (parts.size < 7) return@mapNotNull null

            val id = parts[0].toLongOrNull() ?: return@mapNotNull null
            val playersCount = parts[3].toIntOrNull() ?: 0

            TournamentHistoryEntry(
                id = id,
                name = decodeHistoryField(parts[1]),
                dateText = decodeHistoryField(parts[2]),
                playersCount = playersCount,
                winnerName = decodeHistoryField(parts[4]),
                standingsText = decodeHistoryField(parts[5]),
                ratingText = decodeHistoryField(parts[6])
            )
        }
        .sortedByDescending { it.id }
}

private fun saveTournamentHistory(
    context: Context,
    history: List<TournamentHistoryEntry>
) {
    val encoded = history
        .sortedByDescending { it.id }
        .take(50)
        .joinToString(HISTORY_ENTRY_SEPARATOR) { entry ->
            listOf(
                entry.id.toString(),
                encodeHistoryField(entry.name),
                encodeHistoryField(entry.dateText),
                entry.playersCount.toString(),
                encodeHistoryField(entry.winnerName),
                encodeHistoryField(entry.standingsText),
                encodeHistoryField(entry.ratingText)
            ).joinToString(HISTORY_FIELD_SEPARATOR)
        }

    context
        .getSharedPreferences(HISTORY_PREFS_NAME, Context.MODE_PRIVATE)
        .edit()
        .putString(HISTORY_PREFS_KEY, encoded)
        .apply()
}

private fun createRoundRobinHistoryEntry(draft: TournamentDraft): TournamentHistoryEntry {
    val playerNames = draft.playerFields.mapIndexed { index, field ->
        normalizePlayerName(field.name).ifBlank { "Игрок ${index + 1}" }
    }

    val standings = calculateRoundRobinStandings(
        playerCount = playerNames.size,
        scores = draft.matchScores
    )

    val standingsText = standings
        .mapIndexed { place, stat ->
            "${place + 1}. ${playerNames.getOrElse(stat.index) { "Игрок" }} — ${stat.points} очк."
        }
        .joinToString("\n")

    val winnerName = standings
        .firstOrNull()
        ?.let { stat -> playerNames.getOrElse(stat.index) { "—" } }
        ?: "—"

    val ratingText = if (draft.ratingChanges.isEmpty()) {
        "Рейтинг не изменился"
    } else {
        draft.ratingChanges.joinToString("\n") { change ->
            val delta = change.newRating - change.oldRating
            "${change.fullName}: ${formatRating(change.oldRating)} → ${formatRating(change.newRating)} (${formatDelta(delta)})"
        }
    }

    val dateText = SimpleDateFormat(
        "dd.MM.yyyy HH:mm",
        Locale.getDefault()
    ).format(Date())

    return TournamentHistoryEntry(
        id = System.currentTimeMillis(),
        name = draft.name.ifBlank { "Теннисный турнир" },
        dateText = dateText,
        playersCount = playerNames.size,
        winnerName = winnerName,
        standingsText = standingsText,
        ratingText = ratingText
    )
}

private fun scoreDominanceCoefficient(score: Pair<Int, Int>): Double {
    val setDifference = kotlin.math.abs(score.first - score.second)

    return when {
        setDifference <= 1 -> 0.8
        setDifference == 2 -> 1.0
        else -> 1.2
    }
}

private fun rttfTournamentCoefficient(averageRating: Double): Double =
    when {
        averageRating < 250.0 -> 0.20
        averageRating < 350.0 -> 0.25
        averageRating < 450.0 -> 0.30
        averageRating < 550.0 -> 0.35
        else -> 0.40
    }

private fun rttfBaseDelta(
    winnerRating: Double,
    loserRating: Double
): Double {
    val raw = (100.0 - (winnerRating - loserRating)) / 10.0
    return raw.coerceAtLeast(0.0)
}

private fun calculateRttfPlayerDelta(
    isWinnerSide: Boolean,
    isNovice: Boolean,
    baseDelta: Double,
    tournamentCoefficient: Double,
    scoreCoefficient: Double
): Double {
    val result = if (isNovice) {
        if (isWinnerSide) {
            baseDelta * 1.0 * 1.0
        } else {
            baseDelta * 0.5 * 1.0
        }
    } else {
        baseDelta * tournamentCoefficient * scoreCoefficient
    }

    return roundRating(result)
}

private fun applyRoundRobinRatings(
    existingPlayers: List<ClubPlayer>,
    playerNames: List<String>,
    scores: Map<Pair<Int, Int>, String>,
    tournamentCoefficient: Double,
    applyPlaceBonuses: Boolean = false
): RatingApplyResult {
    val allPlayers = rememberPlayersFromTournament(
        context = FakeContextForPureRating(),
        existingPlayers = existingPlayers,
        playerNames = playerNames
    )

    val playersByName = allPlayers
        .associateBy { it.fullName.lowercase() }
        .toMutableMap()

    val tournamentPlayerNames = playerNames
        .map(::normalizePlayerName)
        .filter { it.isNotBlank() }

    val tournamentPlayers = tournamentPlayerNames
        .mapNotNull { fullName -> playersByName[fullName.lowercase()] }

    val averageRating = if (tournamentPlayers.isEmpty()) {
        100.0
    } else {
        tournamentPlayers.map { it.rating }.average()
    }

    val rttfK = rttfTournamentCoefficient(averageRating)

    val initialRatings = tournamentPlayers.associate { player ->
        player.fullName.lowercase() to player.rating
    }

    val initialTournamentCounts = tournamentPlayers.associate { player ->
        player.fullName.lowercase() to player.tournamentsPlayed
    }

    val accumulatedDeltas = tournamentPlayers
        .associate { it.fullName.lowercase() to 0.0 }
        .toMutableMap()

    val winIncrements = tournamentPlayers
        .associate { it.fullName.lowercase() to 0 }
        .toMutableMap()

    val lossIncrements = tournamentPlayers
        .associate { it.fullName.lowercase() to 0 }
        .toMutableMap()

    val opponentStatsUpdated = tournamentPlayers
        .associate { it.fullName.lowercase() to it.opponentStats }
        .toMutableMap()

    for (first in playerNames.indices) {
        for (second in first + 1 until playerNames.size) {
            val result = parseScore(scores[first to second]) ?: continue
            if (result.first == result.second) continue

            val firstName = normalizePlayerName(playerNames[first])
            val secondName = normalizePlayerName(playerNames[second])

            val firstPlayer = playersByName[firstName.lowercase()] ?: continue
            val secondPlayer = playersByName[secondName.lowercase()] ?: continue

            val firstWon = result.first > result.second
            val winner = if (firstWon) firstPlayer else secondPlayer
            val loser = if (firstWon) secondPlayer else firstPlayer

            val winnerName = winner.fullName
            val loserName = loser.fullName

            winIncrements[winnerName.lowercase()] =
                (winIncrements[winnerName.lowercase()] ?: 0) + 1
            lossIncrements[loserName.lowercase()] =
                (lossIncrements[loserName.lowercase()] ?: 0) + 1

            opponentStatsUpdated[winnerName.lowercase()] = updateOpponentStat(
                raw = opponentStatsUpdated[winnerName.lowercase()].orEmpty(),
                opponentName = loserName,
                won = true
            )

            opponentStatsUpdated[loserName.lowercase()] = updateOpponentStat(
                raw = opponentStatsUpdated[loserName.lowercase()].orEmpty(),
                opponentName = winnerName,
                won = false
            )

            val baseDelta = rttfBaseDelta(
                winnerRating = winner.rating,
                loserRating = loser.rating
            )

            if (baseDelta <= 0.0) {
                continue
            }

            val scoreCoefficient = scoreDominanceCoefficient(result)

            val winnerIsNovice = winner.tournamentsPlayed <= 5
            val loserIsNovice = loser.tournamentsPlayed <= 5

            val winnerDelta = calculateRttfPlayerDelta(
                isWinnerSide = true,
                isNovice = winnerIsNovice,
                baseDelta = baseDelta,
                tournamentCoefficient = rttfK,
                scoreCoefficient = scoreCoefficient
            )

            val loserDelta = calculateRttfPlayerDelta(
                isWinnerSide = false,
                isNovice = loserIsNovice,
                baseDelta = baseDelta,
                tournamentCoefficient = rttfK,
                scoreCoefficient = scoreCoefficient
            )

            accumulatedDeltas[winner.fullName.lowercase()] =
                (accumulatedDeltas[winner.fullName.lowercase()] ?: 0.0) + winnerDelta

            accumulatedDeltas[loser.fullName.lowercase()] =
                (accumulatedDeltas[loser.fullName.lowercase()] ?: 0.0) - loserDelta
        }
    }

    val standings = calculateRoundRobinStandings(
        playerCount = playerNames.size,
        scores = scores
    )

    val medalByName = mutableMapOf<String, Int>()
    standings.take(3).forEachIndexed { place, stat ->
        val fullName = normalizePlayerName(playerNames.getOrElse(stat.index) { "" })
        if (fullName.isNotBlank()) {
            medalByName[fullName.lowercase()] = place + 1
        }
    }

    val placeBonuses = mutableMapOf<String, Double>()
    if (applyPlaceBonuses) {
        standings.take(3).forEachIndexed { place, stat ->
            val fullName = normalizePlayerName(playerNames.getOrElse(stat.index) { "" })
            val bonus = when (place) {
                0 -> 3.0
                1 -> 2.0
                2 -> 1.0
                else -> 0.0
            }

            if (fullName.isNotBlank() && bonus > 0.0) {
                placeBonuses[fullName.lowercase()] = bonus
                accumulatedDeltas[fullName.lowercase()] =
                    (accumulatedDeltas[fullName.lowercase()] ?: 0.0) + bonus
            }
        }
    }

    val tournamentKeys = tournamentPlayerNames
        .map { it.lowercase() }
        .toSet()

    val updatedPlayers = playersByName.values.map { player ->
        val key = player.fullName.lowercase()
        val delta = accumulatedDeltas[key] ?: 0.0
        val medalPlace = medalByName[key]

        if (key in tournamentKeys) {
            player.copy(
                rating = roundRating((player.rating + delta).coerceAtLeast(1.0)),
                tournamentsPlayed = player.tournamentsPlayed + 1,
                wins = player.wins + (winIncrements[key] ?: 0),
                losses = player.losses + (lossIncrements[key] ?: 0),
                goldMedals = player.goldMedals + if (medalPlace == 1) 1 else 0,
                silverMedals = player.silverMedals + if (medalPlace == 2) 1 else 0,
                bronzeMedals = player.bronzeMedals + if (medalPlace == 3) 1 else 0,
                opponentStats = opponentStatsUpdated[key] ?: player.opponentStats
            )
        } else {
            player
        }
    }.sortedBy { it.fullName.lowercase() }

    val updatedByName = updatedPlayers.associateBy { it.fullName.lowercase() }

    val changes = tournamentPlayerNames
        .distinctBy { it.lowercase() }
        .mapNotNull { fullName ->
            val key = fullName.lowercase()
            val oldRating = initialRatings[key] ?: return@mapNotNull null
            val newPlayer = updatedByName[key] ?: return@mapNotNull null

            RatingChange(
                fullName = fullName,
                oldRating = oldRating,
                newRating = newPlayer.rating,
                tournamentsBefore = initialTournamentCounts[key] ?: 0,
                tournamentsAfter = newPlayer.tournamentsPlayed,
                placeBonus = placeBonuses[key] ?: 0.0
            )
        }
        .sortedByDescending { it.newRating - it.oldRating }

    return RatingApplyResult(
        players = updatedPlayers,
        changes = changes
    )
}

/**
 * Заглушка нужна только чтобы переиспользовать функцию rememberPlayersFromTournament
 * без сохранения в память. Внутри функция context не использует.
 */
private class FakeContextForPureRating : ContextWrapper(null)


class TournamentDraft {
    var name by mutableStateOf("")
    var playersCount by mutableStateOf(10)
    var bestOf3 by mutableStateOf(false)
    var tablesCount by mutableStateOf(3)
    var ratingCoefficient by mutableStateOf(1.0)
    var isListGenerated by mutableStateOf(false)
    var nextFieldId by mutableStateOf(0)
    val playerFields = mutableStateListOf<PlayerField>()

    // Состояние Кругового турнира
    val matchScores = mutableStateMapOf<Pair<Int, Int>, String>()
    val activeRoundRobinMatches = mutableStateListOf<Pair<Int, Int>>()
    val withdrawnPlayers = mutableStateListOf<Int>()

    // Изменение рейтинга после завершения турнира
    var ratingApplied by mutableStateOf(false)
    var historySaved by mutableStateOf(false)
    val ratingChanges = mutableStateListOf<RatingChange>()

    // Состояние Сетки Плей-офф
    val playoffScores = mutableStateMapOf<Int, String>()
}

private fun tournamentStartError(draft: TournamentDraft): String? {
    if (normalizePlayerName(draft.name).isBlank()) {
        return "Введите название турнира."
    }

    if (!draft.isListGenerated || draft.playerFields.size < 2) {
        return "Сначала сгенерируйте список игроков."
    }

    val playerNames = draft.playerFields.map { normalizePlayerName(it.name) }

    if (playerNames.any { it.isBlank() }) {
        return "Заполните ФИО всех игроков."
    }

    val duplicates = playerNames
        .groupBy { it.lowercase() }
        .filter { it.value.size > 1 }
        .values
        .flatten()
        .distinct()

    if (duplicates.isNotEmpty()) {
        return "Один игрок добавлен дважды: ${duplicates.first()}."
    }

    val maximumTables = (draft.playerFields.size / 2).coerceIn(1, 5)
    if (draft.tablesCount > maximumTables) {
        return "Для ${draft.playerFields.size} игроков максимум столов: $maximumTables."
    }

    return null
}

private fun playerMatchLines(
    playerIndex: Int,
    playerNames: List<String>,
    scores: Map<Pair<Int, Int>, String>
): List<String> {
    return playerNames.indices
        .filter { opponent -> opponent != playerIndex }
        .map { opponent ->
            val score = scores[playerIndex to opponent] ?: "—"
            "${playerNames.getOrElse(opponent) { "Игрок" }}: $score"
        }
}

enum class AppScreen { Dashboard, PlayerBase, History, CreateTournament, RoundRobin, Playoff, FinalResults }

/**
 * До запуска турнира приложение работает в портретном режиме.
 * После запуска турнира игровые экраны и экран итогов работают в landscape.
 *
 * В AndroidManifest.xml у MainActivity нужно убрать screenOrientation="landscape"
 * и добавить configChanges="orientation|screenSize|keyboardHidden",
 * чтобы при повороте не сбрасывался текущий экран и черновик.
 */
@Composable
fun AppOrientationEffect(currentScreen: AppScreen) {
    val activity = LocalContext.current as? Activity

    val requestedOrientation = when (currentScreen) {
        AppScreen.Dashboard,
        AppScreen.PlayerBase,
        AppScreen.History,
        AppScreen.CreateTournament -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        AppScreen.RoundRobin,
        AppScreen.Playoff,
        AppScreen.FinalResults -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    DisposableEffect(activity, requestedOrientation) {
        activity?.requestedOrientation = requestedOrientation
        onDispose {
            // Следующий экран сам установит нужную ориентацию.
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(typography = AppTypography) {
                Surface(modifier = Modifier.fillMaxSize(), color = BgLight) {
                    TennisApp()
                }
            }
        }
    }
}

@Composable
fun TennisApp() {
    var currentScreen by remember { mutableStateOf(AppScreen.Dashboard) }
    val draft = remember { TournamentDraft() }
    val context = LocalContext.current
    var clubPlayers by remember { mutableStateOf<List<ClubPlayer>>(emptyList()) }
    var tournamentHistory by remember { mutableStateOf<List<TournamentHistoryEntry>>(emptyList()) }

    LaunchedEffect(Unit) {
        clubPlayers = loadClubPlayers(context)
        tournamentHistory = loadTournamentHistory(context)
    }

    fun savePlayersFromCurrentTournament() {
        val updatedPlayers = rememberPlayersFromTournament(
            context = context,
            existingPlayers = clubPlayers,
            playerNames = draft.playerFields.map { it.name }
        )

        saveClubPlayers(context, updatedPlayers)
        clubPlayers = updatedPlayers
    }

    fun applyRatingFromCurrentTournament(applyPlaceBonuses: Boolean) {
        if (draft.ratingApplied) return

        val playerNames = draft.playerFields.map { it.name }

        val result = applyRoundRobinRatings(
            existingPlayers = clubPlayers,
            playerNames = playerNames,
            scores = draft.matchScores,
            tournamentCoefficient = draft.ratingCoefficient,
            applyPlaceBonuses = applyPlaceBonuses
        )

        saveClubPlayers(context, result.players)
        clubPlayers = result.players

        draft.ratingChanges.clear()
        draft.ratingChanges.addAll(result.changes)
        draft.ratingApplied = true
    }

    fun saveHistoryFromCurrentTournament() {
        if (draft.historySaved) return

        val entry = createRoundRobinHistoryEntry(draft)
        val updatedHistory = listOf(entry) + tournamentHistory

        saveTournamentHistory(context, updatedHistory)
        tournamentHistory = updatedHistory
        draft.historySaved = true
    }

    AppOrientationEffect(currentScreen)

    MaterialTheme(typography = AppTypography) {
        when (currentScreen) {
            AppScreen.Dashboard -> MainDashboardScreen(
                draft = draft,
                clubPlayers = clubPlayers,
                tournamentHistory = tournamentHistory,
                onNavigateToCreate = { currentScreen = AppScreen.CreateTournament },
                onNavigateToPlayerBase = { currentScreen = AppScreen.PlayerBase },
                onNavigateToHistory = { currentScreen = AppScreen.History }
            )
            AppScreen.PlayerBase -> PlayerBaseScreen(
                clubPlayers = clubPlayers,
                onBack = { currentScreen = AppScreen.Dashboard },
                onPlayersChanged = { updatedPlayers ->
                    saveClubPlayers(context, updatedPlayers)
                    clubPlayers = updatedPlayers
                }
            )
            AppScreen.History -> TournamentHistoryScreen(
                history = tournamentHistory,
                onBack = { currentScreen = AppScreen.Dashboard },
                onHistoryChanged = { updatedHistory ->
                    saveTournamentHistory(context, updatedHistory)
                    tournamentHistory = updatedHistory
                }
            )
            AppScreen.CreateTournament -> CreateTournamentScreen(
                draft = draft,
                clubPlayers = clubPlayers,
                onBack = { currentScreen = AppScreen.Dashboard },
                onStartTournament = {
                    draft.ratingApplied = false
                    draft.historySaved = false
                    draft.ratingChanges.clear()
                    savePlayersFromCurrentTournament()
                    currentScreen = if (draft.playerFields.size <= 10) AppScreen.RoundRobin else AppScreen.Playoff
                }
            )
            AppScreen.RoundRobin -> RoundRobinScreen(
                draft = draft,
                onBack = { currentScreen = AppScreen.CreateTournament },
                onFinish = { applyPlaceBonuses ->
                    savePlayersFromCurrentTournament()
                    applyRatingFromCurrentTournament(applyPlaceBonuses)
                    saveHistoryFromCurrentTournament()
                    currentScreen = AppScreen.FinalResults
                }
            )
            AppScreen.Playoff -> PlayoffScreen(draft, { currentScreen = AppScreen.CreateTournament })
            AppScreen.FinalResults -> FinalResultsScreen(draft, {
                draft.isListGenerated = false
                draft.name = ""
                draft.playerFields.clear()
                draft.matchScores.clear()
                draft.activeRoundRobinMatches.clear()
                draft.withdrawnPlayers.clear()
                draft.ratingApplied = false
                draft.historySaved = false
                draft.ratingChanges.clear()
                draft.playoffScores.clear()
                currentScreen = AppScreen.Dashboard
            })
        }
    }
}

// ==========================================
// 1. ГЛАВНЫЙ ЭКРАН (DASHBOARD)
// ==========================================
@Composable
fun MainDashboardScreen(
    draft: TournamentDraft,
    clubPlayers: List<ClubPlayer>,
    tournamentHistory: List<TournamentHistoryEntry>,
    onNavigateToCreate: () -> Unit,
    onNavigateToPlayerBase: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val hasDraft = draft.isListGenerated || draft.name.isNotBlank()
    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Row(modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Турниры", style = AppTypography.displayLarge, color = TextDark)
                Icon(
                    imageVector = SearchIcon,
                    contentDescription = "Поиск",
                    tint = TextDark,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(CardWhite)
                        .border(1.dp, BorderGray, RoundedCornerShape(14.dp))
                        .padding(10.dp)
                )
            }
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = CardWhite), shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, BorderGray), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(50.dp).clip(RoundedCornerShape(12.dp)).background(AppleBlue), contentAlignment = Alignment.Center) {
                            Text(if (hasDraft) "📝" else "🏓", fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(if (hasDraft) "Черновик турнира" else "Новый турнир", style = AppTypography.titleLarge, color = TextDark)
                            Text(if (hasDraft) "Продолжите настройку" else "Создайте турнир", style = AppTypography.bodyMedium, color = TextGray)
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    OutlinedTextField(
                        value = draft.name, onValueChange = { draft.name = it },
                        placeholder = { Text("Введите название турнира", color = TextGray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = BorderGray, focusedBorderColor = AppleBlue, unfocusedContainerColor = CardWhite, focusedContainerColor = CardWhite),
                        shape = RoundedCornerShape(12.dp), singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onNavigateToCreate, colors = ButtonDefaults.buttonColors(containerColor = AppleBlue), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().height(54.dp)) {
                        Text(if (hasDraft) "✏️ Продолжить создание" else "🏆 Создать турнир", style = AppTypography.labelLarge, color = Color.White)
                    }
                }
            }
        }
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, BorderGray),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToPlayerBase() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(BlueBadgeBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = PlayersIcon,
                            contentDescription = "База игроков",
                            tint = AppleBlue,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text("База игроков", style = AppTypography.titleLarge, color = TextDark)
                        Text(
                            if (clubPlayers.isEmpty()) {
                                "Пока нет сохранённых игроков"
                            } else {
                                "${clubPlayers.size} игроков • рейтинг обновляется после турнира"
                            },
                            style = AppTypography.bodyMedium,
                            color = TextGray
                        )
                    }

                    Text("›", fontSize = 30.sp, color = TextGray)
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, BorderGray),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToHistory() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(BlueBadgeBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = HistoryIcon,
                            contentDescription = "История турниров",
                            tint = AppleBlue,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text("История турниров", style = AppTypography.titleLarge, color = TextDark)
                        Text(
                            if (tournamentHistory.isEmpty()) {
                                "Завершённые турниры появятся здесь"
                            } else {
                                "${tournamentHistory.size} турниров сохранено"
                            },
                            style = AppTypography.bodyMedium,
                            color = TextGray
                        )
                    }

                    Text("›", fontSize = 30.sp, color = TextGray)
                }
            }
        }

        if (tournamentHistory.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Последний турнир", style = AppTypography.titleLarge, color = TextDark)
                    Text("Все ›", style = AppTypography.bodyMedium, color = AppleBlue)
                }
            }

            item {
                val lastTournament = tournamentHistory.first()
                HistoryCardLight(
                    name = lastTournament.name,
                    date = lastTournament.dateText,
                    players = lastTournament.playersCount.toString(),
                    winner = lastTournament.winnerName,
                    isFinished = true,
                    icon = "🏆",
                    onClick = onNavigateToHistory
                )
            }
        }
    }
}

@Composable
fun PlayerBaseScreen(
    clubPlayers: List<ClubPlayer>,
    onBack: () -> Unit,
    onPlayersChanged: (List<ClubPlayer>) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var playerToDelete by remember { mutableStateOf<ClubPlayer?>(null) }
    var playerToEdit by remember { mutableStateOf<ClubPlayer?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var sortMode by remember { mutableStateOf("rating") }

    val filteredPlayers = remember(clubPlayers, searchQuery, sortMode) {
        val query = normalizePlayerName(searchQuery).lowercase()

        val base = if (query.isBlank()) {
            clubPlayers
        } else {
            clubPlayers.filter { player ->
                player.fullName.lowercase().contains(query)
            }
        }

        when (sortMode) {
            "name" -> base.sortedBy { it.fullName.lowercase() }
            else -> base.sortedWith(
                compareByDescending<ClubPlayer> { it.rating }
                    .thenBy { it.fullName.lowercase() }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgLight)
            .imePadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 16.dp, top = 28.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppBackButton(onClick = onBack)

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("Регистрация игроков", style = AppTypography.displayLarge, color = TextDark)
                Text(
                    "${clubPlayers.size} игроков • ФИО, рейтинг, статистика, аватар",
                    style = AppTypography.bodyMedium,
                    color = TextGray
                )
            }

            Button(
                onClick = { showCreateDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("+ Игрок", color = Color.White)
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Поиск по фамилии или имени") },
                    leadingIcon = {
                        Icon(
                            imageVector = SearchIcon,
                            contentDescription = "Поиск",
                            tint = TextGray,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = BorderGray,
                        focusedBorderColor = AppleBlue,
                        unfocusedContainerColor = CardWhite,
                        focusedContainerColor = CardWhite
                    ),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SegmentBtn("По рейтингу", sortMode == "rating", Modifier.weight(1f)) {
                        sortMode = "rating"
                    }
                    SegmentBtn("По алфавиту", sortMode == "name", Modifier.weight(1f)) {
                        sortMode = "name"
                    }
                }
            }

            if (filteredPlayers.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardWhite),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, BorderGray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            if (clubPlayers.isEmpty()) {
                                "Нажмите «+ Игрок», чтобы зарегистрировать первого игрока."
                            } else {
                                "Ничего не найдено."
                            },
                            style = AppTypography.bodyMedium,
                            color = TextGray,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(
                    count = filteredPlayers.size,
                    key = { index -> filteredPlayers[index].id }
                ) { index ->
                    val player = filteredPlayers[index]
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { dismissValue ->
                            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                playerToDelete = player
                            }

                            false
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = false,
                        backgroundContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(CellLostBg)
                                    .padding(end = 18.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    imageVector = TrashIcon,
                                    contentDescription = "Удалить",
                                    tint = SwipeDeleteRed,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        },
                        content = {
                            PlayerRegistrationCard(
                                player = player,
                                onClick = { playerToEdit = player }
                            )
                        }
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        PlayerRegistrationDialog(
            existingPlayers = clubPlayers,
            player = null,
            onDismiss = { showCreateDialog = false },
            onSave = { savedPlayer ->
                val nextId = (clubPlayers.maxOfOrNull { it.id } ?: 0) + 1
                val playerWithId = savedPlayer.copy(id = nextId)
                onPlayersChanged((clubPlayers + playerWithId).sortedBy { it.fullName.lowercase() })
                showCreateDialog = false
            }
        )
    }

    playerToEdit?.let { player ->
        PlayerRegistrationDialog(
            existingPlayers = clubPlayers,
            player = player,
            onDismiss = { playerToEdit = null },
            onSave = { savedPlayer ->
                val updated = clubPlayers.map {
                    if (it.id == savedPlayer.id) savedPlayer else it
                }
                onPlayersChanged(updated)
                playerToEdit = null
            }
        )
    }

    playerToDelete?.let { player ->
        AlertDialog(
            onDismissRequest = { playerToDelete = null },
            title = { Text("Удалить игрока?") },
            text = {
                Text(
                    "${player.fullName} будет удалён из базы игроков. " +
                        "В истории турниров ФИО останется как текст."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val updatedPlayers = clubPlayers.filterNot { it.id == player.id }
                        onPlayersChanged(updatedPlayers)
                        playerToDelete = null
                    }
                ) {
                    Text("Удалить", color = SwipeDeleteRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { playerToDelete = null }) {
                    Text("Отмена", color = TextGray)
                }
            },
            containerColor = CardWhite
        )
    }
}



@Composable
private fun PlayerRegistrationCard(
    player: ClubPlayer,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, BorderGray),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlayerAvatar(
                player = player,
                size = 58.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    player.fullName,
                    style = AppTypography.titleLarge,
                    color = TextDark,
                    maxLines = 1
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Рейтинг ${formatRating(player.rating)}",
                        color = AppleBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "Турниров: ${player.tournamentsPlayed}",
                        color = TextGray,
                        style = AppTypography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Игры: ${player.wins}-${player.losses}",
                        color = TextGray,
                        style = AppTypography.bodyMedium
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    MedalDots(
                        gold = player.goldMedals,
                        silver = player.silverMedals,
                        bronze = player.bronzeMedals
                    )
                }
            }

            Text("›", fontSize = 28.sp, color = TextGray)
        }
    }
}

@Composable
private fun PlayerAvatar(
    player: ClubPlayer,
    size: Dp
) {
    val avatarBitmap = rememberAvatarBitmap(player.avatarUri)

    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(18.dp))
            .background(BlueBadgeBg),
        contentAlignment = Alignment.Center
    ) {
        if (avatarBitmap != null) {
            Image(
                bitmap = avatarBitmap.asImageBitmap(),
                contentDescription = player.fullName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                player.fullName.firstOrNull()?.uppercase() ?: "И",
                color = AppleBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        }
    }
}

@Composable
private fun MedalDots(
    gold: Int,
    silver: Int,
    bronze: Int
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        MedalDot(Color(0xFFFFD54F), gold)
        Spacer(modifier = Modifier.width(5.dp))
        MedalDot(Color(0xFFC0C0C0), silver)
        Spacer(modifier = Modifier.width(5.dp))
        MedalDot(Color(0xFFCD7F32), bronze)
    }
}

@Composable
private fun MedalDot(
    color: Color,
    count: Int
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(50))
                .background(color)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text("$count", color = TextGray, style = AppTypography.bodyMedium)
    }
}

@Composable
private fun PlayerRegistrationDialog(
    existingPlayers: List<ClubPlayer>,
    player: ClubPlayer?,
    onDismiss: () -> Unit,
    onSave: (ClubPlayer) -> Unit
) {
    val context = LocalContext.current
    val isEditing = player != null

    var fullName by remember(player?.id) { mutableStateOf(player?.fullName.orEmpty()) }
    var ratingText by remember(player?.id) { mutableStateOf(formatRating(player?.rating ?: 100.0)) }
    var tournamentsText by remember(player?.id) { mutableStateOf((player?.tournamentsPlayed ?: 0).toString()) }
    var winsText by remember(player?.id) { mutableStateOf((player?.wins ?: 0).toString()) }
    var lossesText by remember(player?.id) { mutableStateOf((player?.losses ?: 0).toString()) }
    var goldText by remember(player?.id) { mutableStateOf((player?.goldMedals ?: 0).toString()) }
    var silverText by remember(player?.id) { mutableStateOf((player?.silverMedals ?: 0).toString()) }
    var bronzeText by remember(player?.id) { mutableStateOf((player?.bronzeMedals ?: 0).toString()) }
    var avatarUri by remember(player?.id) { mutableStateOf(player?.avatarUri.orEmpty()) }
    var errorText by remember { mutableStateOf<String?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val id = player?.id ?: ((existingPlayers.maxOfOrNull { it.id } ?: 0) + 1)
            avatarUri = saveBitmapAvatar(context, bitmap, id)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            avatarUri = uri.toString()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.92f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    if (isEditing) "Карточка игрока" else "Регистрация игрока",
                    style = AppTypography.displayLarge,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .width(210.dp)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        PlayerAvatar(
                            player = ClubPlayer(
                                id = player?.id ?: 0,
                                fullName = fullName.ifBlank { "Игрок" },
                                rating = ratingText.replace(",", ".").toDoubleOrNull() ?: 100.0,
                                avatarUri = avatarUri
                            ),
                            size = 118.dp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { cameraLauncher.launch(null) },
                            colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("📷 Камера", color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            border = BorderStroke(1.dp, AppleBlue),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("🖼 Галерея", color = AppleBlue)
                        }

                        if (avatarUri.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(onClick = { avatarUri = "" }) {
                                Text("Убрать аватар", color = SwipeDeleteRed)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Card(
                            colors = CardDefaults.cardColors(containerColor = BlueBadgeBg),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                "Если в поле «турниров» поставить 6, правило новичка уже не будет работать.",
                                color = BlueBadgeText,
                                style = AppTypography.bodyMedium,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                    ) {
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = {
                                fullName = it
                                errorText = null
                            },
                            label = { Text("ФИО игрока") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = BorderGray,
                                focusedBorderColor = AppleBlue,
                                unfocusedContainerColor = CardWhite,
                                focusedContainerColor = CardWhite
                            ),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            StatInputField("Текущий рейтинг", ratingText, Modifier.weight(1f)) {
                                ratingText = it
                            }
                            StatInputField("Турниров сыграл", tournamentsText, Modifier.weight(1f)) {
                                tournamentsText = it
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            StatInputField("Победы", winsText, Modifier.weight(1f)) {
                                winsText = it
                            }
                            StatInputField("Поражения", lossesText, Modifier.weight(1f)) {
                                lossesText = it
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Медали", style = AppTypography.titleLarge, color = TextDark)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            StatInputField("Золото", goldText, Modifier.weight(1f)) {
                                goldText = it
                            }
                            StatInputField("Серебро", silverText, Modifier.weight(1f)) {
                                silverText = it
                            }
                            StatInputField("Бронза", bronzeText, Modifier.weight(1f)) {
                                bronzeText = it
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Статистика с соперниками", style = AppTypography.titleLarge, color = TextDark)
                        Spacer(modifier = Modifier.height(8.dp))

                        val stats = decodeOpponentStats(player?.opponentStats.orEmpty())
                            .toList()
                            .sortedByDescending { (_, value) -> value.first + value.second }

                        if (stats.isEmpty()) {
                            Text(
                                "Появится автоматически после сыгранных матчей.",
                                color = TextGray,
                                style = AppTypography.bodyMedium
                            )
                        } else {
                            stats.take(12).forEach { (opponent, result) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        opponent,
                                        modifier = Modifier.weight(1f),
                                        color = TextDark,
                                        maxLines = 1
                                    )
                                    Text(
                                        "${result.first}-${result.second}",
                                        color = AppleBlue,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                HorizontalDivider(color = BorderGray)
                            }
                        }

                        errorText?.let {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(it, color = CellLostText, style = AppTypography.bodyMedium)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Отмена", color = TextGray)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val cleanName = normalizePlayerName(fullName)
                            val rating = ratingText.replace(",", ".").toDoubleOrNull()
                            val tournaments = tournamentsText.toIntOrNull()
                            val wins = winsText.toIntOrNull()
                            val losses = lossesText.toIntOrNull()
                            val gold = goldText.toIntOrNull()
                            val silver = silverText.toIntOrNull()
                            val bronze = bronzeText.toIntOrNull()

                            val duplicate = existingPlayers.any {
                                it.id != (player?.id ?: -1) &&
                                    it.fullName.lowercase() == cleanName.lowercase()
                            }

                            when {
                                cleanName.isBlank() -> errorText = "Введите ФИО игрока."
                                duplicate -> errorText = "Такой игрок уже есть в базе."
                                rating == null || rating < 1.0 -> errorText = "Введите корректный рейтинг."
                                tournaments == null || tournaments < 0 -> errorText = "Введите корректное число турниров."
                                wins == null || wins < 0 -> errorText = "Введите корректное число побед."
                                losses == null || losses < 0 -> errorText = "Введите корректное число поражений."
                                gold == null || gold < 0 -> errorText = "Введите корректное число золотых медалей."
                                silver == null || silver < 0 -> errorText = "Введите корректное число серебряных медалей."
                                bronze == null || bronze < 0 -> errorText = "Введите корректное число бронзовых медалей."
                                else -> {
                                    onSave(
                                        ClubPlayer(
                                            id = player?.id ?: 0,
                                            fullName = cleanName,
                                            rating = roundRating(rating),
                                            tournamentsPlayed = tournaments,
                                            wins = wins,
                                            losses = losses,
                                            goldMedals = gold,
                                            silverMedals = silver,
                                            bronzeMedals = bronze,
                                            avatarUri = avatarUri,
                                            opponentStats = player?.opponentStats.orEmpty()
                                        )
                                    )
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Сохранить", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatInputField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = BorderGray,
            focusedBorderColor = AppleBlue,
            unfocusedContainerColor = CardWhite,
            focusedContainerColor = CardWhite
        ),
        shape = RoundedCornerShape(14.dp),
        singleLine = true
    )
}

@Composable
fun TournamentHistoryScreen(
    history: List<TournamentHistoryEntry>,
    onBack: () -> Unit,
    onHistoryChanged: (List<TournamentHistoryEntry>) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedEntry by remember { mutableStateOf<TournamentHistoryEntry?>(null) }
    var entryToDelete by remember { mutableStateOf<TournamentHistoryEntry?>(null) }

    val filteredHistory = remember(history, searchQuery) {
        val query = normalizePlayerName(searchQuery).lowercase()

        if (query.isBlank()) {
            history
        } else {
            history.filter { entry ->
                entry.name.lowercase().contains(query) ||
                    entry.winnerName.lowercase().contains(query) ||
                    entry.standingsText.lowercase().contains(query)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgLight)
            .imePadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 16.dp, top = 28.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppBackButton(onClick = onBack)

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("История турниров", style = AppTypography.displayLarge, color = TextDark)
                Text(
                    "${history.size} завершённых турниров",
                    style = AppTypography.bodyMedium,
                    color = TextGray
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Поиск по турниру, игроку или победителю") },
                    leadingIcon = {
                        Icon(
                            imageVector = SearchIcon,
                            contentDescription = "Поиск",
                            tint = TextGray,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = BorderGray,
                        focusedBorderColor = AppleBlue,
                        unfocusedContainerColor = CardWhite,
                        focusedContainerColor = CardWhite
                    ),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )
            }

            item {
                Text(
                    "Свайпните турнир влево, чтобы удалить его из истории.",
                    style = AppTypography.bodyMedium,
                    color = TextGray
                )
            }

            if (filteredHistory.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardWhite),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, BorderGray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            if (history.isEmpty()) {
                                "История появится после завершения первого турнира."
                            } else {
                                "Ничего не найдено."
                            },
                            style = AppTypography.bodyMedium,
                            color = TextGray,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(
                    count = filteredHistory.size,
                    key = { index -> filteredHistory[index].id }
                ) { index ->
                    val entry = filteredHistory[index]
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { dismissValue ->
                            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                entryToDelete = entry
                            }

                            false
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = false,
                        backgroundContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(CellLostBg)
                                    .padding(end = 18.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    imageVector = TrashIcon,
                                    contentDescription = "Удалить",
                                    tint = SwipeDeleteRed,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        },
                        content = {
                            HistoryCardLight(
                                name = entry.name,
                                date = entry.dateText,
                                players = entry.playersCount.toString(),
                                winner = entry.winnerName,
                                isFinished = true,
                                icon = "🏆",
                                onClick = { selectedEntry = entry }
                            )
                        }
                    )
                }
            }
        }
    }

    selectedEntry?.let { entry ->
        AlertDialog(
            onDismissRequest = { selectedEntry = null },
            title = {
                Text(entry.name)
            },
            text = {
                Column(
                    modifier = Modifier
                        .heightIn(max = 420.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text("Дата: ${entry.dateText}", color = TextGray)
                    Text("Участников: ${entry.playersCount}", color = TextGray)
                    Text("Победитель: ${entry.winnerName}", color = AppleBlue)

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Итоговые места", style = AppTypography.titleLarge, color = TextDark)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(entry.standingsText, color = TextDark)

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Рейтинг", style = AppTypography.titleLarge, color = TextDark)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(entry.ratingText, color = TextDark)
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedEntry = null }) {
                    Text("Закрыть", color = AppleBlue)
                }
            },
            containerColor = CardWhite
        )
    }

    entryToDelete?.let { entry ->
        AlertDialog(
            onDismissRequest = { entryToDelete = null },
            title = { Text("Удалить турнир?") },
            text = {
                Text(
                    "Турнир «${entry.name}» будет удалён из истории. " +
                        "Рейтинг игроков не откатится."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val updatedHistory = history.filterNot { it.id == entry.id }
                        onHistoryChanged(updatedHistory)
                        entryToDelete = null
                    }
                ) {
                    Text("Удалить", color = SwipeDeleteRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { entryToDelete = null }) {
                    Text("Отмена", color = TextGray)
                }
            },
            containerColor = CardWhite
        )
    }
}

@Composable
fun HistoryCardLight(
    name: String,
    date: String,
    players: String,
    winner: String,
    isFinished: Boolean,
    icon: String,
    onClick: () -> Unit = {}
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderGray),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = icon, fontSize = 28.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(name, style = AppTypography.titleLarge, color = TextDark)
                    Text(
                        "👥 $players участников • 📅 $date",
                        style = AppTypography.bodyMedium,
                        color = TextGray
                    )
                    Text(
                        "Победитель: $winner",
                        style = AppTypography.bodyMedium,
                        color = AppleBlue
                    )
                }
            }

            Text("›", fontSize = 28.sp, color = TextGray)
        }
    }
}

// ==========================================
// 2. ЭКРАН НАСТРОЕК
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTournamentScreen(
    draft: TournamentDraft,
    clubPlayers: List<ClubPlayer>,
    onBack: () -> Unit,
    onStartTournament: () -> Unit
) {
    var startError by remember { mutableStateOf<String?>(null) }

    val normalizedNames = draft.playerFields.map { normalizePlayerName(it.name) }
    val duplicateKeys = normalizedNames
        .filter { it.isNotBlank() }
        .groupBy { it.lowercase() }
        .filter { it.value.size > 1 }
        .keys

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgLight)
            .imePadding()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 16.dp, top = 28.dp, bottom = 12.dp)
        ) {
            AppBackButton(onClick = onBack)

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Новый турнир",
                    style = AppTypography.titleLarge,
                    color = TextDark
                )
                Text(
                    if (draft.name.isBlank()) "Заполните настройки и игроков" else draft.name,
                    style = AppTypography.bodyMedium,
                    color = TextGray,
                    maxLines = 1
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 160.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, BorderGray),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Название турнира", style = AppTypography.titleLarge, color = TextDark)
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = draft.name,
                            onValueChange = {
                                draft.name = it
                                startError = null
                            },
                            placeholder = { Text("Например: Вечерний турнир") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = BorderGray,
                                focusedBorderColor = AppleBlue,
                                unfocusedContainerColor = CardWhite,
                                focusedContainerColor = CardWhite
                            ),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true
                        )
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, BorderGray),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("👥 Количество участников", style = AppTypography.titleLarge, color = TextDark)
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    "–",
                                    fontSize = 22.sp,
                                    color = if (draft.playersCount > 2) TextGray else BorderGray,
                                    modifier = Modifier.clickable(enabled = draft.playersCount > 2) {
                                        draft.playersCount--

                                        val maximumTables = (draft.playersCount / 2).coerceIn(1, 5)
                                        if (draft.tablesCount > maximumTables) {
                                            draft.tablesCount = maximumTables
                                        }

                                        if (draft.isListGenerated && draft.playerFields.isNotEmpty()) {
                                            draft.playerFields.removeAt(draft.playerFields.lastIndex)
                                        }

                                        startError = null
                                    }
                                )

                                Text(
                                    "${draft.playersCount}",
                                    style = AppTypography.titleLarge,
                                    color = TextDark,
                                    modifier = Modifier.padding(horizontal = 22.dp)
                                )

                                Text(
                                    "+",
                                    fontSize = 22.sp,
                                    color = TextGray,
                                    modifier = Modifier.clickable {
                                        draft.playersCount++

                                        if (draft.isListGenerated) {
                                            draft.playerFields.add(PlayerField(draft.nextFieldId++, ""))
                                        }

                                        startError = null
                                    }
                                )
                            }

                            Button(
                                onClick = {
                                    draft.playerFields.clear()
                                    draft.matchScores.clear()
                                    draft.activeRoundRobinMatches.clear()
                                    draft.withdrawnPlayers.clear()
                                    draft.ratingApplied = false
                                    draft.historySaved = false
                                    draft.ratingChanges.clear()
                                    repeat(draft.playersCount) {
                                        draft.playerFields.add(PlayerField(draft.nextFieldId++, ""))
                                    }
                                    draft.isListGenerated = true
                                    startError = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("✨ Список игроков", style = AppTypography.labelLarge, color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text("🏆 До скольки побед", style = AppTypography.titleLarge, color = TextDark)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SegmentBtn("2 победы", !draft.bestOf3, Modifier.weight(1f)) {
                                draft.bestOf3 = false
                            }
                            SegmentBtn("3 победы", draft.bestOf3, Modifier.weight(1f)) {
                                draft.bestOf3 = true
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text("🏓 Количество столов", style = AppTypography.titleLarge, color = TextDark)
                        Spacer(modifier = Modifier.height(12.dp))

                        val maximumTables = (draft.playersCount / 2).coerceIn(1, 5)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    "–",
                                    fontSize = 22.sp,
                                    color = if (draft.tablesCount > 1) TextGray else BorderGray,
                                    modifier = Modifier.clickable(enabled = draft.tablesCount > 1) {
                                        draft.tablesCount--
                                    }
                                )

                                Text(
                                    "${draft.tablesCount}",
                                    style = AppTypography.titleLarge,
                                    color = TextDark,
                                    modifier = Modifier.padding(horizontal = 22.dp)
                                )

                                Text(
                                    "+",
                                    fontSize = 22.sp,
                                    color = if (draft.tablesCount < maximumTables) TextGray else BorderGray,
                                    modifier = Modifier.clickable(enabled = draft.tablesCount < maximumTables) {
                                        draft.tablesCount++
                                    }
                                )
                            }

                            Text(
                                "До ${draft.tablesCount * 2} игроков одновременно",
                                style = AppTypography.bodyMedium,
                                color = TextGray,
                                textAlign = TextAlign.End
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text("📈 Рейтинг RTTF", style = AppTypography.titleLarge, color = TextDark)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Коэффициент турнира рассчитывается автоматически по среднему рейтингу участников.",
                            style = AppTypography.bodyMedium,
                            color = TextGray
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Card(
                            colors = CardDefaults.cardColors(containerColor = BlueBadgeBg),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "RTTF: k зависит от среднего рейтинга турнира, D — от разницы партий. Новички до 5 турниров считаются отдельно.",
                                style = AppTypography.bodyMedium,
                                color = BlueBadgeText,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            if (draft.isListGenerated) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardWhite),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, BorderGray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("👥 Игроки", style = AppTypography.titleLarge, color = TextDark)
                                    Text(
                                        "Свайп здесь отключён. Удаление — только кнопкой справа.",
                                        style = AppTypography.bodyMedium,
                                        color = TextGray
                                    )
                                }

                                Text(
                                    "${draft.playerFields.size}",
                                    style = AppTypography.titleLarge,
                                    color = AppleBlue
                                )
                            }

                            if (clubPlayers.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = BlueBadgeBg),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        "Введите 3 буквы — появятся сохранённые игроки",
                                        style = AppTypography.bodyMedium,
                                        color = BlueBadgeText,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            draft.playerFields.forEachIndexed { index, field ->
                                key(field.id) {
                                    val cleanName = normalizePlayerName(field.name)
                                    val isDuplicate = cleanName.isNotBlank() &&
                                        duplicateKeys.contains(cleanName.lowercase())
                                    val hasProblem = cleanName.isBlank() || isDuplicate

                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (hasProblem) Color(0xFFFFFBFE) else CardWhite
                                        ),
                                        shape = RoundedCornerShape(16.dp),
                                        border = BorderStroke(
                                            width = 1.dp,
                                            color = if (hasProblem) SwipeDeleteRed.copy(alpha = 0.35f) else BorderGray
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 10.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.Top,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(34.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(if (hasProblem) CellLostBg else BlueBadgeBg),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    "${index + 1}",
                                                    style = AppTypography.labelLarge,
                                                    color = if (hasProblem) CellLostText else AppleBlue
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(10.dp))

                                            Column(modifier = Modifier.weight(1f)) {
                                                PlayerNameInputField(
                                                    value = field.name,
                                                    clubPlayers = clubPlayers,
                                                    modifier = Modifier.fillMaxWidth(),
                                                    onValueChange = { newName ->
                                                        val idx = draft.playerFields.indexOf(field)
                                                        if (idx != -1) {
                                                            draft.playerFields[idx] = field.copy(name = newName)
                                                        }
                                                        startError = null
                                                    }
                                                )

                                                if (hasProblem) {
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        if (cleanName.isBlank()) {
                                                            "Заполните ФИО игрока"
                                                        } else {
                                                            "Этот игрок уже есть в списке"
                                                        },
                                                        style = AppTypography.bodyMedium,
                                                        color = CellLostText
                                                    )
                                                }
                                            }

                                            if (draft.playerFields.size > 2) {
                                                Spacer(modifier = Modifier.width(8.dp))
                                                IconButton(
                                                    onClick = {
                                                        draft.playerFields.remove(field)
                                                        draft.playersCount = draft.playerFields.size

                                                        val maximumTablesNow = (draft.playersCount / 2).coerceIn(1, 5)
                                                        if (draft.tablesCount > maximumTablesNow) {
                                                            draft.tablesCount = maximumTablesNow
                                                        }

                                                        startError = null
                                                    },
                                                    modifier = Modifier
                                                        .size(44.dp)
                                                        .clip(RoundedCornerShape(14.dp))
                                                        .background(CellLostBg)
                                                ) {
                                                    Icon(
                                                        imageVector = TrashIcon,
                                                        contentDescription = "Удалить",
                                                        tint = SwipeDeleteRed,
                                                        modifier = Modifier.size(22.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Text(
                                "+ Добавить игрока",
                                style = AppTypography.labelLarge,
                                color = AppleBlue,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, AppleBlue.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable {
                                        draft.playerFields.add(PlayerField(draft.nextFieldId++, ""))
                                        draft.playersCount = draft.playerFields.size
                                        startError = null
                                    }
                                    .padding(14.dp)
                            )
                        }
                    }
                }
            }
        }

        if (draft.isListGenerated) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    startError?.let { error ->
                        Text(
                            error,
                            style = AppTypography.bodyMedium,
                            color = CellLostText,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Button(
                        onClick = {
                            val error = tournamentStartError(draft)

                            if (error == null) {
                                onStartTournament()
                            } else {
                                startError = error
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                    ) {
                        Text("🏆 Создать турнир", style = AppTypography.labelLarge, color = Color.White)
                    }
                }
            }
        }
    }
}





@Composable
fun PlayerNameInputField(
    value: String,
    clubPlayers: List<ClubPlayer>,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    var showSuggestions by remember { mutableStateOf(false) }
    val suggestions = playerSuggestions(value, clubPlayers)

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                onValueChange(newValue)
                showSuggestions = true
            },
            placeholder = { Text("Фамилия Имя") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 58.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = BorderGray,
                focusedBorderColor = AppleBlue,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            ),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )

        if (showSuggestions && suggestions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, AppleBlue.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    suggestions.forEachIndexed { index, player ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onValueChange(player.fullName)
                                    showSuggestions = false
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Text(
                                player.fullName,
                                color = TextDark,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                maxLines = 2
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = BlueBadgeBg),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        "рейтинг ${formatRating(player.rating)}",
                                        color = AppleBlue,
                                        style = AppTypography.bodyMedium,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    "нажмите, чтобы вставить",
                                    color = TextGray,
                                    style = AppTypography.bodyMedium
                                )
                            }
                        }

                        if (index != suggestions.lastIndex) {
                            HorizontalDivider(color = BorderGray)
                        }
                    }
                }
            }
        }
    }
}





@Composable
fun SegmentBtn(text: String, isSelected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val bgColor = if (isSelected) AppleBlue.copy(alpha = 0.1f) else CardWhite
    Box(modifier = modifier.border(1.dp, if (isSelected) AppleBlue else BorderGray, RoundedCornerShape(8.dp)).clip(RoundedCornerShape(8.dp)).background(bgColor).clickable { onClick() }.padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
        Text(text, style = AppTypography.labelLarge, color = if (isSelected) AppleBlue else TextGray)
    }
}

// ==========================================
// 3. ЭКРАН КРУГОВОГО ТУРНИРА
// ==========================================

private fun normalizedPair(first: Int, second: Int): Pair<Int, Int> =
    if (first < second) first to second else second to first

private fun reverseScore(score: String): String {
    val parts = score.split(":")
    return if (parts.size == 2) "${parts[1]}:${parts[0]}" else score
}

private fun parseScore(score: String?): Pair<Int, Int>? {
    if (score.isNullOrBlank() || !score.contains(":")) return null
    val parts = score.split(":")
    if (parts.size != 2) return null

    val first = parts[0].toIntOrNull() ?: return null
    val second = parts[1].toIntOrNull() ?: return null
    return first to second
}

private fun isTechnicalScore(score: String?): Boolean =
    score == "W" || score == "L"

private fun rebuildTechnicalResults(
    draft: TournamentDraft,
    playerCount: Int
) {
    val technicalKeys = draft.matchScores
        .filterValues(::isTechnicalScore)
        .keys
        .toList()

    technicalKeys.forEach { key ->
        draft.matchScores.remove(key)
    }

    val withdrawn = draft.withdrawnPlayers.toSet()

    for (first in 0 until playerCount) {
        for (second in first + 1 until playerCount) {
            val firstScore = draft.matchScores[first to second]
            val secondScore = draft.matchScores[second to first]

            // Уже сыгранный обычный матч не меняем.
            if (parseScore(firstScore) != null || parseScore(secondScore) != null) {
                continue
            }

            val firstWithdrawn = first in withdrawn
            val secondWithdrawn = second in withdrawn

            when {
                firstWithdrawn && secondWithdrawn -> {
                    draft.matchScores[first to second] = "L"
                    draft.matchScores[second to first] = "L"
                }

                firstWithdrawn -> {
                    draft.matchScores[first to second] = "L"
                    draft.matchScores[second to first] = "W"
                }

                secondWithdrawn -> {
                    draft.matchScores[first to second] = "W"
                    draft.matchScores[second to first] = "L"
                }
            }
        }
    }

    refreshActiveRoundRobinMatches(
        draft = draft,
        playerCount = playerCount,
        tablesCount = draft.tablesCount
    )
}

private fun totalRoundRobinMatches(playerCount: Int): Int =
    playerCount * (playerCount - 1) / 2

private fun completedRoundRobinMatches(
    playerCount: Int,
    scores: Map<Pair<Int, Int>, String>
): Int {
    var completed = 0

    for (first in 0 until playerCount) {
        for (second in first + 1 until playerCount) {
            if (scores.containsKey(first to second)) {
                completed++
            }
        }
    }

    return completed
}

/**
 * Оставляет незавершённые назначения на столы и добавляет новые пары.
 * Один игрок не может находиться сразу за двумя столами.
 */
private fun refreshActiveRoundRobinMatches(
    draft: TournamentDraft,
    playerCount: Int,
    tablesCount: Int
) {
    val effectiveTablesCount = tablesCount.coerceIn(
        minimumValue = 1,
        maximumValue = maxOf(1, playerCount / 2)
    )
    val validAssignments = draft.activeRoundRobinMatches
        .filter { pair ->
            pair.first in 0 until playerCount &&
                pair.second in 0 until playerCount &&
                !draft.matchScores.containsKey(normalizedPair(pair.first, pair.second))
        }
        .map { normalizedPair(it.first, it.second) }
        .distinct()

    draft.activeRoundRobinMatches.clear()
    draft.activeRoundRobinMatches.addAll(validAssignments.take(effectiveTablesCount))

    val busyPlayers = draft.activeRoundRobinMatches
        .flatMap { listOf(it.first, it.second) }
        .toMutableSet()

    if (draft.activeRoundRobinMatches.size >= effectiveTablesCount) return

    for (first in 0 until playerCount) {
        for (second in first + 1 until playerCount) {
            if (draft.activeRoundRobinMatches.size >= effectiveTablesCount) return

            val pair = first to second
            val alreadyAssigned = pair in draft.activeRoundRobinMatches
            val alreadyPlayed = draft.matchScores.containsKey(pair)
            val playerIsBusy = first in busyPlayers || second in busyPlayers

            if (!alreadyAssigned && !alreadyPlayed && !playerIsBusy) {
                draft.activeRoundRobinMatches.add(pair)
                busyPlayers.add(first)
                busyPlayers.add(second)
            }
        }
    }
}

private fun playerWon(score: String?): Boolean {
    val parsed = parseScore(score) ?: return false
    return parsed.first > parsed.second
}

private fun rankTiedPlayers(
    tiedPlayers: List<Int>,
    scores: Map<Pair<Int, Int>, String>
): List<Int> {
    if (tiedPlayers.size <= 1) return tiedPlayers

    val stats = tiedPlayers.map { player ->
        var miniPoints = 0
        var setsWon = 0
        var setsLost = 0

        tiedPlayers
            .filter { opponent -> opponent != player }
            .forEach { opponent ->
                val result = parseScore(scores[player to opponent]) ?: return@forEach
                setsWon += result.first
                setsLost += result.second
                if (result.first > result.second) miniPoints++
            }

        val ratio = when {
            setsLost == 0 && setsWon > 0 -> Double.POSITIVE_INFINITY
            setsLost == 0 -> 0.0
            else -> setsWon.toDouble() / setsLost.toDouble()
        }

        TieStat(player, miniPoints, ratio)
    }

    val orderedBuckets = stats
        .groupBy { stat -> stat.miniPoints to stat.setRatio }
        .toList()
        .sortedWith(
            compareByDescending<Pair<Pair<Int, Double>, List<TieStat>>> { it.first.first }
                .thenByDescending { it.first.second }
        )

    // Если никто не разделился по дополнительным показателям,
    // сохраняем стабильный порядок регистрации.
    if (orderedBuckets.size == 1) {
        return tiedPlayers.sorted()
    }

    return orderedBuckets.flatMap { (_, bucket) ->
        val players = bucket.map { it.playerIndex }
        if (players.size > 1) {
            rankTiedPlayers(players, scores)
        } else {
            players
        }
    }
}

private fun calculateRoundRobinStandings(
    playerCount: Int,
    scores: Map<Pair<Int, Int>, String>
): List<PlayerStat> {
    val wins = IntArray(playerCount)

    for (player in 0 until playerCount) {
        for (opponent in 0 until playerCount) {
            if (player != opponent && playerWon(scores[player to opponent])) {
                wins[player]++
            }
        }
    }

    val orderedPlayers = wins.indices
        .groupBy { player -> wins[player] }
        .toList()
        .sortedByDescending { it.first }
        .flatMap { (_, playersWithSamePoints) ->
            rankTiedPlayers(playersWithSamePoints, scores)
        }

    return orderedPlayers.mapIndexed { placeIndex, playerIndex ->
        PlayerStat(
            index = playerIndex,
            name = "",
            points = wins[playerIndex]
        )
    }
}

@Composable
fun RoundRobinScreen(
    draft: TournamentDraft,
    onBack: () -> Unit,
    onFinish: (Boolean) -> Unit
) {
    val playerNames = draft.playerFields.mapIndexed { index, field ->
        field.name.trim().ifBlank { "Игрок ${index + 1}" }
    }

    val playerCount = playerNames.size
    val totalMatches = totalRoundRobinMatches(playerCount)
    val completedMatches = completedRoundRobinMatches(playerCount, draft.matchScores)
    val tournamentIsComplete = playerCount >= 2 && completedMatches == totalMatches
    val standings = calculateRoundRobinStandings(playerCount, draft.matchScores)
    val placesByPlayer = standings
        .mapIndexed { place, stat -> stat.index to place + 1 }
        .toMap()
    val pointsByPlayer = standings.associate { stat -> stat.index to stat.points }
    val activePlayers = draft.activeRoundRobinMatches.flatMap { listOf(it.first, it.second) }.toSet()

    var selectedPair by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var statusPlayerIndex by remember { mutableStateOf<Int?>(null) }
    var askPlaceBonusDialog by remember { mutableStateOf(false) }
    var tableScale by remember { mutableStateOf(if (playerCount <= 6) 0.90f else 0.86f) }

    LaunchedEffect(
        playerCount,
        draft.tablesCount,
        draft.withdrawnPlayers.toList()
    ) {
        rebuildTechnicalResults(
            draft = draft,
            playerCount = playerCount
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgLight)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        SimpleRoundRobinTopBar(
            tournamentName = draft.name.ifBlank { "Турнир" },
            tournamentIsComplete = tournamentIsComplete,
            onBack = onBack,
            onFinish = { askPlaceBonusDialog = true }
        )

        Spacer(modifier = Modifier.height(6.dp))

        if (playerCount > 6) {
            CompactActiveTablesPanel(
                assignments = draft.activeRoundRobinMatches,
                playerNames = playerNames,
                configuredTablesCount = draft.tablesCount,
                onMatchClick = { pair -> selectedPair = pair }
            )

            Spacer(modifier = Modifier.height(6.dp))
        }

        if (playerCount <= 6) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val tableWidth = (270f + playerCount * 62f).dp

                Card(
                    modifier = Modifier
                        .width(tableWidth)
                        .fillMaxHeight(),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BorderGray)
                ) {
                    RoundRobinTable(
                        playerNames = playerNames,
                        scores = draft.matchScores,
                        placesByPlayer = placesByPlayer,
                        pointsByPlayer = pointsByPlayer,
                        activeMatches = draft.activeRoundRobinMatches,
                        activePlayers = activePlayers,
                        withdrawnPlayers = draft.withdrawnPlayers.toSet(),
                        selectedPair = selectedPair,
                        searchQuery = "",
                        tableScale = tableScale,
                        onCellClick = { first, second ->
                            selectedPair = first to second
                        },
                        onPlayerClick = { playerIndex ->
                            statusPlayerIndex = playerIndex
                        }
                    )
                }

                RoundRobinSidePanel(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    assignments = draft.activeRoundRobinMatches,
                    playerNames = playerNames,
                    standings = standings,
                    pointsByPlayer = pointsByPlayer,
                    completedMatches = completedMatches,
                    totalMatches = totalMatches,
                    onMatchClick = { pair -> selectedPair = pair }
                )
            }
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BorderGray)
            ) {
                RoundRobinTable(
                    playerNames = playerNames,
                    scores = draft.matchScores,
                    placesByPlayer = placesByPlayer,
                    pointsByPlayer = pointsByPlayer,
                    activeMatches = draft.activeRoundRobinMatches,
                    activePlayers = activePlayers,
                    withdrawnPlayers = draft.withdrawnPlayers.toSet(),
                    selectedPair = selectedPair,
                    searchQuery = "",
                    tableScale = tableScale,
                    onCellClick = { first, second ->
                        selectedPair = first to second
                    },
                    onPlayerClick = { playerIndex ->
                        statusPlayerIndex = playerIndex
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        SimpleRoundRobinBottomBar(
            completedMatches = completedMatches,
            totalMatches = totalMatches,
            bestOfThreeWins = draft.bestOf3,
            tableScale = tableScale,
            onZoomOut = { tableScale = (tableScale - 0.08f).coerceAtLeast(0.78f) },
            onZoomIn = { tableScale = (tableScale + 0.08f).coerceAtMost(1.18f) },
            onResetZoom = { tableScale = if (playerCount <= 6) 0.90f else 0.86f }
        )
    }

    selectedPair?.let { pair ->
        val first = pair.first
        val second = pair.second

        if (first in playerNames.indices && second in playerNames.indices && first != second) {
            val tableNumber = draft.activeRoundRobinMatches
                .indexOf(normalizedPair(first, second))
                .takeIf { it >= 0 }
                ?.plus(1)

            RoundRobinScoreDialog(
                firstPlayer = playerNames[first],
                secondPlayer = playerNames[second],
                bestOfThreeWins = draft.bestOf3,
                tableNumber = tableNumber,
                existingScore = draft.matchScores[first to second],
                onDismiss = { selectedPair = null },
                onSave = { score ->
                    draft.matchScores[first to second] = score
                    draft.matchScores[second to first] = reverseScore(score)

                    val normalized = normalizedPair(first, second)
                    draft.activeRoundRobinMatches.remove(normalized)
                    refreshActiveRoundRobinMatches(
                        draft = draft,
                        playerCount = playerNames.size,
                        tablesCount = draft.tablesCount
                    )
                    selectedPair = null
                },
                onDelete = {
                    draft.matchScores.remove(first to second)
                    draft.matchScores.remove(second to first)

                    rebuildTechnicalResults(
                        draft = draft,
                        playerCount = playerNames.size
                    )

                    selectedPair = null
                }
            )
        }
    }

    if (askPlaceBonusDialog) {
        AlertDialog(
            onDismissRequest = { askPlaceBonusDialog = false },
            title = { Text("Начислить бонусы за медали?") },
            text = {
                Column {
                    Text(
                        "Мягкие бонусы за 1–3 место:",
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("🥇 1 место: +3 рейтинга", color = TextDark)
                    Text("🥈 2 место: +2 рейтинга", color = TextDark)
                    Text("🥉 3 место: +1 рейтинг", color = TextDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Счётчик медалей сохранится в базе игроков в любом случае.",
                        color = TextGray,
                        style = AppTypography.bodyMedium
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        askPlaceBonusDialog = false
                        onFinish(true)
                    }
                ) {
                    Text("Да, начислить", color = AppleBlue)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        askPlaceBonusDialog = false
                        onFinish(false)
                    }
                ) {
                    Text("Без бонусов", color = TextGray)
                }
            },
            containerColor = CardWhite
        )
    }

    statusPlayerIndex?.let { playerIndex ->
        val isWithdrawn = playerIndex in draft.withdrawnPlayers
        val playerName = playerNames.getOrElse(playerIndex) { "Игрок" }
        val playerPoints = pointsByPlayer[playerIndex] ?: 0
        val playerPlace = placesByPlayer[playerIndex] ?: playerIndex + 1
        val matchLines = playerMatchLines(playerIndex, playerNames, draft.matchScores)

        AlertDialog(
            onDismissRequest = { statusPlayerIndex = null },
            title = { Text(playerName) },
            text = {
                Column(
                    modifier = Modifier
                        .heightIn(max = 360.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text("Место: $playerPlace", color = TextDark)
                    Text("Очки: $playerPoints", color = TextDark)
                    Text(
                        if (isWithdrawn) "Статус: снят с турнира" else "Статус: участвует",
                        color = if (isWithdrawn) CellLostText else GreenBadgeText
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Матчи игрока", style = AppTypography.titleLarge, color = TextDark)
                    Spacer(modifier = Modifier.height(6.dp))

                    matchLines.forEach { line ->
                        Text(line, color = TextGray, modifier = Modifier.padding(vertical = 2.dp))
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (isWithdrawn) {
                            draft.withdrawnPlayers.remove(playerIndex)
                        } else {
                            draft.withdrawnPlayers.add(playerIndex)
                        }

                        rebuildTechnicalResults(
                            draft = draft,
                            playerCount = playerCount
                        )

                        statusPlayerIndex = null
                    }
                ) {
                    Text(
                        if (isWithdrawn) "Вернуть в турнир" else "Снять с турнира",
                        color = if (isWithdrawn) AppleBlue else SwipeDeleteRed
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { statusPlayerIndex = null }) {
                    Text("Закрыть", color = TextGray)
                }
            },
            containerColor = CardWhite
        )
    }
}









@Composable
private fun RoundRobinSidePanel(
    modifier: Modifier = Modifier,
    assignments: List<Pair<Int, Int>>,
    playerNames: List<String>,
    standings: List<PlayerStat>,
    pointsByPlayer: Map<Int, Int>,
    completedMatches: Int,
    totalMatches: Int,
    onMatchClick: (Pair<Int, Int>) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, BorderGray)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Кто играет", style = AppTypography.titleLarge, color = TextDark)
                Spacer(modifier = Modifier.height(8.dp))

                if (assignments.isEmpty()) {
                    Text("Свободных пар пока нет", color = TextGray, style = AppTypography.bodyMedium)
                } else {
                    assignments.forEachIndexed { index, pair ->
                        val firstName = playerNames.getOrElse(pair.first) { "Игрок" }
                        val secondName = playerNames.getOrElse(pair.second) { "Игрок" }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .clickable { onMatchClick(pair) },
                            colors = CardDefaults.cardColors(containerColor = GreenBadgeBg),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, GreenBadgeText.copy(alpha = 0.35f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    "Стол ${index + 1}",
                                    color = TextGray,
                                    style = AppTypography.bodyMedium
                                )
                                Text(
                                    firstName,
                                    color = AppleBlue,
                                    style = AppTypography.labelLarge,
                                    maxLines = 1
                                )
                                Text(
                                    "— $secondName",
                                    color = AppleBlue,
                                    style = AppTypography.labelLarge,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, BorderGray)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(12.dp)
            ) {
                Text("Текущие места", style = AppTypography.titleLarge, color = TextDark)
                Text(
                    "Сыграно: $completedMatches / $totalMatches",
                    color = TextGray,
                    style = AppTypography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                standings.forEachIndexed { place, stat ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "${place + 1}",
                            modifier = Modifier.width(28.dp),
                            color = AppleBlue,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            playerNames.getOrElse(stat.index) { "Игрок" },
                            modifier = Modifier.weight(1f),
                            color = TextDark,
                            maxLines = 1
                        )
                        Text(
                            "${pointsByPlayer[stat.index] ?: 0}",
                            color = TextGray,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (place != standings.lastIndex) {
                        HorizontalDivider(color = BorderGray)
                    }
                }
            }
        }
    }
}

@Composable
private fun SimpleRoundRobinTopBar(
    tournamentName: String,
    tournamentIsComplete: Boolean,
    onBack: () -> Unit,
    onFinish: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppBackButton(onClick = onBack)

        Spacer(modifier = Modifier.width(10.dp))

        Text("🏓", fontSize = 24.sp)

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            tournamentName,
            style = AppTypography.titleLarge,
            color = TextDark,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )

        if (tournamentIsComplete) {
            Button(
                onClick = onFinish,
                colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.height(42.dp)
            ) {
                Text("🏆 Завершить", color = Color.White, maxLines = 1)
            }
        }
    }
}

@Composable
private fun SimpleRoundRobinBottomBar(
    completedMatches: Int,
    totalMatches: Int,
    bestOfThreeWins: Boolean,
    tableScale: Float,
    onZoomOut: () -> Unit,
    onZoomIn: () -> Unit,
    onResetZoom: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, BorderGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Сыграно: $completedMatches / $totalMatches",
                color = TextDark,
                fontWeight = FontWeight.SemiBold,
                style = AppTypography.bodyMedium
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                if (bestOfThreeWins) "До 3 побед" else "До 2 побед",
                color = AppleBlue,
                style = AppTypography.bodyMedium
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
            ) {
                Text(
                    "−",
                    fontSize = 18.sp,
                    color = AppleBlue,
                    modifier = Modifier
                        .clickable { onZoomOut() }
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                )
                Text(
                    "${(tableScale * 100).toInt()}%",
                    color = TextDark,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clickable { onResetZoom() }
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                )
                Text(
                    "+",
                    fontSize = 18.sp,
                    color = AppleBlue,
                    modifier = Modifier
                        .clickable { onZoomIn() }
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                )
            }
        }
    }
}

@Composable
private fun RoundRobinJudgeTopBar(
    tournamentName: String,
    playerCount: Int,
    completedMatches: Int,
    totalMatches: Int,
    remainingMatches: Int,
    tablesCount: Int,
    tournamentIsComplete: Boolean,
    searchQuery: String,
    onBack: () -> Unit,
    onSearchClick: () -> Unit,
    onClearSearch: () -> Unit,
    onAddLatecomer: () -> Unit,
    canAddLatecomer: Boolean,
    onFinish: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppBackButton(onClick = onBack)

        Spacer(modifier = Modifier.width(8.dp))

        Text("🏓", fontSize = 24.sp)

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.widthIn(min = 170.dp, max = 260.dp)) {
            Text(
                tournamentName,
                style = AppTypography.titleLarge,
                color = TextDark,
                maxLines = 1
            )
            Text(
                "$playerCount участников • круговой",
                style = AppTypography.bodyMedium,
                color = TextGray,
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        CompactTopChip("Сыграно", "$completedMatches/$totalMatches")
        Spacer(modifier = Modifier.width(6.dp))
        CompactTopChip("Осталось", "$remainingMatches")
        Spacer(modifier = Modifier.width(6.dp))
        CompactTopChip("Столы", "$tablesCount")
        Spacer(modifier = Modifier.width(6.dp))
        CompactTopChip(
            title = "Статус",
            value = if (tournamentIsComplete) "Готово" else "Идёт",
            accent = tournamentIsComplete
        )

        Spacer(modifier = Modifier.weight(1f))

        if (searchQuery.isNotBlank()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = BlueBadgeBg),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.clickable { onClearSearch() }
            ) {
                Text(
                    "Поиск: $searchQuery  ×",
                    color = BlueBadgeText,
                    style = AppTypography.bodyMedium,
                    maxLines = 1,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))
        }

        IconButton(
            onClick = onSearchClick,
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(CardWhite)
                .border(1.dp, BorderGray, RoundedCornerShape(14.dp))
        ) {
            Icon(
                imageVector = SearchIcon,
                contentDescription = "Лупа",
                tint = AppleBlue,
                modifier = Modifier.size(22.dp)
            )
        }

        if (canAddLatecomer) {
            Spacer(modifier = Modifier.width(6.dp))

            OutlinedButton(
                onClick = onAddLatecomer,
                border = BorderStroke(1.dp, AppleBlue),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier.height(44.dp)
            ) {
                Text("+ Опоздавший", color = AppleBlue, maxLines = 1)
            }
        }

        if (tournamentIsComplete) {
            Spacer(modifier = Modifier.width(6.dp))

            Button(
                onClick = onFinish,
                colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.height(44.dp)
            ) {
                Text("🏆 Завершить", color = Color.White, maxLines = 1)
            }
        }
    }
}

@Composable
private fun CompactTopChip(
    title: String,
    value: String,
    accent: Boolean = false
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (accent) GreenBadgeBg else CardWhite
        ),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            1.dp,
            if (accent) GreenBadgeText.copy(alpha = 0.35f) else BorderGray
        )
    ) {
        Column(
            modifier = Modifier
                .widthIn(min = 70.dp)
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(title, color = TextGray, fontSize = 11.sp, maxLines = 1)
            Text(
                value,
                color = if (accent) GreenBadgeText else TextDark,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun CompactActiveTablesPanel(
    assignments: List<Pair<Int, Int>>,
    playerNames: List<String>,
    configuredTablesCount: Int,
    onMatchClick: (Pair<Int, Int>) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(74.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.width(112.dp)) {
                Text(
                    "Кто играет",
                    style = AppTypography.titleLarge,
                    color = TextDark,
                    maxLines = 1
                )
                Text(
                    "$configuredTablesCount стола",
                    style = AppTypography.bodyMedium,
                    color = AppleBlue,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (assignments.isEmpty()) {
                    Text(
                        "Свободных пар пока нет",
                        style = AppTypography.bodyMedium,
                        color = TextGray
                    )
                } else {
                    assignments.forEachIndexed { index, pair ->
                        val firstName = playerNames.getOrElse(pair.first) { "Игрок" }
                        val secondName = playerNames.getOrElse(pair.second) { "Игрок" }

                        Card(
                            modifier = Modifier
                                .width(340.dp)
                                .fillMaxHeight()
                                .clickable { onMatchClick(pair) },
                            colors = CardDefaults.cardColors(containerColor = GreenBadgeBg),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, GreenBadgeText.copy(alpha = 0.35f))
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    "Стол ${index + 1}",
                                    color = TextGray,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                                Text(
                                    "$firstName — $secondName",
                                    style = AppTypography.labelLarge,
                                    color = AppleBlue,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}



@Composable
private fun RoundRobinBottomTools(
    bestOfThreeWins: Boolean,
    tablesCount: Int,
    tableScale: Float,
    onZoomOut: () -> Unit,
    onZoomIn: () -> Unit,
    onResetZoom: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendChip("С1", GreenBadgeBg, GreenBadgeText)
            Spacer(modifier = Modifier.width(6.dp))
            Text("вызван", color = TextGray, style = AppTypography.bodyMedium)

            Spacer(modifier = Modifier.width(16.dp))

            LegendChip("W", GreenBadgeBg, GreenBadgeText)
            Spacer(modifier = Modifier.width(4.dp))
            LegendChip("L", CellLostBg, CellLostText)
            Spacer(modifier = Modifier.width(6.dp))
            Text("0 очков, рейтинг не меняют", color = TextGray, style = AppTypography.bodyMedium)

            Spacer(modifier = Modifier.weight(1f))

            Text(
                if (bestOfThreeWins) "До 3 побед" else "До 2 побед",
                color = AppleBlue,
                style = AppTypography.bodyMedium
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text("столов: $tablesCount", color = AppleBlue, style = AppTypography.bodyMedium)

            Spacer(modifier = Modifier.width(18.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
            ) {
                Text(
                    "−",
                    fontSize = 18.sp,
                    color = AppleBlue,
                    modifier = Modifier
                        .clickable { onZoomOut() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
                Text(
                    "${(tableScale * 100).toInt()}%",
                    color = TextDark,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clickable { onResetZoom() }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                )
                Text(
                    "+",
                    fontSize = 18.sp,
                    color = AppleBlue,
                    modifier = Modifier
                        .clickable { onZoomIn() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun RoundRobinProgressPanel(
    completedMatches: Int,
    totalMatches: Int,
    remainingMatches: Int,
    tablesCount: Int,
    tournamentIsComplete: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RoundInfoBadge(
            title = "Сыграно",
            value = "$completedMatches / $totalMatches",
            modifier = Modifier.weight(1f)
        )
        RoundInfoBadge(
            title = "Осталось",
            value = "$remainingMatches",
            modifier = Modifier.weight(1f)
        )
        RoundInfoBadge(
            title = "Столы",
            value = "$tablesCount",
            modifier = Modifier.weight(1f)
        )
        RoundInfoBadge(
            title = "Статус",
            value = if (tournamentIsComplete) "Готово" else "Игра идёт",
            modifier = Modifier.weight(1.2f),
            accent = tournamentIsComplete
        )
    }
}

@Composable
private fun RoundInfoBadge(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    accent: Boolean = false
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (accent) GreenBadgeBg else CardWhite
        ),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            1.dp,
            if (accent) GreenBadgeText.copy(alpha = 0.35f) else BorderGray
        )
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Text(title, style = AppTypography.bodyMedium, color = TextGray, maxLines = 1)
            Text(
                value,
                style = AppTypography.labelLarge,
                color = if (accent) GreenBadgeText else TextDark,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun RoundRobinLegend(
    bestOfThreeWins: Boolean,
    tablesCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, BorderGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                LegendChip("С1", GreenBadgeBg, GreenBadgeText)
                Spacer(modifier = Modifier.width(8.dp))
                Text("вызван на стол", style = AppTypography.bodyMedium, color = TextGray)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                LegendChip("W", GreenBadgeBg, GreenBadgeText)
                Spacer(modifier = Modifier.width(6.dp))
                LegendChip("L", CellLostBg, CellLostText)
                Spacer(modifier = Modifier.width(8.dp))
                Text("0 очков, рейтинг не меняют", style = AppTypography.bodyMedium, color = TextGray)
            }

            Text(
                buildString {
                    append(if (bestOfThreeWins) "До 3 побед" else "До 2 побед")
                    append(" • столов: $tablesCount")
                },
                style = AppTypography.bodyMedium,
                color = AppleBlue
            )
        }
    }
}

@Composable
private fun LegendChip(
    text: String,
    background: Color,
    foreground: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .padding(horizontal = 9.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = foreground, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun RoundRobinHeader(
    tournamentName: String,
    playerCount: Int,
    canAddLatecomer: Boolean,
    onBack: () -> Unit,
    onAddLatecomer: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppBackButton(onClick = onBack)

        Spacer(modifier = Modifier.width(10.dp))

        Text("🏓", fontSize = 24.sp)

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                tournamentName,
                style = AppTypography.titleLarge,
                color = TextDark,
                maxLines = 1
            )
            Text(
                "$playerCount участников • каждый с каждым",
                style = AppTypography.bodyMedium,
                color = TextGray,
                maxLines = 1
            )
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = BlueBadgeBg),
            shape = RoundedCornerShape(50)
        ) {
            Text(
                "Очки считаются автоматически",
                style = AppTypography.bodyMedium,
                color = BlueBadgeText,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
            )
        }

        if (canAddLatecomer) {
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(
                onClick = onAddLatecomer,
                border = BorderStroke(1.dp, AppleBlue),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("+ Опоздавший", color = AppleBlue)
            }
        }
    }
}

@Composable
private fun ActiveTablesPanel(
    assignments: List<Pair<Int, Int>>,
    playerNames: List<String>,
    configuredTablesCount: Int,
    onMatchClick: (Pair<Int, Int>) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, BorderGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.width(150.dp)) {
                Text(
                    "Кто играет сейчас",
                    style = AppTypography.titleLarge,
                    color = TextDark
                )
                Text(
                    "Настроено столов: $configuredTablesCount",
                    style = AppTypography.bodyMedium,
                    color = AppleBlue
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (assignments.isEmpty()) {
                Text(
                    "Свободных пар пока нет",
                    style = AppTypography.bodyMedium,
                    color = TextGray
                )
            } else {
                assignments.forEachIndexed { index, pair ->
                    Card(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .weight(1f)
                            .clickable { onMatchClick(pair) },
                        colors = CardDefaults.cardColors(containerColor = GreenBadgeBg),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, AppleBlue.copy(alpha = 0.25f))
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                        ) {
                            Text(
                                "Стол ${index + 1}",
                                style = AppTypography.bodyMedium,
                                color = TextGray
                            )
                            Text(
                                "${playerNames.getOrElse(pair.first) { "Игрок" }} — " +
                                    playerNames.getOrElse(pair.second) { "Игрок" },
                                style = AppTypography.labelLarge,
                                color = AppleBlue,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RoundRobinTable(
    playerNames: List<String>,
    scores: Map<Pair<Int, Int>, String>,
    placesByPlayer: Map<Int, Int>,
    pointsByPlayer: Map<Int, Int>,
    activeMatches: List<Pair<Int, Int>>,
    activePlayers: Set<Int>,
    withdrawnPlayers: Set<Int>,
    selectedPair: Pair<Int, Int>?,
    searchQuery: String,
    tableScale: Float,
    onCellClick: (Int, Int) -> Unit,
    onPlayerClick: (Int) -> Unit
) {
    val verticalScroll = rememberScrollState()
    val horizontalScroll = rememberScrollState()

    val scale = tableScale.coerceIn(0.85f, 1.35f)
    val rowHeight = (42f * scale).dp
    val headerHeight = (42f * scale).dp
    val scoreColumnWidth = (58f * scale).dp
    val leftColumnWidth = (205f * scale).coerceIn(176f, 260f).dp
    val pointsColumnWidth = (66f * scale).dp

    val cleanSearch = normalizePlayerName(searchQuery).lowercase()
    val matchedPlayers = if (cleanSearch.isBlank()) {
        emptySet()
    } else {
        playerNames
            .mapIndexedNotNull { index, name ->
                val matchesName = name.lowercase().contains(cleanSearch)
                val matchesNumber = (index + 1).toString() == cleanSearch

                if (matchesName || matchesNumber) index else null
            }
            .toSet()
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(verticalScroll)
    ) {
        Column(
            modifier = Modifier
                .width(leftColumnWidth)
                .background(CardWhite)
        ) {
            Row(
                modifier = Modifier
                    .height(headerHeight)
                    .fillMaxWidth()
                    .background(BgLight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "№",
                    modifier = Modifier.width((42f * scale).dp),
                    textAlign = TextAlign.Center,
                    color = TextGray,
                    fontSize = (12f * scale).sp
                )
                Text(
                    "Игрок",
                    modifier = Modifier.weight(1f),
                    color = TextGray,
                    fontSize = (12f * scale).sp
                )
                Text(
                    "М",
                    modifier = Modifier.width((34f * scale).dp),
                    textAlign = TextAlign.Center,
                    color = TextGray,
                    fontSize = (12f * scale).sp
                )
            }

            playerNames.forEachIndexed { playerIndex, playerName ->
                val isWithdrawn = playerIndex in withdrawnPlayers
                val isActivePlayer = playerIndex in activePlayers
                val isMatched = playerIndex in matchedPlayers

                Row(
                    modifier = Modifier
                        .height(rowHeight)
                        .fillMaxWidth()
                        .background(
                            when {
                                isWithdrawn -> CellLostBg
                                isMatched -> Color(0xFFFFF4CC)
                                isActivePlayer -> GreenBadgeBg
                                else -> CardWhite
                            }
                        )
                        .border(
                            width = when {
                                isMatched -> 2.dp
                                isActivePlayer -> 1.5.dp
                                else -> 0.5.dp
                            },
                            color = when {
                                isMatched -> Color(0xFFFFB300)
                                isActivePlayer -> GreenBadgeText
                                else -> BorderGray
                            }
                        )
                        .clickable { onPlayerClick(playerIndex) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${playerIndex + 1}",
                        modifier = Modifier.width((42f * scale).dp),
                        textAlign = TextAlign.Center,
                        color = TextGray,
                        fontSize = (12f * scale).sp
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = (6f * scale).dp)
                    ) {
                        Text(
                            when {
                                isWithdrawn -> "$playerName  L"
                                isActivePlayer -> "$playerName  • играет"
                                else -> playerName
                            },
                            color = when {
                                isWithdrawn -> CellLostText
                                isActivePlayer -> GreenBadgeText
                                else -> TextDark
                            },
                            fontWeight = if (isWithdrawn || isActivePlayer || isMatched) {
                                FontWeight.SemiBold
                            } else {
                                FontWeight.Normal
                            },
                            maxLines = 1,
                            fontSize = (13f * scale).sp
                        )
                    }

                    Text(
                        "${placesByPlayer[playerIndex] ?: playerIndex + 1}",
                        modifier = Modifier.width((34f * scale).dp),
                        textAlign = TextAlign.Center,
                        color = AppleBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = (13f * scale).sp
                    )
                }
            }
        }

        Row(
            modifier = Modifier.horizontalScroll(horizontalScroll)
        ) {
            playerNames.indices.forEach { opponentIndex ->
                Column(modifier = Modifier.width(scoreColumnWidth)) {
                    Box(
                        modifier = Modifier
                            .height(headerHeight)
                            .fillMaxWidth()
                            .background(
                                if (opponentIndex in matchedPlayers) {
                                    Color(0xFFFFF4CC)
                                } else {
                                    BgLight
                                }
                            )
                            .border(
                                width = if (opponentIndex in matchedPlayers) 2.dp else 0.5.dp,
                                color = if (opponentIndex in matchedPlayers) Color(0xFFFFB300) else BorderGray
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "${opponentIndex + 1}",
                            color = TextDark,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = (13f * scale).sp
                        )
                    }

                    playerNames.indices.forEach { playerIndex ->
                        val isDiagonal = playerIndex == opponentIndex
                        val score = scores[playerIndex to opponentIndex]
                        val parsed = parseScore(score)
                        val normalized = normalizedPair(playerIndex, opponentIndex)
                        val isActive = !isDiagonal && normalized in activeMatches
                        val isSelected = !isDiagonal &&
                            selectedPair?.let {
                                normalizedPair(it.first, it.second) == normalized
                            } == true
                        val isSearchCross = playerIndex in matchedPlayers || opponentIndex in matchedPlayers

                        val technicalWin = score == "W"
                        val technicalLoss = score == "L"
                        val activeTableNumber = activeMatches
                            .indexOf(normalized)
                            .takeIf { it >= 0 }
                            ?.plus(1)

                        val backgroundColor = when {
                            isDiagonal -> Color(0xFF5C6370)
                            technicalWin -> GreenBadgeBg
                            technicalLoss -> CellLostBg
                            parsed != null && parsed.first > parsed.second -> CellWonBg
                            parsed != null -> CellLostBg
                            isActive -> GreenBadgeBg
                            isSearchCross -> Color(0xFFFFFAE6)
                            else -> CardWhite
                        }

                        val scoreColor = when {
                            technicalWin -> GreenBadgeText
                            technicalLoss -> CellLostText
                            parsed != null && parsed.first > parsed.second -> CellWonText
                            parsed != null -> CellLostText
                            isActive -> GreenBadgeText
                            else -> TextGray
                        }

                        val canOpenScore = !isDiagonal && !isTechnicalScore(score)

                        Box(
                            modifier = Modifier
                                .height(rowHeight)
                                .fillMaxWidth()
                                .background(backgroundColor)
                                .border(
                                    width = when {
                                        isSelected -> 2.dp
                                        isActive -> 1.5.dp
                                        isSearchCross -> 1.dp
                                        else -> 0.5.dp
                                    },
                                    color = when {
                                        isSelected -> AppleBlue
                                        isActive -> GreenBadgeText
                                        isSearchCross -> Color(0xFFFFD54F)
                                        else -> BorderGray
                                    }
                                )
                                .then(
                                    if (canOpenScore) {
                                        Modifier.clickable {
                                            onCellClick(playerIndex, opponentIndex)
                                        }
                                    } else {
                                        Modifier
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!isDiagonal) {
                                Text(
                                    text = when {
                                        score != null -> score
                                        isActive -> "С${activeTableNumber ?: ""}"
                                        else -> "–"
                                    },
                                    color = scoreColor,
                                    fontWeight = if (score != null || isActive) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = (13f * scale).sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .width(pointsColumnWidth)
                .background(CardWhite)
        ) {
            Box(
                modifier = Modifier
                    .height(headerHeight)
                    .fillMaxWidth()
                    .background(BgLight)
                    .border(0.5.dp, BorderGray),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Очки",
                    color = TextGray,
                    fontSize = (12f * scale).sp
                )
            }

            playerNames.indices.forEach { playerIndex ->
                Box(
                    modifier = Modifier
                        .height(rowHeight)
                        .fillMaxWidth()
                        .background(
                            when {
                                playerIndex in matchedPlayers -> Color(0xFFFFF4CC)
                                playerIndex in activePlayers -> GreenBadgeBg.copy(alpha = 0.45f)
                                else -> CardWhite
                            }
                        )
                        .border(0.5.dp, BorderGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "${pointsByPlayer[playerIndex] ?: 0}",
                        color = AppleBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = (14f * scale).sp
                    )
                }
            }
        }
    }
}





@Composable
private fun RoundRobinScoreDialog(
    firstPlayer: String,
    secondPlayer: String,
    bestOfThreeWins: Boolean,
    tableNumber: Int?,
    existingScore: String?,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
    onDelete: () -> Unit
) {
    val firstPlayerWins = if (bestOfThreeWins) {
        listOf("3:0", "3:1", "3:2")
    } else {
        listOf("2:0", "2:1")
    }

    val secondPlayerWins = firstPlayerWins.map(::reverseScore)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.88f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Введите счёт",
                            style = AppTypography.titleLarge,
                            color = TextDark
                        )
                        Text(
                            tableNumber?.let { "Стол $it" } ?: "Матч из таблицы",
                            style = AppTypography.bodyMedium,
                            color = AppleBlue
                        )
                    }

                    if (existingScore != null) {
                        LegendChip(existingScore, BlueBadgeBg, AppleBlue)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ScoreEntryPlayerCard(
                        modifier = Modifier.weight(1f),
                        title = "Победил",
                        playerName = firstPlayer,
                        scores = firstPlayerWins,
                        background = CellWonBg,
                        foreground = CellWonText,
                        onSave = onSave
                    )

                    ScoreEntryPlayerCard(
                        modifier = Modifier.weight(1f),
                        title = "Победил",
                        playerName = secondPlayer,
                        scores = secondPlayerWins,
                        background = CellLostBg,
                        foreground = CellLostText,
                        onSave = onSave
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Нажмите на счёт победителя. Рейтинг обновится после завершения турнира.",
                        modifier = Modifier.weight(1f),
                        color = TextGray,
                        style = AppTypography.bodyMedium,
                        maxLines = 2
                    )

                    if (existingScore != null) {
                        TextButton(onClick = onDelete) {
                            Text("Удалить", color = SwipeDeleteRed)
                        }
                    }

                    TextButton(onClick = onDismiss) {
                        Text("Отмена", color = TextGray)
                    }
                }
            }
        }
    }
}







@Composable
private fun ScoreEntryPlayerCard(
    modifier: Modifier = Modifier,
    title: String,
    playerName: String,
    scores: List<String>,
    background: Color,
    foreground: Color,
    onSave: (String) -> Unit
) {
    Card(
        modifier = modifier.fillMaxHeight(),
        colors = CardDefaults.cardColors(containerColor = background),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, foreground.copy(alpha = 0.20f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
        ) {
            Text(title, color = TextGray, style = AppTypography.bodyMedium)
            Text(
                playerName,
                color = foreground,
                style = AppTypography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 2
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                scores.forEach { score ->
                    Button(
                        onClick = { onSave(score) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(58.dp)
                    ) {
                        Text(
                            score,
                            color = foreground,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactScoreWinnerBlock(
    title: String,
    scores: List<String>,
    background: Color,
    foreground: Color,
    modifier: Modifier = Modifier,
    onSave: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = background),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                title,
                style = AppTypography.bodyMedium,
                color = foreground,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                scores.forEach { score ->
                    Button(
                        onClick = { onSave(score) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            score,
                            color = foreground,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreWinnerBlock(
    title: String,
    scores: List<String>,
    background: Color,
    foreground: Color,
    onSave: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = background),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, style = AppTypography.labelLarge, color = foreground)
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                scores.forEach { score ->
                    ScoreBtn(
                        text = score,
                        bgColor = Color.White,
                        textColor = foreground,
                        modifier = Modifier.weight(1f)
                    ) {
                        onSave(score)
                    }
                }
            }
        }
    }
}

// ==========================================
// 4. ЭКРАН ИТОГОВ
// ==========================================

@Composable
fun FinalResultsScreen(draft: TournamentDraft, onGoHome: () -> Unit) {
    val context = LocalContext.current
    val playerNames = draft.playerFields.mapIndexed { index, field ->
        field.name.trim().ifBlank { "Игрок ${index + 1}" }
    }
    val standings = calculateRoundRobinStandings(
        playerCount = playerNames.size,
        scores = draft.matchScores
    )

    val shareText = buildString {
        appendLine("🏓 ${draft.name.ifBlank { "Теннисный турнир" }}")
        appendLine("Итоги турнира:")
        standings.forEachIndexed { place, stat ->
            appendLine("${place + 1}. ${playerNames.getOrElse(stat.index) { "Игрок" }} — ${stat.points} очк.")
        }

        if (draft.ratingChanges.isNotEmpty()) {
            appendLine()
            appendLine("Изменения рейтинга:")
            draft.ratingChanges.forEach { change ->
                val delta = change.newRating - change.oldRating
                appendLine("${change.fullName}: ${formatRating(change.oldRating)} → ${formatRating(change.newRating)} (${formatDelta(delta)})")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgLight)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🎉", fontSize = 30.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Турнир завершён",
                    style = AppTypography.displayLarge,
                    color = TextDark,
                    maxLines = 1
                )
                Text(
                    "${draft.name.ifBlank { "Теннисный турнир" }} • сохранено в историю",
                    style = AppTypography.bodyMedium,
                    color = GreenBadgeText,
                    maxLines = 1
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PodiumPlaceCard(
                    place = 1,
                    medal = "🏆",
                    playerName = standings.getOrNull(0)?.let { playerNames.getOrElse(it.index) { "Игрок" } } ?: "—",
                    points = standings.getOrNull(0)?.points ?: 0,
                    background = GoldBg,
                    border = GoldBorder,
                    modifier = Modifier.weight(1.15f)
                )

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PodiumPlaceCard(
                        place = 2,
                        medal = "🥈",
                        playerName = standings.getOrNull(1)?.let { playerNames.getOrElse(it.index) { "Игрок" } } ?: "—",
                        points = standings.getOrNull(1)?.points ?: 0,
                        background = SilverBg,
                        border = SilverBorder,
                        modifier = Modifier.weight(1f)
                    )

                    PodiumPlaceCard(
                        place = 3,
                        medal = "🥉",
                        playerName = standings.getOrNull(2)?.let { playerNames.getOrElse(it.index) { "Игрок" } } ?: "—",
                        points = standings.getOrNull(2)?.points ?: 0,
                        background = BronzeBg,
                        border = BronzeBorder,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, BorderGray)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp)
                ) {
                    Text("Все места", style = AppTypography.titleLarge, color = TextDark)
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        standings.forEachIndexed { place, stat ->
                            val playerName = playerNames.getOrElse(stat.index) { "Игрок" }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "${place + 1}",
                                    modifier = Modifier.width(32.dp),
                                    color = AppleBlue,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    playerName,
                                    modifier = Modifier.weight(1f),
                                    color = TextDark,
                                    maxLines = 1
                                )
                                Text(
                                    "${stat.points}",
                                    color = TextGray,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            if (place != standings.lastIndex) {
                                HorizontalDivider(color = BorderGray)
                            }
                        }
                    }
                }
            }

            Card(
                modifier = Modifier
                    .weight(1.05f)
                    .fillMaxHeight(),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, BorderGray)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp)
                ) {
                    Text("Рейтинг", style = AppTypography.titleLarge, color = TextDark)
                    Text("Изменения после турнира", style = AppTypography.bodyMedium, color = TextGray)
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        if (draft.ratingChanges.isEmpty()) {
                            Text(
                                "Рейтинг не изменился: нет сыгранных матчей с обычным счётом.",
                                style = AppTypography.bodyMedium,
                                color = TextGray
                            )
                        } else {
                            draft.ratingChanges.forEach { change ->
                                val delta = change.newRating - change.oldRating

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            change.fullName,
                                            color = TextDark,
                                            maxLines = 1
                                        )
                                        Text(
                                            "${formatRating(change.oldRating)} → ${formatRating(change.newRating)}",
                                            color = TextGray,
                                            style = AppTypography.bodyMedium
                                        )
                                    }

                                    Text(
                                        "${formatDelta(delta)}",
                                        color = if (delta >= 0) GreenBadgeText else CellLostText,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                HorizontalDivider(color = BorderGray)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareText)
                    }
                    context.startActivity(
                        Intent.createChooser(intent, "Поделиться результатами")
                    )
                },
                modifier = Modifier.weight(1f),
                border = BorderStroke(1.dp, AppleBlue),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("↗ Поделиться", color = AppleBlue)
            }

            Button(
                onClick = onGoHome,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("✓ На главную", color = Color.White)
            }
        }
    }
}



// ==========================================
// 5. ЭКРАН ПЛЕЙ-ОФФ И ГРУПП (НОВЫЙ!)
// ==========================================
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PodiumPlaceCard(
    place: Int,
    medal: String,
    playerName: String,
    points: Int,
    background: Color,
    border: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = background),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.5.dp, border)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                medal,
                fontSize = if (place == 1) 46.sp else 34.sp,
                modifier = Modifier.width(if (place == 1) 70.dp else 52.dp),
                textAlign = TextAlign.Center
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "$place место",
                    style = AppTypography.bodyMedium,
                    color = TextGray
                )
                Text(
                    playerName,
                    style = AppTypography.titleLarge,
                    color = TextDark,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )
                Text(
                    "$points очков",
                    style = AppTypography.labelLarge,
                    color = AppleBlue
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayoffScreen(draft: TournamentDraft, onBack: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().background(BgLight)) {
        // Шапка и Вкладки (Tabs)
        Row(
            modifier = Modifier.fillMaxWidth().background(CardWhite).padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("<", fontSize = 24.sp, color = TextDark, modifier = Modifier.clickable { onBack() }.padding(end = 16.dp))

            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = CardWhite,
                contentColor = AppleBlue,
                modifier = Modifier.weight(1f).padding(horizontal = 32.dp),
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = AppleBlue,
                        height = 3.dp
                    )
                }
            ) {
                Tab(
                    selected = pagerState.currentPage == 0,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(0) } },
                    text = { Text("Группы", style = AppTypography.titleLarge, color = if(pagerState.currentPage == 0) AppleBlue else TextGray) }
                )
                Tab(
                    selected = pagerState.currentPage == 1,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } },
                    text = { Text("Сетка Плей-офф", style = AppTypography.titleLarge, color = if(pagerState.currentPage == 1) AppleBlue else TextGray) }
                )
            }
        }

        // Свайп-панели (HorizontalPager)
        HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
            if (page == 0) {
                GroupsTabContent(draft)
            } else {
                PlayoffTreeContent(draft)
            }
        }
    }
}

@Composable
fun GroupsTabContent(draft: TournamentDraft) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Здесь будет 4 мини-таблицы для Групп А, B, C, D.", style = AppTypography.titleLarge, color = TextDark)
            Text("Сделайте свайп влево, чтобы перейти к Сетке 👉", style = AppTypography.bodyMedium, color = TextGray)
        }
    }
}

@Composable
fun PlayoffTreeContent(draft: TournamentDraft) {
    // Состояния для Паннинга и Зумирования
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var selectedMatch by remember { mutableStateOf<PlayoffMatch?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
            .pointerInput(Unit) {
                // Обработка жестов: Перетаскивание и Щипок
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.5f, 2.5f) // Ограничиваем зум
                    offset += pan
                }
            }
    ) {
        // Контейнер, который физически масштабируется и двигается
        Box(
            modifier = Modifier
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
                .padding(64.dp)
        ) {
            // Рисуем классическое Дерево (Сетку)
            Row(horizontalArrangement = Arrangement.spacedBy(64.dp), verticalAlignment = Alignment.CenterVertically) {

                // 1/4 Финала
                Column(verticalArrangement = Arrangement.spacedBy(32.dp)) {
                    BracketMatchCard(draft, PlayoffMatch(1, "Иванов", "Морозов")) { selectedMatch = it }
                    BracketMatchCard(draft, PlayoffMatch(2, "Петров", "Попов")) { selectedMatch = it }
                    BracketMatchCard(draft, PlayoffMatch(3, "Сидоров", "Кузнецов")) { selectedMatch = it }
                    BracketMatchCard(draft, PlayoffMatch(4, "Васильев", "Смирнов")) { selectedMatch = it }
                }

                // 1/2 Финала (Полуфинал)
                Column(verticalArrangement = Arrangement.spacedBy(130.dp)) {
                    BracketMatchCard(draft, PlayoffMatch(5, "?", "?")) { selectedMatch = it }
                    BracketMatchCard(draft, PlayoffMatch(6, "?", "?")) { selectedMatch = it }
                }

                // Финал
                Column {
                    BracketMatchCard(draft, PlayoffMatch(7, "?", "?", isFinal = true)) { selectedMatch = it }
                }
            }
        }
    }

    // Всплывающее окно быстрого ввода счета (Quick Score)
    if (selectedMatch != null) {
        Dialog(onDismissRequest = { selectedMatch = null }) {
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = CardWhite), modifier = Modifier.padding(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(if (selectedMatch!!.isFinal) "Финал" else "Матч Плей-офф", style = AppTypography.titleLarge, color = TextDark)
                    Text("${selectedMatch!!.p1} vs ${selectedMatch!!.p2}", style = AppTypography.bodyMedium, color = TextGray)
                    Spacer(modifier = Modifier.height(16.dp))

                    fun saveScore(score: String) {
                        draft.playoffScores[selectedMatch!!.id] = score
                        // В реальном приложении здесь победитель копируется в следующий матч
                        selectedMatch = null
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            ScoreBtn("3:0", CellWonBg, CellWonText) { saveScore("3:0") }
                            ScoreBtn("3:1", CellWonBg, CellWonText) { saveScore("3:1") }
                            ScoreBtn("3:2", CellWonBg, CellWonText) { saveScore("3:2") }
                        }
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            ScoreBtn("0:3", CellLostBg, CellLostText) { saveScore("0:3") }
                            ScoreBtn("1:3", CellLostBg, CellLostText) { saveScore("1:3") }
                            ScoreBtn("2:3", CellLostBg, CellLostText) { saveScore("2:3") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BracketMatchCard(draft: TournamentDraft, match: PlayoffMatch, onClick: (PlayoffMatch) -> Unit) {
    val score = draft.playoffScores[match.id] ?: "-"
    val scoreP1 = if (score.contains(":")) score.split(":")[0] else "-"
    val scoreP2 = if (score.contains(":")) score.split(":")[1] else "-"

    Card(
        modifier = Modifier.width(180.dp).clickable { onClick(match) }.border(if (match.isFinal) 2.dp else 1.dp, if (match.isFinal) GoldBorder else BorderGray, RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(match.p1, style = AppTypography.labelLarge, color = TextDark)
                Text(scoreP1, color = if (scoreP1 > scoreP2) AppleBlue else TextGray, fontWeight = FontWeight.Bold)
            }
            HorizontalDivider(color = BorderGray, thickness = 1.dp)
            Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(match.p2, style = AppTypography.labelLarge, color = TextDark)
                Text(scoreP2, color = if (scoreP2 > scoreP1) AppleBlue else TextGray, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ScoreBtn(text: String, bgColor: Color, textColor: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(onClick = onClick, colors = ButtonDefaults.buttonColors(containerColor = bgColor), shape = RoundedCornerShape(8.dp), modifier = modifier.fillMaxWidth().height(48.dp)) {
        Text(text, color = textColor, style = AppTypography.labelLarge)
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun LightAppPreview() {
    MaterialTheme(typography = AppTypography) {
        Surface(modifier = Modifier.fillMaxSize(), color = BgLight) {
            TennisApp()
        }
    }
}