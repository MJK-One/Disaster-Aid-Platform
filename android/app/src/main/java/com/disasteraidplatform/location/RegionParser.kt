// android/app/src/main/java/com/disasteraidplatform/location/RegionParser.kt
package com.disasteraidplatform.location

object RegionParser {
    fun parse(region1: String, region2: String): Region {
        var si = ""
        var gu: String? = null

        // region2에서 "○○시", "○○군" 추출
        val siMatch = Regex("([가-힣]+[시군])").find(region2)
        if (siMatch != null) {
            si = siMatch.groupValues[1]
        } else {
            // fallback: 특별시/광역시 처리
            si = region1.replace("특별시", "시").replace("광역시", "시")
        }

        // 구 단위 추출
        val guMatch = Regex("([가-힣]+구)$").find(region2)
        gu = guMatch?.groupValues?.get(1)

        return Region(si, gu)
    }
}
