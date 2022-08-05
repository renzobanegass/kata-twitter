package katatwitterrenzo.core.infrastructure.config

import aws.sdk.kotlin.runtime.auth.credentials.ProfileCredentialsProvider
import aws.sdk.kotlin.runtime.endpoint.AwsEndpoint
import aws.sdk.kotlin.runtime.endpoint.AwsEndpointResolver
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient

object DynamoDbClientConfig{
    val client: DynamoDbClient by lazy {
        DynamoDbClient{
            region = "us-east-1"
            credentialsProvider = ProfileCredentialsProvider()
            endpointResolver = LocalHostDynamoDb()
        }
    }
}
private class LocalHostDynamoDb: AwsEndpointResolver {
    override suspend fun resolve(service: String, region: String): AwsEndpoint
            = AwsEndpoint("http://localhost:8000")
}