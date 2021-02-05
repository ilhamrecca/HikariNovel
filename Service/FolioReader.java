package com.tubes.lightnovel.Service;

import android.util.Log;
import android.widget.Toast;

import com.folioreader.Config;
import com.folioreader.model.locators.ReadLocator;
import com.folioreader.util.ReadLocatorListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tubes.lightnovel.R;

public class FolioReader {
    private com.folioreader.FolioReader fReader;
    private Config config;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private String path, fileName;

    public FolioReader(String path, String downloadFileName) {
        this.path = path;
        this.fileName = downloadFileName;
        initialize();
    }

    private void initialize() {
        //database
        database = FirebaseDatabase.getInstance();

        mAuth = FirebaseAuth.getInstance();


        fileName = fileName.substring(0,fileName.length()-5);
        Log.i("nama file", fileName);

        //Reader
        fReader = com.folioreader.FolioReader.get();
        Config config = new Config();
        config.setNightMode(true);
        config.setThemeColorRes(R.color.colorAccent);
        config.setAllowedDirection(Config.AllowedDirection.VERTICAL_AND_HORIZONTAL);
        config.setDirection(Config.Direction.VERTICAL);
        config.setShowTts(false);
        fReader.setConfig(config,true);

        if(mAuth.getCurrentUser()!=null) {
            myRef = database.getReference("UserLocator/ReadLocator/"+ mAuth.getCurrentUser().getUid());
            checkSavedLocation();
        }
        else{
            fReader.openBook(path);
        }





        fReader.setReadLocatorListener(new ReadLocatorListener() {
            @Override
            public void saveReadLocator(ReadLocator readLocator) {

                Log.i("message", "-> saveReadLocator -> " + readLocator.toJson());
                if(mAuth.getCurrentUser()!=null) {
                    myRef = database.getReference("UserLocator/ReadLocator/" + mAuth.getCurrentUser().getUid());
                    myRef.child(fileName).setValue(readLocator);
                }
//                Toast.makeText(fReader.getClass().getconte, "Failed to launch Reader", Toast.LENGTH_SHORT).show();
                //fReader.close();
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

    private void checkSavedLocation() {
        myRef = database.getReference("UserLocator/ReadLocator/"+ mAuth.getCurrentUser().getUid()+"/"+fileName);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ReadLocator post = dataSnapshot.getValue(ReadLocator.class);
                if(post!=null){
                    Log.i("message", "-> hasil database -> " + post.toJson());
                    fReader.setReadLocator(post);
                }
                fReader.openBook(path);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }
}
