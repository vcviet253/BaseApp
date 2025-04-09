package com.example.mealplanner.data.remote.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class GeminiRequestBody(
    val contents: List<GeminiContentPart>
)

@Serializable
data class GeminiContentPart(
    val parts: List<GeminiPartItem>
)

@Serializable
data class GeminiPartItem(
    val text: String? = null,
    @SerialName("inline_data")
    val inlineData: GeminiInlineData? = null
)

@Serializable
data class GeminiInlineData(
    @SerialName("mime_type")
    val mimeType: String = "image/jpeg",
    val data: String
)