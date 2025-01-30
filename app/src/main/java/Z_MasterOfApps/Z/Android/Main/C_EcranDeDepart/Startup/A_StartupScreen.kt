package Z_MasterOfApps.Z.Android.Main.C_EcranDeDepart.Startup

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z.Android.Main.C_EcranDeDepart.Startup.B.Dialogs.A_OptionsControlsButtons
import Z_MasterOfApps.Z.Android.Main.C_EcranDeDepart.Startup.ViewModel.Startup_Extension
import Z_MasterOfApps.Z.Android.Main.NavigationItems
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
internal fun A_StartupScreen(
    modifier: Modifier = Modifier,
    viewModelInitApp: ViewModelInitApp = viewModel(),
) {
    val items = NavigationItems.items
    val extensionVM = Startup_Extension(viewModelInitApp)

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(items) { screen ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = if (!viewModelInitApp._paramatersAppsViewModelModel.cLeTelephoneDuGerant!!)
                                    Color.Gray else screen.color
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
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = if (!viewModelInitApp._paramatersAppsViewModelModel.cLeTelephoneDuGerant!!)
                                        Color.DarkGray else Color.White
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = screen.titleArab,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (!viewModelInitApp._paramatersAppsViewModelModel.cLeTelephoneDuGerant!!)
                                        Color.DarkGray else Color.White
                                )
                            }
                        }
                    }
                }
            }
            if (viewModelInitApp._paramatersAppsViewModelModel.fabsVisibility) {
                A_OptionsControlsButtons(
                    extensionVM = extensionVM,
                    viewModelInitApp = viewModelInitApp,
                    paddingValues = paddingValues,
                )
            }
        }
    }
}
