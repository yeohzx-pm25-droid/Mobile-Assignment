package com.example.dcsg1_mobileassignment.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL


@Composable
fun RemoteDonationImage(
    imageUrl: String?,
    fallbackTint: Long,
    modifier: Modifier = Modifier,
    imageRes: Int? = null,
    cornerRadius: Dp = 10.dp,
    contentScale: ContentScale = ContentScale.Crop,
    matchImageAspectRatio: Boolean = false,
    fallbackAspectRatio: Float = 4f / 3f
) {
    val context = LocalContext.current

    val imageBitmap by produceState<ImageBitmap?>(initialValue = null, imageUrl) {
        value = null

        if (!imageUrl.isNullOrBlank()) {
            value = withContext(Dispatchers.IO) {
                runCatching {
                    URL(imageUrl).openStream().use { stream ->
                        BitmapFactory.decodeStream(stream)?.asImageBitmap()
                    }
                }.getOrNull()
            }
        }
    }

    val localResBitmap = remember(imageRes) {
        imageRes?.let { id ->
            runCatching { BitmapFactory.decodeResource(context.resources, id) }.getOrNull()
        }
    }

    val shape = RoundedCornerShape(cornerRadius)
    val bitmap = imageBitmap

    val sizingModifier = if (matchImageAspectRatio) {
        val ratio = when {
            bitmap != null -> bitmap.width.toFloat() / bitmap.height.toFloat()
            localResBitmap != null -> localResBitmap.width.toFloat() / localResBitmap.height.toFloat()
            else -> fallbackAspectRatio
        }
        modifier.aspectRatio(ratio)
    } else {
        modifier
    }

    Box(
        modifier = sizingModifier
            .clip(shape)
            .background(Color(fallbackTint))
    ) {
        when {
            bitmap != null -> {
                // Display the image downloaded from the URL.
                Image(
                    bitmap = bitmap,
                    contentDescription = "Donation image",
                    contentScale = contentScale,
                    modifier = Modifier.fillMaxSize()
                )
            }

            imageRes != null -> {
                // Display the local drawable when no URL image is available.
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Donation image",
                    contentScale = contentScale,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}