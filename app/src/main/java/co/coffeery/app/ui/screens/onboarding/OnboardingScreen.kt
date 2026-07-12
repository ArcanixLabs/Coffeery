package co.coffeery.app.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.coffeery.app.R
import co.coffeery.app.ui.components.AppText
import co.coffeery.app.ui.components.CoffeeCard
import co.coffeery.app.ui.components.Glyph
import co.coffeery.app.ui.components.LineIcon
import co.coffeery.app.ui.components.PrimaryButton
import co.coffeery.app.ui.components.SecondaryButton
import co.coffeery.app.ui.screens.root.AppViewModel
import co.coffeery.app.ui.theme.CoffeeTheme

private data class Step2Item(val glyph: Glyph, val labelRes: Int)

private val step2Items = listOf(
    Step2Item(Glyph.CONE, R.string.onb_methods),
    Step2Item(Glyph.BOOK, R.string.onb_learn),
    Step2Item(Glyph.TIMER, R.string.onb_journal),
    Step2Item(Glyph.BEAN, R.string.onb_beans),
)

@Composable
fun OnboardingScreen(vm: AppViewModel) {
    val colors = CoffeeTheme.colors
    var page by remember { mutableIntStateOf(0) }
    val safePage = page.coerceIn(0, 2)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding(),
    ) {
        Spacer(Modifier.height(20.dp))

        AnimatedContent(
            targetState = safePage,
            transitionSpec = {
                val direction = if (targetState > initialState) 1 else -1
                (slideInHorizontally(tween(300)) { direction * it / 2 } + fadeIn(tween(300)))
                    .togetherWith(slideOutHorizontally(tween(300)) { -direction * it / 2 } + fadeOut(tween(200)))
            },
            label = "onboardSlide",
            modifier = Modifier.weight(1f).fillMaxWidth(),
        ) { step ->
            when (step) {
                0 -> WelcomeStep()
                1 -> InsideStep()
                2 -> ReadyStep()
            }
        }

        Spacer(Modifier.height(12.dp))

        StepIndicators(current = safePage, total = 3, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SecondaryButton(text = stringResource(R.string.onb_skip)) { vm.completeOnboarding() }
            Spacer(Modifier.weight(1f))

            if (safePage == 2) {
                PrimaryButton(text = stringResource(R.string.onb_start)) { vm.completeOnboarding() }
            } else {
                PrimaryButton(text = stringResource(R.string.onb_next)) {
                    page = (safePage + 1).coerceAtMost(2)
                }
            }
        }
    }
}

@Composable
private fun StepIndicators(current: Int, total: Int, modifier: Modifier = Modifier) {
    val colors = CoffeeTheme.colors
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
    ) {
        repeat(total) { i ->
            val dotColor by animateColorAsState(
                targetValue = if (i == current) colors.accent else colors.outline,
                animationSpec = tween(300),
            )
            val isActive = i == current
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(if (isActive) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .then(
                        if (isActive) Modifier.background(dotColor)
                        else Modifier.border(1.5.dp, dotColor, CircleShape)
                    ),
            )
        }
    }
}

@Composable
private fun WelcomeStep() {
    val colors = CoffeeTheme.colors
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
    ) {
        Spacer(Modifier.weight(0.6f))
        LineIcon(Glyph.CUP, colors.accent, Modifier.size(64.dp))
        Spacer(Modifier.height(40.dp))
        AppText(
            stringResource(R.string.onb_welcome_title),
            style = CoffeeTheme.type.display,
            color = colors.textPrimary,
            align = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(20.dp))
        AppText(
            stringResource(R.string.onb_welcome_body),
            style = CoffeeTheme.type.body,
            color = colors.textSecondary,
            align = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun InsideStep() {
    val colors = CoffeeTheme.colors
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
    ) {
        Spacer(Modifier.weight(0.3f))
        AppText(
            stringResource(R.string.onb_inside_title),
            style = CoffeeTheme.type.display,
            color = colors.textPrimary,
            align = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(32.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                InsideCard(step2Items[0])
                InsideCard(step2Items[2])
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                InsideCard(step2Items[1])
                InsideCard(step2Items[3])
            }
        }
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun InsideCard(item: Step2Item) {
    val colors = CoffeeTheme.colors
    CoffeeCard {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(8.dp),
        ) {
            LineIcon(item.glyph, colors.accent, Modifier.size(32.dp))
            Spacer(Modifier.height(10.dp))
            AppText(
                stringResource(item.labelRes),
                style = CoffeeTheme.type.label,
                color = colors.textPrimary,
                align = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ReadyStep() {
    val colors = CoffeeTheme.colors
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
    ) {
        Spacer(Modifier.weight(0.6f))
        LineIcon(Glyph.CUP, colors.accent, Modifier.size(64.dp))
        Spacer(Modifier.height(40.dp))
        AppText(
            stringResource(R.string.onb_ready_title),
            style = CoffeeTheme.type.display,
            color = colors.textPrimary,
            align = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(20.dp))
        AppText(
            stringResource(R.string.onb_ready_body),
            style = CoffeeTheme.type.body,
            color = colors.textSecondary,
            align = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.weight(1f))
    }
}
