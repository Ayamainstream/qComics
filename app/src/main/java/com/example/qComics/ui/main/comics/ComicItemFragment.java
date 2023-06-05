package com.example.qComics.ui.main.comics;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import com.example.qComics.data.network.comics.Rating;
import com.example.qComics.data.network.comics.SaveRequest;
import com.example.qComics.ui.base.BaseActivity;
import com.example.qComics.ui.main.adapters.ChapterAdapter;
import com.example.qComics.ui.main.user.AuthorsProfileFragment;
import com.example.q_comics.R;
import com.example.q_comics.databinding.FragmentComicItemBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComicItemFragment extends Fragment {

    private FragmentComicItemBinding binding;
    private BottomSheetBehavior<View> mBottomSheetBehavior, sBottomSheetBehavior;
    FrameLayout bottomSheetInfo, bottomSheetShare;
    TextView tvGenre, tvType, tvComicName, tvCreatorName, tvDescription, tvRating, tvPopularity,
            tvBottomDescription, tvBottomCreator, rateBtn, rateDialogBtn, cancelDialogBtn;
    RoundedImageView cover;
    ImageView backBtn, downloadBtn, infoBtn, shareBtn, saveBtn;
    String comicsName, username;
    CoordinatorLayout mViewBg;
    RecyclerView rvChapters;
    Boolean saveStatus;
    Dialog ratingDialog;
    RatingBar ratingBarDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentComicItemBinding.inflate(inflater, container, false);
        tvGenre = binding.tvGenre;
        tvType = binding.tvType;
        tvComicName = binding.tvComicName;
        tvCreatorName = binding.tvStudioName;
        tvDescription = binding.tvDescription;
        tvRating = binding.tvRating;
        rateBtn = binding.rateBtn;
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
            rateBtn.setVisibility(View.GONE);
        } else {
            saveBtn.setVisibility(View.VISIBLE);
            rateBtn.setVisibility(View.VISIBLE);
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
        rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratingDialog.show();
            }
        });
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BaseActivity) requireActivity()).addFragment(new DownloadFragment());
            }
        });
        rateView();
        initView();

        tvBottomCreator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getContext().getSharedPreferences("DeviceToken", MODE_PRIVATE).edit();
                editor.putString("authorName", tvBottomCreator.getText().toString()); // or add toString() after if needed
                editor.apply();
                ((BaseActivity) requireActivity()).addFragment(new AuthorsProfileFragment());
            }
        });

        return binding.getRoot();
    }

    private void rateView() {
        ratingDialog = new Dialog(requireActivity());
        ratingDialog.setContentView(R.layout.dialog_rating);
        ratingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ratingBarDialog = ratingDialog.findViewById(R.id.ratingBar);
        rateDialogBtn = ratingDialog.findViewById(R.id.rate_btn);
        cancelDialogBtn = ratingDialog.findViewById(R.id.cancel_btn);

        Call<Rating> getRating = ApiClient.getUserService().getRating(comicsName, username);
        getRating.enqueue(new Callback<Rating>() {
            @Override
            public void onResponse(Call<Rating> call, Response<Rating> response) {
                if (response.isSuccessful())
                    ratingBarDialog.setRating(response.body().getRating().floatValue());
            }

            @Override
            public void onFailure(Call<Rating> call, Throwable t) {

            }
        });

        cancelDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratingDialog.dismiss();
            }
        });

        rateDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRatingRequest();
                ratingDialog.dismiss();
            }
        });
    }

    private void sendRatingRequest() {
        Rating ratingRequest = new Rating();
        ratingRequest.setRating((double) ratingBarDialog.getRating());
        ratingRequest.setComicName(comicsName);
        ratingRequest.setUsername(username);
        Call<Rating> setRating = ApiClient.getUserService().setRating(ratingRequest);
        setRating.enqueue(new Callback<Rating>() {
            @Override
            public void onResponse(Call<Rating> call, Response<Rating> response) {
                if (response.isSuccessful()) {
                    ratingBarDialog.setRating(response.body().getRating().floatValue());
                    initView();
                }
            }

            @Override
            public void onFailure(Call<Rating> call, Throwable t) {

            }
        });
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
                    List<String> genres = response.body().getGenres();
                    List<String> genreNames = new ArrayList<>();
                    for (String genre : genres) {
                        String genreName = getStringFromGenreName(genre);
                        genreNames.add(genreName);
                    }
                    String commaSeparatedGenres = TextUtils.join(", ", genreNames);
                    tvGenre.setText(commaSeparatedGenres);
                    byte[] imageBytes = Base64.decode(response.body().getImageCoverBase64(), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    cover.setImageBitmap(bitmap);
                    if (response.body().getType().equals("STUDIO"))
                        tvType.setText(getString(R.string.studio));
                    else
                        tvType.setText(getString(R.string.authors));
                    tvComicName.setText(response.body().getName());
                    tvCreatorName.setText(response.body().getAuthor());
                    tvDescription.setText(response.body().getDescription());
                    tvRating.setText(response.body().getRating().toString());
                    if (response.body().getVotes() == null)
                        tvPopularity.setText("(0)");
                    else
                        tvPopularity.setText("(" + String.valueOf(response.body().getVotes()) + ")");
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

        Call<ArrayList<Comics>> getFavorites = ApiClient.getUserService().getFavorites(username);
        getFavorites.enqueue(new Callback<ArrayList<Comics>>() {
            @Override
            public void onResponse(Call<ArrayList<Comics>> call, Response<ArrayList<Comics>> response) {
                if (response.isSuccessful()) {
                    for (Comics comic : response.body()) {
                        saveStatus = comic.getName().equals(comicsName);
                        if (saveStatus) {
                            saveBtn.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_added));
                            saveBtn.setEnabled(false);
                        }
                        Log.e("ASD", saveStatus.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Comics>> call, Throwable t) {

            }
        });
//        Call<Boolean> getStatus = ApiClient.getUserService().getFavoriteStatus(username, comicsName);
//        getStatus.enqueue(new Callback<Boolean>() {
//            @Override
//            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
//                saveStatus = response.body();
//            }
//
//            @Override
//            public void onFailure(Call<Boolean> call, Throwable t) {
//
//            }
//        });
    }

    private String getStringFromGenreName(String genreName) {
        switch (genreName) {
            case "CHILDREN":
                return getString(R.string.genre_name_children);
            case "FANTASTIC":
                return getString(R.string.genre_name_fantastic);
            case "HISTORICAL":
                return getString(R.string.genre_name_historical);
            case "ROMANTIC":
                return getString(R.string.genre_name_romantic);
            case "DRAMA":
                return getString(R.string.genre_name_drama);
            case "ADVENTURES":
                return getString(R.string.genre_name_adventures);
            case "ACTION":
                return getString(R.string.genre_name_action);
            case "DAILY":
                return getString(R.string.genre_name_daily);
            case "COMEDY":
                return getString(R.string.genre_name_comedy);
            default:
                return genreName;
        }
    }
}