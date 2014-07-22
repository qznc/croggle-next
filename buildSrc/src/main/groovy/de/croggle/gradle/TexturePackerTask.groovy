package de.croggle.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.incremental.IncrementalTaskInputs

class TexturePackerTask extends DefaultTask {
    @InputDirectory
    def File inputDir

    @OutputDirectory
    def File outputDir

    @TaskAction
    void execute(IncrementalTaskInputs inputs) {
        println inputs.incremental ? "CHANGED inputs considered out of date" : "ALL inputs considered out of date"
        inputs.outOfDate { change ->
            println "out of date: ${change.file.name}"
        }

        inputs.removed { change ->
            println "removed: ${change.file.name}"
        }
    }
}
