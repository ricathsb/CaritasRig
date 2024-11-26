package com.superbgoal.caritasrig.activity.homepage.favorites.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.data.model.component.Casing
import com.superbgoal.caritasrig.functions.GenericCard
import com.superbgoal.caritasrig.functions.loadItemsFromResources
import com.superbgoal.caritasrig.functions.savedFavorite

@Composable
fun CasingFavScreen(navController: NavController) {
    val context = LocalContext.current
    val casings: List<Casing> = remember {
        loadItemsFromResources(
            context = context,
            resourceId = R.raw.casing
        )
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(casings) { casing ->
            GenericCard(
                item = casing,
                onClick = {

                },
                onFavoriteClick = {
                    savedFavorite(casing = casing)
                }
            ) { item ->
                Text(
                    text = item.name, // Asumsi Casing memiliki properti "name"
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = item.type, // Asumsi Casing memiliki properti "description"
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
