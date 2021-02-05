package com.example.admincivil.Employee.CertificateServices.IssueOutside;

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

public class IssueOutsideAdapter extends RecyclerView.Adapter<IssueOutsideAdapter.ViewHolder> {

    private Context context;
    private List<IssueOutsideClass> outsideList;
    private DatabaseReference refApprove, refStatus;

     IssueOutsideAdapter(Context context) {
        this.context = context;
    }
    void setOutsideList(List<IssueOutsideClass> outsideList) {
        this.outsideList = outsideList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_outside, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        IssueOutsideClass outsideClass = outsideList.get(position);

        holder.tx_date.setText(outsideClass.getDateOutside());
        holder.tx_country.setText(outsideClass.getCountry());
        holder.tx_foreign_birth.setText(outsideClass.getForeignBirth());
        holder.tx_name_outside.setText(outsideClass.getFullName());
        holder.tx_mother.setText(outsideClass.getMotherName());

        Glide.with(context).load(outsideClass.getImageCardUrl()).into(holder.img_card_outside);

        if (outsideClass.getStatus().equals("1")) {
            holder.tx_approve.setVisibility(View.VISIBLE);
            holder.img_approve.setVisibility(View.GONE);
        }

        holder.img_approve.setOnClickListener(v->{

            String idNational = outsideClass.getNationalId();

            Toast.makeText(context, idNational, Toast.LENGTH_SHORT).show();
            refApprove = FirebaseDatabase.getInstance().getReference("StatusRequest");
            refApprove.child(idNational).child("Certificate").child(outsideClass.getPushKey()).child("status").setValue("1").addOnSuccessListener(aVoid -> {

                holder.tx_approve.setVisibility(View.VISIBLE);
                holder.img_approve.setVisibility(View.GONE);
                sendNotificationForAdmin(idNational,
                        context.getString(R.string.a_request_has_been_approved) +
                                " " + "Certificate Outside");
                notifyDataSetChanged();

                refStatus = FirebaseDatabase.getInstance().getReference("Services").child("Certificate").child("Outside");
                refStatus.child(idNational).child(outsideClass.getPushKey()).child("status").setValue("1").addOnSuccessListener(aVoid1 ->
                        Log.d("Success Add","Done!"));

            });
        });

        holder.img_delete.setOnClickListener(v -> {
            String idNational = outsideClass.getNationalId();

            if (outsideClass.getStatus().equals("0")) {
                DatabaseReference db_node = FirebaseDatabase.getInstance().getReference("Services").child("Certificate").child("Outside")
                        .child(idNational).child(outsideClass.getPushKey());
                db_node.removeValue();

                DatabaseReference db_node2 = FirebaseDatabase.getInstance().getReference("StatusRequest")
                        .child(idNational).child("Certificate").child(outsideClass.getPushKey());
                db_node2.removeValue();

                sendNotificationForAdmin(idNational,
                        context.getString(R.string.your_request_has_been_rejected) + " " + "Certificate Outside");
                notifyDataSetChanged();
            } else {
                DatabaseReference db_node = FirebaseDatabase.getInstance().getReference("Services").child("Certificate").child("Outside")
                        .child(idNational).child(outsideClass.getPushKey());
                db_node.removeValue();

                DatabaseReference db_node2 = FirebaseDatabase.getInstance().getReference("StatusRequest")
                        .child(idNational).child("Certificate").child(outsideClass.getPushKey());
                db_node2.removeValue();
                notifyDataSetChanged();

            }
        });
    }

    @Override
    public int getItemCount() {
        return outsideList.size();
    }

     static class ViewHolder extends RecyclerView.ViewHolder {

         ImageButton img_approve, img_delete;
         ImageView img_card_outside ;
         TextView tx_date,tx_country,tx_foreign_birth,tx_name_outside,tx_mother,tx_approve ;

         ViewHolder(@NonNull View itemView) {
            super(itemView);

             img_approve = itemView.findViewById(R.id.img_approve);
             img_delete = itemView.findViewById(R.id.img_delete);
             img_card_outside = itemView.findViewById(R.id.img_card_outside);
             tx_date = itemView.findViewById(R.id.tx_date);
             tx_country = itemView.findViewById(R.id.tx_country);
             tx_foreign_birth = itemView.findViewById(R.id.tx_foreign_birth);
             tx_name_outside = itemView.findViewById(R.id.tx_name_outside);
             tx_mother = itemView.findViewById(R.id.tx_mother);
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
