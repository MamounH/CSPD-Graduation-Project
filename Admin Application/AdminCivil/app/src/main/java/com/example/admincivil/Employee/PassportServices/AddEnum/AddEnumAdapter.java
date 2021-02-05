package com.example.admincivil.Employee.PassportServices.AddEnum;

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

class AddEnumAdapter extends RecyclerView.Adapter<AddEnumAdapter.ViewHolder> {

    private Context context;
    private List<AddEnumClass> addenumList;
    private DatabaseReference refApprove, refStatus;


     AddEnumAdapter(Context context) {
        this.context = context;
    }

    void setAddEnumList(List<AddEnumClass> addenumList) {
        this.addenumList = addenumList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_addenum, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

         AddEnumClass addEnumClass = addenumList.get(position);

         holder.tx_passport_number.setText(addEnumClass.getPassportNumber());
         holder.tx_child.setText(addEnumClass.getChildName());

        Glide.with(context).load(addEnumClass.getImagePassportUrl()).into(holder.img_passport_addenum);
        Glide.with(context).load(addEnumClass.getImageBirthCertificateUrl()).into(holder.img_birth_addenum);

        if (addEnumClass.getStatus().equals("1")) {
            holder.tx_approve.setVisibility(View.VISIBLE);
            holder.img_approve.setVisibility(View.GONE);
        }

        holder.img_approve.setOnClickListener(v->{

            String idNational = addEnumClass.getNationalId();

            Toast.makeText(context, idNational, Toast.LENGTH_SHORT).show();
            refApprove = FirebaseDatabase.getInstance().getReference("StatusRequest");
            refApprove.child(idNational).child("Passport").child(addEnumClass.getPushKey()).child("status").setValue("1").addOnSuccessListener(aVoid -> {

                holder.tx_approve.setVisibility(View.VISIBLE);
                holder.img_approve.setVisibility(View.GONE);
                sendNotificationForAdmin(idNational,
                        context.getString(R.string.a_request_has_been_approved) +
                                " " + "Passport AddEnum");
                notifyDataSetChanged();

                refStatus = FirebaseDatabase.getInstance().getReference("Services").child("Passport").child("AddEnum");
                refStatus.child(idNational).child(addEnumClass.getPushKey()).child("status").setValue("1").addOnSuccessListener(aVoid1 ->
                        Log.d("Success Add","Done!"));

            });
        });

        holder.img_delete.setOnClickListener(v -> {
            String idNational = addEnumClass.getNationalId();

            if (addEnumClass.getStatus().equals("0")) {
                DatabaseReference db_node = FirebaseDatabase.getInstance().getReference("Services").child("Passport").child("AddEnum")
                        .child(idNational).child(addEnumClass.getPushKey());
                db_node.removeValue();

                DatabaseReference db_node2 = FirebaseDatabase.getInstance().getReference("StatusRequest")
                        .child(idNational).child("Passport").child(addEnumClass.getPushKey());
                db_node2.removeValue();

                sendNotificationForAdmin(idNational,
                        context.getString(R.string.your_request_has_been_rejected) + " " + "Passport AddEnum");
                notifyDataSetChanged();
            } else {
                DatabaseReference db_node = FirebaseDatabase.getInstance().getReference("Services").child("Passport").child("AddEnum")
                        .child(idNational).child(addEnumClass.getPushKey());
                db_node.removeValue();

                DatabaseReference db_node2 = FirebaseDatabase.getInstance().getReference("StatusRequest")
                        .child(idNational).child("Passport").child(addEnumClass.getPushKey());
                db_node2.removeValue();
                notifyDataSetChanged();

            }
        });


    }

    @Override
    public int getItemCount() {
        return addenumList.size();
    }

     static class ViewHolder extends RecyclerView.ViewHolder {

         ImageView img_passport_addenum,img_birth_addenum ;
         TextView tx_passport_number, tx_child,tx_approve ;
         ImageButton img_approve, img_delete;

         ViewHolder(@NonNull View itemView) {
            super(itemView);
             img_approve = itemView.findViewById(R.id.img_approve);
             img_passport_addenum = itemView.findViewById(R.id.img_passport_addenum);
             img_birth_addenum = itemView.findViewById(R.id.img_birth_addenum);
             tx_passport_number = itemView.findViewById(R.id.tx_passport_number);
             tx_child = itemView.findViewById(R.id.tx_child);
             tx_approve = itemView.findViewById(R.id.tx_approve);
             img_delete = itemView.findViewById(R.id.img_delete);


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
