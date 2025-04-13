package com.example.mealplanner.data.remote.dto.gemini

import com.google.gson.Gson
import kotlinx.serialization.Serializable

@Serializable
sealed class GeminiContent {
    data class Image(val base64: String) : GeminiContent()
    data class Text(val text: String) : GeminiContent()
}


fun buildGeminiRequest(contents: List<GeminiContent>): GeminiRequestBody {
    val partItems = contents.map {
        when (it) {
            is GeminiContent.Text -> GeminiPartItem(
                text = it.text
            )
            is GeminiContent.Image -> {
                println(">> Creating image part, base64 = ${it.base64.take(30)}...")

                val part = GeminiPartItem(
                    inlineData = GeminiInlineData(
                        mimeType = "image/jpeg",
                        data = it.base64.trim()
                    )
                )
                println(">> Created image part: ${Gson().toJson(part)}")
                part

            }
        }

    }

    return GeminiRequestBody(
        contents = listOf(GeminiContentPart(parts = partItems))
    )
}