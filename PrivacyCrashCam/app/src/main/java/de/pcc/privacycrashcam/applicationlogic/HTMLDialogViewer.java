package de.pcc.privacycrashcam.applicationlogic;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.annotation.RawRes;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;

import java.io.InputStream;
import java.util.Scanner;

import de.pcc.privacycrashcam.R;

/**
 * @author Giorgio Gross
 */
public class HTMLDialogViewer {
    private Context context;
    private WebView wv_licenses;
    private ProgressBar pb_licenseLoader;
    private View layout;

    private String title;
    private @RawRes int rawRes;

    /**
     * Creates and shows the web view displaying the passed html file.
     *
     * @param context application context
     * @param inflater a layout inflater
     * @param title the title of the dialog
     * @param rawResource the resource id of the html content
     */
    public HTMLDialogViewer(Context context, LayoutInflater inflater, String title, @RawRes int rawResource){
        this.context = context;
        this.layout = inflater.inflate(R.layout.htmldialog, null);
        this.title = title;
        this.rawRes = rawResource;
    }

    /**
     * Loads the specified raw resources and shows it in a dialog
     */
    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        wv_licenses = (WebView) layout.findViewById(R.id.wv_licenses);
        pb_licenseLoader = (ProgressBar) layout.findViewById(R.id.pb_licenseLoader);

        new HTMLfileLoader().execute();

        builder.setTitle(title)
                .setView(layout)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                }).create().show();

    }

    private class HTMLfileLoader extends AsyncTask<Void, Void, Void> {
        StringBuilder s_builder;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            s_builder = new StringBuilder();

            InputStream rawResource = context.getResources().openRawResource(rawRes);
            if(rawResource!=null) {
                Scanner scan = new Scanner(rawResource);

                while (scan.hasNextLine()) {
                    s_builder.append(scan.nextLine());
                    s_builder.append("\n");
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(wv_licenses != null && s_builder != null && !isCancelled()) {
                wv_licenses.loadDataWithBaseURL(null, s_builder.toString(), "text/html", "utf-8", null);
                wv_licenses.getSettings().setUseWideViewPort(true);
                pb_licenseLoader.setVisibility(View.GONE);
            }
        }
    }
}

