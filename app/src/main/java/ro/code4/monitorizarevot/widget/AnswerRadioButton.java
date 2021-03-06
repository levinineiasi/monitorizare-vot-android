package ro.code4.monitorizarevot.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import ro.code4.monitorizarevot.R;
import ro.code4.monitorizarevot.net.model.Answer;
import ro.code4.monitorizarevot.net.model.response.ResponseAnswer;

public class AnswerRadioButton extends RadioButton implements AnswerLayout {
    public AnswerRadioButton(Context context) {
        super(context);
        init(context);
    }

    public AnswerRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AnswerRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AnswerRadioButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        setLayoutParams(new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelSize(R.dimen.button_height)
        ));
        setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.question_option_text));
    }

    @Override
    public void setAnswer(Answer answer) {
        setTag(new ResponseAnswer(answer.getId()));
        setText(answer.getText());
    }

    @Override
    public void setDetail(String detail) {

    }

    @Override
    public ResponseAnswer getAnswer() {
        return (ResponseAnswer) getTag();
    }
}
