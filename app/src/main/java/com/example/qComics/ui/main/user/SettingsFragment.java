package com.example.qComics.ui.main.user;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.qComics.ui.auth.AuthenticationActivity;
import com.example.qComics.ui.auth.ResetPasswordFragment;
import com.example.qComics.ui.base.BaseActivity;
import com.example.qComics.ui.main.MainActivity;
import com.example.q_comics.R;
import com.example.q_comics.databinding.FragmentSettingsBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private BottomSheetBehavior<View> mBottomSheetBehavior;
    ImageView backBtn;
    RelativeLayout rlLanguage, rlResetPassword, rlFeedback, rlLogout;
    CoordinatorLayout mViewBg;
    FrameLayout bottomSheet;
    TextView tvLanguage, tvRussian, tvKazakh, tvEnglish;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        backBtn = binding.backBtn;
        rlLanguage = binding.llLanguage;
        rlResetPassword = binding.llResetPassword;
        rlFeedback = binding.llFeedback;
        rlLogout = binding.llLogout;
        mViewBg = binding.bottomSheetBackground;
        bottomSheet = binding.bottomSheet;
        tvLanguage = binding.tvLanguage;
        tvRussian = binding.tvRussian;
        tvKazakh = binding.tvKazakh;
        tvEnglish = binding.tvEnglish;

        prefs = requireActivity().getSharedPreferences("DeviceToken", MODE_PRIVATE);
        editor = prefs.edit();
        String language = prefs.getString("language", getString(R.string.russian));
        mViewBg.setVisibility(View.GONE);

        if (language.equals("Казахский")) {
            tvLanguage.setText(getString(R.string.kazakh));
        } else {
            tvLanguage.setText(getString(R.string.russian));
        }

        initButtons();

        bottomSheetInitialization();
        return binding.getRoot();
    }

    private void initButtons() {

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

        rlLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                mViewBg.setVisibility(View.VISIBLE);
            }
        });

        rlResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ((BaseActivity) requireActivity()).addFragment(new ResetPasswordFragment());
            }
        });

        rlFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        rlLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
                startActivity(new Intent(getActivity(), AuthenticationActivity.class));
            }
        });

        tvRussian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLanguage(getString(R.string.russian));
            }
        });

        tvKazakh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLanguage(getString(R.string.kazakh));
            }
        });

        tvEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void changeLanguage(String language) {
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        Locale locale;

        if (getString(R.string.kazakh).equals(language)) {
            locale = new Locale("kk"); // Kazakh language code
        } else {
            locale = Locale.getDefault();
        }
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        Intent refresh = new Intent(requireActivity(), MainActivity.class);
        startActivity(refresh);

        editor.putString("language", language);
        editor.apply();
    }


    private void logout() {
        editor.remove("username");
        editor.remove("password");
        editor.apply();
    }

    private void bottomSheetInitialization() {
        mBottomSheetBehavior = initializeBottomSheet(bottomSheet);
    }

    private BottomSheetBehavior<View> initializeBottomSheet(View bottomSheet) {
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                handleBottomSheetStateChange(newState);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                handleBottomSheetSlide(slideOffset);
            }
        });

        return bottomSheetBehavior;
    }

    private void handleBottomSheetStateChange(int newState) {
        if (newState == BottomSheetBehavior.STATE_EXPANDED || newState == BottomSheetBehavior.STATE_HIDDEN) {
            // Show or hide background
            int alpha = (newState == BottomSheetBehavior.STATE_EXPANDED) ? 25 : 0;
            mViewBg.setBackgroundColor(Color.argb(alpha, 0, 0, 0));
        }
    }

    private void handleBottomSheetSlide(float slideOffset) {
        int alpha = (int) (Math.abs(slideOffset * 25));
        mViewBg.setBackgroundColor(Color.argb(Math.abs(25 - alpha), 0, 0, 0));
    }
}