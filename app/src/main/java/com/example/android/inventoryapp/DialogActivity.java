package com.example.android.inventoryapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import static android.os.Build.VERSION_CODES.M;

/**
 * Activity to show dialog with radio buttons
 */

public class DialogActivity extends AppCompatActivity{
    // variable to hold the context of this activity
    private Context mContext = DialogActivity.this;
    // key for radio buttons checked
    public static final String EXTRA_STRING = "RadioButton Checked";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_activity);

        // prevent dialog to be dismissed if user clicks outside of it
        this.setFinishOnTouchOutside(false);

        // receive the intent that started this activity
        final Intent intent = getIntent();

        // find buttons
        final RadioButton first = (RadioButton) findViewById(R.id.radio_name_asc);
        final RadioButton second = (RadioButton) findViewById(R.id.radio_name_desc);
        final RadioButton third = (RadioButton) findViewById(R.id.radio_price_asc);
        final RadioButton fourth = (RadioButton) findViewById(R.id.radio_price_desc);
        final RadioButton fifth = (RadioButton) findViewById(R.id.radio_quantity_asc);
        final RadioButton sixth = (RadioButton) findViewById(R.id.radio_quantity_desc);
        Button cancelButton = (Button) findViewById(R.id.dialog_cancel_button);
        Button okButton = (Button) findViewById(R.id.dialog_ok_button);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // just exit the activity
                finish();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (first.isChecked()){
                    intent.putExtra(EXTRA_STRING, 0);
                } else if (second.isChecked()){
                    intent.putExtra(EXTRA_STRING, 1);
                } else if (third.isChecked()){
                    intent.putExtra(EXTRA_STRING, 2);
                } else if (fourth.isChecked()){
                    intent.putExtra(EXTRA_STRING, 3);
                } else if (fifth.isChecked()){
                    intent.putExtra(EXTRA_STRING, 4);
                } else if (sixth.isChecked()) {
                    intent.putExtra(EXTRA_STRING, 5);
                } else {
                    intent.putExtra(EXTRA_STRING, -1);
                }
                // return the same intent with extra data
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
