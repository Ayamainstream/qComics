package com.example.qComics.ui.main.adapters;

import static android.content.Context.MODE_PRIVATE;

import static com.example.qComics.data.utils.Utils.getString;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Gravity;
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

public class TopComicsAdapter extends RecyclerView.Adapter<TopComicsAdapter.ViewHolder>{

    ArrayList<Comics> dataModel;
    Context context;

    public TopComicsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public TopComicsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_top_card, parent, false);
        return new TopComicsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopComicsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.place)
        TextView tvPlace;
        @BindView(R.id.cover)
        ImageView cover;
        @BindView(R.id.genre_of_comics)
        TextView tvGenreOfComics;
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

        @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
        public void bind(int position) {
            Comics data = dataModel.get(position);
            byte[] imageBytes = Base64.decode(data.getImageCoverBase64(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            cover.setImageBitmap(bitmap);
            tvNameOfComics.setText(data.getName());
            tvRating.setText(String.valueOf(data.getRating()));
            List<String> genres = data.getGenres();
            List<String> genreNames = new ArrayList<>();
            for (String genre : genres) {
                String genreName = getStringFromGenreName(genre);
                genreNames.add(genreName);
            }
            String commaSeparatedGenres = TextUtils.join(", ", genreNames);
            tvGenreOfComics.setText(commaSeparatedGenres);
            tvPlace.setText(String.valueOf(position + 1));
            if (data.getVotes() == null)
                tvVotes.setText("(0)");
            else
                tvVotes.setText("("+String.valueOf(data.getVotes())+")");
            if (position == 0){
                tvPlace.setGravity(Gravity.CENTER);
                tvPlace.setBackground(context.getDrawable(R.drawable.bg_baseline_star_24));
                tvPlace.setTextColor(Color.WHITE);
            }
        }
    }

    private String getStringFromGenreName(String genreName) {
        switch (genreName) {
            case "CHILDREN":
                return getString(R.string.genre_name_children, context);
            case "FANTASTIC":
                return getString(R.string.genre_name_fantastic, context);
            case "HISTORICAL":
                return getString(R.string.genre_name_historical, context);
            case "ROMANTIC":
                return getString(R.string.genre_name_romantic, context);
            case "DRAMA":
                return getString(R.string.genre_name_drama, context);
            case "ADVENTURES":
                return getString(R.string.genre_name_adventures, context);
            case "ACTION":
                return getString(R.string.genre_name_action, context);
            case "DAILY":
                return getString(R.string.genre_name_daily, context);
            case "COMEDY":
                return getString(R.string.genre_name_comedy, context);
            default:
                return genreName;
        }
    }
}
