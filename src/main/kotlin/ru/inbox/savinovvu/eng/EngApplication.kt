package ru.inbox.savinovvu.eng

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EngApplication

fun main(args: Array<String>) {
    System.setProperty("java.awt.headless", "false")
    runApplication<EngApplication>(*args)
}
