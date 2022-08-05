package katatwitterrenzo.core.domain.follower

interface Followers {

    suspend fun find(nickname: String): List<String>
    suspend fun save(follower: Follower)
}