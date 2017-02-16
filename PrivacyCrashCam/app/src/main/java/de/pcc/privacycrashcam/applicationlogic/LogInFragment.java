package de.pcc.privacycrashcam.applicationlogic;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import de.pcc.privacycrashcam.R;
import de.pcc.privacycrashcam.data.Account;
import de.pcc.privacycrashcam.data.memoryaccess.MemoryManager;
import de.pcc.privacycrashcam.data.serverconnection.AuthenticationState;
import de.pcc.privacycrashcam.data.serverconnection.ServerProxy;
import de.pcc.privacycrashcam.data.serverconnection.ServerResponseCallback;
import de.pcc.privacycrashcam.gui.CameraActivity;

/**
 * Shows the log in fragment.
 * @author Giorgio
 */

public class LogInFragment extends Fragment {
    private RelativeLayout base;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // get the main layout describing the content
        base = (RelativeLayout) inflater.inflate(R.layout.content_login, container, false);

        // init view components
        final EditText et_mail = (EditText) base.findViewById(R.id.et_mail);
        final EditText et_password = (EditText) base.findViewById(R.id.et_password);
        final ProgressBar loginProgress = (ProgressBar) base.findViewById(R.id.pb_login);
        final Button login = (Button) base.findViewById(R.id.b_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeVisibility(login, loginProgress);
                final Account account = new Account(et_mail.getText().toString(),
                        et_password.getText().toString());
                new ServerProxy(getContext()).authenticateUser(account, new ServerResponseCallback<AuthenticationState>() {
                    @Override
                    public void onResponse(AuthenticationState response) {
                        switch (response) {
                           case SUCCESS:
                               MemoryManager memoryManager = new MemoryManager(getContext());
                               memoryManager.saveAccountData(account);
                               CameraActivity.Launch(getActivity());
                               getActivity().finish();
                               break;
                           case FAILURE_MISMATCH:
                               Toast.makeText(getContext(), "Mail and password are not matching",
                                Toast.LENGTH_SHORT).show();
                               changeVisibility(login, loginProgress);
                               break;
                           case FAILURE_MISSING:
                               Toast.makeText(getContext(), "Account not existing",
                                Toast.LENGTH_SHORT).show();
                               changeVisibility(login, loginProgress);
                               break;
                           case FAILURE_OTHER:
                               Toast.makeText(getContext(), "Account error!",
                                Toast.LENGTH_SHORT).show();
                               changeVisibility(login, loginProgress);
                               break;
                           default:
                               Toast.makeText(getContext(), getString(R.string.error_no_connection),
                                Toast.LENGTH_SHORT).show();
                               changeVisibility(login, loginProgress);
                               break;
                       }
                    }

                    @Override
                    public void onProgress(int percent) {
                        // ignore this case
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(getContext(), getString(R.string.error_no_connection),
                                Toast.LENGTH_SHORT).show();
                        changeVisibility(login, loginProgress);
                    }
                });
            }
        });
        return base;
    }
    private void changeVisibility(Button  button, ProgressBar progressBar) {
        if(button.getVisibility() == View.VISIBLE) {
            button.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            button.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
