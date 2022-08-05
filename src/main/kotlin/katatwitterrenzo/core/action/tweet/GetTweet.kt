package katatwitterrenzo.core.action.tweet

import katatwitterrenzo.core.domain.tweet.Tweets

class GetTweet(
    private val tweets: Tweets
    ) {
    suspend operator fun invoke(nickname: String): List<String> {
        return tweets.get(nickname)
    }
}
