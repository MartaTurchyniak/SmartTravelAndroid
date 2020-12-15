package com.magic.smarttravel.util.formatters

/**
 * Created by Marta Turchyniak on 12/8/20.
 */
object PhoneNumberFormatter {

    fun format(phone: String): String {
        val withoutMask = phone.replace(" ", "")

        return if (withoutMask.startsWith(UA_COUNTRY_CODE)) {
            withoutMask
        } else {
            "$UA_COUNTRY_CODE$withoutMask"
        }
    }

    private const val UA_COUNTRY_CODE = "+38"
}