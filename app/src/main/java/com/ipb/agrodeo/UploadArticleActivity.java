package com.ipb.agrodeo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

public class UploadArticleActivity extends AppCompatActivity {
    Uri mimguri;
    final int IMAGE_REQUEST_CODE = 7;

    // Firebase Database
    public static final String articlePath = "uploads/article";
    public static final StorageReference myArticleStoreRef = FirebaseStorage.getInstance().getReference(articlePath);
    public static final DatabaseReference myArticleDataRef = FirebaseDatabase.getInstance().getReference(articlePath);

    // UI and UX
    Button btnSelect, btnUpload;
    ImageView imageSelected;
    EditText titleArticle, contentArticle;

    // Progress and Alert Dialog
    ProgressDialog progressDialog;
    AlertDialog.Builder alertDialogSuccessBuilder;
    AlertDialog alertDialogSuccess;

    // private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_article);
        btnSelect = findViewById(R.id.btn_select_article);
        btnUpload = findViewById(R.id.btn_upload_article);
        titleArticle = findViewById(R.id.et_title_article);
        contentArticle = findViewById(R.id.et_content_article);
        imageSelected = findViewById(R.id.selected_img);

        // Toolbar setup
        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitle(R.string.register_article);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        alertDialogSuccessBuilder = new AlertDialog.Builder(UploadArticleActivity.this);
        alertDialogSuccessBuilder.setTitle("Berhasil!")
                .setMessage("Artikel berhasil terunggah")
                .setPositiveButton("OK", (dialog, which) -> onBackPressed());
        alertDialogSuccess  = alertDialogSuccessBuilder.create();

        btnUpload.setOnClickListener(v -> {
            if(TextUtils.isEmpty(titleArticle.getText())) {
                titleArticle.setError("Judul artikel tidak boleh kosong!");
            } else if (TextUtils.isEmpty(contentArticle.getText())) {
                contentArticle.setError("Isi artikel tidak boleh kosong!");
            } else {
                uploadImage();
                progressDialog = new ProgressDialog(new ContextThemeWrapper(this, R.style.Theme_Agrodeo_Poppins));
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setTitle("Uploading image");
                progressDialog.setMessage("Please wait");
                progressDialog.show();
            }
        });

        btnSelect.setOnClickListener(v ->
                ActivityCompat.requestPermissions(UploadArticleActivity.this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        IMAGE_REQUEST_CODE)
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == IMAGE_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(new Intent(Intent.ACTION_PICK));
                intent.setType("image/*");
                // someActivityResultLauncher.launch(intent);
                startActivityForResult(Intent.createChooser(intent,"Select image"), IMAGE_REQUEST_CODE);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            mimguri = data.getData();
            Picasso.with(this).load(mimguri).into(imageSelected);
        }
    }

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    /*
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        // Picasso.with(UploadArticleActivity.this).load(mimguri).into(imageSelected);

                    }
                }
            });
     */

    private String getFileExtension (Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap map = MimeTypeMap.getSingleton();

        return map.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        if (mimguri != null){
            StorageReference storageReference = myArticleStoreRef.child(System.currentTimeMillis() + "." + getFileExtension(mimguri));
            final UploadTask uploadTask = storageReference.putFile(mimguri);

            // Setup handler
            Handler handler = new Handler();
            handler.postDelayed(() -> progressDialog.setProgress(0),5000);
            Log.e("storageRef", storageReference.toString());

            // Set up actions after uploading
            uploadTask.addOnSuccessListener(taskSnapshot -> uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return storageReference.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String thumb_download_url = task.getResult().toString();
                    Log.e("Thumbnail Download URL", thumb_download_url);
                    String uploadId = myArticleDataRef.push().getKey();
                    MainArticleDataList list_data = new MainArticleDataList(
                            uploadId, titleArticle.getText().toString(),
                            contentArticle.getText().toString(), thumb_download_url);

                    myArticleDataRef.child(uploadId).setValue(list_data);

                    progressDialog.dismiss();
                    alertDialogSuccess.show();
                }
            })).addOnFailureListener(e -> progressDialog.dismiss()).addOnProgressListener(snapshot -> {
                double pr = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                progressDialog.setProgress((int) pr);
            });
        } else {
            Toast.makeText(UploadArticleActivity.this, "Gambar belum dipilih", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }

    }
}