package fr.isen.volpelliere.androiderestaurant

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object CartManager {
    private val _cartCountFlow = MutableStateFlow(getInitialCartCount())
    val cartCountFlow = _cartCountFlow.asStateFlow()
    private fun getInitialCartCount(): Int {
          return 0
    }

    fun navigateToCart(context: Context) {
        val intent = Intent(context, CartActivity::class.java)
        context.startActivity(intent)
    }

    fun addToCart(item: MenuItem, quantity: Int, context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Ajout au panier")
            .setMessage("Voulez-vous vraiment ajouter cet article au panier ?")
            .setPositiveButton("OK") { dialog, which ->
                // Lire le panier actuel à partir du fichier
                val currentCart = readCart(context).items
                val index = currentCart.indexOfFirst { it.item.id == item.id }

                if (index != -1) {
                    // L'article existe déjà dans le panier, mettre à jour la quantité
                    val existingItem = currentCart[index]
                    currentCart[index] = existingItem.copy(quantity = existingItem.quantity + quantity)
                } else {
                    // Ajouter le nouvel article au panier
                    currentCart.add(CartItem(item, quantity))
                }

                // Écrire la liste mise à jour dans le fichier
                writeCart(context, currentCart)

                // Mise à jour du compteur du panier dans les préférences partagées
                val totalQuantity = currentCart.sumOf { it.quantity }
                val sharedPref = context.getSharedPreferences("app", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putInt("cart_count", totalQuantity)
                    apply()
                }
                updateCartCount(context)
            }
            .setNegativeButton("Annuler", null)
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

        updateCartCount(context)
    }

    fun removeFromCart(context: Context, itemId: String) {
        val cartData = readCart(context)
        val updatedCart = cartData.items.filterNot { it.item.id == itemId }.toMutableList()
        writeCart(context, updatedCart)

        // Mise à jour du compteur du panier dans les préférences partagées
        val sharedPref = context.getSharedPreferences("app", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("cart_count", updatedCart.sumOf { it.quantity })
            apply()
        }

        updateCartCount(context)
    }


    fun clearCart(context: Context) {
        val json = Gson().toJson(listOf<CartItem>())
        context.openFileOutput("cart.json", Context.MODE_PRIVATE).use { it.write(json.toByteArray()) }

        // Mise à jour des préférences partagées pour refléter un panier vide
        val sharedPref = context.getSharedPreferences("app", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("cart_count", 0)
            apply()
        }
        updateCartCount(context)
    }


    fun updateCartCount(context: Context) {
        val sharedPref = context.getSharedPreferences("app", Context.MODE_PRIVATE)
        val cartCount = sharedPref.getInt("cart_count", 0)
        _cartCountFlow.value = cartCount
    }

    fun updateCartItemQuantity(context: Context, itemId: String, newQuantity: Int) {
        val cartData = readCart(context)
        val cartItems = cartData.items.toMutableList()
        val itemIndex = cartItems.indexOfFirst { it.item.id == itemId }
        if (itemIndex != -1) {
            val updatedItem = cartItems[itemIndex].copy(quantity = newQuantity)
            cartItems[itemIndex] = updatedItem
            writeCart(context, cartItems)
            updateCartCount(context)
        }
    }
}
