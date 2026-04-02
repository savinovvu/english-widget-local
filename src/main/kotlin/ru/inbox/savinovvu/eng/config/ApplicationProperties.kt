package ru.inbox.savinovvu.eng.config

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties("app")
class ApplicationProperties {
    var words: List<Word> = emptyList()
}