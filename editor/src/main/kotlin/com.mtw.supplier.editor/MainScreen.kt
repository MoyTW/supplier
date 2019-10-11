package com.mtw.supplier.editor

import com.mtw.supplier.region.Region
import com.mtw.supplier.region.RegionGridLayout
import com.mtw.supplier.region.RegionGridOrientation
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.control.ScrollPane
import javafx.stage.FileChooser
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import tornadofx.*
import java.io.File
import java.nio.file.Paths

data class RegionFile(var name: String, var path: File?, var region: Region)

class MainScreen : View() {
    private val json = Json(JsonConfiguration.Stable.copy(prettyPrint = true))

    private val runPath = Paths.get("").toAbsolutePath().toString()
    private val pngFilters = arrayOf(FileChooser.ExtensionFilter("pngs!!!", "*png"))
    private val regionFileFilters = arrayOf(FileChooser.ExtensionFilter("REGION FILE", "*region"))

    private val gridHeight = SimpleIntegerProperty(this, "gridHeight", 20)
    private val gridWidth = SimpleIntegerProperty(this, "gridWidth", 20)
    private var regionFile: RegionFile = RegionFile(
        "Unnamed Region File",
        null,
        Region(gridHeight.value, gridWidth.value, RegionGridLayout.RECTANGULAR, RegionGridOrientation.FLAT_TOP, gridHexRadius = 10.0)
    )

    override val root = borderpane {
        top = menubar {
            menu("File") {
                item("Load", "Shortcut+L").action { doLoadRegionFile() }
                item("Save", "Shortcut+S").action { doSaveRegionFile() }
                item("Quit", "Shortcut+Q").action {
                    println("QUIT")
                }
            }
        }
        left = form {
            fieldset("Grid Controls") {
                field("Grid Height") { textfield { bind(gridHeight) } }
                field("Grid Width") { textfield { bind(gridWidth) } }
                button("Regenerate Hexes").action {
                    doRegenerate()
                }
            }
        }
        center = regionRender(regionFile.region)
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
                regionFile.name = file.name
                regionFile.path = file
                regionFile.region = region
                gridHeight.set(region.gridHeight)
                gridWidth.set(region.gridWidth)
                center = regionRender(region)
            }
        }
    }

    private fun doSaveRegionFile() {
        if (regionFile.path == null) {
            var outFile = chooseFile("Name the save file", regionFileFilters, FileChooserMode.Save, op = { initialDirectory = File(runPath) }).firstOrNull()
            if (outFile != null) {
                if (!outFile.absolutePath.endsWith(".region")) {
                    outFile = File(outFile.absolutePath + ".region")
                }
                regionFile.path = outFile
                val regionJson = json.stringify(Region.serializer(), regionFile.region)
                outFile.writeText(regionJson)
            }
        } else {
            val regionJson = json.stringify(Region.serializer(), regionFile.region)
            regionFile.path!!.writeText(regionJson)
        }
    }

    private fun doLoadImage() {
        with (root) {
            val file = chooseFile("FILE", pngFilters, op = { initialDirectory = File(runPath) })
            if (file.isNotEmpty()) {
                println(file[0].path)
                center = imageview("file:\\${file[0].canonicalPath}")
            }
        }
    }

    private fun doRegenerate() {
        with(root) {
            regionFile.region = Region(gridHeight.value, gridWidth.value, RegionGridLayout.RECTANGULAR, RegionGridOrientation.FLAT_TOP, gridHexRadius = 10.0)
            center = regionRender(regionFile.region)
        }
    }

    private fun regionRender(region: Region): ScrollPane {
        return scrollpane {
            group {
                region.getAllPoints().map {
                    polyline(
                        it[0].first, it[0].second,
                        it[1].first, it[1].second,
                        it[2].first, it[2].second,
                        it[3].first, it[3].second,
                        it[4].first, it[4].second,
                        it[5].first, it[5].second,
                        it[0].first, it[0].second
                    )
                }
            }
        }
    }
}
