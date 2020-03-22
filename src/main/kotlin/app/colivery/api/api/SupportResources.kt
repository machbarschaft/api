package app.colivery.api.api

import app.colivery.api.FirestoreUser
import app.colivery.api.ForbiddenException
import app.colivery.api.UnauthorizedException
import app.colivery.api.UserCreationDto
import app.colivery.api.config.AuthContext
import app.colivery.api.config.SecurityUtils
import app.colivery.api.service.UserService
import javax.validation.Valid
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

//@RestController
//@RequestMapping("/support")
//class SupportResources(
//    private val securityUtils: SecurityUtils,
//    private val userService: UserService
//) {
//
//    @PostMapping(consumes = [APPLICATION_JSON_VALUE])
//    fun createUser(@Valid @RequestBody userCreationDto: UserCreationDto) {
//        val (uid, email) = securityUtils.principal
//            ?: throw UnauthorizedException()
//
//        val currentUser = userService.findUser(uid)
//        if (!currentUser.isSupportMember) {
//            throw ForbiddenException("Only support users are allowed to use this resource")
//        }
//
//        userService.createUser(userCreationDto, userId = uid, email = email)
//    }
//
//    @GetMapping(produces = [APPLICATION_JSON_VALUE])
//    fun getUser(@RequestParam(name = "user_id") userId: String): FirestoreUser {
//        return userService.findUser(userId = userId)
//    }
//
//    private fun forbidden() = throw ForbiddenException("Only support users are allowed to use this resource")
//}
