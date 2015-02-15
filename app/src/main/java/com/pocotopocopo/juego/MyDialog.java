package com.pocotopocopo.juego;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

/**
 * Created by Ale on 2/10/2015.
 */
public class MyDialog extends Dialog implements Button.OnClickListener{
    Button okButton;
    NumberPicker minutesPicker;
    NumberPicker secondsPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_time_dialog_widget);
        //okButton = (Button) this.findViewById(R.id.okTimeDialogButton);
        minutesPicker = (NumberPicker) this.findViewById(R.id.minutesPicker);
        secondsPicker = (NumberPicker) this.findViewById(R.id.secondsPicker);
        secondsPicker.setMaxValue(59);
        secondsPicker.setMinValue(0);
        minutesPicker.setMaxValue(10);
        minutesPicker.setMinValue(0);


        okButton.setOnClickListener(this);

    }


    public MyDialog(Context context) {
        super(context);
    }

    public MyDialog(Context context, int theme) {
        super(context, theme);
    }

    public MyDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public interface OkDialogListener{
        void onOkClickListener(long time);
    }

    @Override
    public void onClick(View v) {
        OkDialogListener activity = (OkDialogListener) getOwnerActivity();
        activity.onOkClickListener((minutesPicker.getValue()*60+secondsPicker.getValue())*1000);
        this.dismiss();
    }
}
