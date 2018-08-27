package co.hellocode.micro

import android.text.Html
import android.text.Spanned
import android.text.style.ImageSpan
import android.util.Log
import com.squareup.picasso.Picasso
import net.nightwhistler.htmlspanner.HtmlSpanner



class Post(var text: String, val author: String, val username: String, val isConversation: Boolean = false) {
    var html: Spanned = Html.fromHtml(this.text)

//    val imageGetter = Html.ImageGetter {
//        for (img in it.getSpans(0,
//                it.length(), ImageSpan::class.java)) {
//            Picasso.get().load(img.source)
//        }
//    }
}