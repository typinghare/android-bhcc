package csc244.internet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONObject
import java.io.File


class MainActivity : AppCompatActivity() {
    companion object {
        const val LOCAL_FILEPATH = "storage.json"

        const val LOCAL_KEY_CHOICE = "LOCAL_KEY_CHOICE"

        const val INTENT_JOKE_SETUP = "INTENT_JOKE_SETUP"
        const val INTENT_JOKE_PUNCHLINE = "INTENT_JOKE_PUNCHLINE"
        const val INTENT_DOG_IMAGE_URL = "INTENT_DOG_IMAGE_URL"
        const val INTENT_CAT_IMAGE_URL = "INTENT_CAT_IMAGE_URL"
        const val INTENT_CHOICE = "INTENT_CHOICE"

        const val API_JOKE = "https://dad-jokes.p.rapidapi.com/random/joke"
        const val API_DOT_IMAGE = "https://dog.ceo/api/breeds/image/random"
        const val API_CAT_IMAGE = "https://api.thecatapi.com/v1/images/search"

        enum class Choice { JOKE, DOG, CAT }
    }

    private var choice: Choice? = null
    private val gson: Gson = Gson()
    private var file: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            file = File(applicationContext.filesDir, LOCAL_FILEPATH)
            if (file!!.isFile) {
                val content: String = LocalStorage.read(file!!)
                val jsonObject = gson.fromJson<Map<String, *>>(content, MutableMap::class.java)
                val choiceStr: String = jsonObject[LOCAL_KEY_CHOICE] as String
                choice = Choice.valueOf(choiceStr)
                Log.d("choice", choice.toString())
            }
        } catch (e: Exception) {
            Log.d("error", e.message.toString())
        }

        val textMainCaption: TextView = findViewById(R.id.text_main_caption)
        val captionText: String = when (choice) {
            Choice.JOKE -> "You chose to see a joke last time."
            Choice.DOG -> "You chose to look at a dog last time."
            Choice.CAT -> "You chose to look at a cat last time."
            else -> "Select a button to Click!"
        }

        captionText.also { textMainCaption.text = it }

        val btnJoke: Button = findViewById(R.id.btn_joke)
        val btnDog: Button = findViewById(R.id.btn_dog)
        val btnCat: Button = findViewById(R.id.btn_cat)

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)

        btnJoke.setOnClickListener {
            choice = Choice.JOKE
            val request = object :
                JsonObjectRequest(Method.GET, API_JOKE, null, Response.Listener { response ->
                    val joke: JSONObject = response.getJSONArray("body")[0] as JSONObject
                    val setup = joke.getString("setup")
                    val punchline = joke.getString("punchline")

                    val intent = Intent(this, JokeDisplayActivity::class.java).apply {
                        putExtra(INTENT_JOKE_SETUP, setup)
                        putExtra(INTENT_JOKE_PUNCHLINE, punchline)
                    }

                    startActivity(intent)
                }, Response.ErrorListener { error ->
                    val message: String? = error.message
                    Log.d("errorMessage", message.toString())
                }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()

                    headers["X-RapidAPI-Key"] =
                        "ec59886533msh8e2d307072f3214p1190b2jsn0dcfdb617c2f"
                    headers["X-RapidAPI-Host"] = "dad-jokes.p.rapidapi.com"

                    return headers
                }
            }

            requestQueue.add(request)
        }

        btnDog.setOnClickListener {
            choice = Choice.DOG
            val request = object : JsonObjectRequest(Method.GET, API_DOT_IMAGE, null, { response ->
                val imageUrl: String = response.getString("message")

                val intent = Intent(this, PictureDisplayActivity::class.java).apply {
                    putExtra(INTENT_CHOICE, Choice.DOG)
                    putExtra(INTENT_DOG_IMAGE_URL, imageUrl)
                }

                startActivity(intent)
            }, { error ->
                Log.d("errorMessage", error.message.toString())
            }) {}

            requestQueue.add(request)
        }

        btnCat.setOnClickListener {
            choice = Choice.CAT
            val request = object : JsonArrayRequest(Method.GET, API_CAT_IMAGE, null, { response ->
                val res: JSONObject = response.get(0) as JSONObject
                val imageUrl: String = res.getString("url")

                val intent = Intent(this, PictureDisplayActivity::class.java).apply {
                    putExtra(INTENT_CHOICE, Choice.CAT)
                    putExtra(INTENT_CAT_IMAGE_URL, imageUrl)
                }

                startActivity(intent)
            }, { error ->
                Log.d("errorMessage", error.message.toString())
            }) {}

            requestQueue.add(request)
        }
    }

    override fun onPause() {
        super.onPause()
        save()
    }

    private fun save() {
        file = File(applicationContext.filesDir, LOCAL_FILEPATH)
        val map: HashMap<String, Any?> = HashMap()
        map[LOCAL_KEY_CHOICE] = choice

        val jsonString: String = gson.toJson(map)
        LocalStorage.write(file!!, jsonString)
    }
}