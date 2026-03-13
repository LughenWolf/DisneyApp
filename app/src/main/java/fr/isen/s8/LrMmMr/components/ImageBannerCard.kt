package fr.isen.s8.LrMmMr.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import fr.isen.s8.LrMmMr.R

@Composable
fun ImageBannerCard(
    title: String,
    imageUrl: String? = null,
    imageRes: Int? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { onClick() }
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color.White.copy(alpha = 0.4f),
                spotColor = Color.White.copy(alpha = 0.4f)
            ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.White)) {

            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(),
                    error = painterResource(id = R.drawable.mcu), // Image par défaut en cas d'erreur
                    placeholder = painterResource(id = R.drawable.mcu)
                )
            } else if (imageRes != null) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                )
            }

            // Dégradé oblique pour lisibilité du texte
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                colorResource(id = R.color.baby_blue_ice).copy(alpha = 0.95f),
                                colorResource(id = R.color.baby_blue_ice).copy(alpha = 0.6f),
                                colorResource(id = R.color.baby_blue_ice).copy(alpha = 0.0f)
                            ),
                            start = Offset(450f, 0f),
                            end = Offset(950f, 400f)
                        )
                    )
            )

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                ),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 28.dp),
            )
        }
    }
}

@Preview
@Composable
fun ImageBannerCardPreview() {
    ImageBannerCard(
        title = "MARVEL",
        imageRes = R.drawable.mcu
    )
}
