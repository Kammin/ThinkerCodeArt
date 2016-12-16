package com.example.kamin.thinkercodeart.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.example.kamin.thinkercodeart.R;
import com.example.kamin.thinkercodeart.activity.MainActivity;

public class AlertDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle("Title!").setPositiveButton(R.string.ok, this)
                .setMessage(R.string.message_text);
        if(savedInstanceState!=null)
        Log.d("LOG","savedInstanceState +savedInstanceState.get(\"title\")");
        Log.d("LOG","savedInstanceState");
        return adb.create();
    }

    public static AlertDialogFragment newInstance(int num) {
        AlertDialogFragment f = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", "title");
        args.putString("messege", "messege");
        f.setArguments(args);
        return f;
    }

    public void onClick(DialogInterface dialog, int which) {

    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }
}
