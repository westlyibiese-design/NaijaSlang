package com.westly.naijaslang.feature.settings

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.westly.naijaslang.R
import com.westly.naijaslang.core.prefs.PreferencesRepository
import com.westly.naijaslang.core.prefs.UserPreferences
import com.westly.naijaslang.core.theme.NaijaTheme
import com.westly.naijaslang.core.ui.AppHeader
import com.westly.naijaslang.core.ui.FlagIcon
import com.westly.naijaslang.core.ui.MoonIcon
import com.westly.naijaslang.core.ui.TextSizeIcon
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

/**
 * Phase 5: Display (Dark mode, Text size) and About (About, Rate, Share, Report),
 * then the footer. Dark mode and Text size are saved at once and change the
 * whole app live (the app root reads the same saved settings).
 */
@Composable
fun SettingsScreen(onOpenAbout: () -> Unit) {
    val colors = NaijaTheme.colors
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { PreferencesRepository(context.applicationContext) }
    val preferences by repository.preferences.collectAsState(initial = UserPreferences())

    // While the thumb is being dragged the slider follows the finger, not the saved value.
    var dragValue by remember { mutableStateOf<Float?>(null) }
    var lastWrittenStep by remember { mutableStateOf<Int?>(null) }

    val noEmailMessage = stringResource(R.string.no_email_app)
    val reportSubject = stringResource(R.string.report_subject)
    val contactEmail = stringResource(R.string.contact_email)

    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader()

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
        ) {
            // ---------- DISPLAY ----------
            SettingsCard(title = stringResource(R.string.settings_display)) {
                SettingsRow(
                    icon = MoonIcon,
                    title = stringResource(R.string.settings_dark_mode),
                    subtitle = stringResource(R.string.settings_dark_mode_sub),
                    trailing = {
                        Switch(
                            // Shows the theme that is really in use (phone setting until a choice is saved).
                            checked = colors.isDark,
                            onCheckedChange = { dark -> scope.launch { repository.setDarkMode(dark) } },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = colors.gold,
                                checkedBorderColor = Color.Transparent,
                                uncheckedThumbColor = colors.switchThumbOff,
                                uncheckedTrackColor = colors.switchTrackOff,
                                uncheckedBorderColor = Color.Transparent,
                            ),
                        )
                    },
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 1.dp,
                    color = colors.divider,
                )
                SettingsRow(
                    icon = TextSizeIcon,
                    title = stringResource(R.string.settings_text_size),
                    subtitle = stringResource(R.string.settings_text_size_sub),
                    bottomPadding = 4.dp,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Fixed-size boxes so the slider does not move while the text grows or shrinks.
                    Box(modifier = Modifier.width(36.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(R.string.settings_text_size_small_a),
                            style = MaterialTheme.typography.titleSmall,
                            color = colors.textSecondary,
                        )
                    }
                    Slider(
                        value = dragValue ?: preferences.textSizeStep.toFloat(),
                        onValueChange = { value ->
                            dragValue = value
                            val step = value.roundToInt()
                            if (step != (lastWrittenStep ?: preferences.textSizeStep)) {
                                lastWrittenStep = step
                                scope.launch { repository.setTextSizeStep(step) }
                            }
                        },
                        onValueChangeFinished = { dragValue = null },
                        valueRange = 0f..(UserPreferences.TEXT_SIZE_STEPS - 1).toFloat(),
                        steps = UserPreferences.TEXT_SIZE_STEPS - 2,
                        colors = SliderDefaults.colors(
                            thumbColor = colors.gold,
                            activeTrackColor = colors.gold,
                            inactiveTrackColor = colors.sliderInactive,
                            activeTickColor = Color.Transparent,
                            inactiveTickColor = Color.Transparent,
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp),
                    )
                    Box(modifier = Modifier.width(44.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(R.string.settings_text_size_large_a),
                            style = MaterialTheme.typography.headlineSmall.copy(fontSize = 30.sp),
                            color = colors.wordTitle,
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ---------- ABOUT ----------
            SettingsCard(title = stringResource(R.string.settings_about_section)) {
                SettingsRow(
                    icon = Icons.Outlined.Info,
                    title = stringResource(R.string.settings_about),
                    subtitle = stringResource(R.string.settings_about_sub),
                    onClick = onOpenAbout,
                    trailing = { ChevronIcon() },
                )
                RowDivider()
                SettingsRow(
                    icon = Icons.Outlined.Star,
                    title = stringResource(R.string.settings_rate),
                    subtitle = stringResource(R.string.settings_rate_sub),
                    // Does nothing for now: it will be connected after the app is on Google Play.
                    onClick = { },
                    trailing = { ChevronIcon() },
                )
                RowDivider()
                SettingsRow(
                    icon = Icons.Outlined.Share,
                    title = stringResource(R.string.settings_share),
                    subtitle = stringResource(R.string.settings_share_sub),
                    // Does nothing for now: it will be connected after the app is on Google Play.
                    onClick = { },
                    trailing = { ChevronIcon() },
                )
                RowDivider()
                SettingsRow(
                    icon = FlagIcon,
                    title = stringResource(R.string.settings_report),
                    subtitle = stringResource(R.string.settings_report_sub),
                    onClick = { reportWord(context, contactEmail, reportSubject, noEmailMessage) },
                    trailing = { ChevronIcon() },
                    bottomPadding = 14.dp,
                )
            }

            Spacer(Modifier.height(28.dp))

            Text(
                text = stringResource(R.string.settings_footer_version, appVersionName(context)),
                style = MaterialTheme.typography.bodySmall,
                color = colors.searchPlaceholder,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = stringResource(R.string.copyright),
                style = MaterialTheme.typography.bodySmall,
                color = colors.searchPlaceholder,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 8.dp),
            )
        }
    }
}

