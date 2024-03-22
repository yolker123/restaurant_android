package fr.isen.volpelliere.androiderestaurant

import CartData
import CartItem
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.isen.volpelliere.androiderestaurant.CartManager.clearCart
import fr.isen.volpelliere.androiderestaurant.CartManager.readCart
import fr.isen.volpelliere.androiderestaurant.ui.theme.AndroidERestaurantTheme

class CartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cartData = intent.getSerializableExtra("CART_ITEM") as CartData
        setContent {
            AndroidERestaurantTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CartScreen(context = this, cartData = cartData)
                }
            }
        }
    }
}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun CartScreen(context: Context, cartData: CartData) {
    val cartItems = remember { mutableStateOf(readCart(context)) }

    LazyColumn {
        items(cartData.items) { cartItem ->
            CartItemRow(cartItem, context, onUpdate = { cartItems.value = readCart(context) })
        }
    }

    Button(onClick = { /* Passer la commande */ }) {
        Text("Passer la commande")
    }

    Button(onClick = {
        clearCart(context)
        cartItems.value = readCart(context)
    }) {
        Text("Vider le panier")
    }
}

@Composable
fun CartItemRow(cartItem: CartItem, context: Context, onUpdate: () -> Unit) {
    Row(modifier = Modifier.padding(8.dp)) {
        Text(text = cartItem.item.name_fr, modifier = Modifier.weight(1f))
        Text(text = "Quantité: ${cartItem.quantity}")
        IconButton(onClick = {
            // Logique pour supprimer l'item ou mettre à jour la quantité
            // Par exemple: removeFromCart(context, cartItem.item.id)
            onUpdate()
        }) {
            Icon(Icons.Default.Delete, contentDescription = "Supprimer")
        }
    }
}



