package co.coffeery.app.ui.screens.drinks

import androidx.annotation.StringRes
import co.coffeery.app.R

/** Which catalog section a drink belongs to. */
enum class DrinkGroup(@StringRes val labelRes: Int) {
    MILK(R.string.drink_group_milk),
    REGIONAL(R.string.drink_group_regional),
}

/** A built (not ratio-brewed) drink recipe: milk-based or regional. */
data class DrinkItem(
    val id: String,
    @StringRes val nameRes: Int,
    @StringRes val summaryRes: Int,
    @StringRes val bodyRes: Int,
    val group: DrinkGroup,
)

object DrinkContent {
    /** Ordered so drinks sharing a group are contiguous (drives section headers). */
    val drinks: List<DrinkItem> = listOf(
        DrinkItem("latte", R.string.drink_latte_name, R.string.drink_latte_summary, R.string.drink_latte_body, DrinkGroup.MILK),
        DrinkItem("cappuccino", R.string.drink_cappuccino_name, R.string.drink_cappuccino_summary, R.string.drink_cappuccino_body, DrinkGroup.MILK),
        DrinkItem("flatwhite", R.string.drink_flatwhite_name, R.string.drink_flatwhite_summary, R.string.drink_flatwhite_body, DrinkGroup.MILK),
        DrinkItem("cortado", R.string.drink_cortado_name, R.string.drink_cortado_summary, R.string.drink_cortado_body, DrinkGroup.MILK),
        DrinkItem("macchiato", R.string.drink_macchiato_name, R.string.drink_macchiato_summary, R.string.drink_macchiato_body, DrinkGroup.MILK),
        DrinkItem("mocha", R.string.drink_mocha_name, R.string.drink_mocha_summary, R.string.drink_mocha_body, DrinkGroup.MILK),
        DrinkItem("affogato", R.string.drink_affogato_name, R.string.drink_affogato_summary, R.string.drink_affogato_body, DrinkGroup.MILK),
        DrinkItem("americano", R.string.drink_americano_name, R.string.drink_americano_summary, R.string.drink_americano_body, DrinkGroup.MILK),
        DrinkItem("redeye", R.string.drink_redeye_name, R.string.drink_redeye_summary, R.string.drink_redeye_body, DrinkGroup.MILK),
        DrinkItem("caphetrung", R.string.drink_caphetrung_name, R.string.drink_caphetrung_summary, R.string.drink_caphetrung_body, DrinkGroup.REGIONAL),
        DrinkItem("caphesuada", R.string.drink_caphesuada_name, R.string.drink_caphesuada_summary, R.string.drink_caphesuada_body, DrinkGroup.REGIONAL),
        DrinkItem("greekfrappe", R.string.drink_greekfrappe_name, R.string.drink_greekfrappe_summary, R.string.drink_greekfrappe_body, DrinkGroup.REGIONAL),
        DrinkItem("irishcoffee", R.string.drink_irishcoffee_name, R.string.drink_irishcoffee_summary, R.string.drink_irishcoffee_body, DrinkGroup.REGIONAL),
        DrinkItem("cafebombon", R.string.drink_cafebombon_name, R.string.drink_cafebombon_summary, R.string.drink_cafebombon_body, DrinkGroup.REGIONAL),
        DrinkItem("cortadito", R.string.drink_cortadito_name, R.string.drink_cortadito_summary, R.string.drink_cortadito_body, DrinkGroup.REGIONAL),
        DrinkItem("viennese", R.string.drink_viennese_name, R.string.drink_viennese_summary, R.string.drink_viennese_body, DrinkGroup.REGIONAL),
    )
}
