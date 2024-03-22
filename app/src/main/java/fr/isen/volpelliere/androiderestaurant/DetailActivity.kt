package fr.isen.volpelliere.androiderestaurant

import CartData
import CartItem
import Ingredient
import MenuItem
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import fr.isen.volpelliere.androiderestaurant.CartManager.readCart
import fr.isen.volpelliere.androiderestaurant.ui.theme.AndroidERestaurantTheme


class DetailActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val item = intent.getSerializableExtra("MENU_ITEM") as MenuItem
        val cartItem: MutableList<CartItem> = mutableListOf()
        setContent {
            AndroidERestaurantTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    DetailedScreen(item = item, navigateToCart = { CartItems ->
                        navigateToCart(CartItems)
                    })
                }
            }
        }
    }

    private fun navigateToCart(items: CartData) {
        val intent = Intent(this, CartActivity::class.java)
        intent.putExtra("CART_ITEM", items)
        this.startActivity(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun DetailedScreen(item: MenuItem, navigateToCart: (CartData) -> Unit) {
    val context = LocalContext.current
    val pagerState = rememberPagerState()
    var quantity by remember { mutableIntStateOf(1) }
    val IntPrice =quantity * item.prices.first().price.toInt()
    val skyBlue = Color(0xFF3380EF)
    val cartCount = remember { mutableIntStateOf(CartManager.getCartCount(context)) }
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
        floatingActionButton = {
            Log.d("hey", readCart(context).toString())
            CartIconWithBadge(cartCount.intValue, readCart(context), navigateToCart)
        }
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
                        color = Color.LightGray

                        // Vous pouvez choisir la couleur que vous souhaitez
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "Total : ${IntPrice} €",
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    Button(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), colors=buttonColors ,onClick = {
                        addToCart(item, quantity, context) {
                            cartCount.value = CartManager.getCartCount(context)
                        }
                    }) {
                        Text(color= Color.White,text = "Ajouter au panier")
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

@Composable
fun CartIconWithBadge(cartCount : Int, cartData: CartData, navigateToCart: (CartData) -> Unit){

    Box(contentAlignment = Alignment.TopEnd) {
        FloatingActionButton(
            onClick = { navigateToCart(cartData) },
            containerColor = Color(0xFF3380EF),
            contentColor = Color.White,
            modifier = Modifier.size(96.dp)
        ) {
            Icon(Icons.Default.ShoppingCart, contentDescription = "Panier", modifier = Modifier.size(48.dp))
        }
        if (cartCount > 0) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.Red, CircleShape)
                    .align(Alignment.TopEnd),
                contentAlignment = Alignment.Center
            ) {
                Text(text = cartCount.toString(), fontSize = 24.sp, color = Color.White)
            }
        }
    }
}

