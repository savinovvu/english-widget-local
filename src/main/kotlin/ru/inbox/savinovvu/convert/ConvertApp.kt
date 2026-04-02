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

        // Для отладки: выводим текст
        println("\n=== ТЕКСТ ИЗ PDF ===\n$pdfText\n=== КОНЕЦ ТЕКСТА ===\n")

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
    val lines = text.lines()

    var i = 0
    while (i < lines.size) {
        val line = lines[i].trim()

        // Пропускаем пустые строки и заголовки
        if (line.isEmpty() || line.startsWith("=") || line.startsWith("#")) {
            i++
            continue
        }

        // Пропускаем заголовок словаря
        if (line.contains("персональный словарь", ignoreCase = true) ||
            line == "Покоряй языковые джунгли!") {
            i++
            continue
        }

        // Если строка содержит транскрипцию в квадратных скобках
        if (line.contains('[') && line.contains(']')) {
            // Пропускаем строки с транскрипцией, они нам не нужны
            i++
            continue
        }

        // Если строка похожа на английскую фразу (начинается с буквы и не содержит русских букв)
        if (line.matches(Regex("^[a-zA-Z].*")) && !line.matches(Regex(".*[а-яА-Я].*"))) {
            var engPhrase = line
            var j = i + 1

            // Собираем многострочную английскую фразу
            while (j < lines.size) {
                val nextLine = lines[j].trim()
                if (nextLine.isEmpty()) {
                    j++
                    continue
                }
                // Если следующая строка содержит транскрипцию или русские буквы - это конец английской фразы
                if (nextLine.contains('[') || nextLine.matches(Regex(".*[а-яА-Я].*"))) {
                    break
                }
                // Если следующая строка начинается с английской буквы - продолжаем
                if (nextLine.matches(Regex("^[a-zA-Z].*"))) {
                    engPhrase += " " + nextLine
                    j++
                } else {
                    break
                }
            }

            // Теперь ищем русский перевод (он может быть через несколько строк после транскрипции)
            var ruPhrase = ""
            var k = j
            while (k < lines.size) {
                val nextLine = lines[k].trim()
                if (nextLine.isEmpty()) {
                    k++
                    continue
                }
                // Если нашли следующую английскую фразу - выходим
                if (nextLine.matches(Regex("^[a-zA-Z].*")) && !nextLine.contains('[')) {
                    break
                }
                // Если строка содержит русские буквы - это перевод
                if (nextLine.matches(Regex(".*[а-яА-Я].*"))) {
                    if (ruPhrase.isEmpty()) {
                        ruPhrase = nextLine
                    } else {
                        ruPhrase += " " + nextLine
                    }
                }
                k++
            }

            // Очищаем фразы
            engPhrase = engPhrase.trim().replace(Regex("\\s+"), " ")
            ruPhrase = ruPhrase.trim().replace(Regex("\\s+"), " ")

            // Проверяем, что фразы не пустые и английская фраза не слишком короткая
            if (engPhrase.isNotEmpty() && ruPhrase.isNotEmpty() && engPhrase.length > 2) {
                result.add(mapOf("eng" to engPhrase, "ru" to ruPhrase))
            }

            i = k
        } else {
            i++
        }
    }

    return result
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