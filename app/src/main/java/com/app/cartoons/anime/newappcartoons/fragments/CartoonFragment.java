package com.app.cartoons.anime.newappcartoons.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.cartoons.anime.newappcartoons.activities.MainActivity;
import com.app.cartoons.anime.newappcartoons.app.Config;
import com.app.cartoons.anime.newappcartoons.databinding.FragmentCartoonBinding;
import com.app.cartoons.anime.newappcartoons.model.Cartoon;
import com.app.cartoons.anime.newappcartoons.network.ApiClient;
import com.app.cartoons.anime.newappcartoons.network.ApiService;
import com.github.ybq.android.spinkit.style.Circle;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.app.cartoons.anime.newappcartoons.app.Config.ACTION;
import static com.app.cartoons.anime.newappcartoons.app.Config.ADVENTURE;
import static com.app.cartoons.anime.newappcartoons.app.Config.ALL;
import static com.app.cartoons.anime.newappcartoons.app.Config.CHILD_ANIME;
import static com.app.cartoons.anime.newappcartoons.app.Config.GIRLSANIME;
import static com.app.cartoons.anime.newappcartoons.app.Config.NEW_ANIME;
import static com.app.cartoons.anime.newappcartoons.app.Config.SPORT_ANIME;
import static com.app.cartoons.anime.newappcartoons.app.Config.TRANSLATED_ANIME;

public class CartoonFragment extends Fragment{

    private final String TAG = CartoonFragment.class.getSimpleName();

    FragmentCartoonBinding mBinding;

    int pageNumber = 1;
    List<Cartoon> cartoonList = new ArrayList<>();

    private CompositeDisposable disposable = new CompositeDisposable();
    ApiService apiService;

    boolean isOnRefresh = false;

    private int lastAdPosition = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentCartoonBinding.inflate(inflater);

//        initProgressBar();
        initRecyclerview();
        initSwipeRefreshLayout();
        initRetrofit();
        getAllCartoons();


