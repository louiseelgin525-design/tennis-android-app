package com.example.tennistourney

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


object FirebaseStorage {
    private val database = Firebase.database("https://tennis-tourney-8255a-default-rtdb.europe-west1.firebasedatabase.app")
    private val gson = Gson()

    // ----------------------------------------------------
    // PLAYERS
    // ----------------------------------------------------
    fun savePlayers(clubId: String, players: List<ClubPlayer>) {
        GlobalScope.launch(Dispatchers.IO) {
            val json = gson.toJson(players)
            database.getReference("clubs/$clubId/players").setValue(json)
        }
    }

    fun listenToPlayers(clubId: String, onUpdate: (List<ClubPlayer>) -> Unit): ValueEventListener {
        val ref = database.getReference("clubs/$clubId/players")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val json = snapshot.getValue(String::class.java)
                GlobalScope.launch(Dispatchers.IO) {
                    val list = if (json != null) {
                        try {
                            val type = object : TypeToken<List<ClubPlayer>>() {}.type
                            gson.fromJson<List<ClubPlayer>>(json, type) ?: emptyList()
                        } catch (e: Exception) {
                            emptyList()
                        }
                    } else {
                        emptyList()
                    }
                    withContext(Dispatchers.Main) {
                        onUpdate(list)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        ref.addValueEventListener(listener)
        return listener
    }

    fun removePlayersListener(clubId: String, listener: ValueEventListener) {
        database.getReference("clubs/$clubId/players").removeEventListener(listener)
    }

    // ----------------------------------------------------
    // HISTORY
    // ----------------------------------------------------
    fun saveHistory(clubId: String, history: List<TournamentHistoryEntry>) {
        GlobalScope.launch(Dispatchers.IO) {
            val json = gson.toJson(history)
            database.getReference("clubs/$clubId/history").setValue(json)
        }
    }

    fun listenToHistory(clubId: String, onUpdate: (List<TournamentHistoryEntry>) -> Unit): ValueEventListener {
        val ref = database.getReference("clubs/$clubId/history")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val json = snapshot.getValue(String::class.java)
                GlobalScope.launch(Dispatchers.IO) {
                    val list = if (json != null) {
                        try {
                            val type = object : TypeToken<List<TournamentHistoryEntry>>() {}.type
                            gson.fromJson<List<TournamentHistoryEntry>>(json, type) ?: emptyList()
                        } catch (e: Exception) {
                            emptyList()
                        }
                    } else {
                        emptyList()
                    }
                    withContext(Dispatchers.Main) {
                        onUpdate(list)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        ref.addValueEventListener(listener)
        return listener
    }

    fun removeHistoryListener(clubId: String, listener: ValueEventListener) {
        database.getReference("clubs/$clubId/history").removeEventListener(listener)
    }

    // ----------------------------------------------------
    // DRAFT
    // ----------------------------------------------------
    fun saveDraft(clubId: String, draft: TournamentDraft) {
        val json = gson.toJson(draft)
        database.getReference("clubs/$clubId/draft").setValue(json)
    }

    fun clearDraft(clubId: String) {
        database.getReference("clubs/$clubId/draft").removeValue()
    }

    fun listenToDraft(clubId: String, onUpdate: (TournamentDraft?) -> Unit): ValueEventListener {
        val ref = database.getReference("clubs/$clubId/draft")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val json = snapshot.getValue(String::class.java)
                if (json != null) {
                    try {
                        val type = object : TypeToken<TournamentDraft>() {}.type
                        val draft: TournamentDraft? = gson.fromJson(json, type)
                        onUpdate(draft)
                    } catch (e: Exception) {
                        onUpdate(null)
                    }
                } else {
                    onUpdate(null)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        ref.addValueEventListener(listener)
        return listener
    }

    fun removeDraftListener(clubId: String, listener: ValueEventListener) {
        database.getReference("clubs/$clubId/draft").removeEventListener(listener)
    }
    
    // ----------------------------------------------------
    // CLUB CONFIG (For online login validation)
    // ----------------------------------------------------
    fun saveClubConfig(config: ClubConfig) {
        val json = gson.toJson(config)
        database.getReference("clubConfigs/${config.clubId}").setValue(json)
    }
    
    fun fetchClubConfig(clubId: String, onResult: (ClubConfig?) -> Unit) {
        database.getReference("clubConfigs/$clubId").get().addOnSuccessListener { snapshot ->
            val json = snapshot.getValue(String::class.java)
            if (json != null) {
                try {
                    val config = gson.fromJson(json, ClubConfig::class.java)
                    onResult(config)
                } catch (e: Exception) {
                    onResult(null)
                }
            } else {
                onResult(null)
            }
        }.addOnFailureListener {
            onResult(null)
        }
    }
    
    // ----------------------------------------------------
    // ON-DEMAND AVATARS
    // ----------------------------------------------------
    fun savePlayerAvatar(clubId: String, playerId: Int, base64: String) {
        GlobalScope.launch(Dispatchers.IO) {
            database.getReference("clubs/$clubId/avatars/$playerId").setValue(base64)
        }
    }
    
    fun fetchPlayerAvatar(clubId: String, playerId: Int, onResult: (String) -> Unit) {
        database.getReference("clubs/$clubId/avatars/$playerId").get().addOnSuccessListener { snapshot ->
            onResult(snapshot.getValue(String::class.java).orEmpty())
        }.addOnFailureListener {
            onResult("")
        }
    }
    
    // ----------------------------------------------------
    // GRANULAR DRAFT SYNC
    // ----------------------------------------------------
    fun syncDraftProperty(clubId: String, path: String, value: Any?) {
        GlobalScope.launch(Dispatchers.IO) {
            database.getReference("clubs/$clubId/draft/$path").setValue(value)
        }
    }
    
    fun syncDraftPlayerFields(clubId: String, fields: List<PlayerField>) {
        GlobalScope.launch(Dispatchers.IO) {
            val json = gson.toJson(fields)
            database.getReference("clubs/$clubId/draft/playerFieldsJson").setValue(json)
        }
    }
    
    fun syncDraftMatchScore(clubId: String, p1: Int, p2: Int, score: String) {
        GlobalScope.launch(Dispatchers.IO) {
            database.getReference("clubs/$clubId/draft/matchScores/${p1}_${p2}").setValue(score)
        }
    }
    
    fun syncDraftMatchScoresMap(clubId: String, scores: Map<Pair<Int, Int>, String>) {
        GlobalScope.launch(Dispatchers.IO) {
            val formattedMap = scores.mapKeys { "${it.key.first}_${it.key.second}" }
            database.getReference("clubs/$clubId/draft/matchScores").setValue(formattedMap)
        }
    }
    
    fun syncDraftPlayoffScore(clubId: String, matchId: Int, score: String) {
        GlobalScope.launch(Dispatchers.IO) {
            database.getReference("clubs/$clubId/draft/playoffScores/$matchId").setValue(score)
        }
    }
    
    fun syncDraftWithdrawnPlayers(clubId: String, list: List<Int>) {
        GlobalScope.launch(Dispatchers.IO) {
            database.getReference("clubs/$clubId/draft/withdrawnPlayers").setValue(list)
        }
    }
    
    fun syncDraftActiveMatches(clubId: String, list: List<Pair<Int, Int>?>) {
        GlobalScope.launch(Dispatchers.IO) {
            val serialized = list.map { pair ->
                if (pair != null) "${pair.first}_${pair.second}" else ""
            }
            database.getReference("clubs/$clubId/draft/activeRoundRobinMatches").setValue(serialized)
        }
    }
    
    fun syncDraftLastFinishedPlayers(clubId: String, list: List<Int>) {
        GlobalScope.launch(Dispatchers.IO) {
            database.getReference("clubs/$clubId/draft/lastFinishedPlayers").setValue(list)
        }
    }
    
    fun listenToDraftGranular(clubId: String, onUpdate: (DataSnapshot) -> Unit): ValueEventListener {
        val ref = database.getReference("clubs/$clubId/draft")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                onUpdate(snapshot)
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        ref.addValueEventListener(listener)
        return listener
    }
    
    fun removeDraftGranularListener(clubId: String, listener: ValueEventListener) {
        database.getReference("clubs/$clubId/draft").removeEventListener(listener)
    }
}
