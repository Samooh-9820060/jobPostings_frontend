package com.example.jobpostingsapp;

import android.provider.BaseColumns;

public final class JobPostingContract {
    private JobPostingContract() {}

    public static class JobPostingEntry implements BaseColumns {
        public static final String TABLE_NAME = "job_postings";
        public static final String COLUMN_NAME_JOB_NAME = "job_name";
        public static final String COLUMN_NAME_OFFICE_NAME = "office_name";
        public static final String COLUMN_NAME_DATE_POSTED = "date_posted";
        public static final String COLUMN_NAME_DEADLINE = "deadline";
        public static final String COLUMN_NAME_LINK = "link";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_JOB_NAME + " TEXT," +
                        COLUMN_NAME_OFFICE_NAME + " TEXT," +
                        COLUMN_NAME_DATE_POSTED + " TEXT," +
                        COLUMN_NAME_DEADLINE + " TEXT," +
                        COLUMN_NAME_LINK + " TEXT)";

        public static final String SQL_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
