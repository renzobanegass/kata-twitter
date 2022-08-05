package katatwitterrenzo.core.infrastructure.repository.follower

import katatwitterrenzo.core.domain.follower.Follower
import katatwitterrenzo.core.domain.follower.Followers
import org.slf4j.LoggerFactory

class InMemoryFollowers : Followers {
	private val logger = LoggerFactory.getLogger(this::class.java)

	private val followers = mutableListOf<Follower>()

	override suspend fun find(followedNickname: String): List<String> {

		return listOf()
		//followers.firstOrNull { it.followedNickname == followedNickname}.followerNickname
	}

	override suspend fun save(follower: Follower) {
		logger.info("Saving follower {}", follower.id)
		followers.add(follower)
	}
}