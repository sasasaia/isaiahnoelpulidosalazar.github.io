package com.github.isaiahnoelpulidosalazar.androiddependency;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SimpleList extends RecyclerView {
    private final List<String> data = new ArrayList<>();
    private final LinearLayoutManager llm;
    private OnItemClickListener listener;
    private OnItemLongClickListener listenerLong;
    private int itemPadding = 0, itemPaddingLeft = 0, itemPaddingTop = 0, itemPaddingRight = 0, itemPaddingBottom = 0;
    private boolean singlePadding = false;

    public SimpleList(Context context){
        super(context);
        llm = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        setLayoutManager(llm);
        refresh();
    }

    public SimpleList(Context context, AttributeSet attrs){
        super(context, attrs);
        llm = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        setLayoutManager(llm);
        refresh();
    }

    public SimpleList(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        llm = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        setLayoutManager(llm);
        refresh();
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
        refresh();
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        this.listenerLong = listener;
        refresh();
    }

    public void addItem(String a){
        data.add(a);
        refresh();
    }

    public void removeItem(int position){
        data.remove(position);
        refresh();
    }

    public void clear(){
        data.clear();
        refresh();
    }

    public List<String> getData(){
        return data;
    }

    private void refresh(){
        Adapter sla = new Adapter(data, listener, listenerLong);
        setAdapter(sla);
    }

    public void setItemPadding(int value){
        singlePadding = true;
        itemPadding = value;
    }

    public void setItemPadding(int left, int top, int right, int bottom){
        singlePadding = false;
        itemPaddingLeft = left;
        itemPaddingTop = top;
        itemPaddingRight = right;
        itemPaddingBottom = bottom;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.Holder>{
        List<String> data;
        OnItemClickListener listener;
        OnItemLongClickListener listenerLong;

        public Adapter(List<String> data, OnItemClickListener listener, OnItemLongClickListener listenerLong){
            this.data = data;
            this.listener = listener;
            this.listenerLong = listenerLong;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_list_item, parent, false);
            Holder slh = new Holder(v, listener, listenerLong);
            float scale = parent.getContext().getResources().getDisplayMetrics().density;
            if (singlePadding){
                slh.button.setPadding((int) (itemPadding * scale + 0.5f), (int) (itemPadding * scale + 0.5f), (int) (itemPadding * scale + 0.5f), (int) (itemPadding * scale + 0.5f));
            } else {
                slh.button.setPadding((int) (itemPaddingLeft * scale + 0.5f), (int) (itemPaddingTop * scale + 0.5f), (int) (itemPaddingRight * scale + 0.5f), (int) (itemPaddingBottom * scale + 0.5f));
            }
            return slh;
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            holder.button.setText(data.get(position));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class Holder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

            Button button;
            OnItemClickListener listener;
            OnItemLongClickListener listenerLong;

            public Holder(@NonNull View itemView, OnItemClickListener listener, OnItemLongClickListener listenerLong) {
                super(itemView);

                button = itemView.findViewById(R.id.simple_list_item_id);

                this.listener = listener;
                this.listenerLong = listenerLong;

                itemView.setOnClickListener(listener == null ? null : this);
                itemView.setOnLongClickListener(listenerLong == null ? null : this);
            }

            @Override
            public void onClick(View v) {
                listener.onItemClick(getAdapterPosition());
            }

            @Override
            public boolean onLongClick(View v) {
                listenerLong.onItemLongClick(getAdapterPosition());
                return true;
            }
        }
    }
}