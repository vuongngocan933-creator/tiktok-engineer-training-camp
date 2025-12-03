package com.example.myapplication.data

import androidx.room.*

// 1. 定义用户实体表 (Table)
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val password: String, // ⚠ 注意：实际生产中密码必须哈希加密存储，此处仅演示逻辑
    val avatarPath: String? = null // 新增：头像文件的本地路径
)

// 2. 定义操作接口 (DAO)
@Dao
interface UserDao {
    // 查询匹配账号密码的用户
    @Query("SELECT * FROM users WHERE username = :user AND password = :pwd LIMIT 1")
    suspend fun login(user: String, pwd: String): User?

    // 插入用户（用于初始化测试数据）
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    // 检查表是否为空
    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int

    // 新增：获取用户信息
    @Query("SELECT * FROM users WHERE username = :name LIMIT 1")
    suspend fun getUser(name: String): User?
}

// 3. 定义数据库实例
@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        // 单例模式，防止创建多个数据库实例
        @Volatile private var instance: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
                    .fallbackToDestructiveMigration()
                    .build().also { instance = it }
            }
        }
    }
}