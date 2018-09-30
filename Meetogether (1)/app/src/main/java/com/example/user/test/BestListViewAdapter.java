package com.example.user.test;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class BestListViewAdapter extends RecyclerView.Adapter<BestListViewAdapter.ViewHolder> {

    List<ListData> mListdata;
    String stEmail;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public final View mView;
        public ImageView roomImage;

        public ViewHolder(View itemView) {
            super(itemView);
            roomImage = itemView.findViewById(R.id.root_image);
            mView = itemView;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public BestListViewAdapter(List<ListData> mListData, Context context) {
        this.mListdata = mListData;
        this.context = context;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public BestListViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.best_root_list, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Picasso.get()
                .load(mListdata.get(position).getPhoto())
                .fit()
                .centerInside()
                .into(holder.roomImage);

        //리사이클뷰 클릭리스너
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Context context = v.getContext();
                Toast.makeText(context, position +"", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context, RoomActivity.class);
                context.startActivity(intent);*/

                String roomUid = String.valueOf(position);
                Log.v("포지션",roomUid);
                Intent in = new Intent(context, BestRootImageActivity.class);
                in.putExtra("bestroot", roomUid);
                context.startActivity(in);
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mListdata.size();
    }
}