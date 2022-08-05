package katatwitterrenzo.core.infrastructure.repository

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import katatwitterrenzo.core.domain.follower.Follower
import katatwitterrenzo.core.infrastructure.config.DynamoDbClientConfig
import katatwitterrenzo.core.infrastructure.repository.follower.DynamoFollowers
import katatwitterrenzo.helpers.DynamoDbHelper
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)

class DynamoFollowersShould {

    private val TABLENAME = "FollowersTest"

    private lateinit var client: DynamoDbClient
    private lateinit var followers: DynamoFollowers
    private var exampleFollower = Follower("randomIdOne", "johndoe", "jackba")
    private var secondExampleFollower = Follower("randomIdTwo", "jackba", "elonmusk")

    @BeforeAll
    fun setupEnvironment(): Unit = runBlocking {
        client = DynamoDbClientConfig.client
        DynamoDbHelper.createTable(client, TABLENAME, "FollowedNickname")
    }

    @BeforeEach
    fun setup(): Unit = runBlocking {
        followers = DynamoFollowers(client, TABLENAME)
    }

    @AfterAll
    fun cleanup(): Unit = runBlocking {
        DynamoDbHelper.deleteTable(client, TABLENAME)
    }

    @Test
    fun `be empty at initialization`(): Unit = runBlocking {
        val followerList = followers.find("random")
        Assertions.assertThat(followerList).isEmpty()
    }

    @Test
    fun `create a follower item`(): Unit = runBlocking {
        //When
        followers.save(exampleFollower)
        val followerList = followers.find(exampleFollower.followedNickname)
        //Then
        Assertions.assertThat(followerList.count()).isEqualTo(1)
    }

    @Test
    fun `find all followers by nickname of the followed user`(): Unit = runBlocking {
        //Given
        followers.save(exampleFollower)
        followers.save(secondExampleFollower)
        //When
        val followerList = followers.find(exampleFollower.followedNickname)
        //Then
        Assertions.assertThat(followerList.count()).isEqualTo(1)
    }
}