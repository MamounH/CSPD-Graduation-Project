package com.example.admincivil.Admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admincivil.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AdapterDelete extends RecyclerView.Adapter<AdapterDelete.ViewHolder> {

    private List<String> employeeList;

    AdapterDelete() {
    }

     void setEmployeeList(List<String> employeeList) {
        this.employeeList = employeeList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_delete_employee, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tx_name.setText(employeeList.get(position));
        holder.img_delete.setOnClickListener(v -> {
            DatabaseReference db_node = FirebaseDatabase.getInstance().getReference("Employee").child(employeeList.get(position));
            db_node.removeValue();
            notifyDataSetChanged();
        });
    }


    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tx_name;
        ImageButton img_delete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            tx_name = itemView.findViewById(R.id.tx_name);
            img_delete = itemView.findViewById(R.id.img_delete);

        }
    }
}
