package com.mtw.supplier.region

import org.hexworks.mixite.core.api.Hexagon
import org.hexworks.mixite.core.api.HexagonOrientation
import org.hexworks.mixite.core.api.HexagonalGridBuilder
import org.hexworks.mixite.core.api.HexagonalGridLayout
import org.hexworks.mixite.core.api.Point
import org.hexworks.mixite.core.api.contract.SatelliteData
import kotlin.math.roundToInt

class Region {
    fun lol() {

        val GRID_HEIGHT = 8
        val GRID_WIDTH = 8
        val GRID_LAYOUT = HexagonalGridLayout.RECTANGULAR
        val ORIENTATION = HexagonOrientation.FLAT_TOP
        val RADIUS = 6.0

        val builder = HexagonalGridBuilder<SatelliteData>()
            .setGridHeight(GRID_HEIGHT)
            .setGridWidth(GRID_WIDTH)
            .setGridLayout(GRID_LAYOUT)
            .setOrientation(ORIENTATION)
            .setRadius(RADIUS)

        val grid = builder.build()
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

    private fun recordHex(hex: Hexagon<SatelliteData>, pointMap: MutableMap<Pair<Int,Int>, Char>) {
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
