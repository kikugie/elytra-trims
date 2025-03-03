import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.getChannelOf
import dev.kord.core.entity.channel.NewsChannel
import dev.kord.core.entity.channel.TextChannel
import dev.kord.rest.builder.message.embed
import io.ktor.client.request.forms.ChannelProvider
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import kotlin.io.path.name
import kotlin.io.path.readBytes

abstract class PublishDiscordTask : DefaultTask() {
    @get:Input
    abstract val version: Property<String>

    @get:Input
    abstract val changelog: Property<String>

    @get:Input
    abstract val token: Property<String>

    @get:Input
    abstract val server: Property<Long>

    @get:Input
    abstract val channel: Property<Long>

    @get:Input
    abstract val role: Property<Long>

    @get:InputDirectory
    abstract val files: DirectoryProperty

    @get:InputFiles
    abstract val extras: Property<FileCollection>

    init {
        changelog.convention("")
        extras.convention(project.objects.fileCollection())
    }

    @TaskAction
    fun run() = runBlocking {
        val guild = Kord(token.get()).getGuild(Snowflake(server.get()))
        val channel = guild.getChannelOf<NewsChannel>(Snowflake(channel.get()))
        val files = files.get().asFileTree.files.filter { it.isFile }.map { it.toPath() }
        val extras = extras.get().files.map { it.toPath() }

        val blocks = mutableMapOf<String, MutableList<String>>()
        var current = ""
        for (it in changelog.get().ifBlank { "No changelog provided." }.lineSequence()) {
            if (it.startsWith('#')) current = it.trimStart('#', ' ')
            else blocks.getOrPut(current) { mutableListOf() } += it
        }
        val paragraphs = blocks.mapValues { (_, it) -> it.joinToString("\n") }

        channel.createMessage {
            content = """
                # ${guild.getRole(Snowflake(role.get())).mention} Elytra Trims ${version.get()} is available!
            """.trimIndent()
            for ((title, text) in paragraphs) embed {
                this.title = title
                description = text
                color = Color(0xDB67D9)
            }

            for (it in files) {
                val channel = ChannelProvider(null) { ByteReadChannel(it.readBytes()) }
                addFile(it.fileName.name, channel)
            }
            for (it in extras) {
                val channel = ChannelProvider(null) { ByteReadChannel(it.readBytes()) }
                addFile(it.fileName.name, channel)
            }
        }
    }
}