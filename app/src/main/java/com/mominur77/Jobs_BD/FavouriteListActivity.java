package com.mominur77.Jobs_BD;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mominur77.Jobs_BD.adapter.FavouriteListRecyclerAdapter;
import com.mominur77.Jobs_BD.database.Page;
import com.mominur77.Jobs_BD.viewmodel.FavouriteListActivityViewModel;

import java.util.ArrayList;
import java.util.List;

public class FavouriteListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FavouriteListRecyclerAdapter favouriteListRecyclerAdapter;
    private List<Page> list=new ArrayList<>();
    private FavouriteListActivityViewModel favouriteListActivityViewModel;
    private TextView noItemFoundTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_list);

        initializeAll();

        initializeRecyclerView();

    }

    private void initializeAll() {
        noItemFoundTextView=findViewById(R.id.favouriteListActivityNoItemTextViewId);
        favouriteListActivityViewModel= ViewModelProviders.of(this).get(FavouriteListActivityViewModel.class);
    }

    private void initializeRecyclerView() {
        recyclerView=findViewById(R.id.favouriteListRecyclerViewId);
        favouriteListRecyclerAdapter=new FavouriteListRecyclerAdapter(FavouriteListActivity.this,list);
        recyclerView.setLayoutManager(new LinearLayoutManager(FavouriteListActivity.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(favouriteListRecyclerAdapter);
        if (list.size()>0){
            noItemFoundTextView.setVisibility(View.GONE);
        }
    }

    public void dataObserver(){
        favouriteListActivityViewModel.getPageList().observe(this, new Observer<List<Page>>() {
            @Override
            public void onChanged(List<Page> updateList) {
                list.clear();
                list.addAll(updateList);
                if (favouriteListRecyclerAdapter!=null){
                    favouriteListRecyclerAdapter.notifyDataSetChanged();
                }
                if (list.size()>0){
                    noItemFoundTextView.setVisibility(View.GONE);
                }else {
                    noItemFoundTextView.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        dataObserver();
    }
}
