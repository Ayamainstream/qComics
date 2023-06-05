package com.example.qComics.ui.main.comics;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.qComics.data.network.ApiClient;
import com.example.qComics.data.network.comics.Comics;
import com.example.qComics.ui.main.adapters.SearchAdapter;
import com.example.qComics.ui.main.adapters.MainComicsAdapter;
import com.example.q_comics.R;
import com.example.q_comics.databinding.FragmentSearchBinding;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private TextView tvNumberOfComics, sortPreview;
    EditText searchView;
    ImageView backBtn, searchBtn;
    AutoCompleteTextView filterDropdown, sortDropdown;
    FlexboxLayout customLayout;
    RecyclerView rvSearchComics;
    MainComicsAdapter mainComicsAdapter;
    ArrayList<Comics> tempComics = new ArrayList<>();
    PopupWindow popupWindow;

    private List<String> itemList, sortOptions;
    private ArrayAdapter<String> adapterFilter, adapterSort;
    private ArrayList<String> selectedGenres = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false);

        filterDropdown = binding.filter;
        sortDropdown = binding.sort;
        customLayout = binding.customLayout;
        tvNumberOfComics = binding.numberOfComics;
        rvSearchComics = binding.rvSearchComics;
        sortPreview = binding.sortPreview;
        backBtn = binding.backBtn;
        searchView = binding.searchView;
        searchBtn = binding.searchButton;

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!searchView.getText().toString().isEmpty())
                    search();
            }
        });

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchQuery = s.toString().trim();
                if (!searchQuery.isEmpty()) {
                    ArrayList<Comics> searchResults = mainComicsAdapter.getSearchResults(searchQuery);
                    if (!searchResults.isEmpty()) {
                        showSearchResultsDialog(searchResults);
                    } else {
                        if (popupWindow != null && popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }
                    }
                } else {
                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                }
            }
        });

        setAllComics();
        setDropdown();
        return binding.getRoot();
    }

    private void search() {
        mainComicsAdapter.setData(mainComicsAdapter.getSearchResults(String.valueOf(searchView.getText())));
        mainComicsAdapter.notifyDataSetChanged();
        tvNumberOfComics.setText(mainComicsAdapter.getItemCount() + " " + getString(R.string.comics_one));
    }

    private void showSearchResultsDialog(ArrayList<Comics> searchResults) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_searchible_spinner, null);
        popupWindow = new PopupWindow(dialogView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        SearchAdapter searchResultsAdapter = new SearchAdapter(requireActivity(), popupWindow);
        searchResultsAdapter.setData(searchResults);


        // Set up the RecyclerView and its layout manager
        RecyclerView searchResultsRecyclerView = dialogView.findViewById(R.id.rv_search_result);
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchResultsRecyclerView.setAdapter(searchResultsAdapter);

        // Create and show the popup window
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);

        // Calculate the x and y offsets for the popup window
        int[] location = new int[2];
        searchView.getLocationInWindow(location);
        popupWindow.showAsDropDown(searchView, 0, 0);
    }

    private void setAllComics() {
        mainComicsAdapter = new MainComicsAdapter(requireContext());

        Call<ArrayList<Comics>> getAllComics = ApiClient.getUserService().getAllComics();
        getAllComics.enqueue(new Callback<ArrayList<Comics>>() {
            @Override
            public void onResponse(Call<ArrayList<Comics>> call, Response<ArrayList<Comics>> response) {
                if (response.isSuccessful()) {
                    mainComicsAdapter.setData(response.body());
                    tempComics.addAll(response.body());
                    tvNumberOfComics.setText(mainComicsAdapter.getItemCount() + " " + getString(R.string.comics_one));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Comics>> call, Throwable t) {
                Log.e("ASD","Inside failure "+ t.getLocalizedMessage()+" "+t.getMessage());
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        tvNumberOfComics.setText(mainComicsAdapter.getItemCount() + " " + getString(R.string.comics_one));

        rvSearchComics.setLayoutManager(layoutManager);
        rvSearchComics.setAdapter(mainComicsAdapter);

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
        sortOptions.add(getString(R.string.all));
        sortOptions.add(getString(R.string.studio));
        sortOptions.add(getString(R.string.authors));

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

                // Add the selected genre to the list of selected genres
                selectedGenres.add(selectedItem);

                // Filter the comics by the selected genres and update the adapter with the filtered results
                customLayout.addView(tv);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemList.add(selectedItem);
                        Log.e("onclick", selectedGenres.toString());
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
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedSortOption = (String) parent.getItemAtPosition(position);
                mainComicsAdapter.setData(tempComics);
                mainComicsAdapter.notifyDataSetChanged();
                if (selectedSortOption.equals(getString(R.string.all))) {
                    tvNumberOfComics.setText(mainComicsAdapter.getItemCount() + " " + getString(R.string.comics_one));
                }
                else
                    filterDataByType(selectedSortOption);
                sortDropdown.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                sortPreview.setText(selectedSortOption);
            }
        });

    }

    private void filterComics() {
        ArrayList<Comics> filteredComics = new ArrayList<>();
        Log.e("filter", selectedGenres.toString());
        if (selectedGenres.isEmpty()) {
            // No genres selected, show all comics
            mainComicsAdapter.setData(tempComics);
            tvNumberOfComics.setText(mainComicsAdapter.getItemCount() + " " + getString(R.string.comics_one));
        } else {
            Log.e("else", selectedGenres.toString());
            // Filter comics based on selected genres
            for (Comics comic : tempComics) {
                boolean shouldAdd = true;
                for (String selectedGenre : selectedGenres) {
                    if (!comic.getGenres().contains(getStringFromGenreName(selectedGenre))) {
                        shouldAdd = false;
                        break;
                    }
                }
                if (shouldAdd) {
                    filteredComics.add(comic);
                }
            }
            mainComicsAdapter.setData(filteredComics);
            tvNumberOfComics.setText(filteredComics.size() + " " + getString(R.string.comics_one));
        }
        mainComicsAdapter.notifyDataSetChanged();
    }


    public void filterDataByType(String query) {
        if (query.equals(getString(R.string.studio)))
            query = "STUDIO";
        else
            query = "AUTHOR";
        ArrayList<Comics> filteredData = new ArrayList<>();
        for (Comics comic : mainComicsAdapter.getData()) {
            if (comic.getType().equals(query)) {
                filteredData.add(comic);
            }
        }
        mainComicsAdapter.setData(filteredData);
        tvNumberOfComics.setText(mainComicsAdapter.getItemCount() + " " + getString(R.string.comics_one));
        mainComicsAdapter.notifyDataSetChanged();
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

}