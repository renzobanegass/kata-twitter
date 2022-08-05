package katatwitterrenzo.http.handler.core.tweet

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import katatwitterrenzo.core.action.tweet.GetTweet
import katatwitterrenzo.http.handler.Handler


class GetTweetHandler(private val getTweet: GetTweet) : Handler {

    private val PATH = "/tweets/{nickname}"
    override fun routing(a: Application) {
        a.routing {
            route(PATH){
                get { getTweet() }
            }
        }
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.getTweet() {
        val nickname = context.parameters["nickname"]
        call.respond(getTweet(nickname!!))
        call.respond(HttpStatusCode.Created)
    }
}