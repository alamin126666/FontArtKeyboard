package com.bdalamin.fontkeyboard.utils

object FontConverter {

    private val normalAlpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    private val normalAlphaList = normalAlpha.toList()

    // ── Unicode font maps ──────────────────────────────────────────────────────

    private val boldChars =
        "𝐀𝐁𝐂𝐃𝐄𝐅𝐆𝐇𝐈𝐉𝐊𝐋𝐌𝐍𝐎𝐏𝐐𝐑𝐒𝐓𝐔𝐕𝐖𝐗𝐘𝐙𝐚𝐛𝐜𝐝𝐞𝐟𝐠𝐡𝐢𝐣𝐤𝐥𝐦𝐧𝐨𝐩𝐪𝐫𝐬𝐭𝐮𝐯𝐰𝐱𝐲𝐳𝟎𝟏𝟐𝟑𝟒𝟓𝟔𝟕𝟖𝟗"
    private val italicChars =
        "𝘈𝘉𝘊𝘋𝘌𝘍𝘎𝘏𝘐𝘑𝘒𝘓𝘔𝘕𝘖𝘗𝘘𝘙𝘚𝘛𝘜𝘝𝘞𝘟𝘠𝘡𝘢𝘣𝘤𝘥𝘦𝘧𝘨𝘩𝘪𝘫𝘬𝘭𝘮𝘯𝘰𝘱𝘲𝘳𝘴𝘵𝘶𝘷𝘸𝘹𝘺𝘻0123456789"
    private val boldItalicChars =
        "𝘼𝘽𝘾𝘿𝙀𝙁𝙂𝙃𝙄𝙅𝙆𝙇𝙈𝙉𝙊𝙋𝙌𝙍𝙎𝙏𝙐𝙑𝙒𝙓𝙔𝙕𝙖𝙗𝙘𝙙𝙚𝙛𝙜𝙝𝙞𝙟𝙠𝙡𝙢𝙣𝙤𝙥𝙦𝙧𝙨𝙩𝙪𝙫𝙬𝙭𝙮𝙯0123456789"
    private val scriptChars =
        "𝒜ℬ𝒞𝒟ℰℱ𝒢ℋℐ𝒥𝒦ℒℳ𝒩𝒪𝒫𝒬ℛ𝒮𝒯𝒰𝒱𝒲𝒳𝒴𝒵𝒶𝒷𝒸𝒹ℯ𝒻ℊ𝒽𝒾𝒿𝓀𝓁𝓂𝓃ℴ𝓅𝓆𝓇𝓈𝓉𝓊𝓋𝓌𝓍𝓎𝓏0123456789"
    private val boldScriptChars =
        "𝓐𝓑𝓒𝓓𝓔𝓕𝓖𝓗𝓘𝓙𝓚𝓛𝓜𝓝𝓞𝓟𝓠𝓡𝓢𝓣𝓤𝓥𝓦𝓧𝓨𝓩𝓪𝓫𝓬𝓭𝓮𝓯𝓰𝓱𝓲𝓳𝓴𝓵𝓶𝓷𝓸𝓹𝓺𝓻𝓼𝓽𝓾𝓿𝔀𝔁𝔂𝔃0123456789"
    private val frakturChars =
        "𝔄𝔅ℭ𝔇𝔈𝔉𝔊ℌℑ𝔍𝔎𝔏𝔐𝔑𝔒𝔓𝔔ℜ𝔖𝔗𝔘𝔙𝔚𝔛𝔜ℨ𝔞𝔟𝔠𝔡𝔢𝔣𝔤𝔥𝔦𝔧𝔨𝔩𝔪𝔫𝔬𝔭𝔮𝔯𝔰𝔱𝔲𝔳𝔴𝔵𝔶𝔷0123456789"
    private val doubleStruckChars =
        "𝔸𝔹ℂ𝔻𝔼𝔽𝔾ℍ𝕀𝕁𝕂𝕃𝕄ℕ𝕆ℙℚℝ𝕊𝕋𝕌𝕍𝕎𝕏𝕐ℤ𝕒𝕓𝕔𝕕𝕖𝕗𝕘𝕙𝕚𝕛𝕜𝕝𝕞𝕟𝕠𝕡𝕢𝕣𝕤𝕥𝕦𝕧𝕨𝕩𝕪𝕫𝟘𝟙𝟚𝟛𝟜𝟝𝟞𝟟𝟠𝟡"
    private val monoChars =
        "𝙰𝙱𝙲𝙳𝙴𝙵𝙶𝙷𝙸𝙹𝙺𝙻𝙼𝙽𝙾𝙿𝚀𝚁𝚂𝚃𝚄𝚅𝚆𝚇𝚈𝚉𝚊𝚋𝚌𝚍𝚎𝚏𝚐𝚑𝚒𝚓𝚔𝚕𝚖𝚗𝚘𝚙𝚚𝚛𝚜𝚝𝚞𝚟𝚠𝚡𝚢𝚣𝟶𝟷𝟸𝟹𝟺𝟻𝟼𝟽𝟾𝟿"
    private val circledChars =
        "ⒶⒷⒸⒹⒺⒻⒼⒽⒾⒿⓀⓁⓂⓃⓄⓅⓆⓇⓈⓉⓊⓋⓌⓍⓎⓏⓐⓑⓒⓓⓔⓕⓖⓗⓘⓙⓚⓛⓜⓝⓞⓟⓠⓡⓢⓣⓤⓥⓦⓧⓨⓩ⓪①②③④⑤⑥⑦⑧⑨"
    private val squaredChars =
        "🄰🄱🄲🄳🄴🄵🄶🄷🄸🄹🄺🄻🄼🄽🄾🄿🅀🅁🅂🅃🅄🅅🅆🅇🅈🅉🄰🄱🄲🄳🄴🄵🄶🄷🄸🄹🄺🄻🄼🄽🄾🄿🅀🅁🅂🅃🅄🅅🅆🅇🅈🅉0123456789"
    private val fullWidthChars =
        "ＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ０１２３４５６７８９"

