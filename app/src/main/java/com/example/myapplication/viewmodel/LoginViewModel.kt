package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.myapplication.data.User
import com.example.myapplication.data.UserDao
import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class LoginViewModel(private val userDao: UserDao) : ViewModel() {

    // UI状态：0=空闲, 1=登录成功, 2=登录失败
    private val _loginState = MutableStateFlow(0)
    val loginState = _loginState.asStateFlow()
    init {
        initTestData()
    }
    // 执行登录检查
    fun login(username: String, password: String) {
        viewModelScope.launch {
            val user = userDao.login(username, password)
            if (user != null) {
                _loginState.value = 1 // 成功
            } else {
                _loginState.value = 2 // 失败
                delay(100)
                _loginState.value = 0 // 延迟后重置状态
            }
        }
    }

    // 重置状态（用于登出或页面切换后）
    fun resetState() {
        _loginState.value = 0
    }

    // 初始化测试数据：如果数据库是空的，自动插入一个管理员账号
    fun initTestData() {
        viewModelScope.launch {
            // 检查数据库是否为空
            if (userDao.count() == 0) {
                // 注意：如果你的 User 表结构改了（比如加了 avatar 头像字段），
                // 这里创建 User 对象时可能需要补全参数，例如：
                // User(username = "admin", password = "123", avatar = null)
                userDao.insert(User(username = "admin", password = "123"))
            }
        }
    }
    // 新增：注册状态 (0:未操作, 1:成功, 2:失败/已存在)
    private val _registerState = MutableStateFlow(0)
    val registerState = _registerState.asStateFlow()

    // 新增：重置注册状态
    fun resetRegisterState() {
        _registerState.value = 0
    }

    // 新增：注册功能
    fun register(context: Context, username: String, pass: String, imageUri: Uri?) {
        viewModelScope.launch(Dispatchers.IO) {
            // 1. 检查用户是否存在
            val existingUser = userDao.getUser(username) // 假设你的Dao里有这个方法，没有的话需要去UserDao加一个 @Query("SELECT * FROM users WHERE username = :name LIMIT 1")

            if (existingUser != null) {
                _registerState.value = 2 // 用户名已存在
                delay(100)
                _registerState.value = 0
                return@launch
            }

            // 2. 处理头像图片 (将临时 URI 复制到 App 私有目录)
            var finalAvatarPath: String? = null
            if (imageUri != null) {
                finalAvatarPath = copyImageToInternalStorage(context, imageUri)
            }

            // 3. 插入数据库
            val newUser = User(username = username, password = pass, avatarPath = finalAvatarPath)
            userDao.insert(newUser)
            _registerState.value = 1 // 注册成功
        }
    }

    // 辅助函数：复制图片到内部存储
    private fun copyImageToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            // 创建一个新文件，名字随机防止冲突
            val fileName = "avatar_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)
            val outputStream = FileOutputStream(file)

            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath // 返回文件的绝对路径
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // 获取用户信息（为了在个人主页显示头像）
    suspend fun getUserInfo(username: String): User? {
        return userDao.getUser(username)
    }
}