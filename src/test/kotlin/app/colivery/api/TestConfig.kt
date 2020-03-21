package app.colivery.api

import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import io.mockk.every
import io.mockk.mockk
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@TestConfiguration
class TestConfig {

    @Bean
    @Primary
    fun createFirebaseApp(): FirebaseApp = mockk()

    @Bean
    @Primary
    fun createFirestore(): Firestore = mockk() {
        every { collection(any()) } returns mockk()
    }
}
