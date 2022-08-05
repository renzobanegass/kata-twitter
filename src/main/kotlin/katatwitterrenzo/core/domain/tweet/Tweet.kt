package katatwitterrenzo.core.domain.tweet

import kotlinx.serialization.Serializable

@Serializable
class Tweet (
    var nickname: String,
    var content: String
)
