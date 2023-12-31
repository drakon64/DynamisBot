package cloud.drakon.dynamisbot.eorzeadatabase.item

import cloud.drakon.ktdiscord.channel.embed.EmbedField
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class MedicineMeal(
    @SerialName("Name") override val name: String,
    @SerialName("Description") override val description: String?,
    @SerialName("IconHD") override val iconHd: String,
    @SerialName("ItemUICategory") override val itemUiCategory: Item.ItemUICategory,

    @SerialName("CanBeHq") val canBeHq: Int,
    @SerialName("Bonuses") val bonuses: Map<String, Bonus>,
    @SerialName("LevelItem") val levelItem: String,
): Item {
    @Serializable
    class Bonus(
        @SerialName("Relative") val relative: Boolean,
        @SerialName("Value") val value: Short,
        @SerialName("Max") val max: Short,
        @SerialName("ValueHQ") val valueHq: Short,
        @SerialName("MaxHQ") val maxHq: Short,
    )

    override suspend fun createEmbedFields(language: String) = buildList {
        bonuses.keys.forEach {
            val key = Localisation.bonuses[it]?.getValue(language) ?: it
            val bonus = bonuses.getValue(it)

            if (bonus.relative) {
                if (this@MedicineMeal.canBeHq == 1) {
                    add(
                        "$key +${bonus.value}% (Max ${bonus.max}) / +${bonus.valueHq}% (Max ${bonus.maxHq}) HQ"
                    )
                } else add("$key +${bonus.value}% (Max ${bonus.max})")
            }
        }
    }.let {
        arrayOf(
            EmbedField(
                name = "Item Level",
                value = this@MedicineMeal.levelItem,
                inline = true
            ), EmbedField(
                name = "Effects",
                value = it.joinToString("\n"),
                inline = true
            )
        )
    }
}
