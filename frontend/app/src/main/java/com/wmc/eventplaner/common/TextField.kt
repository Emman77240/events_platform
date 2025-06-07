package com.wmc.eventplaner.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.wmc.eventplaner.R

/**
 * A customizable standard text field component.
 *
 * @param value The current text value
 * @param onValueChange Callback when text changes
 * @param placeholder The hint text when empty
 * @param leadingIcon Optional icon at the start
 * @param modifier Modifier for styling
 * @param enabled Controls enabled state
 * @param readOnly Controls read-only state
 * @param error Shows error state
 * @param supportingText Helper text below field
 * @param singleLine Constrains to single line
 */


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "",
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    maxLines: Int = 1,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    supportingText: String? = null,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            maxLines = maxLines,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp),
            label = {
                if (label.isNotEmpty()) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            placeholder = {
                if (placeholder.isNotEmpty() && value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            },
            leadingIcon = leadingIcon?.let { icon ->
                {
                    Icon(
                        imageVector = icon,
                        contentDescription = "$label icon",
                        tint = if (isError) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        }
                    )
                }
            },
            trailingIcon = trailingIcon?.let { icon ->
                {
                    if (onTrailingIconClick != null) {
                        IconButton(
                            onClick = onTrailingIconClick,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = "$label action",
                                tint = if (isError) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                }
                            )
                        }
                    } else {
                        Icon(
                            imageVector = icon,
                            contentDescription = "$label icon",
                            tint = if (isError) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            }
                        )
                    }
                }
            },
            singleLine = singleLine,
            shape = MaterialTheme.shapes.medium,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                errorTextColor = MaterialTheme.colorScheme.error,
                focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                focusedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
                errorContainerColor = MaterialTheme.colorScheme.errorContainer,
                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            ),
            enabled = enabled,
            readOnly = readOnly,
            isError = isError,
            supportingText = {
                Column {
                    supportingText?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                    errorMessage?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            interactionSource = interactionSource
        )
    }
}

/**
 * A specialized text field for password input with visibility toggle.
 *
 * @param value The current password value
 * @param onValueChange Callback when password changes
 * @param placeholder The hint text when empty
 * @param modifier Modifier for styling
 * @param enabled Controls enabled state
 * @param error Shows error state
 * @param supportingText Helper text below field
 */

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Password",
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    var showPassword by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        visualTransformation = if (showPassword) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        trailingIcon = {
            IconButton(onClick = { showPassword = !showPassword }) {
                Icon(
                      painter = painterResource(
                            id = if (showPassword) {
                                R.drawable.show_password
                            } else {
                                R.drawable.hide_password
                            }
                            ),
                    contentDescription = if (showPassword) {
                        "Hide password"
                    } else {
                        "Show password"
                    }
                )
            }
        },
        isError = isError,
        supportingText = {
            if (isError && errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),  // Changed from fixed height to min height
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            errorTextColor = MaterialTheme.colorScheme.error,
            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            focusedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
            errorContainerColor = MaterialTheme.colorScheme.errorContainer,
            focusedLabelColor = MaterialTheme.colorScheme.onSurface,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )
    )
}
