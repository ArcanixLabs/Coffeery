package co.coffeery.app.ui.screens.drinks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import co.coffeery.app.R
import co.coffeery.app.ui.components.AppText
import co.coffeery.app.ui.components.CoffeeCard
import co.coffeery.app.ui.components.ScreenHeader
import co.coffeery.app.ui.screens.root.AppViewModel
import co.coffeery.app.ui.screens.root.Route
import co.coffeery.app.ui.theme.CoffeeTheme

@Composable
fun DrinksScreen(vm: AppViewModel) {
    val colors = CoffeeTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        ScreenHeader(
            title = stringResource(R.string.drinks_title),
            subtitle = stringResource(R.string.drinks_intro),
        )

        var lastGroup: DrinkGroup? = null
        DrinkContent.drinks.forEachIndexed { index, drink ->
            if (drink.group != lastGroup) {
                lastGroup = drink.group
                Spacer(Modifier.height(2.dp))
                AppText(
                    stringResource(drink.group.labelRes),
                    style = CoffeeTheme.type.label,
                    color = colors.accent,
                )
            }
            CoffeeCard(onClick = { vm.openRoute(Route.DrinkDetail(index)) }, modifier = Modifier.fillMaxWidth()) {
                AppText(stringResource(drink.nameRes), style = CoffeeTheme.type.headline)
                Spacer(Modifier.height(6.dp))
                AppText(
                    stringResource(drink.summaryRes),
                    style = CoffeeTheme.type.body,
                    color = colors.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(8.dp))
                AppText(stringResource(R.string.drink_read_more), style = CoffeeTheme.type.label, color = colors.accent)
            }
        }
    }
}
