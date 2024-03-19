import com.google.gson.annotations.SerializedName

data class MenuData(
    @SerializedName("data")
    val data: List<MenuCategory>
)

data class MenuCategory(
    @SerializedName("name_fr")
    val name_fr: String,
    @SerializedName("name_en")
    val name_en: String,
    @SerializedName("items")
    val items: List<MenuItem>
)

data class MenuItem(
    @SerializedName("id")
    val id: String,
    @SerializedName("name_fr")
    val name_fr: String,
    @SerializedName("name_en")
    val name_en: String,
    @SerializedName("id_category")
    val id_category: String,
    @SerializedName("categ_name_fr")
    val categ_name_fr: String,
    @SerializedName("categ_name_en")
    val categ_name_en: String,
    @SerializedName("images")
    val images: List<String>,
    @SerializedName("ingredients")
    val ingredients: List<Ingredient>,
    @SerializedName("prices")
    val prices: List<Price>
)

data class Ingredient(
    @SerializedName("id")
    val id: String,
    @SerializedName("id_shop")
    val id_shop: String,
    @SerializedName("name_fr")
    val name_fr: String,
    @SerializedName("name_en")
    val name_en: String,
    @SerializedName("create_date")
    val create_date: String,
    @SerializedName("update_date")
    val update_date: String,
    @SerializedName("id_pizza")
    val id_pizza: String
)

data class Price(
    @SerializedName("id")
    val id: String,
    @SerializedName("id_pizza")
    val id_pizza: String,
    @SerializedName("id_size")
    val id_size: String,
    @SerializedName("price")
    val price: String,
    @SerializedName("create_date")
    val create_date: String,
    @SerializedName("update_date")
    val update_date: String,
    @SerializedName("size")
    val size: String,
)
