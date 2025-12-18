package com.example.amaal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.amaal.R;
import com.example.amaal.model.Deed;
import java.util.ArrayList;
import java.util.List;

public class DeedAdapter extends RecyclerView.Adapter<DeedAdapter.DeedViewHolder> {

    private List<Deed> deeds = new ArrayList<>();
    private final OnDeedClickListener listener;

    public interface OnDeedClickListener {
        void onDeedComplete(Deed deed);
        void onDeedLongClick(Deed deed);
    }

    public DeedAdapter(OnDeedClickListener listener) {
        this.listener = listener;
    }

    public void setDeeds(List<Deed> deeds) {
        this.deeds = deeds;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_deed, parent, false);
        return new DeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeedViewHolder holder, int position) {
        Deed deed = deeds.get(position);
        holder.tvTitle.setText(deed.title);
        holder.tvCategory.setText(deed.category.getDisplayName());
        
        holder.cbComplete.setOnCheckedChangeListener(null);
        holder.cbComplete.setChecked(deed.isCompleted);
        
        holder.cbComplete.setOnClickListener(v -> listener.onDeedComplete(deed));
        
        holder.itemView.setOnLongClickListener(v -> {
            if (deed.isPrayer) {
                listener.onDeedLongClick(deed);
                return true; 
            }
            return false;
        });
        
        int colorRes = deed.category.getWeight() >= 90 ? android.R.color.holo_red_dark : android.R.color.holo_blue_dark;
        holder.categoryIndicator.setBackgroundResource(colorRes);
    }

    @Override
    public int getItemCount() {
        return deeds.size();
    }

    static class DeedViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCategory;
        CheckBox cbComplete;
        View categoryIndicator;

        public DeedViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            cbComplete = itemView.findViewById(R.id.cbComplete);
            categoryIndicator = itemView.findViewById(R.id.viewCategoryIndicator);
        }
    }
}