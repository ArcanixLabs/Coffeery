package co.coffeery.app.ui.screens.log

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.coffeery.app.R
import co.coffeery.app.data.local.BeanEntity
import co.coffeery.app.data.local.BrewLogEntity
import co.coffeery.app.ui.components.AppText
import co.coffeery.app.ui.components.CoffeeCard
import co.coffeery.app.ui.components.Glyph
import co.coffeery.app.ui.components.LineIcon
import co.coffeery.app.ui.theme.CoffeeTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

data class Achievement(
    val id: String,
    val titleRes: Int,
    val descriptionRes: Int,
    val glyph: Glyph,
    val isUnlocked: Boolean,
)

fun checkAchievements(
    brewLogs: List<BrewLogEntity>,
    beans: List<BeanEntity>,
    completedChapters: Set<Int> = emptySet(),
): List<Achievement> {
    val days = brewLogs.map {
        Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
    }.toSet()
    var streak = 0
    var date = LocalDate.now()
    while (days.contains(date)) { streak++; date = date.minusDays(1) }
    if (streak == 0 && !days.contains(LocalDate.now())) streak = 0

    val equipmentCount = brewLogs.map { it.equipmentId }.distinct().size
    val roasts = brewLogs.map { it.roast }.distinct().size
    val earlyBirdCount = brewLogs.count {
        Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).hour < 7
    }

    return listOf(
        Achievement(
            id = "first_brew",
            titleRes = R.string.ach_first_brew_title,
            descriptionRes = R.string.ach_first_brew_desc,
            glyph = Glyph.CUP,
            isUnlocked = brewLogs.isNotEmpty(),
        ),
        Achievement(
            id = "streak_7",
            titleRes = R.string.ach_streak_7_title,
            descriptionRes = R.string.ach_streak_7_desc,
            glyph = Glyph.FLAME,
            isUnlocked = streak >= 7,
        ),
        Achievement(
            id = "streak_30",
            titleRes = R.string.ach_streak_30_title,
            descriptionRes = R.string.ach_streak_30_desc,
            glyph = Glyph.FLAME,
            isUnlocked = streak >= 30,
        ),
        Achievement(
            id = "gear_master",
            titleRes = R.string.ach_gear_master_title,
            descriptionRes = R.string.ach_gear_master_desc,
            glyph = Glyph.GEAR,
            isUnlocked = equipmentCount >= 10,
        ),
        Achievement(
            id = "bean_explorer",
            titleRes = R.string.ach_bean_explorer_title,
            descriptionRes = R.string.ach_bean_explorer_desc,
            glyph = Glyph.BEAN,
            isUnlocked = beans.size >= 5,
        ),
        Achievement(
            id = "perfect_score",
            titleRes = R.string.ach_perfect_score_title,
            descriptionRes = R.string.ach_perfect_score_desc,
            glyph = Glyph.CHECK,
            isUnlocked = brewLogs.any { it.rating == 5 },
        ),
        Achievement(
            id = "roast_explorer",
            titleRes = R.string.ach_roast_explorer_title,
            descriptionRes = R.string.ach_roast_explorer_desc,
            glyph = Glyph.PALETTE,
            isUnlocked = roasts >= 3,
        ),
        Achievement(
            id = "early_bird",
            titleRes = R.string.ach_early_bird_title,
            descriptionRes = R.string.ach_early_bird_desc,
            glyph = Glyph.TIMER,
            isUnlocked = earlyBirdCount >= 5,
        ),
        Achievement(
            id = "learn_10",
            titleRes = R.string.ach_learn_10_title,
            descriptionRes = R.string.ach_learn_10_desc,
            glyph = Glyph.BOOK,
            isUnlocked = completedChapters.size >= 10,
        ),
    )
}

@Composable
fun AchievementsContent(achievements: List<Achievement>) {
    val colors = CoffeeTheme.colors
    if (achievements.isEmpty()) return

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
    ) {
        var row = mutableListOf<Achievement>()
        achievements.forEachIndexed { i, ach ->
            row.add(ach)
            if (row.size == 2 || i == achievements.lastIndex) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    row.forEach { a ->
                        AchievementCard(achievement = a, modifier = Modifier.weight(1f))
                    }
                    if (row.size == 1) {
                        Spacer(Modifier.weight(1f))
                    }
                }
                row = mutableListOf()
            }
        }
    }
}

@Composable
private fun AchievementCard(achievement: Achievement, modifier: Modifier = Modifier) {
    val colors = CoffeeTheme.colors
    val unlocked = achievement.isUnlocked
    val iconColor = if (unlocked) colors.accent else colors.textSecondary
    val titleColor = if (unlocked) colors.textPrimary else colors.textSecondary
    val descColor = if (unlocked) colors.textSecondary else colors.textSecondary.copy(alpha = 0.4f)

    CoffeeCard(modifier = modifier) {
        LineIcon(
            glyph = achievement.glyph,
            tint = iconColor,
            modifier = Modifier.size(28.dp).padding(bottom = 4.dp),
        )
        AppText(
            text = if (unlocked) stringResource(achievement.titleRes) else "???",
            style = CoffeeTheme.type.headline,
            color = titleColor,
        )
        Spacer(Modifier.height(2.dp))
        AppText(
            text = stringResource(achievement.descriptionRes),
            style = CoffeeTheme.type.caption,
            color = descColor,
        )
    }
}
