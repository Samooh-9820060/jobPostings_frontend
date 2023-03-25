package com.example.jobpostingsapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class JobPostingDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "job_postings.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "JobPostingDbHelper";


    public JobPostingDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the job_postings table using the SQL_CREATE_TABLE statement from JobPostingContract.JobPostingEntry
        db.execSQL(JobPostingContract.JobPostingEntry.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // If you need to upgrade the database schema in the future, you can implement the necessary changes here
        // For now, we're just dropping and recreating the table
        db.execSQL(JobPostingContract.JobPostingEntry.SQL_DELETE_TABLE);
        onCreate(db);
    }

    public boolean deleteJobPosting(String link) {
        SQLiteDatabase db = getWritableDatabase();
        String selection = JobPostingContract.JobPostingEntry.COLUMN_NAME_LINK + " = ?";
        String[] selectionArgs = new String[]{link};
        int rowsDeleted = db.delete(JobPostingContract.JobPostingEntry.TABLE_NAME, selection, selectionArgs);
        return rowsDeleted > 0;
    }



    public long insertJobPosting(JobPosting jobPosting) {
        // Insert a new job posting into the database
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("job_name", jobPosting.getJobName());
        values.put("office_name", jobPosting.getOfficeName());
        values.put("date_posted", jobPosting.getDatePosted());
        values.put("deadline", jobPosting.getDeadline());
        values.put("link", jobPosting.getLink());
        return db.insert("job_postings", null, values);
    }

    public boolean isJobPostingBookmarked(String link) {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                JobPostingContract.JobPostingEntry.COLUMN_NAME_LINK
        };

        String selection = JobPostingContract.JobPostingEntry.COLUMN_NAME_LINK + " = ?";
        String[] selectionArgs = link != null ? new String[] { link } : null;

        if (selection == null) {
            return false;
        }

        Cursor cursor = db.query(
                JobPostingContract.JobPostingEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        boolean isBookmarked = cursor.getCount() > 0;

        cursor.close();

        return isBookmarked;
    }

    public List<JobPosting> getAllJobPostings() {
        List<JobPosting> jobPostings = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM job_postings", null);
        try {
            while (cursor.moveToNext()) {
                String jobName = cursor.getString(cursor.getColumnIndexOrThrow("job_name"));
                String officeName = cursor.getString(cursor.getColumnIndexOrThrow("office_name"));
                String datePosted = cursor.getString(cursor.getColumnIndexOrThrow("date_posted"));
                String deadline = cursor.getString(cursor.getColumnIndexOrThrow("deadline"));
                String link = cursor.getString(cursor.getColumnIndexOrThrow("link"));
                JobPosting jobPosting = new JobPosting(jobName, officeName, datePosted, deadline, link);
                jobPostings.add(jobPosting);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error reading job postings from database", e);
        } finally {
            cursor.close();
        }
        return jobPostings;
    }

}

