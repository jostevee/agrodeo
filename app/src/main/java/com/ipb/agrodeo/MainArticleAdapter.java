package com.ipb.agrodeo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import static com.ipb.agrodeo.UploadArticleActivity.myArticleDataRef;

public class MainArticleAdapter extends RecyclerView.Adapter<MainArticleAdapter.ViewHolder> {
    public List<MainArticleDataList> mainArticleDataLists;
    public Context ct;
    public ArrayList<MarketDataList> selectedItems = new ArrayList<>();
    public AlertDialog.Builder alertDialogSuccessBuilder;
    public AlertDialog alertDialogSuccess;
    private final boolean multiSelect = false;

    public MainArticleAdapter(List<MainArticleDataList> mainArticleDataLists, Context CT) {
        this.mainArticleDataLists = mainArticleDataLists;
        this.ct = CT;
    }

    @NonNull
    @Override
    public MainArticleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_article_data_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MainArticleDataList mainArticleDataList = mainArticleDataLists.get(position);
        holder.tvTitle.setText(mainArticleDataList.getTitleArticle());
        holder.tvContent.setText(mainArticleDataList.getContentArticle());
        Picasso.with(ct)
                .load(mainArticleDataList.getImageUrl())
                .into(holder.imageView);

        holder.itemView.setOnLongClickListener(v -> {
            alertDialogSuccessBuilder = new AlertDialog.Builder(v.getContext());
            alertDialogSuccessBuilder.setTitle("Menu Artikel")
                    .setMessage("Silakan pilih aksi yang akan anda lakukan")
                    .setPositiveButton("Ubah", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           editDataFromFirebase(mainArticleDataList);
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
                            deleteDataFromFirebase(mainArticleDataList.getArticleKey(), mainArticleDataList);
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

            // holder.update(mainArticleDataList, v);

            return true;
        });

        // Action for each item click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(ct, ArticleDetailActivity.class);
            intent.putExtra("title_article", mainArticleDataList.getTitleArticle());
            intent.putExtra("content_article", mainArticleDataList.getContentArticle());
            intent.putExtra("image_url", mainArticleDataList.getImageUrl());
            ct.startActivity(intent);
        });
    }

    private void editDataFromFirebase(MainArticleDataList mainArticleDataList) {
        Intent intent = new Intent(ct, EditArticleActivity.class);
        intent.putExtra("article_key", mainArticleDataList.getArticleKey());
        intent.putExtra("article_title", mainArticleDataList.getTitleArticle());
        intent.putExtra("article_content", mainArticleDataList.getContentArticle());
        intent.putExtra("image_url", mainArticleDataList.getImageUrl());
        ct.startActivity(intent);
    }

    private void deleteDataFromFirebase(String ArticleKey, MainArticleDataList mainArticleDataList) {
        Log.i("productKey", ArticleKey);
        mainArticleDataLists.remove(mainArticleDataList);
        notifyDataSetChanged();

        myArticleDataRef.child(ArticleKey).removeValue().addOnSuccessListener(aVoid -> {
            Log.e("SUCCESS","BERHASIL");
        }).addOnFailureListener(e -> {
            Toast.makeText(ct, "Artikel gagal dihapus, silakan periksa jaringan internet anda", Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public int getItemCount() {
        return mainArticleDataLists.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView tvTitle;
        public TextView tvContent;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_article);
            tvTitle = itemView.findViewById(R.id.title_article);
            tvContent = itemView.findViewById(R.id.content_article);
        }
    }
}
