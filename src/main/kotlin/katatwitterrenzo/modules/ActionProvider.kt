package katatwitterrenzo.modules

import katatwitterrenzo.core.action.follower.FollowUser
import katatwitterrenzo.core.action.follower.GetFollowers
import katatwitterrenzo.core.action.tweet.CreateTweet
import katatwitterrenzo.core.action.tweet.GetTweet
import katatwitterrenzo.core.action.user.CreateUser
import katatwitterrenzo.core.action.user.UpdateUser

object ActionProvider {

    val getTweet by lazy {
        GetTweet(RepositoriesProvider.tweets)
    }
    val createTweet by lazy{
        CreateTweet(RepositoriesProvider.tweets)
    }

    val getFollowers by lazy{
        GetFollowers(RepositoriesProvider.followers)
    }

    val followUser by lazy{
        FollowUser(RepositoriesProvider.followers, RepositoriesProvider.users)
    }
    val updateUser by lazy{
        UpdateUser(RepositoriesProvider.users)
    }

    val createUser by lazy{
        CreateUser(RepositoriesProvider.users)
    }
}
