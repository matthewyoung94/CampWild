package com.example.campwild

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class InformationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        toolbar.title = "Camp Wild"
        val tvScottishAccessCode = findViewById<TextView>(R.id.tvScottishAccessCode)
        val spannableStringScottish = SpannableString(getString(R.string.scottish_access_code_link))
        val clickableSpanScottish: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                openWebPage("https://www.outdooraccess-scotland.scot/")
            }
        }
        spannableStringScottish.setSpan(
            clickableSpanScottish,
            0,
            spannableStringScottish.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tvScottishAccessCode.text = spannableStringScottish
        tvScottishAccessCode.movementMethod = LinkMovementMethod.getInstance()

        val tvLeaveNoTrace = findViewById<TextView>(R.id.tvLeaveNoTrace)
        val spannableStringLeaveNoTrace = SpannableString(getString(R.string.leave_no_trace_link))
        val clickableSpanLeaveNoTrace: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                openWebPage("https://lnt.org/why/7-principles/")
            }
        }
        spannableStringLeaveNoTrace.setSpan(
            clickableSpanLeaveNoTrace,
            0,
            spannableStringLeaveNoTrace.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tvLeaveNoTrace.text = spannableStringLeaveNoTrace
        tvLeaveNoTrace.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun openWebPage(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}
