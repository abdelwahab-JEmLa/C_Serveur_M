package com.example.Apps_Head.Z.Archives

/*
suspend fun AppInitializeModel.load_Produits_FireBase() {
        try {
            val snapshot = ref_Produits_Main_DataBase.get().await()

            // Clear existing data first
            produits_Main_DataBase.clear()

            // Handle null or empty snapshot
            if (!snapshot.exists()) {
                Log.w("AppInitializeModel", "No data found in Firebase")
                return
            }

            // Try to get data as a List first
            val dataList = snapshot.getValue<List<Map<String, Any?>>>()

            // If list parsing fails, try as a Map (in case data is stored with keys)
            val rawData = when {
                dataList != null -> dataList
                snapshot.value is Map<*, *> -> {
                    // Convert map of products to list
                    (snapshot.value as Map<*, *>).values.mapNotNull { it as? Map<String, Any?> }
                }
                else -> {
                    Log.w("AppInitializeModel", "Unexpected data format in Firebase")
                    return
                }
            }

            Log.d("AppInitializeModel", "Raw data loaded successfully with ${rawData.size} entries")

            val convertedProduits = rawData.mapNotNull { productMap ->
                try {
                    // Safely extract colors and tastes
                    val coloursEtGouts = (productMap["colours_Et_Gouts"] as? List<*>)?.mapNotNull { color ->
                        (color as? Map<String, Any?>)?.let {
                            AppInitializeModel.ProduitModel.ColourEtGout_Model(
                                position_Du_Couleur_Au_Produit = (it["position_Du_Couleur_Au_Produit"] as? Number)?.toLong() ?: 0,
                                nom = (it["nom"] as? String) ?: "",
                                imogi = (it["imogi"] as? String) ?: ""
                            )
                        }
                    } ?: emptyList()

                    // Extract current grossist data
                    val currentGrossist = (productMap["grossist_Pour_Acheter_Ce_Produit_Dons_Cette_Cota"] as? Map<String, Any?>)?.let { grossistMap ->
                        val grossistColors = (grossistMap["colours_Et_Gouts_Commende"] as? List<*>)?.mapNotNull { colorMap ->
                            (colorMap as? Map<String, Any?>)?.let {
                                AppInitializeModel.ProduitModel.GrossistBonCommandes.ColoursGoutsCommendee(
                                    position_Du_Couleur_Au_Produit = (it["position_Du_Couleur_Au_Produit"] as? Number)?.toLong() ?: 0,
                                    id_Don_Tout_Couleurs = (it["id_Don_Tout_Couleurs"] as? Number)?.toLong() ?: 0,
                                    nom = (it["nom"] as? String) ?: "",
                                    quantity_Achete = (it["quantity_Achete"] as? Number)?.toInt() ?: 0,
                                    imogi = (it["imogi"] as? String) ?: ""
                                )
                            }
                        } ?: emptyList()

                        AppInitializeModel.ProduitModel.GrossistBonCommandes(
                            vid = (grossistMap["vid"] as? Number)?.toLong() ?: 0,
                            supplier_id = (grossistMap["supplier_id"] as? Number)?.toLong() ?: 0,
                            nom = (grossistMap["nom"] as? String) ?: "",
                            date = (grossistMap["date"] as? String) ?: "",
                            date_String_Divise = (grossistMap["date_String_Divise"] as? String) ?: "",
                            time_String_Divise = (grossistMap["time_String_Divise"] as? String) ?: "",
                            couleur = (grossistMap["couleur"] as? String) ?: "#FFFFFF",
                            currentCreditBalance = (grossistMap["currentCreditBalance"] as? Number)?.toDouble() ?: 0.0,
                            init_position_Grossist_Don_Parent_Grossists_List = (grossistMap["position_Grossist_Don_Parent_Grossists_List"] as? Number)?.toInt() ?: 0,
                            init_position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit = (grossistMap["position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit"] as? Number)?.toInt() ?: 0,
                            initialColours_Et_Gouts_Commende_Au_Supplier = grossistColors
                        )
                    }

                    // Safely extract mutable status
                    val mutableStatus = (productMap["mutable_App_Produit_Statues"] as? Map<String, Any?>)?.let { statusMap ->
                        val supplierTransaction = (statusMap["son_Grossist_Pour_Acheter_Ce_Produit_In_This_Transaction"] as? Map<String, Any?>)?.let { supplierMap ->
                            val supplierColors = (supplierMap["colours_Et_Gouts_Commende"] as? List<*>)?.mapNotNull { colorMap ->
                                (colorMap as? Map<String, Any?>)?.let {
                                    AppInitializeModel.ProduitModel.GrossistBonCommandes.ColoursGoutsCommendee(
                                        position_Du_Couleur_Au_Produit = (it["position_Du_Couleur_Au_Produit"] as? Number)?.toLong() ?: 0,
                                        id_Don_Tout_Couleurs = (it["id_Don_Tout_Couleurs"] as? Number)?.toLong() ?: 0,
                                        nom = (it["nom"] as? String) ?: "",
                                        quantity_Achete = (it["quantity_Achete"] as? Number)?.toInt() ?: 0,
                                        imogi = (it["imogi"] as? String) ?: ""
                                    )
                                }
                            } ?: emptyList()

                            AppInitializeModel.ProduitModel.GrossistBonCommandes(
                                vid = (supplierMap["vid"] as? Number)?.toLong() ?: 0,
                                supplier_id = (supplierMap["supplier_id"] as? Number)?.toLong() ?: 0,
                                nom = (supplierMap["nom"] as? String) ?: "",
                                date = (supplierMap["date"] as? String) ?: "",
                                date_String_Divise = (supplierMap["date_String_Divise"] as? String) ?: "",
                                time_String_Divise = (supplierMap["time_String_Divise"] as? String) ?: "",
                                couleur = (supplierMap["couleur"] as? String) ?: "#FFFFFF",
                                currentCreditBalance = (supplierMap["currentCreditBalance"] as? Number)?.toDouble() ?: 0.0,
                                init_position_Grossist_Don_Parent_Grossists_List = (supplierMap["position_Grossist_Don_Parent_Grossists_List"] as? Number)?.toInt() ?: 0,
                                init_position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit = (supplierMap["position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit"] as? Number)?.toInt() ?: 0,
                                initialColours_Et_Gouts_Commende_Au_Supplier = supplierColors
                            )
                        }

                        AppInitializeModel.ProduitModel.StatuesMutableProduit_Model(
                            init_dernier_Vent_date_time_String = (statusMap["dernier_Vent_date_time_String"] as? String) ?: "",
                            init_its_Filtre_Au_Grossists_Buttons = (statusMap["its_Filtre_Au_Grossists_Buttons"] as? Boolean) ?: false,
                            init_Son_Grossist_Pour_Acheter_Ce_Produit_In_This_Transaction = supplierTransaction
                        )
                    } ?: AppInitializeModel.ProduitModel.StatuesMutableProduit_Model()

                    // Create the product object with all properties
                    AppInitializeModel.ProduitModel(
                        id = (productMap["id"] as? Number)?.toLong() ?: 0,
                        it_ref_Id_don_FireBase = (productMap["it_ref_Id_don_FireBase"] as? Number)?.toLong() ?: 0,
                        it_ref_don_FireBase = (productMap["it_ref_don_FireBase"] as? String) ?: "",
                        init_nom = (productMap["nom"] as? String) ?: "",
                        init_besoin_To_Be_Updated = (productMap["besoin_To_Be_Updated"] as? Boolean) ?: false,
                        init_it_Image_besoin_To_Be_Updated = (productMap["it_Image_besoin_To_Be_Updated"] as? Boolean) ?: false,
                        initialNon_Trouve = (productMap["non_Trouve"] as? Boolean) ?: false,
                        init_colours_Et_Gouts = coloursEtGouts,
                        init_mutable_App_Produit_Statues = mutableStatus,
                        init_bon_Commend_De_Cette_Cota = currentGrossist
                    ).apply {
                        // Add purchase demands
                        (productMap["demmende_Achate_De_Cette_Produit"] as? List<*>)?.forEach { demand ->
                            (demand as? Map<String, Any?>)?.let { demandMap ->
                                val clientColors = (demandMap["colours_Et_Gouts_Acheter_Depuit_Client"] as? List<*>)?.mapNotNull { clientColor ->
                                    (clientColor as? Map<String, Any?>)?.let {
                                        AppInitializeModel.ProduitModel.ClientBonVent_Model.Color_Achat_Model(
                                            vidPosition = (it["vidPosition"] as? Number)?.toLong() ?: 0,
                                            nom = (it["nom"] as? String) ?: "",
                                            quantity_Achete = (it["quantity_Achete"] as? Number)?.toInt() ?: 0,
                                            imogi = (it["imogi"] as? String) ?: ""
                                        )
                                    }
                                } ?: emptyList()

                                this.acheteurs_pour_Cette_Cota.add(
                                    AppInitializeModel.ProduitModel.ClientBonVent_Model(
                                        vid = (demandMap["vid"] as? Number)?.toLong() ?: 0,
                                        id_Acheteur = (demandMap["id_Acheteur"] as? Number)?.toLong() ?: 0,
                                        nom_Acheteur = (demandMap["nom_Acheteur"] as? String) ?: "",
                                        time_String = (demandMap["time_String"] as? String) ?: "",
                                        inseartion_Temp = (demandMap["inseartion_Temp"] as? Number)?.toLong() ?: 0,
                                        inceartion_Date = (demandMap["inceartion_Date"] as? Number)?.toLong() ?: 0,
                                        init_colours_achete = clientColors
                                    )
                                )
                            }
                        }

                        // Add supplier choices
                        (productMap["grossist_Choisi_Pour_Acheter_CeProduit"] as? List<*>)?.forEach { supplier ->
                            (supplier as? Map<String, Any?>)?.let { supplierMap ->
                                val supplierColors = (supplierMap["colours_Et_Gouts_Commende"] as? List<*>)?.mapNotNull { colorMap ->
                                    (colorMap as? Map<String, Any?>)?.let {
                                        AppInitializeModel.ProduitModel.GrossistBonCommandes.ColoursGoutsCommendee(
                                            position_Du_Couleur_Au_Produit = (it["position_Du_Couleur_Au_Produit"] as? Number)?.toLong() ?: 0,
                                            id_Don_Tout_Couleurs = (it["id_Don_Tout_Couleurs"] as? Number)?.toLong() ?: 0,
                                            nom = (it["nom"] as? String) ?: "",
                                            quantity_Achete = (it["quantity_Achete"] as? Number)?.toInt() ?: 0,
                                            imogi = (it["imogi"] as? String) ?: ""
                                        )
                                    }
                                } ?: emptyList()

                                historique_BonS_Commend.add(
                                    AppInitializeModel.ProduitModel.GrossistBonCommandes(
                                        vid = (supplierMap["vid"] as? Number)?.toLong() ?: 0,
                                        supplier_id = (supplierMap["supplier_id"] as? Number)?.toLong() ?: 0,
                                        nom = (supplierMap["nom"] as? String) ?: "",
                                        date = (supplierMap["date"] as? String) ?: "",
                                        date_String_Divise = (supplierMap["date_String_Divise"] as? String) ?: "",
                                        time_String_Divise = (supplierMap["time_String_Divise"] as? String) ?: "",
                                        couleur = (supplierMap["couleur"] as? String) ?: "#FFFFFF",
                                        currentCreditBalance = (supplierMap["currentCreditBalance"] as? Number)?.toDouble() ?: 0.0,
                                        init_position_Grossist_Don_Parent_Grossists_List = (supplierMap["position_Grossist_Don_Parent_Grossists_List"] as? Number)?.toInt() ?: 0,
                                        init_position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit = (supplierMap["position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit"] as? Number)?.toInt() ?: 0,
                                        initialColours_Et_Gouts_Commende_Au_Supplier = supplierColors
                                    )
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("AppInitializeModel", "Error processing product data", e)
                    null
                }
            }

            // Log statistics for debugging
            Log.d("AppInitializeModel", "Converted ${convertedProduits.size} products")
            Log.d("AppInitializeModel", "Products with suppliers: ${convertedProduits.count { it.historique_BonS_Commend.isNotEmpty() }}")
            Log.d("AppInitializeModel", "Products with demands: ${convertedProduits.count { it.acheteurs_pour_Cette_Cota.isNotEmpty() }}")
            Log.d("AppInitializeModel", "Products with colors: ${convertedProduits.count { it.colours_Et_Gouts.isNotEmpty() }}")

            // Update the state list
            produits_Main_DataBase.addAll(convertedProduits)

        } catch (e: Exception) {
            Log.e("AppInitializeModel", "Failed to load state from Firebase", e)
            throw Exception("Failed to load state from Firebase: ${e.message}")
        }
    }
    */
