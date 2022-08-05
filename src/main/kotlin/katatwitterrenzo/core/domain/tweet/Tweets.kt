package katatwitterrenzo.core.domain.tweet

interface Tweets {

    suspend fun get(nickname: String): List<String>
    suspend fun save(tweet: Tweet)
}