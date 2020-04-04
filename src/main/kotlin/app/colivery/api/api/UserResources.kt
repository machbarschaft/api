package app.colivery.api.api

import app.colivery.api.FirestoreUser
import app.colivery.api.UnauthorizedException
import app.colivery.api.UserCreationDto
import app.colivery.api.config.AuthContext
import app.colivery.api.config.SecurityUtils
import app.colivery.api.service.UserService
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/user")
class UserResources(
    private val securityUtils: SecurityUtils,
    private val userService: UserService
) {

    @GetMapping("/principal", produces = [APPLICATION_JSON_VALUE])
    fun getAuthContext(): AuthContext? {
        return securityUtils.principal
    }

    @PostMapping(consumes = [APPLICATION_JSON_VALUE])
    fun createUser(@Valid @RequestBody userCreationDto: UserCreationDto) {
        val authContext = securityUtils.principal
            ?: throw UnauthorizedException()

        userService.createUser(userCreationDto, userId = authContext.uid, email = authContext.email)
    }

    @GetMapping("/other", produces = [APPLICATION_JSON_VALUE])
    fun getUserById(@RequestParam(name = "user_id") userId: String): FirestoreUser {
        securityUtils.principal
            ?: throw UnauthorizedException()
        return userService.findUser(userId = userId)
    }

    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    fun getUser(): FirestoreUser {
        val (userId) = securityUtils.principal
            ?: throw UnauthorizedException()
        return userService.findUser(userId = userId)
    }

    @DeleteMapping("/own")
    fun deleteSelf() {
        val userId = securityUtils.principal?.uid ?: throw UnauthorizedException()
        this.userService.deleteUser(userId)
    }
}
