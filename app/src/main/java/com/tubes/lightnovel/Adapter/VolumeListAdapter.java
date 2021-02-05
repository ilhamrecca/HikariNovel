package com.tubes.lightnovel.Adapter;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.tubes.lightnovel.Interface.IClickNovelListener;
import com.tubes.lightnovel.Model.Volume;
import com.tubes.lightnovel.R;
import com.tubes.lightnovel.Service.DownloadTask;
import com.tubes.lightnovel.Service.FolioReader;

import java.io.File;
import java.util.List;

public class VolumeListAdapter extends RecyclerView.Adapter<VolumeListAdapter.MyViewHolder> {

    Context context;
    List<Volume> volumeList;
    LayoutInflater inflater;


    public VolumeListAdapter(Context context, List<Volume> volumeList) {
        this.context = context;
        this.volumeList = volumeList;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public VolumeListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.novel_item, parent, false);
        return new MyViewHolder((itemView));
    }

    @Override
    public void onBindViewHolder(@NonNull final VolumeListAdapter.MyViewHolder holder, int position) {
        Picasso.get().load(volumeList.get(position).getImage()).into(holder.imageNovel);
        holder.nameNovel.setText(volumeList.get(position).getName());
        Log.i("kampret",""+volumeList.get(position).getImage());
        Log.i("entahlah",""+volumeList.get(position).getName());
        final Context kontek = this.context;
        //event
        holder.setClickNovelListener(new IClickNovelListener() {
            @Override
            public void onClick(View view, int position) {
                String downloadLink = volumeList.get(position).getLinks();
                downloadLink = downloadLink.trim();
                Log.i("Download URL",""+downloadLink);

                String path = Environment.getExternalStorageDirectory() + "/LightNovel/" + volumeList.get(position).getName();

                File file = new File(path);
                if(file.exists()){
                    new FolioReader(path, volumeList.get(position).getName());
                }
                else
                    new DownloadTask(holder.imageNovel.getContext(), downloadLink, volumeList.get(position).getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return volumeList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView nameNovel;
        ImageView imageNovel;

        IClickNovelListener clickNovelListener;

        public void setClickNovelListener(IClickNovelListener clickNovelListener) {
            this.clickNovelListener = clickNovelListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nameNovel = (TextView) itemView.findViewById(R.id.name_novel);
            imageNovel = (ImageView) itemView.findViewById(R.id.image_novel);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickNovelListener.onClick(v, getAdapterPosition());
        }
    }
}

