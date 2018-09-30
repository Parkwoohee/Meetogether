package com.example.user.test;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.ViewHolder> {

    List<ListData> mListdata;
    String stEmail;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public final View mView;
        public TextView tvEmail;
        public ImageView roomImage;
        public TextView mRoomtitle;

        public ViewHolder(View itemView) {
            super(itemView);
            mRoomtitle = itemView.findViewById(R.id.mText);
            tvEmail = itemView.findViewById(R.id.mDate);
            roomImage = itemView.findViewById(R.id.mImage);
            mView = itemView;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListViewAdapter(List<ListData> mListData, Context context) {
        this.mListdata = mListData;
        this.context = context;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listview_item, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.tvEmail.setText(mListdata.get(position).getEmail());
        holder.mRoomtitle.setText(mListdata.get(position).getRoom());
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

                String roomUid = mListdata.get(position).getKey();
                Intent in = new Intent(context, RoomActivity.class);
                in.putExtra("roomUid", roomUid);
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