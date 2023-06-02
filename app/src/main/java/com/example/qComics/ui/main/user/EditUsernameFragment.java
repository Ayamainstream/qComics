package com.example.qComics.ui.main.user;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.qComics.data.network.ApiClient;
import com.example.qComics.data.network.auth.User;
import com.example.q_comics.R;
import com.example.q_comics.databinding.FragmentEditUsernameBinding;
import com.google.android.material.textfield.TextInputEditText;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditUsernameFragment extends Fragment {

    private FragmentEditUsernameBinding binding;
    TextInputEditText edUsername;
    TextView confirmBtn, tvCharacterCount;
    ImageView backBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditUsernameBinding.inflate(inflater, container, false);

        edUsername = binding.edUsername;
        confirmBtn = binding.confirmButton;
        tvCharacterCount = binding.characterCountTextView;
        backBtn = binding.backBtn;

        edUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int characterCount = s.length();
                tvCharacterCount.setText(characterCount + "/20");
                if (characterCount == 0) {
                    confirmBtn.setTextColor(getResources().getColor(R.color.light_gray));
                } else {
                    confirmBtn.setTextColor(getResources().getColor(R.color.orange_text));
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });
        sendNewUsername();

        return binding.getRoot();
    }

    private void sendNewUsername() {
        User user = new User();
        SharedPreferences prefs = getActivity().getSharedPreferences("DeviceToken", MODE_PRIVATE);
        Call<User> getCurrentUser = ApiClient.getUserService().getCurrentUser(prefs.getString("userName", null));
        getCurrentUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    user.setEmail(response.body().getEmail());
                    user.setId(response.body().getId());
                    user.setRole(response.body().getRole());
                    user.setAvatarBase64(response.body().getAvatarBase64());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirmBtn.getCurrentTextColor() == getResources().getColor(R.color.orange_text)) {
                    user.setUsername(edUsername.getText().toString());
                    Call<ResponseBody> updateUsername = ApiClient.getUserService().updateUser(user);
                    updateUsername.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(),"Update was successful!", Toast.LENGTH_SHORT).show();
                                requireActivity().onBackPressed();
                            } else
                                Toast.makeText(getContext(),"Please check if everything is correct!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(getContext(),"Oops something was wrong!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}