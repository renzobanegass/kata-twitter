package katatwitterrenzo.http.handler.core.follower

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import katatwitterrenzo.core.action.follower.GetFollowers
import katatwitterrenzo.http.handler.Handler

class GetFollowersHandler(private val getFollowers: GetFollowers) : Handler {

    private val PATH = "/followers/{nickname}"
    override fun routing(a: Application) {
        a.routing {
            route(PATH){
                get { getFollowers() }
            }
        }
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.getFollowers() {
        val nickname = context.parameters["nickname"]

        call.respond(getFollowers.invoke(nickname!!))
        call.respond(HttpStatusCode.OK)
    }
}