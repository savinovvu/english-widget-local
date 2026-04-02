package ru.inbox.savinovvu.convert

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.yaml.snakeyaml.Yaml
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

    // Разбиваем текст на строки
    val lines = text.lines()

    var currentEng = ""
    var currentRu = ""
    var isCollectingRu = false

    for (line in lines) {
        val trimmedLine = line.trim()
        if (trimmedLine.isEmpty()) continue

        // Проверяем, есть ли в строке квадратные скобки (транскрипция)
        val hasBrackets = trimmedLine.contains('[') && trimmedLine.contains(']')

        if (hasBrackets) {
            // Если уже собирали предыдущую фразу, сохраняем её
            if (currentEng.isNotEmpty() && currentRu.isNotEmpty()) {
                result.add(mapOf(
                    "eng" to currentEng.trim(),
                    "ru" to currentRu.trim()
                ))
                currentEng = ""
                currentRu = ""
            }

            // Разбираем новую фразу
            val openBracket = trimmedLine.indexOf('[')
            val closeBracket = trimmedLine.indexOf(']')

            // Английская часть (всё до открывающей скобки)
            var eng = trimmedLine.substring(0, openBracket).trim()
            // Убираем номера в начале (1., 2., 3. и т.д.)
            eng = eng.replace(Regex("^\\d+[\\.\\s]+"), "").trim()

            // Русская часть (всё после закрывающей скобки)
            var ru = if (closeBracket + 1 < trimmedLine.length) {
                trimmedLine.substring(closeBracket + 1).trim()
            } else ""

            // Убираем лишние пробелы
            eng = eng.replace(Regex("\\s+"), " ")
            ru = ru.replace(Regex("\\s+"), " ")

            currentEng = eng
            currentRu = ru
            isCollectingRu = true
        } else if (isCollectingRu && currentRu.isNotEmpty()) {
            // Это продолжение русского перевода на следующей строке
            // Но проверяем, не начинается ли новая фраза (по наличию английских букв)
            if (!trimmedLine.matches(Regex("^[a-zA-Z].*"))) {
                currentRu += " " + trimmedLine
                currentRu = currentRu.replace(Regex("\\s+"), " ")
            } else {
                // Начинается новая фраза без скобок - сохраняем текущую и начинаем новую
                if (currentEng.isNotEmpty() && currentRu.isNotEmpty()) {
                    result.add(mapOf(
                        "eng" to currentEng.trim(),
                        "ru" to currentRu.trim()
                    ))
                }
                currentEng = trimmedLine
                currentRu = ""
                isCollectingRu = false
            }
        }
    }

    // Сохраняем последнюю фразу
    if (currentEng.isNotEmpty() && currentRu.isNotEmpty()) {
        // Очищаем от лишнего текста в конце (URL, копирайты и т.д.)
        var cleanRu = currentRu.trim()
        // Удаляем текст после символа © или http
        val copyrightIndex = cleanRu.indexOf("©")
        if (copyrightIndex > 0) {
            cleanRu = cleanRu.substring(0, copyrightIndex).trim()
        }
        val httpIndex = cleanRu.indexOf("http")
        if (httpIndex > 0) {
            cleanRu = cleanRu.substring(0, httpIndex).trim()
        }

        result.add(mapOf(
            "eng" to currentEng.trim(),
            "ru" to cleanRu
        ))
    }

    return result
}

fun saveToYaml(phrases: List<Map<String, String>>, path: String) {
    val yaml = Yaml()

    FileWriter(path).use { writer ->
        writer.write("words:\n")
        phrases.forEach { phrase ->
            writer.write("  - eng: ${phrase["eng"]}\n")
            writer.write("    ru: ${phrase["ru"]}\n")
        }
    }
}