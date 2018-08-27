package co.hellocode.micro

import android.text.Html
import android.text.Spanned
import android.util.Log
import net.nightwhistler.htmlspanner.HtmlSpanner



class Post(var text: String, val author: String, val username: String, val isConversation: Boolean = false) {
    var html: Spanned = Html.fromHtml(this.text)



}