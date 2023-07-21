package cloud.drakon.dynamisbot.eorzeadatabase.quest

import cloud.drakon.ktdiscord.channel.embed.Embed
import cloud.drakon.ktdiscord.channel.embed.EmbedField
import cloud.drakon.ktdiscord.channel.embed.EmbedImage
import cloud.drakon.ktdiscord.channel.embed.EmbedThumbnail
import kotlinx.coroutines.coroutineScope

suspend fun questHandler(quest: Quest, lodestone: String) =
    coroutineScope {
        val embedFields = mutableListOf(
            EmbedField(
                name = "Level",
                value = quest.classJobLevel.toString()
            )
        )

        if (quest.gilReward != null) {
            embedFields.add(
                EmbedField(
                    name = "Gil",
                    value = "${quest.gilReward} <:gil:235457032616935424>"
                )
            )
        }

        return@coroutineScope Embed(
            title = quest.name,
            description = quest.journalGenre.name,
            url = "https://$lodestone.finalfantasyxiv.com/lodestone/playguide/db/search/?q=${
                quest.name.replace(
                    " ",
                    "+"
                )
            }&db_search_category=quest",
            image = EmbedImage(url = "https://xivapi.com${quest.banner}"),
            thumbnail = EmbedThumbnail(url = "https://xivapi.com${quest.journalGenre.icon}"),
            fields = embedFields.toTypedArray()
        )
    }