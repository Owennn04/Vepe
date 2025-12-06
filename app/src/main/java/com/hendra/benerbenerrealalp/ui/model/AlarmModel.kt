package com.hendra.benerbenerrealalp.ui.model

// Request untuk membuat Alarm
data class AlarmRequest(
    val time: String,       // "HH:mm"
    val label: String,
    val days: List<Boolean>, // [true, false, ...]
    val isActive: Boolean = true
)

// Response dari Backend
data class AlarmResponse(
    val id: String,
    val time: String,
    val label: String,
    val days: List<Boolean>,
    val isActive: Boolean,
    val userId: String
) {
    // Helper untuk UI: Format Jam
    fun getTimeString(): String = time

    // Helper untuk UI: Format Hari (Sen, Sel, Rab...)
    fun getDaysString(): String {
        val daysName = listOf("M", "S", "S", "R", "K", "J", "S") // Minggu - Sabtu
        val activeDays = days.mapIndexed { index, active ->
            if (active) daysName[index] else null
        }.filterNotNull()

        return if (activeDays.isEmpty()) "Sekali"
        else if (activeDays.size == 7) "Setiap Hari"
        else activeDays.joinToString(" ")
    }
}

// Request untuk Toggle (Nyala/Mati)
data class ToggleAlarmRequest(
    val isActive: Boolean
)