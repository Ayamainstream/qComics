package com.example.qComics.ui.main.user;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.qComics.data.network.ApiClient;
import com.example.qComics.data.network.comics.Comics;
import com.example.qComics.data.network.comics.Map;
import com.example.qComics.data.network.comics.RequestAuthor;
import com.example.qComics.ui.base.BaseActivity;
import com.example.qComics.ui.main.adapters.MainComicsAdapter;
import com.example.q_comics.R;
import com.example.q_comics.databinding.FragmentDetailedProfileBinding;
import com.example.q_comics.databinding.FragmentProfileBinding;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailedProfileFragment extends Fragment {

    private FragmentDetailedProfileBinding binding;
    CircleImageView userIcon;
    TextView tvUsername, tvComicsNumber, tvFollowersNumber;
    AppCompatButton editProfileBtn;
    RecyclerView rvAddedComics;
    String userName;
    ImageView backBtn;
    MainComicsAdapter mainComicsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetailedProfileBinding.inflate(inflater, container, false);

        userIcon = binding.profileImage;
        tvUsername = binding.username;
        tvComicsNumber = binding.comicsNumber;
        tvFollowersNumber = binding.followersNumber;
        editProfileBtn = binding.btnEditProfile;
        rvAddedComics = binding.rvComics;
        backBtn = binding.backBtn;

        SharedPreferences prefs = getActivity().getSharedPreferences("DeviceToken", MODE_PRIVATE);
        userName = prefs.getString("userName", null);

        tvUsername.setText(userName);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BaseActivity) requireActivity()).addFragment(new EditProfileFragment());
            }
        });

        Call<String> getFollowers = ApiClient.getUserService().getSubscriberAmount(userName);
        getFollowers.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                tvFollowersNumber.setText(response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
        getComics();
        getFollowers();
        return binding.getRoot();
    }

    private void getComics() {
        mainComicsAdapter = new MainComicsAdapter(requireContext());
        RequestAuthor requestAuthor = new RequestAuthor();
        requestAuthor.setAuthor(userName);
        Map mapRating = new Map();
        mapRating.setPage(0);
        mapRating.setSize(100);
        Call<ArrayList<Comics>> authorsComics = ApiClient.getUserService().mapComicsAuthor(requestAuthor, mapRating);
        authorsComics.enqueue(new Callback<ArrayList<Comics>>() {
            @Override
            public void onResponse(Call<ArrayList<Comics>> call, Response<ArrayList<Comics>> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    mainComicsAdapter.setData(response.body());
                    mainComicsAdapter.notifyDataSetChanged();
                    tvComicsNumber.setText(String.valueOf(mainComicsAdapter.getItemCount()));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Comics>> call, Throwable t) {
                Log.e("ASD", "Inside failure " + t.getLocalizedMessage() + " " + t.getMessage());
            }
        });
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        rvAddedComics.setLayoutManager(layoutManager);
        rvAddedComics.setAdapter(mainComicsAdapter);
    }

    private void getFollowers() {
        Call<String> getFollowers = ApiClient.getUserService().getSubscriberAmount(userName);
        getFollowers.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful())
                    tvFollowersNumber.setText(response.body());
                else
                    tvFollowersNumber.setText("0");
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

}