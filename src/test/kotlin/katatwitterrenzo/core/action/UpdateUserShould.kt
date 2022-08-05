package katatwitterrenzo.core.action

import katatwitterrenzo.core.action.user.UpdateUser
import katatwitterrenzo.core.domain.user.User
import katatwitterrenzo.core.domain.user.UserData
import katatwitterrenzo.core.domain.user.UserNotFoundException
import katatwitterrenzo.core.infrastructure.repository.user.InMemoryUsers
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UpdateUserShould {

	private val initialUserData = UserData("Jack Bauer","jackba")
	private val updatedUserData = UserData("John Doe", "jackba")


	@Test
	fun `Update an user`(): Unit = runBlocking {
		//Given
		val users = InMemoryUsers()
		val initialUserId = "randomId"
		val updateUser = UpdateUser(users)
		users.save(User(initialUserId, initialUserData.realName, initialUserData.nickname))

		//When
		updateUser(updatedUserData)

		//Then
		val user = users.find(updatedUserData.nickname)
		assertThat(user).isNotNull
		assertThat(user!!.realName).isEqualTo(updatedUserData.realName)
	}

	@Test
	fun `Throw UserNotFoundException when user does not exist`(): Unit = runBlocking {
		//Given
		val users = InMemoryUsers()
		val updateUser = UpdateUser(users)

		//Then
		Assertions.assertThatThrownBy {
			runBlocking { updateUser(updatedUserData) }
		}.isInstanceOf(UserNotFoundException::class.java)
	}
}