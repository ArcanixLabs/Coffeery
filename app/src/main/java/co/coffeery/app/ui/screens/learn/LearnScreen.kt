package co.coffeery.app.ui.screens.learn

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import co.coffeery.app.R
import co.coffeery.app.ui.components.AppText
import co.coffeery.app.ui.components.Chip
import co.coffeery.app.ui.components.CoffeeCard
import co.coffeery.app.ui.components.AppTextField
import co.coffeery.app.ui.components.ScreenHeader
import co.coffeery.app.ui.components.LineIcon
import co.coffeery.app.ui.components.Glyph
import co.coffeery.app.ui.screens.root.AppViewModel
import co.coffeery.app.ui.screens.root.Route
import co.coffeery.app.ui.theme.CoffeeTheme

@Composable
fun LearnScreen(vm: AppViewModel) {
    val colors = CoffeeTheme.colors
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    var activeChapterRes by remember { mutableStateOf(LearnContent.chapterOrder[0]) }
    var searchQuery by remember { mutableStateOf("") }
    val state by vm.state.collectAsState()
    val completedChapters = state.completedChapters

    val cardTexts = LearnContent.cards.map { card -> card to (stringResource(card.titleRes) + " " + stringResource(card.bodyRes)) }
    val filteredCards = if (searchQuery.isBlank()) {
        LearnContent.cards
    } else {
        cardTexts.filter { (_, text) -> text.contains(searchQuery, true) }.map { it.first }
    }
    val searchActive = searchQuery.isNotBlank()

    LaunchedEffect(Unit) {
        scrollState.scrollTo(state.learnScrollOffset)
    }

    DisposableEffect(Unit) {
        onDispose { vm.setLearnScrollOffset(scrollState.value) }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        ScreenHeader(
            title = stringResource(R.string.learn_title),
            subtitle = stringResource(R.string.learn_intro),
        )

        TodaysLessonCard(vm)

        QuickQuizCard()

        Box(modifier = Modifier.fillMaxWidth()) {
            AppTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                hint = stringResource(R.string.search_hint_learn),
                modifier = Modifier.fillMaxWidth(),
            )
            if (searchQuery.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 12.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                        ) { searchQuery = "" },
                ) {
                    AppText("✕", style = CoffeeTheme.type.headline, color = colors.textSecondary)
                }
            }
        }

        StepMap(
            activeChapterRes = activeChapterRes,
            completedChapters = completedChapters,
            onChapterSelected = { idx ->
                val ch = LearnContent.chapterOrder[idx]
                activeChapterRes = ch
                val first = LearnContent.cards.indexOfFirst { it.chapterRes == ch }
                val offsetDp = 600.dp + 170.dp * first
                scope.launch {
                    scrollState.scrollTo(with(density) { offsetDp.toPx() }.toInt())
                }
            },
        )

        TroubleshootCard()

        ProTipsCard()

        QuickRatioCard()

        GrindSizeCard()

        BrewTroubleshooterCard()

        FlavorWheelCard()

        ExtractionCalculatorCard()

        WaterMineralCard()

        GlossaryCard()

        FoodPairingCard()

        CultureFactsCard()

        if (searchActive && filteredCards.isEmpty()) {
            AppText(
                stringResource(R.string.search_no_results),
                style = CoffeeTheme.type.body,
                color = colors.textSecondary,
                modifier = Modifier.fillMaxWidth(),
                align = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }

        // Render a chapter header whenever the chapter changes, keeping the
        // card's global index stable for detail navigation.
        var lastChapter = 0
        filteredCards.forEachIndexed { index, card ->
            if (card.chapterRes != lastChapter) {
                lastChapter = card.chapterRes
                Spacer(Modifier.height(2.dp))
                AppText(
                    stringResource(card.chapterRes),
                    style = CoffeeTheme.type.label,
                    color = colors.accent,
                )
            }
            CoffeeCard(onClick = { vm.openRoute(Route.LearnDetail(index)) }, modifier = Modifier.fillMaxWidth()) {
                AppText(stringResource(card.titleRes), style = CoffeeTheme.type.headline)
                Spacer(Modifier.height(6.dp))
                AppText(
                    stringResource(card.bodyRes),
                    style = CoffeeTheme.type.body,
                    color = colors.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AppText(stringResource(R.string.learn_read_more), style = CoffeeTheme.type.label, color = colors.accent)
                    if (completedChapters.contains(card.chapterRes)) {
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(colors.accentSoft),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StepMap(
    activeChapterRes: Int,
    completedChapters: Set<Int>,
    onChapterSelected: (Int) -> Unit,
) {
    val colors = CoffeeTheme.colors
    val chapters = LearnContent.chapterOrder
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        chapters.forEachIndexed { index, chapterRes ->
            val isCompleted = chapterRes in completedChapters
            val isUnlocked = index == 0 || chapters[index - 1] in completedChapters
            val isLocked = !isCompleted && !isUnlocked
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(if (isCompleted) colors.accent else Color.Transparent)
                        .border(
                            2.dp,
                            when {
                                isCompleted -> colors.accent
                                isLocked -> colors.outline
                                else -> colors.accent
                            },
                            CircleShape,
                        )
                        .then(
                            if (isLocked) Modifier
                            else Modifier.clickable { onChapterSelected(index) },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    if (isCompleted) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val checkPath = Path().apply {
                                moveTo(size.width * 0.22f, size.height * 0.52f)
                                lineTo(size.width * 0.4f, size.height * 0.72f)
                                lineTo(size.width * 0.78f, size.height * 0.28f)
                            }
                            drawPath(
                                checkPath,
                                colors.onAccent,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(
                                    width = size.minDimension * 0.13f,
                                    cap = androidx.compose.ui.graphics.StrokeCap.Round,
                                    join = androidx.compose.ui.graphics.StrokeJoin.Round,
                                ),
                            )
                        }
                    } else {
                        AppText(
                            text = "${index + 1}",
                            style = CoffeeTheme.type.caption,
                            color = if (isLocked) colors.textSecondary else colors.accent,
                        )
                    }
                }
                if (index < chapters.size - 1) {
                    Spacer(Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .width(16.dp)
                            .height(2.dp)
                            .background(
                                if (isCompleted || chapters.getOrNull(index + 1) in completedChapters) colors.accent
                                else colors.outline,
                            ),
                    )
                    Spacer(Modifier.width(4.dp))
                }
            }
        }
    }
}

@Composable
private fun TroubleshootCard() {
    val colors = CoffeeTheme.colors
    var selected by remember { mutableStateOf<Int?>(null) }
    CoffeeCard(modifier = Modifier.fillMaxWidth()) {
        AppText(stringResource(R.string.learn_troubleshoot_title), style = CoffeeTheme.type.title)
        Spacer(Modifier.height(4.dp))
        AppText(stringResource(R.string.learn_troubleshoot_intro), style = CoffeeTheme.type.caption, color = colors.textSecondary)
        Spacer(Modifier.height(12.dp))
        // Simple wrapping rows of chips (4 per row).
        LearnContent.tasteOptions.withIndex().chunked(4).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 8.dp)) {
                rowItems.forEach { (index, option) ->
                    val isSel = selected == index
                    Chip(
                        text = stringResource(option.labelRes),
                        background = if (isSel) colors.accent else colors.accentSoft,
                        textColor = if (isSel) colors.onAccent else colors.accent,
                        modifier = Modifier.clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                        ) { selected = if (isSel) null else index },
                    )
                }
            }
        }
        val sel = selected
        if (sel != null) {
            AppText(stringResource(LearnContent.tasteOptions[sel].adviceRes), style = CoffeeTheme.type.body)
        }
    }
}

@Composable
private fun ProTipsCard() {
    val colors = CoffeeTheme.colors
    val tips = listOf(
        R.string.pro_tip_1, R.string.pro_tip_2, R.string.pro_tip_3, R.string.pro_tip_4,
        R.string.pro_tip_5, R.string.pro_tip_6, R.string.pro_tip_7, R.string.pro_tip_8,
        R.string.pro_tip_9, R.string.pro_tip_10, R.string.pro_tip_11, R.string.pro_tip_12,
        R.string.pro_tip_13, R.string.pro_tip_14, R.string.pro_tip_15, R.string.pro_tip_16,
        R.string.pro_tip_17, R.string.pro_tip_18, R.string.pro_tip_19, R.string.pro_tip_20,
        R.string.pro_tip_21, R.string.pro_tip_22, R.string.pro_tip_23, R.string.pro_tip_24,
        R.string.pro_tip_25, R.string.pro_tip_26, R.string.pro_tip_27, R.string.pro_tip_28,
        R.string.pro_tip_29, R.string.pro_tip_30, R.string.pro_tip_31, R.string.pro_tip_32,
        R.string.pro_tip_33, R.string.pro_tip_34, R.string.pro_tip_35, R.string.pro_tip_36,
        R.string.pro_tip_37, R.string.pro_tip_38, R.string.pro_tip_39, R.string.pro_tip_40,
        R.string.pro_tip_41, R.string.pro_tip_42, R.string.pro_tip_43, R.string.pro_tip_44,
        R.string.pro_tip_45, R.string.pro_tip_46, R.string.pro_tip_47, R.string.pro_tip_48,
        R.string.pro_tip_49, R.string.pro_tip_50,
    )
    var current by remember { mutableStateOf(kotlin.random.Random.nextInt(tips.size)) }
    CoffeeCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            LineIcon(Glyph.CUP, colors.accent, Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            AppText(stringResource(R.string.pro_tips_title), style = CoffeeTheme.type.title)
        }
        Spacer(Modifier.height(8.dp))
        AppText(stringResource(tips[current]), style = CoffeeTheme.type.body, color = colors.textSecondary)
        Spacer(Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            AppText(
                stringResource(R.string.pro_tips_next),
                style = CoffeeTheme.type.label,
                color = colors.accent,
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ) { current = (current + 1) % tips.size },
            )
        }
    }
}

@Composable
private fun QuickRatioCard() {
    val colors = CoffeeTheme.colors
    CoffeeCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            LineIcon(Glyph.CUP, colors.accent, Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            AppText(stringResource(R.string.ratio_ref_title), style = CoffeeTheme.type.title)
        }
        Spacer(Modifier.height(8.dp))
        RatioRow(R.string.ratio_1_15)
        Spacer(Modifier.height(4.dp))
        RatioRow(R.string.ratio_1_16)
        Spacer(Modifier.height(4.dp))
        RatioRow(R.string.ratio_1_17)
        Spacer(Modifier.height(4.dp))
        RatioRow(R.string.ratio_1_18)
    }
}

@Composable
private fun RatioRow(textRes: Int) {
    val colors = CoffeeTheme.colors
    Row(verticalAlignment = Alignment.CenterVertically) {
        LineIcon(Glyph.CUP, colors.accent, Modifier.size(14.dp))
        Spacer(Modifier.width(6.dp))
        AppText(stringResource(textRes), style = CoffeeTheme.type.body, color = colors.textSecondary)
    }
}

