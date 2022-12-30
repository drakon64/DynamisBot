package cloud.drakon.tempestbot.interact.commands.ffxiv.lodestone

import cloud.drakon.discordkt.interaction.Interaction
import cloud.drakon.discordkt.interaction.applicationcommand.ApplicationCommandData

suspend fun lodestoneHandler(event: Interaction<ApplicationCommandData>) {
    when (event.data !!.options !![0].name) {
        "card" -> return card(event)
        "link" -> return link(event)
        "unlink" -> return unlink(event)
        "portrait" -> return portrait(event)
        "profile" -> return profile(event)
    }
}
