package com.jairo.calendariotrabajo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jairo.calendariotrabajo.MisHorasApplication
import com.jairo.calendariotrabajo.ui.calendar.CalendarScreen
import com.jairo.calendariotrabajo.ui.calendar.CalendarViewModel
import com.jairo.calendariotrabajo.ui.dayDetail.DayDetailScreen
import com.jairo.calendariotrabajo.ui.dayDetail.DayDetailViewModel
import com.jairo.calendariotrabajo.ui.history.HistoryScreen
import com.jairo.calendariotrabajo.ui.history.HistoryViewModel
import com.jairo.calendariotrabajo.ui.home.HomeScreen
import com.jairo.calendariotrabajo.ui.home.HomeViewModel
import java.time.LocalDate

//El NavHost decide que pantalla toca según la ruta
@Composable
fun AppNavGraph(
    app: MisHorasApplication,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {
        composable(Routes.HOME) {
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.factory(
                    workDayRepository = app.workDayRepository,
                    salaryRatesRepository = app.salaryRatesRepository
                )
            )
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToDayDetail = { date ->
                    navController.navigate(Routes.dayDetail(date))
                },
                onNavigateToCalendar = {
                    navController.navigate(Routes.CALENDAR)
                },
                onNavigateToHistory = {
                    navController.navigate(Routes.HISTORY)
                }
            )
        }

        composable(Routes.HISTORY) {
            val historyViewModel: HistoryViewModel = viewModel(
                factory = HistoryViewModel.factory(
                    workDayRepository = app.workDayRepository,
                    salaryRatesRepository = app.salaryRatesRepository
                )
            )
            HistoryScreen(
                viewModel = historyViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.CALENDAR) {
            val calendarViewModel: CalendarViewModel = viewModel(
                factory = CalendarViewModel.factory(
                    workDayRepository = app.workDayRepository,
                    holidayRepository = app.holidayRepository,
                    salaryRatesRepository = app.salaryRatesRepository
                )
            )
            CalendarScreen(
                viewModel = calendarViewModel,
                onBack = { navController.popBackStack() },
                onDayClick = { date ->
                    navController.navigate(Routes.dayDetail(date))
                }
            )
        }

        composable(
            route = Routes.DAY_DETAIL_PATTERN,
            arguments = listOf(
                navArgument(Routes.DAY_DETAIL_ARG) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val dateStr = backStackEntry.arguments?.getString(Routes.DAY_DETAIL_ARG).orEmpty()
            val date = LocalDate.parse(dateStr)
            val dayDetailViewModel: DayDetailViewModel = viewModel(
                factory = DayDetailViewModel.factory(
                    date = date,
                    workDayRepository = app.workDayRepository,
                    salaryRatesRepository = app.salaryRatesRepository,
                    holidayRepository = app.holidayRepository
                )
            )
            DayDetailScreen(
                viewModel = dayDetailViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
