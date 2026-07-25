package com.trevorism.model.exception

class ConflictException extends RuntimeException {

    ConflictException(String message) {
        super(message)
    }

    ConflictException(String message, Throwable cause) {
        super(message, cause)
    }
}
