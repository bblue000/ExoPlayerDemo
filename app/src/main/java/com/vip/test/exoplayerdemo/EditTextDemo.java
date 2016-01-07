package com.vip.test.exoplayerdemo;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.DrawableMarginSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.EditText;

import com.vip.sdk.base.utils.ViewUtils;

/**
 *
 * <p/>
 * <p/>
 * Created by Yin Yong on 16/1/7.
 *
 * @since 1.0
 */
public class EditTextDemo extends Activity {

    private EditText mNumber_ET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.et_demo);

        mNumber_ET = (EditText) findViewById(R.id.et);

        mNumber_ET.addTextChangedListener(new TextWatcher() {

            final char SPACE = ' ';
            StringBuilder mTempFilterBuffer = new StringBuilder(20);
            int beforeChangedLength; // 改变之前的字符长度
            int changeStart; // 该位置之前的不需要处理
            int changeEnd; // 改变的末尾位置，一般是光标所在位置
            int afterChangedLength; // 改变之后的字符长度

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeChangedLength = s.length(); // 记录修改之前的长度
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                afterChangedLength = s.length();
                changeStart = start;
                changeEnd = start + count;

                Log.d("yytest", "之前 len = " + beforeChangedLength);
                Log.d("yytest", "之后 len = " + afterChangedLength);
                Log.d("yytest", "从" + changeStart + "位置覆盖了" + count + "个字符，被覆盖了" + before + "个字符");
            }

            @Override
            public void afterTextChanged(Editable s) {
                formatCardNumber();
            }

            protected void formatCardNumber() {
                if (mTempFilterBuffer.length() > 0) mTempFilterBuffer.delete(0, mTempFilterBuffer.length());

                // 1234 5678 9012 3456 789
                if (changeStart == changeEnd && afterChangedLength <= changeStart + 1) return; // 可以理解为删除字符

                final CharSequence input = mNumber_ET.getText();
                boolean changed = false;
                int selection = changeEnd;

                mTempFilterBuffer.append(input.subSequence(0, changeStart));
                int anchor = changeStart, locIndex = anchor % 5;
                while (true) {
                    boolean needChangeSelection = anchor < changeEnd;
                    if (afterChangedLength <= anchor) { // 到末尾了
                        if (locIndex == 4) { // 如果是第四位数字，直接给它加上一个空格
                            changed = true;
                            if (needChangeSelection) selection ++;
                            mTempFilterBuffer.append(SPACE);
                        }
                        checkChanged(changed, selection);
                        return;
                    }
                    char c = input.charAt(anchor);
                    if (locIndex < 4) { // 如果是0-3
                        if (c != SPACE) {
                            mTempFilterBuffer.append(c);
                            locIndex ++;
                        } else { // 如果是空格，忽略不计
                            changed = true;
                            if (needChangeSelection) selection --;
                        }
                    } else if (locIndex == 4) {
                        locIndex = 0;
                        if (c != SPACE) {
                            changed = true;
                            if (needChangeSelection) selection ++;
                            mTempFilterBuffer.append(SPACE);
                            locIndex = 1;
                        }
                        mTempFilterBuffer.append(c);
                    }
                    anchor++;
                }
            }

            protected void checkChanged(boolean changed, int selection) {
                if (changed) {
                    mNumber_ET.removeTextChangedListener(this);
                    mNumber_ET.setText(mTempFilterBuffer);
                    mNumber_ET.setSelection(selection);
                    mNumber_ET.addTextChangedListener(this);
                }
            }
        });
    }
}
