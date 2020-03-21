package app.colivery.api

import com.google.cloud.firestore.DocumentReference
import com.google.cloud.firestore.DocumentSnapshot
import com.google.cloud.firestore.GeoPoint
import java.time.Instant

fun UserCreationDto.asMap(email: String): Map<String, Any> =
    mapOf(
        "address" to address,
        "geo_location" to GeoPoint(geoLocation.latitude, geoLocation.longitude),
        "accepted_privacy_policy" to acceptedPrivacyPolicy,
        "accepted_terms_of_use" to acceptedTermsOfUse,
        "phone" to phone,
        "name" to name,
        "email" to email,
        "accepted_support_inquiry" to acceptedPrivacyPolicy,
        "is_support_member" to false
    )

fun DocumentReference.toUser() = get().get().toUser()

fun DocumentReference.toOrder() = get().get().toOrder()

fun DocumentReference.toOrderItem() = get().get().toOrderItem()

fun DocumentSnapshot.toUser() = FirestoreUser(
    userId = id,
    name = notNull("name", this::getString),
    address = notNull("address", this::getString),
    phone = notNull("phone", this::getString),
    email = notNull("email", this::getString),
    acceptedPrivacyPolicy = notNull("accepted_privacy_policy", this::getBoolean),
    acceptedTermsOfUse = notNull("accepted_terms_of_use", this::getBoolean),
    geoLocation = notNull("geo_location", this::getGeoPoint),
    updated = getUpdated(),
    created = getCreated(),
    acceptedSupportInquiry = notNull("accepted_support_inquiry", this::getBoolean),
    isSupportMember = notNull("is_support_member", this::getBoolean)
)

@Suppress("unchecked_cast")
fun DocumentSnapshot.toOrder() = FirestoreOrder(
    id = id,
    created = getCreated(),
    updated = getUpdated(),
    userId = notNull("user_id", this::getString),
    status = notNull("status", this::getString),
    driverUserId = getString("driver_user_id"),
    hint = notNull("hint", this::getString),
    pickupAddress = getString("pickup_address"),
    pickupLocation = getGeoPoint("pickup_location"),
    shopName = notNull("shop_name", this::getString),
    shopType = notNull("shop_type", this::getString),
    products = (get("products") as List<DocumentReference>).map { it.toOrderItem() }
)

fun DocumentSnapshot.toOrderItem() = FirestoreOrderItem(
    id = id,
    description = notNull("description", this::getString),
    status = notNull("status", this::getString),
    created = getCreated(),
    updated = getUpdated()
)

private inline fun <reified T : Any> notNull(fieldName: String, provider: (fieldName: String) -> T?): T {
    val value: T? = provider(fieldName)
    return requireNotNull(value) { "$fieldName must not be null" }
}

private fun DocumentSnapshot.getUpdated(): Instant = notNull("updated") { this.updateTime?.seconds?.let { Instant.ofEpochSecond(it) } }
private fun DocumentSnapshot.getCreated(): Instant = notNull("created") { this.createTime?.seconds?.let { Instant.ofEpochSecond(it) } }
