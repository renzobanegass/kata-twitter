package katatwitterrenzo.http.handler.core.follower

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import katatwitterrenzo.core.action.follower.FollowUser
import katatwitterrenzo.core.domain.follower.FollowerData
import katatwitterrenzo.http.handler.Handler


class FollowUserHandler(private val followUser: FollowUser) : Handler {

    private val PATH = "/follow"
    override fun routing(a: Application) {
        a.routing {
            route(PATH){
                post { followUser() }
            }
        }
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.followUser() {
        val request = call.receive<FollowerData>()

        followUser.invoke(request)
        call.respond(HttpStatusCode.Created)
    }
}