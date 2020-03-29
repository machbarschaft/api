package app.colivery.api

import com.google.cloud.firestore.DocumentReference
import com.google.cloud.firestore.DocumentSnapshot
import com.google.cloud.firestore.GeoPoint
import java.time.Instant

fun UserCreationDto.asMap(email: String): Map<String, Any> =
    mapOf(
        "address" to address,
        "geo_location" to geoLocation.toGeoPoint(),
        "accepted_privacy_policy" to acceptedPrivacyPolicy,
        "accepted_terms_of_use" to acceptedTermsOfUse,
        "phone" to phone,
        "first_name" to firstName,
        "last_name" to lastName,
        "email" to email,
        "accepted_support_inquiry" to acceptedPrivacyPolicy,
        "is_support_member" to false
    )

fun OrderCreationDto.asMap(userId: String, status: String): Map<String, Any?> =
    mapOf(
        "pickup_address" to pickupAddress,
        "pickup_location" to pickupLocation?.toGeoPoint(),
        "shop_name" to shopName,
        "shop_type" to shopType,
        "status" to status,
        "hint" to hint,
        "dropoff_location" to dropoffLocation.toGeoPoint(),
        "user_id" to userId,
        "support_user" to supportUser,
        "pickup_location_geohash" to pickupLocationGeohash,
        "dropoff_location_geohash" to dropoffLocationGeohash
    )

fun OrderItemCreationDto.asMap() = mapOf<String, Any>(
    "description" to description,
    "status" to status
)

fun DocumentReference.toUser() = get().get().toUser()

fun DocumentReference.toOrder(items: List<FirestoreOrderItem>) = get().get().toOrder(items = items)

fun DocumentReference.toOrderItem() = get().get().toOrderItem()

fun DocumentSnapshot.toUser() = FirestoreUser(
    userId = id,
    firstName = notNull("first_name", this::getString),
    lastName = notNull("last_name", this::getString),
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
fun DocumentSnapshot.toOrder(items: List<FirestoreOrderItem>) = FirestoreOrder(
    id = id,
    created = getCreated(),
    updated = getUpdated(),
    userId = notNull("user_id", this::getString),
    status = notNull("status", this::getString),
    driverUserId = getString("driver_user_id"),
    hint = notNull("hint", this::getString),
    pickupAddress = getString("pickup_address"),
    pickupLocation = getGeoPoint("pickup_location"),
    shopName = getString("shop_name"),
    shopType = notNull("shop_type", this::getString),
    items = items,
    dropoffLocation = getGeoPoint("dropoff_location"),
    dropoffLocationGeohash = getString("dropoff_location_geohash"),
    pickupLocationGeohash = getString("pickup_location_geohash")
)

@Suppress("unchecked_cast")
fun DocumentSnapshot.toOwnOrderDao(items: List<FirestoreOrderItem>, creator: FirestoreUser) = OwnOrderDao(
    order = this.toOrder(items),
    creator = creator
)

fun DocumentSnapshot.toOrderItem() = FirestoreOrderItem(
    id = id,
    description = notNull("description", this::getString),
    status = notNull("status", this::getString),
    created = getCreated(),
    updated = getUpdated()
)

fun LatLng.toGeoPoint() = GeoPoint(latitude, longitude)

inline fun <reified T : Any> notNull(fieldName: String, provider: (fieldName: String) -> T?): T {
    val value: T? = provider(fieldName)
    return requireNotNull(value) { "$fieldName must not be null" }
}

private fun DocumentSnapshot.getUpdated(): Instant? = updateTime?.seconds?.let { Instant.ofEpochSecond(it) }
private fun DocumentSnapshot.getCreated(): Instant? = createTime?.seconds?.let { Instant.ofEpochSecond(it) }
