package katatwitterrenzo.core.domain.user

interface Users {
    suspend fun find(nickname: String): User?
    suspend fun save(user: User)
    suspend fun update(user: User)
}