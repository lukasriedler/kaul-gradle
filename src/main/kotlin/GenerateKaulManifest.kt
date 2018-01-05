import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.lukasriedler.kaul.Kaul
import com.github.lukasriedler.kaul.KaulManifest
import com.github.lukasriedler.kaul.KaulManifestPlatform
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

open class GenerateKaulManifest : DefaultTask() {
    @OutputFile
    val outputFile: File = File(project.buildDir.path + "${project.name}" + "-manifest.json")

    @InputFile
    val inputFile: File = File(project.buildDir.path + "${project.name}-${project.version}.zip")

    @TaskAction
    fun createManifest() {
        if (!outputFile.isDirectory && !outputFile.mkdirs())
            throw GradleException("Could not create $path")

        val configuration = project.extensions.create("kaul", KaulManifestExtension::class.java)

        val platform = "jvm"
        val applicationName = "${project.name}"
        val latestVersion = "${project.version}"
        val archive = "${project.name}-${project.version}.zip"
        val location = configuration.location
        val changelog = configuration.changelog
        val hash = Kaul("").getSHA256HashOfFile(inputFile)
        val signature = ""
        val kaulManifest = KaulManifest(mutableListOf(KaulManifestPlatform(platform, applicationName, latestVersion, archive, location, changelog, hash, signature)))

        val mapper = jacksonObjectMapper()
        val json = mapper.writeValueAsString(kaulManifest)

        outputFile.writeText(json)
    }
}

class KaulManifestExtension {
    val location = ""
    val changelog = ""
}