package fr.isen.s8.LrMmMr.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SearchGlassField(
    modifier: Modifier = Modifier,
    placeholder: String = "Search"
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(34.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0x66A99BCB),
                        Color(0x445B5278),
                        Color(0x33312642)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.18f),
                shape = RoundedCornerShape(34.dp)
            )
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(34.dp),
                ambientColor = Color.White.copy(alpha = 0.08f),
                spotColor = Color.White.copy(alpha = 0.08f)
            )
            .padding(horizontal = 22.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search icon",
                tint = Color.White.copy(alpha = 0.55f),
                modifier = Modifier.size(28.dp)
            )

            Text(
                text = placeholder,
                color = Color.White.copy(alpha = 0.55f),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 0.2.sp
                )
            )
        }
    }
}