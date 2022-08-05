package katatwitterrenzo.core.action.user

import katatwitterrenzo.core.domain.user.*
import org.slf4j.LoggerFactory

class CreateUser(
	private val users: Users,
) {
	private val logger = LoggerFactory.getLogger(this::class.java)

	suspend operator fun invoke(userData: UserData): User {
		logger.info("Creating user with username {}", userData.nickname)
		if (isUsernameUnavailable(userData.nickname)){
			throw AlreadyExistsException("Username already occupied")
		}
		val user = User(IdGenerator.newRandomId(), userData.realName, userData.nickname)
		users.save(user)
		return user
	}
	private suspend fun isUsernameUnavailable(nickname: String): Boolean = users.find(nickname)?.nickname != null
}