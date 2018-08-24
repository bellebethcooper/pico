package co.hellocode.micro

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import com.android.volley.AuthFailureError
import org.json.JSONObject


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

        val editText = findViewById<EditText>(R.id.editText)
        editText.requestFocus()

        val sendButton = findViewById<Button>(R.id.sendButton)
        sendButton.setOnClickListener { view ->
            val editText = findViewById<EditText>(R.id.editText)
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
            Log.i("MainActivity", rq.toString())
            queue.add(rq)
        }
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
