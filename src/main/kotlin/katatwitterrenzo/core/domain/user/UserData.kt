package katatwitterrenzo.core.domain.user

import kotlinx.serialization.Serializable

@Serializable
data class UserData(
     val realName: String,
     val nickname: String
){}
