package com.example.androidMapApp;

import android.app.Activity;
import android.app.Dialog;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.googlemapapi.R;

public class CostumPopup extends Dialog {
    private String title, positiveButtonText, negativeButtonText, usernameHint, descritpionHint ;
    private TextView titleView;
    private EditText usernameEditText, descriptionEditText;
    private Button cancelButton, saveButton;

    public CostumPopup(Activity activity){
        super(activity, R.style.Theme_AppCompat_DayNight_Dialog);
        setContentView(R.layout.insert_location_popup_template);

        titleView = findViewById(R.id.title);
        usernameEditText = findViewById(R.id.username);
        descriptionEditText = findViewById(R.id.location_description);
        cancelButton = findViewById(R.id.cancel_btn);
        saveButton = findViewById(R.id.save_btn);
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescritpionHint(String descritpionHint) {
        this.descritpionHint = descritpionHint;
    }

    public void setUsernameHint(String usernameHint) {
        this.usernameHint = usernameHint;
    }

    public void setPositiveButtonText(String positiveButtonText) {
        this.positiveButtonText = positiveButtonText;
    }

    public void setNegativeButtonText(String negativeButtonText) {
        this.negativeButtonText = negativeButtonText;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public EditText getUsernameEditText() {
        return usernameEditText;
    }

    public EditText getDescriptionEditText() {
        return descriptionEditText;
    }

    public void build(){
        titleView.setText(title);

        usernameEditText.setHint(usernameHint);
        descriptionEditText.setHint(descritpionHint);

        cancelButton.setText(negativeButtonText);
        saveButton.setText(positiveButtonText);

        show();
    }
}
