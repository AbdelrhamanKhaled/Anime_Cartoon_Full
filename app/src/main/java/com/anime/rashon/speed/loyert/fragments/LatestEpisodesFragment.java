package com.anime.rashon.speed.loyert.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.anime.rashon.speed.loyert.R;
import com.anime.rashon.speed.loyert.activities.MainActivity;
import com.anime.rashon.speed.loyert.adapters.LatestEpisodesAdapter;
import com.anime.rashon.speed.loyert.databinding.FragmentLatestEpisodesBinding;
import com.anime.rashon.speed.loyert.model.Episode;
import com.anime.rashon.speed.loyert.model.EpisodeWithInfo;
import com.anime.rashon.speed.loyert.model.Information;
import com.anime.rashon.speed.loyert.network.ApiClient;
import com.anime.rashon.speed.loyert.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class LatestEpisodesFragment extends Fragment {

    private final String TAG = LatestEpisodesFragment.class.getSimpleName();

    private FragmentLatestEpisodesBinding mBinding;

    private List<EpisodeWithInfo> episodeList = new ArrayList<>();

    private CompositeDisposable disposable = new CompositeDisposable();
    private ApiService apiService;

    private int lastAdPosition = 0;

    LatestEpisodesAdapter adapter;
    boolean isOnRefresh = true;

    public LatestEpisodesFragment() {

    }

    public LatestEpisodesFragment(List<EpisodeWithInfo> episodeList) {
        this.episodeList = episodeList;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentLatestEpisodesBinding.inflate(inflater);
        initSwipeRefreshLayout();
        initRecyclerview(true);
        initRetrofit();
        if (episodeList.size() == 0) {
            mBinding.swipeRefreshLayout.setRefreshing(true);
            isOnRefresh = true ;
            getLatestEpisodes();
        }
        else Log.i("ab_do" , "goDirect");

        return mBinding.getRoot();
    }

    private void initSwipeRefreshLayout(){
        mBinding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mBinding.swipeRefreshLayout.setRefreshing(true);
                if(episodeList != null) {
                    episodeList.clear();
                    isOnRefresh = true ;
                }
                getLatestEpisodes();
            }
        });
    }

    private void initRetrofit() {
        apiService = ApiClient.getClient(getActivity()).create(ApiService.class);
    }

    public void initRecyclerview(boolean isGrid) {
        adapter = new LatestEpisodesAdapter(getActivity(), episodeList, isGrid);
        if (isGrid) {
            //anim = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.recycle_anim);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
            mBinding.episodesRecyclerview.setLayoutManager(gridLayoutManager);
        }
        else {
            mBinding.episodesRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
            //anim = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.recycle_anim);
        }
        mBinding.episodesRecyclerview.setHasFixedSize(true);
        mBinding.episodesRecyclerview.setAdapter(adapter);
        //mBinding.episodesRecyclerview.setLayoutAnimation(anim);
    }


    public RecyclerView getRecyclerView() {
        return mBinding.episodesRecyclerview ;
    }

    public LatestEpisodesAdapter getAdapter() {
        return adapter ;
    }

    private void getLatestEpisodes(){
        disposable.add(
                apiService
                        .latestEpisodesWithInfo()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<EpisodeWithInfo>>() {
                            @Override
                            public void onSuccess(List<EpisodeWithInfo> retrivedEpisodeList) {
                                episodeList.addAll(retrivedEpisodeList);
                                adapter.notifyDataSetChanged();
                                mBinding.progressBarLayout.setVisibility(View.GONE);
                                if (isOnRefresh)
                                mBinding.swipeRefreshLayout.setRefreshing(false);
                            }

                            @Override
                            public void onError(Throwable e) {
                                mBinding.progressBarLayout.setVisibility(View.GONE);
                            }
                        })
        );
    }

    public void notifyItemChangedForEpisodes(int position){
//        Objects.requireNonNull(mBinding.episodesRecyclerview.getAdapter()).notifyItemChanged(position);
        adapter.notifyItemChanged(position);
    }

    //--------Override Methods------//

    @Override
    public void onDestroy() {
        if (disposable!=null)
        disposable.dispose();
        MainActivity.search_item.setVisible(true);
        super.onDestroy();
    }

//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        if (MainActivity.search_item!=null)
//        MainActivity.search_item.setVisible(false);
//    }
}
