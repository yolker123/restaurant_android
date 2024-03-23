package fr.isen.volpelliere.androiderestaurant

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import fr.isen.volpelliere.androiderestaurant.ui.theme.AndroidERestaurantTheme

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        window.statusBarColor = android.graphics.Color.parseColor("#3380EF")
        setContent {
            AndroidERestaurantTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    HomePage(onCategorySelected = { category ->
                        onCategorySelected(this, category)
                    })
                }
            }
        }
    }

    fun onCategorySelected(context: Context, category: String) {
        Toast.makeText(context, "$category sélectionnées", Toast.LENGTH_SHORT).show()
        val intent = Intent(context, CategoryActivity::class.java).apply {
            putExtra("category", category)
        }
        context.startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("HomeActivity", "HomeActivity est en train de se détruire.")
    }
}
@Composable
fun HomePage(onCategorySelected: (String) -> Unit) {
    val myBlue = Color(0xFF0556AC)
    val context = LocalContext.current
    Scaffold(
        floatingActionButton = { CartIconWithBadge(context) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Spartiates Café",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = myBlue,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 24.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Spartiates Café Logo",
                modifier = Modifier
                    .size(300.dp)
                    .padding(16.dp)
            )

            val buttonColors = ButtonDefaults.buttonColors(containerColor = myBlue)
            CategoryButton("Entrées", buttonColors, onCategorySelected)
            CategoryButton("Plats", buttonColors, onCategorySelected)
            CategoryButton("Desserts", buttonColors, onCategorySelected)
        }
    }
}

@Composable
fun CategoryButton(category: String, buttonColors: ButtonColors, onCategorySelected: (String) -> Unit) {
    Button(
        onClick = { onCategorySelected(category) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .size(70.dp),
        colors = buttonColors,
        shape = RoundedCornerShape(30.dp),
    ) {
        Text(
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            text = category,
            style = MaterialTheme.typography.titleLarge // Utilisation du style typographique pour les boutons
        )
    }
}


@Preview(showBackground = true)
@Composable
fun HomePagePreview() {
    AndroidERestaurantTheme {
        HomePage(onCategorySelected = { })
    }
}
