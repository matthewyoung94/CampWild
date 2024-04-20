package com.example.campwild;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class InformationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        // Handle the Scottish Access Code link
        TextView tvScottishAccessCode = findViewById(R.id.tvScottishAccessCode);
        SpannableString spannableStringScottish = new SpannableString(getString(R.string.scottish_access_code_link));
        ClickableSpan clickableSpanScottish = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                openWebPage("https://www.outdooraccess-scotland.scot/");
            }
        };
        spannableStringScottish.setSpan(clickableSpanScottish, 0, spannableStringScottish.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvScottishAccessCode.setText(spannableStringScottish);
        tvScottishAccessCode.setMovementMethod(LinkMovementMethod.getInstance());

        // Handle the Leave No Trace link
        TextView tvLeaveNoTrace = findViewById(R.id.tvLeaveNoTrace);
        SpannableString spannableStringLeaveNoTrace = new SpannableString(getString(R.string.leave_no_trace_link));
        ClickableSpan clickableSpanLeaveNoTrace = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                openWebPage("https://lnt.org/why/7-principles/");
            }
        };
        spannableStringLeaveNoTrace.setSpan(clickableSpanLeaveNoTrace, 0, spannableStringLeaveNoTrace.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvLeaveNoTrace.setText(spannableStringLeaveNoTrace);
        tvLeaveNoTrace.setMovementMethod(LinkMovementMethod.getInstance());
    }


    private void openWebPage(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}
