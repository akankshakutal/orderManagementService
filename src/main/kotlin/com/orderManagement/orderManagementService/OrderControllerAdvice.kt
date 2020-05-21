package com.orderManagement.orderManagementService

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class OrderControllerAdvice {

    @ExceptionHandler(InvalidQuantityException::class)
    @ResponseStatus(value = HttpStatus.OK)
    fun handleUserNotFoundException(exception: InvalidQuantityException): ErrorData {
        return ErrorData("Invalid Quantity is provided", 400)
    }
}

data class ErrorData(val errorDescription: String, val errorCode: Int)
