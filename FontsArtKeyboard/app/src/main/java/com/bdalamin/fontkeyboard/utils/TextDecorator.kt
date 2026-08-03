package com.bdalamin.fontkeyboard.utils

object TextDecorator {

    fun toZalgo(text: String, intensity: Int = 2): String {
        val zalgoUp = listOf(
            '\u030d', '\u030e', '\u0304', '\u0305', '\u033f', '\u0311', '\u0306',
            '\u0310', '\u0352', '\u0357', '\u0351', '\u0307', '\u0308', '\u030a',
            '\u0342', '\u0343', '\u0344', '\u034a', '\u034b', '\u034c', '\u0303',
            '\u0302', '\u030c', '\u0350', '\u0300', '\u0301', '\u030b', '\u030f',
            '\u0312', '\u0313', '\u0314', '\u033d', '\u0309', '\u0363', '\u0364',
            '\u0365', '\u0366', '\u0367', '\u0368', '\u0369', '\u036a', '\u036b',
            '\u036c', '\u036d', '\u036e', '\u036f', '\u033e', '\u035b', '\u0346',
            '\u031a'
        )
        val zalgoMid = listOf(
            '\u0315', '\u031b', '\u0340', '\u0341', '\u0358', '\u0321', '\u0322',
            '\u0327', '\u0328', '\u0334', '\u0335', '\u0336', '\u034f', '\u035c',
            '\u035d', '\u035e', '\u035f', '\u0360', '\u0362', '\u0338', '\u0337',
            '\u0361', '\u0489'
        )
        val zalgoDown = listOf(
            '\u0316', '\u0317', '\u0318', '\u0319', '\u031c', '\u031d', '\u031e',
            '\u031f', '\u0320', '\u0324', '\u0325', '\u0326', '\u0329', '\u032a',
            '\u032b', '\u032c', '\u032d', '\u032e', '\u032f', '\u0330', '\u0331',
            '\u0332', '\u0333', '\u0339', '\u033a', '\u033b', '\u033c', '\u0345',
            '\u0347', '\u0348', '\u0349', '\u034d', '\u034e', '\u0353', '\u0354',
            '\u0355', '\u0356', '\u0359', '\u035a', '\u0323'
        )
        val sb = StringBuilder()
        for (ch in text) {
            sb.append(ch)
            repeat(intensity) { sb.append(zalgoUp.random()) }
            repeat(intensity) { sb.append(zalgoMid.random()) }
            repeat(intensity) { sb.append(zalgoDown.random()) }
        }
        return sb.toString()
    }

    fun toInvisible(): String = "\u2060\u200b\u2062\u200c\u200d"

    fun toNicknameGenerator(name: String): List<String> {
        return listOf(
            "« ${name.uppercase()} »",
            "꧁${name}꧂",
            "★彡${name}彡★",
            "᭄${name}ꦿ᭄",
            "亗${name}亗",
            "༆${name}༆",
            "꧁༺${name}༻꧂",
            "⫷${name}⫸",
            "🅜${name}🅣",
            "҉${name}҉",
            "⁀➴${name}",
            "${name.map { "⃝$it" }.joinToString("")}",
            "✞${name}✞",
            "≋${name}≋",
            "ꀘ${name}ꀘ"
        )
    }

    fun addBorder(text: String): String {
        val lines = text.lines()
        val width = lines.maxOf { it.length }
        val top = "╔${"═".repeat(width + 2)}╗"
        val bottom = "╚${"═".repeat(width + 2)}╝"
        val middle = lines.joinToString("\n") { "║ ${it.padEnd(width)} ║" }
        return "$top\n$middle\n$bottom"
    }

    fun toMirror(text: String): String = text + text.reversed()

    fun addStars(text: String): String = "✦ $text ✦"
    fun addHearts(text: String): String = "❤ $text ❤"
    fun addSparkles(text: String): String = "✨ $text ✨"

    data class DecoratorStyle(
        val id: String,
        val name: String,
        val preview: String,
        val decorator: (String) -> String
    )

    val allStyles: List<DecoratorStyle> = listOf(
        DecoratorStyle("zalgo_low", "Zalgo (Low)", "T̷h̴i̵s̷ ̵i̴s̷", { toZalgo(it, 1) }),
        DecoratorStyle("zalgo_med", "Zalgo (Medium)", "T͙͜h̵͙i͜͡s͙", { toZalgo(it, 2) }),
        DecoratorStyle("zalgo_high", "Zalgo (High)", "T̷̡̢̧̨̛͇̦", { toZalgo(it, 4) }),
        DecoratorStyle("border", "Box Border", "╔══╗\n║Hi║\n╚══╝", ::addBorder),
        DecoratorStyle("mirror", "Mirror", "Helloolleᴴ", ::toMirror),
        DecoratorStyle("stars", "Stars", "✦ Hello ✦", ::addStars),
        DecoratorStyle("hearts", "Hearts", "❤ Hello ❤", ::addHearts),
        DecoratorStyle("sparkles", "Sparkles", "✨ Hello ✨", ::addSparkles),
        DecoratorStyle("invisible", "Invisible Char", "[ ]", { toInvisible() }),
    )
}
