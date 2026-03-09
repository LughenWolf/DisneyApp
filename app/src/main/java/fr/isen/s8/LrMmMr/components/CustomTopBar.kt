package fr.isen.s8.LrMmMr.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
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
    onWantToWatchClick: () -> Unit = {},
    onWatchClick: () -> Unit = {},
    onOwnClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = movieTitle,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(start = 8.dp)

                .weight(1f)
        )




        if (isConnected) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onWantToWatchClick) {
                    Icon(Icons.Filled.BookmarkBorder, contentDescription = "Want to watch", tint = Color.White)
                }
                IconButton(onClick = onWatchClick) {
                    Icon(Icons.Filled.Check, contentDescription = "Watch", tint = Color.White)
                }
                IconButton(onClick = onOwnClick) {
                    Icon(Icons.Filled.Inventory, contentDescription = "Own", tint = Color.White)
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Filled.Delete, contentDescription = "Want to get rid of", tint = Color.White)
                }
            }
        }
    }
}