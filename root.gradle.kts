import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

plugins {
    id("com.replaymod.preprocess") version "1.0-dev.4"
    id("dev.architectury.loom") apply false
    id("me.fallenbreath.yamlang") version "1.3.1" apply false
    id("me.modmuss50.mod-publish-plugin") version "0.4.5" apply false
}

preprocess {
    val fabric194 = createNode("1.19.4-fabric", 11904, "yarn")
    val fabric201 = createNode("1.20.1-fabric", 12001, "yarn")
    val fabric202 = createNode("1.20.2-fabric", 12002, "yarn")

    val forge194 = createNode("1.19.4-forge", 11904, "yarn")
    val forge201 = createNode("1.20.1-forge", 12001, "yarn")
    val forge202 = createNode("1.20.2-forge", 12002, "yarn")

    fabric194.link(fabric201, null)
    fabric201.link(fabric202, null)

    fabric194.link(forge194, null)
    fabric201.link(forge201, null)
    fabric202.link(forge202, null)
}

tasks.register("updateProperties") {
    val versionsDir = rootDir.resolve("versions")

}

fun createProperties(version: File) {
    val (mcVersion, loader) = version.name.split("-")
    val prop = version.resolve("gradle.properties")

}

@Suppress("UNCHECKED_CAST")
fun requestModrinth(mod: String, mc: String): String {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://api.modrinth.com/v2/project/$mod/version")
        .build()

    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) throw IOException("Unexpected code $response")
        val type = object : TypeToken<List<Map<String, Any>>>() {}.type
        val data: List<Map<String, Any>> = Gson().fromJson(response.body!!.string(), type)
        data.forEach {
            if ((it["game_versions"] as List<String>).contains(mc))
                return it["version_number"] as String
        }
    }
    return "NOT_FOUND"
}