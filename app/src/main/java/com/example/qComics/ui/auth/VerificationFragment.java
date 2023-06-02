package com.example.qComics.ui.auth;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.qComics.data.network.ApiClient;
import com.example.qComics.data.network.auth.Validate;
import com.example.qComics.data.utils.VerificationCodeTimer;
import com.example.q_comics.R;
import com.example.q_comics.databinding.FragmentVerificationBinding;
import com.google.android.material.textfield.TextInputEditText;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerificationFragment extends Fragment {

    private FragmentVerificationBinding binding;
    private VerificationCodeTimer verificationCodeTimer;
    TextInputEditText first, second, third, fourth;
    TextView userEmail, timer, countdown;
    String userName, number;
    LinearLayout btnResendCode;
    AppCompatButton btnSend;
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

        binding = FragmentVerificationBinding.inflate(inflater, container, false);
        first = binding.first;
        second = binding.second;
        third = binding.third;
        fourth = binding.fourth;
        userEmail = binding.userEmail;
        btnResendCode = binding.btnResendCode;
        countdown = binding.resendCodeIn;
        timer = binding.timer;
        btnSend = binding.btnSend;
        backBtn = binding.backBtn;

        setButtonEnabled(btnSend, false);
        startVerificationCodeTimer();
        SharedPreferences prefs = getActivity().getSharedPreferences("DeviceToken", MODE_PRIVATE);
        userEmail.setText(prefs.getString("userEmail", "something wrong"));
        userName = prefs.getString("userName", "");

        btnResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificationCodeTimer.startTimer();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateEmail();
            }
        });

        backBtn.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_verificationFragment_to_RegistrationFragment));

        first.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    second.requestFocus();
                    checkCredentials();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        second.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                checkCredentials();
                if (s.length() == 1) {
                    third.requestFocus();
                } else if (s.length() == 0) {
                    first.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        third.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                checkCredentials();
                if (s.length() == 1) {
                    fourth.requestFocus();
                } else if (s.length() == 0) {
                    second.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        fourth.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                checkCredentials();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        second.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN && second.getText().length() == 0) {
                    first.requestFocus();
                    return true;
                }
                return false;
            }
        });

        third.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN && third.getText().length() == 0) {
                    second.requestFocus();
                    return true;
                }
                return false;
            }
        });

        fourth.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN && fourth.getText().length() == 0) {
                    third.requestFocus();
                    return true;
                }
                return false;
            }
        });

        return binding.getRoot();
    }

    private void validateEmail() {
        number = first.getText().toString() +
                second.getText().toString() +
                third.getText().toString() +
                fourth.getText().toString();

        Validate validateRequest = new Validate();
        validateRequest.setUsername(userName);
        validateRequest.setNumber(Integer.parseInt(number));
        Call<ResponseBody> validate = ApiClient.getUserService().validate(validateRequest);
        validate.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Регистрация удалась.", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(
                            R.id.action_verificationFragment_to_RegistrationFragment);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Регистрация не удалась.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkCredentials() {
        if (first.getText().toString().isEmpty() ||
                second.getText().toString().isEmpty() ||
                third.getText().toString().isEmpty() ||
                fourth.getText().toString().isEmpty())
            setButtonEnabled(btnSend, false);
        else
            setButtonEnabled(btnSend, true);
    }

    private void setButtonEnabled(AppCompatButton button, boolean enabled) {
        button.setEnabled(enabled);
        if (enabled) {
            button.setBackgroundResource(R.drawable.bg_orange_button);
        } else {
            button.setBackgroundResource(R.drawable.bg_orange_button_disabled);
        }
    }

    private void startVerificationCodeTimer() {
        if (verificationCodeTimer != null) {
            verificationCodeTimer.cancel();
        }

        verificationCodeTimer = new VerificationCodeTimer(40000, 1000,
                timer, countdown, btnResendCode, ContextCompat.getColor(requireContext(), R.color.orange_dark));
        verificationCodeTimer.startTimer();
    }
}