package app.colivery.api.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.cloud.FirestoreClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.io.FileInputStream

const val USER_COLLECTION_NAME = "user"
const val ORDER_ITEM_COLLECTION_NAME = "items"
const val ORDER_COLLECTION_NAME = "order"

@Configuration
@Profile("!test")
class FirestoreConfig {

    @Bean
    fun createFirebaseApp(@Value("\${google.keyLocation:#{null}}") keyLocation: String?,
                        @Value("\${google.applicationUid:#{null}") applicationUid: String?): FirebaseApp {
        val stream = if (keyLocation == null || keyLocation == "") {
            null
        } else if (keyLocation.startsWith("classpath:")) {
            javaClass.getResourceAsStream(keyLocation.substringAfter("classpath:"))
        } else {
            FileInputStream(keyLocation)
        }
        val options = FirebaseOptions.Builder()
            .setProjectId("colivery-app")
            .setDatabaseUrl("https://colivery-app.firebaseio.com")

        if (stream != null) {
            options.setCredentials(GoogleCredentials.fromStream(stream))
        } else {
            options.setCredentials(GoogleCredentials.getApplicationDefault())
        }

        if (applicationUid != null) {
            options.setDatabaseAuthVariableOverride(mapOf("uid" to applicationUid))
        }

        return FirebaseApp.initializeApp(options.build())
    }

    @Bean
    fun createFirestore(firebaseApp: FirebaseApp): Firestore =
        FirestoreClient.getFirestore(firebaseApp)

    @Bean
    fun createFirebaseAuth(firebaseApp: FirebaseApp) =
        FirebaseAuth.getInstance(firebaseApp)

}
