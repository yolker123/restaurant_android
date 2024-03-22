package fr.isen.volpelliere.androiderestaurant

import CartData
import CartItem
import MenuItem
import android.app.AlertDialog
import android.content.Context
import androidx.compose.runtime.MutableState
import com.google.gson.Gson

object CartManager {
    private var cartCountState: MutableState<Int>? = null

    fun initCartCountState(state: MutableState<Int>) {
        cartCountState = state
    }

    fun getCartCount(context: Context): Int {
        val sharedPref = context.getSharedPreferences("app", Context.MODE_PRIVATE)
        return sharedPref.getInt("cart_count", 0)
    }


    fun addToCart(item: MenuItem, quantity: Int, context: Context, updateCartCount: () -> Unit) {
        AlertDialog.Builder(context)
            .setTitle("Ajout au panier")
            .setMessage("Voulez-vous vraiment ajouter cet article au panier ?")
            .setPositiveButton("OK") { dialog, which ->
                val cartItem = CartItem(item, quantity)
                val json = Gson().toJson(cartItem)

                context.openFileOutput("cart.json", Context.MODE_PRIVATE).use {
                    it.write(json.toByteArray())
                }

                val sharedPref = context.getSharedPreferences("app", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.putInt("cart_count", sharedPref.getInt("cart_count", 0) + quantity)
                editor.apply()
                updateCartCount()

            }
            .setNegativeButton("Annuler") { dialog, which ->
                // Réponse au clic sur le bouton Annuler
            }
            .show()
    }

    fun readCart(context: Context): CartData {
        return try {
            val fileName = "cart.json"
            val json = context.openFileInput(fileName).bufferedReader().use { it.readText() }
            val items = Gson().fromJson(json, Array<CartItem>::class.java).toMutableList()
            CartData(items)
        } catch (e: Exception) {
            e.printStackTrace()
            CartData(mutableListOf())
        }
    }

    fun writeCart(context: Context, cartItems: List<CartItem>) {
        val json = Gson().toJson(cartItems)
        context.openFileOutput("cart.json", Context.MODE_PRIVATE).use { it.write(json.toByteArray()) }
    }

    fun removeFromCart(context: Context, itemId: String) {
        val CartData = readCart(context)
        val updatedCart = CartData.items.filterNot { it.item.id == itemId }.toMutableList()
        writeCart(context, updatedCart)

        // Mise à jour du compteur du panier dans les préférences partagées
        val sharedPref = context.getSharedPreferences("app", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("cart_count", updatedCart.sumOf { it.quantity })
            apply()
        }
    }


    fun clearCart(context: Context) {
        writeCart(context, listOf())

        val sharedPref = context.getSharedPreferences("app", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("cart_count", 0)
            apply()
        }
    }



}
