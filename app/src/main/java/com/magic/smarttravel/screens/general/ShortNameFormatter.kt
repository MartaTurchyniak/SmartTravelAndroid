package com.magic.smarttravel.screens.general

/**
 * Created by Marta Turchyniak on 12/2/20.
 */
object ShortNameFormatter {

    fun format(name: String): String {
        try {
            val trimmedName = name.trim()
            if (trimmedName.isBlank()) {
                return ""
            }
            val parts = trimmedName.toUpperCase().split(" ")

            var shortName = ""

            return if (parts.size > 1) {
                shortName = ""
                shortName += parts[0].first()
                shortName += parts.last().first()
                shortName
            } else {
                shortName = if (parts.first().length > 1) {
                    parts.first().substring(0, 2)
                } else {
                    parts.first().substring(0, 1)
                }
                shortName
            }
        } catch (e: Exception) {
            return ""
        }
    }
}