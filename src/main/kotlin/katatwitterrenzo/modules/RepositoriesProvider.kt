package katatwitterrenzo.modules

import katatwitterrenzo.config.Environment
import katatwitterrenzo.core.infrastructure.config.DynamoDbClientConfig
import katatwitterrenzo.core.infrastructure.repository.follower.DynamoFollowers
import katatwitterrenzo.core.infrastructure.repository.follower.InMemoryFollowers
import katatwitterrenzo.core.infrastructure.repository.tweet.DynamoTweets
import katatwitterrenzo.core.infrastructure.repository.tweet.InMemoryTweets
import katatwitterrenzo.core.infrastructure.repository.user.DynamoUsers
import katatwitterrenzo.core.infrastructure.repository.user.InMemoryUsers

object RepositoriesProvider {

    val tweets by lazy {
        when (Environment.REPOSITORY) {
            "DYNAMO" -> DynamoTweets(DynamoDbClientConfig.client)
            else -> InMemoryTweets()
        }
    }

    val followers by lazy {
        when (Environment.REPOSITORY) {
            "DYNAMO" -> DynamoFollowers(DynamoDbClientConfig.client)
            else -> InMemoryFollowers()
        }
    }

    val users by lazy {
        when (Environment.REPOSITORY) {
            "DYNAMO" -> DynamoUsers(DynamoDbClientConfig.client)
            else -> InMemoryUsers()
        }
    }
}
