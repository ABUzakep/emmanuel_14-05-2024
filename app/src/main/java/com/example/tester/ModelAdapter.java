package com.example.tester;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ModelAdapter extends FirebaseRecyclerAdapter<MainModel, ModelAdapter.ViewHolder> implements Filterable {
    private List<MainModel> mainModelList;
    private List<MainModel> mainModelListFull; // Copy of original list for filtering
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(MainModel mainModel);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ModelAdapter(@NonNull FirebaseRecyclerOptions<MainModel> options) {
        super(options);
        mainModelListFull = new ArrayList<>();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull MainModel mainModel) {
        viewHolder.tvName.setText(mainModel.getName());
        viewHolder.tvPosition.setText(mainModel.getPosition());
        viewHolder.tvEmail.setText(mainModel.getEmail());

        // Set username here or use it for other purposes
        String username = mainModel.getUsername();
        Picasso.get().load(mainModel.getImage())
                .placeholder(R.drawable.ic_launcher_background)
                .into(viewHolder.imageView);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(mainModel);
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPosition, tvEmail;
        CircleImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPosition = itemView.findViewById(R.id.tvPosition);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            imageView = itemView.findViewById(R.id.imageView1);
        }
    }

    // Filter for search functionality
    @Override
    public Filter getFilter() {
        return mainModelFilter;
    }

    private Filter mainModelFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<MainModel> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(mainModelListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (MainModel item : mainModelListFull) {
                    if (item.getName().toLowerCase().contains(filterPattern) || item.getPosition().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mainModelList.clear();
            mainModelList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        mainModelListFull.clear();
        mainModelListFull.addAll(getSnapshots());
    }
}
