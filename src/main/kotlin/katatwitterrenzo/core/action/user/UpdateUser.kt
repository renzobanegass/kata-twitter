package katatwitterrenzo.core.action.user

import katatwitterrenzo.core.domain.user.UserData
import katatwitterrenzo.core.domain.user.UserNotFoundException
import katatwitterrenzo.core.domain.user.Users

class UpdateUser(private val users: Users) {
	suspend operator fun invoke(userData: UserData) {
		val user = users.find(userData.nickname) ?: throw UserNotFoundException(userData.nickname)
		user.realName = userData.realName
		users.update(user)
	}
}