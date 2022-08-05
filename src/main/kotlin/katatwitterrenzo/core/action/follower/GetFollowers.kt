package katatwitterrenzo.core.action.follower

import katatwitterrenzo.core.domain.follower.Followers
import org.slf4j.LoggerFactory

class GetFollowers (
    private val followers: Followers
){
    private val logger = LoggerFactory.getLogger(this::class.java)

    suspend operator fun invoke(nickname: String): List<String> {
        logger.info("Listing followers for user with nickname {}", nickname)

        return followers.find(nickname)
    }
}