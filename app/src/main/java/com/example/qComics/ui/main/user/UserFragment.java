package com.example.qComics.ui.main.user;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.qComics.data.network.ApiClient;
import com.example.qComics.data.network.auth.User;
import com.example.qComics.data.network.comics.Comics;
import com.example.qComics.ui.auth.AuthenticationActivity;
import com.example.qComics.ui.base.BaseActivity;
import com.example.qComics.ui.main.adapters.FavoriteAdapter;
import com.example.qComics.ui.main.adapters.SubscriptionsAdapter;
import com.example.q_comics.R;
import com.example.q_comics.databinding.FragmentProfileBinding;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFragment extends Fragment {

    private FragmentProfileBinding binding;
    CircleImageView userIcon;
    TextView tvUsername, tvCoinsNumber, tvFavoriteComics, tvSubscriptions, tvDownloadComics,
            tvNoFavorites, tvNoSubscriptions, tvNoDownloads;
    LinearLayout llNotLoggedIn, settingsBtn, coinsBtn;
    RecyclerView rvFavorites, rvSubscriptions, rvDownloads;
    String userName, password;
    AppCompatButton loginBtn;
    FavoriteAdapter favoriteAdapter;
    SubscriptionsAdapter subscriptionsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        tvUsername = binding.username;
        tvCoinsNumber = binding.coinsNumber;
        tvFavoriteComics = binding.favorites;
        userIcon = binding.userIcon;
        tvSubscriptions = binding.subscriptions;
        tvDownloadComics = binding.downloads;
        llNotLoggedIn = binding.notLoggedIn;
        tvNoFavorites = binding.tvNoFavouriteComics;
        tvNoSubscriptions = binding.tvNoFavouriteAuthors;
        tvNoDownloads = binding.tvNoDownloadedComics;
        rvFavorites = binding.rvComics;
        rvSubscriptions = binding.rvSubscriptions;
        rvDownloads = binding.rvDownloads;
        coinsBtn = binding.coinBtn;
        settingsBtn = binding.settingsBtn;
        loginBtn = binding.btnLogin;

        SharedPreferences prefs = getActivity().getSharedPreferences("DeviceToken", MODE_PRIVATE);
        userName = prefs.getString("userName", null);
        password = prefs.getString("password", null);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AuthenticationActivity.class));
            }
        });
        tvSubscriptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSubscriptions.setTextColor(ContextCompat.getColor(getContext(), R.color.orange_text));
                tvFavoriteComics.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                tvDownloadComics.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                rvFavorites.setVisibility(View.GONE);
                tvNoFavorites.setVisibility(View.GONE);
                if (subscriptionsAdapter.getItemCount() == 0)
                    tvNoSubscriptions.setVisibility(View.VISIBLE);
                else
                    rvSubscriptions.setVisibility(View.VISIBLE);
            }
        });
        tvFavoriteComics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSubscriptions.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                tvFavoriteComics.setTextColor(ContextCompat.getColor(getContext(), R.color.orange_text));
                tvDownloadComics.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                rvSubscriptions.setVisibility(View.GONE);
                tvNoSubscriptions.setVisibility(View.GONE);
                if (favoriteAdapter.getItemCount() == 0)
                    tvNoFavorites.setVisibility(View.VISIBLE);
                else
                    rvFavorites.setVisibility(View.VISIBLE);
            }
        });
        tvDownloadComics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                tvSubscriptions.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
//                tvFavoriteComics.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
//                tvDownloadComics.setTextColor(ContextCompat.getColor(getContext(), R.color.orange_text));
            }
        });
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BaseActivity)requireActivity()).addFragment(new SettingsFragment());
            }
        });
        setRecyclerViews();
        setUser();

        return binding.getRoot();
    }

    private void setRecyclerViews() {
        favoriteAdapter = new FavoriteAdapter(requireContext());
        subscriptionsAdapter = new SubscriptionsAdapter(requireContext());

        Call<ArrayList<Comics>> getFavorites = ApiClient.getUserService().getFavorites(userName);
        getFavorites.enqueue(new Callback<ArrayList<Comics>>() {
            @Override
            public void onResponse(Call<ArrayList<Comics>> call, Response<ArrayList<Comics>> response) {
                favoriteAdapter.setData(response.body());
                if (favoriteAdapter.getItemCount() == 0) {
                    tvNoFavorites.setVisibility(View.VISIBLE);
                }
                else
                    rvFavorites.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<ArrayList<Comics>> call, Throwable t) {
                Log.e("ASD", "inside failure " + t.getLocalizedMessage());
            }
        });

        Call<ArrayList<User>> getSubscriptions = ApiClient.getUserService().getSubscriptions(userName);
        getSubscriptions.enqueue(new Callback<ArrayList<User>>() {
            @Override
            public void onResponse(Call<ArrayList<User>> call, Response<ArrayList<User>> response) {
                subscriptionsAdapter.setData(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<User>> call, Throwable t) {

            }
        });

        LinearLayoutManager layoutManagerFavorites = new LinearLayoutManager(getContext());
        layoutManagerFavorites.setOrientation(LinearLayoutManager.VERTICAL);
        rvFavorites.setLayoutManager(layoutManagerFavorites);
        rvFavorites.setAdapter(favoriteAdapter);

        LinearLayoutManager layoutManagerSubscriptions = new LinearLayoutManager(getContext());
        layoutManagerSubscriptions.setOrientation(LinearLayoutManager.VERTICAL);
        rvSubscriptions.setLayoutManager(layoutManagerSubscriptions);
        rvSubscriptions.setAdapter(subscriptionsAdapter);
    }

    private void setUser() {
        if (userName != null && password != null) {
            tvUsername.setText(userName);
            llNotLoggedIn.setVisibility(View.GONE);
            tvFavoriteComics.setTextColor(ContextCompat.getColor(getContext(), R.color.orange_text));
            tvUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((BaseActivity) requireActivity()).addFragment(new DetailedProfileFragment());
                }
            });
        } else {
            tvUsername.setText(R.string.click_to_login);
            tvUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), AuthenticationActivity.class));
                }
            });
        }
        Call<User> getCurrentUser = ApiClient.getUserService().getCurrentUser(userName);
        getCurrentUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()){
                    if (response.body().getAvatarBase64() != null) {
                        byte[] imageBytes = Base64.decode(response.body().getAvatarBase64(), Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                        userIcon.setImageBitmap(bitmap);
                        SharedPreferences.Editor editor = requireActivity().getSharedPreferences("DeviceToken", MODE_PRIVATE).edit();
                        editor.putString("avatar", response.body().getAvatarBase64());
                        editor.apply();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }
}