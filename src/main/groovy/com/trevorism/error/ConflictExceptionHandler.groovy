package com.trevorism.error

import com.trevorism.model.exception.ConflictException
import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Produces
import io.micronaut.http.server.exceptions.ExceptionHandler
import jakarta.inject.Singleton
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Produces
@Singleton
@Requires(classes = [ConflictException, ExceptionHandler])
class ConflictExceptionHandler implements ExceptionHandler<ConflictException, HttpResponse> {

    private static final Logger log = LoggerFactory.getLogger(ConflictExceptionHandler)

    @Override
    HttpResponse handle(HttpRequest request, ConflictException exception) {
        log.warn("Conflict on {}: {}", request?.path, exception.message)
        return HttpResponse.status(HttpStatus.CONFLICT).body([status: 409, message: exception.message])
    }
}
