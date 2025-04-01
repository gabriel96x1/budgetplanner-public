package com.bytesdrawer.budgetplanner.common.composables

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bytesdrawer.budgetplanner.common.models.local.Category

@Composable
fun CategoryIcon(
    context: Context,
    category: Category
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(
                Color(category.color),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = context.resources.getIdentifier(category.icon, "drawable", context.packageName)),
            contentDescription = category.name,
            tint = Color.White
        )
    }
}
