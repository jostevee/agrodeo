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
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import static com.ipb.agrodeo.UploadArticleActivity.myArticleDataRef;
import static com.ipb.agrodeo.UploadArticleActivity.myArticleStoreRef;
import static com.ipb.agrodeo.UploadMarketActivity.IMAGE_REQUEST_CODE;

public class EditArticleActivity extends AppCompatActivity {
    String articleKey, titleArticle, contentArticle, imageUrl;
    Uri mimguri;

    // UI and UX
    Button btnSelect, btnUpload;
    TextInputEditText title_article, content_article;
    AppCompatImageView article_image;

    // Progress and Alert Dialog
    ProgressDialog progressDialog;
    AlertDialog.Builder alertDialogSuccessBuilder;
    AlertDialog alertDialogSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_article);
        btnSelect = findViewById(R.id.btn_select);
        btnUpload = findViewById(R.id.btn_upload);
        title_article = findViewById(R.id.edit_article_title);
        content_article = findViewById(R.id.edit_article_content);
        article_image = findViewById(R.id.edit_article_image);

        // Toolbar setup
        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitle(R.string.product_detail);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Mengunggah Produk");
        progressDialog.setMessage("Harap tunggu, kami sedang mengunggah produk anda ke database kami");

        alertDialogSuccessBuilder = new AlertDialog.Builder(this);
        alertDialogSuccessBuilder.setTitle("Berhasil!")
                .setMessage("Artikel berhasil terunggah, silakan kembali ke halaman awal")
                .setPositiveButton("Kembali", (dialog, which) -> onBackPressed())
                .setCancelable(false);
        alertDialogSuccess  = alertDialogSuccessBuilder.create();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            articleKey = extras.getString("article_key");
            titleArticle = extras.getString("article_title");
            contentArticle = extras.getString("article_content");
            imageUrl = extras.getString("image_url");

            Log.d("EditArticleDetail", titleArticle);
            Log.d("EditArticleDetail", contentArticle);
            Log.d("EditArticleDetail", imageUrl);

            title_article.setText(titleArticle);
            content_article.setText(contentArticle);
            Picasso.with(getApplicationContext())
                    .load(imageUrl)
                    .into(article_image);
        }

        btnSelect.setOnClickListener(v ->
                ActivityCompat.requestPermissions(EditArticleActivity.this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        IMAGE_REQUEST_CODE)
        );

        btnUpload.setOnClickListener(v -> {
            if(TextUtils.isEmpty(title_article.getText())) {
                title_article.setError("Nama Produk tidak boleh kosong!");
            } else if (TextUtils.isEmpty(content_article.getText())) {
                content_article.setError("Harga Produk tidak boleh kosong!");
            } else {
                uploadImage(articleKey);
                progressDialog.show();
            }
        });
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
            Picasso.with(this).load(mimguri).into(article_image);
        }
    }

    private String getFileExtension (Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap map = MimeTypeMap.getSingleton();

        return map.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(String articleKey) {
        if (mimguri != null) {
            StorageReference storageReference = myArticleStoreRef.child(System.currentTimeMillis() + "." + getFileExtension(mimguri));
            final UploadTask uploadTask = storageReference.putFile(mimguri);

            // Setup handler
            Handler handler = new Handler();
            handler.postDelayed(() -> progressDialog.setProgress(0), 5000);
            Log.e("storageRef", storageReference.toString());

            // Set up actions after uploading
            uploadTask.addOnSuccessListener(taskSnapshot -> uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }

                // Continue with the task to get the download URL
                return storageReference.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String uploadedUrl = task.getResult().toString();
                    processingDataToFirebase(uploadedUrl, articleKey);
                }
            })).addOnFailureListener(e -> {
            }).addOnProgressListener(snapshot -> {
                double pr = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                progressDialog.setProgress((int) pr);
            });
        } else {
            processingDataToFirebase(imageUrl, articleKey);
        }
    }

    private void processingDataToFirebase(String fixImageUrl, String articleKey) {
        // Chcek if image URL were the same
        /*
        if (Objects.equals(imageUrl, thumb_download_url)){
            fixImageUrl = imageUrl;
        } else {
            fixImageUrl = thumb_download_url;
        }
         */

        // String uploadId = mdataref.push().getKey();
        MainArticleDataList list_data = new MainArticleDataList(
                articleKey, String.valueOf(title_article.getText()),
                String.valueOf(content_article.getText()), fixImageUrl);
        Log.e("Ini productKey", articleKey);

        if (articleKey != null) {
            myArticleDataRef.child(articleKey).setValue(list_data).addOnSuccessListener(aVoid -> {
                progressDialog.dismiss();
                alertDialogSuccess.show();
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Respond to the action bar's Up/Home button
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            // NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}