package com.example.lactionapp

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lactionapp.ui.theme.LactionAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: LocationViewModel = viewModel()
            LactionAppTheme {
                Surface(modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background) {
                    MyApp(viewModel)

                }
            }
        }
    }
}
@Composable
fun MyApp(viewModel: LocationViewModel)
{
    val context = LocalContext.current
    val locationUtils = LocationUtils(context)
    LocationDisplay(LocationUtils = locationUtils, viewModel, context = context)
    
}



@Composable
fun LocationDisplay(
    LocationUtils: LocationUtils,
    viewModel: LocationViewModel,
    context: Context
)
{
    val location = viewModel.location.value

    val address = location?.let {
        LocationUtils.reverseGeocodeLocation(location)
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions() ,
        onResult ={permissions ->
            if(permissions[Manifest.permission.ACCESS_COARSE_LOCATION]==true
                && permissions[Manifest.permission.ACCESS_FINE_LOCATION]==true)
            {
                LocationUtils.requestLocationUpdates(viewModel =viewModel)

            }
            else
            {
                val rationalRequired= ActivityCompat.shouldShowRequestPermissionRationale(
                context as MainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    context ,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                if(rationalRequired)
                {
                    Toast.makeText(context, "Location Permission Required", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(context, "Go to Settings to Give Permission", Toast.LENGTH_SHORT).show()
                }

            }
        } )






    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center)
    {
        if(location!=null)
        {
            Text("Address: ${location.latitude}, ${location.longitude} \n $address")
        }
        else {
            Text(text = "Loaction is Not available")
        }
        Button(onClick = {
            if(LocationUtils.hasLocationPermission(context))
            {
                LocationUtils.requestLocationUpdates(viewModel)

            }
            else
            {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )

            }
        }) {

            Text(text = "GetLocation")
        }
    }
}