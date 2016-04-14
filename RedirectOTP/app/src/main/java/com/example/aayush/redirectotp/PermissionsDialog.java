package com.example.aayush.redirectotp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by aayush on 13/4/16.
 */
public class PermissionsDialog extends DialogFragment{
    @Override
    public Dialog onCreateDialog(Bundle SavedInstanceState){
        //Create the dialog box object
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //setting properties of the dialog box
        builder.setMessage(R.string.permissions_failure_msg)
                .setPositiveButton(R.string.btn_text_OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create();
    }
}
