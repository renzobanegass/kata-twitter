package katatwitterrenzo.core.infrastructure.repository.follower

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.*
import katatwitterrenzo.core.domain.follower.Follower
import katatwitterrenzo.core.domain.follower.Followers


class DynamoFollowers(private val client: DynamoDbClient, private val TABLENAME: String = "Followers") : Followers {

	private val FOLLOWERNICKNAME = "FollowerNickname"
	private val FOLLOWEDNICKNAME = "FollowedNickname"

	override suspend fun find(nickname: String): List<String> {
		val returnedItem = getItemResponse(nickname)
		val followerMap = returnedItem.item ?: return listOf()
		return mapFollowers(followerMap)
	}

	private fun mapFollowers(followerMap: Map<String, AttributeValue>): List<String> {
		val followers = followerMap.getValue(FOLLOWERNICKNAME)
		return if (followers.asSOrNull().isNullOrEmpty())
			followers.asSsOrNull() ?: listOf()
		else
			listOf(followers.asS())
	}

	private suspend fun getItemResponse(nickname: String): GetItemResponse {
		val keyToGet = mutableMapOf<String, AttributeValue>()
		keyToGet[FOLLOWEDNICKNAME] = AttributeValue.S(nickname)

		val request = GetItemRequest {
			key = keyToGet
			tableName = TABLENAME
		}

		return client.getItem(request)
	}

	override suspend fun save(follower: Follower) {
		val request = updateItemRequest(follower)
		client.updateItem(request)
		println(" A new item was placed into $TABLENAME.")
	}

	private fun updateItemRequest(follower: Follower): UpdateItemRequest {
		val itemKey = mutableMapOf<String, AttributeValue>()
		itemKey[FOLLOWEDNICKNAME] = AttributeValue.S(follower.followedNickname)

		val updatedValues = mutableMapOf<String, AttributeValueUpdate>()
		updatedValues[FOLLOWERNICKNAME] = AttributeValueUpdate {
			value = AttributeValue.Ss(listOf(follower.followerNickname))
			action = AttributeAction.Add
		}

		val request = UpdateItemRequest {
			tableName = TABLENAME
			key = itemKey
			attributeUpdates = updatedValues
		}
		return request
	}
}


