package com.ka12.ayirimatrimony;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class user_data extends AppCompatActivity {
    TextInputEditText name, family, age;
    Button submit, male, female, upload;
    LottieAnimationView up, loading;
    CardView card_one, card_two;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    String gender = null, img_link;
    Boolean is_gender_clicked = false;
    // ProgressBar progress;
    public static final String D_LINK = "com.ka12.ayiri_matrimony.this_is_where_download_link_is_saved";
    public static final String P_LINK = "com.ka12.ayiri_matrimony.this_is_where_local_link_is_saved";
    public static final String LOGIN = "com.ka12.ayiri_matrimony_login_details";
    public static final String KEY = "com.ka12.ayiri_matrimony_this_is_where_key_is_stored";
    //database needs
    public static final String PHONE = "com.ka12.ayiri_matrimony_phone_number_is_saved_here";
    public static final String GENDER = "com.ka12.ayiri_matrimony_this_is_where_gender_is_stored";
    String user_num;
    //the following are for uploading the image
    Uri image_url;
    String download_link;
    public StorageReference storageReference;
    public static final int PICK_IMAGE_REQUEST = 1;
    Boolean is_connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
        //initialization
        name = findViewById(R.id.name);
        family = findViewById(R.id.family);
        age = findViewById(R.id.age);
        submit = findViewById(R.id.submit);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        upload = findViewById(R.id.upload);
        //   progress = findViewById(R.id.progress);
        card_one = findViewById(R.id.card_one);
        card_two = findViewById(R.id.card_two);
        up = findViewById(R.id.up);
        loading = findViewById(R.id.loading);
        //status bar colors
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#ED8A6B"));
        //hiding card 2
        card_two.setVisibility(View.GONE);
        loading.setVisibility(View.GONE);
        check_network();
        //TODO:safely remove this section
        //fetching the image link from firebase
        SharedPreferences edit = getSharedPreferences(D_LINK, MODE_PRIVATE);
        img_link = edit.getString("link", "something_went_wrong");
        Log.d("download ", "received " + img_link);

        //fetching the user's phone number to be used as key in firebase
        SharedPreferences get_number = getSharedPreferences(PHONE, MODE_PRIVATE);
        user_num = get_number.getString("key", "9999999999");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!is_connected)
                {
                    Toast.makeText(user_data.this, "Please connect to internet", Toast.LENGTH_SHORT).show();
                } else if (Objects.requireNonNull(name.getText()).toString().equals("") || Objects.requireNonNull(age.getText()).toString().equals("")
                        || Objects.requireNonNull(family.getText()).toString().equals("") && gender != null) {
                    Toast.makeText(user_data.this, "Please enter all the details", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences.Editor edit = getSharedPreferences(GENDER, MODE_PRIVATE).edit();
                    edit.putString("gender", gender).apply();
                    card_one.setVisibility(View.GONE);
                    card_two.setVisibility(View.VISIBLE);
                }
            }
        });
        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor edit = getSharedPreferences(GENDER, MODE_PRIVATE).edit();
                edit.putString("gender", "male").apply();
                gender = "male";
                is_gender_clicked = true;
                male.setBackgroundColor(Color.BLACK);
            }
        });
        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor edit = getSharedPreferences(GENDER, MODE_PRIVATE).edit();
                edit.putString("gender", "female").apply();
                gender = "female";
                is_gender_clicked = true;
                male.setBackgroundColor(Color.BLACK);
            }
        });
        submit.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                try {
                    Log.d("key ", "inside try");
                    firebaseDatabase = FirebaseDatabase.getInstance();
                    reference = firebaseDatabase.getReference().child("male").child("-MNYy-7-otGTVkRMwfcp").child("family");
                    reference.setValue("demo");
                    Toast.makeText(user_data.this, "trying to dynamically change the value!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.d("key ", "error :" + e.getMessage());
                }
                return false;
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storageReference = FirebaseStorage.getInstance().getReference("profile_pictures");
                open_file_chooser();
            }
        });
        //delete the following code
        female.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent in = new Intent(user_data.this, com.ka12.ayirimatrimony.MainActivity.class);
                startActivity(in);
                finish();
                return false;
            }
        });
        //hiding submit button
    }

    private void upload_image() {
        if (image_url != null) {
            Toast.makeText(this, "uploading image", Toast.LENGTH_LONG).show();
            String name_file = System.currentTimeMillis() + "." + get_file_extension(image_url);
            StorageReference filereference = storageReference.child(name_file);

            filereference.putFile(image_url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //pushing the values into firebase
                            push_into_database();
                            //getting the download link
                            download_link = uri.toString();
                            Log.d("download ", "sending " + download_link);
                            //passing it to shared preferences
                            SharedPreferences.Editor edit = getSharedPreferences(D_LINK, MODE_PRIVATE).edit();
                            edit.putString("link", download_link).apply();
                            Log.d("download ", "get download :" + download_link);
                        }
                    });
                    // submit.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Uploaded successfully", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Error :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Please select a image", Toast.LENGTH_SHORT).show();
        }
    }

    //the following method is to get the file extension of the image file
    private String get_file_extension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void open_file_chooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            image_url = data.getData();
            SharedPreferences.Editor edit = getSharedPreferences(P_LINK, MODE_PRIVATE).edit();
            edit.putString("plink", image_url.toString()).apply();
            up.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);
            if(is_connected) {
                upload_image();
                upload.setText("Uploading...");
            }
            //  Picasso.get().load(image_url).into(profile);
        }
    }

    private void push_into_database() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        if (gender.equals("male"))
            reference = firebaseDatabase.getReference().child("male");
        else
            reference = firebaseDatabase.getReference().child("female");

        //retriving the values
        String uname = Objects.requireNonNull(name.getText()).toString().trim();
        String ufamily = Objects.requireNonNull(family.getText()).toString().trim();
        String uage = Objects.requireNonNull(age.getText()).toString().trim();
        String ugender = gender.trim();
        //helperclass
        heplerclass help = new heplerclass();
        help.setName(uname);
        help.setFamily(ufamily);
        help.setAge(uage);
        help.setGender(ugender);
        help.setReceived("received");
        // help.setSent("sent");
        help.setPhone(user_num);
        //TODO:do not forget to uncomment the following code
        //help.setLink(download_link);
        help.setLink("this.is.a.dummy.link");

        reference.child(user_num).setValue(help).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(user_data.this, "success", Toast.LENGTH_SHORT).show();

                //testing change to true
                SharedPreferences.Editor edist = getSharedPreferences(LOGIN, MODE_PRIVATE).edit();
                edist.putBoolean("login", false).apply();
                Intent in = new Intent(user_data.this, com.ka12.ayirimatrimony.MainActivity.class);
                startActivity(in);
                finish();
                Log.d("push ", "from inside " + reference.getKey());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(user_data.this, "error :" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    //TODO: remove the following method
    /*
    public String get_random_name() {
        String num = "";
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            num = num + (random.nextInt(9));
        }
        Log.d("num ", num);
        return num;
    }
     */

    @Keep
    static class heplerclass {
        public String name;
        public String family;
        public String age;
        public String gender;
        public String received;
        public String sent;
        public String link;
        public String phone;

        public heplerclass() {

        }

        public heplerclass(String name, String family, String gender, String link, String phone,
                           String age, String sent, String received) {
            this.name = name;
            this.family = family;
            this.gender = gender;
            this.link = link;
            this.age = age;
            this.sent = sent;
            this.received = received;
            this.phone = phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public void setReceived(String received) {
            this.received = received;
        }

        public void setSent(String sent) {
            this.sent = sent;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setFamily(String family) {
            this.family = family;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public void setAge(String age) {
            this.age = age;
        }

        public void setLink(String link) {
            this.link = link;
        }

    }

    public void check_network() {
        new Handler().postDelayed(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run()
            {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo wifi_conn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo data_conn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                is_connected = (wifi_conn != null && wifi_conn.isConnected()) || (data_conn != null && data_conn.isConnected());
                check_network();
            }
        }, 2000);
    }
}