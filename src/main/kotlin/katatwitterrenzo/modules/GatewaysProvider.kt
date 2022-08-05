package katatwitterrenzo.modules

object GatewaysProvider {

    data class HttpEngineConfiguration(
        val connectTimeoutMs: Int,
        val connectionRequestTimeoutMs: Int,
        val maxConnTotal: Int,
        val maxConnPerRoute: Int
    )
}