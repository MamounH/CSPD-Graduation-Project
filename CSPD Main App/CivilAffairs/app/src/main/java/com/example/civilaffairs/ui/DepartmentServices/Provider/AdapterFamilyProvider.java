package com.example.civilaffairs.ui.DepartmentServices.Provider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.civilaffairs.OurData;
import com.example.civilaffairs.R;

import java.util.List;

public class AdapterFamilyProvider extends RecyclerView.Adapter<AdapterFamilyProvider.ViewHolder> {

    private Context context;
    private List<ProviderClass> providerList;
    private OurData ourData ;

     public AdapterFamilyProvider(Context context) {
        this.context = context;
        ourData = new OurData(context);
    }

    @NonNull
    @Override
    public AdapterFamilyProvider.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_provider, parent, false);
        return new ViewHolder(view);    }

     public void setMissingList(List<ProviderClass> providerList) {
        this.providerList = providerList;
            notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterFamilyProvider.ViewHolder holder, int position) {
        ProviderClass providerClass = providerList.get(position);

        holder.tx_name_req.setText(providerClass.getNameStatus());
        String status = providerClass.getStatus();
        String pushKey =providerClass.getPushKey();

        if (status.equals("0")){
            holder.tx_status.setText(ourData.getOStatus().get(0));
            holder.img_status.setImageResource(R.drawable.ic_no);
        }else if (status.equals("1")){
            holder.tx_status.setText(ourData.getOStatus().get(1));
            holder.img_status.setImageResource(R.drawable.ic_approve);

        }

    }

    @Override
    public int getItemCount() {
        return providerList.size();
    }

     static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tx_name_req ,tx_status ;
        ImageView img_status ;
         ViewHolder(@NonNull View itemView) {
            super(itemView);

            img_status = itemView.findViewById(R.id.img_status);
            tx_status = itemView.findViewById(R.id.tx_status);
            tx_name_req = itemView.findViewById(R.id.tx_name_req);

        }
    }
}
