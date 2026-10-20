package com.westly.naijaslang.feature.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.westly.naijaslang.R
import com.westly.naijaslang.core.theme.NaijaTheme
import com.westly.naijaslang.core.ui.AppHeader
import com.westly.naijaslang.core.ui.HeaderVariant

/** Phase 5: the About page with the agreed text. The version comes from the build. */
@Composable
fun AboutScreen(onBack: () -> Unit) {
    val colors = NaijaTheme.colors
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader(variant = HeaderVariant.Back(onBackClick = onBack))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 20.dp),
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                color = colors.card,
                border = if (colors.isDark) BorderStroke(1.dp, colors.cardBorder) else null,
                shadowElevation = if (colors.isDark) 0.dp else 2.dp,
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = stringResource(R.string.brand_name),
                        style = MaterialTheme.typography.headlineMedium,
                        color = colors.wordTitle,
                    )
                    Text(
                        text = stringResource(R.string.brand_tagline),
                        style = MaterialTheme.typography.titleSmall,
                        color = colors.textSecondary,
                        modifier = Modifier.padding(top = 4.dp),
                    )

                    Spacer(Modifier.height(20.dp))
                    Text(
                        text = stringResource(R.string.about_description),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.about_disclaimer),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Spacer(Modifier.height(20.dp))
                    HorizontalDivider(thickness = 1.dp, color = colors.divider)
                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.about_version, appVersionName(context)),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary,
                    )
                    Text(
                        text = stringResource(R.string.copyright),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                    Text(
                        text = stringResource(R.string.about_contact, stringResource(R.string.contact_email)),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }
    }
}
