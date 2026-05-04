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

/**
 * A simplified {@link RecyclerView} subclass that renders a vertical list of string items,
 * each displayed as a {@link Button}, with support for click and long-click listeners.
 *
 * <p>Items are backed by an internal {@link List} of strings. Adding or removing items
 * automatically refreshes the adapter. Item padding can be configured uniformly or
 * per-side (in dp).</p>
 *
 * <p>Example usage in code:</p>
 * <pre>{@code
 * SimpleList list = new SimpleList(context);
 * list.addItem("Apple");
 * list.addItem("Banana");
 * list.setOnItemClickListener(position -> {
 *     String item = list.getData().get(position);
 *     Toast.makeText(context, item, Toast.LENGTH_SHORT).show();
 * });
 * }</pre>
 *
 * <p>It can also be declared in XML layout files and populated programmatically.</p>
 */
public class SimpleList extends RecyclerView {
    private final List<String> data = new ArrayList<>();
    private final LinearLayoutManager llm;
    private OnItemClickListener listener;
    private OnItemLongClickListener listenerLong;
    private int itemPadding = 0, itemPaddingLeft = 0, itemPaddingTop = 0, itemPaddingRight = 0, itemPaddingBottom = 0;
    private boolean singlePadding = false;

    /**
     * Constructs a {@code SimpleList} programmatically.
     *
     * @param context the Android {@link Context}
     */
    public SimpleList(Context context){
        super(context);
        llm = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        setLayoutManager(llm);
        refresh();
    }

    /**
     * Constructs a {@code SimpleList} from an XML layout.
     *
     * @param context the Android {@link Context}
     * @param attrs   attribute set from XML
     */
    public SimpleList(Context context, AttributeSet attrs){
        super(context, attrs);
        llm = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        setLayoutManager(llm);
        refresh();
    }

    /**
     * Constructs a {@code SimpleList} from an XML layout with a style attribute.
     *
     * @param context  the Android {@link Context}
     * @param attrs    attribute set from XML
     * @param defStyle default style attribute
     */
    public SimpleList(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        llm = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        setLayoutManager(llm);
        refresh();
    }

    /**
     * Sets a listener for item click events and refreshes the list.
     *
     * @param listener the {@link OnItemClickListener} to attach; pass {@code null} to remove
     */
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
        refresh();
    }

    /**
     * Sets a listener for item long-click events and refreshes the list.
     *
     * @param listener the {@link OnItemLongClickListener} to attach; pass {@code null} to remove
     */
    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        this.listenerLong = listener;
        refresh();
    }

    /**
     * Appends a new string item to the end of the list and refreshes the view.
     *
     * @param a the string to add as a list item
     */
    public void addItem(String a){
        data.add(a);
        refresh();
    }

    /**
     * Removes the item at the specified position and refreshes the view.
     *
     * @param position the zero-based index of the item to remove
     * @throws IndexOutOfBoundsException if position is out of range
     */
    public void removeItem(int position){
        data.remove(position);
        refresh();
    }

    /**
     * Removes all items from the list and refreshes the view.
     */
    public void clear(){
        data.clear();
        refresh();
    }

    /**
     * Returns the underlying list of string items.
     *
     * <p>Modifications to the returned list are reflected in the internal data but will not
     * automatically refresh the view — call {@link #addItem(String)} or {@link #removeItem(int)}
     * for managed mutations.</p>
     *
     * @return the mutable internal list of item strings
     */
    public List<String> getData(){
        return data;
    }

    /**
     * Rebuilds and sets the adapter to reflect the current data and listener state.
     *
     * <p>Called automatically after any data or listener change.</p>
     */
    private void refresh(){
        Adapter sla = new Adapter(data, listener, listenerLong);
        setAdapter(sla);
    }

    /**
     * Sets uniform padding (in dp) applied to all four sides of each list item button.
     *
     * @param value the padding value in dp
     */
    public void setItemPadding(int value){
        singlePadding = true;
        itemPadding = value;
    }

    /**
     * Sets individual padding values (in dp) for each side of each list item button.
     *
     * @param left   left padding in dp
     * @param top    top padding in dp
     * @param right  right padding in dp
     * @param bottom bottom padding in dp
     */
    public void setItemPadding(int left, int top, int right, int bottom){
        singlePadding = false;
        itemPaddingLeft = left;
        itemPaddingTop = top;
        itemPaddingRight = right;
        itemPaddingBottom = bottom;
    }

    /**
     * Callback interface for item click events.
     */
    public interface OnItemClickListener{
        /**
         * Invoked when a list item is tapped.
         *
         * @param position the zero-based index of the clicked item
         */
        void onItemClick(int position);
    }

    /**
     * Callback interface for item long-click events.
     */
    public interface OnItemLongClickListener {
        /**
         * Invoked when a list item is long-pressed.
         *
         * @param position the zero-based index of the long-clicked item
         */
        void onItemLongClick(int position);
    }

    /**
     * Internal {@link RecyclerView.Adapter} that binds string data to button-based list item views.
     */
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

        /**
         * ViewHolder that binds a single list item button and delegates click events.
         */
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