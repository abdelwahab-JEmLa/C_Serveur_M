package com.example.Packages.Z.Archives.P1

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.serveurecherielhanaaebeljemla.Models.UiStat


/**
 * PreviewParameter Provider for Fragment3_Main_Screen Preview
 * Provides sample data for the preview
 */
internal class FragmentPreviewParameterProvider :
    PreviewParameterProvider<UiStat> {
    override val values: Sequence<UiStat> = sequenceOf(

    )

    // Provide a default value
    override val count: Int = 1
}

@Preview(showBackground = true)
@Composable
internal fun FragmentScreenParameterizedPreview(
    @PreviewParameter(FragmentPreviewParameterProvider::class)
    state: UiStat
) {
    MaterialTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {

        }
    }
}
