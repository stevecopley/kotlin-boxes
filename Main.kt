/***************************************************************************************
 * Console Text Boxes for Kotlin
 * Steve Copley 2022
 * https://github.com/stevecopley/kotlin-boxes
 ***************************************************************************************/

fun main() {
    testBoxes()
}


/***************************************************************************************
 * See how String and Collection boxes look and work
 */
fun testBoxes() {
    // Some test data to use
    val testText = "Hello World!"
    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    val numbers = mutableListOf<Int>()
    for (i in 1..10)
        numbers.add(i * i * i)

    // Test Strings -------------------------------------------------------------------
    println(testText.box())
    println(testText.box(Box.TEXT_BORDER))
    println(testText.box(Box.THIN_BORDER))
    println(testText.box(Box.THICK_BORDER))
    println(testText.box(Box.DOUBLE_BORDER))
    println(testText.box(width = 20))
    println(testText.box(width = 20, align = Box.LEFT_ALIGN))
    println(testText.box(width = 20, align = Box.RIGHT_ALIGN))

    // Test Collections in columns ----------------------------------------------------
    println(days.column())
    println(days.column(align = Box.LEFT_ALIGN))
    println(days.column(align = Box.RIGHT_ALIGN))
    println(days.column(padding = 0))
    println(days.column(padding = 3))
    println(days.column(width = 20))

    // Test Collections in rows -------------------------------------------------------
    println(days.row(style = Box.THICK_BORDER))
    println(days.row(style = Box.THICK_BORDER, width = Box.FIT_WIDTH))
    println(days.row(style = Box.THICK_BORDER, width = Box.FIT_WIDTH, padding = 0))
    println(days.row(style = Box.THICK_BORDER, width = Box.FIT_WIDTH, padding = 2))
    println(days.row(style = Box.THICK_BORDER, width = 15))

    println(numbers.row(style = Box.DOUBLE_BORDER))
    println(numbers.row(style = Box.DOUBLE_BORDER, align = Box.LEFT_ALIGN))
    println(numbers.row(style = Box.DOUBLE_BORDER, align = Box.RIGHT_ALIGN))
    println(numbers.row(style = Box.DOUBLE_BORDER, align = Box.RIGHT_ALIGN, width = Box.FIT_WIDTH))
    println(numbers.row(style = Box.DOUBLE_BORDER, align = Box.RIGHT_ALIGN, width = Box.FIT_WIDTH, padding = 0))
    println(numbers.row(style = Box.DOUBLE_BORDER, align = Box.RIGHT_ALIGN, width = Box.FIT_WIDTH, padding = 3))
}