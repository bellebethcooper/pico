package co.hellocode.micro

import android.text.Html
import android.text.Spanned
import android.util.Log
import net.nightwhistler.htmlspanner.HtmlSpanner



class Post(var text: String, val author: String) {
    var html: Spanned = Html.fromHtml(this.text)



}