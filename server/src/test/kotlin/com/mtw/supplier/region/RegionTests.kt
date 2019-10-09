package com.mtw.supplier.region

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.hexworks.mixite.core.api.HexagonOrientation
import org.hexworks.mixite.core.api.HexagonalGridLayout
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class RegionTests {
    val json = Json(JsonConfiguration.Stable)

    @Test
    fun testEmptyRegionSerializationToJson() {
        val region = Region(1, 1, HexagonalGridLayout.RECTANGULAR, HexagonOrientation.FLAT_TOP)
        val jsonData = json.stringify(Region.serializer(), region)
        Assert.assertEquals(
            "{\"gridHeight\":1,\"gridWidth\":1,\"gridLayout\":\"RECTANGULAR\",\"gridOrientation\":\"FLAT_TOP\",\"gridHexRadius\":6.0,\"cubeCoordinatesToRegionHexes\":[]}",
            jsonData)
    }

    @Test
    fun testRegionAndRegionHexSerializationToJson() {
        val expected = "{\"gridHeight\":2,\"gridWidth\":2,\"gridLayout\":\"RECTANGULAR\",\"gridOrientation\":\"FLAT_TOP\",\"gridHexRadius\":6.0," +
            "\"cubeCoordinatesToRegionHexes\":[" +
            "{\"gridX\":0,\"gridZ\":0},{\"vegetationPercentage\":50,\"elevation\":69,\"hexEffects\":[],\"passable\":true,\"opaque\":false,\"movementCost\":0.0}," +
            "{\"gridX\":1,\"gridZ\":1},{\"vegetationPercentage\":1,\"elevation\":1,\"hexEffects\":[],\"passable\":true,\"opaque\":false,\"movementCost\":0.0}" +
            "]}"

        val region = Region(2, 2, HexagonalGridLayout.RECTANGULAR, HexagonOrientation.FLAT_TOP)
        region.setHex(CubeCoordinates(0, 0), RegionHex(50, 69))
        region.setHex(CubeCoordinates(1, 1), RegionHex(1, 1))
        val jsonData = json.stringify(Region.serializer(), region)
        Assert.assertEquals(
            expected,
            jsonData)
    }

    @Test
    fun testRegionAndRegionHexSerializationFromJson() {
        val jsonString = "{\"gridHeight\":2,\"gridWidth\":2,\"gridLayout\":\"RECTANGULAR\",\"gridOrientation\":\"FLAT_TOP\",\"gridHexRadius\":6.0," +
            "\"cubeCoordinatesToRegionHexes\":[" +
            "{\"gridX\":0,\"gridZ\":0},{\"vegetationPercentage\":50,\"elevation\":69,\"hexEffects\":[],\"passable\":true,\"opaque\":false,\"movementCost\":0.0}," +
            "{\"gridX\":1,\"gridZ\":1},{\"vegetationPercentage\":1,\"elevation\":1,\"hexEffects\":[],\"passable\":true,\"opaque\":false,\"movementCost\":0.0}" +
            "]}"

        val region = json.parse(Region.serializer(), jsonString)
        Assert.assertEquals(2, region.gridHeight)
        Assert.assertEquals(2, region.gridWidth)
        Assert.assertEquals(HexagonalGridLayout.RECTANGULAR, region.gridLayout)
        Assert.assertEquals(HexagonOrientation.FLAT_TOP, region.gridOrientation)
        Assert.assertEquals(6.0, region.gridHexRadius, 0.01)

        val hex00: RegionHex = region.getHex(CubeCoordinates(0, 0))!!
        Assert.assertEquals(50, hex00.vegetationPercentage)
        Assert.assertEquals(69, hex00.elevation)
        Assert.assertEquals(true, hex00.passable)

        val hex11: RegionHex = region.getHex(CubeCoordinates(1, 1))!!
        Assert.assertEquals(1, hex11.vegetationPercentage)
        Assert.assertEquals(1, hex11.elevation)
        Assert.assertEquals(true, hex11.passable)
    }
}
