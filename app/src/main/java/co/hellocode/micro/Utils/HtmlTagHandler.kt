package co.hellocode.micro.Utils

import android.content.Context
import android.text.Editable
import android.text.Html
import android.text.Spannable
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.text.style.BulletSpan
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.text.style.LeadingMarginSpan
import android.text.style.RelativeSizeSpan
import android.text.style.TypefaceSpan
import android.util.Log

import org.xml.sax.SAXException
import org.xml.sax.XMLReader

import java.io.IOException
import java.util.Vector

/**
 * Created by Josh on 2/12/2015.
 */
class HtmlTagHandler(private val context: Context) : Html.TagHandler {
    private var mListItemCount = 0
    private val mListParents = Vector<String>()

    override fun handleTag(opening: Boolean, tag: String, output: Editable, xmlReader: XMLReader) {

        when (tag.toLowerCase()) {
            "ul", "ol" -> {
                if (opening) {
                    mListParents.add(tag)
                    //output.append('\n');
                } else
                    mListParents.remove(tag)

                mListItemCount = 0
            }
            "li" -> if (!opening) {
                handleListTag(output)
            }
            "code" -> if (opening) {
                output.setSpan(TypefaceSpan("monospace"), output.length, output.length, Spannable.SPAN_MARK_MARK)
            } else {
                Log.d("Code Tag", "Code tag encountered")
                val obj = getLast(output, TypefaceSpan::class.java)
                val where = output.getSpanStart(obj)

                output.setSpan(TypefaceSpan("monospace"), where, output.length, 0)
                output.setSpan(ForegroundColorSpan(-0x1000000), where, output.length, 0)
                output.setSpan(BackgroundColorSpan(-0x11000001), where, output.length, 0)
                output.setSpan(RelativeSizeSpan(0.9f), where, output.length, 0)
            }
        }

    }

    private fun getLast(text: Editable, kind: Class<*>): Any? {
        val objs = text.getSpans(0, text.length, kind)
        if (objs.size == 0) {
            return null
        } else {
            for (i in objs.size downTo 1) {
                if (text.getSpanFlags(objs[i - 1]) == Spannable.SPAN_MARK_MARK) {
                    return objs[i - 1]
                }
            }
            return null
        }
    }

    private fun handleListTag(output: Editable) {
        if (mListParents.lastElement() == "ul") {
            output.append('\n')
            val split = output.toString().split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            val lastIndex = split.size - 1

            //Log.d("li1",split[lastIndex]);

            val start = output.length - split[lastIndex].length - 1
            //
            //            if (!output.subSequence(start,output.length()).toString().equals(split[lastIndex])){
            //                start -= 2;
            //            }

            //Log.d("li2",output.subSequence(start,output.length()).toString());

            output.setSpan(BulletSpan(30), start, output.length, 0)
            //output.append("\n");
        } else if (mListParents.lastElement() == "ol") {
            mListItemCount++

            output.append("\n")
            val split = output.toString().split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            val lastIndex = split.size - 1
            val start = output.length - split[lastIndex].length - 1
            output.insert(start, mListItemCount.toString() + ". ")
            output.setSpan(LeadingMarginSpan.Standard(30), start, output.length, 0)
        }
    }
}