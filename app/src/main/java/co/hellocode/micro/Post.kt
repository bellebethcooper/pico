package co.hellocode.micro

import android.text.Html
import android.text.Spanned
import android.text.style.ImageSpan
import android.util.Log
import com.squareup.picasso.Picasso
import net.nightwhistler.htmlspanner.HtmlSpanner
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class Post(val item: JSONObject) {
    val ID: Int
    val html: Spanned
    val author: String
    val username: String
    var isConversation: Boolean = false
    val date: Date
    val mentions: ArrayList<String>

    init {
        this.ID = (item["id"] as String).toInt()
        val text = item["content_html"] as String
        val content = text.trim()
        this.html = Html.fromHtml(content)
        var mentions: ArrayList<String> = arrayListOf()
        val regex = Regex("[@]\\w+")
        val all = regex.findAll(text)
        for (match in all) {
            mentions.add(match.value)
        }
        this.mentions = mentions
        val datePublished = item["date_published"] as String
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'+'ss':'ss")
        this.date = dateFormat.parse(datePublished)
        val author = (item["author"] as JSONObject)
        this.author = author.getString("name")
        this.username = (author["_microblog"] as JSONObject).getString("username")
        val microblogData = (item["_microblog"] as JSONObject)
        this.isConversation = microblogData.getBoolean("is_conversation")

    }

//    val imageGetter = Html.ImageGetter {
//        for (img in it.getSpans(0,
//                it.length(), ImageSpan::class.java)) {
//            Picasso.get().load(img.source)
//        }
//    }
}