package com.mominur77.Jobs_BD.viewmodel;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mominur77.Jobs_BD.database.DatabaseClient;
import com.mominur77.Jobs_BD.database.Page;

import java.util.List;

public class FavouriteListActivityViewModel extends AndroidViewModel {

    private MutableLiveData<List<Page>> pageList;
    Context context;

    public FavouriteListActivityViewModel(@NonNull Application application) {
        super(application);
        context=application;
    }

    public LiveData<List<Page>> getPageList(){
        if (pageList==null){
            pageList=new MutableLiveData<>();
        }
        PageListAsyncTask asyncTask=new PageListAsyncTask();
        asyncTask.execute();
        return pageList;
    }


    class PageListAsyncTask extends AsyncTask<Void, Void, List<Page>> {

        @Override
        protected List<Page> doInBackground(Void... voids) {
            List<Page> pages = DatabaseClient
                    .getInstance(context)
                    .getAppDatabase()
                    .pageDao()
                    .getAll();
            pageList.postValue(pages);
            return null;
        }
    }
}
