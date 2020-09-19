package com.mominur77.Jobs_BD.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mominur77.Jobs_BD.FavouriteListActivity;
import com.mominur77.Jobs_BD.R;
import com.mominur77.Jobs_BD.WebViewActivity;
import com.mominur77.Jobs_BD.database.DatabaseClient;
import com.mominur77.Jobs_BD.database.Page;

import java.util.List;

public class FavouriteListRecyclerAdapter extends RecyclerView.Adapter<FavouriteListRecyclerAdapter.MyViewHolder> {

    private Context context;
    private List<Page> list;

    public FavouriteListRecyclerAdapter(Context context, List<Page> list){
        this.context=context;
        this.list=list;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.favourite_list_custom_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.title.setText(list.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;
        RelativeLayout rootLayout;
        ImageView deleteImageView;

        public MyViewHolder(@NonNull View view) {
            super(view);
            title=view.findViewById(R.id.favouriteListTitleTextViewId);
            rootLayout=view.findViewById(R.id.favouriteListRootLayoutId);
            deleteImageView=view.findViewById(R.id.favouriteListDeleteImageViewId);
            rootLayout.setOnClickListener(this);
            deleteImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            switch (view.getId()){
                case R.id.favouriteListRootLayoutId:
                    Intent intent=new Intent(context, WebViewActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("Url",list.get(getAdapterPosition()).getUrl());
                    context.startActivity(intent);
                    break;

                case R.id.favouriteListDeleteImageViewId:
                    DeleteAsyncTask deleteAsyncTask=new DeleteAsyncTask();
                    deleteAsyncTask.execute();
                    break;
            }
        }

        class DeleteAsyncTask extends AsyncTask<Void,Void,Void>{

            @Override
            protected Void doInBackground(Void... voids) {
                Page page=list.get(getAdapterPosition());
                DatabaseClient.getInstance(context).getAppDatabase().pageDao().delete(page);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                FavouriteListActivity favouriteListActivity= (FavouriteListActivity) context;
                favouriteListActivity.dataObserver();
                Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
