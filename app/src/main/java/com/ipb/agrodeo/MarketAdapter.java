package com.ipb.agrodeo;

import static com.ipb.agrodeo.UploadMarketActivity.myMarketDataRef;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MarketAdapter extends RecyclerView.Adapter<MarketAdapter.ViewHolder> {
    public List<MarketDataList> marketDataLists;
    public Context ct;
    public ArrayList<MarketDataList> selectedItems = new ArrayList<>();
    public AlertDialog.Builder alertDialogSuccessBuilder;
    public AlertDialog alertDialogSuccess;

    /*
    private final ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            multiSelect = true;
            return false;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            multiSelect = false;
            selectedItems.clear();
            notifyDataSetChanged();
        }
    };
     */

    public MarketAdapter(List<MarketDataList> MarketDataLists, Context CT) {
        this.marketDataLists = MarketDataLists;
        this.ct = CT;
    }

    @NonNull
    @Override
    public MarketAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.market_data_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MarketDataList marketDataList = marketDataLists.get(position);
        holder.productName.setText(marketDataList.getProductName());
        holder.productPrice.setText(ct.getString(R.string.tv_product_price_detail, marketDataList.getProductPrice()));
        Picasso.with(ct)
                .load(marketDataList.getImageUrl())
                .into(holder.productImage);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                alertDialogSuccessBuilder = new AlertDialog.Builder(v.getContext());
                alertDialogSuccessBuilder.setTitle("Menu Pasar")
                        .setMessage("Silakan pilih aksi yang akan anda lakukan")
                        .setPositiveButton("Ubah", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editDataFromFirebase(marketDataList);
                            }
                        })
                        .setNeutralButton("Kembali", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Hapus", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteDataFromFirebase(marketDataList.getProductKey(), marketDataList);
                            }
                        })
                .setCancelable(false);
                alertDialogSuccess  = alertDialogSuccessBuilder.create();
                alertDialogSuccess.show();

                // Getting the view elements
                TextView textView = (TextView) alertDialogSuccess.getWindow().findViewById(android.R.id.message);
                TextView alertTitle = (TextView) alertDialogSuccess.getWindow().findViewById(R.id.alertTitle);
                Button button1 = (Button) alertDialogSuccess.getWindow().findViewById(android.R.id.button1);
                Button button2 = (Button) alertDialogSuccess.getWindow().findViewById(android.R.id.button2);
                Button button3 = (Button) alertDialogSuccess.getWindow().findViewById(android.R.id.button3);
                final Typeface typeface = ResourcesCompat.getFont(ct, R.font.poppins);
                final Typeface typefaceSemiBold = ResourcesCompat.getFont(ct, R.font.poppins_semibold);

                // Setting font
                textView.setTypeface(typeface);
                alertTitle.setTypeface(typefaceSemiBold);
                button1.setTypeface(typefaceSemiBold);
                button2.setTypeface(typefaceSemiBold);
                button3.setTypeface(typefaceSemiBold);

                // holder.update(marketDataList, v);
                // Toast.makeText(ct, "INI TEKAN LAMA", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        // Action for each item click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ct, MarketDetailActivity.class);
                intent.putExtra("product_name", marketDataList.getProductName());
                intent.putExtra("product_price", marketDataList.getProductPrice());
                intent.putExtra("product_weight", marketDataList.getProductWeight());
                intent.putExtra("product_location", marketDataList.getProductLocation());
                intent.putExtra("contact_number", marketDataList.getContactNumber());
                intent.putExtra("image_url", marketDataList.getImageUrl());
                ct.startActivity(intent);
            }

            /*
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MarketDetailActivity.class);
                intent.putExtra("product_name", marketDataList.getProductName());
                intent.putExtra("product_price", marketDataList.getProductPrice());
                intent.putExtra("product_weight", marketDataList.getProductWeight());
                intent.putExtra("product_location", marketDataList.getProductLocation());
                intent.putExtra("contact_number", marketDataList.getContactNumber());
                intent.putExtra("image_url", marketDataList.getImageUrl());
                v.getContext().startActivity(intent);
            }
             */
        });
        
        /*
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                holder.update(marketDataList);
                return true;
            }
        })
         */
    }

    private void editDataFromFirebase(MarketDataList marketDataList) {
        Intent intent = new Intent(ct, EditMarketActivity.class);
        intent.putExtra("product_key", marketDataList.getProductKey());
        intent.putExtra("product_name", marketDataList.getProductName());
        intent.putExtra("product_price", marketDataList.getProductPrice());
        intent.putExtra("product_weight", marketDataList.getProductWeight());
        intent.putExtra("product_location", marketDataList.getProductLocation());
        intent.putExtra("contact_number", marketDataList.getContactNumber());
        intent.putExtra("image_url", marketDataList.getImageUrl());
        ct.startActivity(intent);
    }

    private void deleteDataFromFirebase(String ProductKey, MarketDataList marketDataList) {
        Log.i("productKey", ProductKey);
        marketDataLists.remove(marketDataList);
        notifyDataSetChanged();

        myMarketDataRef.child(ProductKey).removeValue().addOnSuccessListener(aVoid -> {
            Log.e("SUCCESS","BERHASIL");
        }).addOnFailureListener(e -> {
            Log.e("FAILED", "FAILED");
        });
    }

    @Override
    public int getItemCount() {
        return marketDataLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public LinearLayout ItemList;
        public ImageView productImage;
        public TextView productName, productPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            ItemList = itemView.findViewById(R.id.list_item);

            /*
            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onItemClick(View v, int pos) {

                    //INTENT OBJ
                    Intent i=new Intent(c,DetailActivity.class);

                    //ADD DATA TO OUR INTENT
                    i.putExtra("Name",players[position]);
                    i.putExtra("Position",positions[position]);
                    i.putExtra("Image",images[position]);

                    //START DETAIL ACTIVITY
                    c.startActivity(i);

                }
            });
             */
        }

        /*
        private final ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                multiSelect = true;
                menu.add("Delete");
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                for (MarketDataList intItem : selectedItems) {
                    marketDataLists.remove(intItem);
                }
                mode.finish();
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                multiSelect = false;
                selectedItems.clear();
                notifyDataSetChanged();
            }
        };
         */

        void selectItem(MarketDataList item) {
            boolean multiSelect = false;
            if (multiSelect) {
                if (selectedItems.contains(item)) {
                    selectedItems.remove(item);
                    ItemList.setBackgroundColor(Color.WHITE);
                } else {
                    selectedItems.add(item);
                    ItemList.setBackgroundColor(R.attr.colorSecondary);
                }
            }
        }

        /*
        void update(final MarketDataList value, View v) {
            ((AppCompatActivity) v.getContext()).startSupportActionMode((androidx.appcompat.view.ActionMode.Callback) actionModeCallbacks);
            selectItem(value);

            if (selectedItems.contains(value)) {
                // car.setBackgroundColor(Color.LTGRAY);
            } else {
                ItemList.setBackgroundColor(Color.WHITE);
            }


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ((AppCompatActivity)view.getContext()).startSupportActionMode(actionModeCallbacks);
                    selectItem(value);
                    return true;
                }
            });


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // ((AppCompatActivity)view.getContext()).startSupportActionMode(actionModeCallbacks);
                    selectItem(value);
                }
            });

            // textView.setText(value + "");
        }
         */
    }

}
