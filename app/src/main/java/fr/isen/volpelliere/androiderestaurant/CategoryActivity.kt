@file:OptIn(ExperimentalMaterial3Api::class)

package fr.isen.volpelliere.androiderestaurant

import MenuData
import MenuItem
import android.app.Activity
import android.content.ClipData.Item
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.isen.volpelliere.androiderestaurant.ui.theme.AndroidERestaurantTheme
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.layout.ContentScale
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.lifecycleScope
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.withContext
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import coil.compose.AsyncImage
import coil.request.ImageRequest
class CategoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val category = intent.getStringExtra("category") ?: "Catégorie non spécifiée"
        lifecycleScope.launch {
            val tab_categories = filterByCategory(category)
            setContent {
                AndroidERestaurantTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color.White
                    ) {
                        CategoryScreen(category = category, items = tab_categories, navigateToDetail = { itemTitle ->
                            navigateToDetailScreen(itemTitle)
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
    private fun navigateToDetailScreen(itemTitle: String) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("ITEM_TITLE", itemTitle)
        this.startActivity(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(category: String, items: MutableList<MenuItem>,  navigateToDetail: (String) -> Unit) {
    var presses by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    var imageIndex by remember { mutableIntStateOf(0) }
    var lastSuccessfulIndex by remember { mutableStateOf(-1) }
    Log.d("step5", items.toString())
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
        floatingActionButton = {
            FloatingActionButton(contentColor = Color.White, containerColor = Color(0xFF3380EF), onClick = { presses++ }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) { // Appliquer la couleur de fond ici
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                items(items) { item ->
                    Button(
                        onClick = { navigateToDetail(item.name_fr) }, // Gère le clic sur l'élément
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3380EF)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(item.images.getOrNull(imageIndex)) // Utilise getOrNull pour éviter IndexOutOfBoundsException
                                    .crossfade(true)
                                    .build(),
                                contentDescription = item.name_fr,
                                modifier = Modifier.size(100.dp),
                                contentScale = ContentScale.Crop ,
                                onSuccess = {
                                    // Mettre à jour lastSuccessfulIndex quand une image est chargée avec succès
                                    lastSuccessfulIndex = imageIndex
                                },
                                onError = {
                                    if (lastSuccessfulIndex < imageIndex && imageIndex < item.images.lastIndex) {
                                        imageIndex++
                                    } else {
                                        // Gérer le cas où aucune autre image n'est disponible ou l'erreur ne concerne pas le chargement de l'image actuelle
                                        Log.d("LoadImageWithFallback", "Erreur de chargement pour $item à l'index $imageIndex.")
                                    }
                                }
                            )
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
                }
            }
        }
    }
}
