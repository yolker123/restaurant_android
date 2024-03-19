package fr.isen.volpelliere.androiderestaurant

import Ingredient
import MenuItem
import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import fr.isen.volpelliere.androiderestaurant.ui.theme.AndroidERestaurantTheme

class DetailActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val item = intent.getSerializableExtra("MENU_ITEM") as MenuItem

        setContent {
            AndroidERestaurantTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    DetailedScreen(item = item)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun DetailedScreen(item: MenuItem) {
    val context = LocalContext.current
    val pagerState = rememberPagerState()
    var quantity by remember { mutableIntStateOf(1) }
    val IntPrice =quantity * item.prices.first().price.toInt()
    val skyBlue = Color(0xFF3380EF)
    val buttonColors = ButtonDefaults.buttonColors(containerColor = skyBlue)
    val buttonColors2 = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    val activityContext = LocalContext.current as Activity
                    IconButton(onClick = {
                        activityContext.finish()
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF3380EF),
                    titleContentColor = Color.Black,
                ),
                title = {
                    Text(text = item.name_fr,
                        modifier = Modifier.padding(16.dp),
                        color =  Color.Black)
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color(0xFF3380EF),
                contentColor = Color.Black,
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "Bottom app bar",
                )
            }
        },
    ) { innerPadding ->
        Box(modifier = Modifier) {
            LazyColumn(modifier = Modifier.padding(innerPadding)) {
                item {
                    ImageCarousel(images = item.images, pagerState = pagerState)
                }
                item {
                    Text(
                        text = item.name_fr,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
                item {
                    IngredientsList(ingredients = item.ingredients)
                }
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(colors = buttonColors, onClick = { if (quantity > 1) quantity-- }) {
                            Text(text = "-")
                        }
                        Text(
                            text = "$quantity",
                            modifier = Modifier
                                .padding(horizontal = 30.dp, vertical = 12.dp)
                        )
                        Button(colors = buttonColors, onClick = { quantity++ }) {
                            Text(text = "+")
                        }
                    }
                }
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(48.dp),
                        color = Color.LightGray // Vous pouvez choisir la couleur que vous souhaitez
                    ) {
                        Button(colors = buttonColors2, onClick = { /* TODO */ }) {
                            Text(
                                text = "Total : ${IntPrice} €",
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }

}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageCarousel(images: List<String>, pagerState: PagerState) {
    HorizontalPager(
        count = images.size,
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    ) { page ->
        var loadImage by remember { mutableStateOf(true) }
        var imageIndex by remember { mutableIntStateOf(page) } // Initialiser avec 'page' pour garder la logique par page

        if (loadImage) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(images.getOrNull(imageIndex))
                    .crossfade(true)
                    .build(),
                contentDescription = "Image ${imageIndex + 1}",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop,
                onError = {
                    loadImage = false // Aucune autre image à essayer, affichez une image par défaut
                }
            )
        }
        if (!loadImage || images.isEmpty()) {
            // Affiche une image par défaut si aucune image ne peut être chargée ou la liste est vide
            Image(
                painter = painterResource(id = R.drawable.notavailable),
                contentDescription = "Image par défaut",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop,
            )
        }
    }
}

@Composable
fun IngredientsList(ingredients: List<Ingredient>) {
    val ingredientsText = ingredients.joinToString(separator = ", ") { it.name_fr }

    Text(
        text = ingredientsText,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        textAlign = TextAlign.Center
    )
}