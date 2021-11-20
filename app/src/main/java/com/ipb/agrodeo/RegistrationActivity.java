package com.ipb.agrodeo;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.Toolbar;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    private GoogleSignInClient mGoogleSignInClient;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference myRef = database.getReference("users");
    private ProgressDialog progressDialog;
    private TextInputEditText etName, etRole, etCommodity, etBirthdate, etLocation, etAcademic, etEmail, etPhone;
    private AppCompatAutoCompleteTextView tvSex, tvEdu;
    private DatePickerDialog picker;

    // Profile profile = new Profile();
    // List<Profile> profileArrayList = new ArrayList<>();
    // private ArrayList<String> Userlist, UserDetail;
    // private TextInputEditText etReligion;
    // private TextInputEditText etBirthdate;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Get all the element(s)
        etName = findViewById(R.id.name);
        etRole = findViewById(R.id.role);
        etCommodity = findViewById(R.id.commodity);
        etLocation = findViewById(R.id.location);
        etBirthdate = findViewById(R.id.birthdate);
        etEmail = findViewById(R.id.email);
        etPhone = findViewById(R.id.phone);

        // Toolbar setup
        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitle(R.string.register_new_account);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        /* [START configure_signin] */
        /* Configure sign-in to request the user's ID, email address, and basic */
        /* profile. ID and basic profile are included in DEFAULT_SIGN_IN. */
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestIdToken(getString(R.string.default_server_client_id))
                .requestEmail()
                .build();

        /* [START build_client] */
        /* Build a GoogleSignInClient with the options specified by gso. */
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Progress Dialog for processing data
        progressDialog = new ProgressDialog(new ContextThemeWrapper(this, R.style.Theme_Agrodeo_Poppins));
        progressDialog.setTitle("Mohon tunggu");
        progressDialog.setMessage("Data anda sedang kami proses ke database");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        // Create Sex ArrayAdapter using the string array and a default spinner layout
        // Specify the layout to use when the list of choices appears
        // Apply the adapter to the spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sex_array, android.R.layout.simple_spinner_dropdown_item);
        tvSex = findViewById(R.id.sex);
        tvSex.setAdapter(adapter);

        /*
        etSex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                // An item was selected. You can retrieve the selected item using
                sex = parent.getItemAtPosition(pos).toString();
                Log.d("ini sex yang terinput", sex);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
                Toast.makeText(RegistrationActivity.this, "Jenis Kelamin belum anda pilih", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
         */

        // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Create Education ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> eduAdapter = ArrayAdapter.createFromResource(this, R.array.education_array, android.R.layout.simple_spinner_dropdown_item);
        tvEdu = findViewById(R.id.edu);
        tvEdu.setAdapter(eduAdapter);

        /*
        etEdu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                // An item was selected. You can retrieve the selected item using
                education = parent.getItemAtPosition(pos).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
                Toast.makeText(RegistrationActivity.this, "Pendidikan belum anda pilih", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
         */

        // eduAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // OLD Spinner with Material Design's
        /*
        // String[] type = new String[] {"Laki-laki", "Perempuan"};
        // ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.sex_dropdown_menu_item, type);

        // AppCompatAutoCompleteTextView editTextFilledExposedDropdown = findViewById(R.id.sex);
        // editTextFilledExposedDropdown.setAdapter(adapter);
        // etReligion = findViewById(R.id.religion);
        // etBirthdate = findViewById(R.id.birthdate);
        // spinner = findViewById(R.id.sex_spinner);
         */

        // EditText for Birthdate section
        etBirthdate.setInputType(InputType.TYPE_NULL);
        etBirthdate.setOnClickListener(v -> {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);

            // DatePicker Dialog
            picker = new DatePickerDialog(RegistrationActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) -> etBirthdate.setText(getString(R.string.inputted_birthdate,dayOfMonth, (monthOfYear + 1), year1)), year, month, day);
            picker.show();
        });

        /*
        etBirthdate.setOnTouchListener((v, event) -> {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);

            // DatePicker Dialog
            picker = new DatePickerDialog(RegistrationActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) -> etBirthdate.setText(getString(R.string.inputted_birthdate,dayOfMonth, (monthOfYear + 1), year1)), year, month, day);
            picker.show();

            int inType = etBirthdate.getInputType(); // backup the input type
            etBirthdate.setInputType(InputType.TYPE_NULL); // disable soft input
            etBirthdate.onTouchEvent(event); // call native handler
            etBirthdate.setInputType(inType); // restore input type
            return true;
        });
         */

        // Get Intent
        Intent inten = getIntent();
        String email_get = inten.getStringExtra("email");

        // Set the email TextField
        etEmail.setText(email_get);

        // Set OnClickListener
        findViewById(R.id.btnSubmit).setOnClickListener(this);

        // myRef.child("Andi").setValue("Hello, World!");

        // Read from the database
        /*
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        */

        // un.setText(Userlist.get(2));
    }

    @Override
    public void onClick(View v) {
        // Getting text from our edittext fields.
        String name = Objects.requireNonNull(etName.getText()).toString();
        String role = Objects.requireNonNull(etRole.getText()).toString();
        String commodity = Objects.requireNonNull(etCommodity.getText()).toString();
        String location = Objects.requireNonNull(etLocation.getText()).toString();
        String birthdate = Objects.requireNonNull(etBirthdate.getText()).toString();
        String email = Objects.requireNonNull(etEmail.getText()).toString();
        String phone = Objects.requireNonNull(etPhone.getText()).toString();
        String admin = "0";
        String seller = "0";

        // String religion = Objects.requireNonNull(etReligion.getText()).toString();
        // String sex = Objects.requireNonNull(etSex.getText()).toString();
        // String education = etAcademic.getText().toString();

        // below line is for checking weather the
        // edittext fields are empty or not.
        if (TextUtils.isEmpty(name) | TextUtils.isEmpty(role) | TextUtils.isEmpty(location) | TextUtils.isEmpty(birthdate) |
                // TextUtils.isEmpty(sex) && TextUtils.isEmpty(education) && TextUtils.isEmpty(religion)
                TextUtils.isEmpty(email) | TextUtils.isEmpty(phone)) {
            // if the text fields are empty
            // then show the below message.
            Toast.makeText(this, "Data belum lengkap, mohon untuk memasukkan seluruh data yang dibutuhkan", Toast.LENGTH_SHORT).show();
        } else if (tvSex.getText().toString() == null | tvSex.getText().toString().equals("Pilih jenis kelamin")) {
            Toast.makeText(RegistrationActivity.this, "Jenis Kelamin belum anda pilih", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        } else if (tvEdu.getText().toString() == null | tvEdu.getText().toString().equals("Pilih tingkat pendidikan")) {
            Toast.makeText(RegistrationActivity.this, "Pendidikan belum anda pilih", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        } else {
            String sex = tvSex.getText().toString();
            String education = tvEdu.getText().toString();
            // else call the method to add
            // data to our database.
            Profile profile_details = new Profile(name, role, commodity, location, birthdate, sex,
                    education, email, phone, admin, seller); // religion
            addDataToFirebase(profile_details);

            progressDialog.show();
        }
    }

    private void addDataToFirebase(Profile profile) {
        // Below code is used to set data in our object class.
        String name = profile.getName();
        String role = profile.getRole();
        String commodity = profile.getCommodity();
        String location = profile.getLocation();
        String birthdate = profile.getBirthdate();
        String sex = profile.getSex();
        String education = profile.getEducation();
        String email = profile.getEmail();
        String phone = profile.getPhone();
        String admin = profile.getAdmin();
        String seller = profile.getSeller();

        // String religion = profile.getReligion();

        // Logging results
        /*
        Log.e("NAMES", name);
        Log.e("NAMES", role);
        Log.e("NAMES", commodity);
        Log.e("NAMES", location);
        Log.e("NAMES", birthdate);
        Log.e("NAMES", sex);
        Log.e("NAMES", education);
        Log.e("NAMES", email);
        Log.e("NAMES", phone);
        Log.e("NAMES", admin);
        Log.e("NAMES", seller);
         */

        // Log.e("NAMES", religion);

        /*
        profile.setName(name);
        profile.setRole(role);
        profile.setCommodity(commodity);
        profile.setLocation(location);
        profile.setBirthdate(birthdate);
        profile.setSex(sex);
        profile.setAcademic(education);
        profile.setReligion(religion);
        profile.setEmail(email);
        profile.setPhone(phone);
        profile.setAdmin(admin);
        profile.setSeller(seller);
         */

        // Safe the data
        String key = myRef.push().getKey();

        if (key != null) {
            // Set new child refs
            DatabaseReference insertAccountRef = myRef.child(key);
            Log.e("myRefKey", key);

            // Create HashMap for inserting object
            Map<String, Object> map = new HashMap<>();
            map.put("name", name);
            map.put("role", role);
            map.put("commodity", commodity);
            map.put("location", location);
            map.put("birthdate", birthdate);
            map.put("sex", sex);
            map.put("education", education);
            map.put("email", email);
            map.put("phone", phone);
            map.put("admin", admin);
            map.put("seller", seller);

            // map.put("religion", religion);

            // Insert all children
            insertAccountRef.updateChildren(map);
        }

        // we are use add value event listener method
        // which is called with database reference.
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Inside the method of on Data change we are setting
                // Our object class to our database reference.
                // Database reference will sends data to firebase.
                // myRef.child(key).setValue(profile);

                // Setup SharedPreferences
                SharedPreferences sharedPref = RegistrationActivity.this.getSharedPreferences("accountData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.key_education), education);
                editor.putString(getString(R.string.key_birthdate), birthdate);
                editor.putString(getString(R.string.key_commodity), commodity);
                editor.putString(getString(R.string.key_location), location);
                editor.putString(getString(R.string.key_name), name);
                editor.putString(getString(R.string.key_phone), phone);
                editor.putString(getString(R.string.key_role), role);
                editor.putString(getString(R.string.key_sex), sex);
                editor.putString(getString(R.string.key_admin), admin);
                editor.putString(getString(R.string.key_seller), seller);

                // editor.putString(getString(R.string.key_religion), religion);
                // editor.putInt(getString(R.string.saved_high_score_key), newHighScore);

                editor.apply();

                // After adding this data we are showing toast message.
                Toast.makeText(RegistrationActivity.this, "Successfully registered", Toast.LENGTH_SHORT).show();
                Intent inten = new Intent(RegistrationActivity.this, HomeNavigation.class);
                startActivity(inten);

                progressDialog.dismiss();
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // if the data is not added or it is cancelled then
                // we are displaying a failure toast message.
                Toast.makeText(RegistrationActivity.this, "Gagal menambahkan data, mohon coba kembali" + error, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        signOut();
    }

    public void signOut(){
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {
                    // [START_EXCLUDE]
                    Intent inten = new Intent(RegistrationActivity.this, LoginActivity.class);
                    startActivity(inten);
                    finish();
                    // [END_EXCLUDE]
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Respond to the action bar's Up/Home button
        if (item.getItemId() == android.R.id.home) {
            signOut();
            // NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}