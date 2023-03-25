package com.example.jobpostingsapp;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;
    private static final String TAG = "WebViewActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        webView = findViewById(R.id.webview);

        String url = getIntent().getStringExtra("url");

        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);

        // Get a reference to the bookmark icon ImageView
        ImageView bookmarkIcon = findViewById(R.id.bookmark_icon);

        // Check if the job posting is already in the database
        JobPostingDbHelper dbHelper = new JobPostingDbHelper(this);
        String link = url;
        boolean isBookmarked = dbHelper.isJobPostingBookmarked(link);

        // Update the bookmark icon to use the appropriate drawable
        if (isBookmarked) {
            bookmarkIcon.setImageResource(R.drawable.ic_bookmark);
        } else {
            bookmarkIcon.setImageResource(R.drawable.ic_bookmark_open);
        }

        // Add an OnClickListener to the bookmark icon
        bookmarkIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new JobPosting object with the details of the currently open job posting
                String urlIntent = getIntent().getStringExtra("url");
                String jobNameIntent = getIntent().getStringExtra("job_name");
                String officeNameIntent = getIntent().getStringExtra("office_name");
                String datePostedIntent = getIntent().getStringExtra("date_posted");
                String deadlineIntent = getIntent().getStringExtra("deadline");

                String jobName = jobNameIntent;
                String officeName = officeNameIntent;
                String datePosted = datePostedIntent;
                String deadline = deadlineIntent;
                String link = urlIntent;
                JobPosting jobPosting = new JobPosting(jobName, officeName, datePosted, deadline, link);

                // Check if the job posting is already in the database
                JobPostingDbHelper dbHelper = new JobPostingDbHelper(WebViewActivity.this);
                boolean isBookmarked = dbHelper.isJobPostingBookmarked(link);

                if (isBookmarked) {
                    // If the job posting is already bookmarked, remove it from the database
                    boolean rowsDeleted = dbHelper.deleteJobPosting(link);
                    if (rowsDeleted) {
                        // Update the bookmark icon to use a different drawable
                        bookmarkIcon.setImageResource(R.drawable.ic_bookmark_open);
                        // Show a toast message to confirm that the job posting was unbookmarked
                        Toast.makeText(WebViewActivity.this, "Job posting unbookmarked", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(WebViewActivity.this, "Error removing job posting from bookmarks", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // If the job posting is not bookmarked, insert it into the database
                    long rowId = dbHelper.insertJobPosting(jobPosting);
                    Log.i(TAG, "Inserted job posting with ID: " + rowId);
                    // Update the bookmark icon to use a different drawable
                    bookmarkIcon.setImageResource(R.drawable.ic_bookmark);
                    // Show a toast message to confirm that the job posting was bookmarked
                    Toast.makeText(WebViewActivity.this, "Job posting bookmarked", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

