package com.ipb.agrodeo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.ipb.agrodeo.UploadArticleActivity.myArticleDataRef;
import static com.ipb.agrodeo.UploadMarketActivity.myMarketDataRef;

public class MainFragment extends Fragment {
    com.ipb.agrodeo.DatabaseHelper myDB;

    RecyclerView recyclerView;
    List<MainArticleDataList> mainArticleDataListArrayList = new ArrayList<>();
    MainArticleAdapter articleAdapter;
    String defaultValue, admin;

    // UI and UX
    TextView emptyArticle;
    ImageView articleLogo;

    // Progress Dialog
    ProgressDialog progressDialog;

    // RecyclerView.Adapter articleAdapter;
    // int[] colorIntArray = {R.color.walking,R.color.running,R.color.biking,R.color.paddling,R.color.golfing};
    /* VARIABLE USED INSIDE THE ACTIVITY
    private final int[] iconIntArray = {
            R.drawable.ic_chats,
            R.drawable.ic_new_chat,
            R.drawable.ic_menu_share};
    private TabLayout tabLayout;
    private final int[] tabIcons = {
            R.drawable.friends_and_groups,
            R.drawable.messages,
            R.drawable.recent_call
    };
     */

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_main_fragment, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerViewMainArticle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        // RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        // recyclerView.setLayoutManager(layoutManager);

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myDB = new com.ipb.agrodeo.DatabaseHelper(getActivity());

        // Get all the SharedPreferences variable
        SharedPreferences sharedPref = this.requireActivity().getSharedPreferences("accountData", Context.MODE_PRIVATE);
        defaultValue = getResources().getString(R.string.saved_default_key);
        admin = sharedPref.getString(getString(R.string.key_admin), defaultValue);

        // Assign activity this to progress dialog.
        progressDialog = new ProgressDialog(new ContextThemeWrapper(view.getContext(), R.style.Theme_Agrodeo_Poppins));
        progressDialog.setTitle("Mohon tunggu");
        progressDialog.setMessage("Data Artikel sedang diambil");
        progressDialog.show();

        // ============================= Floating Action Button Function ============================= //
        /*
        FloatingActionButton fab = requireActivity().findViewById(R.id.fab_main);
        if (!admin.equals("0")){
            fab.setVisibility(View.VISIBLE);
        }
        //fab.setImageResource(R.drawable.ic_google);
        fab.setOnClickListener(view1 -> {
            Intent inten = new Intent(requireActivity(), UploadArticleActivity.class);
            startActivity(inten);

            /*
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

        });
         */
        // ============================= Floating Action Button Function ============================= //

        /*
        myArticleDataRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                getDataFromFirebase(view, snapshot);

                /*
                Log.e("snapshotPost", String.valueOf(snapshot.getValue(MarketDataList.class)));
                for (DataSnapshot post : snapshot.getChildren()){
                    // MarketDataList marketDataList = post.getValue(MarketDataList.class);
                    // marketDataList.setProductKey(post.getKey());
                    // marketDataListArrayList.add(marketDataList);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                getDataFromFirebase(view, snapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                getDataFromFirebase(view, snapshot);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                getDataFromFirebase(view, snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });
        */

        myArticleDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                emptyArticle = view.findViewById(R.id.empty_article);
                articleLogo = view.findViewById(R.id.article_logo);
                mainArticleDataListArrayList.clear();

                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot post : dataSnapshot.getChildren()) {
                        MainArticleDataList mainArticleDataList = post.getValue(MainArticleDataList.class);
                        mainArticleDataList.setArticleKey(post.getKey());
                        mainArticleDataListArrayList.add(mainArticleDataList);
                    }
                    articleAdapter = new MainArticleAdapter(mainArticleDataListArrayList, view.getContext());
                    recyclerView.setAdapter(articleAdapter);

                    emptyArticle.setVisibility(View.INVISIBLE);
                    articleLogo.setVisibility(View.INVISIBLE);
                } else {
                    emptyArticle.setVisibility(View.VISIBLE);
                    articleLogo.setVisibility(View.VISIBLE);
                }

                // Hiding the progress dialog.
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
                // Hiding the progress dialog.
                progressDialog.dismiss();
            }
        });
    }

    /*
    private void getDataFromFirebase(View view, DataSnapshot dataSnapshot) {
        emptyArticle = view.findViewById(R.id.empty_article);
        articleLogo = view.findViewById(R.id.article_logo);

        if (dataSnapshot.hasChildren()) {
            /*
            for (DataSnapshot post : dataSnapshot.getChildren()){
                MarketDataList marketDataList = post.getValue(MarketDataList.class);
                marketDataList.setProductKey(post.getKey());
                marketDataListArrayList.add(marketDataList);
            }

            MainArticleDataList mainArticleDataList = dataSnapshot.getValue(MainArticleDataList.class);
            mainArticleDataList.setArticleKey(dataSnapshot.getKey());
            mainArticleDataListArrayList.add(mainArticleDataList);
            articleAdapter = new MainArticleAdapter(mainArticleDataListArrayList, view.getContext());
            articleAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(articleAdapter);

            emptyArticle.setVisibility(View.INVISIBLE);
            articleLogo.setVisibility(View.INVISIBLE);
        } else {
            emptyArticle.setVisibility(View.VISIBLE);
            articleLogo.setVisibility(View.VISIBLE);
        }

        // Hiding the progress dialog.
        progressDialog.dismiss();
    }
     */
}
