package com.thoughtworks.travels.application.impl

import com.thoughtworks.travels.application.OffsetDateTimeProvider
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.OffsetDateTime
import java.time.ZoneId

@Component
class OffsetDateTimeProviderImpl: OffsetDateTimeProvider {
    private val clock: Clock = Clock.system(ZoneId.of("Asia/Shanghai"))

    override fun now(): OffsetDateTime = OffsetDateTime.now(clock)
}