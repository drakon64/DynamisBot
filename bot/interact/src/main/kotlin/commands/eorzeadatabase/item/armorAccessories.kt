package cloud.drakon.dynamisbot.interact.commands.eorzeadatabase.item

import cloud.drakon.ktdiscord.channel.embed.Embed
import cloud.drakon.ktdiscord.channel.embed.EmbedField
import cloud.drakon.ktdiscord.channel.embed.EmbedThumbnail
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

suspend fun armorAccessories(item: JsonObject, language: String, lodestone: String) =
    coroutineScope {
        val bonuses = mutableListOf<String>()

        for (i in item["Stats"]!!.jsonObject.keys) {
            val key = Localisation.bonuses[i]?.getValue(language) ?: i

            val bonus = item["Stats"]!!.jsonObject[i]!!
            val value = bonus.jsonObject["NQ"]!!.jsonPrimitive.int
            val valueHq = bonus.jsonObject["HQ"]?.jsonPrimitive?.int

            if (valueHq != null) {
                bonuses.add(
                    "$key +$value / +$valueHq <:hq:916051971063054406>"
                )
            } else {
                bonuses.add(
                    "$key +$value"
                )
            }
        }

        val embedFieldOne: EmbedField
        val embedFieldTwo: EmbedField

        if (item["EquipSlotCategoryTargetID"]!!.jsonPrimitive.int == 2) {
            val nqBlock = item["Block"]!!.jsonPrimitive.int
            val nqBlockRate = item["BlockRate"]!!.jsonPrimitive.int

            val hqBlock =
                if (item["BaseParamValueSpecial0"]?.jsonPrimitive?.int != null) {
                    nqBlock + item["BaseParamValueSpecial0"]!!.jsonPrimitive.int
                } else {
                    null
                }

            val hqBlockRate =
                if (item["BaseParamValueSpecial1"]?.jsonPrimitive?.int != null) {
                    nqBlockRate + item["BaseParamValueSpecial1"]!!.jsonPrimitive.int
                } else {
                    null
                }

            val block = if (hqBlock != null && hqBlock != nqBlock) {
                "$nqBlock / $hqBlock <:hq:916051971063054406>"
            } else {
                "$nqBlock"
            }

            val blockRate = if (hqBlockRate != null && hqBlockRate != nqBlockRate) {
                "$nqBlockRate / $hqBlockRate <:hq:916051971063054406>"
            } else {
                "$nqBlockRate"
            }

            embedFieldOne = EmbedField(
                name = Localisation.block.getValue("Strength").getValue(language),
                value = block,
                inline = true
            )

            embedFieldTwo = EmbedField(
                name = Localisation.block.getValue("Rate").getValue(language),
                value = blockRate,
                inline = true
            )
        } else {
            val nqPhysicalDefense = item["DefensePhys"]!!.jsonPrimitive.int
            val nqMagicDefense = item["DefenseMag"]!!.jsonPrimitive.int

            val hqPhysicalDefense =
                if (item["BaseParamValueSpecial0"]?.jsonPrimitive?.int != null) {
                    nqPhysicalDefense + item["BaseParamValueSpecial0"]!!.jsonPrimitive.int
                } else {
                    null
                }

            val hqMagicDefense =
                if (item["BaseParamValueSpecial1"]?.jsonPrimitive?.int != null) {
                    nqMagicDefense + item["BaseParamValueSpecial1"]!!.jsonPrimitive.int
                } else {
                    null
                }

            val physicalDefense =
                if (hqPhysicalDefense != null && hqPhysicalDefense != nqPhysicalDefense) {
                    "$nqPhysicalDefense / $hqPhysicalDefense <:hq:916051971063054406>"
                } else {
                    "$nqPhysicalDefense"
                }

            val magicDefense =
                if (hqMagicDefense != null && hqMagicDefense != nqMagicDefense) {
                    "$nqMagicDefense / $hqMagicDefense <:hq:916051971063054406>"
                } else {
                    "$nqMagicDefense"
                }

            embedFieldOne = EmbedField(
                name = Localisation.defense.getValue("Defense").getValue(language),
                value = physicalDefense,
                inline = true
            )

            embedFieldTwo = EmbedField(
                name = Localisation.defense.getValue("Magic Defense")
                    .getValue(language),
                value = magicDefense,
                inline = true
            )
        }

        val classJob = """
            ${item["ClassJobCategory"]!!.jsonObject["Name"]!!.jsonPrimitive.content}
            ${Localisation.level.getValue(language)} ${item["LevelEquip"]!!.jsonPrimitive.content}
        """.trimIndent()

        return@coroutineScope Embed(
            title = item["Name"]!!.jsonPrimitive.content,
            description = item["ItemUICategory"]!!.jsonObject["Name"]!!.jsonPrimitive.content,
            url = "https://$lodestone.finalfantasyxiv.com/lodestone/playguide/db/search/?q=${
                item["Name"]!!.jsonPrimitive.content.replace(
                    " ", "+"
                )
            }",
            thumbnail = EmbedThumbnail(url = "https://xivapi.com${item["IconHD"]!!.jsonPrimitive.content}"),
            fields = arrayOf(
                EmbedField(
                    name = Localisation.itemLevel.getValue(language),
                    value = item["LevelItem"]!!.jsonPrimitive.content,
                ), embedFieldOne, embedFieldTwo,
                EmbedField(
                    name = "Class/Job", value = classJob
                ), EmbedField(
                    name = Localisation.bonuses.getValue("Bonuses").getValue(language),
                    value = bonuses.joinToString("\n"),
                )
            )
        )
    }