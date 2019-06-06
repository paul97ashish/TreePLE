package ca.mcgill.ecse321.project6.treeple_android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;

/*
Note that only resident users can be created here.
Municipal users are supposed to be created using the
TreePLE-Admin CLI app.
 */

public class SignUpFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = getActivity().getBaseContext();
        final String successMessage = "Signed Up";
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_signup, null))
                .setPositiveButton(R.string.action_signup, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        TextView tv = getDialog().findViewById(R.id.username);
                        String username = tv.getText().toString();
                        tv = getDialog().findViewById(R.id.signup_password);
                        String password = tv.getText().toString();
                        tv = getDialog().findViewById(R.id.signup_password_confirm);
                        String passwordConfirm = tv.getText().toString();
                        // assuring passwords match /must/ occur in the frontend
                        if(!password.equals(passwordConfirm)) {
                            Toast.makeText(context, "Passwords did not match!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        RequestParams rp = new RequestParams();
                        rp.add("username", username);
                        rp.add("password", password);

                        HttpUtils.post("signup", rp, new SessionResponse(context, successMessage));
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SignUpFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
