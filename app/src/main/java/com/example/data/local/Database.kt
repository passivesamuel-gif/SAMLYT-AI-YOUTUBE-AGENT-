package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "projects")
data class ProjectEntity(
  @PrimaryKey val id: String,
  val title: String,
  val type: String,
  val description: String,
  val timestamp: Long,
  val creationDate: Long = timestamp,
  val targetDuration: String,
  val tone: String,
  val content: String,
  val isEncrypted: Boolean,
  val channelTarget: String = "YouTube",
  val status: String = "In Production",
  val scriptMetadata: String = "",
  val storyboardMetadata: String = "",
  val scriptCount: Int = 0,
  val storyboardCount: Int = 0
)

@Entity(tableName = "user_scripts")
data class ScriptEntity(
  @PrimaryKey val id: String,
  val projectId: String,
  val title: String,
  val topic: String,
  val tone: String,
  val duration: String,
  val architecture: String,
  val scenesJson: String,
  val fullScriptText: String,
  val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "storyboard_metadata")
data class StoryboardEntity(
  @PrimaryKey val id: String,
  val projectId: String,
  val shotNumber: Int,
  val focalLength: String,
  val cameraMovement: String,
  val visualPrompt: String,
  val lighting: String,
  val aspectRatio: String = "16:9",
  val imageBase64: String? = null,
  val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "project_settings")
data class ProjectSettingsEntity(
  @PrimaryKey val key: String = "default_settings",
  val geminiApiKey: String = "",
  val email: String = "alex.creator@studio.samlyt.ai",
  val voiceName: String = "Kore (Female Voice)",
  val voiceGender: String = "Female",
  val productionFocus: String = "Cinematic Longform",
  val nleTarget: String = "Final Cut Pro (FCPXML)",
  val baseFramerate: String = "24 fps",
  val scriptVoice: String = "Aperture Studio (Female)",
  val lowLatencyVoice: Boolean = true,
  val zeroTelemetry: Boolean = true,
  val biometricLock: Boolean = true,
  val autoSaveIntervalSeconds: Int = 30,
  val vaultStorageUsedMb: Double = 1.2,
  val airGapArmed: Boolean = true,
  val updatedAt: Long = System.currentTimeMillis()
)

@Dao
interface ProjectDao {
  @Query("SELECT * FROM projects ORDER BY creationDate DESC")
  fun getAllProjects(): Flow<List<ProjectEntity>>

  @Query("SELECT * FROM projects WHERE status = :status ORDER BY creationDate DESC")
  fun getProjectsByStatus(status: String): Flow<List<ProjectEntity>>

  @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
  suspend fun getProjectById(id: String): ProjectEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProject(project: ProjectEntity)

  @Update
  suspend fun updateProject(project: ProjectEntity)

  @Query("UPDATE projects SET status = :status WHERE id = :id")
  suspend fun updateProjectStatus(id: String, status: String)

  @Query("UPDATE projects SET scriptMetadata = :scriptMeta, scriptCount = :scriptCount WHERE id = :id")
  suspend fun updateScriptMetadata(id: String, scriptMeta: String, scriptCount: Int)

  @Query("UPDATE projects SET storyboardMetadata = :storyboardMeta, storyboardCount = :storyboardCount WHERE id = :id")
  suspend fun updateStoryboardMetadata(id: String, storyboardMeta: String, storyboardCount: Int)

  @Query("DELETE FROM projects WHERE id = :id")
  suspend fun deleteProjectById(id: String)

  @Query("DELETE FROM projects")
  suspend fun deleteAll()
}

@Dao
interface ScriptDao {
  @Query("SELECT * FROM user_scripts WHERE projectId = :projectId ORDER BY timestamp DESC")
  fun getScriptsByProjectId(projectId: String): Flow<List<ScriptEntity>>

  @Query("SELECT * FROM user_scripts ORDER BY timestamp DESC")
  fun getAllScripts(): Flow<List<ScriptEntity>>

  @Query("SELECT * FROM user_scripts WHERE id = :id LIMIT 1")
  suspend fun getScriptById(id: String): ScriptEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertScript(script: ScriptEntity)

  @Query("DELETE FROM user_scripts WHERE id = :id")
  suspend fun deleteScriptById(id: String)

  @Query("DELETE FROM user_scripts WHERE projectId = :projectId")
  suspend fun deleteScriptsByProjectId(projectId: String)
}

@Dao
interface StoryboardDao {
  @Query("SELECT * FROM storyboard_metadata WHERE projectId = :projectId ORDER BY shotNumber ASC")
  fun getStoryboardsByProjectId(projectId: String): Flow<List<StoryboardEntity>>

  @Query("SELECT * FROM storyboard_metadata ORDER BY timestamp DESC")
  fun getAllStoryboards(): Flow<List<StoryboardEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertStoryboard(shot: StoryboardEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertStoryboards(shots: List<StoryboardEntity>)

  @Query("DELETE FROM storyboard_metadata WHERE id = :id")
  suspend fun deleteStoryboardById(id: String)

  @Query("DELETE FROM storyboard_metadata WHERE projectId = :projectId")
  suspend fun deleteStoryboardsByProjectId(projectId: String)
}

@Dao
interface SettingsDao {
  @Query("SELECT * FROM project_settings WHERE key = :key LIMIT 1")
  fun getSettingsFlow(key: String = "default_settings"): Flow<ProjectSettingsEntity?>

  @Query("SELECT * FROM project_settings WHERE key = :key LIMIT 1")
  suspend fun getSettingsSync(key: String = "default_settings"): ProjectSettingsEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSettings(settings: ProjectSettingsEntity)
}

@Database(
  entities = [
    ProjectEntity::class,
    ScriptEntity::class,
    StoryboardEntity::class,
    ProjectSettingsEntity::class
  ],
  version = 3,
  exportSchema = false
)
abstract class SamlytDatabase : RoomDatabase() {
  abstract fun projectDao(): ProjectDao
  abstract fun scriptDao(): ScriptDao
  abstract fun storyboardDao(): StoryboardDao
  abstract fun settingsDao(): SettingsDao

  companion object {
    @Volatile
    private var INSTANCE: SamlytDatabase? = null

    fun getDatabase(context: Context): SamlytDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          SamlytDatabase::class.java,
          "samlyt_studio.db"
        ).fallbackToDestructiveMigration(true).build()
        INSTANCE = instance
        instance
      }
    }
  }
}
