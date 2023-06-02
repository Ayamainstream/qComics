package com.example.qComics.ui.auth;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.qComics.data.network.ApiClient;
import com.example.qComics.data.network.auth.RegisterRequest;
import com.example.q_comics.R;
import com.example.q_comics.databinding.FragmentRegistrationBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationFragment extends Fragment {
    private FragmentRegistrationBinding binding;
    TextInputEditText edEmail, edPassword, edName, edRepeatPassword;
    TextInputLayout llEmail, llPassword, llName, llRepeatPassword;
    AppCompatButton btnRegister;
    LinearLayout btnLogin;
    ImageView backBtn;

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

        binding = FragmentRegistrationBinding.inflate(inflater, container, false);
        edName = binding.name;
        llName = binding.llName;
        edEmail = binding.email;
        llEmail = binding.llEmail;
        edPassword = binding.password;
        llPassword = binding.llPassword;
        edRepeatPassword = binding.repeatPassword;
        llRepeatPassword = binding.llRepeatPassword;
        btnLogin = binding.btnLogin;
        btnRegister = binding.btnRegister;
        backBtn = binding.backBtn;

        backBtn.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_RegistrationFragment_to_LoginFragment));
        btnLogin.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_RegistrationFragment_to_LoginFragment));
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
        initView();
        return binding.getRoot();
    }

    private void initView() {
        edEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edEmail.hasFocus()) {
                    edEmail.setSelection(edEmail.getText().length());
                    checkCredentials();
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
                        edEmail.setError("Not supported type of email");
                    }
                }
            }
        });
        edName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edName.hasFocus()) {
                    edName.setSelection(edName.getText().length());
                    checkCredentials();
                    if (s.toString().length() < 3) {
                        edName.setError("Too short");
                    }
                }
            }
        });
        edPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edPassword.hasFocus()) {
                    edPassword.setSelection(edPassword.getText().length());
                    checkCredentials();
                    if (!s.toString().equals(edRepeatPassword.getText().toString())) {
                        edRepeatPassword.setError("Passwords do not match");
                    } else {
                        edRepeatPassword.setError(null);
                    }
                }
            }
        });

        edRepeatPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (edRepeatPassword.hasFocus()) {
                    edRepeatPassword.setSelection(edRepeatPassword.getText().length());
                    checkCredentials();
                    if (!s.toString().equals(edPassword.getText().toString())) {
                        edRepeatPassword.setError("Passwords do not match");
                    } else {
                        edRepeatPassword.setError(null);
                    }
                }
            }
        });
    }

    private void checkCredentials() {
        if (edName.getText().toString().isEmpty() ||
                edName.getText().toString().isEmpty() ||
                edPassword.getText().toString().isEmpty() ||
                edRepeatPassword.getText().toString().isEmpty())
            btnRegister.setEnabled(false);
        else
            btnRegister.setEnabled(true);
    }

    private void register() {
        RegisterRequest registrationRequest = new RegisterRequest();
        checkCredentials();
        registrationRequest.setUsername(Objects.requireNonNull(edName.getText()).toString());
        registrationRequest.setEmail(Objects.requireNonNull(edEmail.getText()).toString());
        registrationRequest.setPassword(Objects.requireNonNull(edPassword.getText()).toString());
        Call<ResponseBody> userRegisterCall = ApiClient.getUserService().registration(registrationRequest);
        userRegisterCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Registration was successful
                    SharedPreferences.Editor editor = getContext().getSharedPreferences("DeviceToken", MODE_PRIVATE).edit();
                    editor.putString("userEmail", registrationRequest.getEmail()); // or add toString() after if needed
                    editor.putString("userName", registrationRequest.getUsername()); // or add toString() after if needed
                    editor.apply();
                    try {
                        Toast.makeText(getContext(), response.body().string(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                    navController.navigate(R.id.action_RegistrationFragment_to_verificationFragment);
                } else {
                    // Registration failed, handle the error
                    Toast.makeText(getContext(), "Регистрация не удалась.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Request failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}