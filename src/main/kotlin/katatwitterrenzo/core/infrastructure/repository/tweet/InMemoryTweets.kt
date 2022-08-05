package katatwitterrenzo.core.infrastructure.repository.tweet

import katatwitterrenzo.core.domain.tweet.Tweet
import katatwitterrenzo.core.domain.tweet.Tweets

class InMemoryTweets: Tweets {

    private val tweets = mutableListOf<Tweet>()

    override suspend fun get(nickname: String): List<String> {
        return tweets.filter { it.nickname == nickname }.map { it.content }
    }

    override suspend fun save(tweet: Tweet) {
        tweets.add(tweet)
    }
}
