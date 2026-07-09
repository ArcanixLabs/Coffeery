package co.coffeery.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import co.coffeery.app.data.model.BrewCategory
import co.coffeery.app.data.model.Equipment

/**
 * A bespoke, line-art illustration PER piece of equipment. Every brewer is
 * drawn from its own real physical silhouette — no shared category glyph, no
 * copy-pasted shapes. This is the v2 fix for v1's generic icon problem where
 * every pour-over shared one triangle, every immersion one cup, etc.
 *
 * Custom (user-created) gear has no bespoke drawing, so it falls back to a
 * category-representative silhouette.
 */
@Composable
fun EquipmentIcon(
    equipment: Equipment,
    tint: Color,
    modifier: Modifier = Modifier.size(24.dp),
) {
    val key = if (equipment.isCustom) "cat_${equipment.category.name}" else equipment.id
    Canvas(modifier = modifier) {
        val sw = size.minDimension * 0.075f
        val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawEquipment(key, tint, stroke)
    }
}

private fun DrawScope.line(tint: Color, x1: Float, y1: Float, x2: Float, y2: Float, sw: Float) =
    drawLine(tint, Offset(x1, y1), Offset(x2, y2), sw, StrokeCap.Round)

private fun DrawScope.drawEquipment(key: String, tint: Color, stroke: Stroke) {
    val w = size.width
    val h = size.height
    val sw = stroke.width
    fun p(build: Path.() -> Unit) = drawPath(Path().apply(build), tint, style = stroke)
    fun dot(cx: Float, cy: Float, r: Float) = drawCircle(tint, r, Offset(cx, cy), style = stroke)

    when (key) {
        // ---- Pour-over family ----
        "v60" -> { // steep 60° cone, one big hole, interior spiral ridges
            p { moveTo(w * 0.2f, h * 0.24f); lineTo(w * 0.8f, h * 0.24f); lineTo(w * 0.54f, h * 0.72f); lineTo(w * 0.46f, h * 0.72f); close() }
            line(tint, w * 0.5f, h * 0.72f, w * 0.5f, h * 0.86f, sw) // single center drip
            line(tint, w * 0.34f, h * 0.34f, w * 0.5f, h * 0.62f, sw * 0.7f) // spiral ridge hint
            line(tint, w * 0.66f, h * 0.34f, w * 0.5f, h * 0.62f, sw * 0.7f)
        }
        "chemex" -> { // hourglass: funnel over bulb + wood collar band
            p { moveTo(w * 0.24f, h * 0.16f); lineTo(w * 0.76f, h * 0.16f); lineTo(w * 0.54f, h * 0.48f); lineTo(w * 0.46f, h * 0.48f); close() }
            p { moveTo(w * 0.46f, h * 0.48f); cubicTo(w * 0.2f, h * 0.6f, w * 0.24f, h * 0.9f, w * 0.5f, h * 0.9f); cubicTo(w * 0.76f, h * 0.9f, w * 0.8f, h * 0.6f, w * 0.54f, h * 0.48f) }
            line(tint, w * 0.4f, h * 0.46f, w * 0.6f, h * 0.46f, sw) // collar band
            line(tint, w * 0.4f, h * 0.52f, w * 0.6f, h * 0.52f, sw)
        }
        "kalita" -> { // flat-bottom wave, three small holes
            p { moveTo(w * 0.24f, h * 0.26f); lineTo(w * 0.76f, h * 0.26f); lineTo(w * 0.66f, h * 0.66f); lineTo(w * 0.34f, h * 0.66f); close() }
            dot(w * 0.42f, h * 0.72f, w * 0.03f); dot(w * 0.5f, h * 0.72f, w * 0.03f); dot(w * 0.58f, h * 0.72f, w * 0.03f)
        }
        "origami" -> { // conical dripper with vertical fluted ribs
            p { moveTo(w * 0.22f, h * 0.24f); lineTo(w * 0.78f, h * 0.24f); lineTo(w * 0.54f, h * 0.7f); lineTo(w * 0.46f, h * 0.7f); close() }
            line(tint, w * 0.36f, h * 0.24f, w * 0.44f, h * 0.7f, sw * 0.7f)
            line(tint, w * 0.5f, h * 0.24f, w * 0.5f, h * 0.7f, sw * 0.7f)
            line(tint, w * 0.64f, h * 0.24f, w * 0.56f, h * 0.7f, sw * 0.7f)
            line(tint, w * 0.5f, h * 0.7f, w * 0.5f, h * 0.84f, sw)
        }
        "april" -> { // wide flat-bottom dripper, thick base
            p { moveTo(w * 0.22f, h * 0.28f); lineTo(w * 0.78f, h * 0.28f); lineTo(w * 0.64f, h * 0.66f); lineTo(w * 0.36f, h * 0.66f); close() }
            line(tint, w * 0.4f, h * 0.72f, w * 0.6f, h * 0.72f, sw) // flat base slot
            line(tint, w * 0.5f, h * 0.72f, w * 0.5f, h * 0.82f, sw)
        }
        "stagg" -> { // double-wall dripper, squared shoulders + top ridge
            p { moveTo(w * 0.26f, h * 0.26f); lineTo(w * 0.74f, h * 0.26f); lineTo(w * 0.6f, h * 0.68f); lineTo(w * 0.4f, h * 0.68f); close() }
            line(tint, w * 0.22f, h * 0.3f, w * 0.78f, h * 0.3f, sw * 0.8f) // rim ridge (double wall)
            line(tint, w * 0.46f, h * 0.68f, w * 0.46f, h * 0.8f, sw)
            line(tint, w * 0.54f, h * 0.68f, w * 0.54f, h * 0.8f, sw)
        }
        "timemore" -> { // glass cone with a big single "crystal eye" base ring
            p { moveTo(w * 0.24f, h * 0.24f); lineTo(w * 0.76f, h * 0.24f); lineTo(w * 0.56f, h * 0.64f); lineTo(w * 0.44f, h * 0.64f); close() }
            dot(w * 0.5f, h * 0.7f, w * 0.08f) // crystal eye
            line(tint, w * 0.5f, h * 0.78f, w * 0.5f, h * 0.88f, sw)
        }
        "beehouse" -> { // trapezoid body with side "ears" + twin holes
            p { moveTo(w * 0.28f, h * 0.28f); lineTo(w * 0.72f, h * 0.28f); lineTo(w * 0.62f, h * 0.66f); lineTo(w * 0.38f, h * 0.66f); close() }
            line(tint, w * 0.28f, h * 0.32f, w * 0.18f, h * 0.36f, sw) // left ear
            line(tint, w * 0.72f, h * 0.32f, w * 0.82f, h * 0.36f, sw) // right ear
            dot(w * 0.44f, h * 0.72f, w * 0.03f); dot(w * 0.56f, h * 0.72f, w * 0.03f)
        }
        "cafec" -> { // flower dripper: wide cone with curved petal ribs + single hole
            p { moveTo(w * 0.22f, h * 0.22f); lineTo(w * 0.78f, h * 0.22f); lineTo(w * 0.56f, h * 0.64f); lineTo(w * 0.44f, h * 0.64f); close() }
            line(tint, w * 0.22f, h * 0.22f, w * 0.78f, h * 0.22f, sw * 1.2f) // reinforced rim
            p { moveTo(w * 0.38f, h * 0.28f); cubicTo(w * 0.42f, h * 0.42f, w * 0.44f, h * 0.56f, w * 0.48f, h * 0.64f) } // left petal
            p { moveTo(w * 0.62f, h * 0.28f); cubicTo(w * 0.58f, h * 0.42f, w * 0.56f, h * 0.56f, w * 0.52f, h * 0.64f) } // right petal
            line(tint, w * 0.5f, h * 0.64f, w * 0.5f, h * 0.78f, sw * 0.7f) // drip
            dot(w * 0.5f, h * 0.66f, w * 0.04f) // center hole
        }

        // ---- Immersion family ----
        "frenchpress" -> { // glass cylinder, plunger rod + knob, side handle
            p { moveTo(w * 0.3f, h * 0.28f); lineTo(w * 0.3f, h * 0.82f); lineTo(w * 0.7f, h * 0.82f); lineTo(w * 0.7f, h * 0.28f) }
            line(tint, w * 0.5f, h * 0.1f, w * 0.5f, h * 0.44f, sw) // plunger rod
            line(tint, w * 0.4f, h * 0.1f, w * 0.6f, h * 0.1f, sw) // knob
            line(tint, w * 0.34f, h * 0.44f, w * 0.66f, h * 0.44f, sw) // plunger disc
            p { moveTo(w * 0.7f, h * 0.4f); cubicTo(w * 0.86f, h * 0.42f, w * 0.86f, h * 0.66f, w * 0.7f, h * 0.66f) } // handle
        }
        "clever" -> { // dripper w/ flat valve base + side grips
            p { moveTo(w * 0.26f, h * 0.28f); lineTo(w * 0.74f, h * 0.28f); lineTo(w * 0.64f, h * 0.66f); lineTo(w * 0.36f, h * 0.66f); close() }
            line(tint, w * 0.24f, h * 0.34f, w * 0.2f, h * 0.34f, sw) // grip
            line(tint, w * 0.76f, h * 0.34f, w * 0.8f, h * 0.34f, sw)
            drawPath(Path().apply { moveTo(w * 0.42f, h * 0.66f); lineTo(w * 0.42f, h * 0.74f); lineTo(w * 0.58f, h * 0.74f); lineTo(w * 0.58f, h * 0.66f) }, tint, style = stroke) // valve box
        }
        "switch" -> { // Clever-like body + a switch lever on the side
            p { moveTo(w * 0.28f, h * 0.26f); lineTo(w * 0.72f, h * 0.26f); lineTo(w * 0.62f, h * 0.64f); lineTo(w * 0.38f, h * 0.64f); close() }
            drawPath(Path().apply { moveTo(w * 0.42f, h * 0.64f); lineTo(w * 0.42f, h * 0.72f); lineTo(w * 0.58f, h * 0.72f); lineTo(w * 0.58f, h * 0.64f) }, tint, style = stroke)
            line(tint, w * 0.72f, h * 0.5f, w * 0.84f, h * 0.44f, sw) // switch lever
            dot(w * 0.84f, h * 0.44f, w * 0.035f)
        }
        "coldbrew" -> { // mason jar / carafe with lid + coarse grounds dots
            drawRoundRect(tint, Offset(w * 0.3f, h * 0.26f), Size(w * 0.4f, h * 0.6f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.06f), style = stroke)
            line(tint, w * 0.36f, h * 0.2f, w * 0.64f, h * 0.2f, sw) // lid
            line(tint, w * 0.36f, h * 0.2f, w * 0.36f, h * 0.26f, sw)
            line(tint, w * 0.64f, h * 0.2f, w * 0.64f, h * 0.26f, sw)
            dot(w * 0.42f, h * 0.68f, w * 0.025f); dot(w * 0.54f, h * 0.74f, w * 0.025f); dot(w * 0.6f, h * 0.62f, w * 0.025f)
        }

        // ---- Pressure family ----
        "aeropress" -> { // short wide chamber + flat plunger disc on top (not a funnel)
            p { moveTo(w * 0.32f, h * 0.36f); lineTo(w * 0.32f, h * 0.78f); lineTo(w * 0.68f, h * 0.78f); lineTo(w * 0.68f, h * 0.36f) }
            line(tint, w * 0.28f, h * 0.36f, w * 0.72f, h * 0.36f, sw) // rim
            line(tint, w * 0.36f, h * 0.24f, w * 0.64f, h * 0.24f, sw) // plunger cap
            line(tint, w * 0.5f, h * 0.24f, w * 0.5f, h * 0.36f, sw) // plunger rod
            line(tint, w * 0.4f, h * 0.78f, w * 0.6f, h * 0.86f, sw * 0.8f) // dripping into cup hint
        }
        "moka" -> { // octagonal two-part body, short spout, side handle
            p { moveTo(w * 0.34f, h * 0.5f); lineTo(w * 0.4f, h * 0.28f); lineTo(w * 0.6f, h * 0.28f); lineTo(w * 0.66f, h * 0.5f); close() } // top chamber
            p { moveTo(w * 0.32f, h * 0.5f); lineTo(w * 0.38f, h * 0.8f); lineTo(w * 0.62f, h * 0.8f); lineTo(w * 0.68f, h * 0.5f); close() } // base chamber
            line(tint, w * 0.6f, h * 0.28f, w * 0.7f, h * 0.22f, sw) // spout
            p { moveTo(w * 0.66f, h * 0.54f); cubicTo(w * 0.82f, h * 0.56f, w * 0.82f, h * 0.74f, w * 0.68f, h * 0.76f) } // handle
        }
        "espresso" -> { // portafilter: group bar + basket + handle + twin spouts
            line(tint, w * 0.24f, h * 0.3f, w * 0.66f, h * 0.3f, sw) // group head bar
            p { moveTo(w * 0.34f, h * 0.3f); lineTo(w * 0.4f, h * 0.56f); lineTo(w * 0.56f, h * 0.56f); lineTo(w * 0.62f, h * 0.3f) } // basket
            line(tint, w * 0.66f, h * 0.3f, w * 0.86f, h * 0.26f, sw) // handle
            line(tint, w * 0.44f, h * 0.56f, w * 0.44f, h * 0.68f, sw) // twin spouts
            line(tint, w * 0.52f, h * 0.56f, w * 0.52f, h * 0.68f, sw)
        }

        // ---- Other / cultural ----
        "turkish" -> { // cezve: narrow-neck pot with long straight handle
            p { moveTo(w * 0.34f, h * 0.34f); lineTo(w * 0.32f, h * 0.72f); cubicTo(w * 0.32f, h * 0.82f, w * 0.58f, h * 0.82f, w * 0.58f, h * 0.72f); lineTo(w * 0.56f, h * 0.34f) }
            p { moveTo(w * 0.34f, h * 0.34f); cubicTo(w * 0.4f, h * 0.28f, w * 0.5f, h * 0.28f, w * 0.56f, h * 0.34f) } // pouring lip
            line(tint, w * 0.58f, h * 0.4f, w * 0.86f, h * 0.32f, sw) // long handle
        }
        "ibrik" -> { // ibrik: wider-base pot with curved wood handle + prominent spout
            p { moveTo(w * 0.28f, h * 0.3f); lineTo(w * 0.24f, h * 0.7f); cubicTo(w * 0.24f, h * 0.84f, w * 0.6f, h * 0.84f, w * 0.6f, h * 0.7f); lineTo(w * 0.56f, h * 0.3f) }
            p { moveTo(w * 0.26f, h * 0.3f); lineTo(w * 0.5f, h * 0.24f); lineTo(w * 0.54f, h * 0.3f) } // triangular spout lip
            p { moveTo(w * 0.58f, h * 0.46f); cubicTo(w * 0.74f, h * 0.4f, w * 0.82f, h * 0.54f, w * 0.76f, h * 0.58f); cubicTo(w * 0.7f, h * 0.62f, w * 0.66f, h * 0.56f, w * 0.6f, h * 0.54f) } // curved handle loop
            line(tint, w * 0.64f, h * 0.28f, w * 0.74f, h * 0.32f, sw * 0.8f) // handle top hook
        }
        "phin" -> { // Vietnamese phin: chamber + domed press lid on a cup/saucer
            p { moveTo(w * 0.36f, h * 0.34f); lineTo(w * 0.36f, h * 0.58f); lineTo(w * 0.64f, h * 0.58f); lineTo(w * 0.64f, h * 0.34f) } // chamber
            drawArc(tint, 180f, 180f, false, topLeft = Offset(w * 0.4f, h * 0.24f), size = Size(w * 0.2f, h * 0.16f), style = stroke) // dome lid
            line(tint, w * 0.5f, h * 0.18f, w * 0.5f, h * 0.24f, sw) // lid knob
            p { moveTo(w * 0.3f, h * 0.62f); cubicTo(w * 0.32f, h * 0.78f, w * 0.68f, h * 0.78f, w * 0.7f, h * 0.62f); close() } // cup below
        }
        "colddrip" -> { // Kyoto tower: top water globe, drip valve, bottom carafe
            dot(w * 0.5f, h * 0.24f, w * 0.13f) // upper water globe
            line(tint, w * 0.5f, h * 0.37f, w * 0.5f, h * 0.5f, sw) // drip stem
            dot(w * 0.5f, h * 0.5f, w * 0.02f) // droplet
            p { moveTo(w * 0.4f, h * 0.58f); lineTo(w * 0.36f, h * 0.84f); lineTo(w * 0.64f, h * 0.84f); lineTo(w * 0.6f, h * 0.58f); close() } // carafe
            line(tint, w * 0.3f, h * 0.56f, w * 0.7f, h * 0.56f, sw * 0.8f) // frame shelf
        }
        "percolator" -> { // pot with glass bubble knob on lid, spout + handle
            p { moveTo(w * 0.34f, h * 0.34f); lineTo(w * 0.3f, h * 0.8f); lineTo(w * 0.62f, h * 0.8f); lineTo(w * 0.58f, h * 0.34f); close() }
            line(tint, w * 0.34f, h * 0.34f, w * 0.58f, h * 0.34f, sw) // lid
            dot(w * 0.46f, h * 0.26f, w * 0.05f) // glass bubble knob
            line(tint, w * 0.58f, h * 0.4f, w * 0.7f, h * 0.34f, sw) // spout
            p { moveTo(w * 0.62f, h * 0.44f); cubicTo(w * 0.8f, h * 0.46f, w * 0.8f, h * 0.68f, w * 0.62f, h * 0.7f) } // handle
        }
        "batchbrew" -> { // drip machine: body + basket on top + carafe under
            p { moveTo(w * 0.24f, h * 0.2f); lineTo(w * 0.24f, h * 0.58f); lineTo(w * 0.44f, h * 0.58f); lineTo(w * 0.44f, h * 0.2f); close() } // machine tower
            line(tint, w * 0.44f, h * 0.28f, w * 0.72f, h * 0.28f, sw) // top arm
            p { moveTo(w * 0.58f, h * 0.28f); lineTo(w * 0.62f, h * 0.44f); lineTo(w * 0.74f, h * 0.44f); lineTo(w * 0.78f, h * 0.28f); close() } // basket
            p { moveTo(w * 0.56f, h * 0.56f); lineTo(w * 0.58f, h * 0.78f); lineTo(w * 0.8f, h * 0.78f); lineTo(w * 0.82f, h * 0.56f); close() } // carafe
            p { moveTo(w * 0.8f, h * 0.6f); cubicTo(w * 0.9f, h * 0.62f, w * 0.9f, h * 0.72f, w * 0.8f, h * 0.72f) } // carafe handle
        }
        "napoletana" -> { // two-part flip pot: cylinder split mid + top handle + spout
            p { moveTo(w * 0.36f, h * 0.28f); lineTo(w * 0.36f, h * 0.78f); lineTo(w * 0.64f, h * 0.78f); lineTo(w * 0.64f, h * 0.28f) }
            line(tint, w * 0.36f, h * 0.53f, w * 0.64f, h * 0.53f, sw) // split seam
            line(tint, w * 0.36f, h * 0.28f, w * 0.3f, h * 0.22f, sw) // spout
            p { moveTo(w * 0.5f, h * 0.28f); cubicTo(w * 0.5f, h * 0.14f, w * 0.74f, h * 0.14f, w * 0.72f, h * 0.28f) } // top arc handle
        }
        "siphon" -> { // two stacked glass globes on a stand
            dot(w * 0.5f, h * 0.32f, w * 0.16f) // upper bulb
            p { moveTo(w * 0.42f, h * 0.46f); lineTo(w * 0.42f, h * 0.58f); cubicTo(w * 0.3f, h * 0.66f, w * 0.3f, h * 0.82f, w * 0.5f, h * 0.82f); cubicTo(w * 0.7f, h * 0.82f, w * 0.7f, h * 0.66f, w * 0.58f, h * 0.58f); lineTo(w * 0.58f, h * 0.46f) } // lower bulb
            line(tint, w * 0.5f, h * 0.46f, w * 0.5f, h * 0.5f, sw) // connector
        }
        "cowboy" -> { // pot with handle, campfire beneath, steam rising
            p { moveTo(w * 0.3f, h * 0.32f); lineTo(w * 0.26f, h * 0.62f); cubicTo(w * 0.26f, h * 0.72f, w * 0.54f, h * 0.72f, w * 0.54f, h * 0.62f); lineTo(w * 0.5f, h * 0.32f) }
            p { moveTo(w * 0.3f, h * 0.32f); cubicTo(w * 0.35f, h * 0.26f, w * 0.45f, h * 0.26f, w * 0.5f, h * 0.32f) }
            line(tint, w * 0.54f, h * 0.4f, w * 0.74f, h * 0.32f, sw) // handle
            p { moveTo(w * 0.4f, h * 0.76f); lineTo(w * 0.5f, h * 0.94f); lineTo(w * 0.6f, h * 0.76f); close() } // campfire
            line(tint, w * 0.38f, h * 0.2f, w * 0.36f, h * 0.14f, sw * 0.7f) // steam left
            line(tint, w * 0.5f, h * 0.18f, w * 0.5f, h * 0.1f, sw * 0.7f) // steam middle
            line(tint, w * 0.42f, h * 0.16f, w * 0.43f, h * 0.08f, sw * 0.7f) // steam right
        }
        "cupping" -> { // wide bowl with spoon
            p { moveTo(w * 0.18f, h * 0.34f); lineTo(w * 0.2f, h * 0.7f); cubicTo(w * 0.2f, h * 0.8f, w * 0.8f, h * 0.8f, w * 0.8f, h * 0.7f); lineTo(w * 0.82f, h * 0.34f) }
            line(tint, w * 0.82f, h * 0.44f, w * 0.96f, h * 0.26f, sw) // spoon handle
            dot(w * 0.94f, h * 0.22f, w * 0.04f) // spoon bowl
        }
        "cloth" -> { // cloth draped over container
            p { moveTo(w * 0.36f, h * 0.58f); lineTo(w * 0.64f, h * 0.58f); lineTo(w * 0.6f, h * 0.82f); lineTo(w * 0.4f, h * 0.82f); close() } // container
            p { moveTo(w * 0.3f, h * 0.46f); lineTo(w * 0.7f, h * 0.46f); lineTo(w * 0.66f, h * 0.6f); lineTo(w * 0.34f, h * 0.6f); close() } // cloth top
            line(tint, w * 0.36f, h * 0.5f, w * 0.64f, h * 0.54f, sw * 0.6f) // weave h1
            line(tint, w * 0.34f, h * 0.54f, w * 0.66f, h * 0.58f, sw * 0.6f) // weave h2
            line(tint, w * 0.38f, h * 0.48f, w * 0.42f, h * 0.6f, sw * 0.6f) // weave v1
            line(tint, w * 0.5f, h * 0.47f, w * 0.5f, h * 0.59f, sw * 0.6f) // weave v2
            line(tint, w * 0.6f, h * 0.48f, w * 0.58f, h * 0.6f, sw * 0.6f) // weave v3
        }
        "sock" -> { // sock-shaped filter bag on wire ring
            p { moveTo(w * 0.34f, h * 0.28f); cubicTo(w * 0.22f, h * 0.3f, w * 0.2f, h * 0.38f, w * 0.26f, h * 0.4f); cubicTo(w * 0.3f, h * 0.42f, w * 0.28f, h * 0.48f, w * 0.24f, h * 0.56f); cubicTo(w * 0.2f, h * 0.64f, w * 0.22f, h * 0.76f, w * 0.3f, h * 0.82f); cubicTo(w * 0.38f, h * 0.88f, w * 0.48f, h * 0.88f, w * 0.56f, h * 0.84f); cubicTo(w * 0.64f, h * 0.8f, w * 0.7f, h * 0.72f, w * 0.68f, h * 0.6f); cubicTo(w * 0.66f, h * 0.5f, w * 0.6f, h * 0.44f, w * 0.54f, h * 0.38f); cubicTo(w * 0.48f, h * 0.32f, w * 0.38f, h * 0.28f, w * 0.34f, h * 0.28f) } // sock bag
            line(tint, w * 0.2f, h * 0.24f, w * 0.7f, h * 0.24f, sw) // wire ring top
            line(tint, w * 0.2f, h * 0.24f, w * 0.26f, h * 0.4f, sw * 0.7f) // left ring connection
            line(tint, w * 0.7f, h * 0.24f, w * 0.62f, h * 0.4f, sw * 0.7f) // right ring connection
            dot(w * 0.2f, h * 0.24f, w * 0.025f) // hook left
        }
        "decoction" -> { // pot with boiling bubbles and spoon
            p { moveTo(w * 0.3f, h * 0.3f); lineTo(w * 0.26f, h * 0.66f); cubicTo(w * 0.26f, h * 0.78f, w * 0.56f, h * 0.78f, w * 0.56f, h * 0.66f); lineTo(w * 0.52f, h * 0.3f) }
            p { moveTo(w * 0.3f, h * 0.3f); cubicTo(w * 0.36f, h * 0.24f, w * 0.46f, h * 0.24f, w * 0.52f, h * 0.3f) }
            line(tint, w * 0.56f, h * 0.42f, w * 0.8f, h * 0.36f, sw) // spoon handle
            dot(w * 0.82f, h * 0.34f, w * 0.04f) // spoon bowl
            dot(w * 0.38f, h * 0.54f, w * 0.03f); dot(w * 0.48f, h * 0.6f, w * 0.035f); dot(w * 0.44f, h * 0.48f, w * 0.025f) // bubbles
        }
        "papertowel" -> { // funnel with cross-hatched paper texture
            p { moveTo(w * 0.24f, h * 0.28f); lineTo(w * 0.76f, h * 0.28f); lineTo(w * 0.56f, h * 0.72f); lineTo(w * 0.44f, h * 0.72f); close() } // funnel
            line(tint, w * 0.5f, h * 0.72f, w * 0.5f, h * 0.84f, sw) // drip
            line(tint, w * 0.34f, h * 0.38f, w * 0.66f, h * 0.62f, sw * 0.5f) // hatch1
            line(tint, w * 0.66f, h * 0.38f, w * 0.34f, h * 0.62f, sw * 0.5f) // hatch2
            line(tint, w * 0.3f, h * 0.44f, w * 0.7f, h * 0.68f, sw * 0.5f) // hatch3
            line(tint, w * 0.7f, h * 0.44f, w * 0.3f, h * 0.68f, sw * 0.5f) // hatch4
        }
        "egg" -> { // egg cracked into pot
            p { moveTo(w * 0.32f, h * 0.4f); lineTo(w * 0.28f, h * 0.72f); cubicTo(w * 0.28f, h * 0.84f, w * 0.62f, h * 0.84f, w * 0.62f, h * 0.72f); lineTo(w * 0.58f, h * 0.4f) }
            line(tint, w * 0.32f, h * 0.4f, w * 0.58f, h * 0.4f, sw) // pot rim
            p { moveTo(w * 0.4f, h * 0.18f); cubicTo(w * 0.36f, h * 0.28f, w * 0.3f, h * 0.34f, w * 0.38f, h * 0.46f); cubicTo(w * 0.46f, h * 0.34f, w * 0.6f, h * 0.34f, w * 0.56f, h * 0.18f); cubicTo(w * 0.5f, h * 0.1f, w * 0.46f, h * 0.1f, w * 0.4f, h * 0.18f) } // egg white
            dot(w * 0.47f, h * 0.28f, w * 0.035f) // yolk
            dot(w * 0.4f, h * 0.58f, w * 0.02f); dot(w * 0.52f, h * 0.64f, w * 0.02f) // grounds
        }
        "improvturk" -> { // small saucepan with handle and fine grounds
            p { moveTo(w * 0.32f, h * 0.34f); lineTo(w * 0.3f, h * 0.66f); cubicTo(w * 0.3f, h * 0.76f, w * 0.56f, h * 0.76f, w * 0.56f, h * 0.66f); lineTo(w * 0.56f, h * 0.34f) }
            p { moveTo(w * 0.32f, h * 0.34f); cubicTo(w * 0.37f, h * 0.28f, w * 0.49f, h * 0.28f, w * 0.56f, h * 0.34f) }
            line(tint, w * 0.56f, h * 0.44f, w * 0.8f, h * 0.36f, sw) // long handle
            dot(w * 0.38f, h * 0.6f, w * 0.018f); dot(w * 0.44f, h * 0.66f, w * 0.018f)
            dot(w * 0.5f, h * 0.58f, w * 0.018f); dot(w * 0.46f, h * 0.7f, w * 0.018f)
            dot(w * 0.36f, h * 0.7f, w * 0.018f) // fine grounds
        }
        "kopitubruk" -> { // drinking glass with mud layer at bottom and steam
            p { moveTo(w * 0.3f, h * 0.28f); lineTo(w * 0.3f, h * 0.7f); cubicTo(w * 0.3f, h * 0.82f, w * 0.7f, h * 0.82f, w * 0.7f, h * 0.7f); lineTo(w * 0.7f, h * 0.28f) }
            p { moveTo(w * 0.3f, h * 0.6f); cubicTo(w * 0.4f, h * 0.56f, w * 0.5f, h * 0.64f, w * 0.6f, h * 0.58f); cubicTo(w * 0.5f, h * 0.7f, w * 0.4f, h * 0.68f, w * 0.3f, h * 0.6f) }
            line(tint, w * 0.3f, h * 0.56f, w * 0.7f, h * 0.56f, sw * 0.6f)
            line(tint, w * 0.34f, h * 0.18f, w * 0.38f, h * 0.1f, sw * 0.7f)
            line(tint, w * 0.5f, h * 0.14f, w * 0.5f, h * 0.04f, sw * 0.7f)
            line(tint, w * 0.6f, h * 0.18f, w * 0.63f, h * 0.08f, sw * 0.7f)
        }
        "qahwa" -> { // Arabian dallah with long curved spout and handle
            p { moveTo(w * 0.38f, h * 0.24f); cubicTo(w * 0.28f, h * 0.36f, w * 0.28f, h * 0.7f, w * 0.5f, h * 0.78f); cubicTo(w * 0.7f, h * 0.7f, w * 0.62f, h * 0.36f, w * 0.54f, h * 0.24f) }
            p { moveTo(w * 0.5f, h * 0.24f); cubicTo(w * 0.54f, h * 0.14f, w * 0.76f, h * 0.06f, w * 0.8f, h * 0.16f); cubicTo(w * 0.84f, h * 0.26f, w * 0.7f, h * 0.3f, w * 0.6f, h * 0.28f) }
            p { moveTo(w * 0.22f, h * 0.44f); cubicTo(w * 0.16f, h * 0.5f, w * 0.16f, h * 0.62f, w * 0.26f, h * 0.62f); cubicTo(w * 0.14f, h * 0.62f, w * 0.14f, h * 0.5f, w * 0.22f, h * 0.44f) }
            dot(w * 0.38f, h * 0.18f, w * 0.03f)
        }
        "cafedeolla" -> { // clay pot with handles and cinnamon stick
            p { moveTo(w * 0.34f, h * 0.34f); lineTo(w * 0.3f, h * 0.6f); cubicTo(w * 0.3f, h * 0.8f, w * 0.7f, h * 0.8f, w * 0.7f, h * 0.6f); lineTo(w * 0.66f, h * 0.34f) }
            p { moveTo(w * 0.34f, h * 0.34f); cubicTo(w * 0.4f, h * 0.28f, w * 0.6f, h * 0.28f, w * 0.66f, h * 0.34f) }
            p { moveTo(w * 0.3f, h * 0.46f); cubicTo(w * 0.18f, h * 0.42f, w * 0.2f, h * 0.56f, w * 0.3f, h * 0.54f) }
            p { moveTo(w * 0.7f, h * 0.46f); cubicTo(w * 0.82f, h * 0.42f, w * 0.8f, h * 0.56f, w * 0.7f, h * 0.54f) }
            line(tint, w * 0.5f, h * 0.34f, w * 0.62f, h * 0.14f, sw * 0.8f)
            line(tint, w * 0.62f, h * 0.14f, w * 0.68f, h * 0.1f, sw * 0.6f)
        }
        "masonjar" -> { // mason jar with screw threads and grounds
            p { moveTo(w * 0.32f, h * 0.3f); lineTo(w * 0.3f, h * 0.7f); cubicTo(w * 0.3f, h * 0.82f, w * 0.7f, h * 0.82f, w * 0.7f, h * 0.7f); lineTo(w * 0.68f, h * 0.3f) }
            line(tint, w * 0.28f, h * 0.3f, w * 0.72f, h * 0.3f, sw)
            line(tint, w * 0.31f, h * 0.34f, w * 0.69f, h * 0.34f, sw * 0.6f)
            line(tint, w * 0.31f, h * 0.38f, w * 0.69f, h * 0.38f, sw * 0.6f)
            line(tint, w * 0.31f, h * 0.42f, w * 0.69f, h * 0.42f, sw * 0.6f)
            dot(w * 0.4f, h * 0.68f, w * 0.02f); dot(w * 0.55f, h * 0.72f, w * 0.02f)
            dot(w * 0.5f, h * 0.64f, w * 0.02f); dot(w * 0.6f, h * 0.66f, w * 0.02f)
            dot(w * 0.44f, h * 0.74f, w * 0.02f)
        }

        // ---- Custom gear category icons (proper equipment, not stubs) ----
        "cat_POUR_OVER" -> { // dripper over carafe — ridged cone + glass server + drip
            p { moveTo(w * 0.24f, h * 0.22f); lineTo(w * 0.76f, h * 0.22f); lineTo(w * 0.58f, h * 0.52f); lineTo(w * 0.42f, h * 0.52f); close() }
            line(tint, w * 0.34f, h * 0.28f, w * 0.42f, h * 0.52f, sw * 0.6f)
            line(tint, w * 0.5f, h * 0.26f, w * 0.5f, h * 0.52f, sw * 0.6f)
            line(tint, w * 0.66f, h * 0.28f, w * 0.58f, h * 0.52f, sw * 0.6f)
            p { moveTo(w * 0.32f, h * 0.56f); lineTo(w * 0.3f, h * 0.78f); cubicTo(w * 0.3f, h * 0.88f, w * 0.7f, h * 0.88f, w * 0.7f, h * 0.78f); lineTo(w * 0.68f, h * 0.56f) }
            line(tint, w * 0.3f, h * 0.54f, w * 0.7f, h * 0.54f, sw * 0.7f)
            line(tint, w * 0.78f, h * 0.6f, w * 0.84f, h * 0.6f, sw * 0.8f)
            line(tint, w * 0.5f, h * 0.52f, w * 0.5f, h * 0.56f, sw)
        }
        "cat_IMMERSION" -> { // French press style — glass body + plunger + side handle + grounds
            p { moveTo(w * 0.3f, h * 0.26f); lineTo(w * 0.3f, h * 0.78f); lineTo(w * 0.7f, h * 0.78f); lineTo(w * 0.7f, h * 0.26f) }
            line(tint, w * 0.5f, h * 0.1f, w * 0.5f, h * 0.42f, sw)
            line(tint, w * 0.38f, h * 0.1f, w * 0.62f, h * 0.1f, sw)
            line(tint, w * 0.34f, h * 0.42f, w * 0.66f, h * 0.42f, sw)
            p { moveTo(w * 0.7f, h * 0.4f); cubicTo(w * 0.86f, h * 0.42f, w * 0.86f, h * 0.64f, w * 0.7f, h * 0.62f) }
            dot(w * 0.42f, h * 0.68f, w * 0.018f); dot(w * 0.55f, h * 0.72f, w * 0.018f)
            dot(w * 0.5f, h * 0.64f, w * 0.018f); dot(w * 0.6f, h * 0.7f, w * 0.018f)
        }
        "cat_PRESSURE" -> { // portafilter detail — group head + basket + handle + twin spouts + cup
            line(tint, w * 0.26f, h * 0.24f, w * 0.74f, h * 0.24f, sw)
            p { moveTo(w * 0.36f, h * 0.24f); lineTo(w * 0.4f, h * 0.48f); lineTo(w * 0.6f, h * 0.48f); lineTo(w * 0.64f, h * 0.24f) }
            line(tint, w * 0.74f, h * 0.24f, w * 0.88f, h * 0.2f, sw)
            line(tint, w * 0.46f, h * 0.48f, w * 0.46f, h * 0.56f, sw)
            line(tint, w * 0.54f, h * 0.48f, w * 0.54f, h * 0.56f, sw)
            p { moveTo(w * 0.38f, h * 0.64f); cubicTo(w * 0.36f, h * 0.76f, w * 0.64f, h * 0.76f, w * 0.62f, h * 0.64f); close() }
            p { moveTo(w * 0.76f, h * 0.68f); cubicTo(w * 0.8f, h * 0.7f, w * 0.8f, h * 0.74f, w * 0.76f, h * 0.74f) }
        }
        else -> { // OTHER — elegant coffee cup with saucer + rising steam
            p { moveTo(w * 0.35f, h * 0.62f); cubicTo(w * 0.37f, h * 0.74f, w * 0.63f, h * 0.74f, w * 0.65f, h * 0.62f); close() }
            line(tint, w * 0.3f, h * 0.62f, w * 0.7f, h * 0.62f, sw)
            p { moveTo(w * 0.68f, h * 0.48f); cubicTo(w * 0.7f, h * 0.58f, w * 0.7f, h * 0.62f, w * 0.65f, h * 0.62f); cubicTo(w * 0.62f, h * 0.5f, w * 0.38f, h * 0.5f, w * 0.35f, h * 0.62f); cubicTo(w * 0.3f, h * 0.62f, w * 0.3f, h * 0.58f, w * 0.32f, h * 0.48f); close() }
            p { moveTo(w * 0.2f, h * 0.78f); cubicTo(w * 0.3f, h * 0.86f, w * 0.7f, h * 0.86f, w * 0.8f, h * 0.78f); cubicTo(w * 0.7f, h * 0.84f, w * 0.3f, h * 0.84f, w * 0.2f, h * 0.78f); close() }
            line(tint, w * 0.42f, h * 0.42f, w * 0.48f, h * 0.32f, sw * 0.7f)
            line(tint, w * 0.54f, h * 0.4f, w * 0.56f, h * 0.28f, sw * 0.6f)
            line(tint, w * 0.5f, h * 0.36f, w * 0.5f, h * 0.22f, sw * 0.6f)
        }
    }
}