@Composable
private fun SettingsCard(title: String, content: @Composable () -> Unit) {
    val colors = NaijaTheme.colors
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = colors.card,
        border = if (colors.isDark) BorderStroke(1.dp, colors.cardBorder) else null,
        shadowElevation = if (colors.isDark) 0.dp else 1.dp,
    ) {
        Column(modifier = Modifier.padding(top = 18.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.5.sp),
                color = colors.textSecondary,
                modifier = Modifier.padding(start = 16.dp, bottom = 4.dp),
            )
            content()
        }
    }
}

@Composable
private fun RowDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        thickness = 1.dp,
        color = NaijaTheme.colors.divider,
    )
}

@Composable
private fun ChevronIcon() {
    Icon(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
        contentDescription = null,
        tint = NaijaTheme.colors.gold,
        modifier = Modifier.size(30.dp),
    )
}

/** One row: icon, title, subtitle and (optionally) something on the right. */
@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    bottomPadding: androidx.compose.ui.unit.Dp = 14.dp,
) {
    val colors = NaijaTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(start = 16.dp, end = 12.dp, top = 14.dp, bottom = bottomPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.searchIcon,
            modifier = Modifier.size(30.dp),
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 18.sp),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
            )
        }
        if (trailing != null) {
            Spacer(Modifier.width(8.dp))
            trailing()
        }
    }
}

/** Opens the email app with the address, subject and the three lines of the report. */
private fun reportWord(context: Context, email: String, subject: String, noEmailMessage: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:$email")
        putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, "Word: \nWhat is wrong: \nSuggested fix: ")
    }
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, noEmailMessage, Toast.LENGTH_SHORT).show()
    }
}

/** The version from the build (versionName in build.gradle.kts). */
@Suppress("DEPRECATION")
internal fun appVersionName(context: Context): String = try {
    val packageManager = context.packageManager
    val info = if (Build.VERSION.SDK_INT >= 33) {
        packageManager.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(0))
    } else {
        packageManager.getPackageInfo(context.packageName, 0)
    }
    info.versionName.orEmpty()
} catch (e: Exception) {
    ""
}
