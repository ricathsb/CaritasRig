package com.superbgoal.caritasrig.activity.homepage.profileicon

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.superbgoal.caritasrig.activity.homepage.profileicon.profilesettings.ProfileSettingsViewModel

@Composable
fun ImageCropperScreen(
    navController: NavController,
    viewModel: ProfileSettingsViewModel,
    imageUri: Uri?
) {
    Log.d("ImageCropperScreen", "Received image URI: $imageUri")
    // Cek validitas URI
    if (imageUri == null || imageUri.toString().isEmpty()) {
        Log.e("ImageCropperScreen", "Invalid or empty image URI: $imageUri")
        navController.popBackStack() // Navigasi kembali jika URI tidak valid
        return
    }

    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        try{
        if (result.isSuccessful) {
            result.uriContent?.let { croppedUri ->
                // Update ViewModel dengan hasil cropping
                viewModel.updateImageUri(croppedUri)
                Log.d("CropResult", "Cropped URI: $croppedUri")
            }
        } else {
            Log.w("CropResult", "User canceled cropping or an error occurred.")
        }}catch (e: Exception) {
        Log.d("CropResult", "Error cropping image: $e")
        }
        // Kembali ke ProfileSettingsScreen setelah cropping selesai
        navController.popBackStack()
    }

    LaunchedEffect(imageUri) {
        try {
            val cropOptions = CropImageContractOptions(
                uri = imageUri,
                cropImageOptions = CropImageOptions().apply {
                    aspectRatioX = 1
                    aspectRatioY = 1
                    fixAspectRatio = true
                }
            )
            imageCropLauncher.launch(cropOptions)
        } catch (e: Exception) {
            Log.e("ImageCropperScreen", "Error launching cropper: ${e.message}", e)
            navController.popBackStack()
        }
    }
    // Placeholder UI saat cropping sedang berjalan
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}