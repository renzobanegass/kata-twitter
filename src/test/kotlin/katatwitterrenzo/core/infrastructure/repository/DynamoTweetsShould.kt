package katatwitterrenzo.core.infrastructure.repository

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import katatwitterrenzo.core.domain.tweet.Tweet
import katatwitterrenzo.core.infrastructure.config.DynamoDbClientConfig
import katatwitterrenzo.core.infrastructure.repository.tweet.DynamoTweets
import katatwitterrenzo.helpers.DynamoDbHelper
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DynamoTweetsShould {

    private val TABLENAME = "TweetsTest"

    private lateinit var client: DynamoDbClient
    private lateinit var tweets: DynamoTweets
    private val exampleNickname = "jackba"
    private val secondExampleNickname = "johndoe"
    private val exampleContent = "test tweet"
    private val exampleTweet = Tweet(exampleNickname, exampleContent)
    private val secondExampleTweet = Tweet(secondExampleNickname, exampleContent)

    @BeforeAll
    fun setupEnvironment(): Unit = runBlocking{
        client = DynamoDbClientConfig.client
        DynamoDbHelper.createTable(client, TABLENAME, "Nickname")
    }

    @BeforeEach
    fun setup(): Unit = runBlocking{
        tweets = DynamoTweets(client, TABLENAME)
    }

    @AfterAll
    fun cleanup(): Unit = runBlocking{
        DynamoDbHelper.deleteTable(client, TABLENAME)
    }

    @Test
    fun `be empty at initialization`(): Unit = runBlocking {
        //When
        val tweetList = tweets.getAll()
        //Then
        assertThat(tweetList).isEmpty()
    }

    @Test
    fun `create a tweet item in database`(): Unit = runBlocking {
        //When
        tweets.save(exampleTweet)
        //Then
        val tweetList = tweets.get(exampleNickname)
        assertThat(tweetList.count()).isEqualTo(1)
    }

    @Test
    fun `get tweet by nickname`(): Unit = runBlocking {
        //When
        tweets.save(exampleTweet)
        tweets.save(secondExampleTweet)
        //Then
        val tweetList = tweets.get(exampleNickname)
        assertThat(tweetList.first()).isEqualTo(exampleContent)
        assertThat(tweetList.count()).isEqualTo(1)
    }
}
