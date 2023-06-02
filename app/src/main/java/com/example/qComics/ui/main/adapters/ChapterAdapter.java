package com.example.qComics.ui.main.adapters;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qComics.data.network.comics.Chapter;
import com.example.qComics.ui.base.BaseActivity;
import com.example.qComics.ui.main.comics.ReadingFragment;
import com.example.q_comics.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ViewHolder>{

    ArrayList<Chapter> dataModel;
    Context context;

    public ChapterAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ChapterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_chapter, parent, false);
        return new ChapterAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.bind(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context.getSharedPreferences("DeviceToken", MODE_PRIVATE).edit();
                editor.putString("chapterName", dataModel.get(position).getName()); // or add toString() after if needed
                editor.apply();
                ((BaseActivity) context).addFragment(new ReadingFragment());
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataModel == null ? 0 : dataModel.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(ArrayList<Chapter> dataModel) {
        this.dataModel = dataModel;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name_of_chapters)
        TextView tvNameOfChapters;
        @BindView(R.id.like)
        ImageView like;
        @BindView(R.id.like_btn)
        LinearLayout likeBtn;
        @BindView(R.id.date_of_chapter)
        TextView date;
        @BindView(R.id.number_of_likes)
        TextView tvNumberOfLikes;
        @BindView(R.id.number_of_chapters)
        TextView tvNumberOfChapters;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
        public void bind(int position) {
            Chapter data = dataModel.get(position);
            tvNameOfChapters.setText(data.getName());
            tvNumberOfChapters.setText("#"+String.valueOf(position+1));
        }
    }

}
