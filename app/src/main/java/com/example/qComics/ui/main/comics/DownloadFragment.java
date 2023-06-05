package com.example.qComics.ui.main.comics;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qComics.data.network.ApiClient;
import com.example.qComics.data.network.comics.Chapter;
import com.example.qComics.ui.main.adapters.DownloadAdapter;
import com.example.q_comics.R;
import com.example.q_comics.databinding.FragmentDownloadBinding;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownloadFragment extends Fragment {

    private FragmentDownloadBinding binding;
    ImageView closeBtn;
    CheckBox chooseAll;
    RecyclerView rvDownloadComics;
    TextView tvDownload, tvNumberOfComics;
    LinearLayout bottomBar;
    String comicsName, username;
    DownloadAdapter downloadAdapter;
    ArrayList<Integer> selectedIds;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDownloadBinding.inflate(inflater, container, false);
        closeBtn = binding.closeBtn;
//        chooseAll = binding.chooseAll;
        rvDownloadComics = binding.rvDownloadComics;
        tvDownload = binding.tvDownload;
        tvNumberOfComics = binding.tvNumberOfComics;
        bottomBar = binding.bottomBar;

        SharedPreferences prefs = getActivity().getSharedPreferences("DeviceToken", MODE_PRIVATE);
//        username = prefs.getString("userName", null);
        comicsName = prefs.getString("comicsName", "");

        initRecyclerView();
        bottomBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadSelectedChapters();
                tvDownload.setText(getText(R.string.downloading));
            }
        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

        return binding.getRoot();
    }

    private void initRecyclerView() {
        downloadAdapter = new DownloadAdapter(requireActivity(), bottomBar);

        Call<ArrayList<Chapter>> getChapters = ApiClient.getUserService().getChapters(comicsName);
        getChapters.enqueue(new Callback<ArrayList<Chapter>>() {
            @Override
            public void onResponse(Call<ArrayList<Chapter>> call, Response<ArrayList<Chapter>> response) {
                if (response.isSuccessful())
                    downloadAdapter.setData(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<Chapter>> call, Throwable t) {

            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvDownloadComics.setLayoutManager(layoutManager);
        rvDownloadComics.setAdapter(downloadAdapter);
    }

    private void downloadSelectedChapters() {
        selectedIds = downloadAdapter.getSelectedIds();
        final int[] totalItems = {selectedIds.size()};
        final int[] downloadedItems = {0};

        for (int chapterId : selectedIds) {
            // Find the chapter object by its ID
            Chapter selectedChapter = findChapterById(chapterId);
            if (selectedChapter == null) {
                continue; // Skip to the next iteration if chapter not found
            }

            String chapterName = selectedChapter.getName();
            String comicName = selectedChapter.getComicName();

            // Make the download request using the retrieved chapter name
            Call<ResponseBody> downloadChapter = ApiClient.getUserService().downloadChapter(chapterName, comicName);
            downloadChapter.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        try {
                            File file = new File(Environment
                                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                                    comicName + "_" + chapterName + ".zip");
                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                            IOUtils.write(response.body().bytes(), fileOutputStream);

                            downloadedItems[0]++;

                            // Update the progress TextView
                            final String progress = downloadedItems[0] + "/" + totalItems[0];
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateProgressText(progress);
                                }
                            });

                            // Check if all items have been downloaded
                            if (downloadedItems[0] == totalItems[0]) {
                                // Animation completed
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(requireActivity(), "Done!", Toast.LENGTH_LONG).show();
                                        requireActivity().onBackPressed();
                                    }
                                });
                            }
                        } catch (Exception ex) {
                            Log.e("ASD", ex.getLocalizedMessage());
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    // Handle failure
                    downloadedItems[0]++;

                    // Update the progress TextView
                    final String progress = downloadedItems[0] + "/" + totalItems[0];
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateProgressText(progress);
                        }
                    });

                    // Check if all items have been downloaded
                    if (downloadedItems[0] == totalItems[0]) {
                        // Animation completed
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Perform any final animation completion actions
                            }
                        });
                    }
                }
            });
        }
    }

    private Chapter findChapterById(int chapterId) {
        for (Chapter chapter : downloadAdapter.getDataModel()) {
            if (chapter.getId() == chapterId) {
                return chapter;
            }
        }
        return null;
    }

    private void updateProgressText(String progress) {
        // Update the progress TextView with the current progress
        TextView progressText = binding.progressText;
        progressText.setText(progress);

        // Perform your custom animation here
        // Replace the code below with your animation logic
        // This example animates the progressText by scaling it up and down repeatedly
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 1.5f, 1f, 1.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setRepeatCount(Animation.INFINITE);
        scaleAnimation.setRepeatMode(Animation.REVERSE);
        scaleAnimation.setDuration(500);
        progressText.startAnimation(scaleAnimation);
    }

}