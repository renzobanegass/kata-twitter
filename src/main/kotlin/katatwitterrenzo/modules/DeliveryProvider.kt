package katatwitterrenzo.modules

import katatwitterrenzo.http.HttpApiServer
import katatwitterrenzo.http.handler.administrative.InfoHandler
import katatwitterrenzo.http.handler.administrative.StatusHandler
import katatwitterrenzo.http.handler.core.follower.FollowUserHandler
import katatwitterrenzo.http.handler.core.follower.GetFollowersHandler
import katatwitterrenzo.http.handler.core.tweet.CreateTweetHandler
import katatwitterrenzo.http.handler.core.tweet.GetTweetHandler
import katatwitterrenzo.http.handler.core.user.CreateUserHandler
import katatwitterrenzo.http.handler.core.user.UpdateUserHandler
import katatwitterrenzo.modules.ConfigurationProvider.config

object DeliveryProvider {

    private val getTweetHandler by lazy{
        GetTweetHandler(ActionProvider.getTweet)
    }
    private val createUserHandler by lazy{
        CreateUserHandler(ActionProvider.createUser)
    }

    private val updateUserHandler by lazy{
        UpdateUserHandler(ActionProvider.updateUser)
    }

    private val followUserHandler by lazy{
        FollowUserHandler(ActionProvider.followUser)
    }

    private val getFollowersHandler by lazy{
        GetFollowersHandler(ActionProvider.getFollowers)
    }

    private val infoHandler by lazy {
        InfoHandler(config.app)
    }

    private val statusHandler by lazy {
        StatusHandler()
    }

    private val createTweetHandler by lazy{
        CreateTweetHandler(ActionProvider.createTweet)
    }

    val apiServer by lazy{
        HttpApiServer(
            config.app,
            infoHandler,
            statusHandler,
            createUserHandler,
            updateUserHandler,
            followUserHandler,
            getFollowersHandler,
            createTweetHandler,
            getTweetHandler
        )
    }
}