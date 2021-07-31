package dev.surratt.spring.integration.spike

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.integration.config.EnableIntegration

@SpringBootApplication
@EnableIntegration
class IntegrationSpikeApplication

fun main(args: Array<String>) {
	runApplication<IntegrationSpikeApplication>(*args)
}
