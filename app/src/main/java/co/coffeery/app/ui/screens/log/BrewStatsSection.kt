package co.coffeery.app.ui.screens.log

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.coffeery.app.R
import co.coffeery.app.data.local.BrewLogEntity
import co.coffeery.app.ui.components.AppText
import co.coffeery.app.ui.components.CoffeeCard
import co.coffeery.app.ui.components.Glyph
import co.coffeery.app.ui.components.LineIcon
import co.coffeery.app.ui.theme.CoffeeTheme
import co.coffeery.app.util.Format
import java.time.Instant
import java.time.ZoneId
import java.util.Locale

@Composable
fun BrewStatsSection(brewLogs: List<BrewLogEntity>) {
    val scrollState = rememberScrollState()

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth().verticalScroll(scrollState),
    ) {
        TotalBrewsCard(brewLogs)
        EquipmentBreakdownCard(brewLogs)
        RoastPreferenceCard(brewLogs)
        AvgRatingCard(brewLogs)
        TimeOfDayCard(brewLogs)
        FavoriteRatioCard(brewLogs)
    }
}

@Composable
private fun TotalBrewsCard(brewLogs: List<BrewLogEntity>) {
    val colors = CoffeeTheme.colors
    CoffeeCard(modifier = Modifier.fillMaxWidth(), contentPadding = 14) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            LineIcon(Glyph.CUP, colors.accent, Modifier.size(36.dp))
            Spacer(Modifier.width(10.dp))
            Column {
                AppText(brewLogs.size.toString(), style = CoffeeTheme.type.display, color = colors.accent)
                AppText(stringResource(R.string.stats_total_brews), style = CoffeeTheme.type.caption, color = colors.textSecondary)
            }
        }
    }
}

@Composable
private fun EquipmentBreakdownCard(brewLogs: List<BrewLogEntity>) {
    val colors = CoffeeTheme.colors
    val grouped = brewLogs.groupBy { it.equipmentName }
        .mapValues { it.value.size }
        .entries
        .sortedByDescending { it.value }
    if (grouped.isEmpty()) return

    val maxCount = grouped.maxOf { it.value }.toFloat()

    CoffeeCard(modifier = Modifier.fillMaxWidth(), contentPadding = 14) {
        AppText(stringResource(R.string.stats_equipment_breakdown), style = CoffeeTheme.type.label, color = colors.textSecondary)
        Spacer(Modifier.height(10.dp))
        grouped.forEach { (name, count) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
            ) {
                AppText(name, style = CoffeeTheme.type.caption, color = colors.textPrimary, modifier = Modifier.width(80.dp))
                BarCanvas(
                    ratio = count / maxCount,
                    color = colors.accent,
                    modifier = Modifier.weight(1f).height(24.dp),
                )
                Spacer(Modifier.width(6.dp))
                AppText(count.toString(), style = CoffeeTheme.type.caption, color = colors.textSecondary)
            }
        }
    }
}

@Composable
private fun RoastPreferenceCard(brewLogs: List<BrewLogEntity>) {
    val colors = CoffeeTheme.colors
    val roasts = listOf("LIGHT", "MEDIUM", "DARK")
    val labels = listOf(
        stringResource(R.string.roast_light),
        stringResource(R.string.roast_medium),
        stringResource(R.string.roast_dark),
    )
    val counts = roasts.map { roast ->
        brewLogs.count { it.roast.equals(roast, ignoreCase = true) }
    }
    val maxCount = counts.max().toFloat().coerceAtLeast(1f)

    CoffeeCard(modifier = Modifier.fillMaxWidth(), contentPadding = 14) {
        AppText(stringResource(R.string.stats_roast_preference), style = CoffeeTheme.type.label, color = colors.textSecondary)
        Spacer(Modifier.height(10.dp))
        counts.forEachIndexed { i, count ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
            ) {
                AppText(labels[i], style = CoffeeTheme.type.caption, color = colors.textPrimary, modifier = Modifier.width(60.dp))
                BarCanvas(
                    ratio = count / maxCount,
                    color = colors.accent,
                    modifier = Modifier.weight(1f).height(24.dp),
                )
                Spacer(Modifier.width(6.dp))
                AppText(count.toString(), style = CoffeeTheme.type.caption, color = colors.textSecondary)
            }
        }
    }
}

