package ru.inbox.savinovvu.eng.service

import org.springframework.stereotype.Component
import ru.inbox.savinovvu.eng.config.ApplicationProperties
import java.awt.Color
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.Toolkit
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.SwingConstants
import javax.swing.SwingUtilities
import javax.swing.Timer
import javax.swing.UIManager
import javax.swing.plaf.ColorUIResource
import javax.swing.plaf.metal.DefaultMetalTheme
import javax.swing.plaf.metal.MetalLookAndFeel

@Component
class SwingWindow(
    private val applicationProperties: ApplicationProperties
) : JFrame() {

    private val engLabel = JLabel("", SwingConstants.CENTER)
    private val ruLabel = JLabel("", SwingConstants.CENTER)
    private val exampleLabel = JLabel("", SwingConstants.CENTER)

    init {
        setupDarkMode()
        initUI()
        startMessageTimer()
        updateRandomWord()
    }

    private fun setupDarkMode() {
        try {
            // Устанавливаем Metal Look and Feel
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel")

            // Создаем темную тему
            val darkTheme = object : DefaultMetalTheme() {
                override fun getPrimary1(): ColorUIResource = ColorUIResource(0, 0, 0)
                override fun getPrimary2(): ColorUIResource = ColorUIResource(64, 64, 64)
                override fun getPrimary3(): ColorUIResource = ColorUIResource(32, 32, 32)
                override fun getSecondary1(): ColorUIResource = ColorUIResource(0, 0, 0)
                override fun getSecondary2(): ColorUIResource = ColorUIResource(64, 64, 64)
                override fun getSecondary3(): ColorUIResource = ColorUIResource(32, 32, 32)
                override fun getBlack(): ColorUIResource = ColorUIResource(255, 255, 255)
                override fun getWhite(): ColorUIResource = ColorUIResource(0, 0, 0)
            }

            MetalLookAndFeel.setCurrentTheme(darkTheme)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initUI() {
        title = "Language Learning Assistant"
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(1600, 400)
        setLocationToTopRight()

        // Устанавливаем темный фон
        contentPane.background = Color(32, 32, 32)

        layout = GridBagLayout()

        engLabel.font = Font("Arial", Font.BOLD, 60)
        engLabel.foreground = Color.WHITE

        ruLabel.font = Font("Arial", Font.ITALIC, 50)
        ruLabel.foreground = Color(250, 250, 250)

        exampleLabel.font = Font("Arial", Font.PLAIN, 35)
        exampleLabel.foreground = Color(180, 180, 180)

        val constraints = GridBagConstraints()
        constraints.gridwidth = GridBagConstraints.REMAINDER
        constraints.fill = GridBagConstraints.HORIZONTAL
        constraints.weightx = 1.0  // Растягиваем по горизонтали

        // Верхняя часть - английское слово (прижато к верху)
        constraints.weighty = 0.3  // Занимает 30% высоты
        constraints.anchor = GridBagConstraints.PAGE_START  // Прижимаем к верху
        constraints.insets = Insets(50, 0, 0, 0)  // Отступ сверху
        add(engLabel, constraints)

        // Центральная часть - перевод (по центру)
        constraints.weighty = 0.4  // Занимает 40% высоты
        constraints.anchor = GridBagConstraints.CENTER  // Центрируем по вертикали
        constraints.insets = Insets(0, 0, 0, 0)
        add(ruLabel, constraints)

        // Нижняя часть - пример (прижато к низу)
        constraints.weighty = 0.3  // Занимает 30% высоты
        constraints.anchor = GridBagConstraints.PAGE_END  // Прижимаем к низу
        constraints.insets = Insets(0, 0, 50, 0)  // Отступ снизу
        add(exampleLabel, constraints)

        SwingUtilities.invokeLater {
            isVisible = true
        }
    }

    private fun setLocationToTopRight() {
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        setLocation(screenSize.width - width - 20, 20)
    }

    private fun updateRandomWord() {
        val randomIndex = (0 until applicationProperties.words.size).random()
        val selectedWord = applicationProperties.words[randomIndex]
        engLabel.text = selectedWord.eng
        ruLabel.text = selectedWord.ru
        exampleLabel.text = selectedWord.example
    }

    private fun startMessageTimer() {
        val timer = Timer(10 * 1000) {
            SwingUtilities.invokeLater {
                updateRandomWord()
                repaint()
            }
        }
        timer.start()
        println("✅ Таймер запущен. Новые слова будут появляться каждые 10 секунд")
    }
}