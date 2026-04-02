package ru.inbox.savinovvu.eng.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.inbox.savinovvu.eng.service.SwingWindow

@Configuration
@EnableConfigurationProperties(AppProperties::class)
class AppConfig {

    @Bean
    fun swingWindow(properties: AppProperties): SwingWindow {
        return SwingWindow(
            properties.messages.first,
            properties.messages.second,
            properties.timing.delaySeconds
        )
    }
}

