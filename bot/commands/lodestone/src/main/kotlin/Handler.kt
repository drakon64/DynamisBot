package cloud.drakon.dynamisbot.lodestone

import cloud.drakon.ktdiscord.KtDiscord
import cloud.drakon.ktdiscord.interaction.Interaction
import cloud.drakon.ktdiscord.interaction.applicationcommand.ApplicationCommandData
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

class Handler: RequestStreamHandler {
    companion object {
        // Initialise these during the initialization phase

        val ktDiscord = KtDiscord(
            System.getenv("APPLICATION_ID"), System.getenv("BOT_TOKEN")
        ).Interaction(System.getenv("PUBLIC_KEY"))

        val json = Json {
            ignoreUnknownKeys =
                true // Not all fields returned by the Discord API are documented
            isLenient = true // TODO https://github.com/TempestProject/Tempest/issues/3
        }

        val ktorClient = HttpClient(Java)

        val mongoDatabase: MongoDatabase =
            MongoClients.create(System.getenv("MONGODB_URL"))
                .getDatabase(System.getenv("MONGODB_DATABASE"))
    }

    override fun handleRequest(
        inputStream: InputStream,
        outputStream: OutputStream,
        context: Context,
    ): Unit = runBlocking {
        val event: Interaction<ApplicationCommandData> =
            json.decodeFromString(inputStream.readAllBytes().decodeToString())

        when (event.data!!.type) {
            1 -> when (event.data!!.options!![0].name) {
                "card" -> card(event)
                "link" -> link(event)
                "unlink" -> unlink(event)
                "portrait" -> portrait(event)
                "profile" -> profile(event)
                else -> throw Exception("Unknown slash command: ${event.data!!.options!![0].name}")
            }

            2 -> when (event.data!!.name) {
                "Character card" -> card(event)
                "Character portrait" -> portrait(event)
                "Character profile" -> profile(event)
                else -> throw Exception("Unknown user command: ${event.data!!.name}")
            }

            else -> throw Exception("Unknown event type: ${event.type}")
        }
    }
}
