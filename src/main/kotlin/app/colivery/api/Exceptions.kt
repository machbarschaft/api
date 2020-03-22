package app.colivery.api

import java.util.UUID

class NotFoundException(message: String, ex: Exception? = null) : RuntimeException(message, ex) {
    constructor(entity: String, id: UUID, ex: Exception? = null) : this("$entity with ID $id doesn't exist", ex)
}

class PermissionException(message: String, ex: Exception? = null) : RuntimeException(message, ex)

class BadRequestException(message: String, ex: Exception? = null) : RuntimeException(message, ex)

class InternalServerException(message: String, ex: Exception? = null) : RuntimeException(message, ex)

class UnauthorizedException(message: String? = null, ex: Exception? = null) : RuntimeException(message, ex)

class ForbiddenException(message: String? = null, ex: Exception? = null): RuntimeException(message, ex)
