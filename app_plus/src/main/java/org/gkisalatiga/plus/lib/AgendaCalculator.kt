/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Calculates, sorts, and filters both regular agenda and agenda proposal.
 */

package org.gkisalatiga.plus.lib

import org.gkisalatiga.plus.data.CalendarData
import org.gkisalatiga.plus.data.TenseStatus
import java.util.Calendar

class AgendaCalculator {
    companion object {
        private val cal = Calendar.getInstance()

        private val weekdayFromCalendar = mapOf<Int, String>(
            Calendar.SUNDAY to "sun",
            Calendar.MONDAY to "mon",
            Calendar.TUESDAY to "tue",
            Calendar.WEDNESDAY to "wed",
            Calendar.THURSDAY to "thu",
            Calendar.FRIDAY to "fri",
            Calendar.SATURDAY to "sat"
        )

        private fun zeroPadTwoDigits(str: Int): String {
            return if (str < 10) "0$str" else "$str"
        }

        /**
         * Return the list of days at the vicinity of today,
         * up to a week before and a week after today.
         * @param weeksAhead the number of weeks after to consider.
         * @return A list of "YYYY-MM-DD" strings.
         */
        fun getDaysUpAhead(weeksAhead: Int): List<CalendarData> {
            val listOfDays = mutableListOf<CalendarData>()

            // The offset of the maximum days in the "dateAfter" counter.
            val maxDays = (weeksAhead * 7) - 1

            var dateBefore = 0
            var dateAfter = 0
            when(this.cal.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SUNDAY -> { dateBefore = 0; dateAfter = maxDays }
                Calendar.MONDAY -> { dateBefore = 1; dateAfter = maxDays - 1 }
                Calendar.TUESDAY -> { dateBefore = 2; dateAfter = maxDays - 2 }
                Calendar.WEDNESDAY -> { dateBefore = 3; dateAfter = maxDays - 3 }
                Calendar.THURSDAY -> { dateBefore = 4; dateAfter = maxDays - 4 }
                Calendar.FRIDAY -> { dateBefore = 5; dateAfter = maxDays - 5 }
                Calendar.SATURDAY -> { dateBefore = 6; dateAfter = maxDays - 6 }
            }

            while (dateBefore >= 0) {
                val newCal = Calendar.getInstance()
                newCal.add(Calendar.DAY_OF_MONTH, -1 * dateBefore)

                val year = newCal.get(Calendar.YEAR)
                val month = zeroPadTwoDigits(newCal.get(Calendar.MONTH) + 1)
                val day = zeroPadTwoDigits(newCal.get(Calendar.DAY_OF_MONTH))
                val dateString = "$year-$month-$day"
                val weekday = weekdayFromCalendar[newCal.get(Calendar.DAY_OF_WEEK)]!!

                listOfDays.add(
                    CalendarData(
                        newCal,
                        dateString,
                        if (dateBefore > 0) TenseStatus.PAST else TenseStatus.PRESENT,
                        weekday
                    )
                )
                dateBefore--
            }

            while (dateAfter > 0) {
                val newCal = Calendar.getInstance()
                newCal.add(Calendar.DAY_OF_MONTH, 1 * dateAfter)

                val year = newCal.get(Calendar.YEAR)
                val month = zeroPadTwoDigits(newCal.get(Calendar.MONTH) + 1)
                val day = zeroPadTwoDigits(newCal.get(Calendar.DAY_OF_MONTH))
                val dateString = "$year-$month-$day"
                val weekday = weekdayFromCalendar[newCal.get(Calendar.DAY_OF_WEEK)]!!

                listOfDays.add(
                    CalendarData(
                        newCal,
                        dateString,
                        TenseStatus.FUTURE,
                        weekday
                    )
                )
                dateAfter--
            }

            return listOfDays.sortedBy { it.dateString }
        }
    }
}