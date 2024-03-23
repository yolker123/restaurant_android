package fr.isen.volpelliere.androiderestaurant

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@SuppressLint("MutableCollectionMutableState", "RememberReturnType")
@Composable
fun CartScreen(context: Context) {
    var cartItems by remember { mutableStateOf(readCart(context).items) }
    val totalPrice = cartItems.sumOf { it.item.prices[0].price.toDouble() * it.quantity }

    Scaffold(
        containerColor = Color.White,
        topBar = {CategoryTopBar("Panier") },
        ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            CartItemsList(cartItems, context) { cartItems = readCart(context).items }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Total : $totalPrice €", style = MaterialTheme.typography.bodyMedium, modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp))
            CartActions {
                val activityContext = context as Activity
                clearCart(context)
                cartItems.clear()
                activityContext.finish()
            }
        }
    }
}

@Composable
fun CartItemRow(cartItem: CartItem, context: Context, onUpdate: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
        Text(text = cartItem.item.nameFr, modifier = Modifier.weight(1f))

        IconButton(onClick = {
            if (cartItem.quantity > 1) {
                CartManager.updateCartItemQuantity(context, cartItem.item.id, cartItem.quantity - 1)
                onUpdate()
            }
        }) {
            Text("-", fontSize = 24.sp, fontWeight = Bold)
        }

        Text(text = "Quantité : ${cartItem.quantity}")

        IconButton(onClick = {
            CartManager.updateCartItemQuantity(context, cartItem.item.id, cartItem.quantity + 1)
            onUpdate()
        }) {
            Text("+", fontSize = 24.sp, fontWeight = Bold)
        }

        IconButton(onClick = {
            CartManager.removeFromCart(context, cartItem.item.id)
            onUpdate()
        }) {
            Icon(Icons.Default.Delete, contentDescription = "Supprimer")
        }
    }
}
@Composable
fun CartItemsList(cartItems: List<CartItem>, context: Context, onUpdate: () -> Unit) {
    LazyColumn {
        items(cartItems) { cartItem ->
            CartItemRow(cartItem, context, onUpdate)
        }
    }
}

@Composable
fun CartActions(onClear: () -> Unit) {
    Row(modifier = Modifier.padding(8.dp)) {
        Button(
            onClick = { /* TODO: Implémenter la logique de passer la commande */ },
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0556AC))
        ) {
            Text("Passer la commande")
        }
        Button(
            onClick = {
                onClear()
            },
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Vider le panier")
        }
    }
}