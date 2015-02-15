package com.pocotopocopo.juego;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

/**
 * Created by nico on 14/02/15.
 */
public class CountDownPickerDialog extends DialogFragment {
    public static final String MINUTES_KEY = "MINUTES";
    public static final String SECONDS_KEY= "SECONDS";
    public interface CountDownPickerListener {
        public void onTimeSelected(int minutes, int seconds);
    }

    private CountDownPickerListener listener;
    private NumberPicker minutesPicker;
    private NumberPicker secondsPicker;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (CountDownPickerListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement CountDownPickerListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.select_time_dialog_widget, null);

        Bundle bundle = getArguments();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view);
        minutesPicker = (NumberPicker)view.findViewById(R.id.minutesPicker);
        secondsPicker = (NumberPicker)view.findViewById(R.id.secondsPicker);

        if (bundle!=null){
            minutesPicker.setValue(bundle.getInt(MINUTES_KEY));
            secondsPicker.setValue(bundle.getInt(SECONDS_KEY));
        }

        builder.setMessage(R.string.time_picker_title);
        builder.setPositiveButton(R.string.ok_button_text, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int minutes, seconds;
                        minutes=minutesPicker.getValue();
                        seconds=secondsPicker.getValue();
                        if (minutes>0 && seconds>0){
                            listener.onTimeSelected(minutes,seconds);
                            dismiss();
                        }
                    }
                });
        builder.setNegativeButton(R.string.cancel_text, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dismiss();
            }
        });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
