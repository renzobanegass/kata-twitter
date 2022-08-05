package katatwitterrenzo.http.handler.core.user

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import katatwitterrenzo.core.action.user.UpdateUser
import katatwitterrenzo.core.domain.user.UserData
import katatwitterrenzo.http.handler.Handler
import java.io.Console

class UpdateUserHandler(private val updateUser: UpdateUser) : Handler {

    private val PATH = "/users"
    override fun routing(a: Application) {
        a.routing {
            route(PATH){
                put { updateUser() }
            }
        }
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.updateUser() {
        val request = call.receive<UserData>()

        updateUser.invoke(request)
        call.respond(HttpStatusCode.OK)
    }
}