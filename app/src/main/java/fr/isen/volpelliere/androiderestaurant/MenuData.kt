package fr.isen.volpelliere.androiderestaurant

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MenuData(
    @SerializedName("data")
    val data: List<MenuCategory>
) : Serializable

data class MenuCategory(
    @SerializedName("name_fr")
    val nameFr: String,
    @SerializedName("name_en")
    val nameEn: String,
    @SerializedName("items")
    val items: List<MenuItem>
): Serializable

data class MenuItem(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("name_fr")
    val nameFr: String = "",
    @SerializedName("name_en")
    val nameEn: String = "",
    @SerializedName("id_category")
    val idCategory: String = "",
    @SerializedName("categ_name_fr")
    val categoryNameFr: String = "",
    @SerializedName("categ_name_en")
    val categoryNameEn: String = "",
    @SerializedName("images")
    val images: List<String> = emptyList(),
    @SerializedName("ingredients")
    val ingredients: List<Ingredient> =emptyList(),
    @SerializedName("prices")
    val prices: List<Price> =emptyList()
): Serializable

data class Ingredient(
    @SerializedName("id")
    val id: String,
    @SerializedName("id_shop")
    val idShop: String,
    @SerializedName("name_fr")
    val nameFr: String,
    @SerializedName("name_en")
    val nameEn: String,
    @SerializedName("create_date")
    val createDate: String,
    @SerializedName("update_date")
    val updateDate: String,
    @SerializedName("id_pizza")
    val idPizza: String
): Serializable

data class Price(
    @SerializedName("id")
    val id: String,
    @SerializedName("id_pizza")
    val idPizza: String,
    @SerializedName("id_size")
    val idSize: String,
    @SerializedName("price")
    val price: String,
    @SerializedName("create_date")
    val createDate: String,
    @SerializedName("update_date")
    val updateDate: String,
    @SerializedName("size")
    val size: String,
): Serializable

data class CartItem(
    @SerializedName("item")
    val item: MenuItem,
    @SerializedName("quantity")
    val quantity: Int
) : Serializable

data class CartData(
    @SerializedName("items")
    val items: MutableList<CartItem>
) : Serializable