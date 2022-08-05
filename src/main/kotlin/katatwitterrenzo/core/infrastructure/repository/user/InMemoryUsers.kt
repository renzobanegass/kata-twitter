package katatwitterrenzo.core.infrastructure.repository.user

import katatwitterrenzo.core.domain.user.User
import katatwitterrenzo.core.domain.user.Users
import org.slf4j.LoggerFactory

class InMemoryUsers : Users {
	private val logger = LoggerFactory.getLogger(this::class.java)

	private val users = mutableListOf<User>()

	override suspend fun find(nickname: String): User? {

		return users.firstOrNull { it.nickname == nickname}
	}

	override suspend fun save(user: User) {
		logger.info("Saving user {}", user.id)
		users.add(user)
	}

	override suspend fun update(user: User) {
		logger.info("Updating user {}", user.id)
		val id = users.indexOfFirst { it.nickname == user.nickname}
		users[id] = user
	}
}