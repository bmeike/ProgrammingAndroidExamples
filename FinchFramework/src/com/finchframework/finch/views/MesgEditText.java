package com.finchframework.finch.views;

import android.content.Context;
import android.graphics.Rect;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.EditText;

/**
 * A text field that can display a default message until a user types in their
 * own text. 
 */
public class MesgEditText extends EditText {
    private String mMesgText;

    public MesgEditText(Context context) {
        super(context);
    }

    public MesgEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MesgEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setMesgText(String messageText) {
        mMesgText = messageText;
        setText(messageText);
    }

    @Override
    public Editable getText() {
        Editable current = super.getText();
        if ((current != null) && mMesgText.equals(current.toString())) {
        	return Editable.Factory.getInstance().newEditable("");
        }
        return current;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect)
    {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        userOverride();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
//        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            userOverride();
//        }
        return super.onKeyDown(keyCode, keyEvent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean superResponse = super.onTouchEvent(event);
        String actualText = super.getText().toString();
        if (mMesgText.equals(actualText)) {
            super.setText("");
        }
        return superResponse;
    }

    private void userOverride() {
        String actualText = super.getText().toString();
        if (!hasFocus() && actualText.equals("")) {
            setText(mMesgText);
        }
        if (hasFocus() && actualText.equals(mMesgText)) {
            setText("");
        }
    }

    public boolean searchEmpty() {
        CharSequence actualText = super.getText();

        return ((actualText == null) ||
                mMesgText.equals(actualText.toString()) ||
                "".equals(actualText.toString()));
    }
}
