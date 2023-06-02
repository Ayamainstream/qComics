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

import com.example.qComics.data.network.ApiClient;
import com.example.qComics.ui.base.BaseActivity;
import com.example.q_comics.R;
import com.example.q_comics.databinding.FragmentDetailedProfileBinding;
import com.example.q_comics.databinding.FragmentProfileBinding;

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
        return binding.getRoot();
    }
}