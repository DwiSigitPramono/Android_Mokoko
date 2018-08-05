package com.mokoko.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mokoko.Manifest;
import com.mokoko.R;
import com.mokoko.model.UserUpload;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignupUserActivity extends AppCompatActivity {

    @BindView(R.id.input_namaUser) EditText _namaUser;
    @BindView(R.id.input_telpUser) EditText _telpUser;
    @BindView(R.id.input_email) EditText _emailUser;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.input_reEnterPassword) EditText _reEnterPasswordText;
    @BindView(R.id.btn_signup) Button _signupButton;
    @BindView(R.id.btPeriksaEmail) Button _btPeriksaEmail;
    @BindView(R.id.tvhasilEmail) TextView _tvHasilEmail;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private String Storage_Path = "user/";
    private String imgURL;
    private ImageButton btRemove;
    private Uri FilePathUri;
    private ImageView SelectImage;
    private StorageReference storageReference;
    private boolean foto = false;

    private int Image_Request_Code = 7;
    private byte[] compImg;
    private ByteArrayOutputStream baos;

    private List<String> emailList = new ArrayList<>();

    private static final String Database_Path = "bakos_db/user";
    private DatabaseReference databaseReference;

    private String email;
    private String password;

    private ScrollView svDaftar;

    private boolean emailReady = false;
    private boolean emailValid = true;
    private boolean emailKlik = false;
    private ProgressDialog progressDialog;

    private static final int CAMERA_REQUEST_CODE = 100;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_user);
        ButterKnife.bind(this);
        svDaftar = (ScrollView)findViewById(R.id.svDaftar);
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        storageReference = FirebaseStorage.getInstance().getReference();
        SelectImage = (ImageView)findViewById(R.id.gambarPromo);
        progressDialog = new ProgressDialog(SignupUserActivity.this, R.style.AppTheme_Dark_Dialog);
        cekEmail();
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _btPeriksaEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailKlik = true;
                emailValid = true;
                for(String emailStr: emailList) {
                    if(emailStr.trim().contains(_emailUser.getText())){
                        emailValid = false;
                    }

                }
                if(emailReady) {
                    if (emailValid) {
                        _tvHasilEmail.setText("Alamat email masih tersedia.");
                    } else {
                        _tvHasilEmail.setText("Alamat email sudah digunakan.");
                    }
                }

            }
        });

        _emailUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailKlik = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }
    }

    private void signup() {
        if (!validate()) {
            onSignupFailed();
            return;
        }
        _signupButton.setEnabled(false);
        progressDialog = new ProgressDialog(SignupUserActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Proses ...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        email = _emailUser.getText().toString();
        password = _passwordText.getText().toString();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            uploadFoto();
                        }
                    }
                });
    }

    private void createFirebaseUserProfile(final FirebaseUser user) {
        UserProfileChangeRequest addProfileName = new UserProfileChangeRequest.Builder()
                .setDisplayName(WordUtils.capitalizeFully(_namaUser.getText().toString()))
                .setPhotoUri(Uri.parse(imgURL))
                .build();
        user.updateProfile(addProfileName);
    }

    private void uploadFoto() {
        if (foto) {
            Bitmap bitmapOne = ((BitmapDrawable) SelectImage.getDrawable()).getBitmap();
            ByteArrayOutputStream imageOneBytes = new ByteArrayOutputStream();
            bitmapOne.compress(Bitmap.CompressFormat.JPEG, 100, imageOneBytes);
            byte[] dataOne = imageOneBytes.toByteArray();

            StorageReference filepathOne = storageReference.child(Storage_Path + System.currentTimeMillis() + ".jpg");
            filepathOne.putBytes(dataOne).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests")
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    imgURL = downloadUrl.toString();
                    createFirebaseUserProfile(firebaseAuth.getCurrentUser());
                    daftarUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    kirimVerifikasiEmail();
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignupUserActivity.this, R.style.AppCompatAlertDialogStyle);
                    try {
                        alertDialogBuilder
                                .setTitle("Verifikasi Email")
                                .setMessage("Kami telah mengirimkan email ke " + FirebaseAuth.getInstance().getCurrentUser().getEmail() + " untuk  memverifikasi alamat email kamu. Silahkan klik link di email tersebut untuk dapat masuk ke akun.")
                                .setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        onSignupSuccess();
                                        progressDialog.dismiss();
                                    }
                                });
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    final AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                    TextView pesan = (TextView) alertDialog.findViewById(android.R.id.message);
                    pesan.setTextSize(15);

                    Button b = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    b.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }
    }

    private void kirimVerifikasiEmail(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Snackbar.make(svDaftar,"Email verifikasi berhasil dikirim.", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void onSignupSuccess() {
        FirebaseAuth.getInstance().signOut();
        Snackbar.make(svDaftar,"Daftar berhasil.", Snackbar.LENGTH_SHORT).show();
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    private void onSignupFailed() {
        _signupButton.setEnabled(true);
    }

    private Pattern letter = Pattern.compile("[a-zA-z]");
    private Pattern digit = Pattern.compile("[0-9]");
    private boolean ok(String password) {
        Matcher hasLetter = letter.matcher(password);
        Matcher hasDigit = digit.matcher(password);
        return hasLetter.find() && hasDigit.find();
    }


    private void daftarUser(String id){
        String namaUser = WordUtils.capitalizeFully(_namaUser.getText().toString());
        String inc = String.valueOf(emailList.size() + 1);
        String idUser = "USR"+ StringUtils.repeat('0',8-inc.length())+inc;
        String email = _emailUser.getText().toString();
        String telpUser = _telpUser.getText().toString();
        UserUpload su = new UserUpload(idUser, namaUser, email, telpUser, imgURL, id, "0", "0");
        databaseReference.child(id+"/info").setValue(su);
    }

    private boolean validate() {
        boolean valid = true;
        String email = _emailUser.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();
        String namaUser = WordUtils.capitalizeFully(_namaUser.getText().toString());
        String telpUser = _telpUser.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailUser.setError("Alamat email tidak valid");
            Snackbar.make(svDaftar,"Alamat email tidak valid.", Snackbar.LENGTH_SHORT).show();
            valid = false;
        } else {
            _emailUser.setError(null);
        }

        if ((ok(password))&&(password.length()>8)){
            _passwordText.setError(null);
        } else {
            _passwordText.setError("Kata sandi harus lebih dari 8 karakter dan alfanumerik");
            Snackbar.make(svDaftar,"Kata sandi harus lebih dari 8 karakter dan alfanumerik.", Snackbar.LENGTH_SHORT).show();
            valid = false;
        }
        if (reEnterPassword.isEmpty() || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Kata sandi tidak cocok");
            Snackbar.make(svDaftar,"Kata sandi tidak cocok.", Snackbar.LENGTH_SHORT).show();
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        if (namaUser.isEmpty()) {
            _namaUser.setError("Nama belum diisi");
            Snackbar.make(svDaftar,"Nama belum diisi.", Snackbar.LENGTH_SHORT).show();
            valid = false;
        } else {
            _namaUser.setError(null);
        }

        if (telpUser.isEmpty()) {
            _telpUser.setError("Nomor Ponsel belum diisi");
            Snackbar.make(svDaftar,"Nomor Ponsel belum diisi.", Snackbar.LENGTH_SHORT).show();
            valid = false;
        } else {
            _telpUser.setError(null);
        }
        if(!foto){
            valid = false;
            Snackbar.make(svDaftar,"Foto Profil belum dipilih.", Snackbar.LENGTH_SHORT).show();
        }
        if(!emailValid || !emailReady){
            valid = false;
            Snackbar.make(svDaftar,"Email sudah terdaftar, gunakan email yang lain.", Snackbar.LENGTH_SHORT).show();
        }
        if(!emailKlik){
            valid = false;
            Snackbar.make(svDaftar,"Klik tombol periksa email untuk memeriksa email valid.", Snackbar.LENGTH_SHORT).show();
        }
        return valid;
    }

    private boolean validatePart1() {
        boolean valid = true;
        String namaUser = WordUtils.capitalizeFully(_namaUser.getText().toString());
        String telpUser = _telpUser.getText().toString();


        if (namaUser.isEmpty()) {
            _namaUser.setError("Nama belum diisi");
            Snackbar.make(svDaftar,"Nama belum diisi.", Snackbar.LENGTH_SHORT).show();
            valid = false;
        } else {
            _namaUser.setError(null);
        }

        if (telpUser.isEmpty()) {
            _telpUser.setError("Nomor Ponsel belum diisi");
            Snackbar.make(svDaftar,"Nomor Ponsel belum diisi.", Snackbar.LENGTH_SHORT).show();
            valid = false;
        } else {
            _telpUser.setError(null);
        }
        if(!foto){
            valid = false;
            Snackbar.make(svDaftar,"Foto Profil belum dipilih.", Snackbar.LENGTH_SHORT).show();
        }

        return valid;
    }


    public void berikutnyaDaftar(View view) {
        if(validatePart1()) {
            final LinearLayout layBerikutnya = (LinearLayout) findViewById(R.id.layBerikutnya);
            final LinearLayout layDaftar = (LinearLayout) findViewById(R.id.layDaftar);
            layBerikutnya.setVisibility(View.GONE);
            layDaftar.setVisibility(View.VISIBLE);
        }
    }

    public void sebelumnyaDaftar(View view) {
        final LinearLayout layBerikutnya = (LinearLayout)findViewById(R.id.layBerikutnya);
        final LinearLayout layDaftar = (LinearLayout)findViewById(R.id.layDaftar);
        layBerikutnya.setVisibility(View.VISIBLE);
        layDaftar.setVisibility(View.GONE);
    }

    private void cekEmail(){
        Query searchQuery = databaseReference.orderByChild("info/");
        searchQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                UserUpload infoUser = new UserUpload();
                while((iterator.hasNext())){
                    infoUser = iterator.next().getValue(UserUpload.class);
                }
                emailList.add(infoUser.getEmailUser());
                emailReady = true;
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void uploadGambar(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("Upload Foto")
                .setCancelable(true)
                .setPositiveButton("Kamera",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        ambilGambar();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void uploadGambar2(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("Upload Foto")
                .setCancelable(true)
                .setPositiveButton("Kamera",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        ambilGambar2();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void ambilGambar() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, 1);
    }

    private void ambilGambar2() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, 2);
    }

    private void removeImg1() {
        SelectImage.setImageResource(R.drawable.ic_add_img);
        btRemove = (ImageButton)findViewById(R.id.btRemove1);
        btRemove.setVisibility(View.GONE);
        foto = false;
    }

    public void removeImg1v(View view) {
        removeImg1();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null && (requestCode==Image_Request_Code)) {

            FilePathUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);
                baos = new ByteArrayOutputStream();
                bitmap = getResizedBitmap(bitmap, 640);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                compImg = baos.toByteArray();
                Bitmap bitmap2 = BitmapFactory.decodeByteArray(compImg,0,compImg.length);

                if (requestCode == Image_Request_Code) {
                    SelectImage.setImageBitmap(bitmap2);
                    btRemove = (ImageButton)findViewById(R.id.btRemove1);
                    btRemove.setVisibility(View.VISIBLE);
                    foto = true;
                }

                Snackbar.make(svDaftar,"Foto berhasil dipilih.", Snackbar.LENGTH_SHORT).show();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        if ((requestCode == 1)) {
            try {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                baos = new ByteArrayOutputStream();
                bitmap = getResizedBitmap(bitmap, 640);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                compImg = baos.toByteArray();
                Bitmap bitmap2 = BitmapFactory.decodeByteArray(compImg, 0, compImg.length);
                if (requestCode == 1) {
                    SelectImage.setImageBitmap(bitmap2);
                    btRemove = (ImageButton) findViewById(R.id.btRemove1);
                    btRemove.setVisibility(View.VISIBLE);
                    foto = true;
                }
                Snackbar.make(svDaftar, "Foto berhasil dipilih.", Snackbar.LENGTH_SHORT).show();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}