@Composable
private fun AvgRatingCard(brewLogs: List<BrewLogEntity>) {
    val colors = CoffeeTheme.colors
    val rated = brewLogs.filter { it.rating > 0 }
    if (rated.isEmpty()) return

    val avg = rated.map { it.rating }.average()
    val avgStr = String.format(Locale.US, "%.1f", avg)

    CoffeeCard(modifier = Modifier.fillMaxWidth(), contentPadding = 14) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            LineIcon(Glyph.CHECK, colors.accent, Modifier.size(36.dp))
            Spacer(Modifier.width(10.dp))
            Column {
                AppText("$avgStr ★", style = CoffeeTheme.type.display, color = colors.accent)
                AppText(stringResource(R.string.stats_avg_rating), style = CoffeeTheme.type.caption, color = colors.textSecondary)
            }
        }
    }
}

@Composable
private fun TimeOfDayCard(brewLogs: List<BrewLogEntity>) {
    val colors = CoffeeTheme.colors

    val timeSlots = listOf(
        Triple(stringResource(R.string.stats_morning), 6..11, 0),
        Triple(stringResource(R.string.stats_afternoon), 12..17, 1),
        Triple(stringResource(R.string.stats_evening), 18..23, 2),
        Triple(stringResource(R.string.stats_night), 0..5, 3),
    )

    val counts = IntArray(4)
    brewLogs.forEach { log ->
        val hour = Instant.ofEpochMilli(log.timestamp)
            .atZone(ZoneId.systemDefault())
            .hour
        val slot = timeSlots.indexOfFirst { hour in it.second }
        if (slot >= 0) counts[slot]++
    }

    val total = counts.sum().coerceAtLeast(1)
    val percentages = counts.map { it * 100f / total }

    CoffeeCard(modifier = Modifier.fillMaxWidth(), contentPadding = 14) {
        AppText(stringResource(R.string.stats_time_of_day), style = CoffeeTheme.type.label, color = colors.textSecondary)
        Spacer(Modifier.height(10.dp))
        timeSlots.forEachIndexed { i, (label, _, _) ->
            val pct = percentages[i]
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
            ) {
                AppText(label, style = CoffeeTheme.type.caption, color = colors.textPrimary, modifier = Modifier.width(140.dp))
                BarCanvas(
                    ratio = pct / 100f,
                    color = colors.accent,
                    modifier = Modifier.weight(1f).height(24.dp),
                )
                Spacer(Modifier.width(6.dp))
                AppText("${pct.toInt()}%", style = CoffeeTheme.type.caption, color = colors.textSecondary)
            }
        }
    }
}

@Composable
private fun FavoriteRatioCard(brewLogs: List<BrewLogEntity>) {
    val colors = CoffeeTheme.colors
    if (brewLogs.isEmpty()) return

    val ratioCounts = brewLogs
        .groupBy { it.ratioDenominator }
        .mapValues { it.value.size }
    val bestRatio = ratioCounts.maxByOrNull { it.value }
    if (bestRatio == null || bestRatio.value == 0) return

    CoffeeCard(modifier = Modifier.fillMaxWidth(), contentPadding = 14) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            LineIcon(Glyph.BEAN, colors.accent, Modifier.size(36.dp))
            Spacer(Modifier.width(10.dp))
            Column {
                AppText("1:${Format.ratio(bestRatio.key)}", style = CoffeeTheme.type.display, color = colors.accent)
                AppText(
                    stringResource(R.string.stats_favorite_ratio, bestRatio.value),
                    style = CoffeeTheme.type.caption,
                    color = colors.textSecondary,
                )
            }
        }
    }
}

@Composable
private fun BarCanvas(
    ratio: Float,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val safeRatio = ratio.coerceIn(0f, 1f)
        drawRoundRect(
            color = color,
            topLeft = Offset(0f, 4.dp.toPx()),
            size = Size(w * safeRatio, 16.dp.toPx()),
            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
        )
    }
}
