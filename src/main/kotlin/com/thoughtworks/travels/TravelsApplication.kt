package com.thoughtworks.travels

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TravelsApplication

fun main(args: Array<String>) {
	runApplication<TravelsApplication>(*args)
}
