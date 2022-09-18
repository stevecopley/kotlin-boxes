/***************************************************************************************
 * Console Text Boxes for Kotlin
 * Steve Copley 2022
 * https://github.com/stevecopley/kotlin-boxes
 ***************************************************************************************
 * Box Drawing Characters
 * See https://en.wikipedia.org/wiki/Box_Drawing for complete set
 * ┌────────┬────────┐
 * │  Light │ Lines  │
 * ├────────┼────────┤
 * │        │        │
 * └────────┴────────┘
 * ┏━━━━━━━━┳━━━━━━━━┓
 * ┃  Heavy ┃ Lines  ┃
 * ┣━━━━━━━━╋━━━━━━━━┫
 * ┃        ┃        ┃
 * ┗━━━━━━━━┻━━━━━━━━┛
 * ╔════════╦════════╗
 * ║ Double ║ Lines  ║
 * ╠════════╬════════╣
 * ║        ║        ║
 * ╚════════╩════════╝
 ***************************************************************************************/


// Define some constants to use when specifying style, alignment, etc.
//--------------------------------------------------------------------------------------
abstract class Box {
    companion object {
        const val TEXT_BORDER = 0
        const val THIN_BORDER = 1
        const val THICK_BORDER = 2
        const val DOUBLE_BORDER = 3

        const val FIT_WIDTH = 0
        const val MAX_WIDTH = 1

        const val LEFT_ALIGN = 0
        const val CENTRE_ALIGN = 1
        const val CENTER_ALIGN = 1
        const val RIGHT_ALIGN = 2
    }
}

// character set for different box styles
//--------------------------------------------------------------------------------------
private val borders = mapOf(
    Box.TEXT_BORDER to "+++++++++-|",
    Box.THIN_BORDER to "┌┬┐├┼┤└┴┘─│",
    Box.THICK_BORDER to "┏┳┓┣╋┫┗┻┛━┃",
    Box.DOUBLE_BORDER to "╔╦╗╠╬╣╚╩╝═║"
)

// indexes into above
//--------------------------------------------------------------------------------------
private const val topLeft = 0
private const val topJoin = 1
private const val topRight = 2
private const val leftJoin = 3
private const val centre = 4
private const val rightJoin = 5
private const val bottomLeft = 6
private const val bottomJoin = 7
private const val bottomRight = 8
private const val top = 9
private const val side = 10


/**
 * Show a given String within a box, optionally specifying:
 *  - style : THIN, THICK, DOUBLE, TEXT
 *  - width : box width, will be over-ridden if contents (inc. padding) don't fit
 *  - align : LEFT, CENTRE/CENTER, RIGHT
 *  - padding : spaces to add to left/right of text
 */
fun String.box(
    style: Int = Box.THIN_BORDER,
    width: Int = Box.FIT_WIDTH,
    align: Int = Box.CENTRE_ALIGN,
    padding: Int = 1
): String {
    val type = if (style in borders.keys) style else Box.THIN_BORDER
    val borderSet = borderSet(type)

    val minWidth = this.length + padding * 2
    val requiredWidth = if (width < minWidth) minWidth else width

    val topBorder = topBorder(requiredWidth, type)
    val bottomBorder = bottomBorder(requiredWidth, type)

    val textAndSides = borderSet.get(side) + paddedText(this, requiredWidth, align, padding) + borderSet.get(side)

    return "$topBorder\n$textAndSides\n$bottomBorder"
}

/**
 * Show a given Collection within a row of boxes, optionally specifying:
 *  - style : THIN, THICK, DOUBLE, TEXT
 *  - width : cell width, will be over-ridden if contents (inc. padding) don't fit
 *            or FIT (sized to each value) or MAX (sized to longest value)
 *  - align : LEFT, CENTRE/CENTER, RIGHT
 *  - padding : spaces to add to left/right of text
 */
fun <T> Collection<T>.row(
    style: Int = Box.THIN_BORDER,
    width: Int = Box.MAX_WIDTH,
    align: Int = Box.CENTRE_ALIGN,
    padding: Int = 1
): String {
    val type = if (style in borders.keys) style else Box.THIN_BORDER
    val borderSet = borderSet(type)
    val maxWidth = maxWidth(this) + padding * 2

    val requiredWidths = mutableListOf<Int>()
    for (item in this) {
        val requiredWidth = when {
            width == Box.FIT_WIDTH -> item.toString().length + padding * 2
            width > maxWidth -> width
            else -> maxWidth
        }
        requiredWidths.add(requiredWidth)
    }

    var topBorder = ""
    var bottomBorder = ""
    var textAndSides = ""
    for (item in this) {
        val itemWidth = requiredWidths.get(this.indexOf(item))
        val horizEdge = horizEdge(borderSet.get(top), itemWidth)

        topBorder += if (item == this.first()) borderSet.get(topLeft) else borderSet.get(topJoin)
        topBorder += horizEdge
        bottomBorder += if (item == this.first()) borderSet.get(bottomLeft) else borderSet.get(bottomJoin)
        bottomBorder += horizEdge

        textAndSides += borderSet.get(side)
        textAndSides += paddedText(item.toString(), itemWidth, align, padding)
    }
    topBorder += borderSet.get(topRight)
    bottomBorder += borderSet.get(bottomRight)
    textAndSides += borderSet.get(side)

    return "$topBorder\n$textAndSides\n$bottomBorder"
}


