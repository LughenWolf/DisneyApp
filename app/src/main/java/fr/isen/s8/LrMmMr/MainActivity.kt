package fr.isen.s8.LrMmMr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.isen.s8.LrMmMr.models.AppBarState
import fr.isen.s8.LrMmMr.screens.BottomAppBar
import fr.isen.s8.LrMmMr.screens.ProfileScreen
import fr.isen.s8.LrMmMr.ui.theme.TheAmazingDisneyAppTheme

data class TabBarItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val selectedTitle = "Deadpool"
        val myApiKey = "a4a738325f5cd022e712c9b94a94f34a"
        val isUserConnected = true

        // NOTRE FAUX UID POUR TESTER
        val mockUserUid = "test_user_12345"

        setContent {
            val context = LocalContext.current
            val navController = rememberNavController()

            val appBarState = remember { mutableStateOf(AppBarState()) }

            val movieItem = TabBarItem(
                title = "Movies",
                selectedIcon = Icons.Filled.PlayArrow,
                unselectedIcon = Icons.Outlined.PlayArrow
            )
            val categoryItem = TabBarItem(
                title = "Categories",
                selectedIcon = Icons.AutoMirrored.Filled.List,
                unselectedIcon = Icons.AutoMirrored.Outlined.List
            )
            val profileItem = TabBarItem(
                title = "Profile",
                selectedIcon = Icons.Filled.AccountCircle,
                unselectedIcon = Icons.Outlined.AccountCircle
            )

            val tabItems = listOf(movieItem, categoryItem, profileItem)

            TheAmazingDisneyAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { BottomAppBar(tabItems, navController) }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        colorResource(id = R.color.egyptian_blue),
                                        colorResource(id = R.color.glaucous)
                                    )
                                )
                            )
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = movieItem.title
                        ) {
                            composable(movieItem.title) { EmptyScreen("Movies Screen") }
                            composable(categoryItem.title) { EmptyScreen("Categories Screen") }
                            composable(profileItem.title) {
                                ProfileScreen(
                                    onComposing = { appBarState.value = it }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

                @Composable
                fun EmptyScreen(text: String) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = text)
                    }
                }
            }
