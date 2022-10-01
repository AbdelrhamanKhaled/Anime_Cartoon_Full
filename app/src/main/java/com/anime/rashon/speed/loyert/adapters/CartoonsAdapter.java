package com.anime.rashon.speed.loyert.adapters;

import static com.anime.rashon.speed.loyert.activities.MainActivity.searchCase;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.anime.rashon.speed.loyert.R;
import com.anime.rashon.speed.loyert.activities.InformationActivity;
import com.anime.rashon.speed.loyert.activities.MainActivity;
import com.anime.rashon.speed.loyert.app.Config;
import com.anime.rashon.speed.loyert.databinding.LayoutNativeAdBinding;
import com.anime.rashon.speed.loyert.databinding.LayoutRecyclercartoonItemBinding;
import com.anime.rashon.speed.loyert.databinding.LayoutRecyclercartoonItemListBinding;
import com.anime.rashon.speed.loyert.model.Cartoon;
import com.anime.rashon.speed.loyert.model.CartoonWithInfo;

import java.util.ArrayList;
import java.util.List;

public class CartoonsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements Filterable {

    private final String TAG = CartoonsAdapter.class.getSimpleName();
    private Activity mContext;
    private List<CartoonWithInfo> cartoonList;
    private List<CartoonWithInfo> cartoonListFiltered;

    private final int albumView = 1;
    private final int nativeAdView = 2;

    private boolean isGrid ;
    private boolean isEpisodesDates ;

    public CartoonsAdapter(Activity mContext, List<CartoonWithInfo> cartoonList , boolean isGrid , boolean isEpisodesDates) {
        this.mContext = mContext;
        this.cartoonList = cartoonList;
        this.cartoonListFiltered = cartoonList;
        this.isGrid = isGrid ;
        this.isEpisodesDates = isEpisodesDates ;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == nativeAdView){
            LayoutNativeAdBinding binding = DataBindingUtil.inflate(
                    LayoutInflater.from(mContext), R.layout.layout_native_ad, parent, false);

            return new NativeAdHolder(binding.getRoot());
        }

        ViewDataBinding binding ;
        if (isGrid) {
            binding = DataBindingUtil.inflate(
                    LayoutInflater.from(mContext), R.layout.layout_recyclercartoon_item, parent, false);
            return new CartoonHolder((LayoutRecyclercartoonItemBinding) binding);
        }
        else {
            binding = DataBindingUtil.inflate(
                    LayoutInflater.from(mContext), R.layout.layout_recyclercartoon_item_list, parent, false);
            return new CartoonHolder((LayoutRecyclercartoonItemListBinding) binding);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        if(holder.getItemViewType() == nativeAdView){
            NativeAdHolder nativeAdHolder = (NativeAdHolder) holder;
            Config.loadNativeAd(mContext, nativeAdHolder.mBinding.nativeAdTemplate);
        }
        else {
            holder.itemView.setAnimation(AnimationUtils.loadAnimation(mContext , R.anim.anim_itemview));
            CartoonHolder cartoonHolder = (CartoonHolder) holder;
            final CartoonWithInfo cartoon = cartoonListFiltered.get(position);
            if (isGrid) {
                cartoonHolder.gridBinding.setCartoon(cartoon);
                if (isEpisodesDates) {
                    cartoonHolder.gridBinding.cartoonTitle.setText(cartoon.getEpisodeDateTitle());
                }
                else {
                    cartoonHolder.gridBinding.cartoonTitle.setText(cartoon.getTitle());
                }
                switch (cartoon.getStatus()) {
                    case 1:
                        cartoonHolder.gridBinding.statues.setText("مكتمل");
                        break;

                    case 2:
                        cartoonHolder.gridBinding.statues.setText("مستمر");
                        break;
                    default:
                        cartoonHolder.gridBinding.statues.setText("غير محدد");
                }
            }
            else {
                cartoonHolder.listBinding.setCartoon(cartoon);
                if (isEpisodesDates) {
                    cartoonHolder.listBinding.cartoonTitle.setText(cartoon.getEpisodeDateTitle());
                }
                else {
                    cartoonHolder.listBinding.cartoonTitle.setText(cartoon.getTitle());
                }
                switch (cartoon.getStatus()) {
                    case 1:
                        cartoonHolder.listBinding.statues.setText("مكتمل");
                        break;

                    case 2:
                        cartoonHolder.listBinding.statues.setText("مستمر");
                        break;
                    default:
                        cartoonHolder.listBinding.statues.setText("غير محدد");
                }
            }
            cartoonHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, InformationActivity.class);
                    intent.putExtra("cartoon", cartoon);
                    mContext.startActivity(intent);
                }
            });
        }

        if(position == cartoonList.size() - 1 && !searchCase){
            if (!isEpisodesDates)
            ((MainActivity)mContext).getNewCartoons();
        }
    }


    @Override
    public int getItemCount() {
        return cartoonList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                if(TextUtils.isEmpty(charSequence)){
                    //cartoonListFiltered = cartoonList;
                }else{

                    List<Cartoon> filteredList = new ArrayList<>();
                    for(Cartoon cartoon : cartoonList){

                        if(cartoon.getTitle().toLowerCase().contains(charSequence.toString().toLowerCase()))
                            filteredList.add(cartoon);

                    }

                   // cartoonListFiltered = filteredList;

                }


                FilterResults filterResults = new FilterResults();
                filterResults.values = cartoonListFiltered;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                //cartoonListFiltered = (ArrayList<Cartoon>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemViewType(int position) {
        if((position+1)%10 == 0){
            return nativeAdView;
        }

        return albumView;
    }

    public class CartoonHolder extends RecyclerView.ViewHolder{

        LayoutRecyclercartoonItemBinding gridBinding;
        LayoutRecyclercartoonItemListBinding listBinding ;

        public CartoonHolder(LayoutRecyclercartoonItemBinding binding) {
            super(binding.getRoot());
            gridBinding = binding ;
//            CartoonWithInfo cartoon = cartoonListFiltered.get(getBindingAdapterPosition());
//            gridBinding.getRoot().setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(mContext, InformationActivity.class);
//                    intent.putExtra("cartoon", cartoon);
//                    mContext.startActivity(intent);
//                }
//            });
        }

        public CartoonHolder(LayoutRecyclercartoonItemListBinding binding) {
            super(binding.getRoot());
            listBinding = binding ;
//            CartoonWithInfo cartoon = cartoonListFiltered.get(getBindingAdapterPosition());
//            listBinding.getRoot().setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(mContext, InformationActivity.class);
//                    intent.putExtra("cartoon", cartoon);
//                    mContext.startActivity(intent);
//                }
//            });
        }
    }

    private static class NativeAdHolder extends RecyclerView.ViewHolder{

        LayoutNativeAdBinding mBinding;

        NativeAdHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
        }
    }
}
