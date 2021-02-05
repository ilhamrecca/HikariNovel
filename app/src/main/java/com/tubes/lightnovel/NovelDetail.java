package com.tubes.lightnovel;


import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.tubes.lightnovel.Adapter.NovelViewAdapter;
import com.tubes.lightnovel.Adapter.VolumeListAdapter;
import com.tubes.lightnovel.Common.Common;
import com.tubes.lightnovel.Model.Novel;
import com.tubes.lightnovel.Model.Volume;

import java.util.List;

public class NovelDetail extends AppCompatActivity {

    TextView sinopsis;
    ImageView imageNovel;

    RecyclerView recycleVolume;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novel_detail);

        initialize();
        //set image novel
        Log.i("besar volume", ""+Common.novelSelected.getVolume().size());
        Picasso.get().load(Common.novelSelected.getImage()).into(imageNovel);

        //set sinopsis
        sinopsis.setText(Common.novelSelected.getSinopsis());

        //set adapter
        onNovelLoadDoneListener(Common.novelSelected.getVolume());
    }

    private void initialize() {
        sinopsis = (TextView) findViewById(R.id.sinopsis);
        imageNovel = (ImageView) findViewById(R.id.novel_Image);

        recycleVolume = (RecyclerView) findViewById(R.id.recycler_volume);
        recycleVolume.setHasFixedSize(true);
        recycleVolume.setLayoutManager(new GridLayoutManager(this,2));


    }

    public void onNovelLoadDoneListener(List<Volume> volumeList) {
        recycleVolume.setAdapter(new VolumeListAdapter(getBaseContext(), volumeList));
    }
}
