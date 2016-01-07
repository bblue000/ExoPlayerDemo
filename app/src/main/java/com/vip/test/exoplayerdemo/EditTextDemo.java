package com.vip.test.exoplayerdemo;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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


        mNumber_ET.addTextChangedListener(new TextWatcher() {

            final char SPACE = ' ';
            StringBuilder mTempFilterBuffer = new StringBuilder(20);
            int beforeChangedLength; // 改变之前的字符长度
            int changeStart; // 该位置之前的不需要处理
            int changeEnd; //
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
                // 其他根据状态进行处理
                if (TextUtils.isEmpty(mNumber_ET.getText())) {
                    ViewUtils.setViewInvisible(mClearNumber_V);
                } else {
                    ViewUtils.setViewVisible(mClearNumber_V);
                }
                checkSubmitEnabled();
            }

            protected void formatCardNumber() {
                if (mTempFilterBuffer.length() > 0) {
                    mTempFilterBuffer.delete(0, mTempFilterBuffer.length());
                }
                if (changeStart == changeEnd) return; // 可以理解为删除字符

                boolean changed = false;
                int selection = changeEnd;
                CharSequence input = mNumber_ET.getText();

                mTempFilterBuffer.append(input.subSequence(0, changeStart));
                // 先处理从开始位置到第一个空格位置之间的
                // 1234 5678 9012 3456
                int anchor = changeStart, locIndex = changeStart % 5;
                char c;
                while (locIndex < 4) { // 一直填充到空格位置
                    if (input.length() <= anchor + 1) {
                        checkChanged(changed, selection);
                        return;
                    }

                    c = input.charAt(anchor);
                    if (c == SPACE) { // 不应该存在空格
                        selection --;
                        changed = true;
                    } else {
                        mTempFilterBuffer.append(c);
                        locIndex++;
                    }
                    anchor++;
                }

                if (locIndex >= 3) { // 这个位置之后应该是空格项
                    mTempFilterBuffer.append(SPACE);
                    selection ++;
                }

                c = input.charAt(anchor);
                if (c != SPACE) {
                    mTempFilterBuffer.append(SPACE);
                    mTempFilterBuffer.append(input.charAt(changeStart));
                    selection ++;
                }
//
//                int selection = changeEnd;
//                mTempFilterBuffer.append(input.subSequence(0, changeStart));
//
//                // 先处理开始位置，到第一个空格位置
//                int changeStartLoc = changeStart % 5;
//                char c = input.charAt(changeStartLoc);
//                if (changeStartLoc == 4) { // 如果是空格项
//                    if (input.charAt(changeStartLoc) != ' ') {
//                        mTempFilterBuffer.append(' ');
//                        mTempFilterBuffer.append(input.charAt(changeStart));
//                        selection ++;
//                    }
//                }
//                tranform();
//                for (int i = 0; i < afterChangedLength; i++) {
//                    if () {
//
//                    }
//                }
//
//                mNumber_ET.setSelection(selection);
            }

            protected void checkChanged(boolean changed, int selection) {
                if (changed) {
                    mNumber_ET.removeTextChangedListener(this);
                    mNumber_ET.setText(mTempFilterBuffer);
                    mNumber_ET.setSelection(selection);
                    mNumber_ET.addTextChangedListener(this);
                }
            }

            protected void transform(CharSequence input, int anchor, int locIndex, int to) {
                char c;
                while (locIndex != 4) { // 一直填充到空格位置
                    if (input.length() <= anchor + 1) { // 如果长度不够了，直接return
                        break;
                    }
                    c = input.charAt(anchor);
                    if (c == SPACE) { // 不应该存在空格
                        selection --;
                    } else {
                        mTempFilterBuffer.append(c);
                    }
                    anchor++;
                    locIndex++;
                }
            }
        });
    }
}
