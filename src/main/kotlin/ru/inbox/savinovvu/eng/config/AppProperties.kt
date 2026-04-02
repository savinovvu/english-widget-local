package ru.inbox.savinovvu.eng.config

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = "app")
class AppProperties {
    var messages: Messages = Messages()
    var timing: Timing = Timing()

    data class Messages(
        var first: String = "Привет",
        var second: String = "мир"
    )

    data class Timing(
        var delaySeconds: Int = 10
    )
}