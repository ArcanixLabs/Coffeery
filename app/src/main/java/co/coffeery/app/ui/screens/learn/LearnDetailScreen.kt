package co.coffeery.app.ui.screens.learn

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.coffeery.app.R
import co.coffeery.app.ui.components.AppText
import co.coffeery.app.ui.components.CremaMascot
import co.coffeery.app.ui.components.PrimaryButton
import co.coffeery.app.ui.components.ScreenHeader
import co.coffeery.app.ui.screens.root.AppViewModel
import co.coffeery.app.ui.theme.CoffeeTheme

@Composable
fun LearnDetailScreen(cardIndex: Int, vm: AppViewModel) {
    val card = LearnContent.cards.getOrNull(cardIndex)
    if (card == null) {
        NotFoundScreen(message = stringResource(R.string.search_no_results), onBack = { vm.back() })
        return
    }
    LaunchedEffect(cardIndex) { vm.markLearnCardRead(card.chapterRes) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ScreenHeader(title = "", onBack = { vm.back() })
        Spacer(Modifier.height(4.dp))
        AppText(stringResource(card.titleRes), style = CoffeeTheme.type.display)
        Spacer(Modifier.height(8.dp))
        AppText(stringResource(card.bodyRes), style = CoffeeTheme.type.body, color = CoffeeTheme.colors.textSecondary)
    }
}

@Composable
private fun NotFoundScreen(message: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp).padding(top = 12.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ScreenHeader(title = stringResource(R.string.search_no_results), onBack = onBack)
        Spacer(Modifier.height(40.dp))
        CremaMascot(mood = "curious", modifier = Modifier.size(120.dp))
        Spacer(Modifier.height(16.dp))
        AppText(stringResource(R.string.search_no_results), style = CoffeeTheme.type.title, color = CoffeeTheme.colors.textPrimary, align = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        AppText(message, style = CoffeeTheme.type.body, color = CoffeeTheme.colors.textSecondary, align = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(20.dp))
        PrimaryButton(text = stringResource(R.string.action_done), modifier = Modifier.fillMaxWidth()) { onBack() }
    }
}
