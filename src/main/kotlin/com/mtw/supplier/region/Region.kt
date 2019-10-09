package com.mtw.supplier.region

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.hexworks.mixite.core.api.*
import org.hexworks.mixite.core.api.contract.SatelliteData
import kotlin.math.roundToInt

enum class HexEffects {
    POISONOUS_TO_TOUCH_VEGETATION,
    HYPTERGROWTJ_VEGETATION
}

@Serializable
class RegionHex(
    val vegetationPercentage: Int,
    val elevation: Int,
    val hexEffects: MutableList<HexEffects> = mutableListOf(),
    // These parameters are not used, and are vestigal SatelliteData requirements.
    override var passable: Boolean = true,
    override var opaque: Boolean = false,
    override var movementCost: Double = 0.0
) : SatelliteData

// Aside from the serialization thing, it's weird to refer to a [x, y, z] representation is "a coordinate".
// ...this is possibly just me being utterly insane.
@Serializable
data class CubeCoordinates(val gridX: Int, val gridZ: Int) {
    internal fun toMixiteCubeCoordinate(): CubeCoordinate {
        return CubeCoordinate.fromCoordinates(this.gridX, this.gridZ)
    }
}

@Serializable
class Region(
    val gridHeight: Int,
    val gridWidth: Int,
    val gridLayout: HexagonalGridLayout,
    val gridOrientation: HexagonOrientation,
    val gridHexRadius: Double = 6.0 // You shouldn't bother with this, really...
) {
    @Transient
    private val grid = HexagonalGridBuilder<RegionHex>()
        .setGridHeight(gridHeight)
        .setGridWidth(gridWidth)
        .setGridLayout(gridLayout)
        .setOrientation(gridOrientation)
        .setRadius(gridHexRadius)
        .build()
    private val cubeCoordinatesToRegionHexes: MutableMap<CubeCoordinates, RegionHex> = mutableMapOf()

    init {
        cubeCoordinatesToRegionHexes.map { (coordinates, hex) ->
            setGridHexData(coordinates, hex)
        }
    }

    private fun setGridHexData(coordinates: CubeCoordinates, hexData: RegionHex) {
        val gridHex = grid.getByCubeCoordinate(coordinates.toMixiteCubeCoordinate())
        if(!gridHex.isPresent) throw CoordinatesInvalidException(coordinates)
        gridHex.get().setSatelliteData(hexData)
    }

    fun setHex(coordinates: CubeCoordinates, hex: RegionHex) {
        setGridHexData(coordinates, hex)
        cubeCoordinatesToRegionHexes[coordinates] = hex
    }

    fun getHex(coordinates: CubeCoordinates): RegionHex? {
        if(!grid.containsCubeCoordinate(coordinates.toMixiteCubeCoordinate())) throw CoordinatesInvalidException(coordinates)
        return cubeCoordinatesToRegionHexes[coordinates]
    }

    class CoordinatesInvalidException(coordinates: CubeCoordinates):
        Exception("Coordinates [x=${coordinates.gridX}, z=${coordinates.gridZ}] are invalid!")

    fun dumbDraw() {
        val pointMap = mutableMapOf<Pair<Int,Int>, Char>()

        grid.hexagons.forEach { recordHex(it, pointMap) }

        for (y in 100 downTo 0) {
            for (x in 0..100) {
                val p = Pair(x, y)
                if (pointMap.contains(p)) {
                    print(pointMap[p])
                } else {
                    print(" ")
                }
            }
            println()
        }
    }

    private fun toRoundedPair(point: Point): Pair<Int,Int> {
        return Pair(point.coordinateX.roundToInt(), point.coordinateY.roundToInt())
    }

    private fun recordHex(hex: Hexagon<RegionHex>, pointMap: MutableMap<Pair<Int,Int>, Char>) {
        var x: Int
        var y: Int

        val e = toRoundedPair(hex.points[0])
        val ne = toRoundedPair(hex.points[1])
        val nw = toRoundedPair(hex.points[2])
        val w = toRoundedPair(hex.points[3])
        val sw = toRoundedPair(hex.points[4])
        val se = toRoundedPair(hex.points[5])

        pointMap[nw] = '#'
        pointMap[ne] = '#'
        for (x in nw.first+1 until ne.first) {
            pointMap[Pair(x, nw.second)] = '-'
        }
        pointMap[e] = '#'
        y = ne.second - 1
        x = ne.first + 1
        while(y > e.second) {
            if (x < e.first) {
                pointMap[Pair(x, y)] = '.'
                x += 1
            } else {
                pointMap[Pair(x, y)] = '.'
            }
            y -= 1
        }
        y = nw.second - 1
        x = nw.first - 1
        while(y > w.second) {
            if (x > w.first) {
                pointMap[Pair(x, y)] = '.'
                x -= 1
            } else {
                pointMap[Pair(x, y)] = '.'
            }
            y -= 1
        }

        pointMap[se] = '#'
        pointMap[sw] = '#'
        for (x in sw.first+1 until se.first) {
            pointMap[Pair(x, sw.second)] = '-'
        }
        pointMap[w] = '#'
        y = sw.second + 1
        x = sw.first - 1
        while(y < w.second) {
            if (x > w.first) {
                pointMap[Pair(x, y)] = '.'
                x -= 1
            } else {
                pointMap[Pair(x, y)] = '.'
            }
            y += 1
        }
        y = se.second + 1
        x = se.first + 1
        while(y < e.second) {
            if (x < e.first) {
                pointMap[Pair(x, y)] = '.'
                x += 1
            } else {
                pointMap[Pair(x, y)] = '.'
            }
            y += 1
        }
    }
}
