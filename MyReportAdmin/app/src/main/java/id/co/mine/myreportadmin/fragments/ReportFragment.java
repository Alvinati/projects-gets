package id.co.mine.myreportadmin.fragments;


import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import id.co.mine.myreportadmin.BuildConfig;
import id.co.mine.myreportadmin.R;
import id.co.mine.myreportadmin.main.MainActivity;
import id.co.mine.myreportadmin.table.ApelActivity;
import id.co.mine.myreportadmin.table.InvoiceActivity;
import id.co.mine.myreportadmin.table.TkoActivity;

import static android.content.Context.DOWNLOAD_SERVICE;


public class ReportFragment extends Fragment {

    TextView btnApel, btnTKO, btnInvoice;
    WebView wvGraph;
    String urlGraph;


    public ReportFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_report, container, false);

        btnApel = rootView.findViewById(R.id.main_btn_apel);
        btnTKO = rootView.findViewById(R.id.main_btn_tko);
        btnInvoice = rootView.findViewById(R.id.main_btn_invoice);

        btnApel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ApelActivity.class);
                startActivity(intent);
            }
        });

        btnTKO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TkoActivity.class);
                startActivity(intent);
            }
        });

        btnInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), InvoiceActivity.class);
                startActivity(intent);
            }
        });

        urlGraph = BuildConfig.BASE_URL + "/graph";


        wvGraph = rootView.findViewById(R.id.wv_graph);


        if( urlGraph != null) {
            wvGraph.clearCache(true);
            wvGraph.clearHistory();
            wvGraph.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            wvGraph.getSettings().setJavaScriptEnabled(true);
            wvGraph.getSettings().setBuiltInZoomControls(true);
            wvGraph.loadUrl(urlGraph);
            wvGraph.setWebViewClient(new CustomWebViewClient() {
                public void onReceivedError(WebView webView, int errorCode, String description, String failingUrl){
                    try{
                        webView.stopLoading();
                    } catch (Exception e){
                    }

                    if(webView.canGoBack()){
                        webView.goBack();
                    }

                    webView.loadUrl("about:blank");
                    super.onReceivedError(webView, errorCode, description, failingUrl);
                }
            });
            wvGraph.getSettings().setBuiltInZoomControls(false);

        }


        return rootView;
    }

    private class CustomWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().endsWith("")) {
                return false;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            view.getContext().startActivity(intent);
            return true;
        }
    }

}
