package katatwitterrenzo.core.action

import katatwitterrenzo.core.action.follower.FollowUser
import katatwitterrenzo.core.action.user.CreateUser
import katatwitterrenzo.core.domain.follower.FollowerData
import katatwitterrenzo.core.domain.user.UserData
import katatwitterrenzo.core.domain.user.UserNotFoundException
import katatwitterrenzo.core.infrastructure.repository.follower.InMemoryFollowers
import katatwitterrenzo.core.infrastructure.repository.user.InMemoryUsers
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class FollowUserShould {

    private val firstUserData= UserData("Jack Bauer", "jackba")
    private val secondUserData = UserData("Jackie Bauer", "jackieba")
    private val followerData = FollowerData("jackba", "jackieba")

    @Test
    fun `create a follower that contains both the follower and the followed users`(): Unit = runBlocking {
        // Given
        val users = InMemoryUsers()
        val createUser = CreateUser(users)
        val followers = InMemoryFollowers()
        val followUser = FollowUser(followers, users)
        createUser.invoke(firstUserData)
        createUser.invoke(secondUserData)

        // When
        followUser.invoke(followerData)

        // Then
        assertThat(followers.find(followerData.followedNickname)).isNotNull
    }

    @Test
    fun `throw UserNotFoundException if either users do not exist`(): Unit = runBlocking {
        //Given
        val users = InMemoryUsers()
        val followers = InMemoryFollowers()
        val followUser = FollowUser(followers, users)

        //When
        //Then
        Assertions.assertThatThrownBy {
            runBlocking { followUser.invoke(followerData) }
        }.isInstanceOf(UserNotFoundException::class.java)
    }
}