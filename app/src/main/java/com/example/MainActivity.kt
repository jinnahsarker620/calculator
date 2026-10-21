package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.CurrencyConverterScreen
import com.example.ui.screens.ScientificCalculatorScreen
import com.example.ui.screens.UnitConverterScreen
import com.example.ui.theme.CalcBackground
import com.example.ui.theme.KeyDarkGray
import com.example.ui.theme.KeyTextAccent
import com.example.ui.theme.KeyTextSecondary
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.CalculatorViewModel
import com.example.viewmodel.CurrencyViewModel
import com.example.viewmodel.UnitConverterViewModel

data class CalcTab(
    val title: String,
    val icon: ImageVector,
    val testTag: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen()
            }
        }
    }
}

@Composable
fun MainAppScreen() {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    // If on a secondary tab, back button returns to the Scientific calculator
    BackHandler(enabled = selectedTabIndex != 0) {
        selectedTabIndex = 0
    }

    val tabs = listOf(
        CalcTab("Scientific", Icons.Default.Calculate, "tab_scientific"),
        CalcTab("Currency", Icons.Default.AttachMoney, "tab_currency"),
        CalcTab("Unit Converter", Icons.Default.Straighten, "tab_unit")
    )

    val calcViewModel: CalculatorViewModel = viewModel()
    val currencyViewModel: CurrencyViewModel = viewModel()
    val unitViewModel: UnitConverterViewModel = viewModel()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(CalcBackground)
            .windowInsetsPadding(WindowInsets.safeDrawing),
        containerColor = CalcBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            // Bottom navigation bar with 3 equal icons at the very bottom of screen
            NavigationBar(
                containerColor = CalcBackground,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            ) {
                tabs.forEachIndexed { index, tab ->
                    val selected = selectedTabIndex == index
                    NavigationBarItem(
                        selected = selected,
                        onClick = { selectedTabIndex = index },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 11.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = KeyTextAccent,
                            selectedTextColor = KeyTextAccent,
                            unselectedIconColor = KeyTextSecondary,
                            unselectedTextColor = KeyTextSecondary,
                            indicatorColor = Color(0xFF2C323D)
                        ),
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CalcBackground)
        ) {
            AnimatedContent(
                targetState = selectedTabIndex,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally { width -> width } + fadeIn())
                            .togetherWith(slideOutHorizontally { width -> -width } + fadeOut())
                    } else {
                        (slideInHorizontally { width -> -width } + fadeIn())
                            .togetherWith(slideOutHorizontally { width -> width } + fadeOut())
                    }
                },
                label = "tab_content_transition"
            ) { targetIndex ->
                when (targetIndex) {
                    0 -> ScientificCalculatorScreen(
                        viewModel = calcViewModel,
                        modifier = Modifier.fillMaxSize()
                    )
                    1 -> CurrencyConverterScreen(
                        viewModel = currencyViewModel,
                        modifier = Modifier.fillMaxSize()
                    )
                    2 -> UnitConverterScreen(
                        viewModel = unitViewModel,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
