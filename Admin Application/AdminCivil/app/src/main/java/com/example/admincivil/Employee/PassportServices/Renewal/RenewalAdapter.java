package com.example.admincivil.Employee.PassportServices.Renewal;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.bumptech.glide.Glide;
import com.example.admincivil.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RenewalAdapter extends RecyclerView.Adapter<RenewalAdapter.ViewHolder> {


    private Context context;
    private List<RenewalClass> renewalList;
    private DatabaseReference refApprove, refStatus;

    public RenewalAdapter(Context context) {
        this.context = context;
    }

    void setRenewalList(List<RenewalClass> renewalList) {
        this.renewalList = renewalList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RenewalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_reneal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RenewalAdapter.ViewHolder holder, int position) {

        RenewalClass renewalClass = renewalList.get(position);

        holder.tx_number.setText(renewalClass.getPassportNumber());
        holder.tx_date.setText(renewalClass.getDatePassport());
        holder.tx_country.setText(renewalClass.getCountry());
        holder.tx_address.setText(renewalClass.getAddress());

        Glide.with(context).load(renewalClass.getImagePassport()).into(holder.img_passport_renewal);


        if (renewalClass.getStatus().equals("1")) {
            holder.tx_approve.setVisibility(View.VISIBLE);
            holder.img_approve.setVisibility(View.GONE);
        }

        holder.img_approve.setOnClickListener(v->{

            String idNational = renewalClass.getNationalId();

            Toast.makeText(context, idNational, Toast.LENGTH_SHORT).show();
            refApprove = FirebaseDatabase.getInstance().getReference("StatusRequest");
            refApprove.child(idNational).child("Passport").child(renewalClass.getPushKey()).child("status").setValue("1").addOnSuccessListener(aVoid -> {

                holder.tx_approve.setVisibility(View.VISIBLE);
                holder.img_approve.setVisibility(View.GONE);
                sendNotificationForAdmin(idNational,
                        context.getString(R.string.a_request_has_been_approved) +
                                " " + "Passport AddEnum");
                notifyDataSetChanged();

                refStatus = FirebaseDatabase.getInstance().getReference("Services").child("Passport").child("Renewal");
                refStatus.child(idNational).child(renewalClass.getPushKey()).child("status").setValue("1").addOnSuccessListener(aVoid1 ->
                        Log.d("Success Add","Done!"));

            });
        });

        holder.img_delete.setOnClickListener(v -> {
            String idNational = renewalClass.getNationalId();

            if (renewalClass.getStatus().equals("0")) {
                DatabaseReference db_node = FirebaseDatabase.getInstance().getReference("Services").child("Passport").child("Renewal")
                        .child(idNational).child(renewalClass.getPushKey());
                db_node.removeValue();

                DatabaseReference db_node2 = FirebaseDatabase.getInstance().getReference("StatusRequest")
                        .child(idNational).child("Passport").child(renewalClass.getPushKey());
                db_node2.removeValue();

                sendNotificationForAdmin(idNational,
                        context.getString(R.string.your_request_has_been_rejected) + " " + "Passport AddEnum");
                notifyDataSetChanged();
            } else {
                DatabaseReference db_node = FirebaseDatabase.getInstance().getReference("Services").child("Passport").child("Renewal")
                        .child(idNational).child(renewalClass.getPushKey());
                db_node.removeValue();

                DatabaseReference db_node2 = FirebaseDatabase.getInstance().getReference("StatusRequest")
                        .child(idNational).child("Passport").child(renewalClass.getPushKey());
                db_node2.removeValue();
                notifyDataSetChanged();

            }
        });


    }

    @Override
    public int getItemCount() {
        return renewalList.size();
    }

     static class ViewHolder extends RecyclerView.ViewHolder {
       private   ImageButton img_approve, img_delete;
         private   TextView tx_number, tx_date,tx_country,tx_address,tx_approve ;
         private  ImageView img_passport_renewal ;
         ViewHolder(@NonNull View itemView) {
            super(itemView);

             img_approve = itemView.findViewById(R.id.img_approve);
             img_delete = itemView.findViewById(R.id.img_delete);
             tx_number = itemView.findViewById(R.id.tx_number);
             tx_date = itemView.findViewById(R.id.tx_date);
             tx_country = itemView.findViewById(R.id.tx_country);
             tx_address = itemView.findViewById(R.id.tx_address);
             tx_approve = itemView.findViewById(R.id.tx_approve);
             img_passport_renewal = itemView.findViewById(R.id.img_passport_renewal);


         }
    }

    private void sendNotificationForAdmin(String topicKey, String description) {
        RequestQueue mRequestQue = Volley.newRequestQueue(context);

        JSONObject json = new JSONObject();
        try {
            json.put("to", "/topics/" + topicKey);
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title", context.getString(R.string.civil_and_citizenship_department));
            notificationObj.put("body", description);
            //replace notification with data when went send data
            json.put("notification", notificationObj);

            String URL = "https://fcm.googleapis.com/fcm/send";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                    json,
                    response -> Log.d("MUR", "onResponse: "),
                    error -> Log.d("MUR", "onError: " + error.networkResponse)
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=" + context.getString(R.string.key_notification).trim());
                    return header;
                }
            };

            mRequestQue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
