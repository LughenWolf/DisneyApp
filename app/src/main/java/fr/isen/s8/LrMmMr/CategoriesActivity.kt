package fr.isen.s8.LrMmMr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.colorResource
import fr.isen.s8.LrMmMr.screens.CategoriesScreen
import fr.isen.s8.LrMmMr.ui.theme.TheAmazingDisneyAppTheme

class CategoriesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val franchiseName = intent.getStringExtra("FRANCHISE_NAME") ?: ""

        setContent {
            TheAmazingDisneyAppTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    colorResource(id = R.color.egyptian_blue),
                                    colorResource(id = R.color.glaucous)
                                )
                            )
                        )
                ) {
                    CategoriesScreen(
                        franchiseName = franchiseName,
                        onBackClick = { finish() }
                    )
                }
            }
        }
    }
}
