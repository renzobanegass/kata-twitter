package katatwitterrenzo.http.handler.core.user

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import katatwitterrenzo.core.action.user.CreateUser
import katatwitterrenzo.core.domain.user.UserData
import katatwitterrenzo.http.handler.Handler


class CreateUserHandler(private val createUser: CreateUser) : Handler {

    private val PATH = "/users"
    override fun routing(a: Application) {
        a.routing {
            route(PATH){
                post { createUser() }
            }
        }
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.createUser() {
        val request = call.receive<UserData>()

        createUser.invoke(request)
        call.respond(HttpStatusCode.Created)
    }
}