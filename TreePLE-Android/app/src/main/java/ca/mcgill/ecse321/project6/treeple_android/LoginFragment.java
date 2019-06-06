package ca.mcgill.ecse321.project6.treeple_android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;

import com.loopj.android.http.RequestParams;

public class LoginFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final Context context = getActivity().getBaseContext();
        final String successMessage = "Logged In";
        builder.setView(inflater.inflate(R.layout.dialog_login, null))
                .setPositiveButton(R.string.action_login, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String username = ((EditText) getDialog().findViewById(R.id.username_entry)).getText().toString();
                        String password = ((EditText) getDialog().findViewById(R.id.login_password)).getText().toString();
                        RequestParams rp = new RequestParams();
                        rp.add("username", username);
                        rp.add("password", password);
                        HttpUtils.post("login", rp, new SessionResponse(context, successMessage));
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LoginFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
