package Z_MasterOfApps.Z.Android.Main.C_EcranDeDepart.Startup

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z.Android.Main.C_EcranDeDepart.Startup.B.Dialogs.A_OptionsControlsButtons
import Z_MasterOfApps.Z.Android.Main.C_EcranDeDepart.Startup.ViewModel.Startup_Extension
import Z_MasterOfApps.Z.Android.Main.NavigationItems
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
internal fun A_StartupScreen(
    viewModelInitApp: ViewModelInitApp = viewModel(),
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isManagerPhone = viewModelInitApp._paramatersAppsViewModelModel.cLeTelephoneDuGerant ?: false
    val items = remember(isManagerPhone) { NavigationItems.getItems(isManagerPhone) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(items) { screen ->
            val isManagerOnly = screen.route == "main_screen_f4" || screen.route == "Id_App2Fragment1"
            val isDisabled = isManagerOnly && !isManagerPhone

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clickable(enabled = !isDisabled) { onNavigate(screen.route) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isDisabled) Color.Gray else screen.color
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // ID Text at top start
                    Text(
                        text = "ID: ${screen.id}",
                        color = if (isDisabled) Color.DarkGray else Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.TopStart)
                    )

                    // Main content in center
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
                            tint = if (isDisabled) Color.DarkGray else Color.White
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = screen.titleArab,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isDisabled) Color.DarkGray else Color.White
                        )
                    }
                }
            }
        }
    }

    // Afficher les boutons de contrôle si activés
    if (viewModelInitApp._paramatersAppsViewModelModel.fabsVisibility) {
        A_OptionsControlsButtons(
            extensionVM = Startup_Extension(viewModelInitApp),
            viewModelInitApp = viewModelInitApp,
            paddingValues = PaddingValues()
        )
    }
}
