package katatwitterrenzo.core.infrastructure.repository.user

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.*
import katatwitterrenzo.core.domain.user.User
import katatwitterrenzo.core.domain.user.Users


class DynamoUsers(private val client: DynamoDbClient, private val TABLENAME: String = "Users") : Users {

	private val ID = "Id"
	private val NICKNAME = "Nickname"
	private val REALNAME = "RealName"

	override suspend fun find(nickname: String): User? {
		val returnedItem = getItemResponse(nickname)
		val userMap = returnedItem.item ?: return null
		return mapUser(userMap)
	}

	private fun mapUser(userMap: Map<String, AttributeValue>): User? {

		val id = userMap.getValue(ID).asSOrNull()
		val realName = userMap.getValue(REALNAME).asSOrNull()
		val nickname = userMap.getValue(NICKNAME).asSOrNull()

		if (id == null || realName == null || nickname == null) return null

		return User(id, realName, nickname)
	}

	private suspend fun getItemResponse(nickname: String): GetItemResponse {
		val keyToGet = mutableMapOf<String, AttributeValue>()
		keyToGet[NICKNAME] = AttributeValue.S(nickname)

		val request = GetItemRequest {
			key = keyToGet
			tableName = TABLENAME
		}
		return client.getItem(request)
	}

	override suspend fun save(user: User) {
		val request = putItemRequest(user)
		client.putItem(request)
		println(" A new item was placed into $TABLENAME.")
	}

	private fun putItemRequest(user: User): PutItemRequest {
		val itemValues = mutableMapOf<String, AttributeValue>()

		// Add all content to the table.
		itemValues[ID] = AttributeValue.S(user.id)
		itemValues[NICKNAME] = AttributeValue.S(user.nickname)
		itemValues[REALNAME] = AttributeValue.S(user.realName)

		val request = PutItemRequest {
			tableName = TABLENAME
			item = itemValues
		}
		return request
	}

	override suspend fun update(user: User) {
		val request = updateItemRequest(user)
		client.updateItem(request)
		println("Item in $TABLENAME was updated")
	}

	private fun updateItemRequest(user: User): UpdateItemRequest {
		val itemKey = mutableMapOf<String, AttributeValue>()
		itemKey[NICKNAME] = AttributeValue.S(user.nickname)

		val updatedValues = mutableMapOf<String, AttributeValueUpdate>()
		updatedValues[REALNAME] = AttributeValueUpdate {
			value = AttributeValue.S(user.realName)
			action = AttributeAction.Put
		}

		val request = UpdateItemRequest {
			tableName = TABLENAME
			key = itemKey
			attributeUpdates = updatedValues
		}
		return request
	}

	suspend fun allUsers(): Collection<User> {
		val users = ArrayList<User>()
		buildUserList(users)
		return users
	}

	private suspend fun buildUserList(users: ArrayList<User>) {
		val request = ScanRequest {
			tableName = TABLENAME
		}
		val response = client.scan(request)
		response.items?.forEach { item ->
			users.add(mapUser(item)!!)
		}
	}
}


