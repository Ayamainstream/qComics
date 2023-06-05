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
    Boolean onStudio = Boolean.TRUE;

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
                    tvNumberOfComics.setText(mainComicsAdapterAuthor.getItemCount() + " " + getString(R.string.comics_one));
                } else {
                    onStudio = true;
                    tvSwitchStudio.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    rvAuthorsComics.setVisibility(View.GONE);
                    rvStudioComics.setVisibility(View.VISIBLE);
                    tvSwitchAuthors.setTextColor(ContextCompat.getColor(getContext(), R.color.orange_text));
                    tvNumberOfComics.setText(mainComicsAdapterStudio.getItemCount() + " " + getString(R.string.comics_one));
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
        Call<ArrayList<Comics>> studioComics = ApiClient.getUserService().mapComicsType(studioType, mapRating);
        studioComics.enqueue(new Callback<ArrayList<Comics>>() {
            @Override
            public void onResponse(Call<ArrayList<Comics>> call, Response<ArrayList<Comics>> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    mainComicsAdapterStudio.setData(response.body());
                    Log.e("ASD", "Studio Comics: " + response.body());
                    tempComicsStudio.addAll(response.body());
                    tvNumberOfComics.setText(mainComicsAdapterStudio.getItemCount() + " " + getString(R.string.comics_one));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Comics>> call, Throwable t) {
                Log.e("ASD", "Inside failure " + t.getLocalizedMessage() + " " + t.getMessage());
            }
        });
        RequestType authorType = new RequestType();
        authorType.setType("AUTHOR");
        Call<ArrayList<Comics>> authorComics = ApiClient.getUserService().mapComicsType(authorType, mapRating);
        authorComics.enqueue(new Callback<ArrayList<Comics>>() {
            @Override
            public void onResponse(Call<ArrayList<Comics>> call, Response<ArrayList<Comics>> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    mainComicsAdapterAuthor.setData(response.body());
                    tempComicsAuthor.addAll(response.body());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Comics>> call, Throwable t) {
                Log.e("ASD", "Inside failure " + t.getLocalizedMessage() + " " + t.getMessage());
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        tvNumberOfComics.setText(mainComicsAdapterStudio.getItemCount() + " " + getString(R.string.comics_one));

        rvStudioComics.setLayoutManager(layoutManager);
        rvStudioComics.setAdapter(mainComicsAdapterStudio);

        GridLayoutManager layoutManager2 = new GridLayoutManager(getContext(), 3);
        rvAuthorsComics.setLayoutManager(layoutManager2);
        rvAuthorsComics.setAdapter(mainComicsAdapterAuthor);
    }

    private void setDropdown() {
        itemList = new ArrayList<>(Arrays.asList(
                getString(R.string.genre_name_children),
                getString(R.string.genre_name_fantastic),
                getString(R.string.genre_name_historical),
                getString(R.string.genre_name_romantic),
                getString(R.string.genre_name_drama),
                getString(R.string.genre_name_adventures),
                getString(R.string.genre_name_action),
                getString(R.string.genre_name_daily),
                getString(R.string.genre_name_comedy)
        ));

        sortOptions = new ArrayList<>();
        sortOptions.add(getString(R.string.default_string));
        sortOptions.add(getString(R.string.sort_option_high_rating));
        sortOptions.add(getString(R.string.sort_option_new));

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
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
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
                if (selectedSortOption.equals(getString(R.string.sort_option_high_rating))) {
                    sortComicsByRating(mainComicsAdapterStudio.getData());
                    sortComicsByRating(mainComicsAdapterAuthor.getData());
                } else if (selectedSortOption.equals(getString(R.string.sort_option_new))) {
                    sortComicsByPublishedDate(mainComicsAdapterStudio.getData());
                    sortComicsByPublishedDate(mainComicsAdapterAuthor.getData());
                } else if (selectedSortOption.equals(getString(R.string.default_string))) {
                    mainComicsAdapterStudio.setData(tempComicsStudio);
                    mainComicsAdapterAuthor.setData(tempComicsAuthor);
                }
                mainComicsAdapterAuthor.notifyDataSetChanged();
                mainComicsAdapterStudio.notifyDataSetChanged();
                sortDropdown.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                sortPreview.setText(selectedSortOption);
            }
        });

    }

    private void filterComics() {
        ArrayList<Comics> filteredComicsStudio = new ArrayList<>();
        ArrayList<Comics> filteredComicsAuthors = new ArrayList<>();

        if (selectedGenres.isEmpty()) {
            // No genres selected, show all comics
            mainComicsAdapterStudio.setData(tempComicsStudio);
            mainComicsAdapterAuthor.setData(tempComicsAuthor);
            if (onStudio)
                tvNumberOfComics.setText(mainComicsAdapterStudio.getItemCount() + " " + getString(R.string.comics));
            else
                tvNumberOfComics.setText(mainComicsAdapterAuthor.getItemCount() + " " + getString(R.string.comics));
        } else {
            Log.e("else", selectedGenres.toString());
            // Filter comics based on selected genres
            for (Comics comic : tempComicsStudio) {
                boolean shouldAdd = true;
                for (String selectedGenre : selectedGenres) {
                    if (!comic.getGenres().contains(getStringFromGenreName(selectedGenre))) {
                        shouldAdd = false;
                        break;
                    }
                }
                if (shouldAdd) {
                    filteredComicsStudio.add(comic);
                }
            }
            for (Comics comic : tempComicsAuthor) {
                boolean shouldAdd = true;
                for (String selectedGenre : selectedGenres) {
                    if (!comic.getGenres().contains(getStringFromGenreName(selectedGenre))) {
                        shouldAdd = false;
                        break;
                    }
                }
                if (shouldAdd) {
                    filteredComicsAuthors.add(comic);
                }
            }

            mainComicsAdapterStudio.setData(filteredComicsStudio);
            mainComicsAdapterAuthor.setData(filteredComicsAuthors);
        }
        Log.e("ASD", onStudio.toString());

        if (!filteredComicsStudio.isEmpty() || !filteredComicsAuthors.isEmpty()) {
            if (onStudio)
                tvNumberOfComics.setText(filteredComicsStudio.size() + " " + getString(R.string.comics));
            else
                tvNumberOfComics.setText(filteredComicsAuthors.size() + " " + getString(R.string.comics));
        } else {
            if (!selectedGenres.isEmpty())
                tvNumberOfComics.setText("0" + " " + getString(R.string.comics_one));
        }

        mainComicsAdapterStudio.notifyDataSetChanged();
        mainComicsAdapterAuthor.notifyDataSetChanged();
    }

    private String getStringFromGenreName(String genreName) {
        if (genreName.equals(getString(R.string.genre_name_children))) {
            return "CHILDREN";
        } else if (genreName.equals(getString(R.string.genre_name_fantastic))) {
            return "FANTASTIC";
        } else if (genreName.equals(getString(R.string.genre_name_historical))) {
            return "HISTORICAL";
        } else if (genreName.equals(getString(R.string.genre_name_romantic))) {
            return "ROMANTIC";
        } else if (genreName.equals(getString(R.string.genre_name_drama))) {
            return "DRAMA";
        } else if (genreName.equals(getString(R.string.genre_name_adventures))) {
            return "ADVENTURES";
        } else if (genreName.equals(getString(R.string.genre_name_action))) {
            return "ACTION";
        } else if (genreName.equals(getString(R.string.genre_name_daily))) {
            return "DAILY";
        } else if (genreName.equals(getString(R.string.genre_name_comedy))) {
            return "COMEDY";
        } else {
            return genreName;
        }
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