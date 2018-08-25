package co.hellocode.micro

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputEditText
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import uk.me.hardill.volley.multipart.MultipartRequest
import java.io.ByteArrayOutputStream
import javax.security.auth.login.LoginException

    const private val PICK_IMAGE = 1

class MainActivity : AppCompatActivity() {

    val PREFS_FILENAME = "co.hellocode.micro.prefs"
    val TOKEN = "token"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val prefs = this.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
        val token: String? = prefs?.getString(TOKEN, null)
        if (token == null) {
            Log.i("MainActivity", "token is null")
            val input = EditText(this)
            val builder = AlertDialog.Builder(this)
//            val fragment = DialogFragment()
            val dialog = builder.setView(input)
                    .setTitle("Set your app token")
                    .setNegativeButton("Cancel", { dialogInterface, i ->
                        dialogInterface.cancel()
                    })
                    .setPositiveButton("Save", { dialogInterface, i ->
                        Log.i("MainActivity", "token entered: ${input.text.toString()}")
                        // Do something with value!
                        prefs.edit().putString(TOKEN, input.text.toString().toLowerCase()).apply()
                        Toast.makeText(this, "Token set, thanks.", Toast.LENGTH_SHORT).show()
                    })
                    .create()
                    .show()
        }

        editText.requestFocus()

        photoButton.setOnClickListener {
            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = "image/*"
            val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "image/*"
            val chooserIntent = Intent.createChooser(getIntent, "Select image")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, Array(1) { pickIntent })
            startActivityForResult(chooserIntent, PICK_IMAGE)
        }

        sendButton.setTextColor(resources.getColor(R.color.colorBackground))
        sendButton.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        sendButton.setOnClickListener { view ->
            val text = editText.text.toString()
            val queue = Volley.newRequestQueue(this)
            val url = "https://micro.blog/micropub"

            val rq = object : StringRequest(
                    Request.Method.POST,
                    url,
                    Response.Listener<String> { response ->
                        Log.i("MainActivity", "resp: $response")
                        Snackbar.make(view, "Success!", Snackbar.LENGTH_LONG).show()
                        editText.setText("")
                    },
                    Response.ErrorListener { error ->
                        Log.i("MainActivity", "err: $error msg: ${error.message}")
                        Snackbar.make(view, "Error: $error", Snackbar.LENGTH_LONG).show()
                        // TODO: Handle error
                    })
            {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    val prefs = this@MainActivity.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
                    val token: String? = prefs?.getString(TOKEN, null)
                    headers["Authorization"] = "Bearer $token"
                    return headers
                }

                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["h"] = "entry"
                    params["content"] = text
                    return params
                }
            }
            queue.add(rq)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Log.i("MainActivity", "User picked an image")

            val stream = contentResolver.openInputStream(data.data)
            val bitmap = BitmapFactory.decodeStream(stream)
            stream.close()
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val image = baos.toByteArray()
            postImage(image)
        }
    }

    private fun postImage(image: ByteArray) {
        getMediaEndpoint(image)
    }

    fun uploadImage(endpoint: String, image: ByteArray) {
        val headers = HashMap<String, String>()
        val prefs = this@MainActivity.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
        val token: String? = prefs?.getString(TOKEN, null)
        headers["Authorization"] = "Bearer $token"

        val rq = MultipartRequest(endpoint,
               headers,
                Response.Listener {
                    if (it != null) {
                        val data = String(it.data)
                        Log.i("MainActivity", "Success! Resp: $data")
                        val obj = JSONObject(data)
                        if (obj["url"] != null) {
                            val imgURL = obj["url"] as String
                            editText.setText("\n\n![]($imgURL)")
                            editText.requestFocus()
                        }
                    }
                },
                Response.ErrorListener {
                    if (it.networkResponse != null) {
                        Log.i("MainActivity", "Error: ${String(it.networkResponse.data)}")
                    } else {
                        Log.i("MainActivity", "Error without network response: ${it.message}")
                    }
                })
        rq.addPart(MultipartRequest.FilePart("file", "image/jpeg", "file", image))
        val queue = Volley.newRequestQueue(this)
        queue.add(rq)
    }

    private fun getMediaEndpoint(image: ByteArray) {
        val url = "https://micro.blog/micropub?q=config"
        val rq = object : StringRequest(
                Request.Method.GET,
                url,
                Response.Listener<String> { response ->
                    Log.i("MainActivity", "resp: $response")
                    val json = JSONObject(response)
                    val endpoint = json["media-endpoint"] as String
                    uploadImage(endpoint, image)
                },
                Response.ErrorListener { error ->
                    Log.i("MainActivity", "err: $error msg: ${error.message}")
                    // TODO: Handle error
                })
        {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                val prefs = this@MainActivity.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
                val token: String? = prefs?.getString(TOKEN, null)
                headers["Authorization"] = "Bearer $token"
                return headers
            }

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["h"] = "entry"
                return params
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(rq)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
