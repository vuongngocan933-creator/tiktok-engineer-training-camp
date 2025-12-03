package com.example.myapplication.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// 引入数据类、ViewModel 和所有 Screen
import com.example.myapplication.data.User
import com.example.myapplication.viewmodel.LoginViewModel
import com.example.myapplication.ui.screens.LoginScreen
import com.example.myapplication.ui.screens.RegisterScreen
import com.example.myapplication.ui.screens.ProfileScreen

@Composable
fun AppNavigation(viewModelFactory: ViewModelProvider.Factory) {
    val navController = rememberNavController()
    val viewModel: LoginViewModel = viewModel(factory = viewModelFactory)
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = "login") {
        // 1. 登录
        composable("login") {
            LoginScreen(
                onLoginClick = { u, p -> viewModel.login(u, p) },
                loginStateFlow = viewModel.loginState,
                onLoginSuccess = { username ->
                    viewModel.resetState()
                    navController.navigate("profile/$username") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate("register")
                }
            )
        }

        // 2. 注册
        composable("register") {
            RegisterScreen(
                onRegisterClick = { u, p, uri ->
                    viewModel.register(context, u, p, uri)
                },
                registerStateFlow = viewModel.registerState,
                onRegisterSuccess = {
                    viewModel.resetRegisterState()
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        // 3. 个人主页
        composable("profile/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: "User"

            // 获取用户信息
            var currentUser by remember { mutableStateOf<User?>(null) }
            LaunchedEffect(username) {
                currentUser = viewModel.getUserInfo(username)
            }

            ProfileScreen(
                user = currentUser,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("profile/{username}") { inclusive = true }
                    }
                }
            )
        }
    }
}