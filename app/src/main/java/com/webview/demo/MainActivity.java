import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private WebView remoteUi;
    private String base;
    private FloatingActionButton forwardButton;
    private FloatingActionButton backButton;
    private FloatingActionButton reloadButton;

    private static final int File_Chooser = 1;
    private ValueCallback<Uri[]> uploads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        base = "https://m.facebook.com/";
        remoteUi = findViewById(R.id.main_web_view);
        remoteUi.setWebViewClient(new RemoteClient(base));
        WebSettings uiSettings = remoteUi.getSettings();
        uiSettings.setJavaScriptEnabled(true);
        uiSettings.setAllowFileAccess(true);

        remoteUi.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                uploads = filePathCallback;
                Intent intent = fileChooserParams.createIntent();
                startActivityForResult(intent, File_Chooser);
                return true;
            }
        });
        remoteUi.loadUrl(base);

        forwardButton = findViewById(R.id.forwardButton);
        backButton = findViewById(R.id.backButton);
        reloadButton = findViewById(R.id.reloadButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (remoteUi.canGoBack()) {
                    remoteUi.goBack();
                }
            }
        });

        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (remoteUi.canGoForward()) {
                    remoteUi.goForward();
                }
            }
        });

        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remoteUi.reload();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == File_Chooser) {
            if (uploads != null) {
                uploads.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                uploads = null;
            }
        }
    }
}
