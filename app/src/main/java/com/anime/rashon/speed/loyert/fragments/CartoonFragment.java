package com.anime.rashon.speed.loyert.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.anime.rashon.speed.loyert.R;
import com.anime.rashon.speed.loyert.activities.MainActivity;
import com.anime.rashon.speed.loyert.activities.PlayListsActivity;
import com.anime.rashon.speed.loyert.adapters.CartoonsAdapter;
import com.anime.rashon.speed.loyert.app.Config;
import com.anime.rashon.speed.loyert.databinding.FragmentCartoonBinding;
import com.anime.rashon.speed.loyert.model.Cartoon;
import com.anime.rashon.speed.loyert.model.CartoonWithInfo;
import com.anime.rashon.speed.loyert.network.ApiClient;
import com.anime.rashon.speed.loyert.network.ApiService;
import com.github.ybq.android.spinkit.style.Circle;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.anime.rashon.speed.loyert.app.Config.ACTION;
import static com.anime.rashon.speed.loyert.app.Config.ADVENTURE;
import static com.anime.rashon.speed.loyert.app.Config.ALL;
import static com.anime.rashon.speed.loyert.app.Config.CHILD_ANIME;
import static com.anime.rashon.speed.loyert.app.Config.FILMS;
import static com.anime.rashon.speed.loyert.app.Config.GIRLSANIME;
import static com.anime.rashon.speed.loyert.app.Config.NEW_ANIME;
import static com.anime.rashon.speed.loyert.app.Config.SPORT_ANIME;
import static com.anime.rashon.speed.loyert.app.Config.TRANSLATED_ANIME;

public class CartoonFragment extends Fragment{

    private final String TAG = CartoonFragment.class.getSimpleName();

    FragmentCartoonBinding mBinding;

    int pageNumber = 1;
    List<CartoonWithInfo> cartoonList = new ArrayList<>();

    private CompositeDisposable disposable = new CompositeDisposable();
    ApiService apiService;

    boolean isOnRefresh = false;

    private int lastAdPosition = 0;
    private CartoonsAdapter adapter;

    Fragment current ;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentCartoonBinding.inflate(inflater);

        initRecyclerview(true);
        initSwipeRefreshLayout();
        initRetrofit();
//        getAllCartoons();


        return mBinding.getRoot();
    }

    public void initRecyclerview(boolean isGrid){
        adapter = new CartoonsAdapter(getActivity(), cartoonList , isGrid);
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
        sortByCategory(MainActivity.selectedType);
    }

    private void initProgressBar(){
        Circle circle = new Circle();
        mBinding.progress.setIndeterminateDrawable(circle);
    }

    private void initRetrofit(){
        apiService = ApiClient.getClient(getActivity()).create(ApiService.class);
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
                        .getCartoonsWithInfo(pageNumber)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<CartoonWithInfo>>() {
                            @Override
                            public void onSuccess(List<CartoonWithInfo> retrievedCartoonList) {
                                //Check if on refresh case
                                if (isOnRefresh) {
                                    cartoonList.clear();
                                    isOnRefresh = false;
                                }

                                if (MainActivity.selectedType == FILMS) {
                                    Log.i("Ab_do", "Yes Film");
                                    for (int i = 0; i < retrievedCartoonList.size(); i++) {
//
                                        if (retrievedCartoonList.get(i).getTitle() != null && retrievedCartoonList.get(i).getTitle().equals("الافلام")) {
                                            Log.i("Ab_do", "catch Film");
                                            Cartoon cartoon = retrievedCartoonList.get(i);
                                            goToPlaylist(cartoon);
                                            break;
                                        }
                                    }
                                }
                                else {
                                    for (int i = 0; i < retrievedCartoonList.size(); i++) {
                                        if ((i + 1) % 10 == 0) {
                                            retrievedCartoonList.add(i, new CartoonWithInfo());
                                        }
                                        if (retrievedCartoonList.get(i).getTitle() != null && retrievedCartoonList.get(i).getTitle().equals("الافلام")) {
                                            retrievedCartoonList.remove(i);
                                        }
                                    }

                                    cartoonList.addAll(retrievedCartoonList);


                                    //--------------------//
                                    if (cartoonList.isEmpty()) {
                                        mBinding.cartoonsRecyclerview.getAdapter().notifyDataSetChanged();
                                    } else {
                                        mBinding.cartoonsRecyclerview.getAdapter().notifyItemInserted(cartoonList.size());
                                    }
                                    pageNumber++;
                                    mBinding.swipeRefreshLayout.setRefreshing(false);
                                }
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

    private void goToPlaylist(Cartoon cartoon) {
        Intent intent = new Intent(requireContext(), PlayListsActivity.class)
                .setAction("Films");
        intent.putExtra("cartoon", cartoon);
        requireActivity().startActivity(intent);
        requireActivity().finish();
    }

    public void getCartoonsByType(int type){
//        Log.i("Ab_do" , "getCartoonsByType "+type);
//        mBinding.swipeRefreshLayout.setRefreshing(true);
//        disposable.add(
//                apiService
//                        .getCartoonsByType(pageNumber, type)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribeWith(new DisposableSingleObserver<List<Cartoon>>() {
//                            @Override
//                            public void onSuccess(List<Cartoon> retrievedCartoonList) {
//                                    Log.i("Ab_do" , "No Film");
//                                    for (int i = 0; i < retrievedCartoonList.size(); i++) {
//                                        if ((i + 1) % 10 == 0) {
//                                            retrievedCartoonList.add(i, new Cartoon());
//                                        }
//                                    }
//
//                                    cartoonList.addAll(retrievedCartoonList);
//
//
//                                //--------------------//
//
//                                if(cartoonList.isEmpty()){
//                                    mBinding.cartoonsRecyclerview.getAdapter().notifyDataSetChanged();
//                                }
//                                else{
//
//                                    mBinding.cartoonsRecyclerview.getAdapter().notifyItemInserted(cartoonList.size());
//                                }
//                                pageNumber++;
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

    public void sortByCategory(int categoryId){

        cartoonList.clear();
//        mBinding.cartoonsRecyclerview.getAdapter().notifyDataSetChanged();
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

            case FILMS:
                getAllCartoons();
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

    public CartoonsAdapter getAdapter () {
        return adapter ;
    }


    public RecyclerView getRecycleView () {
        return mBinding.cartoonsRecyclerview ;
    }

}
