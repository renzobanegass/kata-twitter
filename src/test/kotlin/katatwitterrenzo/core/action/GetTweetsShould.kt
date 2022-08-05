package katatwitterrenzo.core.action

import katatwitterrenzo.core.action.tweet.GetTweet
import katatwitterrenzo.core.domain.tweet.Tweet
import katatwitterrenzo.core.domain.user.UserNotFoundException
import katatwitterrenzo.core.infrastructure.repository.tweet.InMemoryTweets
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GetTweetsShould {


    private val exampleContent = "tweet test"
    private val exampleNickname = "jackba"
    private val secondExampleContent = "tweets test"
    private val exampleTweet = Tweet(exampleNickname, exampleContent)
    private val secondExampleTweet = Tweet(exampleNickname, secondExampleContent)
    private lateinit var tweets: InMemoryTweets
    private lateinit var getTweet: GetTweet

    @BeforeEach
    fun setup(){
        tweets = InMemoryTweets()
        getTweet = GetTweet(tweets)
    }

    @Test
    fun `get a tweet`(): Unit = runBlocking{
        //Given
        tweets.save(exampleTweet)
        //When
        val tweetList = getTweet(exampleNickname)
        //Then
        assertThat(tweetList.first()).isEqualTo(exampleContent)
    }

    @Test
    fun `get multiple tweets`(): Unit = runBlocking{
        //Given
        tweets.save(exampleTweet)
        tweets.save(secondExampleTweet)
        //When
        val tweetList = getTweet(exampleNickname)
        //Then
        assertThat(tweetList.count()).isEqualTo(2)
        assertThat(tweetList).contains(secondExampleContent)
    }
    @Test
    fun `return an empty list`(): Unit = runBlocking{
        //When
        val tweetList = getTweet(exampleNickname)
        //Then
        assertThat(tweetList).isEmpty()
    }
}