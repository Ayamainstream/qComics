package com.example.qComics.ui.main.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qComics.data.network.comics.Images;
import com.example.q_comics.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {

    ArrayList<Images> dataModel;
    Context context;

    public ImagesAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ImagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_image_item, parent, false);
        return new ImagesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagesAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return dataModel == null ? 0 : dataModel.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(ArrayList<Images> dataModel) {
        this.dataModel = dataModel;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image_item)
        ImageView imageItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
        public void bind(int position) {
            Images data = dataModel.get(position);
            byte[] imageBytes = Base64.decode(data.getBase64(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            imageItem.setImageBitmap(bitmap);
        }
    }
}
