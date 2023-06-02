package com.example.qComics.ui.main.adapters;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qComics.data.network.comics.Comics;
import com.example.qComics.ui.base.BaseActivity;
import com.example.qComics.ui.main.comics.ComicItemFragment;
import com.example.q_comics.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainComicsAdapter extends RecyclerView.Adapter<MainComicsAdapter.ViewHolder> {

    ArrayList<Comics> dataModel, comicsListFiltered;
    Context context;

    public MainComicsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MainComicsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_main_card, parent, false);
        return new MainComicsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainComicsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.bind(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context.getSharedPreferences("DeviceToken", MODE_PRIVATE).edit();
                editor.putString("comicsName", dataModel.get(position).getName()); // or add toString() after if needed
                editor.apply();
                ((BaseActivity) context).addFragment(new ComicItemFragment());
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataModel == null ? 0 : dataModel.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(ArrayList<Comics> dataModel) {
        this.dataModel = dataModel;
        notifyDataSetChanged();
    }

    public ArrayList<Comics> getData() {
        return dataModel;
    }

    public ArrayList<Comics> getSearchResults(String query) {
        ArrayList<Comics> searchResults = new ArrayList<>();
        for (Comics comics : dataModel) {
            if (comics.getName().toLowerCase().contains(query.toLowerCase())) {
                searchResults.add(comics);
            }
        }
        return searchResults;
    }

    public void applyFilters(List<String> selectedGenres) {
        if (selectedGenres == null || selectedGenres.isEmpty()) {
            // If no filters are selected, show all the comics
            comicsListFiltered = new ArrayList<>(dataModel);
        } else {
            // Filter the comics based on the selected genres
            comicsListFiltered = new ArrayList<>();
            for (Comics comics : dataModel) {
                for (String genre : comics.getGenres()) {
                    if (selectedGenres.contains(genre)) {
                        comicsListFiltered.add(comics);
                        break;
                    }
                }
            }
        }
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cover)
        ImageView cover;
        @BindView(R.id.type_of_comics)
        TextView tvTypesOfComics;
        @BindView(R.id.name_of_comics)
        TextView tvNameOfComics;
        @BindView(R.id.rating)
        TextView tvRating;
        @BindView(R.id.votes)
        TextView tvVotes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @SuppressLint("SetTextI18n")
        public void bind(int position) {
            Comics data = dataModel.get(position);
            byte[] imageBytes = Base64.decode(data.getImageCoverBase64(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            cover.setImageBitmap(bitmap);
            tvNameOfComics.setText(data.getName());
            tvTypesOfComics.setText(data.getType());
            tvRating.setText(String.valueOf(data.getRating()));
            tvVotes.setText("("+String.valueOf(data.getVotes())+")");
        }
    }
}
