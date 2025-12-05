package com.example.myapplication.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.* // 确保使用的是 material3
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

// 引入你的 Screen 和 ViewModel
import com.example.myapplication.data.User
import com.example.myapplication.viewmodel.LoginViewModel
import com.example.myapplication.ui.screens.*

// 定义导航项数据结构
sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Apps : BottomNavItem("apps", "应用", Icons.Default.Home)
    object Profile : BottomNavItem("profile_root", "我的", Icons.Default.Person)
    // 注意：这里的 route 叫 profile_root，仅仅是为了点击底部按钮时识别用
}

@Composable
fun AppNavigation(viewModelFactory: ViewModelProvider.Factory) {
    val navController = rememberNavController()
    val viewModel: LoginViewModel = viewModel(factory = viewModelFactory)
    val context = LocalContext.current

    // 1. 获取当前路由，用于判断底部导航栏的显示和选中状态
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // 2. 临时保存登录的用户名，以便底部导航栏跳转时使用
    var loggedInUsername by remember { mutableStateOf("") }

    // 3. 定义哪些页面需要显示底部导航栏
    // 注意：profile/{username} 是动态路由，需要特殊处理，这里简单判断是否包含 profile
    val showBottomBar = currentDestination?.route == "apps" ||
            currentDestination?.route?.startsWith("profile") == true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    // 定义两个按钮：应用 和 我的
                    val items = listOf(BottomNavItem.Apps, BottomNavItem.Profile)

                    items.forEach { item ->
                        // 判断是否选中：当前路由是否匹配 item 的路由
                        // 对于 Profile，因为真实路由是 "profile/{username}"，所以要模糊匹配
                        val isSelected = currentDestination?.hierarchy?.any {
                            if (item == BottomNavItem.Profile) it.route?.startsWith("profile") == true
                            else it.route == item.route
                        } == true

                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = isSelected,
                            onClick = {
                                // 点击逻辑
                                val targetRoute = if (item == BottomNavItem.Profile) {
                                    "profile/$loggedInUsername" // 拼接真实的跳转路由
                                } else {
                                    item.route
                                }

                                navController.navigate(targetRoute) {
                                    // 避免点击返回键时堆积大量页面，回到起始页（这里设为 apps）
                                    popUpTo("apps") {
                                        saveState = true
                                    }
                                    // 避免重复点击同一个标签时多次入栈
                                    launchSingleTop = true
                                    // 恢复之前的状态
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        // Scaffold 会给内容区域计算 padding（避免被底部栏遮挡），必须应用这个 padding
        NavHost(
            navController = navController,
            startDestination = "login",
//            modifier = Modifier.padding(innerPadding) // 关键：应用 padding
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // ... 登录和注册部分代码不变 ...
            composable("login") {
                LoginScreen(
                    onLoginClick = { u, p -> viewModel.login(u, p) },
                    loginStateFlow = viewModel.loginState,
                    onLoginSuccess = { username ->
                        viewModel.resetState()
                        // ★ 登录成功后，记录用户名，并跳转到 "应用列表页" (apps) 而不是直接去 profile
                        loggedInUsername = username
                        navController.navigate("apps") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onRegisterClick = { navController.navigate("register") }
                )
            }

            composable("register") {
                RegisterScreen(
                    onRegisterClick = { u, p, uri -> viewModel.register(context, u, p, uri) },
                    registerStateFlow = viewModel.registerState,
                    onRegisterSuccess = {
                        viewModel.resetRegisterState()
                        navController.popBackStack()
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // ... 应用列表页 ...
            composable("apps") {
                AppsScreen(
                    onAppClick = { appName ->
                        if (appName == "transcribe") {
                            navController.navigate("transcribe")
                        }
                    }
                )
            }

            // ... 个人主页 ...
            composable("profile/{username}") { backStackEntry ->
                val username = backStackEntry.arguments?.getString("username") ?: "User"

                // 如果是从外部直接跳转进来，也可以在这里更新一下全局状态
                LaunchedEffect(username) {
                    if (username != "User") loggedInUsername = username
                }

                // 获取用户信息逻辑不变...
                var currentUser by remember { mutableStateOf<User?>(null) }
                LaunchedEffect(username) {
                    currentUser = viewModel.getUserInfo(username)
                }

                ProfileScreen(
                    user = currentUser,
                    onLogout = {
                        navController.navigate("login") {
                            popUpTo("apps") { inclusive = true } // 清空所有主页栈
                        }
                    }
                )
            }

            // ... 语音笔记页 (不需要底部栏，因为它不在 showBottomBar 的判断列表中) ...
            composable("transcribe") {
                TranscribeScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}