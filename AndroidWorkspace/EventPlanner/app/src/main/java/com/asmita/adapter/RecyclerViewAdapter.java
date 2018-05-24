package com.asmita.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.asmita.activities.R;
import com.asmita.interfaces.EventClickListener;
import com.asmita.models.ContentItem;
import com.asmita.models.Event;
import com.asmita.models.Header;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private ArrayList<Event> eventsArrayList;
    private EventClickListener clickListener;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private int filterType = 0;

    public RecyclerViewAdapter(ArrayList<Event> eventsArrayList)
    {
        /*
         * RecyclerViewAdapter Constructor to Initialize Data which we get from HomeActivity
         */

        this.eventsArrayList = eventsArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        /*
         * LayoutInflater is used to Inflate the view
         * from adapter_layout
         * for showing data in RecyclerView
         */

        if(viewType ==TYPE_HEADER)
        {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_header_layout, parent, false);
            return new VHHeader(v);
        }
        else
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_layout, parent, false);
            return new MyViewHolder(view);
        }

        /*View view = layoutInflater.inflate(R.layout.adapter_layout, parent, false);
        return new MyViewHolder(view);*/
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position)
    {
        /*
         * onBindViewHolder is used to Set all the respective data
         * to Textview form events ArrayList.
         */

        if(holder instanceof VHHeader)
        {
            Header currentItem = (Header) eventsArrayList.get(position);
            VHHeader VHheader = (VHHeader)holder;

            VHheader.headerTxt.setText(currentItem.getHeader());
        }
        else if(holder instanceof MyViewHolder)
        {
            ContentItem currentItem = (ContentItem) eventsArrayList.get(position);
            MyViewHolder VHitem = (MyViewHolder)holder;

             if (!TextUtils.isEmpty(currentItem.email))
                 VHitem.emailTxt.setText(currentItem.email);

            if(filterType == 0)
            {
                if (!TextUtils.isEmpty(currentItem.agenda))
                    VHitem.agendaTxt.setText(currentItem.agenda);
            }
            else
            {
                if (!TextUtils.isEmpty(currentItem.time))
                {
                    String agenda = currentItem.time + " - "+currentItem.agenda;
                    VHitem.agendaTxt.setText(agenda);
                }
            }
        }
    }

    @Override
    public int getItemCount()
    {
        /*
         * getItemCount is used to get the size of respective visitorsArrayList
         */

        return eventsArrayList.size();
    }

    public void updateData(int filterType)
    {
        /*
         * updateData method is to update items in the List and notify the adapter for the data change.
         */

        this.filterType = filterType;
        notifyDataSetChanged();
    }

    public int getItemViewType(int position)
    {
        if(isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position)
    {
        return eventsArrayList.get(position) instanceof Header;
    }

    public void setOnItemClickListener(final EventClickListener mItemClickListener)
    {
        this.clickListener = mItemClickListener;
    }

    class VHHeader extends RecyclerView.ViewHolder
    {
        @BindView(R.id.headerTxt) Button headerTxt;

        VHHeader(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        @BindView(R.id.emailTxt) TextView emailTxt;
        @BindView(R.id.agendaTxt) TextView agendaTxt;

        /*
         * MyViewHolder is used to Initializing the view.
         */

        MyViewHolder(View itemView)
        {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setTag(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            if (clickListener != null)
            {
                clickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }
}