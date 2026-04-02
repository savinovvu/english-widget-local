package ru.inbox.savinovvu.eng.service

import org.springframework.stereotype.Component
import ru.inbox.savinovvu.eng.config.ApplicationProperties
import java.awt.*
import javax.swing.*
import javax.swing.plaf.ColorUIResource
import javax.swing.plaf.metal.DefaultMetalTheme
import javax.swing.plaf.metal.MetalLookAndFeel

@Component
class SwingWindow(
    private val applicationProperties: ApplicationProperties
) : JFrame() {

    private val engLabel = JLabel("", SwingConstants.CENTER)
    private val ruLabel = JLabel("", SwingConstants.CENTER)

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

        engLabel.font = Font("Arial", Font.BOLD, 50)
        engLabel.foreground = Color.WHITE

        ruLabel.font = Font("Arial", Font.ITALIC, 49)
        ruLabel.foreground = Color.LIGHT_GRAY

        val constraints = GridBagConstraints()
        constraints.gridwidth = GridBagConstraints.REMAINDER
        constraints.fill = GridBagConstraints.HORIZONTAL

        constraints.insets = Insets(50, 0, 20, 0)
        add(engLabel, constraints)

        for (i in 1..2) {
            val emptyLabel = JLabel(" ")
            emptyLabel.font = Font("Arial", Font.PLAIN, 20)
            emptyLabel.foreground = Color.WHITE
            constraints.insets = Insets(0, 0, 0, 0)
            add(emptyLabel, constraints)
        }

        constraints.insets = Insets(0, 0, 50, 0)
        add(ruLabel, constraints)

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