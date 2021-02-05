package com.tubes.lightnovel.Adapter;

import android.content.Context;
import android.content.Intent;
import android.telephony.IccOpenLogicalChannelResponse;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.tubes.lightnovel.Common.Common;
import com.tubes.lightnovel.Interface.IClickNovelListener;
import com.tubes.lightnovel.Model.Novel;
import com.tubes.lightnovel.NovelDetail;
import com.tubes.lightnovel.R;

import java.util.List;

public class NovelViewAdapter extends RecyclerView.Adapter<NovelViewAdapter.MyViewHolder> {

    Context context;
    List<Novel> novelList;
    LayoutInflater inflater;



    public NovelViewAdapter(Context context, List<Novel> novelList) {
        this.context = context;
        this.novelList = novelList;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.novel_item, parent, false);
        return new MyViewHolder((itemView));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Picasso.get().load(novelList.get(position).getImage()).into(holder.imageNovel);
        holder.nameNovel.setText(novelList.get(position).getName());



        //event
        holder.setClickNovelListener(new IClickNovelListener() {
            @Override
            public void onClick(View view, int position) {
                Common.novelSelected = novelList.get(position);
                Intent i =  new Intent(context,NovelDetail.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return novelList.size();
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
