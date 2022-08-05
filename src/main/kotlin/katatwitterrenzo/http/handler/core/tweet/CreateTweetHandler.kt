package katatwitterrenzo.http.handler.core.tweet

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import katatwitterrenzo.core.action.tweet.CreateTweet
import katatwitterrenzo.core.domain.tweet.Tweet
import katatwitterrenzo.http.handler.Handler


class CreateTweetHandler(private val createTweet: CreateTweet) : Handler {

    private val PATH = "/tweets"
    override fun routing(a: Application) {
        a.routing {
            route(PATH){
                post { createTweet() }
            }
        }
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.createTweet() {
        val request = call.receive<Tweet>()
        createTweet(request)
        call.respond(HttpStatusCode.Created)
    }
}