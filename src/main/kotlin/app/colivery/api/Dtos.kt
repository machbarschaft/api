package app.colivery.api

import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.google.cloud.firestore.GeoPoint
import java.time.Instant
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import org.springframework.validation.annotation.Validated

@Validated
data class LatLng(
    @NotNull
    val latitude: Double,
    @NotNull
    val longitude: Double
)

@Validated
@JsonNaming(SnakeCaseStrategy::class)
data class UserCreationDto(
    @NotBlank
    val address: String,
    @NotNull
    @Valid
    val geoLocation: LatLng,
    @NotNull
    val acceptedPrivacyPolicy: Boolean,
    @NotNull
    val acceptedTermsOfUse: Boolean,
    @NotNull
    val acceptedSupportInquiry: Boolean,
    @NotBlank
    val phone: String,
    @NotBlank
    val name: String
)

@Validated
@JsonNaming(SnakeCaseStrategy::class)
data class OrderCreationDto(
    @NotBlank
    val pickupAddress: String,
    @NotBlank
    val pickupLocation: LatLng,
    @NotBlank
    val shopName: String,
    @NotBlank
    val shopType: String,
    @NotBlank
    val status: String,
    @NotBlank
    val hint: String,
    @NotNull
    val dropoffLocation: LatLng,
    val userId: String?,
    val supportUser: String?,
    @NotEmpty
    val items: List<OrderItemCreationDto>
)

@Validated
@JsonNaming(SnakeCaseStrategy::class)
data class OrderItemCreationDto(
    @NotBlank
    val description: String,
    @NotBlank
    val status: String
)

@JsonNaming(SnakeCaseStrategy::class)
data class FirestoreUser(
    val address: String,
    val geoLocation: GeoPoint,
    val acceptedPrivacyPolicy: Boolean,
    val acceptedTermsOfUse: Boolean,
    val phone: String,
    val userId: String,
    val name: String,
    val updated: Instant,
    val created: Instant,
    val email: String,
    val acceptedSupportInquiry: Boolean,
    val isSupportMember: Boolean
)

data class FirestoreOrder(
    val id: String,
    val created: Instant,
    val updated: Instant,
    val driverUserId: String?,
    val hint: String,
    val pickupAddress: String?,
    val pickupLocation: GeoPoint?,
    val shopName: String,
    val shopType: String,
    val status: String,
    val userId: String,
    val items: List<FirestoreOrderItem>
)

data class FirestoreOrderItem(
    val id: String,
    val description: String,
    val status: String,
    val created: Instant,
    val updated: Instant
)
