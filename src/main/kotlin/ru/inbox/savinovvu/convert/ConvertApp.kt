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

        val words = parseEngRuPairs(pdfText)
        println("📝 Найдено ${words.size} фраз")

        saveToYaml(words, yamlPath)
        println("✨ YAML файл создан: $yamlPath")

        words.take(3).forEachIndexed { index, pair ->
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

fun parseEngRuPairs(text: String): List<Map<String, String>> {
    val result = mutableListOf<Map<String, String>>()

    // Паттерн для поиска: текст [транскрипция] перевод
    val lines = text.lines()

    for (line in lines) {
        val trimmedLine = line.trim()

        // Ищем строки, содержащие квадратные скобки
        val openBracket = trimmedLine.indexOf('[')
        val closeBracket = trimmedLine.indexOf(']')

        if (openBracket > 0 && closeBracket > openBracket) {
            // Английская часть - все что до '['
            val eng = trimmedLine.substring(0, openBracket).trim()

            // Русская часть - все что после ']'
            val ru = if (closeBracket + 1 < trimmedLine.length) {
                trimmedLine.substring(closeBracket + 1).trim()
            } else ""

            if (eng.isNotEmpty() && ru.isNotEmpty() && eng.length > 2 && ru.length > 1) {
                result.add(mapOf(
                    "eng" to eng.replace(Regex("\\s+"), " "),
                    "ru" to ru.replace(Regex("\\s+"), " ")
                ))
            }
        }
    }

    return result
}

fun saveToYaml(words: List<Map<String, String>>, path: String) {
    val root = mapOf("words" to words)
    val yaml = Yaml()

    FileWriter(path).use { writer ->
        yaml.dump(root, writer)
    }
}