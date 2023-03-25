package com.example.jobpostingsapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class JobPostingAdapter extends RecyclerView.Adapter<JobPostingAdapter.JobPostingViewHolder> {

    private List<JobPosting> jobPostings;
    private Context context;

    public JobPostingAdapter(Context context, List<JobPosting> jobPostings) {
        this.context = context;
        this.jobPostings = jobPostings;
    }

    @NonNull
    @Override
    public JobPostingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.job_list_item, parent, false);
        return new JobPostingViewHolder(view);
    }

    public static boolean detectLanguage(String text) {
        // Check if the text contains only English characters

        return text.matches(".*[\\u0780-\\u07B1]+.*");
    }

    @Override
    public void onBindViewHolder(@NonNull JobPostingViewHolder holder, int position) {
        JobPosting jobPosting = jobPostings.get(position);
        holder.jobName.setText(jobPosting.getJobName());
        holder.officeName.setText(jobPosting.getOfficeName());
        holder.datePosted.setText(jobPosting.getDatePosted());
        holder.deadline.setText(jobPosting.getDeadline());
        holder.itemView.setTag(holder);
        holder.itemView.setOnClickListener(new JobPostingClickListener(context, jobPostings));

        // set click listener to open the link
        String link = jobPosting.getLink();
        holder.jobName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(jobPosting.getLink()));
                context.startActivity(intent);
            }
        });

        if (detectLanguage(jobPosting.getJobName())) {
            Typeface dhivehiFont = getDhivehiTypeface(context);

            // set layout params for job name
            ConstraintLayout.LayoutParams layoutParamsJobName = (ConstraintLayout.LayoutParams) holder.jobName.getLayoutParams();
            layoutParamsJobName.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            holder.jobName.setLayoutParams(layoutParamsJobName);
            holder.jobName.setTypeface(dhivehiFont);

            // set layout params for office name
            ConstraintLayout.LayoutParams layoutParamsOfficeName = (ConstraintLayout.LayoutParams) holder.officeName.getLayoutParams();
            layoutParamsOfficeName.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            holder.officeName.setLayoutParams(layoutParamsOfficeName);
            holder.officeName.setTypeface(dhivehiFont);

            // set layout params for posted date
            ConstraintLayout.LayoutParams layoutParamsDate = (ConstraintLayout.LayoutParams) holder.datePosted.getLayoutParams();
            layoutParamsDate.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            holder.datePosted.setLayoutParams(layoutParamsDate);
            holder.datePosted.setTypeface(dhivehiFont);

            // set layout params for due date
            ConstraintLayout.LayoutParams layoutParamsDeadline = (ConstraintLayout.LayoutParams) holder.deadline.getLayoutParams();
            layoutParamsDeadline.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            holder.deadline.setLayoutParams(layoutParamsDeadline);
            holder.deadline.setTypeface(dhivehiFont);

            // set layout params for image
            ConstraintLayout.LayoutParams layoutParamsImage = (ConstraintLayout.LayoutParams) holder.image.getLayoutParams();
            layoutParamsImage.endToEnd = ConstraintLayout.LayoutParams.UNSET;
            holder.image.setLayoutParams(layoutParamsImage);
        } else {
            // reset layout params for job name
            ConstraintLayout.LayoutParams layoutParamsJobName = (ConstraintLayout.LayoutParams) holder.jobName.getLayoutParams();
            layoutParamsJobName.endToEnd = ConstraintLayout.LayoutParams.UNSET;
            holder.jobName.setLayoutParams(layoutParamsJobName);

            // reset layout params for office name
            ConstraintLayout.LayoutParams layoutParamsOfficeName = (ConstraintLayout.LayoutParams) holder.officeName.getLayoutParams();
            layoutParamsOfficeName.endToEnd = ConstraintLayout.LayoutParams.UNSET;
            holder.officeName.setLayoutParams(layoutParamsOfficeName);

            // reset layout params for posted date
            ConstraintLayout.LayoutParams layoutParamsDate = (ConstraintLayout.LayoutParams) holder.datePosted.getLayoutParams();
            layoutParamsDate.endToEnd = ConstraintLayout.LayoutParams.UNSET;
            holder.datePosted.setLayoutParams(layoutParamsDate);

            // reset layout params for due date
            ConstraintLayout.LayoutParams layoutParamsDeadline = (ConstraintLayout.LayoutParams) holder.deadline.getLayoutParams();
            layoutParamsDeadline.endToEnd = ConstraintLayout.LayoutParams.UNSET;
            holder.deadline.setLayoutParams(layoutParamsDeadline);

            // reset layout params for image
            ConstraintLayout.LayoutParams layoutParamsImage = (ConstraintLayout.LayoutParams) holder.image.getLayoutParams();
            layoutParamsImage.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            holder.image.setLayoutParams(layoutParamsImage);
        }
    }

    private static Typeface getDhivehiTypeface(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/Faruma.ttf");
    }

    @Override
    public int getItemCount() {
        return jobPostings.size();
    }

    public class JobPostingClickListener implements View.OnClickListener {
        private Context context;
        private List<JobPosting> jobPostings;

        public JobPostingClickListener(Context context, List<JobPosting> jobPostings) {
            this.context = context;
            this.jobPostings = jobPostings;
        }

        @Override
        public void onClick(View view) {
            int position = ((RecyclerView.ViewHolder) view.getTag()).getAdapterPosition();
            JobPosting jobPosting = jobPostings.get(position);

            Animation anim = AnimationUtils.loadAnimation(context, R.anim.scale_fade_in);
            view.startAnimation(anim);

            // Open the link in a WebView
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra("url", jobPosting.getLink());
            intent.putExtra("job_name", jobPosting.getJobName());
            intent.putExtra("office_name", jobPosting.getOfficeName());
            intent.putExtra("date_posted", jobPosting.getDatePosted());
            intent.putExtra("deadline", jobPosting.getDeadline());
            context.startActivity(intent);
        }
    }


    public static class JobPostingViewHolder extends RecyclerView.ViewHolder{

        TextView jobName, officeName, datePosted, deadline, link;
        ImageView image;

        ConstraintLayout cardView;

        public JobPostingViewHolder(@NonNull View itemView) {
            super(itemView);
            jobName = itemView.findViewById(R.id.job_name);
            officeName = itemView.findViewById(R.id.office_name);
            datePosted = itemView.findViewById(R.id.date_posted);
            deadline = itemView.findViewById(R.id.deadline);
            image = itemView.findViewById(R.id.icon);
            cardView = itemView.findViewById(R.id.posting_item);
            //cardView.setOnClickListener(this);
        }
    }

    public void setJobPostings(List<JobPosting> jobPostings) {
        this.jobPostings = jobPostings;
        notifyDataSetChanged();
    }
}