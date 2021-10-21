package com.example.joomtest

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class JoomTestApplication

fun main(args: Array<String>) {
	runApplication<JoomTestApplication>(*args)
}
