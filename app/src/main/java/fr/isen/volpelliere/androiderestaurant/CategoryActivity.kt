@file:OptIn(ExperimentalMaterial3Api::class)

package fr.isen.volpelliere.androiderestaurant

import MenuData
import MenuItem
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
        Log.d("Help1", category)
        lifecycleScope.launch {
            val tab_categories = filterByCategory(category)
            setContent {
                AndroidERestaurantTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color.White
                    ) {
                        CategoryScreen(category = category, items = tab_categories, navigateToDetail = { item ->
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

            val tab_categories = mutableListOf<MenuItem>()
            val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, idShopJsonObject,
                { response ->
                    try {
                        val gson = Gson()
                        val menuData: MenuData = gson.fromJson(response.toString(), MenuData::class.java)
                        menuData.data.filter { it.name_fr == category }.forEach { categoryData ->
                            categoryData.items.forEach { item ->
                                tab_categories.add(item)
                            }
                        }
                        continuation.resume(tab_categories)
                    } catch (e: Exception) {
                        continuation.resumeWithException(e)
                    }
                },
                { error ->
                    continuation.resumeWithException(error)
                })

            requestQueue.add(jsonObjectRequest)

            // Gestion de l'annulation de la coroutine
            continuation.invokeOnCancellation {
                requestQueue.cancelAll { true } // Annuler toutes les requêtes si la coroutine est annulée
            }
        }
    }
    private fun navigateToDetailScreen(item: MenuItem) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("MENU_ITEM", item)
        this.startActivity(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(category: String, items: MutableList<MenuItem>,  navigateToDetail: (MenuItem) -> Unit) {
    var presses by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    var imageIndex by remember { mutableIntStateOf(0) }
    Log.d("hey", items.toString())
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
                colors = topAppBarColors(
                    containerColor = Color(0xFF3380EF),
                    titleContentColor = Color.Black,
                ),
                title = {
                        Text(text = category,
                            modifier = Modifier.padding(16.dp),
                            color =  Color.Black)
                    }
            )
        },
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
            modifier = Modifier.size(100.dp),
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
            modifier = Modifier.size(100.dp),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
fun CategoryItem(item: MenuItem, navigateToDetail: (MenuItem) -> Unit) {
    Button(
        onClick = { navigateToDetail(item) }, // Gère le clic sur l'élément
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3380EF)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ItemImage(images = item.images, item.name_fr)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = item.name_fr,
                color = Color.White,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "${item.prices[0].price} €",
                color = Color.White,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

