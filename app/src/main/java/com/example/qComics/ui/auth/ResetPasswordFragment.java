package com.example.qComics.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.q_comics.R;
import com.example.q_comics.databinding.FragmentForgotPasswordBinding;
import com.example.q_comics.databinding.FragmentResetPasswordBinding;
import com.google.android.material.textfield.TextInputEditText;

public class ResetPasswordFragment extends Fragment {

    private FragmentResetPasswordBinding binding;
    TextInputEditText password, repeatPassword;
    AppCompatButton btnContinue;
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

        binding = FragmentResetPasswordBinding.inflate(inflater, container, false);
        password = binding.password;
        repeatPassword = binding.repeatPassword;
        btnContinue = binding.btnContinue;
        backBtn = binding.backBtn;

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Navigation.findNavController(v).navigate(R.id.action_LoginFragment_to_registrationFragment);
            }
        });

        backBtn.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_resetPasswordFragment_to_forgotPasswordCodeFragment));

        return binding.getRoot();
    }
}