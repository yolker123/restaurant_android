package fr.isen.volpelliere.androiderestaurant

import MenuItem
import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
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
        Box(modifier = Modifier.padding(innerPadding)) { // Appliquer la couleur de fond ici
            Column(modifier = Modifier.padding(innerPadding)) {

                ImageCarousel(images = item.images, pagerState = pagerState)

                Text(
                    text = item.name_fr,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(colors=buttonColors, onClick = { if (quantity > 1) quantity-- }) {
                        Text("-")
                    }
                    Text(
                        text = "$quantity",
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                    )
                    Button(colors=buttonColors, onClick = { quantity++ }) {
                        Text("+")
                    }
                }
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(48.dp),
                    color = Color.LightGray // Vous pouvez choisir la couleur que vous souhaitez
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "Prix : ${IntPrice} €", // Supposant que vous utilisez le premier prix listé
                            textAlign = TextAlign.Center
                        )
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
        AsyncImage(
            model = images[page],
            contentDescription = "Image ${page + 1}",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
    }
}