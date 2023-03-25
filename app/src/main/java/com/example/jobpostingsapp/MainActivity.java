package com.example.jobpostingsapp;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.StringReader;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private JobPostingAdapter adapter;
    private List<JobPosting> jobPostings;

    private boolean isLoading = false;

    private static final int PAGE_SIZE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);

        // Authenticate and fetch job postings
        new FetchJobPostingsTask().execute();

        ImageView bookmarkIcon = findViewById(R.id.bookmark_icon);
        bookmarkIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle bookmark icon click event
                // Start the bookmarked_jobs activity with a slide-in animation
                Intent intent = new Intent(MainActivity.this, BookmarkActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, 0);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0 && totalItemCount >= PAGE_SIZE) {
                    // Load the next page of job postings
                    new FetchJobPostingsTask().execute(totalItemCount);
                }
            }
        });
    }

        private class FetchJobPostingsTask extends AsyncTask<Integer, Void, List<JobPosting>> {

            @Override
            protected List<JobPosting> doInBackground(Integer... params) {
                int offset = params.length > 0 ? params[0] : 0;

                OkHttpClient client = new OkHttpClient();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\n    \"collection\":\"job_postings\",\n    \"database\":\"job_postings_db\",\n    \"dataSource\":\"jobpostings\",\n    \"projection\": {},\n    \"limit\": " + PAGE_SIZE + ",\n    \"skip\": " + offset + ",\n    \"sort\": {\"_id\": -1}\n}");
                Request request = new Request.Builder()
                        .url("https://ap-south-1.aws.data.mongodb-api.com/app/data-ecckk/endpoint/data/v1/action/find")
                        .post(body)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Access-Control-Request-Headers", "*")
                        .addHeader("api-key", "mGZm04E093ACvYohPblxuz3tWbr8EKTXgYbkecBfX5LKk7Z7gGyMypt5a6KgBX1f")
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    String jsonResponse = response.body().string();

                    List<JobPosting> jobPostings = new ArrayList<>();

                    try {
                        JsonReader reader = new JsonReader(new StringReader(jsonResponse));
                        reader.beginObject();

                        while (reader.hasNext()) {
                            String name = reader.nextName();

                            if (name.equals("documents")) {
                                reader.beginArray();

                                int count = 0;

                                while (reader.hasNext()) {
                                    reader.beginObject();

                                    String title = null, office = null, date = null, deadline = null, detailsUrl = null;

                                    while (reader.hasNext()) {
                                        String fieldName = reader.nextName();

                                        if (fieldName.equals("title")) {
                                            title = reader.nextString();
                                        } else if (fieldName.equals("office")) {
                                            office = reader.nextString();
                                        } else if (fieldName.equals("date")) {
                                            date = reader.nextString();
                                        } else if (fieldName.equals("deadline")) {
                                            deadline = reader.nextString();
                                        } else if (fieldName.equals("detailsUrl")) {
                                            detailsUrl = reader.nextString();
                                        } else {
                                            reader.skipValue();
                                        }
                                    }

                                    reader.endObject();

                                    jobPostings.add(new JobPosting(title, office, date, deadline, detailsUrl));

                                    count++;
                                }

                                reader.endArray();
                            } else {
                                reader.skipValue();
                            }
                        }

                        reader.endObject();
                        reader.close();
                    } catch (IOException e) {
                        // handle the IOException here
                    }

                    return jobPostings;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<JobPosting> jobPostings) {
                isLoading = false;

                if (jobPostings != null && !jobPostings.isEmpty()) {
                    if (MainActivity.this.jobPostings == null) {
                        MainActivity.this.jobPostings = jobPostings;
                        adapter = new JobPostingAdapter(MainActivity.this, jobPostings);
                        recyclerView.setAdapter(adapter);
                    } else {
                        int positionStart = MainActivity.this.jobPostings.size();
                        MainActivity.this.jobPostings.addAll(jobPostings);
                        adapter.notifyItemRangeInserted(positionStart, jobPostings.size());
                    }
                } else {
                    // Handle error case when jobPostings is null or empty, e.g., show an error message or a retry button
                }
            }
        }
    }