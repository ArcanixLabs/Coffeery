package co.coffeery.app.ui.screens.root

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.Crossfade
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.coffeery.app.R
import co.coffeery.app.ui.components.AppText
import co.coffeery.app.ui.components.BottomNav
import co.coffeery.app.ui.screens.brew.BrewTimerScreen
import co.coffeery.app.ui.screens.brew.CalculatorScreen
import co.coffeery.app.ui.screens.equipment.AddEquipmentScreen
import co.coffeery.app.ui.screens.drinks.DrinkDetailScreen
import co.coffeery.app.ui.screens.drinks.DrinksScreen
import co.coffeery.app.ui.screens.equipment.EquipmentScreen
import co.coffeery.app.ui.screens.learn.LearnDetailScreen
import co.coffeery.app.ui.screens.learn.LearnScreen
import co.coffeery.app.ui.screens.log.BrewLogScreen
import co.coffeery.app.ui.screens.onboarding.OnboardingScreen
import co.coffeery.app.ui.screens.recipes.RecipesScreen
import co.coffeery.app.ui.theme.CoffeeTheme
import co.coffeery.app.ui.theme.coffeeBackground
import co.coffeery.app.ui.theme.CoffeeShapes
import kotlinx.coroutines.delay

private val routeTransition =
    slideInHorizontally { it / 4 } + fadeIn() togetherWith
        slideOutHorizontally { -it / 4 } + fadeOut()

@Composable
fun RootScreen(vm: AppViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()
    BackHandler(enabled = state.route !is Route.Tabs) { vm.back() }
    CoffeeTheme(themeMode = state.themeMode, palette = state.palette) {
    if (!state.hasCompletedOnboarding) {
        OnboardingScreen(vm)
    } else {
        val colors = CoffeeTheme.colors
        var showTip by remember { mutableStateOf(true) }
        LaunchedEffect(Unit) {
            delay(3000)
            showTip = false
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .coffeeBackground(colors),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
            ) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    AnimatedContent(
                        targetState = state.route,
                        transitionSpec = { routeTransition },
                        label = "route",
                        contentKey = { it::class },
                    ) { route ->
                        when (route) {
                            is Route.Timer -> BrewTimerScreen(state, vm)
                            is Route.AddEquipment -> AddEquipmentScreen(vm)
                            is Route.LearnDetail -> LearnDetailScreen(route.cardIndex, vm)
                            is Route.DrinkDetail -> DrinkDetailScreen(route.index, vm)
                            is Route.Settings -> SettingsScreen(vm)
                            is Route.Tabs -> TabContent(state, vm)
                        }
                    }
                }
                if (state.route is Route.Tabs) {
                    BottomNav(
                        items = NavTab.entries.toList(),
                        selected = state.tab,
                        labelFor = { stringResource(it.labelRes) },
                        glyphFor = { it.glyph },
                        onSelect = { vm.selectTab(it) },
                    )
                }
            }
            AnimatedVisibility(
                visible = showTip,
                enter = fadeIn() + slideInHorizontally { it / 2 },
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 80.dp),
            ) {
                Box(
                    modifier = Modifier
                        .clip(CoffeeShapes.pill)
                        .background(colors.surfaceElevated)
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                ) {
                    AppText(
                        stringResource(R.string.tap_brew_to_start),
                        style = CoffeeTheme.type.headline,
                        color = colors.accent,
                        align = TextAlign.Center,
                    )
                }
            }
        }
    }
    }
}

@Composable
private fun TabContent(state: AppUiState, vm: AppViewModel) {
    Crossfade(
        targetState = state.tab,
        label = "tab",
    ) { tab ->
        when (tab) {
            NavTab.BREW -> CalculatorScreen(state, vm)
            NavTab.GEAR -> EquipmentScreen(state, vm)
            NavTab.RECIPES -> RecipesScreen(state, vm)
            NavTab.LOG -> BrewLogScreen(vm)
            NavTab.DRINKS -> DrinksScreen(vm)
            NavTab.LEARN -> LearnScreen(vm)
        }
    }
}
