package com.example.civilaffairs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    EditText et_calender, et_first, et_middle, et_last, et_national, et_email, et_phone, et_password, et_confirm;
    RadioButton rd_male, rd_female, rd_confirm_info;
    Button bt_create;
    Spinner sp_place, sp_social;
    Calendar myCalendar;
    CardView card_account;
    ImageView img_account;
    String place, social, imgExt, urlImage;
    Bitmap imageBitmap;
    ProgressDialog dialog;
    UserClass userClass;
    List<String> socialList;
    OurData ourDataList;
    private static int RESULT_LOAD_IMG = 1;
    DatabaseReference refSignUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        refSignUp = FirebaseDatabase.getInstance().getReference("Users");
        userClass = new UserClass();
        socialList = new ArrayList<>();
        ourDataList = new OurData(this);


        //Hidden Action bar
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        myCalendar = Calendar.getInstance();
        dialog = new ProgressDialog(SignUpActivity.this);

        et_calender = findViewById(R.id.et_calender);
        card_account = findViewById(R.id.img_card);
        img_account = findViewById(R.id.img_account);

        et_first = findViewById(R.id.et_firstName);
        et_middle = findViewById(R.id.et_middleName);
        et_last = findViewById(R.id.et_lastName);

        et_national = findViewById(R.id.et_ID);
        et_email = findViewById(R.id.et_email);
        et_phone = findViewById(R.id.et_phone);

        et_password = findViewById(R.id.et_password);
        et_confirm = findViewById(R.id.et_confirm_password);
        bt_create = findViewById(R.id.bt_create);

        rd_male = findViewById(R.id.rd_male);
        rd_female = findViewById(R.id.rd_female);
        rd_confirm_info = findViewById(R.id.rd_confirm_data);

        sp_place = findViewById(R.id.sp_place);
        sp_social = findViewById(R.id.sp_social);

        //Hidden Keyboard EditText
        et_calender.setInputType(InputType.TYPE_NULL);


        fillSpinnerList(ourDataList.getSocial() , ourDataList.getPlace());

        card_account.setOnClickListener(view -> {

            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);

        });
        et_phone.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().startsWith("+9627")) {
                    et_phone.setText("+9627");
                    Selection.setSelection(et_phone.getText(), et_phone.getText().length());

                }

            }
        });
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateCalender();
        };

        et_calender.setOnClickListener(view -> new DatePickerDialog(SignUpActivity.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        sp_place.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                place = adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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

        bt_create.setOnClickListener(v -> {

            if (checkValidation()) {
                dialog.setTitle(getString(R.string.upload_data));
                dialog.setMessage(getString(R.string.create_account) + " ...");
                dialog.show();

                refSignUp.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(et_national.getText().toString())) {
                            et_national.setError(getString(R.string.the_user_already_exists));
                            dialog.dismiss();
                        } else {
                            UploadDataFirebase();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    void updateCalender() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        et_calender.setText(sdf.format(myCalendar.getTime()));
    }

    boolean checkValidation() {

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
        } else if (et_phone.getText().toString().length() != 13) {
            et_phone.setError(getString(R.string.the_phone_number_must_be_digits));
        } else if ( et_password.getText().toString().length() < 8 ) {
            et_password.setError(getString(R.string.password_digits));
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
            Toast.makeText(this, getString(R.string.select_image_first), Toast.LENGTH_LONG).show();
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

    public boolean isEmailValid(String email) {
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

    @NotNull
    public static byte[] getFileDataFromBitmap(@NotNull Bitmap bitmap, @NotNull String ImageExt) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Bitmap.CompressFormat format = ImageExt.equals("jpg") ? Bitmap.CompressFormat.JPEG : Bitmap.CompressFormat.PNG;

        bitmap.compress(format, 100, byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }

    private void UploadDataFirebase() {

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
                    refSignUp.child(userClass.getNationalityNumber()).setValue(userClass).addOnSuccessListener(aVoid -> {
                        Toast.makeText(SignUpActivity.this, getString(R.string.successful_upload_data), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        //status user Login or not
                        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.status_login), MODE_PRIVATE).edit();
                        editor.putString("key", "1");
                        editor.apply();
                        //save national number on memory cash user
                        SharedPreferences.Editor editor1 = getSharedPreferences(getString(R.string.national_id), MODE_PRIVATE).edit();
                        editor1.putString("national_number", et_national.getText().toString());
                        editor1.apply();

                        startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                        finish();

                    });
                }))
                .addOnFailureListener(e -> {
                    Toast.makeText(this, getString(R.string.upload_failde_please_check_your_connection), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });

    }

    void fillSpinnerList(List<String> list , List<String> list1) {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(SignUpActivity.this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_social.setAdapter(adapter);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(SignUpActivity.this, android.R.layout.simple_spinner_item, list1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_place.setAdapter(adapter1);

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
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK && reqCode == RESULT_LOAD_IMG) {
            try {
                img_account.setImageResource(0);
                final Uri imageUri = data.getData();
                assert imageUri != null;
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                img_account.setImageBitmap(selectedImage);
                imgExt = getMimeType(this, imageUri);
                imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(SignUpActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(SignUpActivity.this, getString(R.string.you_havent_picked_image), Toast.LENGTH_LONG).show();
        }
    }


}
