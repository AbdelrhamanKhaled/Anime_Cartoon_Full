package com.anime.rashon.speed.loyert.Utilites;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.anime.rashon.speed.loyert.R;
import com.anime.rashon.speed.loyert.model.UserResponse;
import com.anime.rashon.speed.loyert.network.ApiClient;
import com.anime.rashon.speed.loyert.network.ApiService;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ServerReportDialog {

    private final Dialog dialog;
    private final Context context;
    private int episode_id ;
    private String episode_name ;
    private String playlist_name ;
    private String cartoon_name ;

    public ServerReportDialog(Context context) {
        dialog = new Dialog(context);
        this.context = context;
        createDialog();
    }

    private void createDialog() {
        dialog.setContentView(R.layout.server_report_dialog);
        ImageView close = dialog.findViewById(R.id.close_Btn);
        Button ok = dialog.findViewById(R.id.ok_Btn);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });
    }

    public void showDialog() {
        dialog.show();
        sendReport();
    }

    public void dismissDialog() {

        try {
            dialog.dismiss();
        } catch (Exception exception) {
            Log.i("ab_do", "dialog login exception " + exception.getMessage());
        }
    }

    private void sendReport() {
        CompositeDisposable disposable = new CompositeDisposable();
        ApiService apiService = ApiClient.getClient(context).create(ApiService.class);
        disposable.add(
                apiService
                        .sendServerReport(episode_id , episode_name , playlist_name ,cartoon_name)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse response) {
                                if (!response.isError()) {
                                    Log.i("ab_do" , "report sent successfully");
                                }
                                else {
                                    Log.i("ab_do" , "error when send report");
                                }
                            }
                            @Override
                            public void onError(Throwable e) {
                                Log.i("ab_do" , "onError send report  " + e.getMessage());
                            }
                        })
        );
    }

    public void setEpisode_id(int episode_id) {
        this.episode_id = episode_id;
    }

    public void setEpisode_name(String episode_name) {
        this.episode_name = episode_name;
    }

    public void setPlaylist_name(String playlist_name) {
        this.playlist_name = playlist_name;
    }

    public void setCartoon_name(String cartoon_name) {
        this.cartoon_name = cartoon_name;
    }
}
