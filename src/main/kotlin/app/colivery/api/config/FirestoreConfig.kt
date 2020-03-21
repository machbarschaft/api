package app.colivery.api.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.io.Resource

const val USER_COLLECTION_NAME = "user"
const val ORDER_ITEM_COLLECTION_NAME = "order_item"
const val ORDER_COLLECTION_NAME = "order"

@Configuration
@Profile("!test")
class FirestoreConfig {

    @Bean
    fun createFirebaseApp(@Value("classpath:serviceAccountKey.json") serviceAccount: Resource): FirebaseApp {
        val options = FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount.inputStream))
            .setDatabaseUrl("https://colivery-app.firebaseio.com")
            .build()

        return FirebaseApp.initializeApp(options)
    }

    @Bean
    fun createFirestore(firebaseApp: FirebaseApp): Firestore = FirestoreClient.getFirestore(firebaseApp)
}
