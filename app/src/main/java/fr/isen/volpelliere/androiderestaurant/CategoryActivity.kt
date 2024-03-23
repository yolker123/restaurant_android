@file:OptIn(ExperimentalMaterial3Api::class)

package fr.isen.volpelliere.androiderestaurant

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import fr.isen.volpelliere.androiderestaurant.ui.theme.AndroidERestaurantTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CategoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val category = intent.getStringExtra("category") ?: "Catégorie non spécifiée"
        lifecycleScope.launch {
            val tabCategories = filterByCategory(category)
            setContent {
                AndroidERestaurantTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color.White
                    ) {
                        CategoryScreen(category = category, items = tabCategories, navigateToDetail = { item ->
                            navigateToDetailScreen(item)
                        })
                    }
                }
            }
        }
    }

    private suspend fun filterByCategory(category: String): MutableList<MenuItem> = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { continuation ->
            val requestQueue: RequestQueue = Volley.newRequestQueue(this@CategoryActivity)
            val url = "http://test.api.catering.bluecodegames.com/menu"
            val idShopJsonObject = JSONObject().apply {
                put("id_shop", "1")
            }

            val tabCategories = mutableListOf<MenuItem>()
            val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, idShopJsonObject,
                { response ->
                    try {
                        val gson = Gson()
                        val menuData: MenuData = gson.fromJson(response.toString(), MenuData::class.java)
                        menuData.data.filter { it.nameFr == category }.forEach { categoryData ->
                            categoryData.items.forEach { item ->
                                tabCategories.add(item)
                            }
                        }
                        continuation.resume(tabCategories)
                    } catch (e: Exception) {
                        continuation.resumeWithException(e)
                    }
                },
                { error ->
                    continuation.resumeWithException(error)
                })

            requestQueue.add(jsonObjectRequest)

            continuation.invokeOnCancellation {
                requestQueue.cancelAll { true }
            }
        }
    }
    private fun navigateToDetailScreen(item: MenuItem) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("MENU_ITEM", item)
        this.startActivity(intent)
    }
}

@Composable
fun CategoryScreen(category: String, items: MutableList<MenuItem>, navigateToDetail: (MenuItem) -> Unit) {
    val context = LocalContext.current
    Scaffold(
        containerColor = Color.White,
        topBar = {
            CategoryTopBar(category)
        },
        floatingActionButton = { CartIconWithBadge(context) }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            items(items) { item ->
                CategoryItem(item = item, navigateToDetail = navigateToDetail)
            }
        }
    }
}


@Composable
fun ItemImage(images: List<String>, contentDescription: String) {
    var imageIndex by remember { mutableIntStateOf(0) }
    var loadImage by remember { mutableStateOf(true) }

    if (loadImage && images.isNotEmpty()) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(images.getOrNull(imageIndex))
                .crossfade(true)
                .build(),
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxWidth().aspectRatio(1.78f).padding(8.dp),
            contentScale = ContentScale.Crop,

            onError = {
                if (imageIndex < images.lastIndex) {
                    imageIndex++
                } else {
                    loadImage = false 
                }
            }
        )
    } else {
        Image(
            painter = painterResource(id = R.drawable.notavailable),
            contentDescription = "Image par défaut",
            modifier = Modifier.fillMaxWidth().aspectRatio(1.78f).padding(8.dp),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
fun CategoryItem(item: MenuItem, navigateToDetail: (MenuItem) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable { navigateToDetail(item) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFCAAE1E)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            ItemImage(images = item.images, contentDescription = item.nameFr)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0556AC))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.nameFr,
                    color = Color.White,
                    fontSize = 25.sp,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                Spacer(Modifier.weight(1f))

                Text(
                    text = "${item.prices[0].price} €",
                    color = Color.White,
                    fontSize = 20.sp,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryTopBar(category: String) {
    TopAppBar(
        navigationIcon = {
            BackButton()
        },
        title = {
            Text(text = category, modifier = Modifier.padding(16.dp), color = Color.White)
        },
        colors = topAppBarColors(containerColor = Color(0xFF0556AC), titleContentColor = Color.White),
    )
}

@Composable
fun BackButton() {
    val activityContext = LocalContext.current as Activity
    IconButton(onClick = { activityContext.finish() }) {
        Icon(Icons.Filled.ArrowBack, contentDescription = "Retour", tint= Color.White)
    }
}

