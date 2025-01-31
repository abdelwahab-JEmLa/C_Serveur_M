package Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_4.id3_AfficheurDesProduitsPourLeColecteur.ViewModel

import Z_MasterOfApps.Kotlin.Model.ClientsDataBase.Companion.updateClientsDataBase
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp

class ExtensionVMApp1FragmentId_3(
    val viewModelInitApp: ViewModelInitApp,
) {
    val clientDataBaseSnapList = viewModelInitApp._modelAppsFather.clientDataBaseSnapList

    fun upButton(index: Int) {
        // Ensure index is valid and there's a previous element
        if (index <= 0 || index >= clientDataBaseSnapList.size) {
            return
        }

        // Get the current and previous clients
        val currentClient = clientDataBaseSnapList[index]
        val prevClient = clientDataBaseSnapList[index - 1]

        // Swap their positions
        val currentPosition = currentClient.statueDeBase.positionDonClientsList
        val prevPosition = prevClient.statueDeBase.positionDonClientsList

        // Update positions
        currentClient.statueDeBase.positionDonClientsList = prevPosition
        prevClient.statueDeBase.positionDonClientsList = currentPosition

        // Update the list order
        clientDataBaseSnapList[index] = prevClient
        clientDataBaseSnapList[index - 1] = currentClient

        // Update both clients in the database
        currentClient.updateClientsDataBase(viewModelInitApp)
        prevClient.updateClientsDataBase(viewModelInitApp)
    }
}
