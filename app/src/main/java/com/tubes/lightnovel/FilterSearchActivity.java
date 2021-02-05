package com.tubes.lightnovel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.tubes.lightnovel.Adapter.NovelViewAdapter;
import com.tubes.lightnovel.Common.Common;
import com.tubes.lightnovel.Model.Novel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FilterSearchActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_search);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //recycler view
        recyclerView = (RecyclerView) findViewById(R.id.recycler_search);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        //bottom nav
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);
        bottomNavigationView.inflateMenu(R.menu.search_menu);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.filter_action:
                        showFilterDialog();
                        break;
                    case R.id.search_action:
                        showSearchDialog();
                        break;
                    default:
                        break;

                }

                return true;
            }
        });


    }

    private void showSearchDialog() {
        AlertDialog.Builder alertDialog= new AlertDialog.Builder(FilterSearchActivity.this);
        alertDialog.setTitle("Search");

        final LayoutInflater inflater = this.getLayoutInflater();
        View searchLayout = inflater.inflate(R.layout.dialog_search,null);

        final EditText querySearch = (EditText) searchLayout.findViewById(R.id.search_query);
        alertDialog.setView(searchLayout);
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setPositiveButton("SEARCH", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fetchSearchNovel(querySearch.getText().toString());
            }
        });

        alertDialog.show();
    }

    private void fetchSearchNovel(String filterQuery) {
        List<Novel> filtered = new ArrayList<>();
        for(Novel novel:Common.novelList){
            if(novel.getName().toLowerCase().contains(filterQuery.toLowerCase())){
                filtered.add(novel);
            }
        }

        recyclerView.setAdapter(new NovelViewAdapter(getBaseContext(),filtered));

    }

    private void showFilterDialog() {
        AlertDialog.Builder alertDialog= new AlertDialog.Builder(FilterSearchActivity.this);
        alertDialog.setTitle("Select Category");

        final LayoutInflater inflater = this.getLayoutInflater();
        View filter_Layout = inflater.inflate(R.layout.dialog_options,null);

        final AutoCompleteTextView fill_category = (AutoCompleteTextView) filter_Layout.findViewById(R.id.fill_category);
        final ChipGroup chipGroup = (ChipGroup) filter_Layout.findViewById(R.id.chipgroup);

        //for autocomplete
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, Common.categories);
        fill_category.setAdapter(adapter);
        fill_category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //clear
                fill_category.setText("");

                //with tags
                Chip chip = (Chip) inflater.inflate(R.layout.chip_item,null,false);
                chip.setText(((TextView)view).getText());
                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chipGroup.removeView(v);
                    }
                });
                chipGroup.addView(chip);
            }
        });

        alertDialog.setView(filter_Layout);
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setPositiveButton("FILTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                List<String> filterKey = new ArrayList<>();
                StringBuilder filterQuery = new StringBuilder("");

                for(int i = 0; i<chipGroup.getChildCount();i++){
                    Chip chip = (Chip) chipGroup.getChildAt(i);
                    filterKey.add(chip.getText().toString());
                }
                //categori di sort dari a-z dan dipisah dengna ","
                Collections.sort(filterKey);
                for(String key:filterKey){
                    filterQuery.append(key).append(",");
                }
                //remove , terakhir
                filterQuery.setLength(filterQuery.length()-1);

                fetchFilterCategory(filterQuery.toString());
            }
        });

        alertDialog.show();

    }

    private void fetchFilterCategory(String filterQuery) {
        List<Novel> filtered = new ArrayList<>();
        for(Novel novel:Common.novelList){
            if(novel.getCategory()!=null) {
                if (novel.getCategory().contains(filterQuery)) {
                    filtered.add(novel);
                }
            }
        }
        if(filtered.size()!=0)
            recyclerView.setAdapter(new NovelViewAdapter(getBaseContext(),filtered));
        else
            Toast.makeText(this, "No Result", Toast.LENGTH_LONG).show();

    }
}
