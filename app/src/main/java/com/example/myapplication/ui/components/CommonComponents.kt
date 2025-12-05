package com.example.myapplication.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.PrimaryBlue

// --- 社交登录按钮 ---
@Composable
fun SocialLoginButton(
    text: String,
    icon: Painter,
    color: Color
) {
    OutlinedButton(
        onClick = { },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        modifier = Modifier.width(150.dp).height(50.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
    ) {
        Icon(painter = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 14.sp)
    }
}

// --- 底部导航栏 ---
//@Composable
//fun BottomNavigationBar() {
//    NavigationBar(
//        containerColor = Color.White,
//        tonalElevation = 8.dp
//    ) {
//        // 1. 首页
//        NavigationBarItem(
//            icon = { Icon(Icons.Outlined.Home, contentDescription = null) },
//            label = { Text("首页") },
//            selected = false,
//            onClick = { /* TODO: 跳转首页 */ },
//            colors = NavigationBarItemDefaults.colors(
//                unselectedIconColor = Color.Gray,
//                unselectedTextColor = Color.Gray
//            )
//        )
//
//        // 2. 信息
//        NavigationBarItem(
//            icon = { Icon(Icons.Outlined.List, contentDescription = null) },
//            label = { Text("信息") },
//            selected = false,
//            onClick = { /* TODO: 跳转信息 */ },
//            colors = NavigationBarItemDefaults.colors(
//                unselectedIconColor = Color.Gray,
//                unselectedTextColor = Color.Gray
//            )
//        )
//
//        // 3. 我的 (选中状态)
//        NavigationBarItem(
//            icon = { Icon(Icons.Filled.Person, contentDescription = null) },
//            label = { Text("我的") },
//            selected = true,
//            onClick = { },
//            colors = NavigationBarItemDefaults.colors(
//                selectedIconColor = PrimaryBlue,
//                selectedTextColor = PrimaryBlue,
//                indicatorColor = Color.Transparent
//            )
//        )
//    }
//}