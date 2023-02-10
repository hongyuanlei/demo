package com.thoughtworks.travels.adapters.secondary.rest

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.time.Duration

@Configuration
class Configuration {

    @Bean
    fun paymentServiceWebClient(@Value("\${app.paymentServiceUrl}") exchangeRateServiceUrl: String): WebClient {
        val client: HttpClient = HttpClient.create()
            .responseTimeout(Duration.ofSeconds(10))
        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(client))
            .baseUrl(exchangeRateServiceUrl)
            .build();
    }
}