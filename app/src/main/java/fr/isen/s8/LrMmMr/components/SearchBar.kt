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
    query: String,
    onQueryChange: (String) -> Unit,
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
                    colors = listOf(Color(0x66A99BCB), Color(0x445B5278), Color(0x33312642))
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(34.dp))
            .padding(horizontal = 22.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        androidx.compose.foundation.text.BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 16.sp),
            modifier = Modifier.fillMaxWidth(),
            cursorBrush = androidx.compose.ui.graphics.SolidColor(Color.White),
            decorationBox = { innerTextField ->
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

                    Box {
                        if (query.isEmpty()) {
                            Text(
                                text = placeholder,
                                color = Color.White.copy(alpha = 0.55f),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        innerTextField()
                    }
                }
            }
        )
    }
}