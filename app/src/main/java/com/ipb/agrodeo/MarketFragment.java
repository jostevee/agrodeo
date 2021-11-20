package com.ipb.agrodeo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static com.ipb.agrodeo.UploadMarketActivity.myMarketDataRef;


public class MarketFragment extends Fragment {

    // TODO: All the parameters for fragment
    RecyclerView recyclerView;
    ArrayList<MarketDataList> marketDataListArrayList = new ArrayList<>();
    MarketAdapter marketAdapter;
    String defaultValue, seller;

    TextView emptyMarket;
    ImageView marketLogo;
    ProgressDialog progressDialog;
    SwipeRefreshLayout smLayout;

    // private ActionMode mActionMode;
    // RecyclerView.Adapter adapter ;

    public MarketFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    /*
    public static MarketFragment newInstance(String param1, String param2) {
        MarketFragment fragment = new MarketFragment();
        Bundle args = new Bundle();
        // args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }
     */

    /*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //int defaultValue = getResources().getString(R.string.saved_high_score_default_key);
    }
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_market, container, false);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        // recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        smLayout = view.findViewById(R.id.swipeMarketLayout);

        // Assign activity this to progress dialog.
        progressDialog = new ProgressDialog(new ContextThemeWrapper(view.getContext(), R.style.Theme_Agrodeo_Poppins));
        progressDialog.setTitle("Mengambil data dari database");
        progressDialog.setMessage("Mohon tunggu");
        progressDialog.show();

        // Get all the SharedPreferences variable
        SharedPreferences sharedPref = this.requireActivity().getSharedPreferences("accountData", Context.MODE_PRIVATE);
        defaultValue = getResources().getString(R.string.saved_default_key);
        seller = sharedPref.getString(getString(R.string.key_seller), defaultValue);

        // Get from Firebase
        getMarketDataFromFirebase(view);

        // Mengeset listener yang akan dijalankan saat layar di refresh/swipe
        smLayout.setOnRefreshListener(() -> {
            // Get from Firebase
            getMarketDataFromFirebase(view);

            // Handler 5 secs
            new Handler().postDelayed(() -> smLayout.setRefreshing(false), 5000);
        });

    }

    private void getMarketDataFromFirebase(View view) {
        myMarketDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                emptyMarket = view.findViewById(R.id.empty_market);
                marketLogo = view.findViewById(R.id.market_logo);
                marketDataListArrayList.clear();

                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot post : dataSnapshot.getChildren()){
                        MarketDataList marketDataList = post.getValue(MarketDataList.class);
                        marketDataList.setProductKey(post.getKey());
                        marketDataListArrayList.add(marketDataList);
                    }
                    marketAdapter = new MarketAdapter(marketDataListArrayList, view.getContext());
                    recyclerView.setAdapter(marketAdapter);

                    emptyMarket.setVisibility(View.INVISIBLE);
                    marketLogo.setVisibility(View.INVISIBLE);
                } else {
                    emptyMarket.setVisibility(View.VISIBLE);
                    marketLogo.setVisibility(View.VISIBLE);
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
    @Override
    public boolean onLongClick(View v) {
        if (mActionMode != null) {
            return false;
        }
        mActionMode = v.startActionMode((android.view.ActionMode.Callback) mActionModeCallback);
        return true;
    }

    private final ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.fragment_market_action_mode, menu);
            mode.setTitle("Choose your option");
            return true;
        }
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.menuHapus) {
                Toast.makeText(requireContext(), "Option 1 selected", Toast.LENGTH_SHORT).show();
                mode.finish();
                return true;
            } else if(item.getItemId() == R.id.menuUbah) {
                Toast.makeText(requireContext(), "Option 2 selected", Toast.LENGTH_SHORT).show();
                mode.finish();
                return true;
            } else {
                return false;
            }
        }
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };
     */
}