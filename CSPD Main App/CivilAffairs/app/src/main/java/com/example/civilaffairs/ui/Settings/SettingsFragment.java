package com.example.civilaffairs.ui.Settings;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.InputType;
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
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.civilaffairs.OurData;
import com.example.civilaffairs.R;

import com.example.civilaffairs.UserClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;


public class SettingsFragment extends Fragment {

    private EditText et_calender, et_first, et_middle, et_last, et_national, et_email, et_phone, et_password, et_confirm;
    private RadioButton rd_male, rd_female, rd_confirm_info;
    private Spinner sp_social, sp_place;
    private Calendar myCalendar;
    private ImageView img_account;
    private String social,place, imgExt, urlImage;
    private Bitmap imageBitmap;
    private ProgressDialog dialog;
    private UserClass userClass;
    private static int RESULT_LOAD_IMG = 1;

    private ValueEventListener valueEventLis;

    private DatabaseReference refSetting;
    private Context context;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        context = view.getContext();

        refSetting = FirebaseDatabase.getInstance().getReference("Users");
        userClass = new UserClass();
        myCalendar = Calendar.getInstance();

        et_calender = view.findViewById(R.id.et_calender);
        CardView card_account = view.findViewById(R.id.img_card);
        img_account = view.findViewById(R.id.img_account);

        et_first = view.findViewById(R.id.et_firstName);
        et_middle = view.findViewById(R.id.et_middleName);
        et_last = view.findViewById(R.id.et_lastName);

        et_national = view.findViewById(R.id.et_ID);
        et_email = view.findViewById(R.id.et_email);
        et_phone = view.findViewById(R.id.et_phone);

        et_password = view.findViewById(R.id.et_password);
        et_confirm = view.findViewById(R.id.et_confirm_password);
        Button bt_create = view.findViewById(R.id.bt_create);

        rd_male = view.findViewById(R.id.rd_male);
        rd_female = view.findViewById(R.id.rd_female);
        rd_confirm_info = view.findViewById(R.id.rd_confirm_data);

        //sp_place = view.findViewById(R.id.sp_place);
        sp_social = view.findViewById(R.id.sp_social);
        sp_place = view.findViewById(R.id.sp_place);

        //Hidden Keyboard EditText
        et_calender.setInputType(InputType.TYPE_NULL);
        dialog = new ProgressDialog(context);
        OurData ourDataList = new OurData(context);
        fillSpinnerList(ourDataList.getSocial() , ourDataList.getPlace());
        DatePickerDialog.OnDateSetListener date = (v, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateCalender();
        };

        et_calender.setOnClickListener(v -> new DatePickerDialog(context, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());


        card_account.setOnClickListener(v -> {

            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);

        });
        bt_create.setOnClickListener(v -> {

            if (checkValidation()) {
                UploadDataFirebase();
            }

        });

        sp_social.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                social = adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sp_place.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                place = adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        loadData();

