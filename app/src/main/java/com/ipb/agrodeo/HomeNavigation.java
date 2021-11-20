package com.ipb.agrodeo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

public class HomeNavigation extends AppCompatActivity {
    public static final String TAG = "HomeActivity";
    public Fragment fragment = null;
    public Class fragmentClass = null;

    public FloatingActionButton fab;
    public String defaultValue, name, seller, admin;
    public TextView profiles;
    public Toolbar toolbar;
    public int fragmentId;
    public long pressedTime;

    public static final int RC_SIGN_IN = 9001;
    public GoogleSignInClient mGoogleSignInClient;

    public SharedPreferences sharedPreferences, sharedPreferencesUser;
    public String username;

    // private ImageView iView;
    // private CoordinatorLayout coordinatorLayout;
    // private String mUsername;  //FOR UserName
    // private static final String ANONYMOUS = "anonymous";
    // private String myUID, UID_get;
    // Toolbar toolbar;
    // private TextView UserName;
    // private InterstitialAd mInterstitialAd; //Kalo ada Interstitial Ads!

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_navigation);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // toolbar.getOverflowIcon().setColorFilter(R.attr.colorOnBackground, PorterDuff.Mode.SRC_ATOP);
        // toolbar.setSubtitle("Cek Pulsa"); // Set toolbar subtitle //

        // Get the previous FRAGMENT_ID code
        fragmentId = getIntent().getIntExtra("FRAGMENT_ID_NAV", 100);

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

        // Get all the SharedPreferences variable
        sharedPreferencesUser = this.getSharedPreferences("accountData", Context.MODE_PRIVATE);
        defaultValue = getResources().getString(R.string.saved_default_key);
        name = sharedPreferencesUser.getString(getString(R.string.key_name), defaultValue);
        admin = sharedPreferencesUser.getString(getString(R.string.key_admin), defaultValue);
        seller = sharedPreferencesUser.getString(getString(R.string.key_seller), defaultValue);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.green_primary));
        // toggle.setHomeAsUpIndicator(R.drawable.fi_menu);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // ============================= Floating Action Button Function ============================= //
        fab = findViewById(R.id.fab);
        if (!admin.equals("0")){
            fab.setVisibility(View.VISIBLE);
        }
        fab.setOnClickListener(view1 -> {
            Intent inten = new Intent(this, UploadArticleActivity.class);
            startActivity(inten);
        });
        // fab.setImageResource(R.drawable.ic_google);
        // ============================= Floating Action Button Function ============================= //


        if (fragmentId == 100){
            fragmentClass = com.ipb.agrodeo.MainFragment.class;
            toolbar.setSubtitle(R.string.home);
        }

        NavigationView navigationView = findViewById(R.id.nav_view);
        setupDrawerContent(navigationView);
        // navigationView.setNavigationItemSelectedListener(this);

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        // Get the fragment object and replace the layout
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameutama, fragment).setMaxLifecycle(fragment, Lifecycle.State.RESUMED).commit();
    }

    // ======================================== Rounded Image function ======================================== //
    /*
    private Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }
     */
    // ======================================== Rounded Image function ======================================== //

    /* [START signOut] */
    public void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {
                    // [START_EXCLUDE]
                    updateUI();
                    // [END_EXCLUDE]
                });
    }

    /* Change the intent HERE */
    private void updateUI() {
        if (null != null) {
            String email = ((GoogleSignInAccount) null).getEmail();
            Log.i("Email", email);
        } else {
            Intent inten = new Intent(this, LoginActivity.class);
            startActivity(inten);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            // Log.e("CLOSE", "SHUT THE DOOR");
            finish();
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();

            /*
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            User = FirebaseDatabase.getInstance().getReference("User").child(myUID);
            User.keepSynced(true);

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("Status", "Last time available : "+ currentDateTimeString);
            User.updateChildren(map);
            */
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_navigation, menu);
        return true;
    }
    */

    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /* ALL ITEMS IN TOOLBAR PUT OVER HERE
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if(id == R.id.action_search){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
     */

    private void setupDrawerContent(NavigationView navigationView) {
        View headerView = navigationView.getHeaderView(0);
        profiles = headerView.findViewById(R.id.username_text);
        sharedPreferences = this.getSharedPreferences("CredentialsDB", MODE_PRIVATE);
        if (sharedPreferences != null) {
            username = sharedPreferences.getString("Username", "");
            Log.e("Username", username);
            profiles.setText(getString(R.string.welcome_messages, name));
            // profiles.setText(getString(R.string.welcome_messages, username));
        }
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    selectDrawerItem(menuItem);
                    return true;
                });
    }

    private void selectDrawerItem(MenuItem menuItem) {
        // Create first page (news-article)
        // fragmentClass = com.ipb.agrodeo.MainFragment.class;

        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();

        // ALL ICON IN NAVIGATION DRAWER PUT OVER HERE
        if (id == R.id.menuBeranda) {
            // Handle the menu Beranda action
            fragmentClass = com.ipb.agrodeo.MainFragment.class;
            toolbar.setSubtitle(R.string.home);

            if (!admin.equals("0")){
                fab.setVisibility(View.VISIBLE);
            }
            fab.setOnClickListener(view1 -> {
                Intent inten = new Intent(this, UploadArticleActivity.class);
                startActivity(inten);
            });
        } else if (id == R.id.menuKonsultasi) {
            // Handle the menu Konsultasi action
            Toast.makeText(this, "Masih dalam tahap pengembangan", Toast.LENGTH_SHORT).show();

            // fab.setVisibility(View.INVISIBLE);
            // fragmentClass = com.ipb.agrodeo.ConfirmationSOFragment.class;
            // toolbar.setSubtitle(R.string.consultation);
        } else if (id == R.id.menuMonitoring) {
            // Handle the menu Monitoring action
            // fragmentClass = com.ipb.agrodeo.MonitoringFragment.class;
            // toolbar.setSubtitle(R.string.monitoring);
            fab.setVisibility(View.INVISIBLE);

            fab.setOnClickListener(v -> {
                Intent intent = new Intent(this, SelectDeviceActivity.class);
                startActivity(intent);
            });

            // Create intent for startActivity
            Intent inten = new Intent(this, MonitoringLightActivity.class);
            startActivity(inten);

            // Toast.makeText(this, "Masih dalam tahap pengembangan", Toast.LENGTH_SHORT).show();
            // fragmentClass = com.ipb.agrodeo.MarketFragment.class;
        } else if (id == R.id.menuPasar) {
            // Handle the menu Pasar action
            fragmentClass = com.ipb.agrodeo.MarketFragment.class;
            toolbar.setSubtitle(R.string.market);

            if (!admin.equals("0")){
                fab.setVisibility(View.VISIBLE);
            }
            fab.setOnClickListener(view1 -> {
                Intent inten = new Intent(this, UploadMarketActivity.class);
                startActivity(inten);
            });
        } else if (id == R.id.menuCuaca) {
            // Handle the menu Cuaca action
            // Toast.makeText(this, "Masih dalam tahap pengembangan", Toast.LENGTH_SHORT).show();

            fab.setVisibility(View.INVISIBLE);
            fragmentClass = WeatherFragment.class;
            toolbar.setSubtitle(R.string.weather);
        } else if (id == R.id.menuProfil) {
            // Handle the menu Profil action
            fragmentClass = com.ipb.agrodeo.ProfileFragment.class;
            toolbar.setSubtitle(R.string.profile);
            fab.setVisibility(View.INVISIBLE);
        } else if (id == R.id.menuKeluar) {
            signOut();
        }

        /* else if (id == R.id.nav_manage) {
            logout();
        }
        */

        // Make a class into a new instances
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        // Get the fragment object and replace the layout
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameutama, fragment).setMaxLifecycle(fragment, Lifecycle.State.STARTED).commit();

        // Get the drawer object and close it
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }



    /*
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
     */
}