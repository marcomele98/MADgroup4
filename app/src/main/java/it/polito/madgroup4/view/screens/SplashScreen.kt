package it.polito.madgroup4.view.screens

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import it.polito.madgroup4.R
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    valid: Boolean?,
    onStart: () -> Unit,
    onSplashEndedValid: () -> Unit,
    onSplashEndedInvalid: () -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
/*
    val color = remember { Animatable(Color.Black) }
*/
    val currentValid = rememberUpdatedState(newValue = valid)

    LaunchedEffect(key1 = null) {
        delay(3000)
        if (currentValid.value == true) onSplashEndedValid()
        else onSplashEndedInvalid()
    }

    LaunchedEffect(key1 = valid) {
        valid?.let { valid ->
            val animateToColor = if (valid) Color.Green else Color.Red
/*
            color.animateTo(animateToColor, animationSpec = tween(500))
*/
        }
    }
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.profile),
            contentDescription = null,
//            colorFilter = ColorFilter.tint(color.value),
            modifier = Modifier.size(100.dp),
        )
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                onStart()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

