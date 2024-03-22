package fr.isen.volpelliere.androiderestaurant

import CartItem
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import fr.isen.volpelliere.androiderestaurant.CartManager.clearCart
import fr.isen.volpelliere.androiderestaurant.CartManager.readCart
import fr.isen.volpelliere.androiderestaurant.ui.theme.AndroidERestaurantTheme

class CartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidERestaurantTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CartScreen(context = this)
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MutableCollectionMutableState", "RememberReturnType")
@Composable
fun CartScreen(context: Context) {
    var cartItems by remember { mutableStateOf(readCart(context).items) }
    val totalPrice = cartItems.sumOf { it.item.prices[0].price.toDouble() * it.quantity }

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
                    Text(text = "Panier",
                        modifier = Modifier.padding(16.dp),
                        color =  Color.Black)
                }
            )
        },
        ) { innerPadding ->
        Column {
            LazyColumn(modifier = Modifier.weight(1f).padding(innerPadding)) {
                items(cartItems) { cartItem ->
                    CartItemRow(cartItem = cartItem, context) {
                        cartItems = readCart(context).items
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Total : $totalPrice €",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp)
            )

            Row(modifier = Modifier.padding(8.dp)) {
                Button(
                    onClick = { /* TODO: Implémenter la logique de passer la commande */ },
                    modifier = Modifier.weight(1f).padding(8.dp)
                ) {
                    Text("Passer la commande")
                }
                val activityContext = LocalContext.current as Activity
                Button(
                    onClick = {
                        clearCart(context)
                        cartItems.clear()
                        activityContext.finish()
                    },
                    modifier = Modifier.weight(1f).padding(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Vider le panier")
                }
            }
        }
    }
}

@Composable
fun CartItemRow(cartItem: CartItem, context: Context, onUpdate: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
        Text(text = cartItem.item.name_fr, modifier = Modifier.weight(1f))

        IconButton(onClick = {
            if (cartItem.quantity > 1) {
                CartManager.updateCartItemQuantity(context, cartItem.item.id, cartItem.quantity - 1)
                onUpdate()
            }
        }) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Diminuer la quantité")
        }

        Text(text = "Quantité : ${cartItem.quantity}")

        IconButton(onClick = {
            CartManager.updateCartItemQuantity(context, cartItem.item.id, cartItem.quantity + 1)
            onUpdate()
        }) {
            Icon(Icons.Default.ArrowForward, contentDescription = "Augmenter la quantité")
        }

        IconButton(onClick = {
            CartManager.removeFromCart(context, cartItem.item.id)
            onUpdate()
        }) {
            Icon(Icons.Default.Delete, contentDescription = "Supprimer")
        }
    }
}
