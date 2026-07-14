package com.jairo.calendariotrabajo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.jairo.calendariotrabajo.ui.home.HomeScreen
import com.jairo.calendariotrabajo.ui.home.HomeViewModel
import com.jairo.calendariotrabajo.ui.theme.MisHorasTheme

class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels {
        val app = application as MisHorasApplication
        HomeViewModel.factory(
            workDayRepository = app.workDayRepository,
            salaryRatesRepository = app.salaryRatesRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MisHorasTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomeScreen(
                        viewModel = homeViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
