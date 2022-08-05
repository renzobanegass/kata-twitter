package katatwitterrenzo.core.action

import katatwitterrenzo.core.action.user.CreateUser
import katatwitterrenzo.core.domain.user.AlreadyExistsException
import katatwitterrenzo.core.domain.user.UserData
import katatwitterrenzo.core.infrastructure.repository.user.InMemoryUsers
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class CreateUserShould {

    private val userData = UserData("Jack Bauer", "jackba")

    @Test
    fun `create a new user on the repository`(): Unit = runBlocking {
        // Given
        val users = InMemoryUsers()
        val createUser = CreateUser(users)

        // When
        createUser.invoke(userData)

        // Then
        assertThat(users.find(userData.nickname)).isNotNull
    }

    @Test
    fun `throw AlreadyExistsException if the nickname already exists`(): Unit = runBlocking {
        //Given
        val users = InMemoryUsers()
        val createUser = CreateUser(users)
        createUser.invoke(userData)

        //When
        //Then
        Assertions.assertThatThrownBy {
            runBlocking { createUser.invoke(userData) }
        }.isInstanceOf(AlreadyExistsException::class.java)
    }
}