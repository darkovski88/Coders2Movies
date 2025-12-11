package com.coders.two.movies.presentation.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.coders.two.movies.R
import com.coders.two.movies.presentation.theme.Coders2MoviesTheme


@Composable
fun IconText(
    @StringRes text: Int,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = stringResource(text)
        )
        Text(
            textDecoration = TextDecoration.Underline,
            text = stringResource(text)
        )
    }
}

@Composable
fun IconText(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .fillMaxSize()
                .padding(4.dp)
        ) {
            Icon(
                imageVector = icon,
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = text,
            )
        }
        Text(
            text = text,
            textDecoration = TextDecoration.Underline
        )
    }
}


@PreviewLightDark
@Composable
private fun IconTextPreview() {
    Coders2MoviesTheme {
        Surface {
            Column {
                IconText(text = R.string.app_name, Icons.Default.Build)
                IconText(
                    text = "Some String text",
                    icon = Icons.Default.Build
                )
            }
        }
    }
}