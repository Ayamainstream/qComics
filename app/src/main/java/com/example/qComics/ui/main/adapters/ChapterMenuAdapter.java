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
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qComics.data.network.comics.Chapter;
import com.example.qComics.ui.base.BaseActivity;
import com.example.qComics.ui.main.comics.ReadingFragment;
import com.example.q_comics.R;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChapterMenuAdapter extends RecyclerView.Adapter<ChapterMenuAdapter.ViewHolder>{

    ArrayList<Chapter> dataModel;
    Context context;
    PopupWindow popupWindow;

    public ChapterMenuAdapter(Context context, PopupWindow popupWindow) {
        this.context = context;
        this.popupWindow = popupWindow;
    }

    @NonNull
    @Override
    public ChapterMenuAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_chapter_menu, parent, false);
        return new ChapterMenuAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterMenuAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.bind(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                SharedPreferences.Editor editor = context.getSharedPreferences("DeviceToken", MODE_PRIVATE).edit();
                editor.putString("chapterName", dataModel.get(position).getName()); // or add toString() after if needed
                editor.apply();
                ((BaseActivity) context).addFragment(new ReadingFragment());
            }
        });
    }

    public int getId(String chapterName){
        for (int i = 0; i < dataModel.size(); i++) {
            if (Objects.equals(dataModel.get(i).getName(), chapterName) || (i+1)!=dataModel.size())
                return i;
        }
        return 0;
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

