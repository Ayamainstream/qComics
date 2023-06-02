package com.example.qComics.ui.main.user;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qComics.data.network.ApiClient;
import com.example.qComics.ui.base.BaseActivity;
import com.example.q_comics.R;
import com.example.q_comics.databinding.FragmentAuthorsProfileBinding;
import com.example.q_comics.databinding.FragmentDetailedProfileBinding;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthorsProfileFragment extends Fragment {

    private FragmentAuthorsProfileBinding binding;
    CircleImageView userIcon;
    TextView tvUsername, tvComicsNumber, tvFollowersNumber;
    AppCompatButton subscribeBtn;
    RecyclerView rvAddedComics;
    String userName, authorName;
    ImageView backBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAuthorsProfileBinding.inflate(inflater, container, false);

        userIcon = binding.profileImage;
        tvUsername = binding.username;
        tvComicsNumber = binding.comicsNumber;
        tvFollowersNumber = binding.followersNumber;
        subscribeBtn = binding.btnSubscribe;
        rvAddedComics = binding.rvComics;
        backBtn = binding.backBtn;

        SharedPreferences prefs = getActivity().getSharedPreferences("DeviceToken", MODE_PRIVATE);
        userName = prefs.getString("userName", null);
        authorName = prefs.getString("authorName", null);

        tvUsername.setText(authorName);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

        subscribeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subscribe();
            }
        });

        Call<String> getFollowers = ApiClient.getUserService().getSubscriberAmount(authorName);
        getFollowers.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                tvFollowersNumber.setText(response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
        return binding.getRoot();
    }

    private void subscribe() {
        Call<String> subscribe = ApiClient.getUserService().subscribe(userName, authorName);
        subscribe.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Toast.makeText(getContext(), response.body(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }
}