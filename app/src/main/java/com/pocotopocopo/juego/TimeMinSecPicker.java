package com.pocotopocopo.juego;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import android.widget.NumberPicker.Formatter;

/**
 * Created by Ale on 2/9/2015.
 */
public class TimeMinSecPicker extends FrameLayout {
    private NumberPicker minutesPicker;
    private NumberPicker secondsPicker;
    private int currentMinutes=1;
    private int currentSeconds=30;
    private OnTimeChangedListener mOnTimeChangedListener;

    private static final OnTimeChangedListener NO_OP_CHANGE_LISTENER = new OnTimeChangedListener() {
        public void onTimeChanged(TimeMinSecPicker view, int minute, int seconds) {
        }
    };

    public static final NumberPicker.Formatter TWO_DIGIT_FORMATTER =
            new Formatter() {

                @Override
                public String format(int value) {
                    // TODO Auto-generated method stub
                    return String.format("%02d", value);
                }
            };

    public TimeMinSecPicker(Context context) {
        super(context);
    }

    public TimeMinSecPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeMinSecPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.select_time_widget_layout,this,true);
        minutesPicker = (NumberPicker)findViewById(R.id.minutesPicker);
        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(10);
        minutesPicker.setFormatter(TWO_DIGIT_FORMATTER);
        minutesPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                currentMinutes = newVal;
                onTimeChanged();
            }
        });
        secondsPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                currentSeconds = newVal;
                onTimeChanged();
            }
        });
    }

    public interface OnTimeChangedListener{
        void onTimeChanged(TimeMinSecPicker view, int minutes, int seconds);
    }

    public void setOnTimeChangedListener(OnTimeChangedListener onTimeChangedListener) {
        mOnTimeChangedListener = onTimeChangedListener;
    }

    private void onTimeChanged() {
        mOnTimeChangedListener.onTimeChanged(this, currentMinutes, currentSeconds);
    }

    private void updateMinutesDisplay() {
        minutesPicker.setValue(currentMinutes);
        mOnTimeChangedListener.onTimeChanged(this, currentMinutes,currentSeconds);
    }
    private void updateSecondsDisplay() {
        secondsPicker.setValue(currentSeconds);
        mOnTimeChangedListener.onTimeChanged(this, currentMinutes,currentSeconds);
    }

    public int getCurrentMinutes() {
        return currentMinutes;
    }

    public void setCurrentMinutes(int currentMinutes) {
        this.currentMinutes = currentMinutes;
        updateMinutesDisplay();
    }

    public int getCurrentSeconds() {
        return currentSeconds;
    }

    public void setCurrentSeconds(int currentSeconds) {
        this.currentSeconds = currentSeconds;
        updateSecondsDisplay();
    }



    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        minutesPicker.setEnabled(enabled);
        secondsPicker.setEnabled(enabled);
    }

    private static class SavedState extends BaseSavedState {

        private final int seconds;
        private final int minutes;

        private SavedState(Parcelable superState, int minutes, int seconds) {
            super(superState);
            this.seconds = seconds;
            this.minutes = minutes;
        }

        private SavedState(Parcel in) {
            super(in);
            minutes = in.readInt();
            seconds = in.readInt();
        }

        public int getSeconds() {
            return seconds;
        }

        public int getMinutes() {
            return minutes;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(minutes);
            dest.writeInt(seconds);
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, currentMinutes, currentSeconds);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setCurrentMinutes(ss.getMinutes());
        setCurrentSeconds(ss.getSeconds());
    }
}
