package app.colivery.api.api

import app.colivery.api.BadRequestException
import app.colivery.api.InternalServerException
import app.colivery.api.NotFoundException
import app.colivery.api.PermissionException
import org.apache.catalina.connector.ClientAbortException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class ExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(PermissionException::class)
    fun handleForbidden(ex: PermissionException): ResponseEntity<Map<String, String?>> {
        logger.info("forbidden")
        return ResponseEntity(mapOf("error" to "forbidden", "message" to ex.message), HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(ex: BadRequestException): ResponseEntity<Map<String, String?>> {
        logger.info("Bad request - ${ex.message}")
        return ResponseEntity(mapOf("error" to ex.message), HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(ex: NotFoundException): ResponseEntity<Map<String, String?>> {
        logger.info("Not found - ${ex.message}")
        return ResponseEntity(mapOf("error" to ex.message), HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(InternalServerException::class)
    fun handleInternalServerError(ex: InternalServerException): ResponseEntity<Map<String, String?>> {
        logger.warn("Internal server error - ${ex.message}")
        return ResponseEntity(mapOf("error" to ex.message), HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(ClientAbortException::class)
    fun handleClientAbortException(ex: ClientAbortException) {
        logger.warn("Client aborted request.")
        if (logger.isDebugEnabled) {
            logger.debug("full stacktrace:", ex)
        }
    }

    @ExceptionHandler(Exception::class)
    fun handleOtherExceptions(ex: Exception): ResponseEntity<Map<String, String?>> {
        logger.error("Unhandled exception", ex)
        return ResponseEntity(mapOf("error" to ex.javaClass.simpleName, "message" to ex.message), HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
