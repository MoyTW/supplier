package com.mtw.supplier.editor

import com.mtw.supplier.region.Region
import com.mtw.supplier.region.RegionGridLayout
import com.mtw.supplier.region.RegionGridOrientation
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.Group
import javafx.scene.control.ScrollPane
import javafx.scene.image.Image
import javafx.stage.FileChooser
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.hexworks.mixite.core.api.HexagonOrientation
import org.hexworks.mixite.core.api.HexagonalGrid
import org.hexworks.mixite.core.api.HexagonalGridBuilder
import org.hexworks.mixite.core.api.HexagonalGridLayout
import org.hexworks.mixite.core.api.contract.SatelliteData
import tornadofx.*
import java.io.File
import java.nio.file.Paths


class MainScreen : View() {
    data class RegionData(var name: String, var path: File?, var region: Region, var grid: HexagonalGrid<SatelliteData>)
    data class BackgroundFile(var path: File?)

    private val json = Json(JsonConfiguration.Stable.copy(prettyPrint = true))

    private val runPath = Paths.get("").toAbsolutePath().toString()
    private val pngFilters = arrayOf(FileChooser.ExtensionFilter("pngs!!!", "*png"))
    private val regionFileFilters = arrayOf(FileChooser.ExtensionFilter("REGION FILE", "*region"))

    private val gridHeight = SimpleIntegerProperty(this, "gridHeight", 20)
    private val gridWidth = SimpleIntegerProperty(this, "gridWidth", 20)
    private val gridRadius = SimpleDoubleProperty(this, "gridRadius", 10.0)
    private var regionData: RegionData = RegionData(
        "Unnamed Region",
        null,
        Region(gridHeight.value, gridWidth.value, RegionGridLayout.RECTANGULAR, RegionGridOrientation.FLAT_TOP),
        HexagonalGridBuilder<SatelliteData>()
            .setGridHeight(gridHeight.value)
            .setGridWidth(gridWidth.value)
            .setGridLayout(HexagonalGridLayout.RECTANGULAR)
            .setOrientation(HexagonOrientation.FLAT_TOP)
            .setRadius(gridRadius.value)
            .build()
    )
    private var backgroundFile: BackgroundFile = BackgroundFile(null)

    override val root = borderpane {
        top = menubar {
            menu("File") {
                item("Load", "Shortcut+L").action { doLoadRegionFile() }
                item("Save", "Shortcut+S").action { doSaveRegionFile() }
                item("Save As", "F12").action { doSaveAsRegionFile() }
                item("Quit", "Shortcut+Q").action {
                    println("QUIT")
                }
            }
        }
        left = form {
            fieldset("Grid Controls") {
                field("Grid Height") { textfield { bind(gridHeight) } }
                field("Grid Width") { textfield { bind(gridWidth) } }
                field("Grid Radius") { textfield { bind(gridRadius) } }
                button("Regenerate Hexes").action { doRegenerate() }
            }
            fieldset("Background Controls") {
                button("Load Background").action { doLoadBackground() }
            }
        }
        center = centerRender(backgroundFile, regionData.region)
    }

    init {
        with (root) {
            prefWidth = 800.0
            prefHeight = 600.0
        }
    }

    private fun doLoadRegionFile() {
        with (root) {
            val file = chooseFile("Select the region file", regionFileFilters, op = { initialDirectory = File(runPath) }).firstOrNull()
            if (file != null) {
                val region = json.parse(Region.serializer(), file.readText())
                regionData.name = file.name
                regionData.path = file
                regionData.region = region
                gridHeight.set(region.gridHeight)
                gridWidth.set(region.gridWidth)
                regionData.grid = HexagonalGridBuilder<SatelliteData>()
                    .setGridHeight(gridHeight.value)
                    .setGridWidth(gridWidth.value)
                    .setGridLayout(region.gridLayout.hexagonalGridLayout)
                    .setOrientation(region.gridOrientation.hexagonOrientation)
                    .setRadius(gridRadius.value)
                    .build()
                center = centerRender(backgroundFile, region)
            }
        }
    }

    private fun doSaveAsRegionFile() {
        var outFile = chooseFile("Name the save file", regionFileFilters, FileChooserMode.Save, op = { initialDirectory = File(runPath) }).firstOrNull()
        if (outFile != null) {
            if (!outFile.absolutePath.endsWith(".region")) {
                outFile = File(outFile.absolutePath + ".region")
            }
            regionData.path = outFile
            val regionJson = json.stringify(Region.serializer(), regionData.region)
            outFile.writeText(regionJson)
        }
    }

    private fun doSaveRegionFile() {
        if (regionData.path == null) {
            doSaveAsRegionFile()
        } else {
            val regionJson = json.stringify(Region.serializer(), regionData.region)
            regionData.path!!.writeText(regionJson)
        }
    }

    private fun doLoadBackground() {
        with (root) {
            val file = chooseFile("FILE", pngFilters, op = { initialDirectory = File(runPath) }).firstOrNull()
            if (file != null) {
                backgroundFile.path = file
                center = centerRender(backgroundFile, regionData.region)
            }
        }
    }

    private fun doRegenerate() {
        with(root) {
            val region = Region(gridHeight.value, gridWidth.value, RegionGridLayout.RECTANGULAR, RegionGridOrientation.FLAT_TOP)
            regionData.region = region
            regionData.grid = HexagonalGridBuilder<SatelliteData>()
                .setGridHeight(gridHeight.value)
                .setGridWidth(gridWidth.value)
                .setGridLayout(region.gridLayout.hexagonalGridLayout)
                .setOrientation(region.gridOrientation.hexagonOrientation)
                .setRadius(gridRadius.value)
                .build()
            center = centerRender(backgroundFile, regionData.region)
        }
    }

    private fun maxXY(data: RegionData): Pair<Double,Double> {
        var maxX = 0.0
        var maxY = 0.0
        data.grid.hexagons.forEach {
            it.points.forEach { point ->
                if (point.coordinateX > maxX) {
                    maxX = point.coordinateX
                }
                if (point.coordinateY > maxY) {
                    maxY = point.coordinateY
                }
            }
        }
        return Pair(maxX, maxY)
    }

    private fun regionLines(data: RegionData): Group {
        return group {
            data.grid.hexagons.map {
                polyline(
                    it.points[0].coordinateX, it.points[0].coordinateY,
                    it.points[1].coordinateX, it.points[1].coordinateY,
                    it.points[2].coordinateX, it.points[2].coordinateY,
                    it.points[3].coordinateX, it.points[3].coordinateY,
                    it.points[4].coordinateX, it.points[4].coordinateY,
                    it.points[5].coordinateX, it.points[5].coordinateY,
                    it.points[0].coordinateX, it.points[0].coordinateY
                )
            }
        }
    }

    // I can't figure out how to make these things work in functions so I'm doing this now because wait it's 12:30
    // already what the i got work tomorrow. wtf. ok technically i got work today. rip me
    private fun centerRender(backgroundFile: BackgroundFile, region: Region): ScrollPane {
        val (maxX, maxY) = maxXY(regionData)
        val sp = if (backgroundFile.path != null) {
            scrollpane {
                stackpane {
                    imageview {
                        image=Image("file:\\${backgroundFile.path!!.canonicalPath}", maxX, maxY, false, false)
                    }
                    children.add(regionLines(regionData))
                }
            }
        } else {
            ScrollPane(regionLines(regionData))
        }
        sp.setOnMouseClicked {
            println("Click at [${it.x}, ${it.y}]")
            println(regionData.grid.getByPixelCoordinate(it.x, it.y))
        }
        return sp
    }
}
