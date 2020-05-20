package com.opsly.demo.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestBody
import java.net.URL
import kotlinx.coroutines.*
import org.springframework.web.bind.annotation.RequestMapping
import kotlin.system.measureTimeMillis


@Controller
class Controller {

    @RequestMapping("/")
    fun home(): String {
        return "index.html"
    }
}

@RestController
class RestController {
    val hostName: String = "https://takehome.io"

    @GetMapping(path = ["/socialsasync"])
    fun getAllSocialsAsync(): OutputJson = runBlocking<OutputJson> {
        val tw = async { getSocialAsync("twitter") }
        val fb = async { getSocialAsync("facebook") }
        val insta = async { getSocialAsync("instagram") }
        var response = OutputJson(arrayOf<String>(), arrayOf<String>(), arrayOf<String>())
        val t = measureTimeMillis {
            response = OutputJson(tw.await(), fb.await(), insta.await())
        }
        println("time to run $t")
        return@runBlocking response
    }

    @GetMapping(path = ["/socials"])
    fun getAllSocials(): OutputJson {
        var response = OutputJson(arrayOf<String>(), arrayOf<String>(), arrayOf<String>())
        val timeToRun = measureTimeMillis {
            response = OutputJson(getSocial("twitter"), getSocial("facebook"), getSocial("instagram"))
        }
        println(timeToRun)
        return response
    }

    @GetMapping(path=["/easteregg"])
    fun getee(): String {
        return URL("https://raw.githubusercontent.com/lancer278/coding_demo_react_redux_client/master/public/bouncy.html").readText()
    }

    fun getSocial(social: String): Any {
        return try {
            URL("${hostName}/${social}").readText()
        } catch (e: Exception) {
            println(e)
            arrayOf<String>()
        }
    }

    suspend fun getSocialAsync(social: String): Any = withContext(Dispatchers.Default) {
        try {
            var resp = ""
            val t = measureTimeMillis {
                resp = URL("${hostName}/${social}").readText()
            }
            println("${social} runtime: $t")
            return@withContext resp
        } catch (e: Exception) {
            println("${social} call failed")
            println(e)
            return@withContext arrayOf<String>()
        }

    }
}


data class OutputJson(val twitter:Any, val facebook: Any, val instagram: Any)