@Composable
private fun GrindSizeCard() {
    val colors = CoffeeTheme.colors
    var selected by remember { mutableStateOf(-1) }
    data class GrindLevel(val nameRes: Int, val descRes: Int, val forRes: Int)
    val grinds = listOf(
        GrindLevel(R.string.grind_vis_name_extra_coarse, R.string.grind_vis_desc_extra_coarse, R.string.grind_vis_for_extra_coarse),
        GrindLevel(R.string.grind_vis_name_coarse, R.string.grind_vis_desc_coarse, R.string.grind_vis_for_coarse),
        GrindLevel(R.string.grind_vis_name_med_coarse, R.string.grind_vis_desc_med_coarse, R.string.grind_vis_for_med_coarse),
        GrindLevel(R.string.grind_vis_name_medium, R.string.grind_vis_desc_medium, R.string.grind_vis_for_medium),
        GrindLevel(R.string.grind_vis_name_med_fine, R.string.grind_vis_desc_med_fine, R.string.grind_vis_for_med_fine),
        GrindLevel(R.string.grind_vis_name_fine, R.string.grind_vis_desc_fine, R.string.grind_vis_for_fine),
        GrindLevel(R.string.grind_vis_name_extra_fine, R.string.grind_vis_desc_extra_fine, R.string.grind_vis_for_extra_fine),
    )
    CoffeeCard(modifier = Modifier.fillMaxWidth()) {
        AppText(stringResource(R.string.grind_vis_title), style = CoffeeTheme.type.title)
        Spacer(Modifier.height(8.dp))
        grinds.forEachIndexed { index, grind ->
            val fraction = index / (grinds.size - 1).toFloat()
            val barColor = lerp(colors.cremaLight, colors.cremaDark, fraction)
            val isSel = selected == index
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                        ) { selected = if (isSel) -1 else index },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .height(24.dp)
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(barColor)
                            .then(
                                if (isSel) Modifier.border(2.dp, colors.accent, RoundedCornerShape(6.dp))
                                else Modifier
                            ),
                    )
                    Spacer(Modifier.width(10.dp))
                    AppText(stringResource(grind.nameRes), style = CoffeeTheme.type.label)
                }
                if (isSel) {
                    Spacer(Modifier.height(4.dp))
                    AppText(stringResource(grind.descRes), style = CoffeeTheme.type.caption, color = colors.textSecondary)
                    AppText(stringResource(grind.forRes), style = CoffeeTheme.type.caption, color = colors.accent)
                }
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun BrewTroubleshooterCard() {
    val colors = CoffeeTheme.colors
    var selected by remember { mutableStateOf<Int?>(null) }
    val issues = listOf(
        R.string.brew_issue_sour to R.string.brew_issue_sour_advice,
        R.string.brew_issue_bitter to R.string.brew_issue_bitter_advice,
        R.string.brew_issue_weak to R.string.brew_issue_weak_advice,
        R.string.brew_issue_dry to R.string.brew_issue_dry_advice,
        R.string.brew_issue_stalling to R.string.brew_issue_stalling_advice,
        R.string.brew_issue_channeling to R.string.brew_issue_channeling_advice,
        R.string.brew_issue_muddy to R.string.brew_issue_muddy_advice,
        R.string.brew_issue_flat to R.string.brew_issue_flat_advice,
        R.string.brew_issue_burnt to R.string.brew_issue_burnt_advice,
        R.string.brew_issue_metallic to R.string.brew_issue_metallic_advice,
        R.string.brew_issue_grassy to R.string.brew_issue_grassy_advice,
        R.string.brew_issue_salty to R.string.brew_issue_salty_advice,
        R.string.brew_issue_no_crema to R.string.brew_issue_no_crema_advice,
        R.string.brew_issue_gusher to R.string.brew_issue_gusher_advice,
        R.string.brew_issue_fines_mud to R.string.brew_issue_fines_mud_advice,
        R.string.brew_issue_clogged_filter to R.string.brew_issue_clogged_filter_advice,
        R.string.brew_issue_temp_loss to R.string.brew_issue_temp_loss_advice,
        R.string.brew_issue_grind_inconsistent to R.string.brew_issue_grind_inconsistent_advice,
    )
    CoffeeCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            LineIcon(Glyph.GEAR, colors.accent, Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            AppText(stringResource(R.string.brew_troubleshoot_title), style = CoffeeTheme.type.title)
        }
        Spacer(Modifier.height(4.dp))
        AppText(stringResource(R.string.brew_troubleshoot_question), style = CoffeeTheme.type.caption, color = colors.textSecondary)
        Spacer(Modifier.height(12.dp))
        issues.withIndex().chunked(2).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 8.dp)) {
                rowItems.forEach { (index, issue) ->
                    val isSel = selected == index
                    Chip(
                        text = stringResource(issue.first),
                        background = if (isSel) colors.accent else colors.accentSoft,
                        textColor = if (isSel) colors.onAccent else colors.accent,
                        modifier = Modifier.clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                        ) { selected = if (isSel) null else index },
                    )
                }
            }
        }
        selected?.let { sel ->
            AppText(stringResource(issues[sel].second), style = CoffeeTheme.type.body)
        }
    }
}

private data class GlossaryTerm(val termRes: Int, val defRes: Int)

private val GlossaryTerms = listOf(
    GlossaryTerm(R.string.glossary_term_1, R.string.glossary_def_1),
    GlossaryTerm(R.string.glossary_term_2, R.string.glossary_def_2),
    GlossaryTerm(R.string.glossary_term_3, R.string.glossary_def_3),
    GlossaryTerm(R.string.glossary_term_4, R.string.glossary_def_4),
    GlossaryTerm(R.string.glossary_term_5, R.string.glossary_def_5),
    GlossaryTerm(R.string.glossary_term_6, R.string.glossary_def_6),
    GlossaryTerm(R.string.glossary_term_7, R.string.glossary_def_7),
    GlossaryTerm(R.string.glossary_term_8, R.string.glossary_def_8),
    GlossaryTerm(R.string.glossary_term_9, R.string.glossary_def_9),
    GlossaryTerm(R.string.glossary_term_10, R.string.glossary_def_10),
    GlossaryTerm(R.string.glossary_term_11, R.string.glossary_def_11),
    GlossaryTerm(R.string.glossary_term_12, R.string.glossary_def_12),
    GlossaryTerm(R.string.glossary_term_13, R.string.glossary_def_13),
    GlossaryTerm(R.string.glossary_term_14, R.string.glossary_def_14),
    GlossaryTerm(R.string.glossary_term_15, R.string.glossary_def_15),
    GlossaryTerm(R.string.glossary_term_16, R.string.glossary_def_16),
    GlossaryTerm(R.string.glossary_term_17, R.string.glossary_def_17),
    GlossaryTerm(R.string.glossary_term_18, R.string.glossary_def_18),
    GlossaryTerm(R.string.glossary_term_19, R.string.glossary_def_19),
    GlossaryTerm(R.string.glossary_term_20, R.string.glossary_def_20),
    GlossaryTerm(R.string.glossary_term_21, R.string.glossary_def_21),
    GlossaryTerm(R.string.glossary_term_22, R.string.glossary_def_22),
    GlossaryTerm(R.string.glossary_term_23, R.string.glossary_def_23),
    GlossaryTerm(R.string.glossary_term_24, R.string.glossary_def_24),
    GlossaryTerm(R.string.glossary_term_25, R.string.glossary_def_25),
    GlossaryTerm(R.string.glossary_term_26, R.string.glossary_def_26),
    GlossaryTerm(R.string.glossary_term_27, R.string.glossary_def_27),
    GlossaryTerm(R.string.glossary_term_28, R.string.glossary_def_28),
    GlossaryTerm(R.string.glossary_term_29, R.string.glossary_def_29),
    GlossaryTerm(R.string.glossary_term_30, R.string.glossary_def_30),
    GlossaryTerm(R.string.glossary_term_31, R.string.glossary_def_31),
    GlossaryTerm(R.string.glossary_term_32, R.string.glossary_def_32),
    GlossaryTerm(R.string.glossary_term_33, R.string.glossary_def_33),
    GlossaryTerm(R.string.glossary_term_34, R.string.glossary_def_34),
    GlossaryTerm(R.string.glossary_term_35, R.string.glossary_def_35),
    GlossaryTerm(R.string.glossary_term_36, R.string.glossary_def_36),
    GlossaryTerm(R.string.glossary_term_37, R.string.glossary_def_37),
    GlossaryTerm(R.string.glossary_term_38, R.string.glossary_def_38),
    GlossaryTerm(R.string.glossary_term_39, R.string.glossary_def_39),
    GlossaryTerm(R.string.glossary_term_40, R.string.glossary_def_40),
    GlossaryTerm(R.string.glossary_term_41, R.string.glossary_def_41),
    GlossaryTerm(R.string.glossary_term_42, R.string.glossary_def_42),
    GlossaryTerm(R.string.glossary_term_43, R.string.glossary_def_43),
    GlossaryTerm(R.string.glossary_term_44, R.string.glossary_def_44),
    GlossaryTerm(R.string.glossary_term_45, R.string.glossary_def_45),
    GlossaryTerm(R.string.glossary_term_46, R.string.glossary_def_46),
    GlossaryTerm(R.string.glossary_term_47, R.string.glossary_def_47),
    GlossaryTerm(R.string.glossary_term_48, R.string.glossary_def_48),
    GlossaryTerm(R.string.glossary_term_49, R.string.glossary_def_49),
    GlossaryTerm(R.string.glossary_term_50, R.string.glossary_def_50),
    GlossaryTerm(R.string.glossary_term_51, R.string.glossary_def_51),
    GlossaryTerm(R.string.glossary_term_52, R.string.glossary_def_52),
    GlossaryTerm(R.string.glossary_term_53, R.string.glossary_def_53),
    GlossaryTerm(R.string.glossary_term_54, R.string.glossary_def_54),
    GlossaryTerm(R.string.glossary_term_55, R.string.glossary_def_55),
    GlossaryTerm(R.string.glossary_term_56, R.string.glossary_def_56),
    GlossaryTerm(R.string.glossary_term_57, R.string.glossary_def_57),
    GlossaryTerm(R.string.glossary_term_58, R.string.glossary_def_58),
    GlossaryTerm(R.string.glossary_term_59, R.string.glossary_def_59),
    GlossaryTerm(R.string.glossary_term_60, R.string.glossary_def_60),
    GlossaryTerm(R.string.glossary_term_61, R.string.glossary_def_61),
    GlossaryTerm(R.string.glossary_term_62, R.string.glossary_def_62),
    GlossaryTerm(R.string.glossary_term_63, R.string.glossary_def_63),
    GlossaryTerm(R.string.glossary_term_64, R.string.glossary_def_64),
    GlossaryTerm(R.string.glossary_term_65, R.string.glossary_def_65),
)

