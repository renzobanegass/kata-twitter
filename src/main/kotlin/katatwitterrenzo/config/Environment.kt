package katatwitterrenzo.config

object Environment {
    val REPOSITORY = "REPOSITORY".getOrDefaultTo("DYNAMO")
}

private fun String.getOrDefaultTo(defaultValue: String) = System.getenv(this) ?: defaultValue