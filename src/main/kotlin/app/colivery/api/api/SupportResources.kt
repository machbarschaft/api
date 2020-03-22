package app.colivery.api.api

// @RestController
// @RequestMapping("/support")
// class SupportResources(
//    private val securityUtils: SecurityUtils,
//    private val userService: UserService
// ) {
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
// }
