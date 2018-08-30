package co.hellocode.micro.Utils


import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.Spannable
import android.text.style.LeadingMarginSpan
import android.text.style.LineBackgroundSpan
import android.text.style.QuoteSpan
import co.hellocode.micro.R

/**
 * Created by Josh on 2/12/2015.
 */
class CustomQuoteSpan(private val backgroundColor: Int, private val stripeColor: Int, private val stripeWidth: Float, private val gap: Float) : LeadingMarginSpan, LineBackgroundSpan {

    override fun getLeadingMargin(first: Boolean): Int {
        return (stripeWidth + gap).toInt()
    }

    override fun drawLeadingMargin(c: Canvas, p: Paint, x: Int, dir: Int, top: Int, baseline: Int, bottom: Int,
                                   text: CharSequence, start: Int, end: Int, first: Boolean, layout: Layout) {
        val style = p.style
        val paintColor = p.color

        p.style = Paint.Style.FILL
        p.color = stripeColor

        c.drawRect(x.toFloat(), top.toFloat(), x + dir * stripeWidth, bottom.toFloat(), p)

        p.style = style
        p.color = paintColor
    }

    override fun drawBackground(c: Canvas, p: Paint, left: Int, right: Int, top: Int, baseline: Int, bottom: Int, text: CharSequence, start: Int, end: Int, lnum: Int) {
        val paintColor = p.color
        p.color = backgroundColor
        c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), p)
        p.color = paintColor
    }

    companion object {

        fun replaceQuoteSpans(spannable: Spannable, context: Context) {
            val quoteSpans = spannable.getSpans(0, spannable.length, QuoteSpan::class.java)

            val backgroundColor = context.resources.getColor(R.color.colorBackground)
            val borderColor = context.resources.getColor(R.color.colorPrimary)

            for (quoteSpan in quoteSpans) {
                val start = spannable.getSpanStart(quoteSpan)
                val end = spannable.getSpanEnd(quoteSpan)
                val flags = spannable.getSpanFlags(quoteSpan)
                spannable.removeSpan(quoteSpan)
                spannable.setSpan(CustomQuoteSpan(
                        backgroundColor,
                        borderColor,
                        8f,
                        32f),
                        start,
                        end,
                        flags)
            }
        }
    }
}