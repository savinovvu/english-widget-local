package ru.inbox.savinovvu.convert

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import java.io.File
import java.io.FileWriter

fun main(args: Array<String>) {
    val pdfPath = args.getOrElse(0) { "/home/skorpion/1.Soft/1.projects/2.myprojects/eng/eng/some/short.pdf" }
    val yamlPath = args.getOrElse(1) { "/home/skorpion/1.Soft/1.projects/2.myprojects/eng/eng/some/output.yml" }

    println("📖 Чтение PDF файла: $pdfPath")

    try {
        val pdfText = extractTextFromPdf(pdfPath)
        println("✅ Текст извлечен (${pdfText.length} символов)")

        val phrases = parsePhrases(pdfText)
        println("📝 Найдено ${phrases.size} фраз")

        saveToYaml(phrases, yamlPath)
        println("✨ YAML файл создан: $yamlPath")

        phrases.forEachIndexed { index, pair ->
            println("  ${index + 1}. ${pair["eng"]} → ${pair["ru"]}")
        }

    } catch (e: Exception) {
        System.err.println("❌ Ошибка: ${e.message}")
        e.printStackTrace()
    }
}

fun extractTextFromPdf(path: String): String {
    val file = File(path)
    return PDDocument.load(file).use { document ->
        val stripper = PDFTextStripper()
        stripper.getText(document)
    }
}

fun parsePhrases(text: String): List<Map<String, String>> {
    val result = mutableListOf<Map<String, String>>()

    // Разбиваем текст на строки и убираем пустые
    val lines = text.lines().map { it.trim() }.filter { it.isNotEmpty() }

    var currentPhrase = StringBuilder()

    for (line in lines) {
        // Пропускаем заголовки
        if (line.equals("мой персональный словарь", ignoreCase = true) ||
            line.equals("Покоряй языковые джунгли!", ignoreCase = true) ||
            line.startsWith("©") ||
            line.startsWith("http")) {
            continue
        }

        // Если строка начинается с номера (1., 2., и т.д.) и у нас есть накопленная фраза
        if (line.matches(Regex("^\\d+\\s.*")) && currentPhrase.isNotEmpty()) {
            // Обрабатываем накопленную фразу
            processPhrase(currentPhrase.toString(), result)
            currentPhrase.clear()
            currentPhrase.append(line)
        } else {
            // Добавляем строку к текущей фразе
            if (currentPhrase.isNotEmpty()) currentPhrase.append(" ")
            currentPhrase.append(line)
        }
    }

    // Обрабатываем последнюю фразу
    if (currentPhrase.isNotEmpty()) {
        processPhrase(currentPhrase.toString(), result)
    }

    return result
}

fun processPhrase(phrase: String, result: MutableList<Map<String, String>>) {
    // Ищем открывающую квадратную скобку
    val openBracket = phrase.indexOf('[')
    val closeBracket = phrase.indexOf(']')

    if (openBracket != -1 && closeBracket != -1) {
        // Английская часть (все до открывающей скобки)
        var eng = phrase.substring(0, openBracket).trim()
        // Убираем номер в начале (1., 2., и т.д.)
        eng = eng.replace(Regex("^\\d+\\s+"), "").trim()
        // Убираем лишние пробелы
        eng = eng.replace(Regex("\\s+"), " ")

        // Русская часть (все после закрывающей скобки)
        var ru = phrase.substring(closeBracket + 1).trim()
        // Убираем лишние пробелы
        ru = ru.replace(Regex("\\s+"), " ")

        // Убираем возможные остатки транскрипции в русской части
        ru = ru.replace(Regex("\\[[^\\]]*\\]"), "").trim()

        if (eng.isNotEmpty() && ru.isNotEmpty()) {
            result.add(mapOf("eng" to eng, "ru" to ru))
        }
    }
}

fun saveToYaml(phrases: List<Map<String, String>>, path: String) {
    FileWriter(path).use { writer ->
        writer.write("words:\n")
        phrases.forEach { phrase ->
            writer.write("  - eng: ${phrase["eng"]}\n")
            writer.write("    ru: ${phrase["ru"]}\n")
        }
    }
}