package com.example.tennistourney

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.combinedClickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import android.widget.Toast
import android.util.Base64
import java.io.ByteArrayOutputStream
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

private var _MoreIcon: ImageVector? = null
val MoreIcon: ImageVector
    get() {
        if (_MoreIcon != null) return _MoreIcon!!

        _MoreIcon = ImageVector.Builder(
            name = "MoreIcon",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(TextDark)) {
                moveTo(12f, 8f)
                curveTo(13.1f, 8f, 14f, 7.1f, 14f, 6f)
                curveTo(14f, 4.9f, 13.1f, 4f, 12f, 4f)
                curveTo(10.9f, 4f, 10f, 4.9f, 10f, 6f)
                curveTo(10f, 7.1f, 10.9f, 8f, 12f, 8f)
                close()
                moveTo(12f, 10f)
                curveTo(10.9f, 10f, 10f, 10.9f, 10f, 12f)
                curveTo(10f, 13.1f, 10.9f, 14f, 12f, 14f)
                curveTo(13.1f, 14f, 14f, 13.1f, 14f, 12f)
                curveTo(14f, 10.9f, 13.1f, 10f, 12f, 10f)
                close()
                moveTo(12f, 16f)
                curveTo(10.9f, 16f, 10f, 16.9f, 10f, 18f)
                curveTo(10f, 19.1f, 10.9f, 20f, 12f, 20f)
                curveTo(13.1f, 20f, 14f, 19.1f, 14f, 18f)
                curveTo(14f, 16.9f, 13.1f, 16f, 12f, 16f)
                close()
            }
        }.build()

        return _MoreIcon!!
    }

private var _TableIcon: ImageVector? = null
val TableIcon: ImageVector
    get() {
        if (_TableIcon != null) return _TableIcon!!
        _TableIcon = ImageVector.Builder(
            name = "TableIcon",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(AppleBlue)) {
                moveTo(19f, 5f)
                horizontalLineTo(5f)
                curveTo(3.9f, 5f, 3f, 5.9f, 3f, 7f)
                verticalLineTo(17f)
                curveTo(3f, 18.1f, 3.9f, 19f, 5f, 19f)
                horizontalLineTo(19f)
                curveTo(20.1f, 19f, 21f, 18.1f, 21f, 17f)
                verticalLineTo(7f)
                curveTo(21f, 5.9f, 20.1f, 5f, 19f, 5f)
                close()
                moveTo(19f, 7f)
                verticalLineTo(9f)
                horizontalLineTo(5f)
                verticalLineTo(7f)
                horizontalLineTo(19f)
                close()
                moveTo(5f, 17f)
                verticalLineTo(11f)
                horizontalLineTo(19f)
                verticalLineTo(17f)
                horizontalLineTo(5f)
                close()
            }
        }.build()
        return _TableIcon!!
    }

private var _CheckCircleIcon: ImageVector? = null
val CheckCircleIcon: ImageVector
    get() {
        if (_CheckCircleIcon != null) return _CheckCircleIcon!!
        _CheckCircleIcon = ImageVector.Builder(
            name = "CheckCircleIcon",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(AppleBlue)) {
                moveTo(12f, 2f)
                curveTo(6.48f, 2f, 2f, 6.48f, 2f, 12f)
                curveTo(2f, 17.52f, 6.48f, 22f, 12f, 22f)
                curveTo(17.52f, 22f, 22f, 17.52f, 22f, 12f)
                curveTo(22f, 6.48f, 17.52f, 2f, 12f, 2f)
                close()
                moveTo(10f, 17f)
                lineTo(5f, 12f)
                lineTo(6.41f, 10.59f)
                lineTo(10f, 14.17f)
                lineTo(17.59f, 6.58f)
                lineTo(19f, 8f)
                lineTo(10f, 17f)
                close()
            }
        }.build()
        return _CheckCircleIcon!!
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
data class MatchRatingImpact(val player1: String, val player2: String, val score: String, val p1Delta: Double, val p2Delta: Double)
data class RatingApplyResult(val players: List<ClubPlayer>, val changes: List<RatingChange>, val matchImpacts: List<MatchRatingImpact> = emptyList())
data class TournamentHistoryEntry(
    val id: Long,
    val name: String,
    val dateText: String,
    val playersCount: Int,
    val winnerName: String,
    val standingsText: String,
    val ratingText: String,
    val structuredMatches: String = "",
    val structuredRatings: String = ""
)

data class PlayerStat(val index: Int, val name: String, val points: Int)
data class PlayoffMatch(val id: Int, val p1: String, val p2: String, val isFinal: Boolean = false)
data class ActiveTableMatch(val tableNumber: Int, val player1: Int, val player2: Int)
data class TieStat(val playerIndex: Int, val miniPoints: Int, val setRatio: Double)


data class ClubConfig(
    val clubId: String,
    val displayName: String,
    val adminPin: String,
    val secretWord: String
)

data class AppBackupData(
    val version: Int = 1,
    val clubs: List<ClubBackupEntry>
)

data class ClubBackupEntry(
    val config: ClubConfig,
    val players: List<ClubPlayer>,
    val history: List<TournamentHistoryEntry>
)

