package com.wmc.eventplaner.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * @author: Muhammad Kamran
 *
*/
@Composable
fun ScreenContainer(
    modifier: Modifier = Modifier,
    currentPrompt: PromptTypeShow?=null,
    horizontalPadding: Dp = 16.dp,
    content: @Composable (() -> Unit),
) {
    //val keyboardController = LocalSoftwareKeyboardController.current
    Box(
        modifier
            .fillMaxSize()
            .padding(horizontal = horizontalPadding)) {
        content()
    }
    currentPrompt?.let { prompt ->
        when (prompt) {
            is PromptTypeShow.Loading -> {
                ShowLoader()
            }
            is PromptTypeShow.Error -> {


            }
            is PromptTypeShow.Warning -> {

            }
            is PromptTypeShow.Success -> {

            }
            is PromptTypeShow.ComingSoon -> {

            }
            is PromptTypeShow.Confirmation -> {

            }
            is PromptTypeShow.CustomContent -> {

            }
            else->{}
        }
    }
}
sealed class PromptTypeShow(
    val title: String?,
    val message: String?,
    val cancelable: Boolean = true,
    val onDragClose: Boolean = false,
    val img: Int? = null,
    val positiveButtonText: String = "Okay",
    val negativeButtonText: String = "Okay",
    val onDismiss: () -> Unit,
    val positiveButtonClick: () -> Unit,
    val negativeButtonClick: () -> Unit = {},
    val customContent: (@Composable (callDismiss: () -> Unit) -> Unit)? = null,
) {
    class Loading: PromptTypeShow(title = null, message = null, cancelable = false, img = null, onDismiss = {}, positiveButtonClick = {})
    class Error(
        title: String? = null,
        message: String? = null,
        cancelable: Boolean = true,
        onDragClose: Boolean = true,
        img: Int? = null,
        buttonText: String = "Okay",
        onDismiss: () -> Unit,
        onButtonClick: () -> Unit
    ) : PromptTypeShow(
        title = title,
        message = message.takeIf { !it.isNullOrEmpty() }?:"We are unable to process your request at this time. Please try again later.",
        cancelable = cancelable,
        onDragClose=onDragClose,
        img = img,
        positiveButtonText = buttonText,
        onDismiss = onDismiss,
        positiveButtonClick = onButtonClick
    )

    class Success(
        title: String? = null,
        message: String? = null,
        cancelable: Boolean = true,
        onDragClose: Boolean = true,
        img: Int? = null,
        buttonText: String = "Okay",
        onDismiss: () -> Unit,
        onButtonClick: () -> Unit
    ) : PromptTypeShow(
        title = title,
        message = message,
        cancelable = cancelable,
        onDragClose=onDragClose,
        img = img,
        positiveButtonText = buttonText,
        onDismiss = onDismiss,
        positiveButtonClick = onButtonClick
    )

    class Warning(
        title: String? = null,
        message: String? = null,
        cancelable: Boolean = true,
        onDragClose: Boolean = true,
        img: Int? = null,
        buttonText: String = "Okay",
        onDismiss: () -> Unit,
        onButtonClick: () -> Unit
    ) : PromptTypeShow(
        title = title,
        message = message,
        cancelable = cancelable,
        onDragClose=onDragClose,
        img = img,
        positiveButtonText = buttonText,
        onDismiss = onDismiss,
        positiveButtonClick = onButtonClick
    )

    class ComingSoon(
        title: String? = null,
        message: String = "Coming Soon",
        cancelable: Boolean = true,
        onDragClose: Boolean = true,
        img: Int? = null,
        buttonText: String = "Okay",
        onButtonClick: () -> Unit={},
        onDismiss: () -> Unit,
    ) : PromptTypeShow(
        title = title,
        message = message,
        cancelable = cancelable,
        onDragClose=onDragClose,
        img = img,
        positiveButtonText = buttonText,
        onDismiss = onDismiss,
        positiveButtonClick = onButtonClick
    )

    class Confirmation(
        title: String? = null,
        message: String? = null,
        cancelable: Boolean = true,
        onDragClose: Boolean = true,
        img: Int? = null,
        positiveButtonText: String = "Yes",
        negativeButtonText: String = "No",
        onDismiss: () -> Unit,
        positiveButtonClick: () -> Unit = {},
        negativeButtonClick: () -> Unit = {}
    ) : PromptTypeShow(
        title = title,
        message = message,
        cancelable = cancelable,
        onDragClose=onDragClose,
        img = img,
        positiveButtonText = positiveButtonText,
        negativeButtonText = negativeButtonText,
        onDismiss = onDismiss,
        positiveButtonClick = positiveButtonClick,
        negativeButtonClick = negativeButtonClick
    )


    class CustomContent(
        cancelable: Boolean = true,
        onDragClose: Boolean = true,
        onDismiss: () -> Unit,
        customContent: (@Composable (callDismiss: () -> Unit) -> Unit)? = null
    ) : PromptTypeShow(
        title = null,
        message = null,
        customContent=customContent,
        positiveButtonClick={},
        cancelable = cancelable,
        onDragClose=onDragClose,
        onDismiss = onDismiss
    )

    class UnAthorized(title: String) : PromptTypeShow(
        title = title,
        message = null,
        cancelable = false,
        img = null,
        onDismiss = {},
        positiveButtonClick = {}
    )

}

@Preview(name = "ScreenContainer")
@Composable
private fun PreviewScreenContainer() {
    ScreenContainer{

    }
}

@Composable
fun PreviewWrapper(
    content: @Composable () -> Unit = {},
    topAppBarTitle: String="Aik Digital",
    showAppBar: Boolean = true
) {

}

@Composable fun AppBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    center = Offset.Infinite.copy(y = 0f),
                    colors = listOf(Color(0xFF13455e), Color(0xFF050f18)),
                )
            )
    )
}