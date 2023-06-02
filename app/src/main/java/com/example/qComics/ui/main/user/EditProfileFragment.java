package com.example.qComics.ui.main.user;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.example.qComics.ui.base.BaseActivity;
import com.example.q_comics.databinding.FragmentEditProfileBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileFragment extends Fragment {

    private FragmentEditProfileBinding binding;
    private BottomSheetBehavior<View> mBottomSheetBehavior;
    ImageView backBtn;
    RelativeLayout rlEditAvatar, rlEditUsername;
    CoordinatorLayout mViewBg;
    FrameLayout bottomSheetAvatar;
    TextView tvUsername, selectFromGalleryBtn, takePictureBtn, deletePhotoBtn;
    CircleImageView userIcon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false);

        backBtn = binding.backBtn;
        rlEditAvatar = binding.llEditAvatar;
        rlEditUsername = binding.llEditUsername;
        mViewBg = binding.bottomSheetBackground;
        bottomSheetAvatar = binding.bottomSheet;
        tvUsername = binding.username;
        selectFromGalleryBtn = binding.selectFromGalleryBtn;
        takePictureBtn = binding.takePictureBtn;
        deletePhotoBtn = binding.deletePhotoBtn;
        userIcon = binding.profileImage;

        mViewBg.setVisibility(View.GONE);

        SharedPreferences prefs = getActivity().getSharedPreferences("DeviceToken", MODE_PRIVATE);
        tvUsername.setText(prefs.getString("userName", null));
        String avatar = prefs.getString("avatar", null);

        if (avatar != null) {
            byte[] imageBytes = Base64.decode(avatar, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            userIcon.setImageBitmap(bitmap);
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

        rlEditAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                mViewBg.setVisibility(View.VISIBLE);
            }
        });

        rlEditUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BaseActivity) requireActivity()).addFragment(new EditUsernameFragment());
            }
        });

        selectFromGalleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        takePictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        deletePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void bottomSheetInitialization() {
        mBottomSheetBehavior = initializeBottomSheet(bottomSheetAvatar);
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