private fun exportBackupToUri(context: Context, uri: android.net.Uri) {
    try {
        val clubsConfigs = loadClubsRegistry(context)
        val backupEntries = clubsConfigs.map { config ->
            ClubBackupEntry(
                config = config,
                players = loadClubPlayers(context, config.clubId),
                history = loadTournamentHistory(context, config.clubId)
            )
        }
        val backupData = AppBackupData(version = 1, clubs = backupEntries)
        val json = Gson().toJson(backupData)

        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            OutputStreamWriter(outputStream, "UTF-8").use { writer ->
                writer.write(json)
            }
        }
        Toast.makeText(context, "Резервная копия успешно экспортирована", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Ошибка при экспорте: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private fun importBackupFromUri(context: Context, uri: android.net.Uri, onImportSuccess: () -> Unit) {
    try {
        val json = context.contentResolver.openInputStream(uri)?.use { inputStream ->
            InputStreamReader(inputStream, "UTF-8").use { reader ->
                reader.readText()
            }
        } ?: throw Exception("Не удалось прочитать файл")

        val backupData = Gson().fromJson(json, AppBackupData::class.java)
        if (backupData == null || backupData.clubs.isEmpty()) {
            throw Exception("Неверный формат файла резервной копии")
        }

        val existingClubs = loadClubsRegistry(context).toMutableList()

        for (entry in backupData.clubs) {
            val config = entry.config
            val clubId = config.clubId

            val existingIdx = existingClubs.indexOfFirst { it.clubId == clubId }
            if (existingIdx != -1) {
                existingClubs[existingIdx] = config
            } else {
                existingClubs.add(config)
            }

            saveClubPlayers(context, clubId, entry.players)
            saveTournamentHistory(context, clubId, entry.history)
            addSavedClub(context, clubId)
        }

        saveClubsRegistry(context, existingClubs)

        Toast.makeText(context, "Резервная копия успешно импортирована", Toast.LENGTH_SHORT).show()
        onImportSuccess()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Ошибка при импорте: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

private const val CLUBS_REGISTRY_PREFS = "clubs_registry_storage"
private const val CLUBS_REGISTRY_KEY = "clubs_list"
private const val LAST_CLUB_PREFS = "app_global_prefs"
private const val LAST_CLUB_KEY = "last_used_club"

private fun loadClubsRegistry(context: Context): List<ClubConfig> {
    val saved = context.getSharedPreferences(CLUBS_REGISTRY_PREFS, Context.MODE_PRIVATE)
        .getString(CLUBS_REGISTRY_KEY, "") ?: ""
    if (saved.isBlank()) return emptyList()

    return saved.split(";;").mapNotNull {
        val parts = it.split("|")
        if (parts.size >= 4) {
            ClubConfig(parts[0], safeDecode(parts[1]), parts[2], safeDecode(parts[3]))
        } else null
    }
}

private fun saveClubsRegistry(context: Context, clubs: List<ClubConfig>) {
    val encoded = clubs.joinToString(";;") {
        "${it.clubId}|${safeEncode(it.displayName)}|${it.adminPin}|${safeEncode(it.secretWord)}"
    }
    context.getSharedPreferences(CLUBS_REGISTRY_PREFS, Context.MODE_PRIVATE)
        .edit()
        .putString(CLUBS_REGISTRY_KEY, encoded)
        .apply()
}

private fun getLastClubId(context: Context): String? {
    return context.getSharedPreferences(LAST_CLUB_PREFS, Context.MODE_PRIVATE)
        .getString(LAST_CLUB_KEY, null)
}

private fun setLastClubId(context: Context, clubId: String?) {
    context.getSharedPreferences(LAST_CLUB_PREFS, Context.MODE_PRIVATE)
        .edit()
        .putString(LAST_CLUB_KEY, clubId)
        .apply()
}

// Prefix for isolating club data
private fun getPlayersPrefsName(clubId: String) = "${PLAYERS_PREFS_NAME}_$clubId"
private fun getHistoryPrefsName(clubId: String) = "${HISTORY_PREFS_NAME}_$clubId"
private fun getDraftPrefsName(clubId: String) = "tournament_draft_storage_$clubId"

fun getSavedClubs(context: Context): Set<String> {
    val prefs = context.getSharedPreferences("tennis_app", Context.MODE_PRIVATE)
    return prefs.getStringSet("saved_clubs", emptySet()) ?: emptySet()
}

fun addSavedClub(context: Context, clubId: String) {
    val prefs = context.getSharedPreferences("tennis_app", Context.MODE_PRIVATE)
    val set = prefs.getStringSet("saved_clubs", emptySet())?.toMutableSet() ?: mutableSetOf()
    set.add(clubId)
    prefs.edit().putStringSet("saved_clubs", set).apply()
}

fun removeSavedClub(context: Context, clubId: String) {
    val prefs = context.getSharedPreferences("tennis_app", Context.MODE_PRIVATE)
    val set = prefs.getStringSet("saved_clubs", emptySet())?.toMutableSet() ?: mutableSetOf()
    set.remove(clubId)
    prefs.edit().putStringSet("saved_clubs", set).apply()
}

fun isClubAdmin(context: Context, clubId: String): Boolean {
    val prefs = context.getSharedPreferences("clubs_registry_storage", Context.MODE_PRIVATE)
    return prefs.getBoolean("admin_$clubId", false)
}

fun setClubAdmin(context: Context, clubId: String, isAdmin: Boolean) {
    val prefs = context.getSharedPreferences("clubs_registry_storage", Context.MODE_PRIVATE)
    prefs.edit().putBoolean("admin_$clubId", isAdmin).apply()
}


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

private fun getClubPlayerByName(name: String, clubPlayers: List<ClubPlayer>): ClubPlayer? {
    val cleanName = normalizePlayerName(name).lowercase()
    return clubPlayers.find { it.fullName.lowercase() == cleanName }
}

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
    clubId: String,
    bitmap: Bitmap,
    playerId: Int
): String {
    return try {
        val outputStream = ByteArrayOutputStream()
        val scaled = if (bitmap.width > 200 || bitmap.height > 200) {
            val scale = 200.0 / kotlin.math.max(bitmap.width, bitmap.height)
            Bitmap.createScaledBitmap(bitmap, (bitmap.width * scale).toInt(), (bitmap.height * scale).toInt(), true)
        } else {
            bitmap
        }
        scaled.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val bytes = outputStream.toByteArray()
        val base64 = "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP)
        
        val placeholder = "db_avatar_$playerId"
        avatarMemoryCache[placeholder] = scaled
        
        if (clubId.isNotBlank()) {
            FirebaseStorage.savePlayerAvatar(clubId, playerId, base64)
        }
        
        placeholder
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

private fun saveUriAvatar(
    context: Context,
    clubId: String,
    uri: Uri,
    playerId: Int
): String {
    return try {
        context.contentResolver.openInputStream(uri)?.use { input ->
            val bitmap = BitmapFactory.decodeStream(input)
            if (bitmap != null) {
                saveBitmapAvatar(context, clubId, bitmap, playerId)
            } else ""
        } ?: ""
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

@Composable
private fun rememberAvatarBitmap(uriOrPath: String): Bitmap? {
    val context = LocalContext.current
    val clubId = LocalCurrentClubId.current ?: ""

    return remember(uriOrPath, clubId) {
        if (uriOrPath.isBlank()) {
            null
        } else if (uriOrPath.startsWith("db_avatar_")) {
            val cached = avatarMemoryCache[uriOrPath]
            if (cached != null) {
                cached
            } else {
                val playerId = uriOrPath.substringAfter("db_avatar_").toIntOrNull()
                if (playerId != null && clubId.isNotBlank()) {
                    FirebaseStorage.fetchPlayerAvatar(clubId, playerId) { base64 ->
                        if (base64.isNotBlank()) {
                            try {
                                val base64Data = base64.substringAfter("base64,")
                                val bytes = Base64.decode(base64Data, Base64.DEFAULT)
                                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                if (bitmap != null) {
                                    avatarMemoryCache[uriOrPath] = bitmap
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
                null
            }
        } else {
            try {
                if (uriOrPath.startsWith("data:image/jpeg;base64,")) {
                    val base64Data = uriOrPath.substringAfter("base64,")
                    val bytes = Base64.decode(base64Data, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                } else if (uriOrPath.startsWith("/") || uriOrPath.startsWith("file:")) {
                    BitmapFactory.decodeFile(uriOrPath.removePrefix("file://"))
                } else {
                    context.contentResolver.openInputStream(Uri.parse(uriOrPath)).use { input ->
                        BitmapFactory.decodeStream(input)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}

private fun loadClubPlayers(context: Context, clubId: String): List<ClubPlayer> {
    val saved = context
        .getSharedPreferences("${PLAYERS_PREFS_NAME}_$clubId", Context.MODE_PRIVATE)
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
    clubId: String,
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
        .getSharedPreferences("${PLAYERS_PREFS_NAME}_$clubId", Context.MODE_PRIVATE)
        .edit()
        .putString(PLAYERS_PREFS_KEY, encoded)
        .apply()

    FirebaseStorage.savePlayers(clubId, players)
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

private fun loadTournamentHistory(context: Context, clubId: String): List<TournamentHistoryEntry> {
    val saved = context
        .getSharedPreferences("${HISTORY_PREFS_NAME}_$clubId", Context.MODE_PRIVATE)
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
                ratingText = decodeHistoryField(parts[6]),
                structuredMatches = parts.getOrElse(7) { "" },
                structuredRatings = parts.getOrElse(8) { "" }
            )
        }
        .sortedByDescending { it.id }
}

private fun saveTournamentHistory(
    context: Context,
    clubId: String,
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
                encodeHistoryField(entry.ratingText),
                entry.structuredMatches,
                entry.structuredRatings
            ).joinToString(HISTORY_FIELD_SEPARATOR)
        }

    context
        .getSharedPreferences("${HISTORY_PREFS_NAME}_$clubId", Context.MODE_PRIVATE)
        .edit()
        .putString(HISTORY_PREFS_KEY, encoded)
        .apply()

    FirebaseStorage.saveHistory(clubId, history)
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
            val medal = if (place == 0) "🥇 " else if (place == 1) "🥈 " else if (place == 2) "🥉 " else ""
            "${place + 1}. ${medal}${playerNames.getOrElse(stat.index) { "Игрок" }}"
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

    val structuredMatches = draft.matchImpacts.joinToString(";;") { impact ->
        listOf(
            safeEncode(impact.player1),
            safeEncode(impact.player2),
            impact.score,
            formatRatingForStorage(impact.p1Delta),
            formatRatingForStorage(impact.p2Delta)
        ).joinToString("::")
    }

    val structuredRatings = draft.ratingChanges.joinToString(";;") { change ->
        listOf(
            safeEncode(change.fullName),
            formatRatingForStorage(change.oldRating),
            formatRatingForStorage(change.newRating),
            formatRatingForStorage(change.newRating - change.oldRating)
        ).joinToString("::")
    }

    return TournamentHistoryEntry(
        id = System.currentTimeMillis(),
        name = draft.name.ifBlank { "Теннисный турнир" },
        dateText = dateText,
        playersCount = playerNames.size,
        winnerName = winnerName,
        standingsText = standingsText,
        ratingText = ratingText,
        structuredMatches = structuredMatches,
        structuredRatings = structuredRatings
    )
}

private fun scoreDominanceCoefficient(score: Pair<Int, Int>): Double {
    val setDifference = kotlin.math.abs(score.first - score.second)

    return when {
        setDifference <= 1 -> 0.8
        setDifference == 2 -> 1.0
        else -> 1.2 // 3 or more
    }
}

private fun calculateTournamentK(averageRating: Double): Double =
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
    if (winnerRating - loserRating >= 100.0) return 0.0
    val raw = (100.0 - (winnerRating - loserRating)) / 10.0
    return raw.coerceAtLeast(0.0)
}

private fun applyRoundRobinRatings(
    existingPlayers: List<ClubPlayer>,
    playerNames: List<String>,
    scores: Map<Pair<Int, Int>, String>,
    fixedK: Double,
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

    val rttfK = fixedK

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

    val matchImpacts = mutableListOf<MatchRatingImpact>()

    for (first in playerNames.indices) {
        for (second in first + 1 until playerNames.size) {
            val scoreStr = scores[first to second]
            val result = parseScore(scoreStr) ?: continue
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
                matchImpacts.add(MatchRatingImpact(winnerName, loserName, scoreStr ?: "0:0", 0.0, 0.0))
                continue
            }

            val scoreCoefficient = scoreDominanceCoefficient(result)

            val winnerIsNovice = winner.tournamentsPlayed <= 5
            val loserIsNovice = loser.tournamentsPlayed <= 5

            val winnerDelta = if (winnerIsNovice) {
                roundRating(baseDelta * 1.0 * 1.0)
            } else {
                roundRating(baseDelta * rttfK * scoreCoefficient)
            }

            val loserDelta = if (loserIsNovice) {
                roundRating(baseDelta * 0.5 * 1.0)
            } else {
                roundRating(baseDelta * rttfK * scoreCoefficient)
            }

            accumulatedDeltas[winner.fullName.lowercase()] =
                (accumulatedDeltas[winner.fullName.lowercase()] ?: 0.0) + winnerDelta

            accumulatedDeltas[loser.fullName.lowercase()] =
                (accumulatedDeltas[loser.fullName.lowercase()] ?: 0.0) - loserDelta

            matchImpacts.add(
                MatchRatingImpact(
                    player1 = winnerName,
                    player2 = loserName,
                    score = if (firstWon) (scoreStr ?: "0:0") else reverseScore(scoreStr ?: "0:0"),
                    p1Delta = winnerDelta,
                    p2Delta = -loserDelta
                )
            )
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
        changes = changes,
        matchImpacts = matchImpacts
    )
}

/**
 * Заглушка нужна только чтобы переиспользовать функцию rememberPlayersFromTournament
 * без сохранения в память. Внутри функция context не использует.
 */
private class FakeContextForPureRating : ContextWrapper(null)


class TournamentDraft {
    var name by mutableStateOf("")
    var playersCount by mutableStateOf(2)
    var winsToWin by mutableStateOf(2)
    var tablesCount by mutableStateOf(1)
    var fixedK by mutableStateOf(0.2)
    var isListGenerated by mutableStateOf(false)
    var nextFieldId by mutableStateOf(0)
    val playerFields = mutableStateListOf<PlayerField>()

    // Состояние Кругового турнира
    val matchScores = mutableStateMapOf<Pair<Int, Int>, String>()
    val activeRoundRobinMatches = mutableStateListOf<Pair<Int, Int>?>()
    val withdrawnPlayers = mutableStateListOf<Int>()
    val lastFinishedPlayers = mutableStateListOf<Int>()

    // Изменение рейтинга после завершения турнира
    var ratingApplied by mutableStateOf(false)
    var historySaved by mutableStateOf(false)
    var bonusesAwarded by mutableStateOf(false)
    val ratingChanges = mutableStateListOf<RatingChange>()
    val matchImpacts = mutableStateListOf<MatchRatingImpact>()

    // Состояние Сетки Плей-офф
    val playoffScores = mutableStateMapOf<Int, String>()
}

private fun tournamentStartError(
    draft: TournamentDraft,
    clubPlayers: List<ClubPlayer>
): String? {
    if (normalizePlayerName(draft.name).isBlank()) {
        return "Введите название турнира."
    }

    if (clubPlayers.isEmpty()) {
        return "Сначала зарегистрируйте игроков в базе. Без регистрации игрок не может участвовать."
    }

    if (draft.playerFields.size < 2) {
        return "Недостаточно игроков для начала турнира."
    }

    val playerNames = draft.playerFields.map { normalizePlayerName(it.name) }

    if (playerNames.any { it.isBlank() }) {
        return "Выберите всех игроков из базы."
    }

    val registeredNames = clubPlayers
        .map { it.fullName.lowercase() }
        .toSet()

    val notRegistered = playerNames.firstOrNull { it.lowercase() !in registeredNames }
    if (notRegistered != null) {
        return "Игрока «$notRegistered» нет в базе. Сначала зарегистрируйте его."
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

enum class AppScreen { Dashboard, PlayerBase, History, CreateTournament, RoundRobin, Playoff, FinalResults, ClubSelection, ClubLogin, AdminLogin }

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
        AppScreen.CreateTournament,
        AppScreen.ClubSelection,
        AppScreen.ClubLogin,
        AppScreen.AdminLogin -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

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
        enableEdgeToEdge()
        setContent {
            MaterialTheme(typography = AppTypography) {
                Surface(modifier = Modifier.fillMaxSize(), color = BgLight) {
                    Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
                        TennisApp()
                    }
                }
            }
        }
    }
}

val LocalIsAdmin = staticCompositionLocalOf { false }
val LocalSetIsAdmin = staticCompositionLocalOf<(Boolean) -> Unit> { {} }
val LocalCurrentClubId = staticCompositionLocalOf<String?> { null }
val LocalCurrentClubName = staticCompositionLocalOf<String> { "" }
val LocalSaveDraft = staticCompositionLocalOf<() -> Unit> { {} }
val avatarMemoryCache = androidx.compose.runtime.mutableStateMapOf<String, android.graphics.Bitmap>()

@Composable
fun TennisApp() {
    val context = LocalContext.current
    val draft = remember { TournamentDraft() }

    val initialClubs = remember { getSavedClubs(context) }
    val initialHasAdmin = remember { initialClubs.any { isClubAdmin(context, it) } }

    var currentClubId by remember {
        val defaultClub = if (initialClubs.size == 1) initialClubs.first() else null
        mutableStateOf(defaultClub)
    }

    var currentScreen by remember {
        val startScreen = when {
            initialClubs.isEmpty() -> AppScreen.ClubLogin
            initialClubs.size == 1 -> {
                if (initialHasAdmin) AppScreen.AdminLogin else AppScreen.Dashboard
            }
            else -> AppScreen.ClubSelection
        }
        mutableStateOf(startScreen)
    }

    var clubPlayers by remember { mutableStateOf<List<ClubPlayer>>(emptyList()) }
    var tournamentHistory by remember { mutableStateOf<List<TournamentHistoryEntry>>(emptyList()) }
    var isAdmin by remember { mutableStateOf(false) }

    DisposableEffect(currentClubId) {
        if (currentClubId != null) {
            val clubId = currentClubId!!
            isAdmin = isClubAdmin(context, clubId)
            clubPlayers = loadClubPlayers(context, clubId)
            tournamentHistory = loadTournamentHistory(context, clubId)

            val pListener = FirebaseStorage.listenToPlayers(clubId) { list ->
                clubPlayers = list
                val encoded = list
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
                context.getSharedPreferences("${PLAYERS_PREFS_NAME}_$clubId", Context.MODE_PRIVATE)
                    .edit().putString(PLAYERS_PREFS_KEY, encoded).apply()
            }

            val hListener = FirebaseStorage.listenToHistory(clubId) { list ->
                tournamentHistory = list
                val encoded = list
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
                            encodeHistoryField(entry.ratingText),
                            entry.structuredMatches,
                            entry.structuredRatings
                        ).joinToString(HISTORY_FIELD_SEPARATOR)
                    }
                context.getSharedPreferences("${HISTORY_PREFS_NAME}_$clubId", Context.MODE_PRIVATE)
                    .edit().putString(HISTORY_PREFS_KEY, encoded).apply()
            }

            val dListener = FirebaseStorage.listenToDraftGranular(clubId) { snapshot ->
                val firebaseName = snapshot.child("name").getValue(String::class.java).orEmpty()
                if (draft.name != firebaseName) {
                    draft.name = firebaseName
                }
                
                val firebasePlayersCount = snapshot.child("playersCount").getValue(Int::class.java) ?: 2
                if (draft.playersCount != firebasePlayersCount) {
                    draft.playersCount = firebasePlayersCount
                }
                
                val firebaseWinsToWin = snapshot.child("winsToWin").getValue(Int::class.java) ?: 2
                if (draft.winsToWin != firebaseWinsToWin) {
                    draft.winsToWin = firebaseWinsToWin
                }
                
                val firebaseTablesCount = snapshot.child("tablesCount").getValue(Int::class.java) ?: 1
                if (draft.tablesCount != firebaseTablesCount) {
                    draft.tablesCount = firebaseTablesCount
                }
                
                val firebaseFixedK = snapshot.child("fixedK").getValue(Double::class.java) ?: 0.2
                if (draft.fixedK != firebaseFixedK) {
                    draft.fixedK = firebaseFixedK
                }
                
                val firebaseIsListGenerated = snapshot.child("isListGenerated").getValue(Boolean::class.java) ?: false
                if (draft.isListGenerated != firebaseIsListGenerated) {
                    draft.isListGenerated = firebaseIsListGenerated
                }
                
                val firebaseNextFieldId = snapshot.child("nextFieldId").getValue(Int::class.java) ?: 0
                if (draft.nextFieldId != firebaseNextFieldId) {
                    draft.nextFieldId = firebaseNextFieldId
                }
                
                val firebaseRatingApplied = snapshot.child("ratingApplied").getValue(Boolean::class.java) ?: false
                if (draft.ratingApplied != firebaseRatingApplied) {
                    draft.ratingApplied = firebaseRatingApplied
                }
                
                val firebaseHistorySaved = snapshot.child("historySaved").getValue(Boolean::class.java) ?: false
                if (draft.historySaved != firebaseHistorySaved) {
                    draft.historySaved = firebaseHistorySaved
                }
                
                val firebaseBonusesAwarded = snapshot.child("bonusesAwarded").getValue(Boolean::class.java) ?: false
                if (draft.bonusesAwarded != firebaseBonusesAwarded) {
                    draft.bonusesAwarded = firebaseBonusesAwarded
                }
                
                val pfJson = snapshot.child("playerFieldsJson").getValue(String::class.java).orEmpty()
                if (pfJson.isNotBlank()) {
                    try {
                        val type = object : com.google.gson.reflect.TypeToken<List<PlayerField>>() {}.type
                        val list: List<PlayerField> = Gson().fromJson(pfJson, type)
                        if (draft.playerFields != list) {
                            draft.playerFields.clear()
                            draft.playerFields.addAll(list)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    if (draft.playerFields.isNotEmpty()) {
                        draft.playerFields.clear()
                    }
                }
                
                val withdrawn = snapshot.child("withdrawnPlayers").children.mapNotNull { it.getValue(Int::class.java) }
                if (draft.withdrawnPlayers != withdrawn) {
                    draft.withdrawnPlayers.clear()
                    draft.withdrawnPlayers.addAll(withdrawn)
                }
                
                val lastFinished = snapshot.child("lastFinishedPlayers").children.mapNotNull { it.getValue(Int::class.java) }
                if (draft.lastFinishedPlayers != lastFinished) {
                    draft.lastFinishedPlayers.clear()
                    draft.lastFinishedPlayers.addAll(lastFinished)
                }
                
                val newMatchScores = mutableMapOf<Pair<Int, Int>, String>()
                snapshot.child("matchScores").children.forEach { child ->
                    val keyStr = child.key ?: ""
                    val value = child.getValue(String::class.java) ?: ""
                    val parts = keyStr.split("_")
                    if (parts.size == 2) {
                        val p1 = parts[0].toIntOrNull()
                        val p2 = parts[1].toIntOrNull()
                        if (p1 != null && p2 != null) {
                            newMatchScores[p1 to p2] = value
                        }
                    }
                }
                if (draft.matchScores != newMatchScores) {
                    draft.matchScores.clear()
                    draft.matchScores.putAll(newMatchScores)
                }
                
                val newPlayoffScores = mutableMapOf<Int, String>()
                snapshot.child("playoffScores").children.forEach { child ->
                    val keyInt = child.key?.toIntOrNull()
                    val value = child.getValue(String::class.java) ?: ""
                    if (keyInt != null) {
                        newPlayoffScores[keyInt] = value
                    }
                }
                if (draft.playoffScores != newPlayoffScores) {
                    draft.playoffScores.clear()
                    draft.playoffScores.putAll(newPlayoffScores)
                }
                
                val activeList = snapshot.child("activeRoundRobinMatches").children.map { child ->
                    val keyStr = child.getValue(String::class.java) ?: ""
                    val parts = keyStr.split("_")
                    if (parts.size == 2) {
                        val p1 = parts[0].toIntOrNull()
                        val p2 = parts[1].toIntOrNull()
                        if (p1 != null && p2 != null) {
                            p1 to p2
                        } else null
                    } else null
                }
                if (draft.activeRoundRobinMatches != activeList) {
                    draft.activeRoundRobinMatches.clear()
                    draft.activeRoundRobinMatches.addAll(activeList)
                }
                
                val rcJson = snapshot.child("ratingChangesJson").getValue(String::class.java).orEmpty()
                if (rcJson.isNotBlank()) {
                    try {
                        val type = object : com.google.gson.reflect.TypeToken<List<RatingChange>>() {}.type
                        val list: List<RatingChange> = Gson().fromJson(rcJson, type)
                        if (draft.ratingChanges != list) {
                            draft.ratingChanges.clear()
                            draft.ratingChanges.addAll(list)
                        }
                    } catch (e: Exception) {}
                } else {
                    if (draft.ratingChanges.isNotEmpty()) {
                        draft.ratingChanges.clear()
                    }
                }
                
                val miJson = snapshot.child("matchImpactsJson").getValue(String::class.java).orEmpty()
                if (miJson.isNotBlank()) {
                    try {
                        val type = object : com.google.gson.reflect.TypeToken<List<MatchRatingImpact>>() {}.type
                        val list: List<MatchRatingImpact> = Gson().fromJson(miJson, type)
                        if (draft.matchImpacts != list) {
                            draft.matchImpacts.clear()
                            draft.matchImpacts.addAll(list)
                        }
                    } catch (e: Exception) {}
                } else {
                    if (draft.matchImpacts.isNotEmpty()) {
                        draft.matchImpacts.clear()
                    }
                }
            }

            onDispose {
                FirebaseStorage.removePlayersListener(clubId, pListener)
                FirebaseStorage.removeHistoryListener(clubId, hListener)
                FirebaseStorage.removeDraftGranularListener(clubId, dListener)
            }
        } else {
            clubPlayers = emptyList()
            tournamentHistory = emptyList()
            isAdmin = false
            onDispose {}
        }
    }

    fun savePlayersFromCurrentTournament() {
        val updatedPlayers = rememberPlayersFromTournament(
            context = context,
            existingPlayers = clubPlayers,
            playerNames = draft.playerFields.map { it.name }
        )

        saveClubPlayers(context, currentClubId ?: "", updatedPlayers)
        clubPlayers = updatedPlayers
    }

    fun applyRatingFromCurrentTournament(applyPlaceBonuses: Boolean) {
        if (draft.ratingApplied) return

        val playerNames = draft.playerFields.map { it.name }

        val result = applyRoundRobinRatings(
            existingPlayers = clubPlayers,
            playerNames = playerNames,
            scores = draft.matchScores,
            fixedK = draft.fixedK,
            applyPlaceBonuses = applyPlaceBonuses
        )

        val clubId = currentClubId ?: ""
        saveClubPlayers(context, clubId, result.players)
        clubPlayers = result.players

        draft.ratingChanges.clear()
        draft.ratingChanges.addAll(result.changes)
        draft.matchImpacts.clear()
        draft.matchImpacts.addAll(result.matchImpacts)
        draft.ratingApplied = true
        draft.bonusesAwarded = true
        
        if (clubId.isNotBlank()) {
            FirebaseStorage.syncDraftProperty(clubId, "ratingApplied", true)
            FirebaseStorage.syncDraftProperty(clubId, "bonusesAwarded", true)
            FirebaseStorage.syncDraftProperty(clubId, "ratingChangesJson", Gson().toJson(draft.ratingChanges))
            FirebaseStorage.syncDraftProperty(clubId, "matchImpactsJson", Gson().toJson(draft.matchImpacts))
        }
    }

    fun saveHistoryFromCurrentTournament() {
        if (draft.historySaved) return

        val entry = createRoundRobinHistoryEntry(draft)
        val updatedHistory = listOf(entry) + tournamentHistory

        val clubId = currentClubId ?: ""
        saveTournamentHistory(context, clubId, updatedHistory)
        tournamentHistory = updatedHistory
        draft.historySaved = true
        
        if (clubId.isNotBlank()) {
            FirebaseStorage.syncDraftProperty(clubId, "historySaved", true)
        }
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
                onNavigateToHistory = { currentScreen = AppScreen.History },
                onChangeClub = {
                    currentClubId = null
                    currentScreen = AppScreen.ClubSelection
                }
            )
            AppScreen.PlayerBase -> PlayerBaseScreen(
                clubPlayers = clubPlayers,
                onBack = { currentScreen = AppScreen.Dashboard },
                onPlayersChanged = { updatedPlayers ->
                    saveClubPlayers(context, currentClubId ?: "", updatedPlayers)
                    clubPlayers = updatedPlayers
                },
                onAddToTournament = { player ->
                    val alreadyIn = draft.playerFields.any { it.name.lowercase() == player.fullName.lowercase() }
                    if (alreadyIn) {
                        Toast.makeText(context, "${player.fullName} уже в турнире", Toast.LENGTH_SHORT).show()
                    } else {
                        val emptyFieldIdx = draft.playerFields.indexOfFirst { it.name.isBlank() }
                        if (emptyFieldIdx != -1) {
                            draft.playerFields[emptyFieldIdx] = draft.playerFields[emptyFieldIdx].copy(name = player.fullName)
                        } else {
                            draft.playerFields.add(PlayerField(draft.nextFieldId++, player.fullName))
                            draft.playersCount = draft.playerFields.size
                        }
                        Toast.makeText(context, "Игрок добавлен в турнир", Toast.LENGTH_SHORT).show()
                    }
                }
            )
            AppScreen.History -> TournamentHistoryScreen(
                history = tournamentHistory,
                clubPlayers = clubPlayers,
                onBack = { currentScreen = AppScreen.Dashboard },
                onHistoryChanged = { updatedHistory ->
                    saveTournamentHistory(context, currentClubId ?: "", updatedHistory)
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

                    // Фиксируем коэффициент турнира K до начала
                    val tPlayers = draft.playerFields.mapNotNull { f ->
                        getClubPlayerByName(f.name, clubPlayers)
                    }
                    val avgRating = if (tPlayers.isEmpty()) 100.0 else tPlayers.map { it.rating }.average()
                    draft.fixedK = calculateTournamentK(avgRating)

                    draft.isListGenerated = true
                    if (draft.playerFields.size <= 10) {
                        refreshActiveRoundRobinMatches(
                            draft = draft,
                            playerCount = draft.playerFields.size,
                            tablesCount = draft.tablesCount
                        )
                    }

                    val clubId = currentClubId ?: ""
                    if (clubId.isNotBlank()) {
                        FirebaseStorage.syncDraftProperty(clubId, "ratingApplied", false)
                        FirebaseStorage.syncDraftProperty(clubId, "historySaved", false)
                        FirebaseStorage.syncDraftProperty(clubId, "fixedK", draft.fixedK)
                        FirebaseStorage.syncDraftProperty(clubId, "isListGenerated", true)
                        FirebaseStorage.syncDraftPlayerFields(clubId, draft.playerFields)
                        FirebaseStorage.syncDraftActiveMatches(clubId, draft.activeRoundRobinMatches)
                    }

                    savePlayersFromCurrentTournament()
                    currentScreen = if (draft.playerFields.size <= 10) AppScreen.RoundRobin else AppScreen.Playoff
                }
            )
            AppScreen.RoundRobin -> RoundRobinScreen(
                draft = draft,
                clubPlayers = clubPlayers,
                onBack = { currentScreen = AppScreen.CreateTournament },
                onFinish = { applyPlaceBonuses ->
                    savePlayersFromCurrentTournament()
                    applyRatingFromCurrentTournament(applyPlaceBonuses)
                    saveHistoryFromCurrentTournament()
                    currentScreen = AppScreen.FinalResults
                }
            )
            AppScreen.Playoff -> PlayoffScreen(draft, { currentScreen = AppScreen.CreateTournament })
            AppScreen.FinalResults -> FinalResultsScreen(
                draft = draft,
                onBackToTable = { currentScreen = AppScreen.RoundRobin },
                onGoHome = {
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
                    val clubId = currentClubId ?: ""
                    if (clubId.isNotBlank()) {
                        FirebaseStorage.clearDraft(clubId)
                    }
                    currentScreen = AppScreen.Dashboard
                }
            )
            AppScreen.ClubSelection -> ClubSelectionScreen(
                onSelectClub = { clubId ->
                    currentClubId = clubId
                    val hasAdmin = isClubAdmin(context, clubId)
                    if (hasAdmin) {
                        currentScreen = AppScreen.AdminLogin
                    } else {
                        currentScreen = AppScreen.Dashboard
                    }
                },
                onSelectClubAdmin = { clubId ->
                    currentClubId = clubId
                    currentScreen = AppScreen.AdminLogin
                },
                onAddClub = { currentScreen = AppScreen.ClubLogin },
                onRemoveClub = { clubId ->
                    removeSavedClub(context, clubId)
                    if (getSavedClubs(context).isEmpty()) {
                        currentScreen = AppScreen.ClubLogin
                    }
                }
            )
            AppScreen.ClubLogin -> ClubLoginScreen(
                onLoginSuccess = { clubId ->
                    currentClubId = clubId
                    addSavedClub(context, clubId)
                    setClubAdmin(context, clubId, false)
                    currentScreen = AppScreen.Dashboard
                },
                onAdminLoginSuccess = { clubId, isNew ->
                    currentClubId = clubId
                    addSavedClub(context, clubId)
                    setClubAdmin(context, clubId, true)
                    if (isNew) {
                        currentScreen = AppScreen.Dashboard
                    } else {
                        currentScreen = AppScreen.AdminLogin
                    }
                }
            )
            AppScreen.AdminLogin -> AdminAuthDialogs(
                onDismiss = {
                    val clubs = getSavedClubs(context)
                    if (clubs.size <= 1) {
                        currentScreen = AppScreen.ClubLogin
                    } else {
                        currentScreen = AppScreen.ClubSelection
                    }
                },
                onAdminSuccess = {
                    val clubId = currentClubId ?: ""
                    addSavedClub(context, clubId)
                    setClubAdmin(context, clubId, true)
                    isAdmin = true
                    currentScreen = AppScreen.Dashboard
                }
            )
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
    onNavigateToHistory: () -> Unit,
    onChangeClub: () -> Unit
) {
    val hasDraft = draft.isListGenerated || draft.name.isNotBlank()

    var showMenu by remember { mutableStateOf(false) }
    var showVersionDialog by remember { mutableStateOf(false) }
    var showFaqDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val exportBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            exportBackupToUri(context, uri)
        }
    }

    val importBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            importBackupFromUri(context, uri) {
                (context.findActivity())?.recreate()
            }
        }
    }

    if (showVersionDialog) {
        AlertDialog(
            onDismissRequest = { showVersionDialog = false },
            title = { Text("О приложении", fontWeight = FontWeight.Bold, color = TextDark) },
            text = {
                Column {
                    Text("Теннисный Турнир", fontWeight = FontWeight.SemiBold, color = TextDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Версия: 1.0", color = TextGray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Приложение для организации, проведения и учета клубных турниров по настольному теннису с расчетом рейтинга RTTF.", color = TextDark)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showVersionDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = AppleBlue)
                ) {
                    Text("ОК", color = Color.White)
                }
            },
            containerColor = CardWhite,
            shape = RoundedCornerShape(20.dp)
        )
    }

    if (showFaqDialog) {
        AlertDialog(
            onDismissRequest = { showFaqDialog = false },
            title = { Text("FAQ — Справка", fontWeight = FontWeight.Bold, color = TextDark) },
            text = {
                Column(
                    modifier = Modifier
                        .heightIn(max = 400.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column {
                        Text("Как создать турнир?", fontWeight = FontWeight.Bold, color = TextDark)
                        Text("На главном экране нажмите «Новый турнир». Заполните название, выберите игроков из базы и начните турнир. Сетка сгенерируется автоматически.", color = TextGray)
                    }
                    Column {
                        Text("Режим Администратора (Скрытый вход)", fontWeight = FontWeight.Bold, color = TextDark)
                        Text("Чтобы войти в режим админа, зажмите пальцем название вашего клуба вверху экрана на 2 секунды. Появится запрос ПИН-кода.", color = TextGray)
                    }
                    Column {
                        Text("Как добавить игрока в базу?", fontWeight = FontWeight.Bold, color = TextDark)
                        Text("Перейдите в меню «База игроков» и нажмите синий круглый значок «+» справа вверху.", color = TextGray)
                    }
                    Column {
                        Text("Как рассчитывается рейтинг?", fontWeight = FontWeight.Bold, color = TextDark)
                        Text("Расчет ведется по системе RTTF. За победу над более сильным противником начисляется больше баллов. Игроки со статусом новичка (сыгравшие 5 или менее турниров) получают и теряют рейтинг по ускоренной шкале.", color = TextGray)
                    }
                    Column {
                        Text("Как сделать резервную копию?", fontWeight = FontWeight.Bold, color = TextDark)
                        Text("Нажмите на три точки в правом верхнем углу, выберите «Экспорт бэкапа» и сохраните файл. Позже на другом устройстве или после переустановки выберите «Импорт бэкапа» для восстановления всех клубов, игроков и результатов.", color = TextGray)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showFaqDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = AppleBlue)
                ) {
                    Text("Закрыть", color = Color.White)
                }
            },
            containerColor = CardWhite,
            shape = RoundedCornerShape(20.dp)
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Меню",
                        tint = TextDark,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(CardWhite)
                            .border(1.dp, BorderGray, RoundedCornerShape(14.dp))
                            .clickable { showMenu = true }
                            .padding(11.dp)
                    )

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier
                                .background(CardWhite, RoundedCornerShape(16.dp))
                                .border(1.dp, BorderGray, RoundedCornerShape(16.dp))
                        ) {
                            DropdownMenuItem(
                                text = { Text("О версии", style = AppTypography.bodyMedium) },
                                onClick = {
                                    showMenu = false
                                    showVersionDialog = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("FAQ (Вопросы и ответы)", style = AppTypography.bodyMedium) },
                                onClick = {
                                    showMenu = false
                                    showFaqDialog = true
                                }
                            )
                            val isAdminLocal = LocalIsAdmin.current
                            if (isAdminLocal) {
                                DropdownMenuItem(
                                    text = { Text("Экспорт бэкапа", style = AppTypography.bodyMedium) },
                                    onClick = {
                                        showMenu = false
                                        exportBackupLauncher.launch("tennistourney_backup.json")
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Импорт бэкапа", style = AppTypography.bodyMedium) },
                                    onClick = {
                                        showMenu = false
                                        importBackupLauncher.launch(arrayOf("application/json", "text/plain", "*/*"))
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text("Сменить / Создать клуб", style = AppTypography.bodyMedium) },
                                onClick = {
                                    showMenu = false
                                    onChangeClub()
                                }
                            )
                        }
                    }
                }
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
                        value = draft.name, onValueChange = { 
                            draft.name = it
                            val clubId = currentClubId ?: ""
                            if (clubId.isNotBlank()) {
                                FirebaseStorage.syncDraftProperty(clubId, "name", it)
                            }
                        },
                        placeholder = { Text("Введите название турнира", color = TextGray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = BorderGray, focusedBorderColor = AppleBlue, unfocusedContainerColor = CardWhite, focusedContainerColor = CardWhite),
                        shape = RoundedCornerShape(12.dp), singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
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

    }
}

@Composable
fun PlayerBaseScreen(
    clubPlayers: List<ClubPlayer>,
    onBack: () -> Unit,
    onPlayersChanged: (List<ClubPlayer>) -> Unit,
    onAddToTournament: (ClubPlayer) -> Unit
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
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .imePadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 16.dp, top = 32.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppBackButton(onClick = onBack)

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "База игроков",
                    style = AppTypography.displayLarge.copy(fontSize = 22.sp, fontWeight = FontWeight.ExtraBold),
                    color = TextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "${clubPlayers.size} игроков • рейтинг, аватар",
                    style = AppTypography.bodyMedium,
                    color = TextGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            IconButton(
                onClick = { showCreateDialog = true },
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppleBlue)
            ) {
                Text("+", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
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
                    var lastActionTimestamp by remember { mutableLongStateOf(0L) }

                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { dismissValue ->
                            val now = System.currentTimeMillis()
                            if (now - lastActionTimestamp > 500) {
                                when (dismissValue) {
                                    SwipeToDismissBoxValue.EndToStart -> {
                                        playerToDelete = player
                                    }
                                    SwipeToDismissBoxValue.StartToEnd -> {
                                        onAddToTournament(player)
                                        lastActionTimestamp = now
                                    }
                                    else -> {}
                                }
                            }
                            false
                        },
                        positionalThreshold = { distance -> distance * 0.4f }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = true,
                        backgroundContent = {
                            val color = when (dismissState.dismissDirection) {
                                SwipeToDismissBoxValue.StartToEnd -> GreenBadgeBg
                                SwipeToDismissBoxValue.EndToStart -> CellLostBg
                                else -> Color.Transparent
                            }
                            val alignment = when (dismissState.dismissDirection) {
                                SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                                SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                                else -> Alignment.Center
                            }
                            val icon = when (dismissState.dismissDirection) {
                                SwipeToDismissBoxValue.StartToEnd -> CheckCircleIcon
                                SwipeToDismissBoxValue.EndToStart -> TrashIcon
                                else -> null
                            }
                            val iconColor = when (dismissState.dismissDirection) {
                                SwipeToDismissBoxValue.StartToEnd -> GreenBadgeText
                                SwipeToDismissBoxValue.EndToStart -> SwipeDeleteRed
                                else -> Color.Transparent
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(color)
                                    .padding(horizontal = 20.dp),
                                contentAlignment = alignment
                            ) {
                                if (icon != null) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = iconColor,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
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

                Text(
                    "Рейтинг ${formatRating(player.rating)}",
                    color = AppleBlue,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    "Турниров: ${player.tournamentsPlayed}",
                    color = TextGray,
                    style = AppTypography.bodyMedium,
                    maxLines = 1
                )

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
    size: Dp,
    modifier: Modifier = Modifier
) {
    val avatarBitmap = rememberAvatarBitmap(player.avatarUri)
    val fontSize = (size.value * 0.45f).sp

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
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
                fontSize = fontSize
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
    var showAvatarMenu by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }

    val clubId = LocalCurrentClubId.current ?: ""

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val id = player?.id ?: ((existingPlayers.maxOfOrNull { it.id } ?: 0) + 1)
            avatarUri = saveBitmapAvatar(context, clubId, bitmap, id)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val id = player?.id ?: ((existingPlayers.maxOfOrNull { it.id } ?: 0) + 1)
            avatarUri = saveUriAvatar(context, clubId, uri, id)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .padding(20.dp)
            ) {
                // Header
                Box(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (isEditing) "Профиль игрока" else "Регистрация игрока",
                        style = AppTypography.displayLarge.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = TextDark,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 40.dp)
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Text("✕", fontSize = 24.sp, color = TextGray)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Avatar Circle Clickable
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box {
                            PlayerAvatar(
                                player = ClubPlayer(
                                    id = player?.id ?: 0,
                                    fullName = fullName.ifBlank { "Игрок" },
                                    rating = ratingText.replace(",", ".").toDoubleOrNull() ?: 100.0,
                                    avatarUri = avatarUri
                                ),
                                size = 120.dp,
                                modifier = Modifier.clickable { showAvatarMenu = true }
                            )

                            DropdownMenu(
                                expanded = showAvatarMenu,
                                onDismissRequest = { showAvatarMenu = false },
                                modifier = Modifier
                                    .background(CardWhite, RoundedCornerShape(16.dp))
                                    .border(1.dp, BorderGray, RoundedCornerShape(16.dp))
                            ) {
                                DropdownMenuItem(
                                    text = { Text(if (avatarUri.isBlank()) "Снять фото" else "Снять новое фото", style = AppTypography.bodyMedium) },
                                    onClick = {
                                        showAvatarMenu = false
                                        cameraLauncher.launch(null)
                                    },
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                                )
                                DropdownMenuItem(
                                    text = { Text("Выбрать из галереи", style = AppTypography.bodyMedium) },
                                    onClick = {
                                        showAvatarMenu = false
                                        galleryLauncher.launch("image/*")
                                    },
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                                )
                                if (avatarUri.isNotBlank()) {
                                    DropdownMenuItem(
                                        text = { Text("Удалить фото", color = SwipeDeleteRed, style = AppTypography.bodyMedium) },
                                        onClick = {
                                            showAvatarMenu = false
                                            avatarUri = ""
                                        },
                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = fullName,
                        onValueChange = {
                            fullName = it
                            errorText = null
                        },
                        label = { Text("ФИО игрока") },
                        placeholder = { Text("Иванов Алексей") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = BorderGray,
                            focusedBorderColor = AppleBlue,
                            unfocusedContainerColor = CardWhite,
                            focusedContainerColor = CardWhite
                        ),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = BlueBadgeBg),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Важно: если «турниров сыграл» = 6, игрок уже не считается новичком в рейтинге.",
                            color = BlueBadgeText,
                            style = AppTypography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text("Рейтинг и опыт", style = AppTypography.titleLarge, color = TextDark)
                    Spacer(modifier = Modifier.height(12.dp))

                    StatInputField("Текущий рейтинг", ratingText, Modifier.fillMaxWidth()) {
                        ratingText = it
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    StatInputField("Сколько турниров уже сыграл", tournamentsText, Modifier.fillMaxWidth()) {
                        tournamentsText = it
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text("Игры", style = AppTypography.titleLarge, color = TextDark)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatInputField("Победы", winsText, Modifier.weight(1f)) {
                            winsText = it
                        }
                        StatInputField("Поражения", lossesText, Modifier.weight(1f)) {
                            lossesText = it
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        "Медали",
                        style = AppTypography.titleLarge,
                        color = TextDark,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatInputField("🥇", goldText, Modifier.weight(1f)) {
                            goldText = it
                        }
                        StatInputField("🥈", silverText, Modifier.weight(1f)) {
                            silverText = it
                        }
                        StatInputField("🥉", bronzeText, Modifier.weight(1f)) {
                            bronzeText = it
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text("Статистика с соперниками", style = AppTypography.titleLarge, color = TextDark)
                    Spacer(modifier = Modifier.height(12.dp))

                    val stats = decodeOpponentStats(player?.opponentStats.orEmpty())
                        .toList()
                        .sortedByDescending { (_, value) -> value.first + value.second }

                    if (stats.isEmpty()) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = BgLight),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Появится автоматически после сыгранных матчей.",
                                color = TextGray,
                                style = AppTypography.bodyMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        stats.take(12).forEach { (opponent, result) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    opponent,
                                    modifier = Modifier.weight(1f),
                                    color = TextDark,
                                    maxLines = 1,
                                    style = AppTypography.bodyMedium
                                )
                                Text(
                                    "${result.first}-${result.second}",
                                    color = AppleBlue,
                                    fontWeight = FontWeight.Bold,
                                    style = AppTypography.bodyMedium
                                )
                            }
                            HorizontalDivider(color = BorderGray)
                        }
                    }

                    errorText?.let {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(it, color = CellLostText, style = AppTypography.bodyMedium)
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        border = BorderStroke(1.dp, BorderGray),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Отмена", color = TextGray, fontSize = 17.sp)
                    }

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
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Сохранить", color = Color.White, fontSize = 17.sp)
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
    clubPlayers: List<ClubPlayer>,
    onBack: () -> Unit,
    onHistoryChanged: (List<TournamentHistoryEntry>) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedEntry by remember { mutableStateOf<TournamentHistoryEntry?>(null) }
    var entryToDelete by remember { mutableStateOf<TournamentHistoryEntry?>(null) }

    val filteredHistory = remember(history, searchQuery) {
        val query = searchQuery.trim().lowercase()

        if (query.isBlank()) {
            history
        } else {
            history.filter { entry ->
                entry.name.lowercase().contains(query) ||
                        entry.winnerName.lowercase().contains(query) ||
                        entry.standingsText.lowercase().contains(query) ||
                        entry.ratingText.lowercase().contains(query)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgLight)
            .windowInsetsPadding(WindowInsets.safeDrawing)
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
                    placeholder = { Text("Поиск по турниру") },
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
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
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
        TournamentDetailsDialog(
            entry = entry,
            clubPlayers = clubPlayers,
            onDismiss = { selectedEntry = null }
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
fun TournamentDetailsDialog(
    entry: TournamentHistoryEntry,
    clubPlayers: List<ClubPlayer>,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = BgLight),
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardWhite)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppBackButton(onClick = onDismiss)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            entry.name,
                            style = AppTypography.titleLarge.copy(fontSize = 20.sp),
                            color = TextDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            "${entry.dateText} • ${entry.playersCount} участников",
                            style = AppTypography.bodyMedium,
                            color = TextGray
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Text("✕", fontSize = 24.sp, color = TextGray)
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Parse structured matches
                    val allMatches = remember(entry.structuredMatches) {
                        val matches = mutableListOf<MatchRatingImpact>()
                        if (entry.structuredMatches.isNotBlank()) {
                            val matchRows = entry.structuredMatches.split(";;")
                            matchRows.forEach { row ->
                                val p = row.split("::")
                                if (p.size >= 5) {
                                    matches.add(MatchRatingImpact(
                                        player1 = safeDecode(p[0]),
                                        player2 = safeDecode(p[1]),
                                        score = p[2],
                                        p1Delta = p[3].toDoubleOrNull() ?: 0.0,
                                        p2Delta = p[4].toDoubleOrNull() ?: 0.0
                                    ))
                                }
                            }
                        }
                        matches
                    }

                    // Parse structured ratings for overall delta
                    val ratingChangesMap = remember(entry.structuredRatings) {
                        val map = mutableMapOf<String, Double>()
                        if (entry.structuredRatings.isNotBlank()) {
                            val ratingRows = entry.structuredRatings.split(";;")
                            ratingRows.forEach { row ->
                                val p = row.split("::")
                                if (p.size >= 4) {
                                    map[safeDecode(p[0])] = p[3].toDoubleOrNull() ?: 0.0
                                }
                            }
                        }
                        map
                    }

                    var expandedPlayer by remember { mutableStateOf<String?>(null) }

                    // 1. Итоговая таблица с аккордеоном
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardWhite),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, BorderGray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("ИТОГОВАЯ ТАБЛИЦА И МАТЧИ", style = AppTypography.bodyMedium, color = TextGray, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Нажмите на игрока, чтобы увидеть результаты его матчей", style = AppTypography.bodySmall, color = AppleBlue)
                            Spacer(modifier = Modifier.height(12.dp))

                            val lines = entry.standingsText.split("\n").filter { it.isNotBlank() }
                            lines.forEachIndexed { index, line ->
                                val parts = line.split(". ", limit = 2)
                                if (parts.size == 2) {
                                    val place = parts[0]
                                    val details = parts[1] // e.g. "Смирнов Михаил — 3 очка (3:0)"
                                    val playerName = details.split(" — ").getOrNull(0) ?: details

                                    val isExpanded = expandedPlayer == playerName
                                    val overallDelta = ratingChangesMap[playerName] ?: 0.0

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                expandedPlayer = if (isExpanded) null else playerName
                                            }
                                            .background(if (isExpanded) BgLight else Color.Transparent)
                                            .padding(vertical = 12.dp, horizontal = 8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                place,
                                                modifier = Modifier.width(28.dp),
                                                color = AppleBlue,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                playerName,
                                                color = TextDark,
                                                style = AppTypography.labelLarge,
                                                modifier = Modifier.weight(1f)
                                            )
                                            if (overallDelta != 0.0) {
                                                Text(
                                                    formatDelta(overallDelta),
                                                    color = if (overallDelta > 0) GreenBadgeText else SwipeDeleteRed,
                                                    fontWeight = FontWeight.Bold,
                                                    style = AppTypography.bodyMedium
                                                )
                                            }
                                        }

                                        // Accordion Content
                                        if (isExpanded) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 16.dp)
                                            ) {
                                                val playerMatches = allMatches.filter {
                                                    it.player1.equals(playerName, ignoreCase = true) || it.player2.equals(playerName, ignoreCase = true)
                                                }

                                                if (playerMatches.isEmpty()) {
                                                    Text("Нет сыгранных матчей", color = TextGray, style = AppTypography.bodySmall)
                                                } else {
                                                    playerMatches.forEach { match ->
                                                        val isWinner = match.player1.equals(playerName, ignoreCase = true)
                                                        val opponentName = if (isWinner) match.player2 else match.player1
                                                        val delta = if (isWinner) match.p1Delta else match.p2Delta

                                                        val scoreParts = match.score.split(":")
                                                        val displayScore = if (scoreParts.size == 2) {
                                                            if (isWinner) "${scoreParts[0]}:${scoreParts[1]}" else "${scoreParts[1]}:${scoreParts[0]}"
                                                        } else match.score

                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(vertical = 6.dp),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Text(opponentName, style = AppTypography.bodyMedium, color = TextDark, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                                                            Text(displayScore, style = AppTypography.bodyMedium, color = TextGray, modifier = Modifier.padding(horizontal = 8.dp))
                                                            Text(
                                                                formatDelta(delta),
                                                                style = AppTypography.bodyMedium,
                                                                color = if (delta > 0) GreenBadgeText else if (delta < 0) SwipeDeleteRed else TextGray,
                                                                fontWeight = FontWeight.Bold,
                                                                modifier = Modifier.width(48.dp),
                                                                textAlign = TextAlign.End
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (index < lines.lastIndex && !isExpanded) {
                                        HorizontalDivider(color = BorderGray, modifier = Modifier.padding(horizontal = 8.dp))
                                    }
                                } else {
                                    Text(line, color = TextDark, style = AppTypography.labelLarge, modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp))
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
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
    val currentClubId = LocalCurrentClubId.current ?: "" 

    LaunchedEffect(draft.playersCount) {
        var changed = false
        while (draft.playerFields.size < draft.playersCount) {
            draft.playerFields.add(PlayerField(draft.nextFieldId++, ""))
            changed = true
        }
        while (draft.playerFields.size > draft.playersCount) {
            draft.playerFields.removeAt(draft.playerFields.lastIndex)
            changed = true
        }
        if (changed && currentClubId.isNotBlank()) {
            FirebaseStorage.syncDraftPlayerFields(currentClubId, draft.playerFields)
            FirebaseStorage.syncDraftProperty(currentClubId, "nextFieldId", draft.nextFieldId)
        }
    }

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
            .windowInsetsPadding(WindowInsets.safeDrawing)
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
                                if (currentClubId.isNotBlank()) {
                                    FirebaseStorage.syncDraftProperty(currentClubId, "name", it)
                                }
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
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Левая колонка: Участники
                            Column(modifier = Modifier.weight(1f)) {
                                Text("👥 Участники", style = AppTypography.titleLarge, color = TextDark)
                                Spacer(modifier = Modifier.height(12.dp))
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
                                            if (draft.tablesCount > maximumTables) draft.tablesCount = maximumTables
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
                                            startError = null
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Правая колонка: Формат до побед
                            Column(modifier = Modifier.weight(1.3f)) {
                                Text("🏆 Кол-во партий", style = AppTypography.titleLarge, color = TextDark)
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    SegmentBtn("до 1", draft.winsToWin == 1, Modifier.weight(1f)) {
                                        draft.winsToWin = 1
                                        if (currentClubId.isNotBlank()) FirebaseStorage.syncDraftProperty(currentClubId, "winsToWin", 1)
                                    }
                                    SegmentBtn("до 2", draft.winsToWin == 2, Modifier.weight(1f)) {
                                        draft.winsToWin = 2
                                        if (currentClubId.isNotBlank()) FirebaseStorage.syncDraftProperty(currentClubId, "winsToWin", 2)
                                    }
                                    SegmentBtn("до 3", draft.winsToWin == 3, Modifier.weight(1f)) {
                                        draft.winsToWin = 3
                                        if (currentClubId.isNotBlank()) FirebaseStorage.syncDraftProperty(currentClubId, "winsToWin", 3)
                                    }
                                }
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
                                        if (currentClubId.isNotBlank()) FirebaseStorage.syncDraftProperty(currentClubId, "tablesCount", draft.tablesCount)
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
                                        if (currentClubId.isNotBlank()) FirebaseStorage.syncDraftProperty(currentClubId, "tablesCount", draft.tablesCount)
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
                            Text("👥 Игроки", style = AppTypography.titleLarge, color = TextDark)
                            Text("${draft.playerFields.size}", style = AppTypography.titleLarge, color = AppleBlue)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        draft.playerFields.forEachIndexed { index, field ->
                            key(field.id) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                                ) {
                                    Text(
                                        "${index + 1}",
                                        style = AppTypography.labelLarge,
                                        color = TextGray,
                                        modifier = Modifier.width(24.dp)
                                    )

                                    PlayerNameInputField(
                                        value = field.name,
                                        clubPlayers = clubPlayers,
                                        modifier = Modifier.weight(1f),
                                        onValueChange = { newName ->
                                            val idx = draft.playerFields.indexOf(field)
                                            if (idx != -1) {
                                                draft.playerFields[idx] = field.copy(name = newName)
                                                if (currentClubId.isNotBlank()) FirebaseStorage.syncDraftPlayerFields(currentClubId, draft.playerFields)
                                            }
                                            startError = null
                                        }
                                    )

                                    if (draft.playerFields.size > 2) {
                                        IconButton(
                                            onClick = {
                                                draft.playerFields.removeAt(index)
                                                draft.playersCount--
                                                if (currentClubId.isNotBlank()) {
                                                    FirebaseStorage.syncDraftPlayerFields(currentClubId, draft.playerFields)
                                                    FirebaseStorage.syncDraftProperty(currentClubId, "playersCount", draft.playersCount)
                                                }
                                                startError = null
                                            }
                                        ) {
                                            Icon(
                                                imageVector = TrashIcon,
                                                contentDescription = "Удалить",
                                                tint = SwipeDeleteRed.copy(alpha = 0.8f),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "+ Добавить игрока",
                            style = AppTypography.labelLarge,
                            color = AppleBlue,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    draft.playersCount++
                                    draft.playerFields.add(PlayerField(draft.nextFieldId++, ""))
                                    if (currentClubId.isNotBlank()) {
                                        FirebaseStorage.syncDraftProperty(currentClubId, "playersCount", draft.playersCount)
                                        FirebaseStorage.syncDraftPlayerFields(currentClubId, draft.playerFields)
                                        FirebaseStorage.syncDraftProperty(currentClubId, "nextFieldId", draft.nextFieldId)
                                    }
                                    startError = null
                                }
                                .padding(12.dp)
                        )
                    }
                }
            }
        }

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
                        val error = tournamentStartError(draft, clubPlayers)
                        if (error == null) onStartTournament() else startError = error
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(58.dp)
                ) {
                    Text("🏆 Создать турнир", style = AppTypography.labelLarge, color = Color.White)
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
    var query by remember(value) { mutableStateOf(value) }
    var showSuggestions by remember { mutableStateOf(false) }

    val exactPlayer = clubPlayers.firstOrNull {
        it.fullName.lowercase() == normalizePlayerName(value).lowercase()
    }

    val suggestions = remember(query, clubPlayers) {
        val cleanQuery = normalizePlayerName(query).lowercase()
        if (cleanQuery.length < 2) {
            emptyList()
        } else {
            clubPlayers
                .filter { it.fullName.lowercase().contains(cleanQuery) }
                .sortedWith(compareBy<ClubPlayer> { !it.fullName.lowercase().startsWith(cleanQuery) }.thenByDescending { it.rating })
                .take(6)
        }
    }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = query,
            onValueChange = { newValue ->
                query = newValue
                onValueChange(newValue)
                showSuggestions = true
            },
            placeholder = { Text("Фамилия Имя") },
            enabled = clubPlayers.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().heightIn(min = 44.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = if (exactPlayer == null && query.isNotBlank()) SwipeDeleteRed.copy(alpha = 0.4f) else BorderGray,
                focusedBorderColor = AppleBlue,
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            textStyle = AppTypography.bodyMedium,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
        )

        if (showSuggestions && suggestions.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, BorderGray),
                modifier = Modifier.fillMaxWidth().padding(top = 50.dp)
            ) {
                Column {
                    suggestions.forEachIndexed { index, player ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    query = player.fullName
                                    onValueChange(player.fullName)
                                    showSuggestions = false
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = player.fullName,
                                modifier = Modifier.weight(1f),
                                color = TextDark,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = formatRating(player.rating),
                                color = AppleBlue,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                        if (index != suggestions.lastIndex) HorizontalDivider(color = BorderGray)
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
    clubId: String,
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
    if (clubId.isNotBlank()) {
        FirebaseStorage.syncDraftMatchScoresMap(clubId, draft.matchScores)
        FirebaseStorage.syncDraftActiveMatches(clubId, draft.activeRoundRobinMatches)
    }
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
 * Приоритет отдается игрокам с наименьшим количеством сыгранных матчей.
 * Вторая метрика — расстояние между игроками в таблице.
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

    val withdrawnSet = draft.withdrawnPlayers.toSet()

    // 1. Maintain the list size equal to tables count, filling with nulls
    while (draft.activeRoundRobinMatches.size < effectiveTablesCount) {
        draft.activeRoundRobinMatches.add(null)
    }
    while (draft.activeRoundRobinMatches.size > effectiveTablesCount) {
        draft.activeRoundRobinMatches.removeAt(draft.activeRoundRobinMatches.lastIndex)
    }

    // 2. Clear invalid assignments (match played, player withdrawn, etc.)
    for (i in draft.activeRoundRobinMatches.indices) {
        val pair = draft.activeRoundRobinMatches[i] ?: continue
        val invalid = pair.first !in 0 until playerCount ||
                pair.second !in 0 until playerCount ||
                pair.first in withdrawnSet ||
                pair.second in withdrawnSet ||
                draft.matchScores.containsKey(normalizedPair(pair.first, pair.second))

        if (invalid) {
            draft.activeRoundRobinMatches[i] = null
        }
    }

    val busyPlayers = draft.activeRoundRobinMatches
        .filterNotNull()
        .flatMap { listOf(it.first, it.second) }
        .toMutableSet()

    // 3. Find candidates for empty slots
    val emptySlots = draft.activeRoundRobinMatches.indices.filter { draft.activeRoundRobinMatches[it] == null }
    if (emptySlots.isEmpty()) return

    val matchesPlayed = IntArray(playerCount)
    for (f in 0 until playerCount) {
        for (s in f + 1 until playerCount) {
            if (draft.matchScores.containsKey(f to s)) {
                matchesPlayed[f]++
                matchesPlayed[s]++
            }
        }
    }

    val currentPairs = draft.activeRoundRobinMatches.filterNotNull().toSet()
    val lastFinished = draft.lastFinishedPlayers.toSet()

    val candidates = mutableListOf<Triple<Int, Int, Int>>()
    for (f in 0 until playerCount) {
        if (f in withdrawnSet) continue
        for (s in f + 1 until playerCount) {
            if (s in withdrawnSet) continue

            val pair = f to s
            if (!draft.matchScores.containsKey(pair) && !currentPairs.contains(pair)) {
                val p1Penalty = if (f in lastFinished) 20 else 0
                val p2Penalty = if (s in lastFinished) 20 else 0
                val weight = (matchesPlayed[f] + matchesPlayed[s] + p1Penalty + p2Penalty)
                val distance = kotlin.math.abs(f - s)
                candidates.add(Triple(f, s, weight * 1000 - distance))
            }
        }
    }

    candidates.sortBy { it.third }

    var nextCandidateIdx = 0
    for (slotIdx in emptySlots) {
        while (nextCandidateIdx < candidates.size) {
            val candidate = candidates[nextCandidateIdx++]
            if (candidate.first !in busyPlayers && candidate.second !in busyPlayers) {
                draft.activeRoundRobinMatches[slotIdx] = candidate.first to candidate.second
                busyPlayers.add(candidate.first)
                busyPlayers.add(candidate.second)
                break
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
    clubPlayers: List<ClubPlayer>,
    onBack: () -> Unit,
    onFinish: (Boolean) -> Unit
) {
    val currentClubId = LocalCurrentClubId.current ?: ""
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
    val activePlayers = draft.activeRoundRobinMatches.filterNotNull().flatMap { listOf(it.first, it.second) }.toSet()

    val queueMatches = remember(draft.matchScores.size, draft.activeRoundRobinMatches.size, draft.withdrawnPlayers.size, draft.lastFinishedPlayers.size) {
        val assigned = draft.activeRoundRobinMatches.filterNotNull().map { normalizedPair(it.first, it.second) }.toSet()
        val withdrawn = draft.withdrawnPlayers.toSet()
        val lastFinished = draft.lastFinishedPlayers.toSet()

        val matchesPlayed = IntArray(playerCount)
        for (f in 0 until playerCount) {
            for (s in f + 1 until playerCount) {
                if (draft.matchScores.containsKey(f to s)) {
                    matchesPlayed[f]++
                    matchesPlayed[s]++
                }
            }
        }

        val queue = mutableListOf<Pair<Int, Int>>()
        for (f in 0 until playerCount) {
            for (s in f + 1 until playerCount) {
                if (f in withdrawn || s in withdrawn) continue
                val pair = f to s
                if (!draft.matchScores.containsKey(pair) && !assigned.contains(pair)) {
                    queue.add(pair)
                }
            }
        }

        queue.sortedBy { pair ->
            val p1Penalty = if (pair.first in lastFinished) 20 else 0
            val p2Penalty = if (pair.second in lastFinished) 20 else 0
            val weight = (matchesPlayed[pair.first] + matchesPlayed[pair.second] + p1Penalty + p2Penalty)
            val distance = kotlin.math.abs(pair.first - pair.second)
            weight * 1000 - distance
        }
    }

    var selectedPair by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var restorePlayerCandidate by remember { mutableStateOf<Int?>(null) }
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
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .verticalScroll(rememberScrollState())
    ) {
        SimpleRoundRobinTopBar(
            tournamentName = draft.name.ifBlank { "Турнир" },
            participantCount = playerCount,
            bestOfWins = draft.winsToWin,
            tablesCount = draft.tablesCount,
            tournamentIsComplete = tournamentIsComplete,
            onBack = onBack,
            onFinish = {
                if (draft.bonusesAwarded) {
                    onFinish(false)
                } else {
                    askPlaceBonusDialog = true
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        val isCompactTournament = playerCount <= 6

        if (isCompactTournament) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Левая колонка: Таблица + Результаты
                Column(modifier = Modifier.width(IntrinsicSize.Min)) {
                    Card(
                        modifier = Modifier.wrapContentWidth(),
                        colors = CardDefaults.cardColors(containerColor = CardWhite),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, BorderGray)
                    ) {
                        RoundRobinTable(
                            playerNames = playerNames,
                            clubPlayers = clubPlayers,
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

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("ТЕКУЩИЕ РЕЗУЛЬТАТЫ И РЕЙТИНГ", style = AppTypography.bodyMedium, color = TextGray, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp))
                    Spacer(modifier = Modifier.height(8.dp))

                    val liveRatingResult = remember(draft.matchScores.size) {
                        applyRoundRobinRatings(
                            existingPlayers = clubPlayers,
                            playerNames = playerNames,
                            scores = draft.matchScores,
                            fixedK = draft.fixedK,
                            applyPlaceBonuses = false
                        )
                    }
                    val ratingChangesMap = liveRatingResult.changes.associateBy { it.fullName.lowercase() }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CardWhite),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, BorderGray)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            standings.forEachIndexed { index, stat ->
                                val pName = playerNames.getOrElse(stat.index) { "" }
                                val pObj = getClubPlayerByName(pName, clubPlayers)

                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
                                    Text("${index + 1}", modifier = Modifier.width((24f * tableScale).dp), color = AppleBlue, fontWeight = FontWeight.Bold)
                                    PlayerAvatar(player = pObj ?: ClubPlayer(0, pName), size = (32f * tableScale).dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(pName, style = AppTypography.labelLarge.copy(fontSize = (13f * tableScale).sp), color = TextDark, maxLines = 1)
                                        Text("Рейтинг: ${formatRating(pObj?.rating ?: 100.0)}", color = TextGray, fontSize = (11f * tableScale).sp)
                                    }

                                    val rChange = ratingChangesMap[pName.lowercase()]
                                    if (rChange != null) {
                                        val delta = rChange.newRating - rChange.oldRating
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(formatRating(rChange.oldRating), color = TextGray, fontSize = (11f * tableScale).sp)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                formatDelta(delta),
                                                color = if (delta >= 0) GreenBadgeText else SwipeDeleteRed,
                                                fontSize = (11f * tableScale).sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(formatRating(rChange.newRating), color = AppleBlue, fontSize = (12f * tableScale).sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                if (index < standings.lastIndex) HorizontalDivider(color = BorderGray)
                            }
                        }
                    }
                }

                // Правая колонка: Столы + Очередь
                Column(modifier = Modifier.weight(1f)) {
                    CompactActiveTablesPanel(
                        assignments = draft.activeRoundRobinMatches,
                        queueMatches = queueMatches,
                        playerNames = playerNames,
                        clubPlayers = clubPlayers,
                        configuredTablesCount = draft.tablesCount,
                        isVertical = true,
                        onMatchClick = { pair -> selectedPair = pair }
                    )
                }
            }
        } else {
            // Большой турнир (Классическая вертикальная верстка)
            CompactActiveTablesPanel(
                assignments = draft.activeRoundRobinMatches,
                queueMatches = queueMatches,
                playerNames = playerNames,
                clubPlayers = clubPlayers,
                configuredTablesCount = draft.tablesCount,
                onMatchClick = { pair -> selectedPair = pair }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BorderGray)
            ) {
                RoundRobinTable(
                    playerNames = playerNames,
                    clubPlayers = clubPlayers,
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
                        val score = draft.matchScores[first to second]
                        if (score == "L" || score == "W") {
                            val firstWithdrawn = first in draft.withdrawnPlayers
                            val secondWithdrawn = second in draft.withdrawnPlayers
                            if (firstWithdrawn) {
                                restorePlayerCandidate = first
                            } else if (secondWithdrawn) {
                                restorePlayerCandidate = second
                            }
                        } else {
                            selectedPair = first to second
                        }
                    },
                    onPlayerClick = { playerIndex ->
                        statusPlayerIndex = playerIndex
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("ТЕКУЩИЕ РЕЗУЛЬТАТЫ И РЕЙТИНГ", style = AppTypography.bodyMedium, color = TextGray, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp))
            Spacer(modifier = Modifier.height(8.dp))

            val liveRatingResult = remember(draft.matchScores.size) {
                applyRoundRobinRatings(
                    existingPlayers = clubPlayers,
                    playerNames = playerNames,
                    scores = draft.matchScores,
                    fixedK = draft.fixedK,
                    applyPlaceBonuses = false
                )
            }
            val ratingChangesMap = liveRatingResult.changes.associateBy { it.fullName.lowercase() }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BorderGray)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    standings.forEachIndexed { index, stat ->
                        val pName = playerNames.getOrElse(stat.index) { "" }
                        val pObj = getClubPlayerByName(pName, clubPlayers)

                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
                            Text("${index + 1}", modifier = Modifier.width(28.dp), color = AppleBlue, fontWeight = FontWeight.Bold)
                            PlayerAvatar(player = pObj ?: ClubPlayer(0, pName), size = 40.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(pName, style = AppTypography.labelLarge, color = TextDark, maxLines = 1)
                                Text("Рейтинг: ${formatRating(pObj?.rating ?: 100.0)}", color = TextGray, fontSize = 12.sp)
                            }

                            val rChange = ratingChangesMap[pName.lowercase()]
                            if (rChange != null) {
                                val delta = rChange.newRating - rChange.oldRating
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(formatRating(rChange.oldRating), color = TextGray, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        formatDelta(delta),
                                        color = if (delta >= 0) GreenBadgeText else SwipeDeleteRed,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(formatRating(rChange.newRating), color = AppleBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        if (index < standings.lastIndex) HorizontalDivider(color = BorderGray)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        SimpleRoundRobinBottomBar(
            completedMatches = completedMatches,
            totalMatches = totalMatches,
            bestOfThreeWins = draft.winsToWin >= 2, // technically it's bestOf but we can just use the name
            tableScale = tableScale,
            onZoomOut = { tableScale = (tableScale - 0.08f).coerceAtLeast(0.78f) },
            onZoomIn = { tableScale = (tableScale + 0.08f).coerceAtMost(1.18f) },
            onResetZoom = { tableScale = if (playerCount <= 6) 0.90f else 0.86f }
        )

        Spacer(modifier = Modifier.height(32.dp))
    }

    selectedPair?.let { pair ->
        val first = pair.first
        val second = pair.second

        if (first in playerNames.indices && second in playerNames.indices && first != second) {
            val cp1 = getClubPlayerByName(playerNames[first], clubPlayers) ?: ClubPlayer(id = 0, fullName = playerNames[first], rating = 0.0)
            val cp2 = getClubPlayerByName(playerNames[second], clubPlayers) ?: ClubPlayer(id = 0, fullName = playerNames[second], rating = 0.0)

            RoundRobinScoreDialog(
                player1 = cp1,
                player2 = cp2,
                setsToWin = draft.winsToWin,
                existingScore = draft.matchScores[first to second],
                onDismiss = { selectedPair = null },
                onSave = { score ->
                    draft.matchScores[first to second] = score
                    draft.matchScores[second to first] = reverseScore(score)
                    
                    if (currentClubId.isNotBlank()) {
                        FirebaseStorage.syncDraftMatchScore(currentClubId, first, second, score)
                        FirebaseStorage.syncDraftMatchScore(currentClubId, second, first, reverseScore(score))
                    }

                    draft.lastFinishedPlayers.clear()
                    draft.lastFinishedPlayers.addAll(listOf(first, second))
                    if (currentClubId.isNotBlank()) {
                        FirebaseStorage.syncDraftLastFinishedPlayers(currentClubId, draft.lastFinishedPlayers)
                    }

                    val normalized = normalizedPair(first, second)
                    val idx = draft.activeRoundRobinMatches.indexOf(normalized)
                    if (idx != -1) {
                        draft.activeRoundRobinMatches[idx] = null
                    }
                    refreshActiveRoundRobinMatches(
                        draft = draft,
                        playerCount = playerNames.size,
                        tablesCount = draft.tablesCount
                    )
                    if (currentClubId.isNotBlank()) {
                        FirebaseStorage.syncDraftActiveMatches(currentClubId, draft.activeRoundRobinMatches)
                    }
                    selectedPair = null
                },
                onDelete = {
                    draft.matchScores.remove(first to second)
                    draft.matchScores.remove(second to first)
                    
                    if (currentClubId.isNotBlank()) {
                        FirebaseStorage.syncDraftProperty(currentClubId, "matchScores/${first}_${second}", null)
                        FirebaseStorage.syncDraftProperty(currentClubId, "matchScores/${second}_${first}", null)
                    }

                    rebuildTechnicalResults(
                        clubId = currentClubId,
                        draft = draft,
                        playerCount = playerNames.size
                    )

                    selectedPair = null
                },
                onWithdrawPlayer = { playerToWithdraw ->
                    val withdrawnIndex = if (playerToWithdraw == cp1) first else second
                    if (withdrawnIndex !in draft.withdrawnPlayers) {
                        draft.withdrawnPlayers.add(withdrawnIndex)
                        if (currentClubId.isNotBlank()) {
                            FirebaseStorage.syncDraftWithdrawnPlayers(currentClubId, draft.withdrawnPlayers)
                        }
                    }

                    draft.lastFinishedPlayers.clear()
                    draft.lastFinishedPlayers.addAll(listOf(first, second))
                    if (currentClubId.isNotBlank()) {
                        FirebaseStorage.syncDraftLastFinishedPlayers(currentClubId, draft.lastFinishedPlayers)
                    }

                    rebuildTechnicalResults(
                        clubId = currentClubId,
                        draft = draft,
                        playerCount = playerNames.size
                    )
                    selectedPair = null
                }
            )

        }
    }


    if (restorePlayerCandidate != null) {
        val playerIndex = restorePlayerCandidate!!
        val pName = playerNames.getOrNull(playerIndex) ?: "Игрок"
        AlertDialog(
            onDismissRequest = { restorePlayerCandidate = null },
            title = { Text("Вернуть в турнир?", style = AppTypography.titleLarge) },
            text = { Text("Вы хотите отменить снятие игрока $pName и вернуть его в турнир?", style = AppTypography.bodyMedium) },
            confirmButton = {
                TextButton(onClick = {
                    draft.withdrawnPlayers.remove(playerIndex)
                    rebuildTechnicalResults(draft, playerNames.size)
                    restorePlayerCandidate = null
                }) { Text("Вернуть", color = AppleBlue, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { restorePlayerCandidate = null }) { Text("Отмена", color = TextGray) }
            },
            containerColor = CardWhite,
            shape = RoundedCornerShape(24.dp)
        )
    }

    if (askPlaceBonusDialog) {
        AlertDialog(
            onDismissRequest = { askPlaceBonusDialog = false },
            title = { Text("Бонусы за медали") },
            text = {
                Column(
                    modifier = Modifier
                        .heightIn(max = 180.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text("🥇 +3   🥈 +2   🥉 +1", color = TextDark, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Медали сохранятся в базе в любом случае. Бонусы к рейтингу начислять только по решению судьи.",
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
                    Text("Начислить", color = AppleBlue)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        askPlaceBonusDialog = false
                        onFinish(false)
                    }
                ) {
                    Text("Пропустить", color = TextGray)
                }
            },
            containerColor = CardWhite
        )
    }

    statusPlayerIndex?.let { playerIndex ->
        val playerName = playerNames.getOrElse(playerIndex) { "Игрок" }
        val playerPlace = placesByPlayer[playerIndex] ?: (playerIndex + 1)

        val liveRatingResult = applyRoundRobinRatings(
            existingPlayers = clubPlayers,
            playerNames = playerNames,
            scores = draft.matchScores,
            fixedK = draft.fixedK,
            applyPlaceBonuses = false
        )
        val playerImpacts = liveRatingResult.matchImpacts.filter {
            it.player1.equals(playerName, ignoreCase = true) || it.player2.equals(playerName, ignoreCase = true)
        }

        Dialog(onDismissRequest = { statusPlayerIndex = null }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        playerName,
                        style = AppTypography.headlineSmall,
                        color = TextDark,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Текущее место: ", style = AppTypography.bodyLarge, color = TextGray)
                        Text("$playerPlace", style = AppTypography.titleLarge, color = AppleBlue, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Сыгранные матчи", style = AppTypography.titleMedium, color = TextDark, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    if (playerImpacts.isEmpty()) {
                        Text("Нет сыгранных матчей", color = TextGray, style = AppTypography.bodyMedium)
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 300.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            playerImpacts.forEach { impact ->
                                val isWinner = impact.player1.equals(playerName, ignoreCase = true)
                                val opponentName = if (isWinner) impact.player2 else impact.player1
                                val delta = if (isWinner) impact.p1Delta else impact.p2Delta

                                val scoreParts = impact.score.split(":")
                                val displayScore = if (scoreParts.size == 2) {
                                    if (isWinner) "${scoreParts[0]}:${scoreParts[1]}" else "${scoreParts[1]}:${scoreParts[0]}"
                                } else impact.score

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(BgLight, RoundedCornerShape(8.dp))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(opponentName, style = AppTypography.bodyMedium, color = TextDark, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text("Счет: $displayScore", style = AppTypography.bodySmall, color = TextGray)
                                    }
                                    val deltaColor = if (delta > 0) GreenBadgeText else if (delta < 0) SwipeDeleteRed else TextGray
                                    Text(
                                        formatDelta(delta),
                                        style = AppTypography.titleMedium,
                                        color = deltaColor,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
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
    participantCount: Int,
    bestOfWins: Int,
    tablesCount: Int,
    tournamentIsComplete: Boolean,
    onBack: () -> Unit,
    onFinish: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppBackButton(onClick = onBack)

        Spacer(modifier = Modifier.width(16.dp))

        Text("🏆", fontSize = 32.sp)

        Spacer(modifier = Modifier.width(12.dp))

        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                tournamentName.uppercase(),
                style = AppTypography.displayLarge.copy(fontSize = 24.sp, fontWeight = FontWeight.ExtraBold),
                color = TextDark,
                maxLines = 1
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                "Участников: $participantCount",
                style = AppTypography.bodyMedium,
                color = TextGray,
                maxLines = 1,
                modifier = Modifier.padding(bottom = 3.dp)
            )
        }

        if (tournamentIsComplete) {
            Button(
                onClick = onFinish,
                colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("🏆 Завершить", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun TopBarChip(text: String, icon: ImageVector) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BlueBadgeBg),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = AppleBlue, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text, color = AppleBlue, style = AppTypography.bodyMedium, fontWeight = FontWeight.Medium)
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
    assignments: List<Pair<Int, Int>?>,
    queueMatches: List<Pair<Int, Int>>,
    playerNames: List<String>,
    clubPlayers: List<ClubPlayer>,
    configuredTablesCount: Int,
    isVertical: Boolean = false,
    onMatchClick: (Pair<Int, Int>) -> Unit
) {
    val containerModifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()

    val content = @Composable { scope: Any? ->
        val activeTableModifier = if (isVertical) {
            Modifier.fillMaxWidth()
        } else if (scope is RowScope) {
            with(scope) { Modifier.weight(1f) }
        } else {
            Modifier.fillMaxWidth()
        }

        // КТО ИГРАЕТ СЕЙЧАС (Active Tables)
        Card(
            modifier = activeTableModifier,
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, BorderGray)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("КТО ИГРАЕТ СЕЙЧАС", style = AppTypography.bodyMedium, color = TextGray, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                val activeAssignments = assignments.filterNotNull()
                if (activeAssignments.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(60.dp), contentAlignment = Alignment.Center) {
                        Text("Нет активных матчей", color = TextGray)
                    }
                } else {
                    assignments.forEachIndexed { index, pair ->
                        if (pair != null) {
                            ActiveTableCard(
                                tableNumber = index + 1,
                                player1 = playerNames.getOrElse(pair.first) { "" },
                                player2 = playerNames.getOrElse(pair.second) { "" },
                                clubPlayers = clubPlayers,
                                onClick = { onMatchClick(pair) }
                            )
                            if (index < assignments.lastIndex) Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }

        if (isVertical) {
            Spacer(modifier = Modifier.height(12.dp))
        }

        // ОЧЕРЕДЬ К СТОЛАМ (Queue)
        Card(
            modifier = activeTableModifier,
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, BorderGray)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("ОЧЕРЕДЬ К СТОЛАМ", style = AppTypography.bodyMedium, color = TextGray, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (queueMatches.isEmpty()) {
                        Text("Очередь пуста", color = TextGray, modifier = Modifier.padding(top = 8.dp))
                    } else {
                        queueMatches.take(2).forEachIndexed { index, pair ->
                            QueueMatchRow(
                                orderNumber = index + 1,
                                player1Name = playerNames.getOrElse(pair.first) { "" },
                                player2Name = playerNames.getOrElse(pair.second) { "" },
                                clubPlayers = clubPlayers
                            )
                        }
                        if (queueMatches.size > 2) {
                            Text(
                                "и еще ${queueMatches.size - 2} в ожидании...",
                                style = AppTypography.bodyMedium,
                                color = TextGray,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (isVertical) {
        Column(modifier = containerModifier) {
            content(null)
        }
    } else {
        Row(
            modifier = containerModifier,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            content(this)
        }
    }
}

@Composable
private fun ActiveTableCard(
    tableNumber: Int,
    player1: String,
    player2: String,
    clubPlayers: List<ClubPlayer>,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderGray)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(AppleBlue)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        "Стол $tableNumber",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            PlayerMiniRow(name = player1, clubPlayers = clubPlayers)
            Spacer(modifier = Modifier.height(10.dp))
            PlayerMiniRow(name = player2, clubPlayers = clubPlayers)
        }
    }
}

@Composable
private fun PlayerMiniRow(name: String, clubPlayers: List<ClubPlayer>) {
    val player = getClubPlayerByName(name, clubPlayers)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        PlayerAvatar(player = player ?: ClubPlayer(0, name), size = 36.dp)
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                name,
                style = AppTypography.labelLarge.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold),
                color = TextDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                formatRating(player?.rating ?: 100.0),
                color = AppleBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun QueueMatchRow(
    orderNumber: Int,
    player1Name: String,
    player2Name: String,
    clubPlayers: List<ClubPlayer>
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text("$orderNumber", style = AppTypography.bodyMedium, color = TextGray, modifier = Modifier.width(20.dp))

        Box(modifier = Modifier.weight(1f)) {
            PlayerMiniRowQueue(name = player1Name, clubPlayers = clubPlayers)
        }

        Text("—", modifier = Modifier.padding(horizontal = 4.dp), color = TextGray)

        Box(modifier = Modifier.weight(1f)) {
            PlayerMiniRowQueue(name = player2Name, clubPlayers = clubPlayers)
        }
    }
}

@Composable
private fun PlayerMiniRowQueue(name: String, clubPlayers: List<ClubPlayer>) {
    val player = getClubPlayerByName(name, clubPlayers)
    Row(verticalAlignment = Alignment.CenterVertically) {
        PlayerAvatar(player = player ?: ClubPlayer(0, name), size = 28.dp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            name,
            style = AppTypography.labelLarge.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold),
            color = TextDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            formatRating(player?.rating ?: 100.0),
            color = AppleBlue,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold
        )
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
    clubPlayers: List<ClubPlayer>,
    scores: Map<Pair<Int, Int>, String>,
    placesByPlayer: Map<Int, Int>,
    pointsByPlayer: Map<Int, Int>,
    activeMatches: List<Pair<Int, Int>?>,
    activePlayers: Set<Int>,
    withdrawnPlayers: Set<Int>,
    selectedPair: Pair<Int, Int>?,
    searchQuery: String,
    tableScale: Float,
    onCellClick: (Int, Int) -> Unit,
    onPlayerClick: (Int) -> Unit
) {
    val horizontalScroll = rememberScrollState()

    val scale = tableScale.coerceIn(0.85f, 1.35f)
    val rowHeight = (64f * scale).dp
    val headerHeight = (42f * scale).dp
    val scoreColumnWidth = (68f * scale).dp
    val leftColumnWidth = (320f * scale).coerceIn(240f, 450f).dp
    val pointsColumnWidth = (66f * scale).dp

    val normalizedActiveMatches = activeMatches.map { if (it != null) normalizedPair(it.first, it.second) else null }

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
        modifier = Modifier.wrapContentWidth()
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
                    modifier = Modifier.weight(1f).padding(start = (42f * scale).dp),
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
                        .clickable { onPlayerClick(playerIndex) }
                        .padding(horizontal = (8f * scale).dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${playerIndex + 1}",
                        modifier = Modifier.width((28f * scale).dp),
                        textAlign = TextAlign.Start,
                        color = TextGray,
                        fontSize = (12f * scale).sp
                    )

                    val clubPlayer = getClubPlayerByName(playerName, clubPlayers)
                    PlayerAvatar(player = clubPlayer ?: ClubPlayer(0, playerName), size = (36f * scale).dp)

                    Spacer(modifier = Modifier.width((10f * scale).dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            playerName,
                            color = TextDark,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            fontSize = (14f * scale).sp
                        )
                        Text(
                            formatRating(clubPlayer?.rating ?: 100.0),
                            color = AppleBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = (11f * scale).sp
                        )
                    }
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
                        val isActive = !isDiagonal && normalized in normalizedActiveMatches
                        val isSelected = !isDiagonal &&
                                selectedPair?.let {
                                    normalizedPair(it.first, it.second) == normalized
                                } == true
                        val isSearchCross = playerIndex in matchedPlayers || opponentIndex in matchedPlayers

                        val technicalWin = score == "W"
                        val technicalLoss = score == "L"
                        val activeTableNumber = normalizedActiveMatches
                            .indexOf(normalized)
                            .takeIf { it >= 0 }
                            ?.plus(1)

                        val backgroundColor = when {
                            isDiagonal -> Color(0xFFF0F0F2)
                            technicalWin -> GreenBadgeBg
                            technicalLoss -> CellLostBg
                            isActive -> GreenBadgeBg
                            isSearchCross -> Color(0xFFFFFAE6)
                            else -> Color.Transparent
                        }

                        val scoreColor = when {
                            technicalWin -> Color(0xFF24A148)
                            technicalLoss -> Color(0xFFFF3B30)
                            parsed != null && parsed.first > parsed.second -> Color(0xFF24A148)
                            parsed != null && parsed.first < parsed.second -> Color(0xFFFF3B30)
                            isActive -> Color(0xFF24A148)
                            else -> TextGray
                        }

                        val canOpenScore = !isDiagonal

                        val isWin = parsed != null && parsed.first > parsed.second
                        val isLoss = parsed != null && parsed.first < parsed.second

                        Box(
                            modifier = Modifier
                                .height(rowHeight)
                                .fillMaxWidth()
                                .background(
                                    if (isDiagonal) Color(0xFFF2F4F7) else Color.White
                                )
                                .border(
                                    width = when {
                                        isDiagonal -> 0.dp
                                        isSelected -> 2.dp
                                        isActive -> 1.5.dp
                                        isSearchCross -> 1.dp
                                        else -> 0.5.dp
                                    },
                                    color = when {
                                        isDiagonal -> Color.Transparent
                                        isSelected -> AppleBlue
                                        isActive -> Color(0xFF24A148)
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
    player1: ClubPlayer,
    player2: ClubPlayer,
    setsToWin: Int,
    existingScore: String?,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
    onDelete: () -> Unit,
    onWithdrawPlayer: (ClubPlayer) -> Unit
) {
    var showWithdrawalDialog by remember { mutableStateOf(false) }

    val firstPlayerWins = buildList {
        for (loserScore in 0 until setsToWin) {
            add("${setsToWin}:${loserScore}")
        }
    }

    val secondPlayerWins = firstPlayerWins.map(::reverseScore)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 700.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        PlayerIdentityBlock(player = player1)
                    }

                    Text(
                        "VS",
                        color = TextGray.copy(alpha = 0.4f),
                        style = AppTypography.titleLarge,
                        modifier = Modifier.padding(horizontal = 4.dp).padding(top = 28.dp)
                    )

                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        PlayerIdentityBlock(player = player2)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    val isNarrow = maxWidth < 480.dp
                    if (isNarrow) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            QuickScoreRowRedesign(
                                scores = firstPlayerWins,
                                background = CellWonBg,
                                foreground = AppleBlue,
                                onSave = onSave
                            )
                            QuickScoreRowRedesign(
                                scores = secondPlayerWins,
                                background = CellLostBg,
                                foreground = CellLostText,
                                onSave = onSave
                            )
                        }
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            QuickScoreRowRedesign(
                                scores = firstPlayerWins,
                                background = CellWonBg,
                                foreground = AppleBlue,
                                modifier = Modifier.weight(1f),
                                onSave = onSave
                            )
                            QuickScoreRowRedesign(
                                scores = secondPlayerWins,
                                background = CellLostBg,
                                foreground = CellLostText,
                                modifier = Modifier.weight(1f),
                                onSave = onSave
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                HorizontalDivider(color = BorderGray)
                Spacer(modifier = Modifier.height(20.dp))

                Text("Особые условия", style = AppTypography.bodyMedium, color = TextGray, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { showWithdrawalDialog = true },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    border = BorderStroke(1.dp, SwipeDeleteRed.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Снялся с турнира (L)", color = SwipeDeleteRed, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                if (showWithdrawalDialog) {
                    AlertDialog(
                        onDismissRequest = { showWithdrawalDialog = false },
                        title = { Text("Кто снялся?", style = AppTypography.titleLarge) },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                Text("Все несыгранные матчи выбранного игрока будут отмечены как техническое поражение (L).", style = AppTypography.bodyMedium)

                                Button(
                                    onClick = {
                                        onWithdrawPlayer(player1)
                                        showWithdrawalDialog = false
                                    },
                                    modifier = Modifier.fillMaxWidth().height(56.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = BlueBadgeBg, contentColor = AppleBlue),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Text(player1.fullName, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                                }

                                Button(
                                    onClick = {
                                        onWithdrawPlayer(player2)
                                        showWithdrawalDialog = false
                                    },
                                    modifier = Modifier.fillMaxWidth().height(56.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = BlueBadgeBg, contentColor = AppleBlue),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Text(player2.fullName, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                                }
                            }
                        },
                        confirmButton = {},
                        dismissButton = {
                            TextButton(onClick = { showWithdrawalDialog = false }) {
                                Text("Отмена", color = TextGray, fontSize = 16.sp)
                            }
                        },
                        containerColor = CardWhite,
                        shape = RoundedCornerShape(24.dp)
                    )
                }

                if (existingScore != null) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onDelete,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SwipeDeleteRed.copy(alpha = 0.08f),
                            contentColor = SwipeDeleteRed
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Удалить результат", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerIdentityBlock(
    player: ClubPlayer
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        PlayerAvatar(player = player, size = 80.dp)
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            player.fullName,
            style = AppTypography.titleLarge,
            color = TextDark,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(6.dp))
        RatingChip(rating = player.rating)
    }
}

@Composable
private fun RatingChip(rating: Double) {
    Surface(
        color = BgLight,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = formatRating(rating),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = AppTypography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = AppleBlue
        )
    }
}

@Composable
private fun QuickScoreRowRedesign(
    scores: List<String>,
    background: Color,
    foreground: Color,
    modifier: Modifier = Modifier,
    onSave: (String) -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = background),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, foreground.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Выбери счет", color = foreground.copy(alpha = 0.7f), style = AppTypography.bodySmall)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                scores.forEach { score ->
                    Button(
                        onClick = { onSave(score) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.weight(1f).height(50.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Text(score, color = foreground, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerScoreCard(player: ClubPlayer, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        PlayerAvatar(player = player, size = 54.dp)
        Spacer(modifier = Modifier.width(12.dp))
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Text(
                player.fullName,
                style = AppTypography.labelLarge.copy(fontSize = 15.sp),
                color = TextDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                formatRating(player.rating),
                style = AppTypography.bodyMedium,
                color = AppleBlue,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ScoreButton(text: String, bgColor: Color, textColor: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = bgColor),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth().height(56.dp),
        elevation = null
    ) {
        Text(text, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
    }
}

@Composable
private fun TechnicalButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        border = BorderStroke(1.dp, BorderGray),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.height(48.dp)
    ) {
        Text(text, color = TextDark, style = AppTypography.labelLarge)
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
private fun QuickScoreRow(
    winnerLabel: String,
    playerName: String,
    scores: List<String>,
    background: Color,
    foreground: Color,
    modifier: Modifier = Modifier,
    onSave: (String) -> Unit
) {
    Card(
        modifier = modifier.fillMaxHeight(),
        colors = CardDefaults.cardColors(containerColor = background),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, foreground.copy(alpha = 0.18f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Text(winnerLabel, color = TextGray, style = AppTypography.bodyMedium)
            Text(
                playerName,
                color = foreground,
                fontWeight = FontWeight.Bold,
                style = AppTypography.titleLarge,
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
                            fontSize = 22.sp
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
fun FinalResultsScreen(
    draft: TournamentDraft,
    onBackToTable: () -> Unit,
    onGoHome: () -> Unit
) {
    val context = LocalContext.current
    BackHandler(onBack = onBackToTable)

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
            appendLine("${place + 1}. ${playerNames.getOrElse(stat.index) { "Игрок" }}")
        }

        if (draft.matchImpacts.isNotEmpty()) {
            appendLine()
            appendLine("Результаты матчей:")
            draft.matchImpacts.forEach { impact ->
                appendLine("${impact.player1} [${impact.score}] ${impact.player2}")
            }
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

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(BgLight)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(12.dp)
    ) {
        val isPortrait = maxWidth < 720.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppBackButton(onClick = onBackToTable)

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Турнир завершён",
                        style = AppTypography.titleLarge,
                        color = TextDark,
                        maxLines = 1
                    )
                    Text(
                        "${draft.name.ifBlank { "Теннисный турнир" }} • стрелка назад вернёт к шахматке",
                        style = AppTypography.bodyMedium,
                        color = TextGray,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            CompactTopThreeRow(
                standings = standings,
                playerNames = playerNames
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isPortrait) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ResultsPlacesCard(
                        modifier = Modifier.fillMaxWidth(),
                        standings = standings,
                        playerNames = playerNames
                    )

                    RatingChangesCard(
                        modifier = Modifier.fillMaxWidth(),
                        ratingChanges = draft.ratingChanges
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ResultsPlacesCard(
                        modifier = Modifier.weight(1.08f),
                        standings = standings,
                        playerNames = playerNames
                    )

                    RatingChangesCard(
                        modifier = Modifier.weight(1f),
                        ratingChanges = draft.ratingChanges
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBackToTable,
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(1.dp, AppleBlue),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("← К шахматке", color = AppleBlue, style = AppTypography.labelLarge)
                }

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
                    Text("↗ Поделиться", color = AppleBlue, style = AppTypography.labelLarge)
                }

                Button(
                    onClick = onGoHome,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("✓ На главную", color = Color.White, style = AppTypography.labelLarge)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}




// ==========================================
// 5. ЭКРАН ПЛЕЙ-ОФФ И ГРУПП (НОВЫЙ!)
// ==========================================
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CompactTopThreeRow(
    standings: List<PlayerStat>,
    playerNames: List<String>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 82.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CompactTopPlaceCard(
            modifier = Modifier.weight(1f),
            place = 1,
            medal = "🥇",
            playerName = standings.getOrNull(0)?.let { playerNames.getOrElse(it.index) { "Игрок" } } ?: "—",
            points = standings.getOrNull(0)?.points ?: 0,
            background = GoldBg,
            border = GoldBorder
        )
        CompactTopPlaceCard(
            modifier = Modifier.weight(1f),
            place = 2,
            medal = "🥈",
            playerName = standings.getOrNull(1)?.let { playerNames.getOrElse(it.index) { "Игрок" } } ?: "—",
            points = standings.getOrNull(1)?.points ?: 0,
            background = SilverBg,
            border = SilverBorder
        )
        CompactTopPlaceCard(
            modifier = Modifier.weight(1f),
            place = 3,
            medal = "🥉",
            playerName = standings.getOrNull(2)?.let { playerNames.getOrElse(it.index) { "Игрок" } } ?: "—",
            points = standings.getOrNull(2)?.points ?: 0,
            background = BronzeBg,
            border = BronzeBorder
        )
    }
}

@Composable
private fun CompactTopPlaceCard(
    modifier: Modifier = Modifier,
    place: Int,
    medal: String,
    playerName: String,
    points: Int,
    background: Color,
    border: Color
) {
    Card(
        modifier = modifier.wrapContentHeight(),
        colors = CardDefaults.cardColors(containerColor = background),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, border)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(medal, fontSize = 28.sp)

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("$place место", color = TextGray, fontSize = 12.sp, maxLines = 1)
                Text(
                    playerName,
                    color = TextDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ResultsPlacesCard(
    modifier: Modifier = Modifier,
    standings: List<PlayerStat>,
    playerNames: List<String>
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, BorderGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Все места", style = AppTypography.titleLarge, color = TextDark)
            Text("Итоговая таблица турнира", style = AppTypography.bodyMedium, color = TextGray)

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                standings.forEachIndexed { place, stat ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "${place + 1}",
                            modifier = Modifier.width(36.dp),
                            color = AppleBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        val medal = if (place == 0) "🥇 " else if (place == 1) "🥈 " else if (place == 2) "🥉 " else ""
                        Text(
                            "$medal${playerNames.getOrElse(stat.index) { "Игрок" }}",
                            modifier = Modifier.weight(1f),
                            color = TextDark,
                            fontWeight = if (place < 3) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1
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
private fun RatingChangesCard(
    modifier: Modifier = Modifier,
    ratingChanges: List<RatingChange>
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, BorderGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Рейтинг", style = AppTypography.titleLarge, color = TextDark)
            Text("Изменения после турнира", style = AppTypography.bodyMedium, color = TextGray)

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (ratingChanges.isEmpty()) {
                    Text(
                        "Рейтинг не изменился.",
                        color = TextGray,
                        style = AppTypography.bodyMedium
                    )
                } else {
                    ratingChanges.forEach { change ->
                        val delta = change.newRating - change.oldRating

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    change.fullName,
                                    color = TextDark,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                                Text(
                                    "${formatRating(change.oldRating)} → ${formatRating(change.newRating)}",
                                    color = TextGray,
                                    style = AppTypography.bodyMedium
                                )
                            }

                            Text(
                                formatDelta(delta),
                                color = if (delta >= 0) GreenBadgeText else CellLostText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                        }

                        HorizontalDivider(color = BorderGray)
                    }
                }
            }
        }
    }
}

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

            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayoffScreen(draft: TournamentDraft, onBack: () -> Unit) {
    val currentClubId = LocalCurrentClubId.current ?: ""
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().background(BgLight).windowInsetsPadding(WindowInsets.safeDrawing)) {
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
                        if (currentClubId.isNotBlank()) {
                            FirebaseStorage.syncDraftPlayoffScore(currentClubId, selectedMatch!!.id, score)
                        }
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


@Composable
@OptIn(ExperimentalFoundationApi::class)
fun ClubSelectionScreen(
    onSelectClub: (String) -> Unit,
    onSelectClubAdmin: (String) -> Unit,
    onAddClub: () -> Unit,
    onRemoveClub: (String) -> Unit
) {
    val context = LocalContext.current
    val savedClubs = getSavedClubs(context).toList()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Мои клубы",
            style = AppTypography.displayLarge.copy(fontSize = 32.sp, fontWeight = FontWeight.Bold),
            color = TextDark
        )
        Spacer(modifier = Modifier.height(32.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f, fill = false),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(savedClubs) { clubId ->
                val isAdmin = isClubAdmin(context, clubId)
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BorderGray),
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = { onSelectClub(clubId) },
                            onLongClick = { onSelectClubAdmin(clubId) }
                        )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                clubId.replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.getDefault()) else it.toString() },
                                style = AppTypography.titleLarge,
                                color = TextDark
                            )
                            if (isAdmin) {
                                Text("Организатор", style = AppTypography.bodySmall, color = AppleBlue)
                            }
                        }
                        IconButton(onClick = { onRemoveClub(clubId) }) {
                            Icon(imageVector = TrashIcon, contentDescription = "Удалить", tint = SwipeDeleteRed)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onAddClub,
            colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("+ Войти в другой клуб", style = AppTypography.labelLarge, color = Color.White)
        }
    }
}

@Composable
fun ClubLoginScreen(
    onLoginSuccess: (String) -> Unit,
    onAdminLoginSuccess: (String, Boolean) -> Unit
) {
    val context = LocalContext.current
    var clubNameInput by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var newClubName by remember { mutableStateOf("") }
    var newAdminPin by remember { mutableStateOf("") }
    var newSecretWord by remember { mutableStateOf("") }
    var createError by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text("Добро пожаловать!", style = AppTypography.headlineMedium, color = TextDark)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Введите название вашего клуба", style = AppTypography.bodyLarge, color = TextGray)
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = clubNameInput,
                onValueChange = { clubNameInput = it; errorMessage = null },
                label = { Text("Название клуба") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(errorMessage!!, color = Color.Red, style = AppTypography.bodySmall)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val clubs = loadClubsRegistry(context)
                    val formattedId = clubNameInput.trim().lowercase().replace("\\s+".toRegex(), "")
                    if (formattedId.isEmpty()) {
                        errorMessage = "Введите название"
                        return@Button
                    }
                    val found = clubs.find { it.clubId == formattedId }
                    if (found != null) {
                        onLoginSuccess(found.clubId)
                    } else {
                        FirebaseStorage.fetchClubConfig(formattedId) { onlineConfig ->
                            if (onlineConfig != null) {
                                val updatedClubs = loadClubsRegistry(context).toMutableList()
                                if (!updatedClubs.any { it.clubId == formattedId }) {
                                    updatedClubs.add(onlineConfig)
                                    saveClubsRegistry(context, updatedClubs)
                                }
                                addSavedClub(context, formattedId)
                                onLoginSuccess(onlineConfig.clubId)
                            } else {
                                errorMessage = "Клуб не найден"
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ) {
                Text("Войти", modifier = Modifier.padding(8.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            TextButton(onClick = { showCreateDialog = true }) {
                Text("Создать новый клуб", color = AppleBlue)
            }
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Создание клуба") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newClubName,
                        onValueChange = { newClubName = it },
                        label = { Text("Название") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                    )
                    OutlinedTextField(
                        value = newAdminPin,
                        onValueChange = { newAdminPin = it },
                        label = { Text("ПИН-код") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newSecretWord,
                        onValueChange = { newSecretWord = it },
                        label = { Text("Секретное слово") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                    )
                    if (createError != null) {
                        Text(createError!!, color = Color.Red, style = AppTypography.bodySmall)
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val formattedId = newClubName.trim().lowercase().replace("\\s+".toRegex(), "")
                    if (formattedId.isEmpty() || newAdminPin.isEmpty() || newSecretWord.isEmpty()) {
                        createError = "Заполните все поля"
                        return@Button
                    }
                    FirebaseStorage.fetchClubConfig(formattedId) { existing ->
                        if (existing != null) {
                            createError = "Такой клуб уже существует в базе"
                        } else {
                            val newConfig = ClubConfig(formattedId, newClubName.trim(), newAdminPin.trim(), newSecretWord.trim())
                            val clubs = loadClubsRegistry(context).toMutableList()
                            if (!clubs.any { it.clubId == formattedId }) {
                                clubs.add(newConfig)
                                saveClubsRegistry(context, clubs)
                            }
                            FirebaseStorage.saveClubConfig(newConfig)
                            showCreateDialog = false
                            onAdminLoginSuccess(formattedId, true)
                        }
                    }
                }) { Text("Создать") }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) { Text("Отмена") }
            }
        )
    }
}

@Composable
fun AdminAuthDialogs(
    onDismiss: () -> Unit,
    onAdminSuccess: () -> Unit
) {
    val context = LocalContext.current
    val currentClubId = LocalCurrentClubId.current
    var showPinDialog by remember { mutableStateOf(true) }
    var showRecoverDialog by remember { mutableStateOf(false) }

    var pinInput by remember { mutableStateOf("") }
    var secretWordInput by remember { mutableStateOf("") }
    var newPinInput by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Режим организатора") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Введите ПИН-код организатора:")
                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = { pinInput = it; errorMsg = null },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true
                    )
                    if (errorMsg != null) {
                        Text(errorMsg!!, color = Color.Red, style = AppTypography.bodySmall)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = { showPinDialog = false; showRecoverDialog = true }) {
                        Text("Забыли ПИН-код?", color = AppleBlue)
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val clubs = loadClubsRegistry(context)
                    val config = clubs.find { it.clubId == currentClubId }
                    if (config?.adminPin == pinInput.trim()) {
                        onAdminSuccess()
                    } else {
                        errorMsg = "Неверный ПИН-код"
                    }
                }) { Text("Войти") }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Отмена") }
            }
        )
    }

    if (showRecoverDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Сброс ПИН-кода") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = secretWordInput,
                        onValueChange = { secretWordInput = it; errorMsg = null },
                        label = { Text("Секретное слово") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                    )
                    OutlinedTextField(
                        value = newPinInput,
                        onValueChange = { newPinInput = it; errorMsg = null },
                        label = { Text("Новый ПИН-код") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true
                    )
                    if (errorMsg != null) {
                        Text(errorMsg!!, color = Color.Red, style = AppTypography.bodySmall)
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val clubs = loadClubsRegistry(context).toMutableList()
                    val idx = clubs.indexOfFirst { it.clubId == currentClubId }
                    if (idx != -1) {
                        val config = clubs[idx]
                        if (config.secretWord == secretWordInput.trim()) {
                            if (newPinInput.isBlank()) {
                                errorMsg = "Введите новый ПИН"
                                return@Button
                            }
                            clubs[idx] = config.copy(adminPin = newPinInput.trim())
                            saveClubsRegistry(context, clubs)
                            onAdminSuccess()
                        } else {
                            errorMsg = "Неверное секретное слово"
                        }
                    }
                }) { Text("Сохранить") }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Отмена") }
            }
        )
    }
}
