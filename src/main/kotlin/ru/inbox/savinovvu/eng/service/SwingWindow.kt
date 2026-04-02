package ru.inbox.savinovvu.eng.service

import org.springframework.stereotype.Component
import ru.inbox.savinovvu.eng.config.ApplicationProperties
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Toolkit
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.SwingConstants
import javax.swing.SwingUtilities
import javax.swing.Timer

@Component
class SwingWindow(
    private val messsage: ApplicationProperties
) : JFrame() {

    private val engLabel = JLabel("", SwingConstants.CENTER)
    private val ruLabel = JLabel("", SwingConstants.CENTER)

    init {
        initUI()
        startMessageTimer()
        updateRandomWord() // Показываем первое слово сразу
    }

    private fun initUI() {
        title = "Swing Spring Boot App"
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(1600, 400)

        // Устанавливаем окно в верхнем правом углу
        setLocationToTopRight()

        // Добавляем слушатель для сохранения позиции при изменении размера окна
        addComponentListener(object : java.awt.event.ComponentAdapter() {
            override fun componentResized(e: java.awt.event.ComponentEvent) {
                setLocationToTopRight()
            }
        })

        layout = GridBagLayout()

        // Настройка жирного шрифта для английского текста
        engLabel.font = Font("Arial", Font.BOLD, 50)

        // Настройка курсивного шрифта для русского текста
        ruLabel.font = Font("Arial", Font.ITALIC, 49)

        val constraints = GridBagConstraints()
        constraints.gridwidth = GridBagConstraints.REMAINDER
        constraints.fill = GridBagConstraints.HORIZONTAL

        // Добавляем engLabel с отступом сверху
        constraints.insets = java.awt.Insets(50, 0, 20, 0)
        add(engLabel, constraints)

        // Добавляем пустое пространство (2 пустые строки)
        for (i in 1..2) {
            val emptyLabel = JLabel(" ")
            emptyLabel.font = Font("Arial", Font.PLAIN, 20)
            constraints.insets = java.awt.Insets(0, 0, 0, 0)
            add(emptyLabel, constraints)
        }

        // Добавляем ruLabel с отступом снизу
        constraints.insets = java.awt.Insets(0, 0, 50, 0)
        add(ruLabel, constraints)

        // Запускаем UI в потоке EDT
        SwingUtilities.invokeLater {
            isVisible = true
        }
    }

    private fun setLocationToTopRight() {
        // Способ 1: Использование Toolkit (простой)
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        val rightMargin = 20  // Отступ от правого края
        val topMargin = 20    // Отступ от верхнего края
        setLocation(screenSize.width - width - rightMargin, topMargin)

        // Альтернативный способ 2: Использование GraphicsEnvironment (учитывает панель задач)
        // val maxBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().maximumWindowBounds
        // setLocation(maxBounds.width - width - rightMargin, maxBounds.y + topMargin)
    }

    private fun updateRandomWord() {
        val randomIndex = (0 until messsage.words.size).random()
        val selectedWord = messsage.words[randomIndex]

        engLabel.text = selectedWord.eng
        ruLabel.text = selectedWord.ru
    }

    private fun startMessageTimer() {
        // Используем javax.swing.Timer для обновления UI в EDT
        val timer = Timer(10 * 1000) { // 10 секунд
            SwingUtilities.invokeLater {
                updateRandomWord()
                repaint()
            }
        }
        timer.start()

        println("Таймер запущен. Новые слова будут появляться каждые 10 секунд")
    }
}