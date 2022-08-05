package katatwitterrenzo.http.handler.administrative

import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.feature
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import katatwitterrenzo.http.HttpApiServer
import katatwitterrenzo.http.handler.Handler
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class InfoHandler(private val config: HttpApiServer.AppConfig): Handler {
    lateinit var application: Application

    override fun routing(a: Application) {
        application = a

        a.routing {
            get("/kata-twitter-renzo/info") { handleInfo() }
        }
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.handleInfo() {
        val root = application.feature(Routing)

        val allRoutes = allRoutes(root)

        val allRoutesWithMethod = allRoutes.filter { it.selector is HttpMethodRouteSelector }

        val info = JsonArray(listOf(
            JsonObject(
                mapOf(
                    "name" to config.name.toJson(),
                    "version" to config.version.toJson(),
                    "image" to config.image.toJson(),
                    "deployedAt" to config.deployedAt.toJson()
                )
            ),
            JsonObject(
                mapOf(
                    "routes" to JsonArray(allRoutesWithMethod.map { it.toString().toJson() })
                )
            )
        ))

        arrayOf(
            mapOf(
                "name" to config.name,
                "version" to config.version,
                "image" to config.image,
                "deployedAt" to config.deployedAt
            ),
            mapOf(
                "routes" to allRoutesWithMethod.map { it.toString() }
            )
        )

        call.respond(info)
    }
    private fun allRoutes(root: Route): List<Route> {
        return listOf(root) + root.children.flatMap { allRoutes(it) }
    }

    private fun String.toJson(): JsonPrimitive = JsonPrimitive(this)
}
