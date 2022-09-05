package com.anime.rashon.speed.loyert.adapters;

import android.app.Activity;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.anime.rashon.speed.loyert.Database.SQLiteDatabaseManager;
import com.anime.rashon.speed.loyert.R;
import com.anime.rashon.speed.loyert.app.Config;
import com.anime.rashon.speed.loyert.databinding.LayoutRecyclerdownloadItemBinding;
import com.anime.rashon.speed.loyert.model.Download;

import java.io.File;
import java.util.List;

public class DownloadsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = DownloadsAdapter.class.getSimpleName();
    private Activity mContext;
    private List<Download> downloadList;
    private SQLiteDatabaseManager sqLiteDatabaseManager;

    public DownloadsAdapter(Activity mContext, List<Download> downloadList) {
        this.mContext = mContext;
        this.downloadList = downloadList;
        sqLiteDatabaseManager = new SQLiteDatabaseManager(mContext);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutRecyclerdownloadItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(mContext), R.layout.layout_recyclerdownload_item, parent, false);

        return new DownloadHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        DownloadHolder downloadHolder = (DownloadHolder) holder;
        final Download download = downloadList.get(position);
        downloadHolder.mBinding.setDownload(download);

        downloadHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(mContext, OfflineExoplayerActivity.class);

                intent.putExtra("download", download);
                mContext.startActivity(intent);*/
                String videoUrl = download.getPath();
                Config.openExoPlayerApp(mContext, videoUrl);
            }
        });

        downloadHolder.mBinding.trash.setOnClickListener(v -> {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);

            builder.setMessage("هل تريد حذف هذا التنزيل؟");
            builder.setCancelable(true);
            builder.setPositiveButton("نعم", (dialog, which) -> {
                sqLiteDatabaseManager.deleteDownload(download.getId());

                File file = new File(Environment.getExternalStoragePublicDirectory(download.getPath()).getAbsolutePath());
                boolean deleted = file.delete();

                downloadList.remove(position);
                notifyDataSetChanged();
            });

            builder.setNegativeButton("لا", (dialog, which) -> dialog.cancel());

            android.app.AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
    }


    @Override
    public int getItemCount() {
        return downloadList.size();
    }

    public class DownloadHolder extends RecyclerView.ViewHolder{

        LayoutRecyclerdownloadItemBinding mBinding;

        public DownloadHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
        }
    }
}
