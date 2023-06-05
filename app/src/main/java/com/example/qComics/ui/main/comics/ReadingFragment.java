package com.example.qComics.ui.main.comics;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.qComics.data.network.ApiClient;
import com.example.qComics.data.network.comics.Chapter;
import com.example.qComics.data.network.comics.Comics;
import com.example.qComics.data.network.comics.Images;
import com.example.qComics.data.utils.OnBackPressed;
import com.example.qComics.ui.main.adapters.ChapterMenuAdapter;
import com.example.qComics.ui.main.adapters.ImagesAdapter;
import com.example.qComics.ui.main.adapters.SearchAdapter;
import com.example.q_comics.R;
import com.example.q_comics.databinding.FragmentReadingBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReadingFragment extends Fragment implements OnBackPressed {

    private FragmentReadingBinding binding;
    private BottomSheetBehavior<View> mBottomSheetBehavior;
    FrameLayout bottomSheetComment;
    TextView tvChaptersNumber, tvLikes, tvComments, sendBtn;
    ImageView backBtn, chaptersMenuBtn, moreBtn, commentBtn, likeBtn, nextChapterBtn, previewsBtn;
    String comicsName, chapterName;
    CoordinatorLayout mViewBg;
    RecyclerView rvImages;
    FrameLayout containerLayout;
    BottomNavigationView navigationView;
    PopupWindow popupWindow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReadingBinding.inflate(inflater, container, false);
        tvLikes = binding.tvLikes;
        tvComments = binding.tvComments;
        tvChaptersNumber = binding.chaptersNumber;
        sendBtn = binding.sendBtn;
        chaptersMenuBtn = binding.chaptersMenu;
//        moreBtn = binding.more;
        commentBtn = binding.icComment;
        likeBtn = binding.icLike;
        nextChapterBtn = binding.nextChapter;
        previewsBtn = binding.previewsChapter;
        rvImages = binding.images;
        bottomSheetComment = binding.bottomSheetComment;
        mViewBg = binding.bottomSheetBackground;
        backBtn = binding.backBtn;

        mViewBg.setVisibility(View.INVISIBLE);

        containerLayout = requireActivity().findViewById(R.id.nav_host_fragment_activity_base);
        navigationView = requireActivity().findViewById(R.id.nav_view);
        navigationView.setVisibility(View.GONE);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) containerLayout.getLayoutParams();
        layoutParams.bottomMargin = 0;
        containerLayout.setLayoutParams(layoutParams);

        bottomSheetInitialization();
        initButtons();

        initView();
        return binding.getRoot();
    }

    private void initButtons() {
        chaptersMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setChapters();
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });
        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                mViewBg.setVisibility(View.VISIBLE);
            }
        });
        nextChapterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setChapters() {
        Call<ArrayList<Chapter>> getChapter = ApiClient.getUserService().getChapters(comicsName);
        getChapter.enqueue(new Callback<ArrayList<Chapter>>() {
            @Override
            public void onResponse(Call<ArrayList<Chapter>> call, Response<ArrayList<Chapter>> response) {
                if (response.isSuccessful())
                    showChapterMenuDialog(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<Chapter>> call, Throwable t) {
                Log.e("ASD", "inside failure " + t.getLocalizedMessage());
            }
        });
    }

    private void showChapterMenuDialog(ArrayList<Chapter> chapters) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_searchible_spinner, null);
        popupWindow = new PopupWindow(dialogView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ChapterMenuAdapter chapterMenuAdapter = new ChapterMenuAdapter(requireActivity(), popupWindow);
        chapterMenuAdapter.setData(chapters);

        // Set up the RecyclerView and its layout manager
        RecyclerView rvChapterMenu = dialogView.findViewById(R.id.rv_search_result);
        rvChapterMenu.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChapterMenu.setAdapter(chapterMenuAdapter);

        // Create and show the popup window
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);

        // Calculate the x and y offsets for the popup window
        int[] location = new int[2];
        chaptersMenuBtn.getLocationInWindow(location);
        popupWindow.showAsDropDown(chaptersMenuBtn, 0, 0);
    }

    private void bottomSheetInitialization() {
        mBottomSheetBehavior = initializeBottomSheet(bottomSheetComment);
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

    private void initView() {
        ImagesAdapter imagesAdapter = new ImagesAdapter(requireActivity());
        SharedPreferences prefs = getActivity().getSharedPreferences("DeviceToken", MODE_PRIVATE);
        comicsName = prefs.getString("comicsName","");
        chapterName = prefs.getString("chapterName","");
        tvChaptersNumber.setText(chapterName);
        Call<ArrayList<Images>> getImages = ApiClient.getUserService().getImages(chapterName, comicsName);
        getImages.enqueue(new Callback<ArrayList<Images>>() {
            @Override
            public void onResponse(Call<ArrayList<Images>> call, Response<ArrayList<Images>> response) {
                imagesAdapter.setData(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<Images>> call, Throwable t) {
                Log.e("ASD","inside failure "+ t.getLocalizedMessage());
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvImages.setLayoutManager(layoutManager);
        rvImages.setAdapter(imagesAdapter);
    }

    @Override
    public boolean onBackPressed() {
        navigationView.setVisibility(View.VISIBLE);
        ConstraintLayout.LayoutParams layoutParamsBack = (ConstraintLayout.LayoutParams) containerLayout.getLayoutParams();
        layoutParamsBack.bottomMargin = dpToPx(58);
        containerLayout.setLayoutParams(layoutParamsBack);
        return true;
    }

    public int dpToPx(float dp) {
        float scale = getResources().getDisplayMetrics().density;
        int pixels = (int) (dp * scale + 0.5f);
        return pixels;
    }

}