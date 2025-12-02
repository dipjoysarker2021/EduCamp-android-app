package com.example.educamp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
// androidx.swiperefreshlayout.widget.SwipeRefreshLayout is no longer needed

public class MainActivity extends AppCompatActivity {

    String websiteURL = "https://vercityproject.drmcoders.com/"; // sets web url

    private WebView webview;
    // SwipeRefreshLayout mySwipeRefreshLayout; // REMOVED: No longer needed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- 1. SETUP BACK PRESS LOGIC EARLY ---
        setupBackPressedCallback();
        // ----------------------------------------

        if( ! CheckNetwork.isInternetAvailable(this)) //returns true if internet available
        {
            //if there is no internet do this
            setContentView(R.layout.activity_main);

            new AlertDialog.Builder(this) //alert the person knowing they are about to close
                    .setTitle("No internet connection available")
                    .setMessage("Please Check you're Mobile data or Wifi network.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
        }
        else
        {
            //Webview stuff
            webview = findViewById(R.id.webView);
            webview.getSettings().setJavaScriptEnabled(true);
            webview.getSettings().setDomStorageEnabled(true);
            webview.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
            webview.loadUrl(websiteURL);
            webview.setWebViewClient(new WebViewClientDemo());
        }

        // REMOVED: Swipe to refresh functionality and listener are removed
        /*
        //Swipe to refresh functionality
        mySwipeRefreshLayout = (SwipeRefreshLayout)this.findViewById(R.id.swipeContainer);

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if (webview != null) {
                            webview.reload();
                        }
                    }
                }
        );
        */
    }


    private class WebViewClientDemo extends WebViewClient {
        @Override
        //Keep webview in app when clicking links
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            // mySwipeRefreshLayout.setRefreshing(false); // REMOVED: No longer need to stop refreshing
        }
    }

    /**
     * Sets up the custom logic for handling the system back button press.
     * This logic prioritizes WebView back navigation over application exit.
     */
    private void setupBackPressedCallback() {
        // Create an OnBackPressedCallback that is enabled by default (true)
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {

                // 2. BACK PRESS LOGIC: Check if the WebView can navigate back
                if (webview != null && webview.canGoBack()) {
                    webview.goBack(); // Navigate back in the WebView history
                } else {
                    // If not, show the exit confirmation dialog
                    showExitConfirmationDialog();
                }
            }
        };
        // Add the callback to the dispatcher, binding it to this Activity's lifecycle
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    /**
     * Displays the AlertDialog to confirm application exit.
     */
    private void showExitConfirmationDialog() {
        new AlertDialog.Builder(MainActivity.this) // Use MainActivity.this for context
                .setTitle("EXIT")
                .setMessage("Are you sure you want to close this app?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Close the Activity
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }


}

class CheckNetwork {
    private static final String TAG = CheckNetwork.class.getSimpleName();

    public static boolean isInternetAvailable(Context context)
    {
        NetworkInfo info = (NetworkInfo) ((ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if (info == null)
        {
            Log.d(TAG,"no internet connection");
            return false;
        }
        else
        {
            if(info.isConnected())
            {
                Log.d(TAG," internet connection available...");
                return true;
            }
            else
            {
                // Note: The logic here is generally considered fine for basic checks,
                // but a disconnected network info usually means no internet.
                // The current logic handles this specific case differently.
                Log.d(TAG," internet connection");
                return true;
            }

        }
    }
}