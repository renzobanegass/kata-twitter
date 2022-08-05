package katatwitterrenzo.core.domain.follower

import kotlinx.serialization.Serializable

@Serializable
class FollowerData (
    val followerNickname: String,
    val followedNickname: String
){

}
