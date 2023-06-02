package com.example.qComics.ui.auth;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.qComics.data.network.ApiClient;
import com.example.qComics.data.network.SessionManager;
import com.example.qComics.data.network.auth.LoginRequest;
import com.example.qComics.data.network.auth.LoginResponse;
import com.example.qComics.ui.main.MainActivity;
import com.example.q_comics.R;
import com.example.q_comics.databinding.FragmentLoginBinding;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    TextInputEditText username;
    TextInputEditText password;
    LinearLayout btnRegister;
    TextView resendPassword;
    ImageView close;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button even
//                Log.d("BACKBUTTON", "Back button clicks");
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        username = binding.username;
        password = binding.password;
        btnRegister = binding.btnRegister;
        resendPassword = binding.resendPassword;
        close = binding.close;

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_LoginFragment_to_registrationFragment);
            }
        });

        resendPassword.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_LoginFragment_to_forgotPasswordFragment));

        binding.btnLogin.setOnClickListener(view1 -> {
            if (TextUtils.isEmpty(Objects.requireNonNull(username.getText()).toString()) || TextUtils.isEmpty(Objects.requireNonNull(password.getText()).toString())) {
                Toast.makeText(getActivity(), "Email/Password required", Toast.LENGTH_LONG).show();
            } else {
                login();
            }
        });

        return binding.getRoot();
    }

    private void login() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(Objects.requireNonNull(username.getText()).toString());
        loginRequest.setPassword(Objects.requireNonNull(password.getText()).toString());

        Call<LoginResponse> loginResponseCall = ApiClient.getUserService().userLogin(loginRequest);
        SessionManager sessionManager = new SessionManager(getActivity());
        loginResponseCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    Bundle bundle = new Bundle();
                    new Handler().postDelayed(() -> {
                        try {
                            assert loginResponse != null;
                            sessionManager.saveAuthToken(loginResponse.getToken());
                            SharedPreferences prefs = requireActivity().getSharedPreferences("DeviceToken", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("userName", String.valueOf(username.getText()));
                            editor.putString("password", String.valueOf(password.getText()));
                            editor.apply();
                            startActivity(new Intent(getActivity(), MainActivity.class).putExtras(bundle));
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Something wrong", Toast.LENGTH_SHORT).show();
                        }
                    }, 100);
                } else {
                    Toast.makeText(getActivity(), "Login failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Throwable " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}