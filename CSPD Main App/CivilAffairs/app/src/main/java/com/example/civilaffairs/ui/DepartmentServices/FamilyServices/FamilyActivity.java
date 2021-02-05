package com.example.civilaffairs.ui.DepartmentServices.FamilyServices;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
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
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.civilaffairs.OurData;
import com.example.civilaffairs.R;

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
import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.HashMap;
import java.util.Locale;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class FamilyActivity extends AppCompatActivity {

    ImageButton img_family;
    EditText et_paterfamilias, et_address, et_national, et_book_num, et_release_date;
    Spinner sp_place_issue, sp_status;
    Calendar myCalendar;
    DatabaseReference refFamily;
    FamilyClass familyClass;
    Bitmap imageBitmap;
    String place,status, imgExt, urlImage, nationalID , pushKey;
    ProgressDialog dialog;

    private static int RESULT_LOAD_IMG = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        refFamily = FirebaseDatabase.getInstance().getReference("Services").child("Family");
        familyClass = new FamilyClass();
        dialog = new ProgressDialog(FamilyActivity.this);
        myCalendar = Calendar.getInstance();


        img_family = findViewById(R.id.img_family);
        et_paterfamilias = findViewById(R.id.et_paterfamilias);
        et_address = findViewById(R.id.et_address_family);
        et_national = findViewById(R.id.et_national);
        et_book_num = findViewById(R.id.et_book_num);
        et_release_date = findViewById(R.id.et_calender);
        sp_place_issue = findViewById(R.id.sp_place_issue);
        sp_status = findViewById(R.id.sp_status);

        et_release_date.setInputType(InputType.TYPE_NULL);

        SharedPreferences prefs = getSharedPreferences(getString(R.string.national_id), MODE_PRIVATE);
        nationalID = prefs.getString("national_number", null);
        et_national.setText(nationalID);
        et_national.setEnabled(false);
        OurData ourDataList = new OurData(this);

        ArrayAdapter adapter = new ArrayAdapter<>(FamilyActivity.this, android.R.layout.simple_spinner_item, ourDataList.getPlace());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_place_issue.setAdapter(adapter);

        ArrayAdapter adapter2 = new ArrayAdapter<>(FamilyActivity.this, android.R.layout.simple_spinner_item, ourDataList.getOrderStatus());
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_status.setAdapter(adapter2);




        sp_place_issue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                place = ourDataList.getPlace().get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                place = ourDataList.getPlace().get(0);
            }
        });

        sp_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                status = ourDataList.getOrderStatus().get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                status = ourDataList.getOrderStatus().get(0);
            }
        });



        img_family.setOnClickListener(v -> {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
        });

        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateCalender();
        };

        et_release_date.setOnClickListener(view -> new DatePickerDialog(FamilyActivity.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        findViewById(R.id.bt_submission).setOnClickListener(v -> {

            if (checkValidation()) {
                DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            uploadData();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:

                            break;
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }

        });
    }

    private void uploadData() {

        dialog.setTitle(getString(R.string.upload_data));
        dialog.setMessage(getString(R.string.submission_of_the_application) + " ...");
        dialog.show();


        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference("ServicesFamily").child(nationalID).child(UUID.randomUUID().toString());
        storageReference.putBytes(getFileDataFromBitmap(imageBitmap, imgExt)).addOnSuccessListener(taskSnapshot ->
                storageReference.getDownloadUrl().addOnCompleteListener(task -> {

                    urlImage = Objects.requireNonNull(task.getResult()).toString();
                    familyClass.setPaterfamilias(et_paterfamilias.getText().toString());
                    familyClass.setAddress(et_address.getText().toString());
                    familyClass.setNumberPaterfamilias(nationalID);
                    familyClass.setBookNumber(et_book_num.getText().toString());
                    familyClass.setPlaceIssue(place);
                    familyClass.setReleaseDate(et_release_date.getText().toString());
                    familyClass.setUrlImage(urlImage);
                    familyClass.setStatus("0");
                     pushKey = refFamily.push().getKey();

                    familyClass.setPushKey(pushKey);

                    String nationalID = et_national.getText().toString();

                    refFamily.child(nationalID).child(pushKey).setValue(familyClass).addOnSuccessListener(aVoid -> {

                        Toast.makeText(this, getString(R.string.successful_upload_data), Toast.LENGTH_SHORT).show();
                        sendNotificationForAdmin();
                        statusRequest();
                        dialog.dismiss();
                        finish();
                    });

                })).addOnFailureListener(e -> {
            Toast.makeText(this, getString(R.string.upload_failde_please_check_your_connection), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });


    }

    private void statusRequest() {
    DatabaseReference refRequest = FirebaseDatabase.getInstance().getReference("StatusRequest");
        String nationalID = et_national.getText().toString();

        ProviderClass providerClass = new ProviderClass();

        providerClass.setStatus("0");
        providerClass.setNameStatus(getString(R.string.renewing_the_family_book));
        providerClass.setPushKey(pushKey);

        refRequest.child(nationalID).child("Family").child(pushKey).setValue(providerClass).addOnSuccessListener(aVoid ->
                Toast.makeText(FamilyActivity.this, "push", Toast.LENGTH_SHORT).show());

    }

    public static byte[] getFileDataFromBitmap(@NotNull Bitmap bitmap, @NotNull String ImageExt) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Bitmap.CompressFormat format = ImageExt.equals("jpg") ? Bitmap.CompressFormat.JPEG : Bitmap.CompressFormat.PNG;

        bitmap.compress(format, 100, byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }

    public String getMimeType(Context context, @NonNull Uri uri) {
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == RESULT_LOAD_IMG) {
            try {
                img_family.setImageResource(0);
                final Uri imageUri = data.getData();
                assert imageUri != null;
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                img_family.setImageBitmap(selectedImage);
                imgExt = getMimeType(this, imageUri);
                imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(FamilyActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(FamilyActivity.this, getString(R.string.you_havent_picked_image), Toast.LENGTH_LONG).show();
        }
    }

    boolean checkValidation() {
        if (et_paterfamilias.getText().toString().isEmpty()) {
            et_paterfamilias.setError(getString(R.string.please_enter_paterfamilias_name));
        } else if (et_address.getText().toString().isEmpty()) {
            et_address.setError(getString(R.string.please_enter_your_address));
        } else if (et_national.getText().toString().isEmpty()) {
            et_national.setError(getString(R.string.enter_the_national_number_of_paterfamilias));
        } else if (et_release_date.getText().toString().isEmpty()) {
            et_release_date.setError(getString(R.string.release_date));
        } else {
            et_paterfamilias.setError(null);
            et_address.setError(null);
            et_national.setError(null);
            et_release_date.setError(null);
            return true;

        }

        return false;
    }

    void updateCalender() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        et_release_date.setText(sdf.format(myCalendar.getTime()));
    }

    void sendNotificationForAdmin() {
        RequestQueue mRequestQue = Volley.newRequestQueue(this);

        JSONObject json = new JSONObject();
        try {
            json.put("to", "/topics/" + "Admin");
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title", getString(R.string.there_ia_a_new_request));
            notificationObj.put("body", getString(R.string.there_is_a_new_request_from)+et_national.getText().toString());
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
