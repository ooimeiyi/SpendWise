package com.example.spendwise.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendwise.ui.home.HomeScreen
import com.example.spendwise.ui.budget.BudgetScreen
import com.example.spendwise.ui.profile.ProfileScreen
import com.example.spendwise.ui.theme.SpendWisePrimary
import com.example.spendwise.ui.transaction.TransactionScreen

private enum class MainTab {
    Home,
    Transaction,
    Budget,
    Profile,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onLogout: () -> Unit,
) {
    val navRowHeight = 64.dp
    val fabSize = 56.dp

    var selectedTab by remember { mutableStateOf(MainTab.Home) }

    val headerTitle =
        when (selectedTab) {
            MainTab.Home -> "Home"
            MainTab.Transaction -> "Transaction"
            MainTab.Budget -> "Budget"
            MainTab.Profile -> "Profile"
        }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.White,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = headerTitle,
                            style = MaterialTheme.typography.titleLarge,
                        )
                    },
                    colors =
                        TopAppBarDefaults.topAppBarColors(
                            containerColor = SpendWisePrimary,
                            titleContentColor = Color.White,
                            scrolledContainerColor = SpendWisePrimary,
                        ),
                )
            },
            bottomBar = {
                BottomNavWithCenterCamera(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                )
            },
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color.White),
            ) {
                when (selectedTab) {
                    MainTab.Home -> HomeScreen(onLogout = onLogout)
                    MainTab.Transaction -> TransactionScreen()
                    MainTab.Budget -> BudgetScreen()
                    MainTab.Profile -> ProfileScreen()
                }
            }
        }

        FloatingActionButton(
            onClick = { /* TODO: camera scan action */ },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = -navRowHeight / 2)
                .size(fabSize),
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 6.dp,
                pressedElevation = 8.dp,
            ),
        ) {
            Icon(
                imageVector = Icons.Filled.CameraAlt,
                contentDescription = "Camera",
            )
        }
    }
}

@Composable
private fun BottomNavWithCenterCamera(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
) {
    val navRowHeight = 64.dp
    val fabSize = 56.dp

    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        tonalElevation = 0.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(navRowHeight),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(navRowHeight)
                    .align(Alignment.BottomCenter),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BottomNavEntry(
                    icon = Icons.Filled.Home,
                    label = "Home",
                    selected = selectedTab == MainTab.Home,
                    onClick = { onTabSelected(MainTab.Home) },
                    modifier = Modifier.weight(1f),
                )
                BottomNavEntry(
                    icon = Icons.Filled.ListAlt,
                    label = "Transaction",
                    selected = selectedTab == MainTab.Transaction,
                    onClick = { onTabSelected(MainTab.Transaction) },
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.width(fabSize))
                BottomNavEntry(
                    icon = Icons.Filled.AccountBalanceWallet,
                    label = "Budget",
                    selected = selectedTab == MainTab.Budget,
                    onClick = { onTabSelected(MainTab.Budget) },
                    modifier = Modifier.weight(1f),
                )
                BottomNavEntry(
                    icon = Icons.Filled.Person,
                    label = "Profile",
                    selected = selectedTab == MainTab.Profile,
                    onClick = { onTabSelected(MainTab.Profile) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun BottomNavEntry(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color =
        if (selected) SpendWisePrimary
        else MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = modifier
            .offset(y = (-4).dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            maxLines = 1,
        )
    }
}
