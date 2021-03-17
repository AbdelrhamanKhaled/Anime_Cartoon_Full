package com.app.cartoons.anime.newappcartoons.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.app.cartoons.anime.newappcartoons.R;
import com.app.cartoons.anime.newappcartoons.activities.MainActivity;
import com.app.cartoons.anime.newappcartoons.activities.PlayListsActivity;
import com.app.cartoons.anime.newappcartoons.app.Config;
import com.app.cartoons.anime.newappcartoons.databinding.LayoutNativeAdBinding;
import com.app.cartoons.anime.newappcartoons.databinding.LayoutRecyclercartoonItemBinding;
import com.app.cartoons.anime.newappcartoons.model.Cartoon;

import java.util.ArrayList;
import java.util.List;

import static com.app.cartoons.anime.newappcartoons.activities.MainActivity.searchCase;

public class CartoonsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements Filterable {

    private final String TAG = CartoonsAdapter.class.getSimpleName();
    private Context mContext;
    private List<Cartoon> cartoonList;
    private List<Cartoon> cartoonListFiltered;

    private final int albumView = 1;
    private final int nativeAdView = 2;

    public CartoonsAdapter(Context mContext, List<Cartoon> cartoonList) {
        this.mContext = mContext;
        this.cartoonList = cartoonList;
        this.cartoonListFiltered = cartoonList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == nativeAdView){
            LayoutNativeAdBinding binding = DataBindingUtil.inflate(
                    LayoutInflater.from(mContext), R.layout.layout_native_ad, parent, false);

            return new NativeAdHolder(binding.getRoot());
        }

        LayoutRecyclercartoonItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(mContext), R.layout.layout_recyclercartoon_item, parent, false);

        return new CartoonHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        if(holder.getItemViewType() == nativeAdView){
            NativeAdHolder nativeAdHolder = (NativeAdHolder) holder;
            Config.loadNativeAd(mContext, nativeAdHolder.mBinding.nativeAdTemplate);
        }
        else{
            CartoonHolder cartoonHolder = (CartoonHolder) holder;
            final Cartoon cartoon = cartoonListFiltered.get(position);
            cartoonHolder.mBinding.setCartoon(cartoon);

            cartoonHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PlayListsActivity.class);
                    intent.putExtra("cartoon", cartoon);
                    mContext.startActivity(intent);
                }
            });
        }

        if(position == cartoonList.size() - 1 && !searchCase){
            ((MainActivity)mContext).getNewCartoons();
        }
    }


    @Override
    public int getItemCount() {
        return cartoonListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                if(TextUtils.isEmpty(charSequence)){
                    cartoonListFiltered = cartoonList;
                }else{

                    List<Cartoon> filteredList = new ArrayList<>();
                    for(Cartoon cartoon : cartoonList){

                        if(cartoon.getTitle().toLowerCase().contains(charSequence.toString().toLowerCase()))
                            filteredList.add(cartoon);

                    }

                    cartoonListFiltered = filteredList;

                }


                FilterResults filterResults = new FilterResults();
                filterResults.values = cartoonListFiltered;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                cartoonListFiltered = (ArrayList<Cartoon>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemViewType(int position) {
        if(cartoonList.get(position).getId() == 0){
            return nativeAdView;
        }

        return albumView;
    }

    public static class CartoonHolder extends RecyclerView.ViewHolder{

        LayoutRecyclercartoonItemBinding mBinding;

        public CartoonHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
        }
    }

    public static class NativeAdHolder extends RecyclerView.ViewHolder{

        LayoutNativeAdBinding mBinding;

        NativeAdHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
        }
    }
}
