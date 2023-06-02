package com.example.qComics.ui.main.comics;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qComics.data.network.ApiClient;
import com.example.qComics.data.network.comics.Comics;
import com.example.qComics.data.network.comics.Map;
import com.example.qComics.data.network.comics.RequestType;
import com.example.qComics.ui.base.BaseActivity;
import com.example.qComics.ui.main.adapters.MainComicsAdapter;
import com.example.q_comics.R;
import com.example.q_comics.databinding.FragmentComicsBinding;
import com.google.android.flexbox.FlexboxLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComicsFragment extends Fragment {

    private FragmentComicsBinding binding;
    private TextView tvSwitchStudio, tvSwitchAuthors, tvNumberOfComics, sortPreview;
    private SwitchCompat switchCompat;
    ImageView searchBtn;
    AutoCompleteTextView filterDropdown, sortDropdown;
    FlexboxLayout customLayout;
    RecyclerView rvStudioComics, rvAuthorsComics;
    MainComicsAdapter mainComicsAdapterStudio, mainComicsAdapterAuthor;
    ArrayList<Comics> tempComicsStudio, tempComicsAuthor;
    Boolean onStudio = false;

    private List<String> itemList, sortOptions;
    private ArrayAdapter<String> adapterFilter, adapterSort;
    private ArrayList<String> selectedGenres = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentComicsBinding.inflate(inflater, container, false);

        filterDropdown = binding.filter;
        sortDropdown = binding.sort;
        customLayout = binding.customLayout;
        tvSwitchStudio = binding.tvSwitchStudio;
        tvSwitchAuthors = binding.tvSwitchAuthors;
        tvNumberOfComics = binding.numberOfComics;
        switchCompat = binding.switcher;
        rvStudioComics = binding.rvStudioComics;
        rvAuthorsComics = binding.rvAuthorsComics;
        sortPreview = binding.sortPreview;
        searchBtn = binding.searchButton;
        tempComicsStudio = new ArrayList<>();
        tempComicsAuthor = new ArrayList<>();

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    onStudio = false;
                    tvSwitchStudio.setTextColor(ContextCompat.getColor(getContext(), R.color.orange_text));
                    rvStudioComics.setVisibility(View.GONE);
                    rvAuthorsComics.setVisibility(View.VISIBLE);
                    tvSwitchAuthors.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    tvNumberOfComics.setText(mainComicsAdapterAuthor.getItemCount() + " комиксов");
                } else {
                    onStudio = true;
                    tvSwitchStudio.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    rvAuthorsComics.setVisibility(View.GONE);
                    rvStudioComics.setVisibility(View.VISIBLE);
                    tvSwitchAuthors.setTextColor(ContextCompat.getColor(getContext(), R.color.orange_text));
                    tvNumberOfComics.setText(mainComicsAdapterStudio.getItemCount() + " комиксов");
                }
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BaseActivity) requireActivity()).addFragment(new SearchFragment());
            }
        });

        setAllComics();
        setDropdown();
        return binding.getRoot();
    }

    private void setAllComics() {
        mainComicsAdapterStudio = new MainComicsAdapter(requireContext());
        mainComicsAdapterAuthor = new MainComicsAdapter(requireContext());

        RequestType studioType = new RequestType();
        studioType.setType("STUDIO");
        Map mapRating = new Map();
        mapRating.setPage(0);
        mapRating.setSize(100);
        mapRating.setSort("publishedDate");
        Call<ArrayList<Comics>> studioComics = ApiClient.getUserService().mapComics(studioType, mapRating);
        studioComics.enqueue(new Callback<ArrayList<Comics>>() {
            @Override
            public void onResponse(Call<ArrayList<Comics>> call, Response<ArrayList<Comics>> response) {
                assert response.body() != null;
                mainComicsAdapterStudio.setData(response.body());
                Log.e("ASD", "Studio Comics: "+response.body());
                tempComicsStudio.addAll(response.body());
                tvNumberOfComics.setText(mainComicsAdapterStudio.getItemCount() + " комиксов");
            }

            @Override
            public void onFailure(Call<ArrayList<Comics>> call, Throwable t) {
                Log.e("ASD","Inside failure "+ t.getLocalizedMessage()+" "+t.getMessage());
            }
        });
        RequestType authorType = new RequestType();
        authorType.setType("AUTHOR");
        Call<ArrayList<Comics>> authorComics = ApiClient.getUserService().mapComics(authorType, mapRating);
        authorComics.enqueue(new Callback<ArrayList<Comics>>() {
            @Override
            public void onResponse(Call<ArrayList<Comics>> call, Response<ArrayList<Comics>> response) {
                mainComicsAdapterAuthor.setData(response.body());
                tempComicsAuthor.addAll(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<Comics>> call, Throwable t) {
                Log.e("ASD","Inside failure "+ t.getLocalizedMessage()+" "+t.getMessage());
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        tvNumberOfComics.setText(mainComicsAdapterStudio.getItemCount() + " комиксов");

        rvStudioComics.setLayoutManager(layoutManager);
        rvStudioComics.setAdapter(mainComicsAdapterStudio);

        GridLayoutManager layoutManager2 = new GridLayoutManager(getContext(), 3);
        rvAuthorsComics.setLayoutManager(layoutManager2);
        rvAuthorsComics.setAdapter(mainComicsAdapterAuthor);
    }

    private void setDropdown() {
        itemList = new ArrayList<>(Arrays.asList(
                "CHILDREN",
                "FANTASTIC",
                "HISTORICAL",
                "ROMANTIC",
                "DRAMA",
                "ADVENTURES",
                "ACTION",
                "DAILY",
                "COMEDY"
        ));

        sortOptions = new ArrayList<>();
        sortOptions.add("По высокому рейтингу");
        sortOptions.add("По новизне");

        adapterFilter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_dropdown_item_1line, itemList);
        filterDropdown.setAdapter(adapterFilter);

        adapterSort = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_dropdown_item_1line, sortOptions);
        sortDropdown.setAdapter(adapterSort);

        filterDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDropdown.showDropDown();
                filterDropdown.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
            }
        });

        sortDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortDropdown.showDropDown();
                sortDropdown.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
            }
        });

        filterDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final TextView tv = new TextView(requireActivity());
                String selectedItem = (String) parent.getItemAtPosition(position);
                tv.setText(selectedItem + "    ✕  ");
                tv.setHeight(70);
                tv.setTextSize(12.0f);
                tv.setGravity(Gravity.CENTER_VERTICAL);
                tv.setTextColor(Color.parseColor("#FFFFFFFF"));
                tv.setBackground(getResources().getDrawable(R.drawable.bg_genre_item));
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(10, 2, 2, 2);
                tv.setLayoutParams(layoutParams);
                tv.setPadding(35, 5, 5, 5);
                itemList.remove(selectedItem);
                adapterFilter.notifyDataSetChanged();
                selectedGenres.add(selectedItem);

                // Filter the comics by the selected genres and update the adapter with the filtered results
                customLayout.addView(tv);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemList.add(selectedItem);
                        selectedGenres.remove(selectedItem);
                        customLayout.removeView(tv);
                        filterComics();
                    }
                });
                filterComics();
                filterDropdown.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
            }
        });

        sortDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedSortOption = (String) parent.getItemAtPosition(position);
                if (selectedSortOption.equals("По высокому рейтингу")){
                    sortComicsByRating(mainComicsAdapterStudio.getData());
                    sortComicsByRating(mainComicsAdapterAuthor.getData());
                } else if (selectedSortOption.equals("По популярности")){
                    sortComicsByPublishedDate(mainComicsAdapterStudio.getData());
                    sortComicsByPublishedDate(mainComicsAdapterAuthor.getData());
                }
                mainComicsAdapterAuthor.notifyDataSetChanged();
                mainComicsAdapterStudio.notifyDataSetChanged();
                sortDropdown.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                sortPreview.setText(selectedSortOption);
            }
        });

    }

    private void filterComics() {
        ArrayList<Comics> filteredComics1 = new ArrayList<>();
        ArrayList<Comics> filteredComics2 = new ArrayList<>();
        Log.e("filter", selectedGenres.toString());
        if (selectedGenres.isEmpty()) {
            // No genres selected, show all comics
            mainComicsAdapterStudio.setData(tempComicsStudio);
            mainComicsAdapterAuthor.setData(tempComicsAuthor);
            if (onStudio)
                tvNumberOfComics.setText(mainComicsAdapterStudio.getItemCount() + " комиксов");
            else
                tvNumberOfComics.setText(mainComicsAdapterAuthor.getItemCount() + " комиксов");
        } else {
            Log.e("else", selectedGenres.toString());
            // Filter comics based on selected genres
            for (Comics comic : tempComicsStudio) {
                boolean shouldAdd = true;
                for (String selectedGenre : selectedGenres) {
                    if (!comic.getGenres().contains(selectedGenre)) {
                        shouldAdd = false;
                        break;
                    }
                }
                if (shouldAdd) {
                    filteredComics1.add(comic);
                }
            }
            for (Comics comic : tempComicsAuthor) {
                boolean shouldAdd = true;
                for (String selectedGenre : selectedGenres) {
                    if (!comic.getGenres().contains(selectedGenre)) {
                        shouldAdd = false;
                        break;
                    }
                }
                if (shouldAdd) {
                    filteredComics2.add(comic);
                }
            }
            Log.e("ASD", "1 -----"+ filteredComics1);
            Log.e("ASD", "2 -----"+ filteredComics2);
            mainComicsAdapterStudio.setData(filteredComics1);
            mainComicsAdapterAuthor.setData(filteredComics2);
            Log.e("ASD", onStudio.toString());
        }
        if (!filteredComics1.isEmpty() || !filteredComics2.isEmpty()) {
            if (onStudio)
                tvNumberOfComics.setText(filteredComics1.size() + " комиксов");
            else
                tvNumberOfComics.setText(filteredComics2.size() + " комиксов");
        }
        mainComicsAdapterStudio.notifyDataSetChanged();
        mainComicsAdapterAuthor.notifyDataSetChanged();
    }

    private void sortComicsByRating(ArrayList<Comics> comics) {
        Collections.sort(comics, new Comparator<Comics>() {
            @Override
            public int compare(Comics c1, Comics c2) {
                return Float.compare(c2.getRating(), c1.getRating());
            }
        });
    }

    private void sortComicsByPublishedDate(ArrayList<Comics> comics) {
        Collections.sort(comics, new Comparator<Comics>() {
            @Override
            public int compare(Comics c1, Comics c2) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                try {
                    Date date1 = dateFormat.parse(c1.getPublishedDate());
                    Date date2 = dateFormat.parse(c2.getPublishedDate());
                    return date2.compareTo(date1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }

}