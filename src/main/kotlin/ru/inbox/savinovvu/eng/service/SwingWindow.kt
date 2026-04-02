package ru.inbox.savinovvu.eng.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.awt.Dimension
import java.awt.Font
import javax.swing.*

@Component
class SwingWindow(
    @Value("\${app.messages.first}") private val firstMessage: String,
    @Value("\${app.messages.second}") private val secondMessage: String,
    @Value("\${app.timing.delay-seconds}") private val delaySeconds: Int
) : JFrame() {

    private val label = JLabel(firstMessage, SwingConstants.CENTER)

    init {
        initUI()
        startMessageTimer()
    }

    private fun initUI() {
        title = "Swing Spring Boot App"
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(400, 300)
        setLocationRelativeTo(null)

        label.font = Font("Arial", Font.BOLD, 24)
        label.preferredSize = Dimension(400, 300)

        contentPane.add(label)

        // Запускаем UI в потоке EDT
        SwingUtilities.invokeLater {
            isVisible = true
        }
    }

    private fun startMessageTimer() {
        // Используем javax.swing.Timer для обновления UI в EDT
        val timer = Timer(delaySeconds * 1000) {
            SwingUtilities.invokeLater {
                label.text = secondMessage
                label.repaint()
            }
        }
//        timer.repeat = false
        timer.start()

        println("Таймер запущен на $delaySeconds секунд")
    }
}