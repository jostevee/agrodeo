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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

public class UploadMarketActivity extends AppCompatActivity {
    // private static final int PICK_IMAGE_REQUEST = 1;
    static final int IMAGE_REQUEST_CODE = 7;
    private Uri mimguri;

    // Firebase Database
    public static final String marketPath = "uploads/market";
    public static final StorageReference myMarketStoreRef = FirebaseStorage.getInstance().getReference(marketPath);
    public static final DatabaseReference myMarketDataRef = FirebaseDatabase.getInstance().getReference(marketPath);

    // UI and UX
    Button btnSelect, btnUpload;
    ImageView imageSelected;
    EditText productName, productPrice, productWeight, productLocation, contactNumber;

    // Progress and Alert Dialog
    ProgressDialog progressDialog;
    AlertDialog.Builder alertDialogSuccessBuilder;
    AlertDialog alertDialogSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_market);
        btnSelect = findViewById(R.id.btn_select);
        btnUpload = findViewById(R.id.btn_upload);
        imageSelected = findViewById(R.id.selected_img);
        productName = findViewById(R.id.product_name);
        productPrice = findViewById(R.id.product_price);
        productWeight = findViewById(R.id.product_weight);
        productLocation = findViewById(R.id.product_location);
        contactNumber = findViewById(R.id.contact_number);

        // Toolbar setup
        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitle(R.string.register_product_market);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        /*
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Agrodeo");
            actionBar.setSubtitle(R.string.register_product_market);
        }
         */

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Mengunggah Produk");
        progressDialog.setMessage("Harap tunggu, kami sedang mengunggah produk anda ke database kami");

        alertDialogSuccessBuilder = new AlertDialog.Builder(UploadMarketActivity.this);
        alertDialogSuccessBuilder.setTitle("Berhasil!")
                .setMessage("Barang berhasil terunggah, silakan kembali ke halaman awal")
                .setPositiveButton("Kembali", (dialog, which) -> onBackPressed())
                .setCancelable(false);
        alertDialogSuccess  = alertDialogSuccessBuilder.create();

        btnSelect.setOnClickListener(v ->
                ActivityCompat.requestPermissions(UploadMarketActivity.this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        IMAGE_REQUEST_CODE)
        );

        btnUpload.setOnClickListener(v -> {
            if(TextUtils.isEmpty(productName.getText())) {
                productName.setError("Nama Produk tidak boleh kosong!");
            } else if (TextUtils.isEmpty(productPrice.getText())) {
                productPrice.setError("Harga Produk tidak boleh kosong!");
            } else if (TextUtils.isEmpty(productWeight.getText())) {
                productWeight.setError("Berat Produk tidak boleh kosong!");
            } else if (TextUtils.isEmpty(productLocation.getText())) {
                productLocation.setError("Lokasi Produk tidak boleh kosong!");
            } else if (TextUtils.isEmpty(contactNumber.getText())) {
                contactNumber.setError("Nomor Penjual tidak boleh kosong!");
            } else {
                uploadImage();
                progressDialog.show();
            }
        });

        /*
        btnSelect.setOnClickListener(v ->
                ActivityCompat.requestPermissions(UploadMarketActivity.this,
                new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                IMAGE_REQUEST_CODE)
        );
         */
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
                        // Intent data = result.getData();
                        Picasso.with(UploadMarketActivity.this).load(mimguri).into(imageSelected);

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
                    String thumb_download_url = task.getResult().toString();
                    Log.e("Thumbnail Download URL", thumb_download_url);
                    String uploadId = myMarketDataRef.push().getKey();
                    MarketDataList list_data = new MarketDataList(
                            uploadId, String.valueOf(productName.getText()), Integer.parseInt(String.valueOf(productPrice.getText())),
                            Integer.parseInt(String.valueOf(productWeight.getText())), String.valueOf(productLocation.getText()),
                            String.valueOf(contactNumber.getText()), thumb_download_url);
                    if (uploadId != null) {
                        myMarketDataRef.child(uploadId).setValue(list_data);
                    }

                    progressDialog.dismiss();
                    alertDialogSuccess.show();
                }
            })).addOnFailureListener(e -> {
            }).addOnProgressListener(snapshot -> {
                double pr = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                progressDialog.setProgress((int) pr);
            });

            /*
            storageReference.putFile(mimguri).addOnSuccessListener(taskSnapshot -> {
                Handler handler = new Handler();
                handler.postDelayed(() -> progressDialog.setProgress(0),5000);
                Log.e("storageRef", storageReference.toString());
            }).addOnFailureListener(e -> {
            }).addOnProgressListener(taskSnapshot -> {
                double pr = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setProgress((int) pr);
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    UploadTask.TaskSnapshot downloadUri = task.getResult();
                    System.out.println("Upload " + downloadUri);

                    if (downloadUri != null) {
                        String photoStringLink = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!
                        System.out.println("Upload " + photoStringLink);
                        Log.e("TaskSnapshot", photoStringLink);

                        MarketDataList list_data = new MarketDataList(imageName.getText().toString().trim(), storageReference.toString());
                        String uploadId = mdataref.push().getKey();
                        mdataref.child(uploadId).setValue(list_data);

                        Toast.makeText(UploadMarketActivity.this, "Upload Succsessful", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle failures
                    // ...
                }
            });
             */

        } else {
            Toast.makeText(UploadMarketActivity.this, "Gambar belum dipilih", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
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