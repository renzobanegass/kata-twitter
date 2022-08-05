package katatwitterrenzo.core.infrastructure.repository.tweet

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.*
import katatwitterrenzo.core.domain.tweet.Tweet
import katatwitterrenzo.core.domain.tweet.Tweets


class DynamoTweets(private val client: DynamoDbClient, private val TABLENAME: String = "Tweets") : Tweets {

	val NICKNAME = "Nickname"
	val CONTENT = "Content"

	override suspend fun get(nickname: String): List<String> {
		val returnedItem = getItemResponse(nickname)
		val tweetMap = returnedItem.item ?: return listOf()
		return mapTweets(tweetMap)
	}

	private fun mapTweets(tweetMap: Map<String, AttributeValue>): List<String> {
		val tweets = tweetMap.getValue(CONTENT)
		return if (tweets.asSOrNull().isNullOrEmpty())
			tweets.asSsOrNull() ?: listOf()
		else
			listOf(tweets.asS())
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

	override suspend fun save(tweet: Tweet) {
		val request = updateItemRequest(tweet)
		client.updateItem(request)
		println(" A new item was placed into $TABLENAME.")
	}

	private fun updateItemRequest(tweet: Tweet): UpdateItemRequest {
		val itemKey = mutableMapOf<String, AttributeValue>()
		itemKey[NICKNAME] = AttributeValue.S(tweet.nickname)

		val updatedValues = mutableMapOf<String, AttributeValueUpdate>()
		updatedValues[CONTENT] = AttributeValueUpdate {
			value = AttributeValue.Ss(listOf(tweet.content))
			action = AttributeAction.Add
		}

		val request = UpdateItemRequest {
			tableName = TABLENAME
			key = itemKey
			attributeUpdates = updatedValues
		}
		return request
	}

	suspend fun getAll(): List<String> {
		val tweets = ArrayList<String>()
		buildTweetList(tweets)
		return tweets
	}

	private suspend fun buildTweetList(tweets: ArrayList<String>) {
		val request = ScanRequest {
			tableName = TABLENAME
		}
		val response = client.scan(request)
		response.items?.forEach { item ->
			tweets.add(item[CONTENT]!!.asSOrNull()!!)
		}
	}
}


