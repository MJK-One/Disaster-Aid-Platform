// android/app/src/main/java/com/disasteraidplatform/location/RegionParser.kt
package com.disasteraidplatform.location

object RegionParser {
    private val provinces = listOf(
        "경기도", "충청북도", "충청남도", "경상북도",
        "경상남도", "전라북도", "전라남도", "강원도", "제주특별자치도"
    )

    fun parse(siRaw: String, guRaw: String): Region {
        var si = siRaw
        provinces.forEach { province ->
            if (si.startsWith(province)) {
                val parts = si.split(" ")
                if (parts.size > 1) {
                    si = parts[1]
                }
            }
        }

        val gu = if (guRaw.contains("구")) guRaw else null

        return Region(si, gu)
    }
}
