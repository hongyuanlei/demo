package com.thoughtworks.travels.application

import java.time.OffsetDateTime

interface OffsetDateTimeProvider {
    fun now(): OffsetDateTime
}