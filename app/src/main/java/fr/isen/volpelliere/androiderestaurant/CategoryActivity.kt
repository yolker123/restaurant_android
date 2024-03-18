@file:OptIn(ExperimentalMaterial3Api::class)

package fr.isen.volpelliere.androiderestaurant

import android.app.Activity
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity

class CategoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val category = intent.getStringExtra("category") ?: "Catégorie non spécifiée"

        setContent {
            AndroidERestaurantTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    CategoryScreen(category = category, navigateToDetail = { itemTitle ->
                        navigateToDetailScreen(itemTitle)
                    })
                }
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
fun CategoryScreen(category: String, navigateToDetail: (String) -> Unit) {
    var presses by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    val items = when (category) {
        "Entrées" -> context.resources.getStringArray(R.array.entrees_titles).toList()
        "Plats" -> context.resources.getStringArray(R.array.plats_titles).toList()
        "Desserts" -> context.resources.getStringArray(R.array.desserts_titles).toList()
        else -> listOf<String>()
    }
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
                        onClick = { navigateToDetail(item) }, // Gère le clic sur l'élément
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3380EF)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = item,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryScreenPreview() {
    AndroidERestaurantTheme {
        CategoryScreen("Entrées", navigateToDetail = {})
    }
}
