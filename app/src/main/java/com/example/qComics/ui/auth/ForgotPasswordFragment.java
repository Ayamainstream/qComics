package com.example.qComics.ui.auth;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.q_comics.R;
import com.example.q_comics.databinding.FragmentForgotPasswordBinding;
import com.example.q_comics.databinding.FragmentLoginBinding;
import com.google.android.material.textfield.TextInputEditText;

public class ForgotPasswordFragment extends Fragment {

    private FragmentForgotPasswordBinding binding;
    TextInputEditText email;
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

        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false);
        email = binding.email;
        btnContinue = binding.btnContinue;
        backBtn = binding.backBtn;

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_forgotPasswordFragment_to_forgotPasswordCodeFragment);
            }
        });

        backBtn.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_forgotPasswordFragment_to_LoginFragment));

        return binding.getRoot();
    }
}