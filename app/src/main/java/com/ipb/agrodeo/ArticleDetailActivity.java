package com.ipb.agrodeo;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ArticleDetailActivity extends AppCompatActivity {
    AppCompatTextView title_article, content_article;
    AppCompatImageView article_image;
    String titleArticle, contentArticle, imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        article_image = findViewById(R.id.image_article_detail);
        content_article = findViewById(R.id.content_article_detail);

        // title_article = findViewById(R.id.title_article_detail);

        // Toolbar setup
        final Toolbar toolbar = findViewById(R.id.toolbar);

        // toolbar.setTitle(R.string.app_name);
        // toolbar.setSubtitle(R.string.article_detail);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitleEnabled(true);
        collapsingToolbarLayout.setContentScrimColor(R.attr.backgroundColor);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            titleArticle = extras.getString("title_article");
            contentArticle = extras.getString("content_article");
            imageUrl = extras.getString("image_url");

            Log.d("ArticleDetail", titleArticle);
            Log.d("ArticleDetail", contentArticle);
            Log.d("ArticleDetail", imageUrl);

            collapsingToolbarLayout.setTitle(titleArticle);
            content_article.setText(contentArticle);
            Picasso.with(getApplicationContext())
                    .load(imageUrl)
                    .into(article_image);

            // title_article.setText(titleArticle);
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