package com.trevorism.error

import com.trevorism.model.exception.ConflictException
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import org.junit.jupiter.api.Test

class ConflictExceptionHandlerTest {

    @Test
    void testConflictIsA409() {
        ConflictExceptionHandler handler = new ConflictExceptionHandler()
        HttpResponse response = handler.handle(request(), new ConflictException("Item with id run already exists in prompt-event"))

        assert HttpStatus.CONFLICT == response.status
    }

    @Test
    void testTheReasonIsPassedToTheCaller() {
        ConflictExceptionHandler handler = new ConflictExceptionHandler()
        HttpResponse response = handler.handle(request(), new ConflictException("Item with id run already exists in prompt-event"))

        assert 409 == response.body()["status"]
        assert response.body()["message"].contains("already exists")
    }

    private static HttpRequest request() {
        return [getPath: { "/object/prompt-event" }] as HttpRequest
    }
}
