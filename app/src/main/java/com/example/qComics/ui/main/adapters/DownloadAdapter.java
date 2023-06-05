package com.example.qComics.ui.main.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qComics.data.network.comics.Chapter;
import com.example.q_comics.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.ViewHolder> {

    private ArrayList<Chapter> dataModel;
    private Context context;
    private ArrayList<Integer> selectedIds;
    private ArrayList<String> checked = new ArrayList<>();
    private ArrayList<Boolean> selected;
    private LinearLayout bottomBar;

    public DownloadAdapter(Context context, LinearLayout bottomBar) {
        this.context = context;
        this.selectedIds = new ArrayList<>();
        this.bottomBar = bottomBar;
    }

    @NonNull
    @Override
    public DownloadAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_download_comics, parent, false);
        return new DownloadAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadAdapter.ViewHolder holder, int position) {
        holder.bind(position);
        Chapter chapter = dataModel.get(position);
        holder.checkBox.setChecked(selectedIds.contains(chapter.getId()));
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedIds.add(chapter.getId());
                } else {
                    selectedIds.remove(Integer.valueOf(chapter.getId()));
                }
                checkNumber();
            }
        });
    }

    private void checkNumber() {
        if (selectedIds.size() == 0)
            bottomBar.setBackgroundColor(ContextCompat.getColor(context, R.color.extra_light_gray));
        else
            bottomBar.setBackgroundColor(ContextCompat.getColor(context, R.color.orange_text));
    }

    private void chooseAll() {
        for (Chapter data : dataModel) {
            if (!selectedIds.contains(data.getId())) {
                selectedIds.add(data.getId());
            }
        }
        notifyDataSetChanged();
        checkNumber();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void check(DownloadAdapter.ViewHolder holder, int position){
        if (!selected.get(position)){
            addId(dataModel.get(position));
            selected.set(position, true);
        } else {
            removeId(dataModel.get(position));
            selected.set(position, false);
        }
        notifyDataSetChanged();
    }

    public void addId(Chapter movie) {
        checked.add(movie.getId().toString());
    }

    public void removeId(Chapter movie){
        checked.remove(movie.getId().toString());
    }

    @Override
    public int getItemCount() {
        return dataModel == null ? 0 : dataModel.size();
    }

    public void setData(ArrayList<Chapter> dataModel) {
        this.dataModel = dataModel;
        notifyDataSetChanged();
    }

    public ArrayList<Integer> getSelectedIds() {
        return selectedIds;
    }

    public ArrayList<Chapter> getDataModel() {
        return dataModel;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.checkbox)
        CheckBox checkBox;
        @BindView(R.id.name_of_chapters)
        TextView tvNameOfChapter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
        public void bind(int position) {
            Chapter data = dataModel.get(position);
            tvNameOfChapter.setText(data.getName());
        }
    }
}

