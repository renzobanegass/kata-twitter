package katatwitterrenzo.core.domain.follower

import kotlinx.serialization.Serializable

@Serializable
class Follower(
	var id: String,
	var followerNickname: String,
	val followedNickname: String
	)
{
}