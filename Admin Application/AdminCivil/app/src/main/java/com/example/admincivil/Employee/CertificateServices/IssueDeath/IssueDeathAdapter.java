package com.example.admincivil.Employee.CertificateServices.IssueDeath;

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

public class IssueDeathAdapter extends RecyclerView.Adapter<IssueDeathAdapter.ViewHolder> {

    private Context context;
    private List<IssueDeathClass> deathList;
    private DatabaseReference refApprove, refStatus;

     IssueDeathAdapter(Context context) {
        this.context = context;
    }

    void setDeathList(List<IssueDeathClass> deathList) {
        this.deathList = deathList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_death, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        IssueDeathClass issueDeathClass = deathList.get(position);

        holder.tx_name.setText(issueDeathClass.getName());
        holder.tx_relation.setText(issueDeathClass.getRelationDeath());

        Glide.with(context).load(issueDeathClass.getCardImageUrl()).into(holder.img_card_death);
        Glide.with(context).load(issueDeathClass.getImageFamilyUrl()).into(holder.img_family_death);

        if (issueDeathClass.getStatus().equals("1")) {
            holder.tx_approve.setVisibility(View.VISIBLE);
            holder.img_approve.setVisibility(View.GONE);
        }

        holder.img_delete.setOnClickListener(v -> {
            String idNational = issueDeathClass.getNationalId();

            if (issueDeathClass.getStatus().equals("0")) {
                DatabaseReference db_node = FirebaseDatabase.getInstance().getReference("Services").child("Certificate").child("IssueDeath")
                        .child(idNational).child(issueDeathClass.getPushKey());
                db_node.removeValue();

                DatabaseReference db_node2 = FirebaseDatabase.getInstance().getReference("StatusRequest")
                        .child(idNational).child("Certificate").child(issueDeathClass.getPushKey());
                db_node2.removeValue();

                sendNotificationForAdmin(idNational,
                        context.getString(R.string.your_request_has_been_rejected) + " " + "Issue Death");
                notifyDataSetChanged();
            } else {
                DatabaseReference db_node = FirebaseDatabase.getInstance().getReference("Services").child("Certificate").child("IssueDeath")
                        .child(idNational).child(issueDeathClass.getPushKey());
                db_node.removeValue();

                DatabaseReference db_node2 = FirebaseDatabase.getInstance().getReference("StatusRequest")
                        .child(idNational).child("Certificate").child(issueDeathClass.getPushKey());
                db_node2.removeValue();
                notifyDataSetChanged();

            }
        });

        holder.img_approve.setOnClickListener(v->{

            String idNational = issueDeathClass.getNationalId();

            Toast.makeText(context, idNational, Toast.LENGTH_SHORT).show();
            refApprove = FirebaseDatabase.getInstance().getReference("StatusRequest");
            refApprove.child(idNational).child("Certificate").child(issueDeathClass.getPushKey()).child("status").setValue("1").addOnSuccessListener(aVoid -> {

                holder.tx_approve.setVisibility(View.VISIBLE);
                holder.img_approve.setVisibility(View.GONE);
                sendNotificationForAdmin(idNational,
                        context.getString(R.string.a_request_has_been_approved) +
                                " " + "Issue Death");
                notifyDataSetChanged();

                refStatus = FirebaseDatabase.getInstance().getReference("Services").child("Certificate").child("IssueDeath");
                refStatus.child(idNational).child(issueDeathClass.getPushKey()).child("status").setValue("1").addOnSuccessListener(aVoid1 ->
                        Log.d("Success Add","Done!"));

            });
        });

    }

    @Override
    public int getItemCount() {
        return deathList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton img_approve, img_delete;
        ImageView img_card_death, img_family_death;
        TextView tx_relation, tx_name, tx_approve;
         ViewHolder(@NonNull View itemView) {
            super(itemView);

            img_card_death = itemView.findViewById(R.id.img_card_death);
            img_family_death = itemView.findViewById(R.id.img_family_death);
            tx_relation = itemView.findViewById(R.id.tx_relation);
            tx_name = itemView.findViewById(R.id.tx_name);
            img_approve = itemView.findViewById(R.id.img_approve);
            img_delete = itemView.findViewById(R.id.img_delete);
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
