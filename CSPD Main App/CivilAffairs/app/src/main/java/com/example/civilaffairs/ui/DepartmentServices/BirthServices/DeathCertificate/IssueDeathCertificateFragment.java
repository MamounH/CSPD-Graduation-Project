package com.example.civilaffairs.ui.DepartmentServices.BirthServices.DeathCertificate;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.civilaffairs.OurData;
import com.example.civilaffairs.R;
import com.example.civilaffairs.SignUpActivity;
import com.example.civilaffairs.ui.DepartmentServices.Provider.ProviderClass;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;


public class IssueDeathCertificateFragment extends Fragment {
    private ImageView img_card_death, img_family_death;
    private Spinner sp_relation;
    private EditText et_name_death;
    private static int RESULT_LOAD_IMG = 1;
    private Bitmap imageBitmap, imageBitmap1;
    private String imgExt, imgExt1, urlImage, urlImage1, nationalID, pushKey, relation;
    private int numImage = 0;
    private ProgressDialog dialog;
    private DatabaseReference refDeath;
    private DeathClass deathClass ;

   private OurData ourData;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_issue_death_certificate, container, false);

        img_card_death = view.findViewById(R.id.img_card_death);
        img_family_death = view.findViewById(R.id.img_family_death);
        sp_relation = view.findViewById(R.id.sp_relation);
        et_name_death = view.findViewById(R.id.et_name_death);

        ourData = new OurData(getContext());
        dialog = new ProgressDialog(getContext());
        deathClass = new DeathClass();

        refDeath = FirebaseDatabase.getInstance().getReference("Services").child("Certificate").child("IssueDeath");

        SharedPreferences prefs = getContext().getSharedPreferences(getString(R.string.national_id), MODE_PRIVATE);
        nationalID = prefs.getString("national_number", null);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, ourData.getRelations());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_relation.setAdapter(adapter);

        img_card_death.setOnClickListener(v -> {

            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            numImage = 0;

        });

        img_family_death.setOnClickListener(v -> {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            numImage = 1;

        });

        sp_relation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                relation = ourData.getRelations().get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                relation = ourData.getRelations().get(0);
            }
        });

        
        view.findViewById(R.id.bt_submission).setOnClickListener(v -> {
            if (checkValidation()) {

                DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            UploadData();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:

                            break;
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                


            }
        });


        return view;
    }

    private void UploadData() {

        dialog.setTitle(getActivity().getString(R.string.upload_data));
        dialog.setMessage(getActivity().getString(R.string.submission_of_the_application) + " ...");
        dialog.show();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference("ServicesCertificate").child("DeathCertificate").child(nationalID).child(UUID.randomUUID().toString());

        storageReference.putBytes(getFileDataFromBitmap(imageBitmap, imgExt)).addOnSuccessListener(taskSnapshot ->
                storageReference.getDownloadUrl().addOnCompleteListener(task -> {
                    urlImage = Objects.requireNonNull(task.getResult()).toString();

                    uploadAnotherData();

                })).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(getContext(), getContext().getString(R.string.upload_failde_please_check_your_connection), Toast.LENGTH_SHORT).show();

        });

    }

    private void uploadAnotherData() {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference("ServicesCertificate").child("DeathCertificate").child(nationalID).child(UUID.randomUUID().toString());

        storageReference.putBytes(getFileDataFromBitmap(imageBitmap1, imgExt1)).addOnSuccessListener(taskSnapshot ->
                storageReference.getDownloadUrl().addOnCompleteListener(task -> {
                    urlImage1 = Objects.requireNonNull(task.getResult()).toString();

                    deathClass.setCardImageUrl(urlImage);
                    deathClass.setImageFamilyUrl(urlImage1);
                    deathClass.setName(et_name_death.getText().toString());
                    deathClass.setRelationDeath(relation);
                    deathClass.setStatus("0");
                    deathClass.setNationalId(nationalID);
                    pushKey = refDeath.push().getKey();
                    deathClass.setPushKey(pushKey);

                    refDeath.child(nationalID).child(pushKey).setValue(deathClass).addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), getContext().getString(R.string.successful_upload_data), Toast.LENGTH_SHORT).show();
                        statusRequest();
                        dialog.dismiss();
                        sendNotificationForAdmin();
                        getActivity().finish();
                    }).addOnFailureListener(e -> {
                        dialog.dismiss();
                        Toast.makeText(getContext(), getContext().getString(R.string.upload_failde_please_check_your_connection), Toast.LENGTH_SHORT).show();

                    });

                })).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(getContext(), getContext().getString(R.string.upload_failde_please_check_your_connection), Toast.LENGTH_SHORT).show();

        });

    }

    private void statusRequest() {

        DatabaseReference refRequest = FirebaseDatabase.getInstance().getReference("StatusRequest");


        ProviderClass providerClass = new ProviderClass();

        providerClass.setStatus("0");
        providerClass.setPushKey(pushKey);
        providerClass.setNameStatus(getString(R.string.certificate_death));
        refRequest.child(nationalID).child("Certificate").child(pushKey).setValue(providerClass).addOnSuccessListener(aVoid ->
                Toast.makeText(getContext(), "push", Toast.LENGTH_SHORT).show());
    }

    private boolean checkValidation() {

        if (et_name_death.getText().toString().isEmpty())
            et_name_death.setError("");
        else {
            et_name_death.setError(null);
            return true;

        }

        return false;
    }

    private static byte[] getFileDataFromBitmap(@NotNull Bitmap bitmap, @NotNull String ImageExt) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Bitmap.CompressFormat format = ImageExt.equals("jpg") ? Bitmap.CompressFormat.JPEG : Bitmap.CompressFormat.PNG;

        bitmap.compress(format, 100, byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }

    private String getMimeType(Context context, @NonNull Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (Objects.requireNonNull(uri.getScheme()).equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(Objects.requireNonNull(uri.getPath()))).toString());

        }

        return extension;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == RESULT_LOAD_IMG) {
            try {
                if (numImage == 0) {
                    img_card_death.setImageResource(0);
                    final Uri imageUri = data.getData();
                    assert imageUri != null;
                    final InputStream imageStream = getContext().getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    img_card_death.setImageBitmap(selectedImage);
                    imgExt = getMimeType(getContext(), imageUri);
                    imageBitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
                    numImage = 1;
                } else {
                    img_family_death.setImageResource(0);
                    final Uri imageUri1 = data.getData();
                    assert imageUri1 != null;
                    final InputStream imageStream = getContext().getContentResolver().openInputStream(imageUri1);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    img_family_death.setImageBitmap(selectedImage);
                    imgExt1 = getMimeType(getContext(), imageUri1);
                    imageBitmap1 = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri1);
                    numImage = 0;
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(getContext(), getString(R.string.you_havent_picked_image), Toast.LENGTH_LONG).show();
        }
    }

    void sendNotificationForAdmin() {
        RequestQueue mRequestQue = Volley.newRequestQueue(getContext());

        JSONObject json = new JSONObject();
        try {
            json.put("to", "/topics/" + "Admin");
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title", getString(R.string.there_ia_a_new_request));
            notificationObj.put("body", getString(R.string.there_is_a_new_request_from)+nationalID);
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
                    header.put("authorization", "key=" + getString(R.string.key_notification).trim());
                    return header;
                }
            };


            mRequestQue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
