package pl.wluczak.care_me.presentation.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import pl.wluczak.care_me.R

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
            ActivityDetailScreen(activityId = route.activityId)
        }
    }
}

@Composable
private fun DashboardScreen(
    onOpenActivity: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(id = R.string.dashboard_title),
                style = MaterialTheme.typography.headlineMedium,
            )

            Button(
                onClick = { onOpenActivity(1L) },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(stringResource(id = R.string.open_activity_detail))
            }
        }
    }
}

@Composable
private fun ActivityDetailScreen(
    activityId: Long,
    modifier: Modifier = Modifier
) {
    Scaffold { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(id = R.string.activity_detail_title),
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = stringResource(id = R.string.activity_id, activityId),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
