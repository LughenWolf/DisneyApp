package fr.isen.s8.LrMmMr.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DetailledMovie(modifier: Modifier){
    Box(
        Modifier.background(
            brush = Brush.verticalGradient(
                listOf(
                    colorResource(R.color.purple_500),
                    colorResource(R.color.purple_700)
                )
            ))
            .fillMaxSize()) {
        Column(
            modifier = modifier.fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
            ,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)) {
            AsyncImage(
                model = drink.strDrinkThumb,
                "",
                modifier = Modifier
                    .width(200.dp)
                    .height(200.dp)
                    .clip(CircleShape)
                    .border(
                        1.dp,
                        colorResource(R.color.teal_200),
                        CircleShape
                    )
            )


            Text(
                text = drink.strDrink ?: "",
                fontSize = 40.sp,
                lineHeight = 48.sp,
                textAlign = TextAlign.Center,
                color = colorResource(R.color.white)
            )

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                drink.strCategory?.let { CategoryView(text = it, catogory = Category.OTHER) }
                drink.strAlcoholic?.let { CategoryView(text = it, catogory = Category.NON_ALCOHOLIC) }
            }

            Text(
                drink.strGlass ?: "Unknown glass",
                color = colorResource(R.color.grey)
            )

            Card() {
                Column(
                    Modifier.padding(16.dp)
                        .fillMaxWidth()) {
                    Text(stringResource(R.string.ingredient),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold)

                    drink.ingredientList().forEach { (ingredient, measure) ->
                        Text("$measure $ingredient".trim())
                    }
                }
            }

            Card() {
                Column(
                    Modifier.padding(16.dp)
                        .fillMaxWidth()) {
                    Text(stringResource(R.string.preparation),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold)

                    Text(drink.strInstructions ?: "")
                }
            }
        }
    }
}

@Composable
fun DetailCocktailTopButton(drink: Drink?) {
    val context = LocalContext.current
    val favoritesManager = FavoritesManager()
    drink?.let { drink ->
        var isFavorites = remember {
            mutableStateOf<Boolean>(favoritesManager.isFavorite(drink, context))
        }

        IconButton({
            favoritesManager.toggleFavorite(drink, context)
            isFavorites.value = favoritesManager.isFavorite(drink, context)
        }) {
            Icon(
                imageVector = if (isFavorites.value) {
                    Icons.Filled.Favorite
                } else {
                    Icons.Filled.FavoriteBorder
                },
                contentDescription = "Localized description"
            )
        }
    }
}
}