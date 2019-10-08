package com.mtw.supplier.region

import org.hexworks.mixite.core.api.HexagonOrientation
import org.hexworks.mixite.core.api.HexagonalGridLayout
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class RegionTests {
    @Test
    fun testSerializationToJson() {
        val r = Region(8, 8, HexagonalGridLayout.RECTANGULAR, HexagonOrientation.FLAT_TOP)
        r.dumbDraw()
    }
}
