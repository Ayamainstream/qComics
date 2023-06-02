package com.example.qComics.ui.main.home;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.qComics.data.network.ApiClient;
import com.example.qComics.data.network.comics.Comics;
import com.example.qComics.data.network.comics.Filter;
import com.example.qComics.ui.auth.AuthenticationActivity;
import com.example.qComics.ui.base.BaseActivity;
import com.example.qComics.ui.main.adapters.MainComicsAdapter;
import com.example.qComics.ui.main.adapters.NewComicsAdapter;
import com.example.qComics.ui.main.adapters.SliderAdapter;
import com.example.qComics.ui.main.adapters.TopComicsAdapter;
import com.example.qComics.ui.main.comics.SearchFragment;
import com.example.q_comics.R;
import com.example.q_comics.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ViewPager2 viewPager2;
    private LinearLayout layoutIndicator;
    private SliderAdapter sliderAdapter;
    private RecyclerView rvRecentUpdates, rvNews, rvTops;
    private TextView user;
    private ImageView userIcon, searchBtn;
    private SharedPreferences prefs;
    private final Handler sliderHandler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        viewPager2 = binding.viewPagerImageSlider;
        layoutIndicator = binding.layoutIndicator;
        rvRecentUpdates = binding.rvRecentUpdates;
        rvNews = binding.rvNews;
        rvTops = binding.topComics;
        user = binding.username;
        userIcon = binding.userIcon;
        searchBtn = binding.searchButton;

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BaseActivity) requireActivity()).addFragment(new SearchFragment());
            }
        });

        setUserName();
        slider();
        setupComics();
        return binding.getRoot();
    }

    private void setUserName() {
        prefs = requireActivity().getSharedPreferences("DeviceToken", MODE_PRIVATE);
        String username = prefs.getString("userName", null);
        String password = prefs.getString("password", null);

        if (username != null && password != null) {
            user.setText(username);
            userIcon.setVisibility(View.VISIBLE);

            //temporary logout
            user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logout();
                    startActivity(new Intent(getActivity(), AuthenticationActivity.class));
                }
            });
        } else {
            user.setText(R.string.login_or_register);
            userIcon.setVisibility(View.GONE);
            setMarginLeft(user);

            user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), AuthenticationActivity.class));
                }
            });
        }
    }

    private void logout() {
        prefs = requireActivity().getSharedPreferences("DeviceToken", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("username");
        editor.remove("password");
        editor.apply();
    }

    private void setMarginLeft(TextView textView) {
        float dpValue = 20; // 20dp
        float pxValue = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
        int marginStartInPx = (int) pxValue; // change this to the desired value in pixels
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) this.user.getLayoutParams();
        params.setMarginStart(marginStartInPx);
        textView.setLayoutParams(params);
    }

    private void setupComics() {
        MainComicsAdapter mainComicsAdapter = new MainComicsAdapter(requireActivity());
        NewComicsAdapter newComicsAdapter = new NewComicsAdapter(requireContext());
        TopComicsAdapter topComicsAdapter = new TopComicsAdapter(requireContext());

        Call<ArrayList<Comics>> getAllComics = ApiClient.getUserService().getAllComics();
        getAllComics.enqueue(new Callback<ArrayList<Comics>>() {
            @Override
            public void onResponse(Call<ArrayList<Comics>> call, Response<ArrayList<Comics>> response) {
                mainComicsAdapter.setData(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<Comics>> call, Throwable t) {
                Log.e("ASD","inside failure "+ t.getLocalizedMessage());
            }
        });

        Call<ArrayList<Comics>> newComics = ApiClient.getUserService().filteredComics(createFilteredRequest("publishedDate", true, 0, 5));
        newComics.enqueue(new Callback<ArrayList<Comics>>() {
            @Override
            public void onResponse(Call<ArrayList<Comics>> call, Response<ArrayList<Comics>> response) {
                newComicsAdapter.setData(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<Comics>> call, Throwable t) {
                Log.e("ASD","inside failure "+ t.getLocalizedMessage());
            }
        });

        Call<ArrayList<Comics>> topComics = ApiClient.getUserService().filteredComics(createFilteredRequest("rating", false, 0, 10));
        topComics.enqueue(new Callback<ArrayList<Comics>>() {
            @Override
            public void onResponse(Call<ArrayList<Comics>> call, Response<ArrayList<Comics>> response) {
                topComicsAdapter.setData(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<Comics>> call, Throwable t) {
                Log.e("ASD","inside failure "+ t.getLocalizedMessage()+" "+t.getMessage());
            }
        });
        setRecyclerView(rvRecentUpdates, mainComicsAdapter, LinearLayoutManager.HORIZONTAL);
        setRecyclerView(rvNews, newComicsAdapter, LinearLayoutManager.HORIZONTAL);
        setRecyclerView(rvTops, topComicsAdapter, LinearLayoutManager.VERTICAL);
    }

    public Filter createFilteredRequest(String field, Boolean asc, Integer page, Integer size) {
        Filter filteredRequest = new Filter();
        filteredRequest.setField(field);
        filteredRequest.setAscending(asc);
        filteredRequest.setPage(page);
        filteredRequest.setSize(size);
        return filteredRequest;
    }

    private void setRecyclerView(RecyclerView recyclerView, RecyclerView.Adapter adapter, int orientation) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(orientation);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void slider() {
        List<SliderItem> sliderItems = new ArrayList<>();

        //hardcode
        sliderItems.add(new SliderItem(R.drawable.banner_1));
        sliderItems.add(new SliderItem(R.drawable.banner_2));
        sliderItems.add(new SliderItem(R.drawable.banner_3));
        sliderItems.add(new SliderItem(R.drawable.banner_4));
        //

        sliderAdapter = new SliderAdapter(sliderItems, viewPager2);
        viewPager2.setAdapter(sliderAdapter);

        setupIndicator();
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(20));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
        });

        viewPager2.setPageTransformer(compositePageTransformer);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000);
            }
        });
    }

    private void setupIndicator() {
        ImageView[] indicators = new ImageView[sliderAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(8, 0, 8, 0);
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getContext());
            indicators[i].setImageResource(R.drawable.indicator_inactive);
            indicators[i].setLayoutParams(layoutParams);
            layoutIndicator.addView(indicators[i]);
        }
        indicators[0].setImageResource(R.drawable.indicator_active);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                for (int i = 0; i < indicators.length; i++) {
                    indicators[i].setImageResource(i == position ? R.drawable.indicator_active : R.drawable.indicator_inactive);
                }
            }
        });
    }

    private final Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }
}