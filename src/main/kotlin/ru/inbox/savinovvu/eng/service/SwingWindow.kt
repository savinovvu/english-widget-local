package ru.inbox.savinovvu.eng.service

import org.springframework.stereotype.Component
import ru.inbox.savinovvu.eng.config.ApplicationProperties
import java.awt.Dimension
import java.awt.Font
import java.awt.GridBagLayout
import java.awt.GridBagConstraints
import javax.swing.*

@Component
class SwingWindow(
    private val messsage: ApplicationProperties
) : JFrame() {

    private val engLabel = JLabel(messsage.words[0].eng, SwingConstants.CENTER)
    private val ruLabel = JLabel(messsage.words[0].ru, SwingConstants.CENTER)

    init {
        initUI()
        startMessageTimer()
    }

    private fun initUI() {
        title = "Swing Spring Boot App"
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(400, 300)
        setLocationRelativeTo(null)
        layout = GridBagLayout()

        // Настройка жирного шрифта для английского текста
        engLabel.font = Font("Arial", Font.BOLD, 24)

        // Настройка курсивного шрифта для русского текста
        ruLabel.font = Font("Arial", Font.ITALIC, 20)

        val constraints = GridBagConstraints()
        constraints.gridwidth = GridBagConstraints.REMAINDER
        constraints.fill = GridBagConstraints.HORIZONTAL

        // Добавляем отступы между компонентами
        val emptyBorder = BorderFactory.createEmptyBorder(10, 0, 10, 0)

        // Добавляем engLabel с отступом снизу
        constraints.insets = java.awt.Insets(50, 0, 20, 0) // верх, лево, низ, право
        add(engLabel, constraints)

        // Добавляем пустое пространство (2 пустые строки)
        for (i in 1..2) {
            val emptyLabel = JLabel(" ")
            emptyLabel.font = Font("Arial", Font.PLAIN, 20)
            constraints.insets = java.awt.Insets(0, 0, 0, 0)
            add(emptyLabel, constraints)
        }

        // Добавляем ruLabel
        constraints.insets = java.awt.Insets(0, 0, 50, 0)
        add(ruLabel, constraints)

        // Запускаем UI в потоке EDT
        SwingUtilities.invokeLater {
            isVisible = true
        }
    }

    private fun startMessageTimer() {
        // Используем javax.swing.Timer для обновления UI в EDT
        var showEnglish = true
        val timer = Timer(5 * 1000) {
            SwingUtilities.invokeLater {
                if (showEnglish) {
                    engLabel.text = messsage.words[0].eng
                    engLabel.font = Font("Arial", Font.BOLD, 30)
                    ruLabel.text = " "
                } else {
                    engLabel.text = " "
                    ruLabel.text = messsage.words[0].ru
                    ruLabel.font = Font("Arial", Font.ITALIC, 28)
                }
                showEnglish = !showEnglish
                repaint()
            }
        }
        timer.start()

        println("Таймер запущен на 10 секунд")
    }
}