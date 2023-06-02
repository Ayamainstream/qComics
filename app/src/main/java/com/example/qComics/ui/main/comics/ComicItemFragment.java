package com.example.qComics.ui.main.comics;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qComics.data.network.ApiClient;
import com.example.qComics.data.network.comics.Chapter;
import com.example.qComics.data.network.comics.Comics;
import com.example.qComics.data.network.comics.SaveRequest;
import com.example.qComics.ui.base.BaseActivity;
import com.example.qComics.ui.main.adapters.ChapterAdapter;
import com.example.qComics.ui.main.user.AuthorsProfileFragment;
import com.example.q_comics.R;
import com.example.q_comics.databinding.FragmentComicItemBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComicItemFragment extends Fragment {

    private FragmentComicItemBinding binding;
    private BottomSheetBehavior<View> mBottomSheetBehavior, sBottomSheetBehavior;
    FrameLayout bottomSheetInfo, bottomSheetShare;
    TextView tvGenre, tvType, tvComicName, tvCreatorName, tvDescription, tvRating, tvPopularity, tvBottomDescription, tvBottomCreator;
    RoundedImageView cover;
    ImageView backBtn, downloadBtn, infoBtn, shareBtn, saveBtn;
    String comicsName, username;
    CoordinatorLayout mViewBg;
    RecyclerView rvChapters;
    Boolean saveStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentComicItemBinding.inflate(inflater, container, false);
        tvGenre = binding.tvGenre;
        tvType = binding.tvType;
        tvComicName = binding.tvComicName;
        tvCreatorName = binding.tvStudioName;
        tvDescription = binding.tvDescription;
        tvRating = binding.tvRating;
        tvPopularity = binding.tvPopularity;
        tvBottomDescription = binding.tvBottomDescription;
        tvBottomCreator = binding.tvBottomStudioName;
        bottomSheetInfo = binding.bottomSheet;
        bottomSheetShare = binding.bottomSheetShare;
        mViewBg = binding.bottomSheetBackground;
        backBtn = binding.backBtn;
        downloadBtn = binding.download;
        infoBtn = binding.info;
        shareBtn = binding.share;
        saveBtn = binding.add;
        cover = binding.cover;
        rvChapters = binding.rvChapters;
        saveStatus = Boolean.FALSE;

        mViewBg.setVisibility(View.INVISIBLE);
        bottomSheetInitialization();
        SharedPreferences prefs = getActivity().getSharedPreferences("DeviceToken", MODE_PRIVATE);
        username = prefs.getString("userName", null);
        comicsName = prefs.getString("comicsName", "");
        if (username == null) {
            saveBtn.setVisibility(View.GONE);
        } else {
            saveBtn.setVisibility(View.VISIBLE);
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });
        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetShare.setVisibility(View.GONE);
                bottomSheetInfo.setVisibility(View.VISIBLE);
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                mViewBg.setVisibility(View.VISIBLE);
            }
        });
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetInfo.setVisibility(View.GONE);
                bottomSheetShare.setVisibility(View.VISIBLE);
                sBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                mViewBg.setVisibility(View.VISIBLE);
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToFavorites();
            }
        });
        initView();

        tvCreatorName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getContext().getSharedPreferences("DeviceToken", MODE_PRIVATE).edit();
                editor.putString("authorName", tvCreatorName.getText().toString()); // or add toString() after if needed
                editor.apply();
                ((BaseActivity)requireActivity()).addFragment(new AuthorsProfileFragment());
            }
        });

        if (saveStatus) {
            saveBtn.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_added));
        }
        return binding.getRoot();
    }

    private void addToFavorites() {
        SaveRequest saveRequest = new SaveRequest();
        saveRequest.setUsername(username);
        saveRequest.setComicName(comicsName);
        Call<ResponseBody> addToFavorites = ApiClient.getUserService().addToFavorites(saveRequest);
        addToFavorites.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                saveBtn.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_added));
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void bottomSheetInitialization() {
        mBottomSheetBehavior = initializeBottomSheet(bottomSheetInfo);
        sBottomSheetBehavior = initializeBottomSheet(bottomSheetShare);
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
        Call<Comics> getComic = ApiClient.getUserService().getComic(comicsName);
        getComic.enqueue(new Callback<Comics>() {
            @Override
            public void onResponse(Call<Comics> call, Response<Comics> response) {
                if (response.body() != null) {
                    String commaSeparatedGenres = TextUtils.join(", ", response.body().getGenres());
                    tvGenre.setText(commaSeparatedGenres);
                    byte[] imageBytes = Base64.decode(response.body().getImageCoverBase64(), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    cover.setImageBitmap(bitmap);
                    tvType.setText(response.body().getType());
                    tvComicName.setText(response.body().getName());
                    tvCreatorName.setText(response.body().getAuthor());
                    tvDescription.setText(response.body().getDescription());
                    tvRating.setText(response.body().getRating().toString());
                    tvPopularity.setText(String.valueOf(response.body().getVotes()));
                    tvBottomDescription.setText(response.body().getDescription());
                    tvBottomCreator.setText(response.body().getAuthor());
                }
            }

            @Override
            public void onFailure(Call<Comics> call, Throwable t) {

            }
        });
        ChapterAdapter chapterAdapter = new ChapterAdapter(requireActivity());

        Call<ArrayList<Chapter>> getChapter = ApiClient.getUserService().getChapters(comicsName);
        getChapter.enqueue(new Callback<ArrayList<Chapter>>() {
            @Override
            public void onResponse(Call<ArrayList<Chapter>> call, Response<ArrayList<Chapter>> response) {
                chapterAdapter.setData(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<Chapter>> call, Throwable t) {
                Log.e("ASD", "inside failure " + t.getLocalizedMessage());
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvChapters.setLayoutManager(layoutManager);
        rvChapters.setAdapter(chapterAdapter);

        Call<Boolean> getStatus = ApiClient.getUserService().getFavoriteStatus(username, comicsName);
        getStatus.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                saveStatus = response.body();
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {

            }
        });
    }
}