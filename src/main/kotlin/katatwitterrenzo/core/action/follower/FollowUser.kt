package katatwitterrenzo.core.action.follower

import katatwitterrenzo.core.domain.follower.Follower
import katatwitterrenzo.core.domain.follower.FollowerData
import katatwitterrenzo.core.domain.follower.Followers
import katatwitterrenzo.core.domain.user.IdGenerator
import katatwitterrenzo.core.domain.user.UserNotFoundException
import katatwitterrenzo.core.domain.user.Users
import org.slf4j.LoggerFactory

class FollowUser(
    private val followers: Followers,
    private val users: Users
){
    private val logger = LoggerFactory.getLogger(this::class.java)

    suspend operator fun invoke(followerData: FollowerData): Follower {
        logger.info("Following user with nickname {}", followerData.followedNickname)

        if (users.find(followerData.followedNickname) == null) throw UserNotFoundException(followerData.followedNickname)
        if (users.find(followerData.followerNickname) == null) throw UserNotFoundException(followerData.followerNickname)

        val follower = Follower(IdGenerator.newRandomId(), followerData.followerNickname, followerData.followedNickname)
        followers.save(follower)
        return follower
    }
}