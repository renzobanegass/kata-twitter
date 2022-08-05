package katatwitterrenzo.http.handler

import io.ktor.application.*

/**
 * Implementers handle requests in the configured routes by `routing` methods
 */
interface Handler {
	fun routing(a: Application)
}
