package katatwitterrenzo.core.infrastructure.repository

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import katatwitterrenzo.core.domain.user.User
import katatwitterrenzo.core.infrastructure.config.DynamoDbClientConfig
import katatwitterrenzo.core.infrastructure.repository.user.DynamoUsers
import katatwitterrenzo.helpers.DynamoDbHelper
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DynamoUsersShould {

    private val TABLENAME = "UsersTest"

    private lateinit var client: DynamoDbClient
    private lateinit var users: DynamoUsers
    private var exampleUser = User("random", "Jack Bauer", "jackba")

    @BeforeAll
    fun setupEnvironment(): Unit = runBlocking{
        client = DynamoDbClientConfig.client
        DynamoDbHelper.createTable(client, TABLENAME, "Nickname")
    }

    @BeforeEach
    fun setup(): Unit = runBlocking{
        users = DynamoUsers(client, TABLENAME)
    }

    @AfterAll
    fun cleanup(): Unit = runBlocking{
        DynamoDbHelper.deleteTable(client, TABLENAME)
    }

    @Test
    fun `be empty at initialization`(): Unit = runBlocking {
        val userList = users.allUsers()
        assertThat(userList).isEmpty()
    }

    @Test
    fun `create a user item`(): Unit = runBlocking {
        //When
        users.save(exampleUser)
        val userList = users.allUsers()
        //Then
        assertThat(userList.count()).isEqualTo(1)
    }

    @Test
    fun `find a user by nickname`(): Unit = runBlocking {
        //Given
        users.save(exampleUser)
        //When
        val user = users.find(exampleUser.nickname)
        //Then
        assertThat(user!!.nickname).isEqualTo(exampleUser.nickname)
    }

    @Test
    fun `update an user`(): Unit = runBlocking {
        //Given
        users.save(exampleUser)
        //When
        exampleUser.realName = "jackknife"
        users.update(exampleUser)
        val user = users.find(exampleUser.nickname)
        //Then
        assertThat(user!!.realName).isEqualTo(exampleUser.realName)
    }


}
