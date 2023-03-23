package cloud.drakon.tempestbot.interact.commands.openai

import cloud.drakon.ktdiscord.interaction.Interaction
import cloud.drakon.ktdiscord.interaction.applicationcommand.ApplicationCommandData
import cloud.drakon.ktdiscord.webhook.EditWebhookMessage
import cloud.drakon.tempestbot.interact.Handler
import cloud.drakon.tempestbot.interact.api.openai.OpenAI
import cloud.drakon.tempestbot.interact.api.openai.chat.ChatRequest
import cloud.drakon.tempestbot.interact.api.openai.chat.Message
import cloud.drakon.tempestbot.interact.api.openai.chat.Messages
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Projections
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import org.bson.BsonArray
import org.bson.BsonDocument

suspend fun chat(event: Interaction<ApplicationCommandData>) {
    lateinit var message: String
    var thread: String? = null

    for (i in event.data !!.options !!) {
        when (i.name) {
            "message" -> message = i.value !!
            "thread" -> thread = i.value !!
        }
    }

    val mongoCollection = Handler.mongoDatabase.getCollection("chat")

    val messages: MutableList<Message>
    val newMessage = Message("user", message)
    if (thread != null) {
        val mongoThread = mongoCollection.find(
            Filters.and(
                Filters.eq("guild_id", event.guildId), Filters.eq("thread", thread)
            )
        ).projection(
            Projections.fields(
                Projections.include("messages"), Projections.excludeId()
            )
        ).first()

        if (mongoThread != null) {
            messages = Handler.json.decodeFromString(
                Messages.serializer(), mongoThread.toJson()
            ).messages.toMutableList()
            messages.add(newMessage)
        } else {
            messages = mutableListOf(newMessage)
        }
    } else {
        messages = mutableListOf(newMessage)
    }

    val chatGpt = OpenAI(System.getenv("OPENAI_API_KEY")).createChatCompletion(
        ChatRequest(
            "gpt-3.5-turbo",
            messages.toTypedArray(),
            temperature = 0.2,
            maxTokens = 2000
        )
    ).choices[0].message

    if (thread != null) {
        val newMessages: Array<Message> = arrayOf(newMessage, chatGpt)
        val document = BsonArray()
        for (i in newMessages) {
            document.add(
                BsonDocument.parse(
                    Handler.json.encodeToString(
                        Message.serializer(), i
                    )
                )
            )
        }

        mongoCollection.updateOne(
            Filters.and(
                Filters.eq("guild_id", event.guildId), Filters.eq("thread", thread)
            ), Updates.addEachToSet(
                "messages", document
            ), UpdateOptions().upsert(true)
        )
    }

    Handler.ktDiscordClient.editOriginalInteractionResponse(
        EditWebhookMessage(
            content = chatGpt.content
        ), event.token
    )
}