        return view;
    }

    private void loadData() {
        SharedPreferences prefs = context.getSharedPreferences(getString(R.string.national_id), MODE_PRIVATE);
        String national_number = prefs.getString("national_number", "0");

        Log.e("national0", national_number);
        valueEventLis = refSetting.child(national_number).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                et_first.setText(Objects.requireNonNull(dataSnapshot.child("nameFirst").getValue()) + "");
                et_middle.setText(dataSnapshot.child("nameMiddle").getValue() + "");
                et_last.setText(dataSnapshot.child("nameLast").getValue() + "");
                String gender = dataSnapshot.child("gender").getValue() + "";
                if (gender.equals("Male")) {
                    rd_male.setChecked(true);
                } else if (gender.equals("Female")) {
                    rd_female.setChecked(true);
                }
                et_calender.setText(dataSnapshot.child("dateOfBirth").getValue() + "");
                et_national.setText(national_number);
                et_email.setText(dataSnapshot.child("email").getValue() + "");
                et_phone.setText(dataSnapshot.child("phone").getValue() + "");
                et_password.setText(dataSnapshot.child("password").getValue() + "");

                String imgUrl = (String) dataSnapshot.child("urlImage").getValue();
                Glide.with(Objects.requireNonNull(getContext())).load(imgUrl).into(img_account);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, R.string.check_your_internet, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        card_account.setOnClickListener(view -> {
//
//            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//            photoPickerIntent.setType("image/*");
//            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
//
//        });
    }

    private boolean checkValidation() {

        if (et_first.getText().toString().isEmpty()) {
            et_first.setError(getString(R.string.enter_your_first_name));
        } else if (et_middle.getText().toString().isEmpty()) {
            et_middle.setError(getString(R.string.enter_your_middle_name));
        } else if (et_last.getText().toString().isEmpty()) {
            et_last.setError(getString(R.string.enter_your_last_name));
        } else if (!rd_male.isChecked()) {
            rd_male.setError(getString(R.string.select_your_gender));
        } else if (et_calender.getText().toString().isEmpty()) {
            et_calender.setError(getString(R.string.enter_your_date_of_birth));
        } else if (et_national.getText().toString().isEmpty() && et_national.getText().toString().length() != 10) {
            et_national.setError(getString(R.string.enter_your_nationality_number));
        } else if (et_email.getText().toString().isEmpty()) {
            et_email.setError(getString(R.string.enter_your_email));
        } else if (!isEmailValid(et_email.getText().toString())) {
            et_email.setError(getString(R.string.enter_the_email_correctly));
        } else if (et_phone.getText().toString().isEmpty()) {
            et_phone.setError(getString(R.string.enter_your_phone_number));
        } else if (et_phone.getText().toString().length() != 10) {
            et_phone.setError(getString(R.string.the_phone_number_must_be_digits));
        } else if (et_password.getText().toString().isEmpty()) {
            et_password.setError(getString(R.string.enter_your_password));
        } else if (!et_password.getText().toString().equals(et_confirm.getText().toString())) {
            et_password.setError(getString(R.string.password_not_match));
            et_confirm.setError(getString(R.string.password_not_match));
        } else if (et_confirm.getText().toString().isEmpty()) {
            et_confirm.setError(getString(R.string.enter_your_confirm_password));
        } else if (!rd_confirm_info.isChecked()) {
            rd_confirm_info.setError(getString(R.string.check_your_all_info_is_correct));
        } else if (imageBitmap == null) {
            Toast.makeText(context, getString(R.string.select_image_first), Toast.LENGTH_LONG).show();
        } else {
            et_first.setError(null);
            et_middle.setError(null);
            et_last.setError(null);
            et_calender.setError(null);
            et_national.setError(null);
            et_email.setError(null);
            et_phone.setError(null);
            et_password.setError(null);
            et_confirm.setError(null);
            rd_confirm_info.setError(null);
            return true;
        }
        return false;
    }


    private void updateCalender() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        et_calender.setText(sdf.format(myCalendar.getTime()));
    }

    private boolean isEmailValid(String email) {
        String regExpn = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);


        return matcher.matches();
    }

    private void fillSpinnerList(List<String> list , List<String> listPlace) {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_social.setAdapter(adapter);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, listPlace);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_place.setAdapter(adapter2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == RESULT_LOAD_IMG) {
            try {
                assert data != null;
                final Uri imageUri = data.getData();
                assert imageUri != null;
                final InputStream imageStream = context.getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                Glide.with(this).clear(img_account);
                Glide.with(this).load(selectedImage).into(img_account);

                imgExt = getMimeType(getContext(), imageUri);

                imageBitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getContext()).getContentResolver(), imageUri);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(context, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(context, getString(R.string.you_havent_picked_image), Toast.LENGTH_LONG).show();
        }

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

    private static byte[] getFileDataFromBitmap(@NotNull Bitmap bitmap, @NotNull String ImageExt) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Bitmap.CompressFormat format = ImageExt.equals("jpg") ? Bitmap.CompressFormat.JPEG : Bitmap.CompressFormat.PNG;

        bitmap.compress(format, 100, byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }

    private void UploadDataFirebase() {
        dialog.setTitle(getString(R.string.loading));
        dialog.setMessage(getString(R.string.edit_profile) + "...");
        dialog.show();

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageReference = storage.getReference("Person").child(UUID.randomUUID().toString());

        storageReference.putBytes(getFileDataFromBitmap(imageBitmap, imgExt)).addOnSuccessListener(taskSnapshot ->
                storageReference.getDownloadUrl().addOnCompleteListener(task -> {

                    urlImage = Objects.requireNonNull(task.getResult()).toString();

                    userClass.setNameFirst(et_first.getText().toString());
                    userClass.setNameMiddle(et_middle.getText().toString());
                    userClass.setNameLast(et_last.getText().toString());
                    if (rd_male.isChecked()) {
                        userClass.setGender("Male");
                    } else {
                        userClass.setGender("Female");
                    }
                    userClass.setNationalityNumber(et_national.getText().toString());
                    userClass.setDateOfBirth(et_calender.getText().toString());
                    userClass.setPlaceOfRegistration("Amman");
                    userClass.setEmail(et_email.getText().toString());
                    userClass.setPhone(et_phone.getText().toString());
                    userClass.setPassword(et_password.getText().toString());
                    userClass.setSocialStatus(social);
                    userClass.setUrlImage(urlImage);
                    refSetting.child(userClass.getNationalityNumber()).setValue(userClass).addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, getString(R.string.successful_upload_data), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        //status user Login or not


                    });
                }))
                .addOnFailureListener(e -> {
                    Toast.makeText(context, getString(R.string.upload_failde_please_check_your_connection), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });

    }

    @Override
    public void onPause() {
        super.onPause();
        if (valueEventLis != null)
            refSetting.removeEventListener(valueEventLis);
    }
}
