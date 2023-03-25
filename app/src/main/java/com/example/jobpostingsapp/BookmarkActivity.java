package com.example.jobpostingsapp;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BookmarkActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private JobPostingAdapter jobPostingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookmarked_jobs);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        jobPostingAdapter = new JobPostingAdapter(this, getAllBookmarkedJobs());
        recyclerView.setAdapter(jobPostingAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload data in the RecyclerView or refresh the UI here
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        jobPostingAdapter = new JobPostingAdapter(this, getAllBookmarkedJobs());
        recyclerView.setAdapter(jobPostingAdapter);
    }

    private List<JobPosting> getAllBookmarkedJobs() {
        JobPostingDbHelper dbHelper = new JobPostingDbHelper(this);
        List<JobPosting> allJobPostings = dbHelper.getAllJobPostings();
        List<JobPosting> bookmarkedJobs = new ArrayList<>();
        for (JobPosting jobPosting : allJobPostings) {
            if (isJobPostingBookmarked(jobPosting.getLink())) {
                bookmarkedJobs.add(jobPosting);
            }
        }
        return bookmarkedJobs;
    }

    private boolean isJobPostingBookmarked(String link) {
        JobPostingDbHelper dbHelper = new JobPostingDbHelper(this);
        return dbHelper.isJobPostingBookmarked(link);
    }
}

