package com.ipb.agrodeo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;
    private SharedPreferences sharedPref;

    private ArrayList<String> Userlist;
    private DatabaseReference myRef;
    private Intent inten;
    private ProgressDialog progressDialog;

    // private ArrayList<String> UserDetail;
    // private TextView mStatusTextView;
    // private SignInClient oneTapClient;
    // private BeginSignInRequest signInRequest, signUpRequest;
    // private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    // private boolean showOneTapUI = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Declare firebase reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");

        /* Button listeners */
        findViewById(R.id.btnGoogle).setOnClickListener(this);
        // findViewById(R.id.sign_out_button).setOnClickListener(this);
        // findViewById(R.id.disconnect_button).setOnClickListener(this);

        // Progress Dialog
        progressDialog = new ProgressDialog(new ContextThemeWrapper(this, R.style.Theme_Agrodeo_Poppins));
        progressDialog.setTitle("Checking credentials");
        progressDialog.setMessage("Please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

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

        // Views
        //mStatusTextView = findViewById(R.id.status);

        /* [START customize_button] */
        /* If you're using Google's Default button */
        // signInButton.setSize(SignInButton.SIZE_STANDARD);
        // signInButton.setColorScheme(SignInButton.COLOR_LIGHT);

//        oneTapClient = Identity.getSignInClient(this);
//        signInRequest = BeginSignInRequest.builder()
//                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
//                        .setSupported(true)
//                        .build())
//                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
//                        .setSupported(true)
//                        // Your server's client ID, not your Android client ID.
//                        .setServerClientId(getString(R.string.default_web_client_id))
//                        // Only show accounts previously used to sign in.
//                        .setFilterByAuthorizedAccounts(true)
//                        .build())
//                // Automatically sign in when exactly one credential is retrieved.
//                .setAutoSelectEnabled(true)
//                .build();
//
//        signUpRequest = BeginSignInRequest.builder()
//                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
//                        .setSupported(true)
//                        // Your server's client ID, not your Android client ID.
//                        .setServerClientId(getString(R.string.default_web_client_id))
//                        // Show all accounts on the device.
//                        .setFilterByAuthorizedAccounts(false)
//                        .build())
//                .build();
//
//
//        oneTapClient.beginSignIn(signUpRequest)
//                .addOnSuccessListener(this, new OnSuccessListener<BeginSignInResult>() {
//                    @Override
//                    public void onSuccess(BeginSignInResult result) {
//                        try {
//                            startIntentSenderForResult(
//                                    result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
//                                    null, 0, 0, 0);
//                        } catch (IntentSender.SendIntentException e) {
//                            Log.e(TAG, "Couldn't start One Tap UI: " + e.getLocalizedMessage());
//                        }
//                    }
//                })
//                .addOnFailureListener(this, new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // No saved credentials found. Launch the One Tap sign-up flow, or
//                        // do nothing and continue presenting the signed-out UI.
//                        Log.d(TAG, e.getLocalizedMessage());
//                    }
//                });
    }

    @Override
    public void onStart() {
        super.onStart();

        // [START on_start_sign_in]
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
        // [END on_start_sign_in]
    }

    /* [START onActivityResult] */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    // [START handleSignInResult]
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    /* [START signIn] */
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /* [START revokeAccess] */
    /*
    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, task -> {
                    // [START_EXCLUDE]
                    updateUI(null);
                    // [END_EXCLUDE]
                });
    }
     */

    /* Change the intent HERE */
    private void updateUI(@Nullable GoogleSignInAccount account) {
        if (account != null) {
            String email = account.getEmail();
            Log.i("Email", account.getEmail());

            checkEmail(email);

            /*
            inten = new Intent(LoginActivity.this, HomeNavigation.class);
            inten.putExtra("email", email);

            // Start Intent and finish the activity
            startActivity(inten);
            finish();
            */

            // mStatusTextView.setText(account.getDisplayName());
            // findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            // findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {
            progressDialog.dismiss();
        }

        /* If 2 conditions were available */
        // mStatusTextView.setText(R.string.signed_out);
        // (R.id.sign_in_button).setVisibility(View.VISIBLE);
        // findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);

    }

    private void checkEmail(String email){
        /* Read from database */
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                Userlist = new ArrayList<>();
                // UserDetail = new ArrayList<>();
                int found = 0;

                // Result will be holded Here
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    String userkey  = dsp.getKey();
                    Userlist.add(userkey); // add result into array list

                    // Get email value from database
                    String email_database = Objects.requireNonNull(dsp.child("email").getValue()).toString();

                    // Check if signed email was registered on database
                    if (email_database.equals(email)){
                        found += 1;

                        // Start a new intent to HomeNavigation
                        inten = new Intent(LoginActivity.this, HomeNavigation.class);

                        // Get all data value from database
                        String education_database = Objects.requireNonNull(dsp.child("education").getValue()).toString();
                        String birthdate_database = Objects.requireNonNull(dsp.child("birthdate").getValue()).toString();
                        String commodity_database = Objects.requireNonNull(dsp.child("commodity").getValue()).toString();
                        String location_database = Objects.requireNonNull(dsp.child("location").getValue()).toString();
                        String name_database = Objects.requireNonNull(dsp.child("name").getValue()).toString();
                        String phone_database = Objects.requireNonNull(dsp.child("phone").getValue()).toString();
                        String role_database = Objects.requireNonNull(dsp.child("role").getValue()).toString();
                        String sex_database = Objects.requireNonNull(dsp.child("sex").getValue()).toString();
                        String admin_database = Objects.requireNonNull(dsp.child("admin").getValue()).toString();
                        String seller_database = Objects.requireNonNull(dsp.child("seller").getValue()).toString();
                        // String religion_database = Objects.requireNonNull(dsp.child("religion").getValue()).toString();

                        /*
                        Log.d("user", education_database);
                        Log.d("user", birthdate_database);
                        Log.d("user", commodity_database);
                        Log.d("user", location_database);
                        Log.d("user", name_database);
                        Log.d("user", phone_database);
                        Log.d("user", religion_database);
                        Log.d("user", role_database);
                        Log.d("user", sex_database);
                        */

                        sharedPref = LoginActivity.this.getSharedPreferences("accountData", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(getString(R.string.key_education), education_database);
                        editor.putString(getString(R.string.key_birthdate), birthdate_database);
                        editor.putString(getString(R.string.key_commodity), commodity_database);
                        editor.putString(getString(R.string.key_location), location_database);
                        editor.putString(getString(R.string.key_name), name_database);
                        editor.putString(getString(R.string.key_phone), phone_database);
                        editor.putString(getString(R.string.key_role), role_database);
                        editor.putString(getString(R.string.key_sex), sex_database);
                        editor.putString(getString(R.string.key_admin), admin_database);
                        editor.putString(getString(R.string.key_seller), seller_database);
                        // editor.putString(getString(R.string.key_religion), religion_database);
                        // editor.putInt(getString(R.string.saved_high_score_key), newHighScore);

                        editor.apply();

                        /*
                        SharedPreferences sharedPref = LoginActivity.this.getSharedPreferences("accountData", Context.MODE_PRIVATE);
                        String defaultValue = getResources().getString(R.string.saved_default_key);
                        String education = sharedPref.getString(getString(R.string.key_education), defaultValue);
                        String birthdate = sharedPref.getString(getString(R.string.key_birthdate), defaultValue);
                        String commodity = sharedPref.getString(getString(R.string.key_commodity), defaultValue);
                        String location = sharedPref.getString(getString(R.string.key_location), defaultValue);
                        String name = sharedPref.getString(getString(R.string.key_name), defaultValue);
                        String phone = sharedPref.getString(getString(R.string.key_phone), defaultValue);
                        String religion = sharedPref.getString(getString(R.string.key_religion), defaultValue);
                        String role = sharedPref.getString(getString(R.string.key_role), defaultValue);
                        String sex = sharedPref.getString(getString(R.string.key_sex), defaultValue);
                        String admin = sharedPref.getString(getString(R.string.key_admin), defaultValue);
                        String seller = sharedPref.getString(getString(R.string.key_seller), defaultValue);

                        Log.d("User Detail", education);
                        Log.d("User Detail", birthdate);
                        Log.d("User Detail", commodity);
                        Log.d("User Detail", location);
                        Log.d("User Detail", name);
                        Log.d("User Detail", phone);
                        Log.d("User Detail", religion);
                        Log.d("User Detail", role);
                        Log.d("User Detail", sex);
                        Log.d("User Detail", admin);
                        Log.d("User Detail", seller);
                         */
                    }

                    /*
                    for (DataSnapshot eachDSP : dsp.getChildren()) {
                        String info = Objects.requireNonNull(eachDSP.getValue()).toString();
                        String email_database   = Objects.requireNonNull(dsp.child("email").getValue()).toString();
                        Log.i("EMAIL DATABASE", email_database);
                        //UserDetail.add(info); //add result into array list
                    }
                     */
                }

                if (found == 1) {
                    // Log.d("masuk", "ya masuk");
                    inten = new Intent(LoginActivity.this, HomeNavigation.class);
                } else if(found == 0) {
                    // Log.d("gamasuk", "engga masuk");
                    inten = new Intent(LoginActivity.this, RegistrationActivity.class);
                    inten.putExtra("email", email);
                }

                // Start Intent and finish the activity - with log
                Log.e("found:", String.valueOf(found));
                startActivity(inten);
                progressDialog.dismiss();
                finish();

                // Log.e("test", Userlist.get(2));
                // Log.d("DetailUser", String.valueOf(UserDetail));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnGoogle) {
            signIn();

            /*
            case R.id.sign_out_button:
                signOut();
               break;
            case R.id.disconnect_button:
                revokeAccess();
                break;
             */
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.e("TES", String.valueOf(requestCode));
//
//        if (requestCode == REQ_ONE_TAP) {
//            try {
//                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
//                String idToken = credential.getGoogleIdToken();
//                String username = credential.getId();
//                String password = credential.getPassword();
//                if (idToken != null) {
//                    // Got an ID token from Google. Use it to authenticate
//                    // with your backend.
//                    Log.d(TAG, "Got ID token.");
//                } else if (password != null) {
//                    // Got a saved username and password. Use them to authenticate
//                    // with your backend.
//                    Log.d(TAG, "Got password.");
//                }
//            } catch (ApiException e) {
//                switch (e.getStatusCode()) {
//                    case CommonStatusCodes.CANCELED:
//                        Log.d(TAG, "One-tap dialog was closed.");
//                        // Don't re-prompt the user.
//                        showOneTapUI = false;
//                        break;
//                    case CommonStatusCodes.NETWORK_ERROR:
//                        Log.d(TAG, "One-tap encountered a network error.");
//                        // Try again or just ignore.
//                        break;
//                    default:
//                        Log.d(TAG, "Couldn't get credential from result."
//                                + e.getLocalizedMessage());
//                        break;
//                }
//            }
//        }
//    }
}