package com.ipb.agrodeo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/*
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    AppCompatTextView tvAcademic, tvBirthdate, tvCommodity, tvLocation, tvName, tvPhone, tvReligion, tvRole, tvSex;
    String defaultValue, education, birthdate, commodity, location, name, phone, religion, role, sex;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    // private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters

    public ProfileFragment() {
        // Required empty public constructor
    }

    /*
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    /*
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // int defaultValue = getResources().getString(R.string.saved_high_score_default_key);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        // Get all the SharedPreferences variable
        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("accountData", Context.MODE_PRIVATE);
        defaultValue = getResources().getString(R.string.saved_default_key);
        education = sharedPref.getString(getString(R.string.key_education), defaultValue);
        birthdate = sharedPref.getString(getString(R.string.key_birthdate), defaultValue);
        commodity = sharedPref.getString(getString(R.string.key_commodity), defaultValue);
        location = sharedPref.getString(getString(R.string.key_location), defaultValue);
        name = sharedPref.getString(getString(R.string.key_name), defaultValue);
        phone = sharedPref.getString(getString(R.string.key_phone), defaultValue);
        // religion = sharedPref.getString(getString(R.string.key_religion), defaultValue);
        role = sharedPref.getString(getString(R.string.key_role), defaultValue);
        sex = sharedPref.getString(getString(R.string.key_sex), defaultValue);

        // Get all the object
        tvName = rootView.findViewById(R.id.tvName2);
        tvAcademic = rootView.findViewById(R.id.tvAcademic2);
        tvBirthdate = rootView.findViewById(R.id.tvBirthdate2);
        tvCommodity = rootView.findViewById(R.id.tvCommodity2);
        tvLocation = rootView.findViewById(R.id.tvLocation2);
        tvPhone = rootView.findViewById(R.id.tvPhone2);
        tvRole = rootView.findViewById(R.id.tvRole2);
        tvSex = rootView.findViewById(R.id.tvSex2);
        // tvReligion = rootView.findViewById(R.id.tvReligion2);

        // Set all the text
        tvName.setText(getString(R.string.welcome_messages, name));
        tvAcademic.setText(education);
        tvBirthdate.setText(birthdate);
        tvCommodity.setText(commodity);
        tvLocation.setText(location);
        tvPhone.setText(phone);
        tvRole.setText(role);
        tvSex.setText(sex);
        // tvReligion.setText(religion);

        /*
        Log.d("User Details", education);
        Log.d("User Details", birthdate);
        Log.d("User Details", commodity);
        Log.d("User Details", location);
        Log.d("User Details", name);
        Log.d("User Details", phone);
        Log.d("User Details", religion);
        Log.d("User Details", role);
        Log.d("User Details", sex);
         */

        // Inflate the layout for this fragment
        return rootView;
    }
}