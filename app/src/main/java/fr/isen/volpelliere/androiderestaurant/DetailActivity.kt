package fr.isen.volpelliere.androiderestaurant

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import fr.isen.volpelliere.androiderestaurant.CartManager.addToCart
import fr.isen.volpelliere.androiderestaurant.CartManager.navigateToCart
import fr.isen.volpelliere.androiderestaurant.CartManager.updateCartCount
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

@OptIn(ExperimentalPagerApi::class)
@Composable
fun DetailedScreen(item: MenuItem) {
    val context = LocalContext.current
    val pagerState = rememberPagerState()
    var quantity by remember { mutableIntStateOf(1) }
    val price =quantity * item.prices.first().price.toDouble()
    val skyBlue = Color(0xFF0556AC)
    val buttonColors = ButtonDefaults.buttonColors(containerColor = skyBlue)
    updateCartCount(context)

    Scaffold(
        containerColor = Color.White,
        topBar = { DetailTopBar(item) },
        floatingActionButton = { CartIconWithBadge(context) }
    ) { innerPadding ->
        Box(modifier = Modifier) {
            LazyColumn(modifier = Modifier.padding(innerPadding)) {
                item { ImageCarousel(images = item.images, pagerState = pagerState) }
                item { Text(text = item.nameFr, modifier = Modifier.fillMaxWidth().padding(16.dp), textAlign = TextAlign.Center) }
                item { IngredientsList(ingredients = item.ingredients) }
                item {
                    QuantitySelector(quantity = quantity, buttonColors = buttonColors, onIncrease = { quantity++ }, onDecrease = { if (quantity > 1) quantity-- })
                }
                item {
                    TotalPriceDisplay(totalPrice = price)
                    Button(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), colors=buttonColors ,onClick = {
                        addToCart(item, quantity, context)
                    }) {
                        Text(fontSize = 20.sp, color= Color.White,text = "Ajouter au panier")
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
    val ingredientsText = ingredients.joinToString(separator = ", ") { it.nameFr }
    Text(
        fontSize = 15.sp,
        text = "Ingredients :",
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        textAlign = TextAlign.Start
    )
    Text(
        fontSize = 15.sp,
        text = ingredientsText,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        textAlign = TextAlign.Center
    )
}

@Composable
fun CartIconWithBadge(context: Context){
    val cartCount by CartManager.cartCountFlow.collectAsState()
    Box(contentAlignment = Alignment.TopEnd) {
        FloatingActionButton(
            onClick = { navigateToCart(context) },
            containerColor = Color(0xFF0556AC),
            contentColor = Color.White,
            modifier = Modifier.size(85.dp)
        ) {
            Icon(Icons.Default.ShoppingCart, contentDescription = "Panier", modifier = Modifier.size(48.dp))
        }
        if (cartCount > 0) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(Color.Red, CircleShape)
                    .align(Alignment.TopEnd),
                contentAlignment = Alignment.Center
            ) {
                Text(text = cartCount.toString(), fontSize = 24.sp, color = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTopBar(item: MenuItem) {
    TopAppBar(
        navigationIcon = {
            BackButton()
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF0556AC),
            titleContentColor = Color.White,
        ),
        title = {
            Text(text = item.nameFr,
                modifier = Modifier.padding(16.dp),
                color =  Color.White)
        }
    )
}

@Composable
fun QuantitySelector(quantity: Int, buttonColors: ButtonColors, onIncrease: () -> Unit, onDecrease: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(colors = buttonColors, onClick = onDecrease) {
            Text(fontSize = 20.sp, text = "-")
        }
        Text(
            text = "$quantity",
            modifier = Modifier
                .padding(horizontal = 30.dp, vertical = 12.dp)
        )
        Button(colors = buttonColors, onClick = onIncrease) {
            Text(fontSize = 20.sp, text = "+")
        }
    }
}


@Composable
fun TotalPriceDisplay(totalPrice: Double) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(48.dp),
        color = Color.LightGray

        // Vous pouvez choisir la couleur que vous souhaitez
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                fontSize = 20.sp,
                text = "Total : $totalPrice €",
                textAlign = TextAlign.Center
            )
        }
    }
}

