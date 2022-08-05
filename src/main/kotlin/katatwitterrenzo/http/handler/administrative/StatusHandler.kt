package katatwitterrenzo.http.handler.administrative

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import katatwitterrenzo.http.handler.Handler

/**
 * Provides endpoints for liveness, readiness, and started probes.
 */
class StatusHandler : Handler {
    override fun routing(a: Application) {
        a.routing {
            get("/started") {
                call.respondText("Ok", ContentType.Text.Plain)
            }

            get("/ready") {
                call.respondText("Ok", ContentType.Text.Plain)
            }

            get("/live") {
                call.respondText("Ok", ContentType.Text.Plain)
            }
        }
    }
}
