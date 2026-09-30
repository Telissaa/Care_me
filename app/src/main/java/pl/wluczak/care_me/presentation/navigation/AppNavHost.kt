package pl.wluczak.care_me.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import pl.wluczak.care_me.presentation.activity_detail.ActivityDetailScreen
import pl.wluczak.care_me.presentation.dashboard.DashboardScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard,
    ) {
        composable<Screen.Dashboard> {
            DashboardScreen(
                onOpenActivity = { activityId ->
                    navController.navigate(Screen.ActivityDetail(activityId))
                }
            )
        }

        composable<Screen.ActivityDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.ActivityDetail>()
            ActivityDetailScreen(
                activityId = route.activityId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
