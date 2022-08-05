import java.net.URL
import java.net.HttpURLConnection
import java.io.OutputStreamWriter
import java.nio.charset.Charset

buildscript {
    repositories {
        mavenCentral()
        jcenter()
    }
    dependencies {
        classpath("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.2")
    }
}

open class NotifyDependencyUpdates : DefaultTask() {
    lateinit var projectName: String
    lateinit var channel: String
    lateinit var username: String
    lateinit var webhook: String
    var isTest = false

    private val mapper = com.fasterxml.jackson.databind.ObjectMapper()
        .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    data class Report(val outdated: OutdatedDependencies? = null)
    data class OutdatedDependencies(val dependencies: List<OutdatedDependency>? = null)
    data class OutdatedDependency(
        val name: String? = null,
        val group: String? = null,
        val version: String? = null,
        val projectUrl: String? = null,
        val available: AvailableVersion? = null
    )

    data class AvailableVersion(
        val release: String? = null,
        val milestone: String? = null,
        val integration: String? = null
    )

    @TaskAction
    fun notifyDependencyUpdates() {
        if (!shouldProcessReport()) {
            println("The report will not be processed because Slack Webhook and/or Slack Channel were not set. " +
                    "Please, consider configuring both or enabling 'test mode' to print it locally.")
            return
        }

        val reportFile = File("build/dependencyUpdates/report.json")
        if (!reportFile.exists()) {
            println("Dependencies report was not generated, run: ./gradlew dependencyUpdates notifyDependencyUpdates")
            return
        }

        val report = mapper.readValue(reportFile, Report::class.java)

        if (report.hasOutdatedDependencies()) {
            println("Sending outdated dependencies message")
            sendPostRequest(
                message = buildSlackMessage(report),
                channel = channel,
                username = username
            )
        } else {
            println("There are not outdated dependencies!!")
        }
    }

    private fun shouldProcessReport() = isTest || isWebhookSet()

    private fun isWebhookSet() = !webhook.isNullOrEmpty() && !channel.isNullOrEmpty()

    private fun Report?.hasOutdatedDependencies() = !this?.outdated?.dependencies.isNullOrEmpty()

    private fun buildSlackMessage(report: Report?): String {
        val title = "*:gradle: :warning:  $projectName has outdated dependencies*\n"
        return title + outdatedDependenciesSection(report)
    }

    private fun outdatedDependenciesSection(report: Report?): String {
        return report?.outdated?.dependencies
            ?.groupBy { it.group }
            ?.mapValues(::groupingByArtifactGroupIfGlobalUpdate)
            ?.flatMap { (_, v) -> v }
            ?.sortedBy { it.group?.toLowerCase() }
            ?.map(::toOutdatedDependencyLineItem)
            ?.joinToString(separator = "") ?: ""
    }

    private fun toOutdatedDependencyLineItem(it: OutdatedDependency): String {
        var release = it.available?.release

        if (!isTest) {
            release = "_*$release*_"
        }

        return " - ${it.getDescriptiveName()}   ${it.version} -> $release\n"
    }

    private fun OutdatedDependency.getDescriptiveName(): String? {
        return if ((name?.length ?: 0) < 25) "$group:$name" else name
    }

    private fun groupingByArtifactGroupIfGlobalUpdate(e: Map.Entry<String?, List<OutdatedDependency>>): List<OutdatedDependency> {
        val (_, v) = e
        var values = v
        if (v.size > 1) {
            val outdatedDependency = v.first()
            if (v.all { it.version == outdatedDependency.version && it.available == outdatedDependency.available }) {
                values = listOf(outdatedDependency.copy(name = "_*<all>*_"))
            }
        }
        return values
    }

    private fun sendPostRequest(message: String, channel: String, username: String) {
        val payload = "payload={\"channel\": \"$channel\", \"username\": \"$username\", \"text\": \"$message\"}"
        if (isTest) {
            println("(test mode) Skipping message post on slack")
            println(payload)
            return
        }

        val mURL = URL(webhook)
        with(mURL.openConnection() as HttpURLConnection) {
            doOutput = true
            requestMethod = "POST"
            OutputStreamWriter(outputStream, Charset.forName("utf-8")).use {
                it.write(payload)
                it.flush();
            }

            println("URL : $url")
            println("Response Code : $responseCode")
        }
    }
}

val isTestProp = "isTest"
val webhookUrlProp = "webhookUrl"
val channelProp = "channel"

fun isTest() = if (project.hasProperty(isTestProp)) getIsTest() else false

fun getSlackWebhookUrl() = if (project.hasProperty(webhookUrlProp)) getProperty(webhookUrlProp) else ""

fun getSlackChannel() = if (project.hasProperty(channelProp)) getProperty(channelProp) else ""

fun getIsTest() = getProperty(isTestProp).toBoolean()

fun getProperty(property: String) = project.properties[property].toString()

tasks.register<NotifyDependencyUpdates>("notifyDependencyUpdates") {
    isTest = isTest()
    group = "Dependency updates"
    description = "Notifies all dependencies available to update"
    channel = getSlackChannel()
    username = "Dependencies Bot"
    webhook = System.getenv("SLACK_URL") ?: getSlackWebhookUrl()
    projectName = project.name
}