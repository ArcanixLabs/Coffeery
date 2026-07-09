package co.coffeery.app.ui.screens.onboarding

import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import co.coffeery.app.R
import co.coffeery.app.ui.components.AppText
import co.coffeery.app.ui.components.LineIcon
import co.coffeery.app.ui.components.Glyph
import co.coffeery.app.ui.components.PrimaryButton
import co.coffeery.app.ui.components.SecondaryButton
import co.coffeery.app.ui.screens.root.AppViewModel
import co.coffeery.app.ui.theme.CoffeeTheme
import androidx.compose.foundation.layout.statusBarsPadding

data class OnboardingSlide(
    val glyph: Glyph,
    val titleRes: Int,
    val bodyRes: Int,
)

private val slides = listOf(
    OnboardingSlide(Glyph.DROP, R.string.onboard1_title, R.string.onboard1_body),
    OnboardingSlide(Glyph.TIMER, R.string.onboard2_title, R.string.onboard2_body),
    OnboardingSlide(Glyph.BOOK, R.string.onboard3_title, R.string.onboard3_body),
    OnboardingSlide(Glyph.CUP, R.string.onboard4_title, R.string.onboard4_body),
)

@Composable
fun OnboardingScreen(vm: AppViewModel) {
    val colors = CoffeeTheme.colors
    val pagerState = rememberPagerState(pageCount = { slides.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 32.dp, vertical = 48.dp)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            SecondaryButton(
                text = stringResource(R.string.onboard_skip),
                modifier = Modifier,
            ) { vm.completeOnboarding() }
        }

        Spacer(Modifier.height(40.dp))

        HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
            val slide = slides[page]
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            ) {
                LineIcon(slide.glyph, colors.accent, Modifier.size(64.dp))
                Spacer(Modifier.height(32.dp))
                AppText(
                    stringResource(slide.titleRes),
                    style = CoffeeTheme.type.display,
                    color = colors.textPrimary,
                    align = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(16.dp))
                AppText(
                    stringResource(slide.bodyRes),
                    style = CoffeeTheme.type.body,
                    color = colors.textSecondary,
                    align = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Dots indicator
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(slides.size) { i ->
                Box(
                    modifier = Modifier
                        .size(if (i == pagerState.currentPage) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(if (i == pagerState.currentPage) colors.accent else colors.outline),
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        PrimaryButton(
            text = if (pagerState.currentPage == slides.lastIndex)
                stringResource(R.string.onboard_start)
            else
                stringResource(R.string.onboard_next),
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (pagerState.currentPage == slides.lastIndex) {
                vm.completeOnboarding()
            } else {
                scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
            }
        }
    }
}
