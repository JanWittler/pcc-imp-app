package de.pcc.privacycrashcam.applicationlogic;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.pcc.privacycrashcam.R;

/**
 * Shows the legal information, licenses and links to homepage and privacy policy.
 *
 * @author Giorgio Groß
 */
public class LegalFragment extends Fragment {

    /* #############################################################################################
     *                                  attributes
     * ###########################################################################################*/

    /* #############################################################################################
     *                                  methods
     * ###########################################################################################*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // get the main layout describing the content
        LinearLayout base = (LinearLayout) inflater.inflate(R.layout.content_legal, container, false);

        TextView legal = (TextView) base.findViewById(R.id.legal);
        legal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLicenses();
            }
        });

        // init view components
        return base;
    }

    public void showLicenses () {
        new HTMLDialogViewer(getContext(), getLayoutInflater(null),
                getString(R.string.legal_title), R.raw.legal).showDialog();
    }
}
