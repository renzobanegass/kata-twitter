package katatwitterrenzo.helpers

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.*

class DynamoDbHelper(){
    companion object{
         suspend fun deleteTable(client: DynamoDbClient, tableNameVal: String) {

            val request = DeleteTableRequest {
                tableName = tableNameVal
            }

            client.deleteTable(request)
            println("$tableNameVal was deleted")

        }

         suspend fun createTable(client: DynamoDbClient, tableNameVal: String, key: String): String {

            val attDef = AttributeDefinition {
                attributeName = key
                attributeType = ScalarAttributeType.S
            }

            val keySchemaVal = KeySchemaElement {
                attributeName = key
                keyType = KeyType.Hash
            }

            val provisionedVal = ProvisionedThroughput {
                readCapacityUnits = 1
                writeCapacityUnits = 1
            }

            val request = CreateTableRequest {
                attributeDefinitions = listOf(attDef)
                keySchema = listOf(keySchemaVal)
                provisionedThroughput = provisionedVal
                tableName = tableNameVal
            }

            var tableArn: String
            val response = client.createTable(request)
            tableArn = response.tableDescription!!.tableArn.toString()
            println("Table $tableArn is ready")
            return tableArn
        }
    }
}