/**
 * Show a given Collection within a column of boxes, optionally specifying:
 *  - style : THIN, THICK, DOUBLE, TEXT
 *  - width : column width, will be over-ridden if contents (inc. padding) don't fit
 *            or MAX (sized to longest value)
 *  - align : LEFT, CENTRE/CENTER, RIGHT
 *  - padding : spaces to add to left/right of text
 */
fun <T> Collection<T>.column(
    style: Int = Box.THIN_BORDER,
    width: Int = Box.MAX_WIDTH,
    align: Int = Box.CENTRE_ALIGN,
    padding: Int = 1
): String {
    val type = if (style in borders.keys) style else Box.THIN_BORDER
    val borderSet = borderSet(type)
    val maxWidth = maxWidth(this) + padding * 2
    val requiredWidth = if (width > maxWidth) width else maxWidth

    val topBorder = topBorder(requiredWidth, type)
    val midBorder = midBorder(requiredWidth, type)
    val bottomBorder = bottomBorder(requiredWidth, type)

    var output = "$topBorder\n"
    for (item in this) {
        output += borderSet.get(side)
        output += paddedText(item.toString(), requiredWidth, align, padding)
        output += borderSet.get(side) + "\n"
        if (item != this.last()) output += "$midBorder\n"
    }
    output += bottomBorder

    return output
}


//--------------------------------------------------------------------------------------
// Utility functions
//--------------------------------------------------------------------------------------


// Determine correct border set to use, defaulting to text if invalid type given
//--------------------------------------------------------------------------------------
private fun borderSet(type: Int): String {
    return borders[type] ?: borders[Box.TEXT_BORDER]!!
}

// Determine max width of items in a give collection, when shown as Strings
//--------------------------------------------------------------------------------------
private fun <T> maxWidth(collection: Collection<T>): Int {
    var maxWidth = 0
    for (item in collection) {
        val len = item.toString().length
        if (len > maxWidth) maxWidth = len
    }
    return maxWidth
}

// Generate a top/mid/bottom edge of a given character and length
//--------------------------------------------------------------------------------------
private fun horizEdge(symbol: Char, width: Int): String {
    return symbol.toString().repeat(width)
}

// Generate a top border, consisting of corners and an edge between
//--------------------------------------------------------------------------------------
private fun topBorder(width: Int, type: Int): String {
    val borderSet = borderSet(type)
    return borderSet.get(topLeft) + horizEdge(borderSet.get(top), width) + borderSet.get(topRight)
}

// Generate a middle border, consisting of joins and an edge between
//--------------------------------------------------------------------------------------
private fun midBorder(width: Int, type: Int): String {
    val borderSet = borderSet(type)
    return borderSet.get(leftJoin) + horizEdge(borderSet.get(top), width) + borderSet.get(rightJoin)
}

// Generate a bottom border, consisting of corners and an edge between
//--------------------------------------------------------------------------------------
private fun bottomBorder(width: Int, type: Int): String {
    val borderSet = borderSet(type)
    return borderSet.get(bottomLeft) + horizEdge(borderSet.get(top), width) + borderSet.get(bottomRight)

}

// Generate text padded to fit a required width and alignment, and/or with given padding
//--------------------------------------------------------------------------------------
private fun paddedText(text: String, width: Int, align: Int, padding: Int): String {
    val itemWidth = text.length + padding * 2
    val additionalPadding = (width - itemWidth).coerceAtLeast(0)
    var padFront = 0
    var padBack = 0

    when (align) {
        Box.LEFT_ALIGN -> padBack = additionalPadding
        Box.RIGHT_ALIGN -> padFront = additionalPadding
        else -> {
            padFront = additionalPadding / 2
            padBack = if (additionalPadding % 2 == 0) padFront else padFront + 1
        }
    }

    return " ".repeat(padding + padFront) + text + " ".repeat(padding + padBack)
}

