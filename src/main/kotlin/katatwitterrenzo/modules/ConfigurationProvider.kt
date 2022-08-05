package katatwitterrenzo.modules

import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.yaml.YamlParser
import katatwitterrenzo.http.HttpApiServer

object ConfigurationProvider {

    private const val configurationFileName = "/config.yaml"

    data class Configuration constructor(
        val app: HttpApiServer.AppConfig,
        val http: GatewaysProvider.HttpEngineConfiguration,
        val platformUrl: String
    )

    /**
     * In charge of obtaining the configuration values and setting them in the corresponding variables.
     */
    val config: Configuration = ConfigLoader.Builder()
        .addFileExtensionMapping("yaml", YamlParser())
        .build()
        .loadConfigOrThrow(configurationFileName)
}