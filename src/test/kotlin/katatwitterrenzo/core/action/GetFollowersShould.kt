package katatwitterrenzo.core.action

import katatwitterrenzo.core.action.follower.FollowUser
import katatwitterrenzo.core.action.follower.GetFollowers
import katatwitterrenzo.core.action.user.CreateUser
import katatwitterrenzo.core.domain.follower.FollowerData
import katatwitterrenzo.core.domain.user.UserData
import katatwitterrenzo.core.infrastructure.repository.follower.InMemoryFollowers
import katatwitterrenzo.core.infrastructure.repository.user.InMemoryUsers
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class GetFollowersShould {

    private val firstUserData= UserData("Jack Bauer", "jackba")
    private val secondUserData = UserData("Jackie Bauer", "jackieba")
    private val thirdUserData= UserData("Joe Doe", "joedo")
    private val firstFollowerData = FollowerData("jackba", "joedo")
    private val secondFollowerData = FollowerData("joedo", "jackieba")


    @Test
    fun `return a list of followers for a certain nickname`(): Unit = runBlocking {
        // Given
        val users = InMemoryUsers()
        val createUser = CreateUser(users)
        val followers = InMemoryFollowers()
        val followUser = FollowUser(followers, users)
        val getFollowers = GetFollowers(followers)
        createUser.invoke(firstUserData)
        createUser.invoke(secondUserData)
        createUser.invoke(thirdUserData)
        followUser.invoke(firstFollowerData)
        followUser.invoke(secondFollowerData)
        // When
        val followerList = getFollowers.invoke("jackieba")

        // Then
        assertThat(followerList.size == 1)
    }
}