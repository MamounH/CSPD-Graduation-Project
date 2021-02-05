package com.example.admincivil.Employee.FamilyServices;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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

 class AdapterFamily extends RecyclerView.Adapter<AdapterFamily.ViewHolder> {

    private Context context;
    private List<FamilyClass> employeeList;
    private DatabaseReference refApprove ,refStatus;

    AdapterFamily(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_family, parent, false);
        return new ViewHolder(view);
    }

   void setFamilyList(List<FamilyClass> employeeList) {
        this.employeeList = employeeList;

        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        FamilyClass familyClass = employeeList.get(position);

        holder.tx_paterfamilias.setText(familyClass.getPaterfamilias());
        holder.tx_address.setText(familyClass.getAddress());
        holder.tx_national.setText(familyClass.getNumberPaterfamilias());
        holder.tx_book.setText(familyClass.getBookNumber());
        holder.tx_place.setText(familyClass.getPlaceIssue());
        holder.tx_date.setText(familyClass.getReleaseDate());
        holder.tx_name_status.setText(familyClass.getNameStatus());
        holder.tx_push_key.setText(familyClass.getPushKey());



        if (familyClass.getStatus().equals("1")){
            holder.tx_approve.setVisibility(View.VISIBLE);
            holder.img_approve.setVisibility(View.GONE);
        }


        Glide.with(context).load(familyClass.getUrlImage()).into(holder.img_family);

        holder.img_delete.setOnClickListener(v -> {
            String idNational = holder.tx_national.getText().toString();

            if (familyClass.getStatus().equals("0")) {
                DatabaseReference db_node = FirebaseDatabase.getInstance().getReference("Services").child("Family")
                        .child(idNational).child(familyClass.getPushKey());
                db_node.removeValue();

                DatabaseReference db_node2 = FirebaseDatabase.getInstance().getReference("StatusRequest")
                        .child(idNational).child("Family").child(familyClass.getPushKey());
                db_node2.removeValue();

                sendNotificationForAdmin(idNational,
                        context.getString(R.string.your_request_has_been_rejected) + " " + familyClass.getNameStatus());
                notifyDataSetChanged();
            }else {
                DatabaseReference db_node = FirebaseDatabase.getInstance().getReference("Services").child("Family")
                        .child(idNational).child(familyClass.getPushKey());
                db_node.removeValue();

                DatabaseReference db_node2 = FirebaseDatabase.getInstance().getReference("StatusRequest")
                        .child(idNational).child("Family").child(familyClass.getPushKey());
                db_node2.removeValue();
                notifyDataSetChanged();

            }
        });

        holder.img_approve.setOnClickListener(v -> {


           refApprove = FirebaseDatabase.getInstance().getReference("StatusRequest").child(familyClass.getNumberPaterfamilias());
            refApprove.child("Family").child(familyClass.getPushKey()).child("status").setValue("1").addOnSuccessListener(aVoid -> {

                holder.tx_approve.setVisibility(View.VISIBLE);
                holder.img_approve.setVisibility(View.GONE);
                sendNotificationForAdmin(familyClass.getNumberPaterfamilias(),
                        context.getString(R.string.a_request_has_been_approved) +
                        " " + familyClass.getNameStatus());
                notifyDataSetChanged();

               refStatus = FirebaseDatabase.getInstance().getReference("Services").child("Family");
               refStatus.child(familyClass.getNumberPaterfamilias()).child(familyClass.getPushKey()).child("status").setValue("1").addOnSuccessListener(aVoid1 ->
                       Log.d("Success Add","Done!"));

            });




        });

    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tx_paterfamilias, tx_address, tx_national, tx_book, tx_place, tx_date, tx_name_status, tx_push_key, tx_approve;
        ImageButton img_approve, img_delete;
        ImageView img_family;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_family = itemView.findViewById(R.id.img_family);
            tx_paterfamilias = itemView.findViewById(R.id.tx_paterfamilias);
            tx_address = itemView.findViewById(R.id.tx_address);
            tx_national = itemView.findViewById(R.id.tx_national);
            tx_book = itemView.findViewById(R.id.tx_book);
            tx_place = itemView.findViewById(R.id.tx_place);
            tx_date = itemView.findViewById(R.id.tx_date);
            img_approve = itemView.findViewById(R.id.img_approve);
            img_delete = itemView.findViewById(R.id.img_delete);
            tx_name_status = itemView.findViewById(R.id.tx_name_status);
            tx_push_key = itemView.findViewById(R.id.tx_push_key);
            tx_approve = itemView.findViewById(R.id.tx_approve);
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
