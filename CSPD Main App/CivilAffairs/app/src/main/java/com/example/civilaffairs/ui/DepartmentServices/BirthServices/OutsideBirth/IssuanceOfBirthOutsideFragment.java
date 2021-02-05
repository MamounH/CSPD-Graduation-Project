package com.example.civilaffairs.ui.DepartmentServices.BirthServices.OutsideBirth;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;


public class IssuanceOfBirthOutsideFragment extends Fragment {


    private ImageView img_family_book_birth;
    private EditText et_calender_birth_outside, et_birth_outside_country, et_foriegn_birth_certificate, et_full_name, et_mother_name;
    private OutsideClass outsideClass;
    private static int RESULT_LOAD_IMG = 1;
    private Bitmap imageBitmap;
    private String imgExt, urlImage, nationalID, pushKey;
    private ProgressDialog dialog;
    private DatabaseReference refOutside;
    private Calendar myCalendar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_issuance_of_birth_outside, container, false);

        img_family_book_birth = view.findViewById(R.id.img_family_book_birth);
        et_calender_birth_outside = view.findViewById(R.id.et_calender_birth_outside);
        et_birth_outside_country = view.findViewById(R.id.et_birth_outside_country);
        et_foriegn_birth_certificate = view.findViewById(R.id.et_foriegn_birth_certificate);
        et_full_name = view.findViewById(R.id.et_full_name);
        et_mother_name = view.findViewById(R.id.et_mother_name);

        refOutside = FirebaseDatabase.getInstance().getReference("Services").child("Certificate").child("Outside");

        outsideClass = new OutsideClass();
        myCalendar = Calendar.getInstance();
        dialog = new ProgressDialog(getContext());

        SharedPreferences prefs = getContext().getSharedPreferences(getString(R.string.national_id), MODE_PRIVATE);
        nationalID = prefs.getString("national_number", null);

        et_calender_birth_outside.setInputType(InputType.TYPE_NULL);
        DatePickerDialog.OnDateSetListener date = (v, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateCalender();
        };

        et_calender_birth_outside.setOnClickListener(v -> new DatePickerDialog(getContext(), date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        img_family_book_birth.setOnClickListener(v -> {

            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);

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
        StorageReference storageReference = storage.getReference("ServicesCertificate").child("Outside").child(nationalID).child(UUID.randomUUID().toString());

        storageReference.putBytes(getFileDataFromBitmap(imageBitmap, imgExt)).addOnSuccessListener(taskSnapshot ->
                storageReference.getDownloadUrl().addOnCompleteListener(task -> {
                    urlImage = Objects.requireNonNull(task.getResult()).toString();

                    outsideClass.setImageCardUrl(urlImage);
                    outsideClass.setDateOutside(et_calender_birth_outside.getText().toString());
                    outsideClass.setForeignBirth(et_foriegn_birth_certificate.getText().toString());
                    outsideClass.setCountry(et_birth_outside_country.getText().toString());
                    outsideClass.setFullName(et_full_name.getText().toString());
                    outsideClass.setMotherName(et_mother_name.getText().toString());
                    outsideClass.setStatus("0");
                    outsideClass.setNationalId(nationalID);
                    pushKey = refOutside.push().getKey();

                    outsideClass.setPushKey(pushKey);


                    refOutside.child(nationalID).child(pushKey).setValue(outsideClass).addOnSuccessListener(aVoid -> {

                        Toast.makeText(getContext(), getString(R.string.successful_upload_data), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        statusRequest();
                        sendNotificationForAdmin();
                        getActivity().finish();
                    });

                })).addOnFailureListener(e -> {
            Toast.makeText(getContext(), getString(R.string.upload_failde_please_check_your_connection), Toast.LENGTH_SHORT).show();
            dialog.dismiss();


        });

    }

    private void statusRequest() {

        DatabaseReference refRequest = FirebaseDatabase.getInstance().getReference("StatusRequest");


        ProviderClass providerClass = new ProviderClass();

        providerClass.setStatus("0");
        providerClass.setPushKey(pushKey);
        providerClass.setNameStatus(getString(R.string.certificate_outside));
        refRequest.child(nationalID).child("Certificate").child(pushKey).setValue(providerClass).addOnSuccessListener(aVoid ->
                Toast.makeText(getContext(), "push", Toast.LENGTH_SHORT).show());
    }

    private boolean checkValidation() {

        if (et_calender_birth_outside.getText().toString().isEmpty())
            et_calender_birth_outside.setError("");
        else if (et_birth_outside_country.getText().toString().isEmpty())
            et_birth_outside_country.setError("");
        else if (et_foriegn_birth_certificate.getText().toString().isEmpty())
            et_foriegn_birth_certificate.setError("");
        else if (et_full_name.getText().toString().isEmpty())
            et_full_name.setError("");
        else if (et_mother_name.getText().toString().isEmpty())
            et_mother_name.setError("");
        else {
            et_calender_birth_outside.setError(null);
            et_birth_outside_country.setError(null);
            et_foriegn_birth_certificate.setError(null);
            et_full_name.setError(null);
            et_mother_name.setError(null);
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
                img_family_book_birth.setImageResource(0);
                final Uri imageUri = data.getData();
                assert imageUri != null;
                final InputStream imageStream = getContext().getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                img_family_book_birth.setImageBitmap(selectedImage);
                imgExt = getMimeType(getContext(), imageUri);
                imageBitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
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

    private void updateCalender() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        et_calender_birth_outside.setText(sdf.format(myCalendar.getTime()));
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
