package com.tubes.lightnovel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import com.folioreader.Config;
import com.folioreader.FolioReader;
import com.folioreader.model.locators.ReadLocator;

import com.folioreader.util.ReadLocatorListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tubes.lightnovel.Adapter.BannerSliderAdapter;
import com.tubes.lightnovel.Adapter.NovelViewAdapter;
import com.tubes.lightnovel.Common.Common;
import com.tubes.lightnovel.Interface.IBannerLoadDone;
import com.tubes.lightnovel.Interface.INovelLoadDone;
import com.tubes.lightnovel.Model.Novel;
import com.tubes.lightnovel.Service.PicassoLoadingService;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import ss.com.bannerslider.Slider;

public class MainActivity extends AppCompatActivity implements IBannerLoadDone, INovelLoadDone {

    //database variable
    DatabaseReference banners;
    DatabaseReference novel;

    //layout slider, button, dll
    RecyclerView recyclerNovel;
    TextView txtNovel;
    Slider slider;
    SwipeRefreshLayout swipeRefreshLayout;

    //Listener
    IBannerLoadDone bannerLoadDoneListener;
    INovelLoadDone novelLoadDone;

    //actionbar button
    Button search;

    //alert dialog
    android.app.AlertDialog alertDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!isStoragePermissionGranted()){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        initialize();

        //setFolio();



    }

    private void initialize() {
        //init dataabase
        banners = FirebaseDatabase.getInstance().getReference("Banners");
        novel = FirebaseDatabase.getInstance().getReference("Novel");
        mAuth = FirebaseAuth.getInstance();
        //init layout, button dll.
        slider = (Slider) findViewById(R.id.slider);
        Slider.init(new PicassoLoadingService());
        recyclerNovel = (RecyclerView) findViewById(R.id.recycler_novel);
        recyclerNovel.setHasFixedSize(true);
        recyclerNovel.setLayoutManager(new GridLayoutManager(this,2));

        //init listener banner
        bannerLoadDoneListener = this;
        novelLoadDone = this;

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadBanner();
                loadNovel();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                loadBanner();
                loadNovel();
            }
        });




    }

    private void loadBanner() {
        banners.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> bannerList = new ArrayList<>();

                for(DataSnapshot bannerSnapshot:dataSnapshot.getChildren()){
                    String image = bannerSnapshot.getValue(String.class);
                    bannerList.add(image);
                }
                onBannerLoadDoneListener(bannerList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this,""+databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public void onBannerLoadDoneListener(List<String> banners) {
        slider.setAdapter(new BannerSliderAdapter(banners));
    }

    private void loadNovel() {
        //show dialog
        alertDialog = new SpotsDialog.Builder().setContext(this)
                .setCancelable(false)
                .setMessage("Please Wait")
                .build();

        if(!swipeRefreshLayout.isRefreshing())
            alertDialog.show();

        novel.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Novel> novelList = new ArrayList<>();

                for(DataSnapshot bannerSnapshot:dataSnapshot.getChildren()){
                    Novel novel = bannerSnapshot.getValue(Novel.class);
                    novelList.add(novel);
                }

                novelLoadDone.onNovelLoadDoneListener(novelList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this,""+databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onNovelLoadDoneListener(List<Novel> novelList) {
        Common.novelList = novelList;
        recyclerNovel.setAdapter(new NovelViewAdapter(getBaseContext(), novelList));


        if(!swipeRefreshLayout.isRefreshing()){
            alertDialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.search) {
            Intent intent = new Intent(MainActivity.this, FilterSearchActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.account){
            if(mAuth.getCurrentUser()!=null){
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);

                startActivity(intent);
            }
            else{
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
            }

        }
        return super.onOptionsItemSelected(item);
    }
    private void setFolio() {
        FolioReader folioReader = FolioReader.get();
        Config config = new Config();
        config.setNightMode(true);
        config.setThemeColorRes(R.color.colorPrimary);
        config.setAllowedDirection(Config.AllowedDirection.VERTICAL_AND_HORIZONTAL);
        config.setDirection(Config.Direction.VERTICAL);
        config.setShowTts(false);
        folioReader.setConfig(config,true);

        folioReader.openBook("/storage/emulated/0/Overlord Volume 01 - The Undead King.epub");

        folioReader.setReadLocatorListener(new ReadLocatorListener() {
            @Override
            public void saveReadLocator(ReadLocator readLocator) {

                Log.i("message", "-> saveReadLocator -> " + readLocator.toJson());

        /*ReadLocator has toJson() method which gives JSON in following format -
        {
            bookId : string,
            href : string,
            created : integer,
            locations : {
                cfi : string
            }
        }
        You can save this last read position in your local or remote db*/
            }
        });
    }

    private  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("permission","Permission is granted");
                return true;
            } else {

                Log.v("entahlah","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("permission","Permission is granted");
            return true;
        }
    }



}