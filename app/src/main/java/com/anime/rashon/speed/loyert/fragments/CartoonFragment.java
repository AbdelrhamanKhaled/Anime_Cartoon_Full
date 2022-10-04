package com.anime.rashon.speed.loyert.fragments;

import static com.anime.rashon.speed.loyert.Constants.Constants.DUBBED_ANIME;
import static com.anime.rashon.speed.loyert.Constants.Constants.DUBBED_FILMS;
import static com.anime.rashon.speed.loyert.Constants.Constants.FAVOURITE;
import static com.anime.rashon.speed.loyert.Constants.Constants.MOST_VIEWED;
import static com.anime.rashon.speed.loyert.Constants.Constants.NEW_ANIME;
import static com.anime.rashon.speed.loyert.Constants.Constants.TRANSLATED_ANIME;
import static com.anime.rashon.speed.loyert.Constants.Constants.TRANSLATED_FILMS;
import static com.anime.rashon.speed.loyert.Constants.Constants.WATCHED;
import static com.anime.rashon.speed.loyert.Constants.Constants.WATCH_LATER;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.anime.rashon.speed.loyert.Utilites.LoginUtil;
import com.anime.rashon.speed.loyert.activities.MainActivity;
import com.anime.rashon.speed.loyert.adapters.CartoonsAdapter;
import com.anime.rashon.speed.loyert.app.UserOptions;
import com.anime.rashon.speed.loyert.databinding.FragmentCartoonBinding;
import com.anime.rashon.speed.loyert.model.CartoonWithInfo;
import com.anime.rashon.speed.loyert.network.ApiClient;
import com.anime.rashon.speed.loyert.network.ApiService;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class CartoonFragment extends Fragment{


    FragmentCartoonBinding mBinding;

    int pageNumber = 1;
    List<CartoonWithInfo> cartoonList = new ArrayList<>();

    private final CompositeDisposable disposable = new CompositeDisposable();
    ApiService apiService;

    boolean isOnRefresh = false;

    private CartoonsAdapter adapter;

    Fragment current ;

    int user_id ;


    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentCartoonBinding.inflate(inflater);
        initRecyclerview(true);
        initSwipeRefreshLayout();
        initRetrofit();
        return mBinding.getRoot();
    }

    public void initRecyclerview(boolean isGrid) {
        mBinding.cartoonsRecyclerview.setHasFixedSize(true);
        adapter = new CartoonsAdapter(getActivity(), cartoonList , isGrid , false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if((position+1)%10 == 0){
                    return 3;
                }
                return 1;
            }
        });
        if (isGrid) {
            mBinding.cartoonsRecyclerview.setLayoutManager(gridLayoutManager);
        }
        else {
            mBinding.cartoonsRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
        mBinding.cartoonsRecyclerview.setHasFixedSize(true);
        mBinding.cartoonsRecyclerview.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        current = this ;
        LoginUtil loginUtil = new LoginUtil(getActivity()) ;
        user_id = loginUtil.getCurrentUser() != null ? loginUtil.getCurrentUser().getId() : -1 ;
        checkCartoonType(MainActivity.selectedType);
    }


    private void initRetrofit(){
        apiService = ApiClient.getClient(getActivity()).create(ApiService.class);
    }

    private void initSwipeRefreshLayout(){
        mBinding.swipeRefreshLayout.setRefreshing(true);
        mBinding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mBinding.swipeRefreshLayout.setRefreshing(true);
                if(cartoonList != null){
                    cartoonList.clear();
                    isOnRefresh = true;
                    pageNumber = 1;
                    checkCartoonType(MainActivity.selectedType);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MainActivity.selectedType==FAVOURITE) {
            if(cartoonList!=null) cartoonList.clear();
            getFavouriteCartoons();
        }
        else if (MainActivity.selectedType==WATCH_LATER) {
            if(cartoonList!=null) cartoonList.clear();
            getWatchLaterCartoons();
        }
        else if (MainActivity.selectedType==WATCHED) {
            if(cartoonList!=null) cartoonList.clear();
            getWatchedCartoons();
        }
    }

    public void getFavouriteCartoons() {
        List<CartoonWithInfo> retrievedCartoonList = new ArrayList<>(UserOptions.getUserOptions().getFavouriteCartoons());
        for (int i = 0; i < retrievedCartoonList.size(); i++) {
            if ((i + 1) % 10 == 0) {
                retrievedCartoonList.add(i, new CartoonWithInfo());
            }
        }
        cartoonList.addAll(retrievedCartoonList);
        mBinding.cartoonsRecyclerview.getAdapter().notifyDataSetChanged();
        mBinding.swipeRefreshLayout.setRefreshing(false);
    }

    public void getWatchedCartoons(){
        List<CartoonWithInfo> retrievedCartoonList = new ArrayList<>(UserOptions.getUserOptions().getWatchedCartoons());
        for (int i = 0; i < retrievedCartoonList.size(); i++) {
            if ((i + 1) % 10 == 0) {
                retrievedCartoonList.add(i, new CartoonWithInfo());
            }
        }
        cartoonList.addAll(retrievedCartoonList);
        mBinding.cartoonsRecyclerview.getAdapter().notifyDataSetChanged();
        mBinding.swipeRefreshLayout.setRefreshing(false);
    }

    public void getWatchLaterCartoons(){
        List<CartoonWithInfo> retrievedCartoonList = new ArrayList<>(UserOptions.getUserOptions().getWatchLaterCartoons());
        for (int i = 0; i < retrievedCartoonList.size(); i++) {
            if ((i + 1) % 10 == 0) {
                retrievedCartoonList.add(i, new CartoonWithInfo());
            }
        }
        cartoonList.addAll(retrievedCartoonList);
        mBinding.cartoonsRecyclerview.getAdapter().notifyDataSetChanged();
        mBinding.swipeRefreshLayout.setRefreshing(false);
    }

    public void getMostViewedCartoons(){
        mBinding.swipeRefreshLayout.setRefreshing(true);
        disposable.add(
                apiService
                        .getMostViewedCartoons()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<CartoonWithInfo>>() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onSuccess(List<CartoonWithInfo> retrievedCartoonList) {
                                //Check if on refresh case
                                if (isOnRefresh) {
                                    cartoonList.clear();
                                    isOnRefresh = false;
                                }
                                for (int i = 0; i < retrievedCartoonList.size(); i++) {
                                    if ((i + 1) % 10 == 0) {
                                        retrievedCartoonList.add(i, new CartoonWithInfo());
                                    }
                                    if (retrievedCartoonList.get(i).getTitle() != null && retrievedCartoonList.get(i).getTitle().equals("الافلام")) {
                                        retrievedCartoonList.remove(i);
                                        break;
                                    }
                                }

                                cartoonList.addAll(retrievedCartoonList);
                                mBinding.cartoonsRecyclerview.getAdapter().notifyDataSetChanged();
                                mBinding.swipeRefreshLayout.setRefreshing(false);
                            }
                            @Override
                            public void onError(Throwable e) {
                                onGetCartoonError();
                            }
                        })
        );
    }


    public void filterAdapter(String searchQuery){
//        mBinding.swipeRefreshLayout.setRefreshing(true);
//        disposable.add(
//                apiService
//                        .searchCartoons(searchQuery)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribeWith(new DisposableSingleObserver<List<Cartoon>>() {
//                            @Override
//                            public void onSuccess(List<Cartoon> cartoonList) {
//                                CartoonFragment.this.cartoonList.clear();
//                                CartoonFragment.this.cartoonList.addAll(cartoonList);
//
//                                mBinding.cartoonsRecyclerview.getAdapter().notifyDataSetChanged();
//                                mBinding.swipeRefreshLayout.setRefreshing(false);
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//                                mBinding.progressBarLayout.setVisibility(View.GONE);
//                                mBinding.swipeRefreshLayout.setRefreshing(false);
////                                Toast.makeText(getActivity(), getString(R.string.err_general), Toast.LENGTH_SHORT).show();
//                            }
//                        })
//        );

//        ((CartoonsAdapter)mBinding.cartoonsRecyclerview.getAdapter()).getFilter().filter(searchQuery);
    }

    public void checkCartoonType(int categoryId){
        cartoonList.clear();
        pageNumber = 1;
        switch (categoryId){
            case DUBBED_ANIME: //All Cartoons
                getCartoonsByType(DUBBED_ANIME);
                break;

            case DUBBED_FILMS: //Action Cartoons
                getCartoonsByType(DUBBED_FILMS);
                break;

            case TRANSLATED_ANIME:
                getCartoonsByType(TRANSLATED_ANIME);
                break;

            case TRANSLATED_FILMS:
                getCartoonsByType(TRANSLATED_FILMS);
                break;

            case NEW_ANIME:
                getCartoonsByType(NEW_ANIME);
                break;

            case FAVOURITE:
                getFavouriteCartoons();
                break;

            case WATCH_LATER:
                getWatchLaterCartoons();
                break;

            case WATCHED:
                getWatchedCartoons();
                break;

            case MOST_VIEWED:
                getMostViewedCartoons();
                break;

            default: //No Action
        }
    }


    private void onGetCartoonError() {
        mBinding.swipeRefreshLayout.setRefreshing(false);
    }

    private void updateUI(List<CartoonWithInfo> retrievedCartoonList) {
        mBinding.swipeRefreshLayout.setRefreshing(false);
        if (isOnRefresh) {
            cartoonList.clear();
            isOnRefresh = false;
        }
            for (int i = 0; i < retrievedCartoonList.size(); i++) {
            if ((i + 1) % 10 == 0) {
                retrievedCartoonList.add(i, new CartoonWithInfo());
               }
            }
        cartoonList.addAll(retrievedCartoonList);
        mBinding.cartoonsRecyclerview.getAdapter().notifyDataSetChanged();
        pageNumber++;
    }

    //--------Override Methods------//

    @Override
    public void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }

    public CartoonsAdapter getAdapter () {
        return adapter ;
    }


    public RecyclerView getRecycleView () {
        return mBinding.cartoonsRecyclerview ;
    }

    public void getCartoonsByType(int selectedType) {
        if (selectedType == DUBBED_ANIME) {
            disposable.add(
                    apiService
                            .readPagingDUBBEDSeriesAnime(pageNumber)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<List<CartoonWithInfo>>() {
                                @Override
                                public void onSuccess(List<CartoonWithInfo> retrievedCartoonList) {
                                    //Check if on refresh case
                                    updateUI(retrievedCartoonList);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    onGetCartoonError();
                                }
                            })
            );
        }
        else if (selectedType == DUBBED_FILMS) {
            disposable.add(
                    apiService
                            .readPagingDUBBEDFilms(pageNumber)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<List<CartoonWithInfo>>() {
                                @Override
                                public void onSuccess(List<CartoonWithInfo> retrievedCartoonList) {
                                    //Check if on refresh case
                                    updateUI(retrievedCartoonList);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    onGetCartoonError();
                                }
                            })
            );
        }
        else if (selectedType == TRANSLATED_ANIME) {
            disposable.add(
                    apiService
                            .readPagingTranslatedSeriesAnime(pageNumber)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<List<CartoonWithInfo>>() {
                                @Override
                                public void onSuccess(List<CartoonWithInfo> retrievedCartoonList) {
                                    //Check if on refresh case
                                    updateUI(retrievedCartoonList);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    onGetCartoonError();
                                }
                            })
            );
        }
        else if (selectedType == TRANSLATED_FILMS) {
            disposable.add(
                    apiService
                            .readPagingTranslatedFilms(pageNumber)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<List<CartoonWithInfo>>() {
                                @Override
                                public void onSuccess(List<CartoonWithInfo> retrievedCartoonList) {
                                    //Check if on refresh case
                                    updateUI(retrievedCartoonList);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    onGetCartoonError();
                                }
                            })
            );
        }
        else if (selectedType == NEW_ANIME) {
            disposable.add(
                    apiService
                            .readPagingContinueAnime(pageNumber)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<List<CartoonWithInfo>>() {
                                @Override
                                public void onSuccess(List<CartoonWithInfo> retrievedCartoonList) {
                                    //Check if on refresh case
                                    updateUI(retrievedCartoonList);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    onGetCartoonError();
                                }
                            })
            );
        }

    }
}
