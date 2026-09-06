package com.tmsat.tv

data class ActivationRequest(
    val deviceCode: String
)

data class ActivationResponse(
    val active: Boolean,
    val expiresAt: String?,
    val token: String?
)

data class Channel(
    val id: Long,
    val name: String,
    val logoUrl: String?,
    val streamUrl: String
)

/*
Recommended API contract:

POST /api/v1/activate
Body: { "deviceCode": "ABCD-EF12-3456" }

GET /api/v1/categories
Authorization: Bearer <token>

GET /api/v1/channels
GET /api/v1/movies
GET /api/v1/series

Only expose streams/content you own or are licensed to distribute.
*/
