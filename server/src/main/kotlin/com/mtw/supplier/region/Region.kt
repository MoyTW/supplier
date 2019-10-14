package com.mtw.supplier.region

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.hexworks.mixite.core.api.*
import kotlin.math.roundToInt


enum class RegionGridLayout(val hexagonalGridLayout: HexagonalGridLayout) {
    RECTANGULAR(HexagonalGridLayout.RECTANGULAR),
    HEXAGONAL(HexagonalGridLayout.HEXAGONAL),
    TRIANGULAR(HexagonalGridLayout.TRIANGULAR),
    TRAPEZOID(HexagonalGridLayout.TRAPEZOID)
}

enum class RegionGridOrientation(val hexagonOrientation: HexagonOrientation) {
    FLAT_TOP(HexagonOrientation.FLAT_TOP),
    POINTY_TOP(HexagonOrientation.POINTY_TOP)
}

// Aside from the serialization thing, it's weird to refer to a [x, y, z] representation is "a coordinate".
// ...this is possibly just me being utterly insane.
@Serializable
data class CubeCoordinates(val gridX: Int, val gridZ: Int) {
    internal fun toMixiteCubeCoordinate(): CubeCoordinate {
        return CubeCoordinate.fromCoordinates(this.gridX, this.gridZ)
    }

    companion object {
        internal fun fromMixiteCubeCoordinate(coordinate: CubeCoordinate): CubeCoordinates {
            return CubeCoordinates(coordinate.gridX, coordinate.gridZ)
        }
    }
}

@Serializable
class Region(
    val gridHeight: Int,
    val gridWidth: Int,
    val gridLayout: RegionGridLayout,
    val gridOrientation: RegionGridOrientation,
    val gridHexRadius: Double = 6.0 // You shouldn't bother with this, really...
) {
    @Transient
    private val grid = HexagonalGridBuilder<RegionHex>()
        .setGridHeight(gridHeight)
        .setGridWidth(gridWidth)
        .setGridLayout(gridLayout.hexagonalGridLayout)
        .setOrientation(gridOrientation.hexagonOrientation)
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

    // <editor-fold desc="UI proxy">
    // Unsure on UI philosophy.
    fun getAllPoints(): List<List<Pair<Double,Double>>> {
        return this.grid.hexagons.map { hex -> hex.points.map { point -> Pair(point.coordinateX, point.coordinateY) } }
    }

    fun getByPixel(x: Double, y: Double): RegionHex? {
        val hexMaybe = this.grid.getByPixelCoordinate(x, y)
        return if (hexMaybe.isPresent) {
            cubeCoordinatesToRegionHexes[CubeCoordinates.fromMixiteCubeCoordinate(hexMaybe.get().cubeCoordinate)]
        } else {
            null
        }
    }
    // </editor-fold>

    class CoordinatesInvalidException(coordinates: CubeCoordinates):
        Exception("Coordinates [x=${coordinates.gridX}, z=${coordinates.gridZ}] are invalid!")

}
