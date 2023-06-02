package com.example.qComics.ui.main.adapters;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.qComics.ui.main.home.SliderItem;
import com.example.q_comics.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    private final List<SliderItem> sliderItems;
    private final Handler handler = new Handler();
    private boolean slideBackToFirstPage;

    public SliderAdapter(List<SliderItem> sliderItems, ViewPager2 viewPager2) {
        this.sliderItems = sliderItems;

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == getItemCount() - 1) {
                    slideBackToFirstPage = true;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            viewPager2.setCurrentItem(0, true);
                            slideBackToFirstPage = false;
                        }
                    }, 3000);
                } else {
                    slideBackToFirstPage = false;
                }
            }
        });
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         return new SliderViewHolder(
                 LayoutInflater.from(parent.getContext()).inflate(
                         R.layout.slider_item_container,
                         parent,
                         false
                 )
         );
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        holder.setImage(sliderItems.get(position));
    }

    @Override
    public int getItemCount() {
        return sliderItems.size();
    }

    static class SliderViewHolder extends RecyclerView.ViewHolder{

        private final RoundedImageView roundedImageView;

        SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            roundedImageView = itemView.findViewById(R.id.image_slide);
        }

        void setImage(SliderItem sliderItem){
            roundedImageView.setImageResource(sliderItem.getImage() );
        }
    }

}
