package fr.isen.volpelliere.androiderestaurant

import MenuData
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import coil.Coil
import coil.request.CachePolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import fr.isen.volpelliere.androiderestaurant.ui.theme.AndroidERestaurantTheme
import org.json.JSONObject

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Définit la couleur de la barre de notification pour cette activité
        window.statusBarColor = android.graphics.Color.parseColor("#3380EF")
        setContent {
            AndroidERestaurantTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    HomePage()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("HomeActivity", "HomeActivity est en train de se détruire.")
    }
}

@Composable
fun HomePage() {
    val skyBlue = Color(0xFF3380EF)
    val context = LocalContext.current
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Spartiates Café",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = skyBlue,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Remplacer R.drawable.ic_restaurant_logo par votre ressource de logo
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Spartiates Café Logo",
            modifier = Modifier
                .size(150.dp)
                .padding(16.dp)
        )

        val buttonColors = ButtonDefaults.buttonColors(containerColor = skyBlue)
        // Boutons pour les catégories
        Button(
            onClick = { Toast.makeText(context, "Entrées sélectionnées", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, CategoryActivity::class.java)
                intent.putExtra("category", "Entrées")
                context.startActivity(intent)},
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = buttonColors
        ) {
            Text("Entrées")
        }

        Button(
            onClick = { Toast.makeText(context, "Plats sélectionnés", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, CategoryActivity::class.java)
                intent.putExtra("category", "Plats")
                context.startActivity(intent)},
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = buttonColors
        ) {
            Text("Plats")
        }

        Button(
            onClick = {
                Toast.makeText(context, "Desserts sélectionnés", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, CategoryActivity::class.java)
                intent.putExtra("category", "Desserts")
                context.startActivity(intent)},
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = buttonColors
        ) {
            Text("Desserts")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePagePreview() {
    AndroidERestaurantTheme {
        HomePage()
    }
}
