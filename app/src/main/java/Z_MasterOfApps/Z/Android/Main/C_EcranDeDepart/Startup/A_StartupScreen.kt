package Z_MasterOfApps.Z.Android.Main.C_EcranDeDepart.Startup

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z.Android.Main.C_EcranDeDepart.Startup.B.Dialogs.A_OptionsControlsButtons
import Z_MasterOfApps.Z.Android.Main.C_EcranDeDepart.Startup.ViewModel.Startup_Extension
import Z_MasterOfApps.Z.Android.Main.NavigationItems
import Z_MasterOfApps.Z.Android.Main.Screen
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

// A_StartupScreen.kt

@Composable
internal fun A_StartupScreen(
    modifier: Modifier = Modifier,
    viewModelInitApp: ViewModelInitApp = viewModel(),
    onNavigate: (String) -> Unit // Add navigation callback instead of NavController
) {
    // Create extension view model
    val extensionVM = Startup_Extension(viewModelInitApp)

    // Safely get the manager phone status
    val isManagerPhone = viewModelInitApp._paramatersAppsViewModelModel.cLeTelephoneDuGerant ?: false

    // Get navigation items list
    val navigationItems = remember(isManagerPhone) {
        NavigationItems.getItems(isManagerPhone)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(navigationItems) { screen ->
                        NavigationCard(
                            screen = screen,
                            isManagerPhone = isManagerPhone,
                            onClick = { onNavigate(screen.route) }
                        )
                    }
                }

                // Show control buttons if enabled
                if (viewModelInitApp._paramatersAppsViewModelModel.fabsVisibility) {
                    A_OptionsControlsButtons(
                        extensionVM = extensionVM,
                        viewModelInitApp = viewModelInitApp,
                        paddingValues = paddingValues
                    )
                }
            }
        }
    }
}

@Composable
private fun NavigationCard(
    screen: Screen,
    isManagerPhone: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isManagerPhone) screen.color else Color.Gray
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = screen.icon,
                contentDescription = screen.titleArab,
                modifier = Modifier.size(48.dp),
                tint = if (isManagerPhone) Color.White else Color.DarkGray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = screen.titleArab,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isManagerPhone) Color.White else Color.DarkGray
            )
        }
    }
}
