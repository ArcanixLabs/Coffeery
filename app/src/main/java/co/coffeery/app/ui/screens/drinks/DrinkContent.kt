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
        DrinkItem("doppio", R.string.drink_doppio_name, R.string.drink_doppio_summary, R.string.drink_doppio_body, DrinkGroup.MILK),
        DrinkItem("ristretto", R.string.drink_ristretto_name, R.string.drink_ristretto_summary, R.string.drink_ristretto_body, DrinkGroup.MILK),
        DrinkItem("lungo", R.string.drink_lungo_name, R.string.drink_lungo_summary, R.string.drink_lungo_body, DrinkGroup.MILK),
        DrinkItem("marocchino", R.string.drink_marocchino_name, R.string.drink_marocchino_summary, R.string.drink_marocchino_body, DrinkGroup.MILK),
        DrinkItem("caphetrung", R.string.drink_caphetrung_name, R.string.drink_caphetrung_summary, R.string.drink_caphetrung_body, DrinkGroup.REGIONAL),
        DrinkItem("caphesuada", R.string.drink_caphesuada_name, R.string.drink_caphesuada_summary, R.string.drink_caphesuada_body, DrinkGroup.REGIONAL),
        DrinkItem("greekfrappe", R.string.drink_greekfrappe_name, R.string.drink_greekfrappe_summary, R.string.drink_greekfrappe_body, DrinkGroup.REGIONAL),
        DrinkItem("irishcoffee", R.string.drink_irishcoffee_name, R.string.drink_irishcoffee_summary, R.string.drink_irishcoffee_body, DrinkGroup.REGIONAL),
        DrinkItem("cafebombon", R.string.drink_cafebombon_name, R.string.drink_cafebombon_summary, R.string.drink_cafebombon_body, DrinkGroup.REGIONAL),
        DrinkItem("cortadito", R.string.drink_cortadito_name, R.string.drink_cortadito_summary, R.string.drink_cortadito_body, DrinkGroup.REGIONAL),
        DrinkItem("viennese", R.string.drink_viennese_name, R.string.drink_viennese_summary, R.string.drink_viennese_body, DrinkGroup.REGIONAL),
        DrinkItem("cubano", R.string.drink_cubano_name, R.string.drink_cubano_summary, R.string.drink_cubano_body, DrinkGroup.REGIONAL),
        DrinkItem("conmiel", R.string.drink_conmiel_name, R.string.drink_conmiel_summary, R.string.drink_conmiel_body, DrinkGroup.REGIONAL),
        DrinkItem("barraquito", R.string.drink_barraquito_name, R.string.drink_barraquito_summary, R.string.drink_barraquito_body, DrinkGroup.REGIONAL),
        DrinkItem("eiskaffee", R.string.drink_eiskaffee_name, R.string.drink_eiskaffee_summary, R.string.drink_eiskaffee_body, DrinkGroup.REGIONAL),
        DrinkItem("touba", R.string.drink_touba_name, R.string.drink_touba_summary, R.string.drink_touba_body, DrinkGroup.REGIONAL),
    )
}

data class CoffeeVariety(
    @StringRes val nameRes: Int,
    @StringRes val originRes: Int,
    @StringRes val flavorRes: Int,
    @StringRes val bestBrewRes: Int,
)

object VarietyContent {
    val varieties: List<CoffeeVariety> = listOf(
        CoffeeVariety(R.string.variety_typica_name, R.string.variety_typica_origin, R.string.variety_typica_flavor, R.string.variety_typica_brew),
        CoffeeVariety(R.string.variety_bourbon_name, R.string.variety_bourbon_origin, R.string.variety_bourbon_flavor, R.string.variety_bourbon_brew),
        CoffeeVariety(R.string.variety_gesha_name, R.string.variety_gesha_origin, R.string.variety_gesha_flavor, R.string.variety_gesha_brew),
        CoffeeVariety(R.string.variety_sl28_name, R.string.variety_sl28_origin, R.string.variety_sl28_flavor, R.string.variety_sl28_brew),
        CoffeeVariety(R.string.variety_caturra_name, R.string.variety_caturra_origin, R.string.variety_caturra_flavor, R.string.variety_caturra_brew),
        CoffeeVariety(R.string.variety_pacamara_name, R.string.variety_pacamara_origin, R.string.variety_pacamara_flavor, R.string.variety_pacamara_brew),
        CoffeeVariety(R.string.variety_robusta_name, R.string.variety_robusta_origin, R.string.variety_robusta_flavor, R.string.variety_robusta_brew),
        CoffeeVariety(R.string.variety_heirloom_name, R.string.variety_heirloom_origin, R.string.variety_heirloom_flavor, R.string.variety_heirloom_brew),
        CoffeeVariety(R.string.variety_mundonovo_name, R.string.variety_mundonovo_origin, R.string.variety_mundonovo_flavor, R.string.variety_mundonovo_brew),
        CoffeeVariety(R.string.variety_catuai_name, R.string.variety_catuai_origin, R.string.variety_catuai_flavor, R.string.variety_catuai_brew),
        CoffeeVariety(R.string.variety_maragogipe_name, R.string.variety_maragogipe_origin, R.string.variety_maragogipe_flavor, R.string.variety_maragogipe_brew),
        CoffeeVariety(R.string.variety_liberica_name, R.string.variety_liberica_origin, R.string.variety_liberica_flavor, R.string.variety_liberica_brew),
    )
}
