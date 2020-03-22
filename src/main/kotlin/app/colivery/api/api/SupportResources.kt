package app.colivery.api.api

import app.colivery.api.ForbiddenException
import app.colivery.api.UnauthorizedException
import app.colivery.api.UserCreationDto
import app.colivery.api.config.AuthContext
import app.colivery.api.config.SecurityUtils
import app.colivery.api.service.UserService
import javax.validation.Valid
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/support")
class SupportResources(
    private val securityUtils: SecurityUtils,
    private val userService: UserService
) {

    @PostMapping("/create_user", consumes = [APPLICATION_JSON_VALUE])
    fun createUser(@RequestParam(name = "user_id") userId: String, @RequestParam(name = "email") email: String, @Valid @RequestBody userCreationDto: UserCreationDto) {
        checkIsSupport()
        userService.createUser(userCreationDto, userId = userId, email = email)
    }

    private fun checkIsSupport(): AuthContext {
        val authContext = securityUtils.principal
            ?: throw UnauthorizedException()

        val currentUser = userService.findUser(authContext.uid)
        if (!currentUser.isSupportMember) {
            throw ForbiddenException("Only support users are allowed to use this resource")
        }
        throw ForbiddenException("Only support users are allowed to use this resource")
    }
}
