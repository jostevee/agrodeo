package com.ipb.agrodeo;

public class MainArticleDataList {
    String articleKey;
    String titleArticle;
    String contentArticle;
    String imageUrl;

    public MainArticleDataList(){
    }

    public String getArticleKey() {
        return articleKey;
    }

    public void setArticleKey(String articleKey) {
        this.articleKey = articleKey;
    }

    public void setTitleArticle(String TitleArticle) {
        this.titleArticle = TitleArticle;
    }

    public String getTitleArticle() {
        return titleArticle;
    }

    public void setContentArticle(String ContentArticle) {
        this.contentArticle = ContentArticle;
    }

    public String getContentArticle() {
        return contentArticle;
    }

    public void setImageUrl(String ImageUrl) {
        this.imageUrl = ImageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public MainArticleDataList(String ArticleKey, String TitleArticle, String ContentArticle, String ImageUrl){
        this.articleKey = ArticleKey;
        this.titleArticle = TitleArticle;
        this.contentArticle = ContentArticle;
        this.imageUrl = ImageUrl;
    }
}
