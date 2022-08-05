@file:Suppress("MagicNumber", "WildcardImport", "MaxLineLength")

package katatwitterrenzo.http

import com.etermax.ktor.plugins.metrics.Prometheus
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import katatwitterrenzo.http.handler.Handler
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import java.time.Duration

/**
 * This class exposed the services provided by this app as HTTP REST requests
 */
class HttpApiServer(
	private val config: AppConfig,
	private vararg val handlers: Handler
) {
	data class AppConfig(
		val image: String,
		val deployedAt: String,
		val port: Int
	) {
		val name = image.splitToSequence(':').elementAtOrElse(0) { "N/A" }
		val version = image.splitToSequence(':').elementAtOrElse(1) { "N/A" }
	}

	private val logger = LoggerFactory.getLogger(this::class.java)
	private val port = config.port

	fun start() {
		logger.info("Starting ${config.name} in port $port")

		val server = embeddedServer(Netty, port = port) {
			main()
		}

		server.start(wait = true)
	}

	private fun Application.main() {
		installFeatures()

		routing {
			if (logger.isTraceEnabled)
				trace { logger.trace(it.buildText()) }
		}

		handlers.forEach { it.routing(this) }
	}

	private fun Application.installFeatures() {
		install(DefaultHeaders)
		install(Compression)
		installContentNegotiation()
		install(XForwardedHeaderSupport)
		installCORS()
		install(Prometheus)
		install(CallLogging) {
			level = Level.INFO
			filter { ignoreInternalEndpoints(it.request.path()) }
		}
		install(StatusPages) {
			addExceptionHandlers()
		}
	}

	private fun ignoreInternalEndpoints(path: String) = path != "/live" && path != "/ready" && path != "/metrics"

	private fun Application.installContentNegotiation() {
		install(ContentNegotiation) {
			json(
				Json {
					ignoreUnknownKeys = true
				}
			)
		}
	}

	private fun Application.installCORS() {
		install(CORS) {
			anyHost()
			allowNonSimpleContentTypes = true
			maxAgeInSeconds = Duration.ofDays(1).seconds
			allowCORSInModificationMethods()
		}
	}

	private fun CORS.Configuration.allowCORSInModificationMethods() {
		method(HttpMethod.Patch)
		method(HttpMethod.Delete)
		method(HttpMethod.Put)
	}

	private fun StatusPages.Configuration.addExceptionHandlers() {
		exception<Exception> { cause ->
			logger.error("Unhandled exception in ${call.request.path()}: ${cause.localizedMessage}", cause)
			call.respond(HttpStatusCode.InternalServerError, cause::class.java.simpleName + ": " + cause.message)
		}
	}
}