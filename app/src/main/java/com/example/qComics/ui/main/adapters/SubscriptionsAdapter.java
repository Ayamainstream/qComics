package com.example.qComics.ui.main.adapters;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qComics.data.network.ApiClient;
import com.example.qComics.data.network.auth.User;
import com.example.qComics.data.network.comics.Comics;
import com.example.qComics.ui.base.BaseActivity;
import com.example.qComics.ui.main.comics.ComicItemFragment;
import com.example.q_comics.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubscriptionsAdapter extends RecyclerView.Adapter<SubscriptionsAdapter.ViewHolder> {

    ArrayList<User> dataModel;
    Context context;

    public SubscriptionsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public SubscriptionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_subscribtions_card, parent, false);
        return new SubscriptionsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubscriptionsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.bind(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context.getSharedPreferences("DeviceToken", MODE_PRIVATE).edit();
                editor.putString("subscriberName", dataModel.get(position).getUsername()); // or add toString() after if needed
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
    public void setData(ArrayList<User> dataModel) {
        this.dataModel = dataModel;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.studio_cover)
        ImageView cover;
        @BindView(R.id.comics_number)
        TextView numberOfComics;
        @BindView(R.id.followers_number)
        TextView numberOfFollowers;
        @BindView(R.id.name_of_studio)
        TextView tvNameOfSubscription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
        public void bind(int position) {
            User data = dataModel.get(position);
            if (data.getAvatarBase64() != null) {
                byte[] imageBytes = Base64.decode(data.getAvatarBase64(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                cover.setImageBitmap(bitmap);
            }
            tvNameOfSubscription.setText(data.getUsername());
            Call<String> getSubscriberAmount = ApiClient.getUserService().getSubscriberAmount(data.getUsername());
            getSubscriberAmount.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()){
                        numberOfFollowers.setText(response.body());
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });
        }
    }
}
