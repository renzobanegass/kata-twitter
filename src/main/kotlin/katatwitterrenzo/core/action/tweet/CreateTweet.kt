package katatwitterrenzo.core.action.tweet

import katatwitterrenzo.core.domain.tweet.Tweet
import katatwitterrenzo.core.domain.tweet.Tweets

class CreateTweet(private val tweets: Tweets) {
    suspend operator fun invoke(tweet: Tweet) {
        tweets.save(tweet)
    }
}
