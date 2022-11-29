package cloud.drakon.tempestbot.interact.commands

import aws.sdk.kotlin.services.translate.TranslateClient
import aws.sdk.kotlin.services.translate.translateText
import cloud.drakon.tempest.interaction.Interaction
import cloud.drakon.tempest.interaction.applicationcommand.ApplicationCommandData
import cloud.drakon.tempest.webbook.EditWebhookMessage
import cloud.drakon.tempestbot.interact.Handler

suspend fun translate(event: Interaction<ApplicationCommandData>) {
    lateinit var message: String
    lateinit var to: String
    var from = "auto"

    when (event.data !!.type.toInt()) {
        1 -> {
            for (i in event.data !!.options !!) {
                when (i.name) {
                    "message" -> message = i.value !!
                    "to" -> to = i.value !!
                    "from" -> from = i.value !!
                }
            }
        }

        3 -> {
            for (i in event.data !!.resolved !!.messages !!) {
                message = event.data !!.resolved !!.messages !![i.key] !!.content
            }
            to = event.locale !!
        }
    }

    val translation = TranslateClient.invoke { region = Handler.region }.translateText {
        text = message
        targetLanguageCode = to
        sourceLanguageCode = from
    }

    val translatedMessage: String = if (from == "auto") {
        "${translation.sourceLanguageCode}: ${translation.translatedText}"
    } else (translation.translatedText !!)

    Handler.tempestClient.editOriginalInteractionResponse(
        EditWebhookMessage(content = translatedMessage), event.token
    )
}
