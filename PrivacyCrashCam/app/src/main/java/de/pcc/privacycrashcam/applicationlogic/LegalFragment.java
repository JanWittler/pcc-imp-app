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
 * @author David Laubenstein
 * Created by David Laubenstein on 1/27/17.
 */
public class LegalFragment extends Fragment {

    /* #############################################################################################
     *                                  attributes
     * ###########################################################################################*/

    private LinearLayout base;

    private TextView legal;
    private TextView privacy;
    private TextView licences;
    private TextView website;
    private AlertDialog.Builder dialogBuilder;
    private WebView content;
    private ProgressBar loading;

    /* #############################################################################################
     *                                  methods
     * ###########################################################################################*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // get the main layout describing the content
        base = (LinearLayout) inflater.inflate(R.layout.content_legal, container, false);

        legal = (TextView) base.findViewById(R.id.legal);
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
