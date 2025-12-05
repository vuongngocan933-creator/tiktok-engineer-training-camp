package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.myapplication.data.User
//import com.example.myapplication.ui.components.BottomNavigationBar
import com.example.myapplication.ui.theme.PrimaryBlue

@Composable
fun ProfileScreen(user: User?,
                  onLogout: () -> Unit) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
//                .padding(innerPadding)
            .background(Color(0xFFF5F5F5))
            .verticalScroll(scrollState),
    ) {
        // 1. 顶部用户信息卡片
        ProfileHeader(
            username = user?.username ?: "未登录",
            avatarPath = user?.avatarPath
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 2. 菜单列表
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            ProfileMenuItem(icon = Icons.Default.Person, title = "个人信息") { }
            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)

            ProfileMenuItem(icon = Icons.Default.BookmarkBorder, title = "我的收藏") { }
            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)

            ProfileMenuItem(icon = Icons.Default.History, title = "浏览历史") { }
            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)

            ProfileMenuItem(icon = Icons.Default.Settings, title = "设置") { }
            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)

            ProfileMenuItem(icon = Icons.Default.Info, title = "关于我们") { }
            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)

            ProfileMenuItem(icon = Icons.Default.Notifications, title = "意见反馈") { }

        }
        Spacer(modifier = Modifier.height(96.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("退出登录", color = PrimaryBlue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onLogout() }
            )
        }
    }
}

// --- 顶部蓝色头部组件 ---
@Composable
fun ProfileHeader(username: String, avatarPath: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(PrimaryBlue)
            .padding(24.dp)
            .systemBarsPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 头像
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            if (!avatarPath.isNullOrEmpty()) {
                AsyncImage(
                    model = avatarPath,
                    contentDescription = "Avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.LightGray,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(20.dp))

        // 右侧文字
        Column {
            Text(
                text = username,
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "欢迎来到信息App",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White.copy(alpha = 0.8f)
                )
            )
        }
    }
}

// --- 单个菜单项组件 ---
@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF333333),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF333333)),
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.ArrowForwardIos,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(16.dp)
        )
    }
}