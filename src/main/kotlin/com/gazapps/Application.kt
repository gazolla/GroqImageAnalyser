package com.gazapps

import com.groq.api.client.GroqClientFactory
import com.groq.api.utils.JsonUtils
import io.ktor.http.content.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.*
import org.slf4j.LoggerFactory
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

fun main() {
    embeddedServer(Netty, port = 8080) {
        // Create upload directory if it doesn't exist
        val uploadDir = File("uploads")
        if (!uploadDir.exists()) {
            uploadDir.mkdirs()
        }

        install(Thymeleaf) {
            setTemplateResolver(ClassLoaderTemplateResolver().apply {
                prefix = "/templates/"
                suffix = ".html"
                characterEncoding = "UTF-8"
                templateMode = TemplateMode.HTML
            })
        }

        install(ContentNegotiation) {
            jackson()
        }

        install(Resources)

        val logger = LoggerFactory.getLogger(Application::class.java)

        routing {
            staticFiles("/static", File("src/main/resources/static")) {
                default("index.html")
            }

            // Serve uploaded files
            staticFiles("/uploads", uploadDir)

            get("/") {
                call.respond(ThymeleafContent("index", emptyMap()))
            }

            // Route to reset the form (return empty result)
            get("/reset") {
                call.respond(ThymeleafContent("reset", emptyMap()))
            }

            post("/upload") {
                val multipart = call.receiveMultipart()
                var fileName = ""
                var fileDescription = ""

                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FileItem -> {
                            // Generate a UUID filename to prevent conflicts
                            val originalFileName = part.originalFileName ?: "unknown"
                            val fileExtension = originalFileName.substringAfterLast('.', "jpg")
                            fileName = "${UUID.randomUUID()}.$fileExtension"
                            val file = File(uploadDir, fileName)

                            // Save the file
                            part.streamProvider().use { input ->
                                file.outputStream().buffered().use { output ->
                                    input.copyTo(output)
                                }
                            }
                            logger.info("Uploaded file: $fileName")
                        }
                        else -> {}
                    }
                    part.dispose()
                }

                if (fileName.isNotEmpty()) {
                    // Use the Groq API to describe the image
                    try {
                        // Path to the local file
                        val filePath = File(uploadDir, fileName).absolutePath
                        fileDescription = describeImageFromFile(filePath)
                        logger.info("Image description: $fileDescription")
                    } catch (e: Exception) {
                        logger.error("Error getting image description", e)
                        fileDescription = "Error processing image: ${e.message}"
                    }
                }

                // Respond with the image info and hide the upload form
                call.respond(ThymeleafContent("image-result", mapOf(
                    "filename" to fileName,
                    "description" to fileDescription
                )))
            }
        }
    }.start(wait = true)
}

suspend fun describeImageFromFile(filePath: String): String {
    val apiKey = System.getenv("GROQ_API_KEY") ?: "Api key here"
    return try {
        GroqClientFactory.createClient(apiKey).use { client ->
            val response = client.createVisionCompletionWithBase64Image(
                imagePath = filePath,
                prompt = "Describe what you see in this image in detail.",
                model = "llama-3.2-90b-vision-preview"
            )

            JsonUtils.extractContentFromCompletion(response) ?: "No description available."
        }
    } catch (e: Exception) {
        "Groq API error: ${e.message}"
    }
}