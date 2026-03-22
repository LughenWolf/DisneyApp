package fr.isen.s8.LrMmMr.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomTopBar(
    movieTitle: String,
    isConnected: Boolean,

    isWantToWatch: Boolean,
    isWatched: Boolean,
    isOwn: Boolean,
    isWantToGetRidOf: Boolean,

    onBackClick: () -> Unit = {},

    onWantToWatchClick: () -> Unit = {},
    onWatchClick: () -> Unit = {},
    onOwnClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .heightIn(min = 56.dp)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Retourner à la liste",
                tint = Color.White
            )
        }

        Text(
            text = movieTitle,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp).weight(1f)
        )

        if (isConnected) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                // Bouton Want to watch
                IconButton(onClick = onWantToWatchClick) {
                    Icon(
                        imageVector = if (isWantToWatch) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                        contentDescription = "Want to watch",
                        tint = if (isWantToWatch) Color.White else Color.White.copy(alpha = 0.5f)
                    )
                }

                // Bouton Watched
                IconButton(onClick = {
                    onWatchClick()

                    if (!isWatched && isWantToWatch) {
                        onWantToWatchClick()
                    }
                }) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Watch",
                        tint = if (isWatched) Color.White else Color.White.copy(alpha = 0.5f)
                    )
                }

                IconButton(onClick = onOwnClick) {
                    Icon(
                        imageVector = Icons.Filled.Inventory,
                        contentDescription = "Own",
                        tint = if (isOwn) Color.White else Color.White.copy(alpha = 0.5f)
                    )
                }

                if (isOwn) {
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Want to get rid of",
                            tint = if (isWantToGetRidOf) Color.White else Color.White.copy(alpha = 0.5f)
                        )
                    }
                }

            }
        }
    }
}