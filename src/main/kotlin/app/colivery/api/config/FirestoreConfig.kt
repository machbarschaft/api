package app.colivery.api.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import java.io.FileInputStream
import javax.validation.constraints.NotBlank
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

const val USER_COLLECTION_NAME = "user"
const val ORDER_ITEM_COLLECTION_NAME = "items"
const val ORDER_COLLECTION_NAME = "order"

@Configuration
@Profile("!test")
class FirestoreConfig {

    @Bean
    fun createFirebaseApp(@NotBlank @Value("\${google.keyLocation}") keyLocation: String): FirebaseApp {
        val stream = if (keyLocation.startsWith("classpath:")) {
            javaClass.getResourceAsStream(keyLocation.substringAfter("classpath:"))
        } else {
            FileInputStream(keyLocation)
        }
        val options = FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(stream))
            .setDatabaseUrl("https://colivery-app.firebaseio.com")
            .build()

        return FirebaseApp.initializeApp(options)
    }

    @Bean
    fun createFirestore(firebaseApp: FirebaseApp): Firestore = FirestoreClient.getFirestore(firebaseApp)
}
