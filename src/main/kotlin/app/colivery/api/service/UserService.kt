package app.colivery.api.service

import app.colivery.api.FirestoreUser
import app.colivery.api.UserCreationDto
import app.colivery.api.asMap
import app.colivery.api.client.FirestoreClient
import org.springframework.stereotype.Service

@Service
class UserService(private val firestoreClient: FirestoreClient) {

    fun createUser(userCreationDto: UserCreationDto, userId: String, email: String) {
        firestoreClient.saveUser(userId = userId, userDetails = userCreationDto.asMap(email = email))
    }

    fun findUser(userId: String): FirestoreUser {
        return firestoreClient.findUser(userId = userId)
    }

    fun deleteUser(userId: String) {
        return firestoreClient.deleteUser(userId)
    }
}
