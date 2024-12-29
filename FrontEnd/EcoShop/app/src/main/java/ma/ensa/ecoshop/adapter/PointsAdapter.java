package ma.ensa.ecoshop.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ma.ensa.ecoshop.R;
import ma.ensa.ecoshop.model.Product;

public class PointsAdapter extends RecyclerView.Adapter<PointsAdapter.ViewHolder> {
    private List<Product.Points.Component> components;

    public PointsAdapter(List<Product.Points.Component> components) {
        this.components = components;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_points, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product.Points.Component component = components.get(position);
        holder.componentName.setText(component.getName());
        holder.componentValue.setText(component.getValue());
    }

    @Override
    public int getItemCount() {
        return components != null ? components.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView componentName;
        TextView componentValue;

        ViewHolder(View view) {
            super(view);
            componentName = view.findViewById(R.id.componentName);
            componentValue = view.findViewById(R.id.componentValue);
        }
    }
}