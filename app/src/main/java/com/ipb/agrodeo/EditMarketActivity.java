package com.ipb.agrodeo;

import static com.ipb.agrodeo.UploadMarketActivity.IMAGE_REQUEST_CODE;
import static com.ipb.agrodeo.UploadMarketActivity.myMarketDataRef;
import static com.ipb.agrodeo.UploadMarketActivity.myMarketStoreRef;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class EditMarketActivity extends AppCompatActivity {
    String productKey, productName, productLocation, contactNumber, imageUrl;
    int productPrice, productWeight;
    Uri mimguri;

    // UI and UX
    Button btnSelect, btnUpload;
    TextInputEditText product_name, product_weight, product_price, product_location, contact_number;
    AppCompatImageView product_image;

    // Progress and Alert Dialog
    ProgressDialog progressDialog;
    AlertDialog.Builder alertDialogSuccessBuilder;
    AlertDialog alertDialogSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_market);
        btnSelect = findViewById(R.id.btn_select);
        btnUpload = findViewById(R.id.btn_upload);
        product_name = findViewById(R.id.edit_product_name);
        product_weight = findViewById(R.id.edit_product_weight);
        product_price = findViewById(R.id.edit_product_price);
        product_location = findViewById(R.id.edit_product_location);
        contact_number = findViewById(R.id.edit_contact_number);
        product_image = findViewById(R.id.edit_product_image);

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
                .setMessage("Barang berhasil terunggah, silakan kembali ke halaman awal")
                .setPositiveButton("Kembali", (dialog, which) -> onBackPressed())
                .setCancelable(false);
        alertDialogSuccess  = alertDialogSuccessBuilder.create();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            productKey = extras.getString("product_key");
            productName = extras.getString("product_name");
            productPrice = extras.getInt("product_price");
            productWeight = extras.getInt("product_weight");
            productLocation = extras.getString("product_location");
            contactNumber = extras.getString("contact_number");
            imageUrl = extras.getString("image_url");

            Log.d("EditMarketDetail", productName);
            Log.d("EditMarketDetail", String.valueOf(productPrice));
            Log.d("EditMarketDetail", String.valueOf(productWeight));
            Log.d("EditMarketDetail", productLocation);
            Log.d("EditMarketDetail", contactNumber);
            Log.d("EditMarketDetail", imageUrl);

            product_name.setText(productName);
            product_price.setText(String.valueOf(productPrice));
            product_weight.setText(String.valueOf(productWeight));
            product_location.setText(productLocation);
            contact_number.setText(contactNumber);
            Picasso.with(getApplicationContext())
                    .load(imageUrl)
                    .into(product_image);
        }

        btnSelect.setOnClickListener(v ->
                ActivityCompat.requestPermissions(EditMarketActivity.this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        IMAGE_REQUEST_CODE)
        );

        btnUpload.setOnClickListener(v -> {
            if(TextUtils.isEmpty(product_name.getText())) {
                product_name.setError("Nama Produk tidak boleh kosong!");
            } else if (TextUtils.isEmpty(product_price.getText())) {
                product_price.setError("Harga Produk tidak boleh kosong!");
            } else if (TextUtils.isEmpty(product_weight.getText())) {
                product_weight.setError("Berat Produk tidak boleh kosong!");
            } else if (TextUtils.isEmpty(product_location.getText())) {
                product_location.setError("Lokasi Produk tidak boleh kosong!");
            } else if (TextUtils.isEmpty(contact_number.getText())) {
                contact_number.setError("Nomor Penjual tidak boleh kosong!");
            } else {
                uploadImage(productKey);
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
            Picasso.with(this).load(mimguri).into(product_image);
        }
    }

    private String getFileExtension (Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap map = MimeTypeMap.getSingleton();

        return map.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(String productKey) {
        if (mimguri != null ){
            StorageReference storageReference = myMarketStoreRef.child(System.currentTimeMillis() + "." + getFileExtension(mimguri));
            final UploadTask uploadTask = storageReference.putFile(mimguri);

            // Setup handler
            Handler handler = new Handler();
            handler.postDelayed(() -> progressDialog.setProgress(0),5000);
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
                    processingDataToFirebase(uploadedUrl, productKey);
                }
            })).addOnFailureListener(e -> {
            }).addOnProgressListener(snapshot -> {
                double pr = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                progressDialog.setProgress((int) pr);
            });
        } else {
            processingDataToFirebase(imageUrl, productKey);
        }
    }

    private void processingDataToFirebase(String fixImageUrl, String productKey) {
        // Chcek if image URL were the same
        /*
        if (Objects.equals(imageUrl, thumb_download_url)){
            fixImageUrl = imageUrl;
        } else {
            fixImageUrl = thumb_download_url;
        }
         */

        // String uploadId = mdataref.push().getKey();
        MarketDataList list_data = new MarketDataList(
                productKey, String.valueOf(product_name.getText()), Integer.parseInt(String.valueOf(product_price.getText())),
                Integer.parseInt(String.valueOf(product_weight.getText())), String.valueOf(product_location.getText()),
                String.valueOf(contact_number.getText()), fixImageUrl);
        Log.e("Ini productKey", productKey);

        if (productKey != null) {
            myMarketDataRef.child(productKey).setValue(list_data).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressDialog.dismiss();
                    alertDialogSuccess.show();
                }
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