        return mBinding.getRoot();
    }

    private void initProgressBar(){
        Circle circle = new Circle();
        mBinding.progress.setIndeterminateDrawable(circle);
    }

    private void initRetrofit(){
        apiService = ApiClient.getClient(getActivity()).create(ApiService.class);
    }

    private void initRecyclerview(){
        mBinding.setCartoonList(cartoonList);
    }

    private void initSwipeRefreshLayout(){
        mBinding.swipeRefreshLayout.setRefreshing(true);
        mBinding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(cartoonList != null){
                    isOnRefresh = true;
                    pageNumber = 1;
                    getAllCartoons();
                    ((MainActivity)getActivity()).getAdmobData();
                }

                ((MainActivity)getActivity()).resetTitleAndSelection();
            }
        });
    }

    public void getAllCartoons(){
        mBinding.swipeRefreshLayout.setRefreshing(true);
        disposable.add(
                apiService
                        .getCartoons(pageNumber)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Cartoon>>() {
                            @Override
                            public void onSuccess(List<Cartoon> retrievedCartoonList) {
                                //Check if on refresh case
                                if(isOnRefresh){
                                    cartoonList.clear();
                                    isOnRefresh = false;
                                }

                                int counter = 0;
                                if(!cartoonList.isEmpty()){
                                    counter = (cartoonList.size() - 1) - lastAdPosition ;
                                }

                                for (int i=0; i<retrievedCartoonList.size(); i++){
                                    if(counter == Config.numOfItemsBetweenAds){
                                        retrievedCartoonList.add(i, new Cartoon());
                                        counter = 0;
                                        lastAdPosition = cartoonList.size() + i;
                                    }else{
                                        counter++;
                                    }
                                }

                                cartoonList.addAll(retrievedCartoonList);

                                //--------------------//
                                if(cartoonList.isEmpty()){
                                    mBinding.cartoonsRecyclerview.getAdapter().notifyDataSetChanged();
                                }else{
                                    mBinding.cartoonsRecyclerview.getAdapter().notifyItemInserted(cartoonList.size());
                                }
                                pageNumber++;
                                mBinding.swipeRefreshLayout.setRefreshing(false);
                            }

                            @Override
                            public void onError(Throwable e) {
                                mBinding.progressBarLayout.setVisibility(View.GONE);
                                mBinding.swipeRefreshLayout.setRefreshing(false);
//                                Toast.makeText(getActivity(), getString(R.string.err_general), Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    public void getCartoonsByType(int type){
        mBinding.swipeRefreshLayout.setRefreshing(true);
        disposable.add(
                apiService
                        .getCartoonsByType(pageNumber, type)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Cartoon>>() {
                            @Override
                            public void onSuccess(List<Cartoon> retrievedCartoonList) {

                                int counter = 0;
                                if(!cartoonList.isEmpty()){
                                    counter = (cartoonList.size() - 1) - lastAdPosition ;
                                }

                                for (int i=0; i<retrievedCartoonList.size(); i++){
                                    if(counter == Config.numOfItemsBetweenAds){
                                        retrievedCartoonList.add(i, new Cartoon());
                                        counter = 0;
                                        lastAdPosition = cartoonList.size() + i;
                                    }else{
                                        counter++;
                                    }
                                }

                                cartoonList.addAll(retrievedCartoonList);

                                //--------------------//

                                if(cartoonList.isEmpty()){
                                    mBinding.cartoonsRecyclerview.getAdapter().notifyDataSetChanged();
                                }else{
                                    mBinding.cartoonsRecyclerview.getAdapter().notifyItemInserted(cartoonList.size());
                                }
                                pageNumber++;
                                mBinding.swipeRefreshLayout.setRefreshing(false);
                            }

                            @Override
                            public void onError(Throwable e) {
                                mBinding.progressBarLayout.setVisibility(View.GONE);
                                mBinding.swipeRefreshLayout.setRefreshing(false);
//                                Toast.makeText(getActivity(), getString(R.string.err_general), Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    public void filterAdapter(String searchQuery){
        mBinding.swipeRefreshLayout.setRefreshing(true);
        disposable.add(
                apiService
                        .searchCartoons(searchQuery)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Cartoon>>() {
                            @Override
                            public void onSuccess(List<Cartoon> cartoonList) {
                                CartoonFragment.this.cartoonList.clear();
                                CartoonFragment.this.cartoonList.addAll(cartoonList);

                                mBinding.cartoonsRecyclerview.getAdapter().notifyDataSetChanged();
                                mBinding.swipeRefreshLayout.setRefreshing(false);
                            }

                            @Override
                            public void onError(Throwable e) {
                                mBinding.progressBarLayout.setVisibility(View.GONE);
                                mBinding.swipeRefreshLayout.setRefreshing(false);
//                                Toast.makeText(getActivity(), getString(R.string.err_general), Toast.LENGTH_SHORT).show();
                            }
                        })
        );

//        ((CartoonsAdapter)mBinding.cartoonsRecyclerview.getAdapter()).getFilter().filter(searchQuery);
    }

    public void sortByCategory(int categoryId){

        cartoonList.clear();
        mBinding.cartoonsRecyclerview.getAdapter().notifyDataSetChanged();
        pageNumber = 1;

        switch (categoryId){
            case ALL: //All Cartoons
                getAllCartoons();
                break;

            case ACTION: //Action Cartoons
                getCartoonsByType(ACTION);
                break;

            case GIRLSANIME: //Girls Cartoons
                getCartoonsByType(GIRLSANIME);
                break;

            case ADVENTURE: //Adventure Cartoons
                getCartoonsByType(ADVENTURE);
                break;

            case TRANSLATED_ANIME:
                getCartoonsByType(TRANSLATED_ANIME);
                break;

            case CHILD_ANIME:
                getCartoonsByType(CHILD_ANIME);
                break;

            case SPORT_ANIME:
                getCartoonsByType(SPORT_ANIME);
                break;

            case NEW_ANIME:
                getCartoonsByType(NEW_ANIME);
                break;

            default: //No Action
        }
    }

    //--------Override Methods------//

    @Override
    public void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }
}
