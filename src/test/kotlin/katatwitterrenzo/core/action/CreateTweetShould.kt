package katatwitterrenzo.core.action

import katatwitterrenzo.core.action.tweet.CreateTweet
import katatwitterrenzo.core.domain.tweet.Tweet
import katatwitterrenzo.core.infrastructure.repository.tweet.InMemoryTweets
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CreateTweetShould {

    private val nickname = "nombre"
    private val firstTweetContent = "tweet test"
    private val secondTweetContent = "second tweet test"
    private val firstTweetExample = Tweet(nickname, firstTweetContent)
    private val secondTweetExample = Tweet(nickname, secondTweetContent)
    private lateinit var tweets: InMemoryTweets
    private lateinit var createTweet: CreateTweet


    @BeforeEach
    fun setup(){
        tweets = InMemoryTweets()
        createTweet = CreateTweet(tweets)
    }

    @Test
    fun `create a tweet`(): Unit = runBlocking{
        //When
        createTweet(firstTweetExample)
        //Then
        assertThat(tweets.get(nickname)).isNotEmpty
        assertThat(tweets.get(nickname).first()).isEqualTo(firstTweetContent)
    }

    @Test
    fun `create a second tweet`(): Unit = runBlocking{
        //Given
        createTweet(firstTweetExample)
        //When
        createTweet(secondTweetExample)
        //Then
        assertThat(tweets.get(nickname).size).isEqualTo(2)
    }
}