private data class FlavorCategory(val labelRes: Int, val notes: List<Int>)

private val FlavorWheelData = listOf(
    FlavorCategory(R.string.flavor_fruity, listOf(R.string.flavor_berry, R.string.flavor_citrus, R.string.flavor_stone_fruit, R.string.flavor_tropical, R.string.flavor_tropical_fruit, R.string.flavor_red_berry)),
    FlavorCategory(R.string.flavor_floral, listOf(R.string.flavor_jasmine, R.string.flavor_rose, R.string.flavor_chamomile, R.string.flavor_lavender)),
    FlavorCategory(R.string.flavor_sweet, listOf(R.string.flavor_chocolate, R.string.flavor_caramel, R.string.flavor_honey, R.string.flavor_brown_sugar, R.string.flavor_caramelized, R.string.flavor_maple)),
    FlavorCategory(R.string.flavor_nutty_spice, listOf(R.string.flavor_almond, R.string.flavor_cinnamon, R.string.flavor_clove, R.string.flavor_nutmeg, R.string.flavor_hazelnut, R.string.flavor_milk_chocolate, R.string.flavor_dark_cocoa)),
    FlavorCategory(R.string.flavor_earthy, listOf(R.string.flavor_woody, R.string.flavor_tobacco, R.string.flavor_leather, R.string.flavor_mushroom)),
)

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun FlavorWheelCard() {
    val colors = CoffeeTheme.colors
    CoffeeCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            LineIcon(Glyph.PALETTE, colors.accent, Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            AppText(stringResource(R.string.flavor_wheel_title), style = CoffeeTheme.type.title)
        }
        Spacer(Modifier.height(8.dp))
        FlavorWheelData.forEach { category ->
            Spacer(Modifier.height(8.dp))
            AppText(stringResource(category.labelRes), style = CoffeeTheme.type.headline, color = colors.accent)
            Spacer(Modifier.height(4.dp))
            category.notes.chunked(4).forEach { rowItems ->
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(bottom = 6.dp)) {
                    rowItems.forEach { noteRes ->
                        Chip(
                            text = stringResource(noteRes),
                            background = colors.accentSoft,
                            textColor = colors.textPrimary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GlossaryCard() {
    val colors = CoffeeTheme.colors
    CoffeeCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            LineIcon(Glyph.BOOK, colors.accent, Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            AppText(stringResource(R.string.glossary_title), style = CoffeeTheme.type.title)
        }
        Spacer(Modifier.height(8.dp))
        GlossaryTerms.forEach { term ->
            AppText(stringResource(term.termRes), style = CoffeeTheme.type.headline)
            Spacer(Modifier.height(2.dp))
            AppText(
                stringResource(term.defRes),
                style = CoffeeTheme.type.caption,
                color = colors.textSecondary,
            )
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun FoodPairingCard() {
    val colors = CoffeeTheme.colors
    val pairings = listOf(
        Triple(R.string.pairing_1_coffee, R.string.pairing_1_food, R.string.pairing_1_why),
        Triple(R.string.pairing_2_coffee, R.string.pairing_2_food, R.string.pairing_2_why),
        Triple(R.string.pairing_3_coffee, R.string.pairing_3_food, R.string.pairing_3_why),
        Triple(R.string.pairing_4_coffee, R.string.pairing_4_food, R.string.pairing_4_why),
        Triple(R.string.pairing_5_coffee, R.string.pairing_5_food, R.string.pairing_5_why),
    )
    CoffeeCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            LineIcon(Glyph.BEAN, colors.accent, Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            AppText(stringResource(R.string.pairing_title), style = CoffeeTheme.type.title)
        }
        Spacer(Modifier.height(8.dp))
        pairings.forEach { (coffee, food, why) ->
            AppText(stringResource(coffee), style = CoffeeTheme.type.headline)
            AppText("+ ${stringResource(food)}", style = CoffeeTheme.type.body, color = colors.accent)
            AppText(stringResource(why), style = CoffeeTheme.type.caption, color = colors.textSecondary)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun CultureFactsCard() {
    val colors = CoffeeTheme.colors
    val facts = listOf(
        R.string.culture_fact_1,
        R.string.culture_fact_2,
        R.string.culture_fact_3,
        R.string.culture_fact_4,
    )
    CoffeeCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            LineIcon(Glyph.BOOK, colors.accent, Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            AppText(stringResource(R.string.culture_title), style = CoffeeTheme.type.title)
        }
        Spacer(Modifier.height(8.dp))
        facts.forEach { fact ->
            AppText(stringResource(fact), style = CoffeeTheme.type.body, color = colors.textSecondary)
            Spacer(Modifier.height(6.dp))
        }
    }
}

@Composable
fun TodaysLessonCard(vm: AppViewModel) {
    val colors = CoffeeTheme.colors
    val daySeed = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
    val card = LearnContent.cards[daySeed % LearnContent.cards.size]
    CoffeeCard(modifier = Modifier.fillMaxWidth().clickable { vm.openRoute(Route.LearnDetail(LearnContent.cards.indexOf(card))) }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            LineIcon(Glyph.BOOK, colors.accent, Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            AppText(stringResource(R.string.learn_today), style = CoffeeTheme.type.label, color = colors.accent)
        }
        Spacer(Modifier.height(6.dp))
        AppText(stringResource(card.titleRes), style = CoffeeTheme.type.headline)
        Spacer(Modifier.height(2.dp))
        AppText(stringResource(card.bodyRes), style = CoffeeTheme.type.caption, color = colors.textSecondary, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}

private data class QuizQuestion(
    val question: String,
    val answers: List<String>,
    val correctIndex: Int,
    val explanation: String,
)

private val QuizQuestions = listOf(
    QuizQuestion(
        "What grind size for French Press?",
        listOf("Fine", "Medium", "Coarse", "Extra Fine"),
        2,
        "French press uses a coarse grind to prevent sludge and bitterness.",
    ),
    QuizQuestion(
        "What water temp for pour-over (SCA standard)?",
        listOf("80-85°C", "90-96°C", "70-75°C", "100°C"),
        1,
        "SCA recommends 90-96°C for optimal pour-over extraction.",
    ),
    QuizQuestion(
        "What causes sour coffee?",
        listOf("Over-extraction", "Under-extraction", "Old beans", "Hard water"),
        1,
        "Sour coffee is a classic sign of under-extraction.",
    ),
    QuizQuestion(
        "Bloom time for fresh coffee?",
        listOf("5-10 sec", "30-45 sec", "60-90 sec", "No bloom needed"),
        1,
        "Bloom for 30-45 seconds to allow CO₂ to escape for even extraction.",
    ),
    QuizQuestion(
        "Which has more caffeine?",
        listOf("Arabica", "Robusta", "Both equal", "Decaf"),
        1,
        "Robusta beans contain roughly twice the caffeine of Arabica.",
    ),
    QuizQuestion(
        "What is the ideal water temperature for pour-over (SCA standard)?",
        listOf("80-85°C", "90-96°C", "70-75°C", "100°C"),
        1,
        "SCA specifies 195-205°F (90-96°C). Below 90°C under-extracts; above 96°C risks bitterness.",
    ),
    QuizQuestion(
        "Which coffee species has more caffeine?",
        listOf("Arabica (~1.2%)", "Robusta (~2.2%)", "Liberica", "All equal"),
        1,
        "Robusta has nearly double the caffeine of Arabica, giving it a more bitter taste and stronger pest resistance.",
    ),
    QuizQuestion(
        "What is 'crema'?",
        listOf("Coffee foam from steamed milk", "The tan foam on top of espresso", "A type of bean", "A roasting level"),
        1,
        "Crema forms when CO2 and coffee oils emulsify under 9 bars of pressure. Fresh beans produce more crema.",
    ),
    QuizQuestion(
        "Which grind for French Press?",
        listOf("Extra fine", "Fine", "Coarse", "Medium"),
        2,
        "Coarse grind prevents sludge and over-extraction during the 4-minute immersion. Think breadcrumbs, not powder.",
    ),
    QuizQuestion(
        "What does 'blooming' mean?",
        listOf("Adding milk", "Pre-wetting grounds to release CO2", "Roasting beans", "Grinding coffee"),
        1,
        "Fresh coffee releases CO2 when hot water first hits it. Blooming for 30-45 seconds ensures even extraction.",
    ),
    QuizQuestion(
        "What is a 'ristretto'?",
        listOf("Espresso with more water", "Espresso with less water", "A type of bean", "A brewing device"),
        1,
        "Ristretto uses the same dose but half the water — more concentrated, sweeter, less bitter than regular espresso.",
    ),
    QuizQuestion(
        "Which country is the birthplace of coffee?",
        listOf("Brazil", "Colombia", "Ethiopia", "Vietnam"),
        2,
        "Coffee originated in Ethiopia's Kaffa region. Legend says a goatherd named Kaldi discovered it when his goats ate red berries and became energetic.",
    ),
    QuizQuestion(
        "What is 'TDS' in coffee?",
        listOf("Total Dissolved Solids", "Temperature Degree Scale", "Total Drink Strength", "Turkish Dark Special"),
        0,
        "TDS measures brew strength — how much coffee is dissolved in the water. SCA target is 1.15-1.55%.",
    ),
    QuizQuestion(
        "Which country produces the most coffee?",
        listOf("Colombia", "Ethiopia", "Brazil", "Vietnam"),
        2,
        "Brazil produces ~35% of the world's coffee. Vietnam is #2 but mostly Robusta.",
    ),
    QuizQuestion(
        "What does 'cupping' mean?",
        listOf("Choosing coffee beans", "Professional coffee tasting", "Making espresso", "Storing coffee"),
        1,
        "Cupping is the industry-standard evaluation method — 8.25g coffee to 150ml water, slurped from a spoon.",
    ),
    QuizQuestion(
        "What is 'first crack' in roasting?",
        listOf("Breaking beans", "Audible pop when beans expand", "Grinding beans", "Opening the bag"),
        1,
        "At ~196°C, beans pop audibly as moisture expands — marking the transition from light to medium roast.",
    ),
    QuizQuestion(
        "Cold brew is typically steeped for how long?",
        listOf("5 minutes", "30 minutes", "12-24 hours", "2 hours"),
        2,
        "Cold brew uses time instead of heat — 12-24 hours at room temp or refrigerated extracts smooth, low-acid coffee.",
    ),
    QuizQuestion(
        "What is 'channeling' in espresso?",
        listOf("TV channel", "Water finding easy paths through coffee", "Water channel", "Coffee brand"),
        1,
        "Channeling creates uneven extraction — some grounds overextract, others underextract. Proper distribution and tamping prevents it.",
    ),
    QuizQuestion(
        "Which grind for Turkish coffee?",
        listOf("Coarse", "Medium", "Fine", "Extra fine/powder"),
        3,
        "Turkish coffee needs the finest grind of any method — almost like flour. This allows the unique unfiltered brewing.",
    ),
    QuizQuestion(
        "What is 'direct trade'?",
        listOf("Buying from supermarket", "Roasters buying directly from farmers", "Online shopping", "Trading stocks"),
        1,
        "Direct trade bypasses the commodity market, often paying farmers 2-3x the C-market price for quality.",
    ),
    QuizQuestion(
        "What causes bitter coffee?",
        listOf("Under-extraction", "Cold water", "Over-extraction", "Fresh beans"),
        2,
        "When too much dissolves from the coffee, bitter compounds dominate. Fix: grind coarser, use cooler water, or reduce brew time.",
    ),
    QuizQuestion(
        "What is 'natural processing'?",
        listOf("Organic coffee", "Drying whole cherries with fruit intact", "No processing", "Machine processing"),
        1,
        "The oldest method — cherries dried in sun for 2-4 weeks. Creates intense fruity, winey flavors and heavy body.",
    ),
    QuizQuestion(
        "What is 'washed processing'?",
        listOf("Cleaning with soap", "Removing fruit before drying", "Dry cleaning", "Water washing only"),
        1,
        "Cherries depulped, fermented in water tanks, washed clean, then dried. Produces clean, bright cups highlighting origin character.",
    ),
    QuizQuestion(
        "What is the ideal coffee-to-water ratio for pour-over?",
        listOf("1:5", "1:10", "1:16", "1:30"),
        2,
        "SCA recommends 1:15-1:18. 1:16 means 1g coffee per 16g water — the sweet spot for most pour-over methods.",
    ),
    QuizQuestion(
        "What is 'crema'?",
        listOf("Cream", "The tan foam on top of espresso", "A dessert", "Milk foam"),
        1,
        "Crema forms when CO₂ and coffee oils emulsify under 9 bars of pressure — a sign of fresh espresso.",
    ),
    QuizQuestion(
        "Which is NOT a coffee processing method?",
        listOf("Washed", "Natural", "Carbonic maceration", "All are real methods"),
        3,
        "Wait — they're ALL real methods! Carbonic maceration is anaerobic fermentation borrowed from winemaking, using CO₂-pressurized tanks.",
    ),
    QuizQuestion(
        "What is the SCA cupping score range for 'specialty' coffee?",
        listOf("60-70", "70-80", "80-100", "90-100"),
        2,
        "SCA defines specialty as 80+ points. 80-84 is 'Very Good', 85-89 is 'Excellent', 90+ is 'Outstanding'.",
    ),
    QuizQuestion(
        "What is 'degassing'?",
        listOf("Removing gas from beans", "CO₂ releasing from roasted beans", "Ventilating", "Vacuum packing"),
        1,
        "Roasted beans release CO₂ for days after roasting. Fresh beans bloom vigorously. Too fresh (1-2 days) = excessive bloom.",
    ),
    QuizQuestion(
        "What temperature for milk steaming?",
        listOf("30-40°C", "40-50°C", "55-65°C", "70-80°C"),
        2,
        "Stop at 60-65°C. Beyond 70°C, proteins denature causing scalded flavor. Properly steamed milk looks like wet paint.",
    ),
    QuizQuestion(
        "What is 'peaberry'?",
        listOf("A berry", "Single round bean in cherry", "A defect bean", "Small bean"),
        1,
        "A mutation where only one round bean forms instead of two flat ones (~5% of harvest). Often sold at premium for perceived superior flavor.",
    ),
    QuizQuestion(
        "What pressure does espresso brew at?",
        listOf("1 bar", "3 bars", "9 bars", "15 bars"),
        2,
        "Standard espresso uses 9 bars of pressure. This forces water through fine grounds in 25-35 seconds, creating the concentrated shot and crema.",
    ),
    QuizQuestion(
        "Where did Arabica coffee originate?",
        listOf("Brazil", "Colombia", "Ethiopia", "Vietnam"),
        2,
        "Arabica is indigenous to Ethiopia's highland forests. The name comes from 'Arabia' where it was first traded globally through Yemen's port of Mocha.",
    ),
    QuizQuestion(
        "How many coffee beans are typically in one coffee cherry?",
        listOf("1", "2", "3", "4"),
        1,
        "Each cherry normally contains two flat-sided beans facing each other. Peaberries are the rare mutation with only one round bean.",
    ),
    QuizQuestion(
        "What is 'honey processing' in coffee?",
        listOf("Adding honey", "Drying with mucilage left on", "Sweet coffee", "Fermenting with honey"),
        1,
        "The middle path between washed and natural — skin removed but sticky mucilage stays during drying. Pioneered in Costa Rica.",
    ),
    QuizQuestion(
        "Who invented the AeroPress?",
        listOf("James Hoffmann", "Alan Adler", "Howard Schultz", "Alfred Peet"),
        1,
        "Stanford engineering professor Alan Adler invented the AeroPress in 2005. He also invented the Aerobie flying ring.",
    ),
    QuizQuestion(
        "What makes Chemex filters different?",
        listOf("They're reusable", "They're 20-30% thicker than standard", "They're metal", "They're cloth"),
        1,
        "Chemex filters are uniquely thick — producing extremely clean, tea-like coffee by trapping nearly all oils and micro-fines.",
    ),
    QuizQuestion(
        "Which has MORE total caffeine per typical serving?",
        listOf("Espresso shot", "Drip coffee 12oz", "Both equal", "Decaf"),
        1,
        "Per ounce espresso is stronger (~63mg/oz vs ~12mg/oz), but a 12oz drip has ~150mg total vs ~63mg in a single espresso shot.",
    ),
    QuizQuestion(
        "How should you store coffee beans?",
        listOf("Refrigerator", "Airtight container away from light", "Original bag open", "Freezer door"),
        1,
        "Airtight, cool, dark place. Not the fridge — condensation ruins flavor. Freeze only sealed portions you won't use within 2 weeks.",
    ),
    QuizQuestion(
        "What is the Swiss Water Process?",
        listOf("Swiss coffee brand", "Chemical-free decaffeination", "Water filtration", "Swiss brewing method"),
        1,
        "Uses water and carbon filters to remove 99.9% of caffeine without chemicals. The gold standard for decaf.",
    ),
    QuizQuestion(
        "What is 'microfoam' in milk steaming?",
        listOf("Tiny bubbles creating wet paint texture", "Large foam bubbles", "Cold milk foam", "Whipped cream"),
        0,
        "Properly steamed milk has microfoam — velvety, glossy, no visible bubbles. Essential for latte art.",
    ),
    QuizQuestion(
        "What defines 'specialty coffee'?",
        listOf("Any expensive coffee", "Coffee scoring 80+ points on SCA scale", "Coffee from one farm", "Organic coffee"),
        1,
        "SCA defines specialty as 80+ on the 100-point cupping scale. Only ~10% of global production meets this standard.",
    ),
    QuizQuestion(
        "What is 'Fair Trade' certification?",
        listOf("Price guarantee", "Minimum price floor for farmers", "Organic certification", "Direct from farmer"),
        1,
        "Fair Trade guarantees a minimum price ($1.40+/lb) regardless of C-market fluctuations, plus a social premium for community development.",
    ),
    QuizQuestion(
        "Blade vs burr grinder — which is better?",
        listOf("Blade", "Burr", "Both equal", "Neither"),
        1,
        "Burr grinders crush beans uniformly. Blade grinders chop randomly — producing fines and boulders simultaneously. A burr grinder is the single best upgrade you can make.",
    ),
    QuizQuestion(
        "What is the ideal water hardness for brewing (SCA)?",
        listOf("0-50 ppm", "50-175 ppm", "200-300 ppm", "300+ ppm"),
        1,
        "SCA specifies 50-175 ppm CaCO₃ hardness. Too soft = flat coffee. Too hard = chalky, muted. Magnesium extracts better than calcium.",
    ),
    QuizQuestion(
        "How much caffeine does decaf contain?",
        listOf("0mg", "2-5mg per cup", "50mg", "Same as regular"),
        1,
        "Decaf isn't caffeine-free — it contains 2-5mg per 8oz cup vs 95-165mg for regular. Swiss Water Process removes 99.9%.",
    ),
    QuizQuestion(
        "What is the world's most expensive coffee often called?",
        listOf("Blue Mountain", "Kopi Luwak", "Panama Geisha", "Kona"),
        2,
        "While Kopi Luwak is famous, Panama Geisha regularly breaks auction records — top lots selling for $10,000+/kg. Known for explosive jasmine and bergamot notes.",
    ),
    QuizQuestion(
        "What is the 'cascade' or 'bloom' reaction?",
        listOf("Adding sugar", "CO² release when hot water hits fresh grounds", "Milk pouring", "Roasting process"),
        1,
        "Fresh coffee releases trapped CO² violently when hot water first hits it. Blooming (30-45s) lets gas escape for even extraction.",
    ),
    QuizQuestion(
        "What is 'single origin' coffee?",
        listOf("One bean type", "Coffee from one specific region/farm", "One roast level", "One cup"),
        1,
        "Single origin means traceable to a specific farm, cooperative, or region — showcasing terroir rather than blending for consistency.",
    ),
    QuizQuestion(
        "What year was the espresso machine invented?",
        listOf("1800", "1884", "1950", "1901"),
        1,
        "Angelo Moriondo patented the first espresso machine in Turin (1884). The modern version by Achille Gaggia (1948) added the lever that creates crema.",
    ),
    QuizQuestion(
        "What is the world's oldest coffee house?",
        listOf("Starbucks", "Caffè Florian in Venice", "Café de Flore Paris", "Vienna coffee house"),
        1,
        "Caffè Florian opened in Venice's St. Mark's Square in 1720 and still operates today. Coffee houses were called 'penny universities' in 17th century London.",
    ),
    QuizQuestion(
        "Standard double espresso dose weight?",
        listOf("9-10g", "14-15g", "18-20g", "25-30g"),
        2,
        "The standard Italian doppio uses 14-18g, but modern specialty shops use 18-20g for a 36-40g yield (1:2 ratio) in 25-35 seconds.",
    ),
    QuizQuestion(
        "What is the typical espresso yield ratio?",
        listOf("1:1", "1:2", "1:4", "1:10"),
        1,
        "A 1:2 ratio means 18g in → 36g out. Ristretto is ~1:1, normale is 1:2, lungo is 1:3-4.",
    ),
    QuizQuestion(
        "What is pre-infusion in espresso?",
        listOf("Pre-grinding beans", "Low-pressure water soak before full pressure", "Pre-heating the cup", "Steaming milk first"),
        1,
        "Pre-infusion wets the puck at low pressure (~2-3 bar) before ramping to 9 bar, reducing channeling and improving extraction uniformity.",
    ),
    QuizQuestion(
        "What is a pressurized espresso basket?",
        listOf("Basket with a single tiny hole", "Basket with extra holes", "Double-wall basket with false bottom", "Larger basket"),
        2,
        "Pressurized baskets create artificial backpressure; designed for pre-ground coffee. Non-pressurized baskets require proper grind and puck prep.",
    ),
    QuizQuestion(
        "What is a WDT tool used for?",
        listOf("Weighing dose", "Breaking clumps and distributing grounds", "Measuring temperature", "Tamping"),
        1,
        "Weiss Distribution Technique — thin needles stir the grounds to break clumps and evenly distribute before tamping for shot consistency.",
    ),
    QuizQuestion(
        "What is the recommended tamping pressure?",
        listOf("5 kg", "10 kg", "15 kg", "Consistent pressure matters more than the number"),
        3,
        "Research shows tamping consistency is more important than absolute force. Once the puck is fully compressed, extra force doesn't change flow.",
    ),
    QuizQuestion(
        "Stretching vs texturing milk — which comes first?",
        listOf("Texturing, then stretching", "Stretching, then texturing", "They're the same", "Neither"),
        1,
        "Stretch first (tip near surface, air injected with a ripping sound), then texture (submerge wand to whirl and blend microfoam).",
    ),
    QuizQuestion(
        "Which plant milk froths best for latte art?",
        listOf("Almond", "Oat (barista edition)", "Rice", "Coconut"),
        1,
        "Oat milk (especially barista editions with added fat and stabilizers) creates the best microfoam and pours most like dairy milk.",
    ),
    QuizQuestion(
        "What is the key to pouring a latte art heart?",
        listOf("Pour fast from high", "Pour close to surface, wiggle, then draw through", "Mix milk and coffee first", "Use cold milk"),
        1,
        "Pour from ~2 cm above surface, wiggle the pitcher to create a blob, then raise and draw through the center to form a heart.",
    ),
    QuizQuestion(
        "Immersion vs percolation brewing — which is French Press?",
        listOf("Percolation", "Immersion", "Both", "Neither"),
        1,
        "French Press is immersion — grounds steep fully submerged. Pour-over is percolation — water flows through a bed of grounds.",
    ),
    QuizQuestion(
        "Why agitate or stir during pour-over brewing?",
        listOf("To cool the coffee", "To ensure even extraction and prevent dry spots", "To speed up brewing", "To create crema"),
        1,
        "Gentle agitation (stirring or swirling) ensures all grounds contact fresh water, preventing channels and uneven extraction in the coffee bed.",
    ),
    QuizQuestion(
        "Should you push the French Press plunger all the way down?",
        listOf("Yes, press hard", "No, stop just above the grounds", "Yes, immediately", "Press halfway only"),
        1,
        "Stop just above the coffee bed — pressing all the way disturbs settled grounds and releases bitter sediment into the cup.",
    ),
    QuizQuestion(
        "How does a siphon (vacuum) brewer work?",
        listOf("Electric pump", "Gravity only", "Vapor pressure and vacuum", "Centrifugal force"),
        2,
        "Heat creates vapor pressure pushing water into the top chamber; removing heat creates a vacuum that pulls brewed coffee back down through a filter.",
    ),
    QuizQuestion(
        "What distinguishes the Kalita Wave dripper?",
        listOf("Single large hole", "Three small holes and flat bottom", "Ridges all the way down", "No holes"),
        1,
        "The flat bottom with three small holes regulates flow, making it more forgiving than a V60 — great for beginners seeking consistency.",
    ),
    QuizQuestion(
        "V60 vs flat-bottom dripper — which extracts more evenly?",
        listOf("V60", "Flat-bottom (e.g. Kalita)", "Both identical", "Depends on coffee color"),
        1,
        "Flat-bottom drippers create a more even bed depth, reducing bypass and high-and-dry grounds; V60s require better technique but reward with clarity.",
    ),
    QuizQuestion(
        "How does a metal filter change brew taste vs paper?",
        listOf("No difference", "More body and oils, less clarity", "Cleaner, lighter body", "Sweeter taste"),
        1,
        "Metal filters allow oils and micro-fines through, producing a fuller-bodied cup with more mouthfeel but less clarity than paper-filtered coffee.",
    ),
    QuizQuestion(
        "What pH water is ideal for coffee brewing?",
        listOf("5.0-5.5", "6.0-6.5", "7.0 (neutral)", "8.0-8.5"),
        2,
        "SCA recommends pH ~7.0 (neutral). Water that is too acidic or alkaline buffers poorly, leading to flat or harsh extraction.",
    ),
    QuizQuestion(
        "What role does alkalinity (buffer) play in brewing?",
        listOf("No role", "Neutralizes acids, affecting perceived sourness", "Adds sweetness", "Colors the brew"),
        1,
        "Alkalinity (carbonate buffer) neutralizes coffee acids. Too much = flat, chalky coffee. Too little = sharply sour. Ideal range: 40-75 ppm as CaCO₃.",
    ),
    QuizQuestion(
        "Magnesium vs calcium — which extracts coffee flavors better?",
        listOf("Calcium", "Magnesium", "Equal", "Neither extracts"),
        1,
        "Magnesium ions bind more effectively to flavor compounds (especially fruity acids) than calcium, making it the preferred hardness mineral for brewing.",
    ),
    QuizQuestion(
        "Why should you NOT use distilled water for coffee?",
        listOf("It's too expensive", "It lacks minerals needed for extraction", "It's too hot", "It's illegal"),
        1,
        "Distilled water has no minerals — it over-extracts harsh compounds and produces flat, lifeless coffee. Minerals are essential for proper flavor extraction.",
    ),
    QuizQuestion(
        "What is Third Wave Water?",
        listOf("A coffee brand", "Mineral packets added to distilled water for ideal brewing", "Bottled spring water", "Filtered tap water"),
        1,
        "Third Wave Water packets add the optimal blend of magnesium sulfate, calcium citrate, and sodium chloride to distilled water, creating SCA-ideal brewing water.",
    ),
    QuizQuestion(
        "What is the Maillard reaction in coffee roasting?",
        listOf("Beans turning brown", "Amino acids + sugars reacting under heat to create flavor", "Water evaporating", "Beans cracking"),
        1,
        "Above ~150°C, amino acids and reducing sugars react in the Maillard reaction, creating hundreds of flavor and aroma compounds — from malty to nutty to chocolatey.",
    ),
    QuizQuestion(
        "What is Development Time Ratio (DTR) in roasting?",
        listOf("Total roast time", "Time from first crack to end as % of total", "Time before first crack", "Cooling time"),
        1,
        "DTR = (time after first crack / total roast time) × 100. Typical range is 15-25%; too short = grassy, too long = baked flavors.",
    ),
    QuizQuestion(
        "What does the Agtron scale measure?",
        listOf("Bean size", "Roast color/degree", "Moisture content", "Caffeine level"),
        1,
        "Agtron uses infrared light to measure roast degree numerically. Higher numbers = lighter roasts (Agtron 95-75), lower = darker (Agtron 35-25).",
    ),
    QuizQuestion(
        "Light vs dark roast — which has more caffeine by weight?",
        listOf("Dark roast", "Light roast (slightly)", "Equal by weight", "Neither has caffeine"),
        1,
        "By weight, light roasts have marginally more caffeine (beans lose mass during roasting). By volume, dark roasts can yield more since beans are less dense.",
    ),
    QuizQuestion(
        "What was Yemen's Port of Mocha famous for?",
        listOf("Chocolate production", "Being the world's first coffee trading port", "Tea exports", "Sugar trade"),
        1,
        "From the 15th-17th centuries, Mocha was the exclusive global coffee trading port. 'Mocha' became synonymous with coffee, not the chocolate drink.",
    ),
    QuizQuestion(
        "What makes Jamaican Blue Mountain coffee special?",
        listOf("It's blue", "Grown at high altitude in specific parishes, certified by CIAB", "It's wet-processed", "It's the cheapest"),
        1,
        "Only coffee grown between 910-1,700m in specific Blue Mountain parishes earns certification. Known for mild flavor, bright acidity, and lack of bitterness.",
    ),
    QuizQuestion(
        "Where is Kona coffee grown?",
        listOf("Brazil", "Ethiopia", "Hawaii (Big Island)", "Jamaica"),
        2,
        "Kona coffee grows on the volcanic slopes of Mauna Loa and Hualalai in Hawaii. The unique microclimate of sunny mornings and cloudy afternoons defines its character.",
    ),
    QuizQuestion(
        "What is Sumatran giling basah (wet-hulling)?",
        listOf("Washing in rivers", "Removing parchment at high moisture while beans are still soft", "Sun drying only", "Steaming beans"),
        1,
        "A uniquely Indonesian method: parchment is removed at ~50% moisture (vs 11% for washed), giving Sumatran coffee its earthy, herbal, full-bodied profile.",
    ),
    QuizQuestion(
        "How does the Kenyan coffee auction system work?",
        listOf("Online only", "Weekly open-outcry auction at Nairobi Coffee Exchange", "By direct email", "Through government quotas"),
        1,
        "Kenya's auction system is one of the world's most transparent — lots are graded and catalogued, then buyers bid openly, rewarding quality with higher prices.",
    ),
    QuizQuestion(
        "Who is Kaldi in coffee legend?",
        listOf("The first barista", "The Ethiopian goatherd who discovered coffee", "A Turkish sultan", "The inventor of espresso"),
        1,
        "Ethiopian legend says Kaldi noticed his goats dancing after eating red coffee cherries circa 850 AD. A monk then brewed the first cup from the beans.",
    ),
    QuizQuestion(
        "What role did Ottoman coffeehouses play in coffee history?",
        listOf("Just served coffee", "Became centers of social life, political debate, and entertainment", "Only sold beans", "Were banned from start"),
        1,
        "The first coffeehouse opened in Istanbul in 1555. They became so influential as political discussion hubs that Sultan Murad IV briefly banned them in the 1630s.",
    ),
    QuizQuestion(
        "How did Viennese coffee culture begin?",
        listOf("From Italy", "After the Ottoman siege of Vienna (1683), using abandoned coffee sacks", "From France", "From America"),
        1,
        "After the failed Ottoman siege, Polish officer Jerzy Kulczycki took abandoned coffee sacks and opened Vienna's first coffeehouse, adding milk and sugar — creating the Viennese style.",
    ),
    QuizQuestion(
        "What defines Italian espresso culture?",
        listOf("Sitting for hours", "Standing at the bar, drinking quickly, and leaving", "Drive-through coffee", "Home brewing only"),
        1,
        "Espresso in Italy is consumed standing at the bar in ~3 sips. It's a quick ritual, often under €1 (government-regulated), and drunk multiple times daily.",
    ),
    QuizQuestion(
        "What defines the Third Wave coffee movement?",
        listOf("Instant coffee", "Treating coffee as an artisanal product, like wine", "Flavored lattes", "Fast-food coffee"),
        1,
        "Third Wave (2000s-present) treats coffee as a craft product — emphasizing direct trade, single origins, lighter roasts, and transparency from farm to cup.",
    ),
    QuizQuestion(
        "What is the ideal extraction yield percentage?",
        listOf("5-10%", "18-22%", "30-35%", "50-60%"),
        1,
        "SCA Gold Cup standard targets 18-22% extraction. Below 18% = sour/underdeveloped; above 22% = bitter/over-extracted. Only ~30% of a bean is soluble.",
    ),
    QuizQuestion(
        "What does a coffee refractometer measure?",
        listOf("Temperature", "TDS (Total Dissolved Solids) to calculate extraction yield", "pH", "Bean density"),
        1,
        "A refractometer (like the VST) measures the refractive index of brewed coffee, converting it to TDS and then extraction yield using brew ratio and water mass.",
    ),
    QuizQuestion(
        "What are chlorogenic acids in coffee?",
        listOf("Artificial additives", "Natural antioxidants that give coffee its perceived acidity and health benefits", "Roasting defects", "Milk proteins"),
        1,
        "Chlorogenic acids (CGAs) are the primary antioxidants in coffee. They degrade during roasting, so lighter roasts retain more CGAs than darker roasts.",
    ),
    QuizQuestion(
        "What is the half-life of caffeine in the human body?",
        listOf("30 minutes", "1-2 hours", "3-7 hours", "12-24 hours"),
        2,
        "Caffeine's half-life is 3-7 hours in adults, meaning half is still in your system. For a 4pm coffee, roughly 50% remains at 9pm — hence the 2pm cutoff guideline.",
    ),
    QuizQuestion(
        "What is cafestol?",
        listOf("A coffee brand", "A diterpene compound that raises cholesterol (trapped by paper filters)", "A brewing device", "A type of bean"),
        1,
        "Cafestol, found in coffee oils, can raise LDL cholesterol. Paper filters trap most of it; unfiltered methods like French Press and Turkish retain cafestol.",
    ),
    QuizQuestion(
        "How long before bed should you stop drinking caffeine?",
        listOf("30 minutes", "1-2 hours", "6-8 hours", "No need to stop"),
        2,
        "Sleep experts recommend a caffeine cutoff 6-8 hours before bedtime. Caffeine blocks adenosine receptors; even if you fall asleep, deep-sleep quality suffers.",
    ),
    QuizQuestion(
        "What is the Ethiopian coffee ceremony?",
        listOf("Quick espresso service", "A 1-3 hour ritual of roasting, grinding, and brewing in a jebena pot, with incense", "Instant coffee tasting", "A farming ritual"),
        1,
        "The ceremony involves green beans roasted over charcoal, hand-ground, and brewed in a jebena clay pot. Three rounds are served with popcorn or snacks.",
    ),
    QuizQuestion(
        "What is Turkish coffee fortune telling (tasseography)?",
        listOf("Reading tea leaves", "Interpreting patterns in coffee grounds left in the cup", "Reading the foam", "Using coffee beans as dice"),
        1,
        "After drinking Turkish coffee, the cup is inverted onto the saucer. The patterns formed by the remaining grounds are 'read' to tell fortunes.",
    ),
    QuizQuestion(
        "What is Swedish 'fika'?",
        listOf("A brewing method", "The daily coffee break ritual with pastries, emphasizing social connection", "A type of bean", "A roasting technique"),
        1,
        "Fika is more than a coffee break — it's a social institution in Sweden. The ritual of pausing with coffee and a cinnamon bun (kanelbulle) is culturally sacred.",
    ),
    QuizQuestion(
        "What is Vietnamese egg coffee (cà phê trứng)?",
        listOf("Coffee with scrambled eggs", "Espresso topped with whipped egg yolk and condensed milk", "Coffee served in an eggshell", "Hard-boiled eggs in coffee"),
        1,
        "Invented in 1940s Hanoi during a milk shortage, it tops strong robusta coffee with a whipped mixture of egg yolks and condensed milk — rich and dessert-like.",
    ),
    QuizQuestion(
        "What is cascara?",
        listOf("A coffee variety", "The dried fruit skin of coffee cherries, brewed as a tea-like infusion", "A roasting machine", "A Colombian dish"),
        1,
        "Cascara (Spanish for 'husk') is the dried cherry skin and pulp, typically discarded during processing. Brewed as a tea, it tastes fruity, sweet, with notes of hibiscus.",
    ),
    QuizQuestion(
        "What is Bulletproof Coffee?",
        listOf("Espresso with sugar", "Coffee blended with grass-fed butter and MCT oil", "Cold brew with ice cream", "Decaf with milk"),
        1,
        "Popularized by Dave Asprey, Bulletproof Coffee blends brewed coffee with unsalted butter and MCT oil — marketed as a high-fat, ketogenic breakfast replacement.",
    ),
    QuizQuestion(
        "What is nitro cold brew?",
        listOf("Cold brew with ice", "Cold brew infused with nitrogen gas for a creamy, stout-like texture", "Instant cold brew", "Hot coffee cooled quickly"),
        1,
        "Nitrogen infusion creates tiny bubbles that give cold brew a silky, cascading mouthfeel — similar to a stout beer — without needing milk or sugar.",
    ),
    QuizQuestion(
        "What is the World Barista Championship (WBC)?",
        listOf("A latte art contest", "An annual global competition with 15-min routines judged on taste, technique, and presentation", "A roasting competition", "A local cafe contest"),
        1,
        "The WBC is the premier global barista competition. Competitors serve 4 espresso, 4 milk drinks, and 4 signature beverages to judges with strict scoring across taste, cleanliness, and presentation.",
    ),
    QuizQuestion(
        "What is the World Latte Art Championship?",
        listOf("Fastest coffee making", "Competitors pour patterns judged on symmetry, contrast, and creativity", "Coffee tasting", "Best roast"),
        1,
        "Baristas pour free-pour latte art and designer patterns. Judging criteria include visual symmetry, color infusion, contrast, and overall pattern creativity and difficulty.",
    ),
    QuizQuestion(
        "Carbonic maceration in coffee was adapted from which industry?",
        listOf("Beer brewing", "Winemaking", "Tea processing", "Distilling"),
        1,
        "Borrowed from Beaujolais winemaking, carbonic maceration places whole cherries in CO₂-flushed sealed tanks, creating intensely fruity, wine-like flavors impossible with traditional methods.",
    ),
    QuizQuestion(
        "Anaerobic fermentation differs from washed by fermenting coffee where?",
        listOf("In open-air tanks", "In sealed, oxygen-free tanks", "In direct sunlight", "In cold water"),
        1,
        "Anaerobic fermentation seals cherries in oxygen-free tanks for 24-72+ hours, developing unique tropical, boozy, and intensely sweet flavor profiles unlike traditional methods.",
    ),
    QuizQuestion(
        "Lactic acid fermentation produces what signature flavor in coffee?",
        listOf("Bitter, burnt notes", "Creamy, yogurty, and buttery notes", "Grassy, vegetal notes", "Smoky, spicy notes"),
        1,
        "Inoculating coffee with Lactobacillus cultures during fermentation yields creamy, yogurty mouthfeel and stone-fruit sweetness — borrowed from dairy science and applied to coffee processing.",
    ),
    QuizQuestion(
        "Monsooned coffee develops its unique flavor from exposure to what?",
        listOf("High-altitude winds", "Seasonal monsoon moisture and winds", "Underground caves", "Freezing temperatures"),
        1,
        "Monsooned Malabar from India exposes dried beans to monsoon winds and humidity for 12-16 weeks, swelling them to a pale gold color with a uniquely low-acid, earthy profile.",
    ),
    QuizQuestion(
        "How do naturally processed coffees typically differ from washed in flavor?",
        listOf("More clean, bright acidity", "More fruity, heavy body, and wine-like complexity", "More bitter and smoky", "No difference at all"),
        1,
        "Natural process dries cherries with fruit intact, infusing sugars into the bean. Result: intense berry, tropical, and fermented notes with heavier body compared to washed coffee's cleaner, brighter cup.",
    ),
    QuizQuestion(
        "Typica is what to most modern Arabica cultivars?",
        listOf("A disease-resistant hybrid", "The original genetic foundation or 'mother' variety", "A recently discovered wild strain", "An African robusta variety"),
        1,
        "Typica is the ur-Arabica — the original variety from which most others descend. It spread from Ethiopia to Yemen, then to Java, Amsterdam, and across the colonial coffee world.",
    ),
    QuizQuestion(
        "The Bourbon variety was named after which island?",
        listOf("Madagascar", "Réunion Island (formerly Bourbon Island)", "Sri Lanka", "Java"),
        1,
        "Réunion Island in the Indian Ocean was called Bourbon Island when the French introduced Typica there. A natural mutation produced the Bourbon variety with rounder cherries and sweeter cup profile.",
    ),
    QuizQuestion(
        "Caturra is what kind of mutation of Bourbon?",
        listOf("A disease-resistant mutation", "A natural dwarf mutation discovered in Brazil", "A cross-bred hybrid", "A mutation that doubles bean size"),
        1,
        "Discovered in 1937 in Brazil, Caturra is a single-gene dwarf mutation of Bourbon. Shorter internodes mean higher planting density and easier picking, though it requires more fertilizer.",
    ),
    QuizQuestion(
        "Catuai was created by crossing Mundo Novo with which variety?",
        listOf("Typica", "Geisha", "Caturra", "SL28"),
        2,
        "Developed in Brazil (1949), Catuai combines Mundo Novo's vigor with Caturra's compact stature. Yellow and red Catuai are both widely planted for high yield and disease resistance.",
    ),
    QuizQuestion(
        "Which African country developed the prized SL28 and SL34 varieties?",
        listOf("Ethiopia", "Kenya", "Tanzania", "Rwanda"),
        1,
        "Scott Agricultural Laboratories (hence 'SL') in Kenya selected these varieties in the 1930s. SL28 is drought-tolerant with blackcurrant notes; SL34 is adapted to high altitudes with complex fruit acidity.",
    ),
    QuizQuestion(
        "Geisha coffee from Panama is most famous for tasting notes of what?",
        listOf("Chocolate and caramel", "Jasmine, bergamot, and stone fruit", "Tobacco and leather", "Smoke and pepper"),
        1,
        "Originally from Ethiopia's Gesha region, the Geisha variety exploded in Panama in 2004. Its soaring jasmine aromatics, bergamot brightness, and peach-sweet finish triggered record-breaking auction prices.",
    ),
    QuizQuestion(
        "Maragogipe is nicknamed 'elephant bean' because of its unusually large what?",
        listOf("Cherry size", "Bean (seed) size", "Tree height", "Leaf width"),
        1,
        "A natural Typica mutation discovered in Brazil's Maragogipe region. Beans are roughly double normal size, producing a mild, often tea-like cup at the expense of low yield and productivity.",
    ),
    QuizQuestion(
        "Pacamara is a hybrid of which two varieties?",
        listOf("Pacamara and Typica", "Pacas and Maragogipe", "Pacas and Caturra", "Pacamara and Bourbon"),
        1,
        "Created in 1958 in El Salvador, Pacamara crosses the compact Pacas with the giant-beaned Maragogipe. The result: large beans with complex, often tropical-fruity and floral cup character.",
    ),
    QuizQuestion(
        "What is the main goal of the drying phase in coffee roasting?",
        listOf("Creating roast color", "Driving off moisture and preparing beans for browning", "Caramelizing all sugars", "Releasing caffeine"),
        1,
        "The drying phase (first ~30-40% of the roast) evaporates the bean's 8-12% moisture content. Water must be driven off before Maillard browning and caramelization can effectively begin.",
    ),
    QuizQuestion(
        "Second crack typically begins at what approximate bean temperature?",
        listOf("196°C", "210°C", "224-230°C", "250°C"),
        2,
        "Second crack (~224-230°C) is the fracturing of cellulose in the bean walls. Oils emerge on the surface, roast character dominates, and you've entered dark roast territory. Many specialty roasters drop before this point.",
    ),
    QuizQuestion(
        "The 'baking' roast defect occurs when the roast progresses too _____?",
        listOf("Quickly with very high heat", "Slowly, stalling before first crack", "Dark all the way", "Quickly, then cooled instantly"),
        1,
        "Baking happens when the roast stalls — insufficient heat momentum causes the bean to 'stew' rather than roast. Result: flat, bread-like, papery flavors with no sweetness or acidity.",
    ),
    QuizQuestion(
        "'Scorching' is a roast defect caused by what?",
        listOf("Too little heat", "Bean surfaces contacting excessively hot drum or airflow", "Roasting too dark", "Cooling too fast"),
        1,
        "When the drum or inlet air is too hot, bean surfaces char while the interior remains underdeveloped. The result tastes smoky and burnt on the outside, grassy and raw inside.",
    ),
    QuizQuestion(
        "'Tipping' appears as what on roasted coffee beans?",
        listOf("White spots in the center", "Small dark burn marks on the bean tips (ends)", "Cracks along the side", "Green patches throughout"),
        1,
        "Tipping is localized charring on the pointed ends of beans where heat transfer is highest. Common in drum roasters with excessive charge temperature, it creates acrid, smoky notes.",
    ),
    QuizQuestion(
        "Fluid bed roasters use what instead of a rotating drum?",
        listOf("Microwave radiation", "A stream of hot air to suspend and agitate beans", "Infrared lamps only", "Ultrasonic vibration"),
        1,
        "Fluid bed (air) roasters float beans on a column of hot air — like a popcorn popper. Roasts tend to be faster and brighter, with less conduction and more convective heat transfer than drum roasting.",
    ),
    QuizQuestion(
        "Conical burrs generally produce what compared to flat burrs?",
        listOf("Fewer fines and wider particle distribution", "Exactly uniform particles", "Only coarse grinds", "No difference at all"),
        0,
        "Conical burrs produce a bimodal particle distribution — more fines mixed with larger particles. This can enhance body and mouthfeel for espresso. Flat burrs tend toward unimodal, more uniform grinds.",
    ),
    QuizQuestion(
        "'Fines' in ground coffee are what?",
        listOf("Large, coarse particles", "Very small dust-like particles, typically under 100 microns", "Perfectly uniform grounds", "Coffee bean husk fragments"),
        1,
        "Fines are micro-particles (<100μm) produced during grinding. Too many fines can clog filters, create bitterness, and cause uneven extraction. A quality grinder minimizes excessive fines.",
    ),
    QuizQuestion(
        "The RDT (Ross Droplet Technique) adds what before grinding?",
        listOf("A drop of oil on the beans", "A tiny amount of water on the beans to reduce static", "A pinch of salt", "Nothing — it's a tamping technique"),
        1,
        "Spritzing or stirring a single drop of water into beans before grinding dramatically reduces static cling and retention — grounds flow smoothly instead of sticking to the grinder chute and walls.",
    ),
    QuizQuestion(
        "What is 'grinder retention'?",
        listOf("How long the grinder lasts", "Stale grounds from previous use that remain inside the grinder", "The grinder's maximum capacity", "How fast the grinder runs"),
        1,
        "Retention refers to old coffee grounds trapped inside the burr chamber and chute that mix with fresh grounds. High retention means you're always tasting yesterday's coffee in today's brew.",
    ),
    QuizQuestion(
        "Seasoning new burrs involves running how much coffee through them?",
        listOf("Just one batch", "2-5 kg of beans to wear off machining burrs and stabilize grind", "100 kg minimum", "Seasoning is unnecessary"),
        1,
        "Fresh burrs have microscopic machining ridges and sharp edges. Running several kilograms through them smooths the cutting surfaces for consistent, predictable grind size and reduced retention.",
    ),
    QuizQuestion(
        "What does the AeroPress inverted method primarily prevent?",
        listOf("Over-extraction", "Premature dripping before steeping is complete", "Under-heating", "Crema formation"),
        1,
        "Flipping the AeroPress upside-down (plunger at the bottom) prevents water from dripping through before you're ready to press — giving you full control over immersion time without early bypass.",
    ),
    QuizQuestion(
        "Kyoto-style cold drip coffee relies on what principle?",
        listOf("Hot water pressure", "Slow, gravity-fed drip — one drop at a time over 8-16 hours", "Vacuum extraction", "Centrifugal force"),
        1,
        "Kyoto drip uses a tower of glass chambers where ice water drips one drop every 1-2 seconds through a bed of coffee. The 8-16 hour process yields a smooth, oxidation-free, almost liqueur-like brew.",
    ),
    QuizQuestion(
        "Japanese iced coffee is made by brewing hot coffee directly onto what?",
        listOf("More hot water", "Ice, flash-chilling the brew instantly", "Milk", "An empty cup"),
        1,
        "Unlike cold brew, Japanese iced coffee uses hot water for extraction (~60% of usual water) over ice (~40%), locking in volatile aromatics that cold brewing can't extract. Bright, aromatic, and immediately ready.",
    ),
    QuizQuestion(
        "The pulse pouring technique in pour-over involves what?",
        listOf("Pouring all water at once", "Adding water in multiple, smaller pours spaced over time", "Using a machine pulse setting", "Pouring only at the center"),
        1,
        "Instead of one continuous pour, pulse pouring divides water into 3-5 separate additions. Each pulse agitates and resettles the bed, promoting higher and more even extraction from all grounds.",
    ),
    QuizQuestion(
        "Bypass brewing involves adding water after what step?",
        listOf("Grinding", "The initial concentrated extraction is complete", "Roasting", "Milk is added"),
        1,
        "Bypass brewing intentionally brews a strong concentrate, then dilutes it with hot water post-brew. This technique (used in batch brewers and some AeroPress recipes) balances extraction and strength independently.",
    ),
    QuizQuestion(
        "Affogato consists of what poured over a scoop of gelato or ice cream?",
        listOf("Cold brew", "A hot shot of espresso", "Drip coffee", "Latte"),
        1,
        "The Italian 'affogato al caffè' means 'drowned in coffee.' A hot espresso shot melts and contrasts with cold vanilla gelato, creating a bittersweet, creamy dessert that's absurdly simple yet elegant.",
    ),
    QuizQuestion(
        "Traditional tiramisu soaks ladyfingers (savoiardi) in what?",
        listOf("Hot milk", "Strong brewed espresso, often with a splash of Marsala or rum", "Instant coffee water", "Diluted cold brew"),
        1,
        "Authentic tiramisu dips savoiardi biscuits in cooled, strong espresso (optionally spiked with Marsala), layered with mascarpone cream. Coffee provides the bitter backbone balancing the sweet, creamy layers.",
    ),
    QuizQuestion(
        "Coffee dry rub acts as what for grilled or smoked meats?",
        listOf("A marinade substitute only", "Both a seasoning blend and a natural tenderizer, with a crust-building dark bark", "A sauce", "A brine"),
        1,
        "Coffee's acidity helps tenderize meat while fine grounds create a dark, flavorful crust on grilled steak or brisket. Combined with spices, sugar, and salt, it enhances smoky depth.",
    ),
    QuizQuestion(
        "Why do coffee and dark chocolate pair so well together?",
        listOf("They share no similarities", "Both contain overlapping roasted, bitter-sweet flavor compounds from similar bean families", "One neutralizes the other", "Only the sugar in chocolate matters"),
        1,
        "Coffee and cacao share parallel roasting chemistry — Maillard reactions create complementary notes of nuts, caramel, and dark fruit. Their shared bitterness also makes the combination feel balanced and harmonious.",
    ),
    QuizQuestion(
        "Why is it called 'coffee cake'?",
        listOf("It contains coffee as an ingredient", "It's a cake meant to be served alongside coffee, not containing coffee", "It's baked with coffee grounds", "It's espresso-flavored frosting"),
        1,
        "Classic American coffee cake contains zero coffee — the name indicates it's designed to accompany a cup of coffee. This confusing tradition dates back to 17th-century European 'kaffeeklatsch' gatherings.",
    ),
    QuizQuestion(
        "Shade-grown coffee primarily benefits what in tropical ecosystems?",
        listOf("Nothing — it's just marketing", "Biodiversity by preserving the forest canopy and wildlife habitat", "Only the coffee flavor", "Faster harvest times"),
        1,
        "Shade-grown coffee is cultivated under existing forest canopy, preserving trees that harbor migratory birds, insects, and mammals. Unlike full-sun monoculture, it maintains soil health and natural pest control.",
    ),
    QuizQuestion(
        "Bird-friendly coffee certification requires what key feature?",
        listOf("No specific requirements", "A multi-layered forest canopy with native tree species providing habitat", "Just being organic", "Single-layer shade trees"),
        1,
        "Smithsonian Bird-Friendly certification is the strictest eco-label: farms must provide diverse, multi-story canopy with native trees, organic practices, and measurable biodiversity — not just any shade qualifies.",
    ),
    QuizQuestion(
        "Coffee leaf rust (la roya) is caused by what type of organism?",
        listOf("A bacteria", "A fungus (Hemileia vastatrix)", "A virus", "An insect"),
        1,
        "This devastating fungus attacks coffee leaves, causing defoliation and crop loss. Since the 2012-2013 Central American epidemic, it has cost billions in damages. Arabica is especially susceptible.",
    ),
    QuizQuestion(
        "Climate change is predicted to reduce suitable coffee-growing land by what by 2050?",
        listOf("No reduction", "Roughly 50%, halving the land where Arabica can grow", "10%", "99%"),
        1,
        "Rising temperatures, shifting rainfall, and increased pest pressure could halve suitable Arabica acreage by 2050. Ethiopia alone may lose 60% of its coffee-growing areas — threatening millions of smallholder livelihoods.",
    ),
    QuizQuestion(
        "F1 coffee hybrids are created through what breeding method?",
        listOf("Random natural mutation", "Controlled cross-pollination of two genetically distinct parents", "Laboratory genetic modification", "Cloning only"),
        1,
        "F1 hybrids (like Starmaya, Centroamericano) are the first-generation offspring of two distinct pure-line parents. They exhibit 'hybrid vigor' — higher yield, disease resistance, and cup quality than either parent alone.",
    ),
    QuizQuestion(
        "C-market Arabica coffee prices are based on which futures exchange?",
        listOf("New York Stock Exchange (NYSE)", "Intercontinental Exchange (ICE) in New York", "London Metal Exchange", "NASDAQ"),
        1,
        "Arabica futures trade on the ICE (Intercontinental Exchange) in New York under the ticker KC. The C-market price is the global commodity baseline, but it rarely reflects farmers' actual production costs.",
    ),
    QuizQuestion(
        "The Cup of Excellence is what type of program?",
        listOf("A chain café competition", "A rigorous competition and auction that identifies and rewards the best coffees from a country", "A roasting contest", "A latte art showcase"),
        1,
        "Cup of Excellence runs national competitions where expert juries blind-cup hundreds of coffees. Winners (scoring 87+) are sold via transparent internet auctions, often fetching 5-50x the C-market price.",
    ),
    QuizQuestion(
        "A coffee microlot typically comes from what scale of production?",
        listOf("An entire country's harvest", "A specific, small section of a farm or single day's picking", "A commercial blend", "A roaster's warehouse"),
        1,
        "Microlots isolate exceptional coffee from a defined small plot, specific altitude band, or single day's picking — typically 5-50 bags. They represent the highest tier of traceability and quality potential.",
    ),
    QuizQuestion(
        "How does 'relationship coffee' differ from Fair Trade certification?",
        listOf("There's no difference", "Relationship coffee is a direct, ongoing partnership between buyer and producer, not a third-party certification", "Relationship coffee is cheaper", "Fair Trade is direct buying"),
        1,
        "Unlike Fair Trade's standardized certification model, relationship coffee is a commitment — roasters visit farms, collaborate on quality improvement, and negotiate prices directly, often paying well above both C-market and Fair Trade floors.",
    ),
    QuizQuestion(
        "After Brazil, which country produces the most coffee globally?",
        listOf("Colombia", "Ethiopia", "Vietnam", "Indonesia"),
        2,
        "Vietnam ranks #2, producing mostly Robusta for instant coffee and commodity blends. It went from negligible production in the 1980s to a global powerhouse in just 30 years — a remarkable agricultural transformation.",
    ),
    QuizQuestion(
        "In coffee tasting, body refers to weight on the tongue, while mouthfeel describes what?",
        listOf("Only temperature", "Tactile sensation — creamy, silky, juicy, or astringent texture", "Only bitterness level", "Just the aftertaste duration"),
        1,
        "Body is the perceived heaviness (light like skim milk to heavy like cream). Mouthfeel is the texture quality — whether the coffee feels silky, buttery, juicy, chalky, or drying on the palate.",
    ),
    QuizQuestion(
        "How does quality acidity differ from sourness in coffee?",
        listOf("They're the same thing", "Acidity is bright, complex, and pleasing (citrus, malic); sourness is harsh and unpleasant, indicating a defect", "Sourness only exists in dark roasts", "Acidity refers only to pH level"),
        1,
        "Acidity in fine coffee is vibrant and complex — think crisp green apple, juicy orange, or sparkling white wine. Sourness is a defect — harsh, vinegary, and unbalanced, typically from under-extraction or poor processing.",
    ),
    QuizQuestion(
        "Where does the perception of sweetness in black coffee originate?",
        listOf("Only from added sugar", "From caramelized sugars, fruity esters, and specific aroma compounds created during roasting", "From the water used", "Coffee has no sweetness without milk"),
        1,
        "Black coffee contains trace carbohydrates, but perceived sweetness mostly comes from aroma compounds (furaneol, vanillin) and the balance of bitterness and acidity that tricks the brain into detecting sweetness.",
    ),
    QuizQuestion(
        "'Aftertaste' in professional cupping refers to what?",
        listOf("How fast the coffee cools", "The length and quality of flavor that lingers after swallowing or spitting", "The initial sip impact", "Only bitter after-notes"),
        1,
        "Aftertaste (or 'finish') is a key SCA scoring attribute. The best coffees leave a clean, sweet, long-lasting finish. A short or unpleasant aftertaste — harsh, metallic, or papery — signals processing or roasting flaws.",
    ),
    QuizQuestion(
        "What does a 'clean cup' mean on a cupping form?",
        listOf("The cup was washed", "No flavor defects detected — a transparent, pure coffee expression", "No crema present", "The coffee was filtered twice"),
        1,
        "'Clean cup' indicates the coffee has zero off-flavors — no ferment, mold, phenolic, or rioy defects. It's the coffee's purity check: does this taste exactly like good coffee should, with nothing hiding underneath?",
    ),
    QuizQuestion(
        "What does 'balance' mean in coffee sensory evaluation?",
        listOf("Only equal parts of each flavor", "All flavor attributes — acidity, sweetness, body, bitterness — work in harmony; no single element dominates or is missing", "The cup is symmetrical", "Only refers to acidity-to-bitterness ratio"),
        1,
        "Balance is the judgement that acidity, body, sweetness, and flavor notes complement rather than clash. A balanced coffee tastes integrated and complete — where no single attribute shouts louder than the others.",
    ),
    QuizQuestion(
        "Coffee kombucha is made by fermenting what with a SCOBY?",
        listOf("Whole coffee beans", "Brewed coffee sweetened with sugar, like traditional kombucha", "Coffee grounds only", "Instant coffee with vinegar"),
        1,
        "Coffee kombucha replaces the tea in traditional kombucha with brewed coffee. The SCOBY ferments the sweetened coffee, yielding a fizzy, tangy-sweet probiotic drink with coffee's depth and kombucha's funk.",
    ),
    QuizQuestion(
        "Coffee flour is made by milling what part of the coffee plant?",
        listOf("Roasted coffee beans", "The dried cherry pulp and skin (cascara), a byproduct of processing", "Ground coffee leaves", "Spent coffee grounds"),
        1,
        "Coffee flour upcycles the dried cherry pulp discarded during processing into a gluten-free, high-fiber baking ingredient. It tastes nothing like brewed coffee — more like dried fruit with subtle floral notes.",
    ),
    QuizQuestion(
        "How do used coffee grounds benefit garden soil?",
        listOf("They do nothing beneficial", "Adding nitrogen and organic matter, improving soil structure and feeding worms", "They acidify soil dramatically", "They repel all insects"),
        1,
        "Used grounds are pH-neutral (acids are water-soluble and mostly end up in your cup) and contain ~2% nitrogen. Mixed into soil or compost, they improve drainage, aeration, and feed beneficial earthworms.",
    ),
    QuizQuestion(
        "Why is caffeine commonly used in skincare products?",
        listOf("It has no real effect", "It constricts blood vessels, reduces puffiness, and is a potent antioxidant that protects against UV damage", "It's just for fragrance", "It exfoliates dead skin"),
        1,
        "Caffeine's vasoconstricting properties reduce under-eye puffiness and redness. As a topical antioxidant, it protects skin from free-radical damage. Many eye creams and cellulite treatments feature it as a key active ingredient.",
    ),
)

@Composable
private fun QuickQuizCard() {
    val colors = CoffeeTheme.colors
    var questionIndex by remember { mutableStateOf(kotlin.random.Random.nextInt(QuizQuestions.size)) }
    var selectedAnswer by remember { mutableStateOf(-1) }
    var correctCount by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var counted by remember { mutableStateOf(false) }
    val q = QuizQuestions[questionIndex]
    val wrongColor = Color(0xFFE53935)
    CoffeeCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            LineIcon(Glyph.CHECK, colors.accent, Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            AppText(stringResource(R.string.learn_quiz_title), style = CoffeeTheme.type.title)
        }
        Spacer(Modifier.height(8.dp))
        AppText(q.question, style = CoffeeTheme.type.headline)
        Spacer(Modifier.height(8.dp))
        q.answers.forEachIndexed { index, answer ->
            val isSelected = selectedAnswer == index
            val isCorrect = index == q.correctIndex
            val background = when {
                selectedAnswer != -1 && isCorrect -> colors.accent
                selectedAnswer != -1 && isSelected && !isCorrect -> wrongColor
                else -> colors.accentSoft
            }
            val textColor = when {
                selectedAnswer != -1 && (isCorrect || (isSelected && !isCorrect)) -> colors.onAccent
                else -> colors.textPrimary
            }
            val prefix = when {
                selectedAnswer != -1 && isCorrect -> "✓  "
                selectedAnswer != -1 && isSelected && !isCorrect -> "✕  "
                else -> ""
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(background)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                    ) { if (selectedAnswer == -1) selectedAnswer = index }
                    .padding(10.dp),
            ) {
                AppText("$prefix$answer", style = CoffeeTheme.type.body, color = textColor)
            }
            Spacer(Modifier.height(6.dp))
        }
        if (selectedAnswer != -1) {
            Spacer(Modifier.height(4.dp))
            val isCorrect = selectedAnswer == q.correctIndex
            if (isCorrect && !counted) {
                correctCount++
                streak++
                counted = true
            }
            AppText(
                if (isCorrect) stringResource(R.string.learn_quiz_correct) else stringResource(R.string.learn_quiz_wrong),
                style = CoffeeTheme.type.label,
                color = if (isCorrect) colors.accent else wrongColor,
            )
            AppText(q.explanation, style = CoffeeTheme.type.caption, color = colors.textSecondary)
            Spacer(Modifier.height(4.dp))
            AppText(
                stringResource(R.string.learn_quiz_score, correctCount),
                style = CoffeeTheme.type.label,
                color = colors.accent,
            )
            if (!isCorrect) {
                streak = 0
            }
            if (isCorrect && streak >= 3) {
                Spacer(Modifier.height(2.dp))
                AppText(
                    stringResource(R.string.learn_quiz_streak, streak),
                    style = CoffeeTheme.type.label,
                    color = colors.accent,
                    modifier = Modifier.fillMaxWidth(),
                    align = TextAlign.Center,
                )
            }
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AppText(
                    "Next question →",
                    style = CoffeeTheme.type.label,
                    color = colors.accent,
                    modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                    ) {
                        questionIndex = kotlin.random.Random.nextInt(QuizQuestions.size)
                        selectedAnswer = -1
                        counted = false
                    },
                )
                AppText(
                    stringResource(R.string.learn_quiz_random),
                    style = CoffeeTheme.type.label,
                    color = colors.accent,
                    modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                    ) {
                        questionIndex = kotlin.random.Random.nextInt(QuizQuestions.size)
                        selectedAnswer = -1
                        counted = false
                    },
                )
            }
        }
    }
}
