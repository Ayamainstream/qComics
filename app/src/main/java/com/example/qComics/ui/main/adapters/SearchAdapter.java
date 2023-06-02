package com.example.qComics.ui.main.adapters;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qComics.data.network.comics.Comics;
import com.example.qComics.ui.base.BaseActivity;
import com.example.qComics.ui.main.comics.ComicItemFragment;
import com.example.q_comics.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    ArrayList<Comics> dataModel;
    PopupWindow popupWindow;
    Context context;

    public SearchAdapter(Context context, PopupWindow popupWindow) {
        this.context = context;
        this.popupWindow = popupWindow;
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_search_card, parent, false);
        return new SearchAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.bind(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                popupWindow.dismiss();
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
        @BindView(R.id.type_of_comics)
        TextView tvTypeOfComics;
        @BindView(R.id.cover)
        ImageView cover;
        @BindView(R.id.genre_of_comics)
        TextView tvGenreOfComics;
        @BindView(R.id.name_of_comics)
        TextView tvNameOfComics;
        @BindView(R.id.rating)
        TextView tvRating;

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
            tvTypeOfComics.setText(String.valueOf(data.getType()));
            tvGenreOfComics.setText(data.getGenres().get(0));
        }
    }

    private void hideKeyboard() {
        View view = ((Activity) context).getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