    private fun convert(text: String, targetChars: String): String {
        val targetList = targetChars.codePoints().toArray()
        val sb = StringBuilder()
        for (ch in text) {
            val idx = normalAlpha.indexOf(ch)
            if (idx >= 0 && idx < targetList.size) {
                sb.appendCodePoint(targetList[idx])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    fun toBold(text: String) = convert(text, boldChars)
    fun toItalic(text: String) = convert(text, italicChars)
    fun toBoldItalic(text: String) = convert(text, boldItalicChars)
    fun toScript(text: String) = convert(text, scriptChars)
    fun toBoldScript(text: String) = convert(text, boldScriptChars)
    fun toFraktur(text: String) = convert(text, frakturChars)
    fun toDoubleStruck(text: String) = convert(text, doubleStruckChars)
    fun toMono(text: String) = convert(text, monoChars)
    fun toCircled(text: String) = convert(text, circledChars)
    fun toSquared(text: String) = convert(text, squaredChars)
    fun toFullWidth(text: String) = convert(text, fullWidthChars)

    fun toSmallCaps(text: String): String {
        val smallCapsMap = mapOf(
            'a' to 'ᴀ', 'b' to 'ʙ', 'c' to 'ᴄ', 'd' to 'ᴅ', 'e' to 'ᴇ',
            'f' to 'ꜰ', 'g' to 'ɢ', 'h' to 'ʜ', 'i' to 'ɪ', 'j' to 'ᴊ',
            'k' to 'ᴋ', 'l' to 'ʟ', 'm' to 'ᴍ', 'n' to 'ɴ', 'o' to 'ᴏ',
            'p' to 'ᴘ', 'q' to 'ǫ', 'r' to 'ʀ', 's' to 'ꜱ', 't' to 'ᴛ',
            'u' to 'ᴜ', 'v' to 'ᴠ', 'w' to 'ᴡ', 'x' to 'x', 'y' to 'ʏ',
            'z' to 'ᴢ'
        )
        return text.lowercase().map { smallCapsMap[it] ?: it }.joinToString("")
    }

    fun toCursive(text: String) = toScript(text)

    fun toBubble(text: String): String {
        val bubbleMap = mapOf(
            'a' to "ⓐ", 'b' to "ⓑ", 'c' to "ⓒ", 'd' to "ⓓ", 'e' to "ⓔ",
            'f' to "ⓕ", 'g' to "ⓖ", 'h' to "ⓗ", 'i' to "ⓘ", 'j' to "ⓙ",
            'k' to "ⓚ", 'l' to "ⓛ", 'm' to "ⓜ", 'n' to "ⓝ", 'o' to "ⓞ",
            'p' to "ⓟ", 'q' to "ⓠ", 'r' to "ⓡ", 's' to "ⓢ", 't' to "ⓣ",
            'u' to "ⓤ", 'v' to "ⓥ", 'w' to "ⓦ", 'x' to "ⓧ", 'y' to "ⓨ",
            'z' to "ⓩ", 'A' to "Ⓐ", 'B' to "Ⓑ", 'C' to "Ⓒ", 'D' to "Ⓓ",
            'E' to "Ⓔ", 'F' to "Ⓕ", 'G' to "Ⓖ", 'H' to "Ⓗ", 'I' to "Ⓘ",
            'J' to "Ⓙ", 'K' to "Ⓚ", 'L' to "Ⓛ", 'M' to "Ⓜ", 'N' to "Ⓝ",
            'O' to "Ⓞ", 'P' to "Ⓟ", 'Q' to "Ⓠ", 'R' to "Ⓡ", 'S' to "Ⓢ",
            'T' to "Ⓣ", 'U' to "Ⓤ", 'V' to "Ⓥ", 'W' to "Ⓦ", 'X' to "Ⓧ",
            'Y' to "Ⓨ", 'Z' to "Ⓩ",
            '0' to "⓪", '1' to "①", '2' to "②", '3' to "③", '4' to "④",
            '5' to "⑤", '6' to "⑥", '7' to "⑦", '8' to "⑧", '9' to "⑨"
        )
        return text.map { bubbleMap[it] ?: it.toString() }.joinToString("")
    }

    fun toFlip(text: String): String {
        val flipMap = mapOf(
            'a' to 'ɐ', 'b' to 'q', 'c' to 'ɔ', 'd' to 'p', 'e' to 'ǝ',
            'f' to 'ɟ', 'g' to 'ƃ', 'h' to 'ɥ', 'i' to 'ı', 'j' to 'ɾ',
            'k' to 'ʞ', 'l' to 'l', 'm' to 'ɯ', 'n' to 'u', 'o' to 'o',
            'p' to 'd', 'q' to 'b', 'r' to 'ɹ', 's' to 's', 't' to 'ʇ',
            'u' to 'n', 'v' to 'ʌ', 'w' to 'ʍ', 'x' to 'x', 'y' to 'ʎ',
            'z' to 'z', 'A' to '∀', 'B' to 'ᗺ', 'C' to 'Ɔ', 'D' to 'ᗡ',
            'E' to 'Ǝ', 'F' to 'Ⅎ', 'G' to 'ᒍ', 'H' to 'H', 'I' to 'I',
            'J' to 'ɾ', 'K' to 'ʞ', 'L' to '˥', 'M' to 'W', 'N' to 'N',
            'O' to 'O', 'P' to 'Ԁ', 'Q' to 'Q', 'R' to 'ᴚ', 'S' to 'S',
            'T' to '┴', 'U' to '∩', 'V' to 'Λ', 'W' to 'M', 'X' to 'X',
            'Y' to '⅄', 'Z' to 'Z', ',' to '\'', '.' to '˙', '!' to '¡',
            '?' to '¿', '(' to ')', ')' to '(', '[' to ']', ']' to '['
        )
        return text.reversed().map { flipMap[it] ?: it }.joinToString("")
    }

    data class FontStyle(
        val id: String,
        val name: String,
        val preview: String,
        val converter: (String) -> String
    )

    val allFontStyles: List<FontStyle> = listOf(
        FontStyle("normal", "Normal", "The quick brown fox", { it }),
        FontStyle("bold", "𝐁𝐨𝐥𝐝", "𝐓𝐡𝐞 𝐪𝐮𝐢𝐜𝐤 𝐛𝐫𝐨𝐰𝐧 𝐟𝐨𝐱", ::toBold),
        FontStyle("italic", "𝘐𝘵𝘢𝘭𝘪𝘤", "𝘛𝘩𝘦 𝘲𝘶𝘪𝘤𝘬 𝘣𝘳𝘰𝘸𝘯 𝘧𝘰𝘹", ::toItalic),
        FontStyle("bold_italic", "𝙱𝚘𝚕𝚍 𝙸𝚝𝚊𝚕𝚒𝚌", "𝙏𝙝𝙚 𝙦𝙪𝙞𝙘𝙠 𝙗𝙧𝙤𝙬𝙣 𝙛𝙤𝙭", ::toBoldItalic),
        FontStyle("script", "𝒮𝒸𝓇𝒾𝓅𝓉", "𝒯𝒽𝑒 𝓆𝓊𝒾𝒸𝓀 𝒷𝓇ℴ𝓌𝓃 𝒻ℴ𝓍", ::toScript),
        FontStyle("bold_script", "𝓑𝓸𝓵𝓭 𝓢𝓬𝓻𝓲𝓹𝓽", "𝓣𝓱𝓮 𝓺𝓾𝓲𝓬𝓴 𝓫𝓻𝓸𝔀𝓷 𝓯𝓸𝔁", ::toBoldScript),
        FontStyle("fraktur", "𝔉𝔯𝔞𝔨𝔱𝔲𝔯", "𝔗𝔥𝔢 𝔮𝔲𝔦𝔠𝔨 𝔟𝔯𝔬𝔴𝔫 𝔣𝔬𝔵", ::toFraktur),
        FontStyle("double_struck", "𝔻𝕠𝕦𝕓𝕝𝕖", "𝕋𝕙𝕖 𝕢𝕦𝕚𝕔𝕜 𝕓𝕣𝕠𝕨𝕟 𝕗𝕠𝕩", ::toDoubleStruck),
        FontStyle("mono", "Mono", "𝚃𝚑𝚎 𝚚𝚞𝚒𝚌𝚔 𝚋𝚛𝚘𝚠𝚗 𝚏𝚘𝚡", ::toMono),
        FontStyle("small_caps", "Sᴍᴀʟʟ Cᴀᴘꜱ", "Tʜᴇ ǫᴜɪᴄᴋ ʙʀᴏᴡɴ ꜰᴏx", ::toSmallCaps),
        FontStyle("bubble", "Ⓑⓤⓑⓑⓛⓔ", "Ⓣⓗⓔ ⓠⓤⓘⓒⓚ ⓑⓡⓞⓦⓝ ⓕⓞⓧ", ::toBubble),
        FontStyle("full_width", "Ｆｕｌｌ　Ｗｉｄｔｈ", "Ｔｈｅ ｑｕｉｃｋ ｂｒｏｗｎ ｆｏｘ", ::toFullWidth),
        FontStyle("flip", "dılɟ", "ɹoɟ uʍoɹq ʞɔınb ǝɥʇ", ::toFlip),
        FontStyle("circled", "Ⓒⓘⓡⓒⓛⓔⓓ", "Ⓣⓗⓔ ⓠⓤⓘⓒⓚ ⓑⓡⓞⓦⓝ ⓕⓞⓧ", ::toCircled),
    )
}
