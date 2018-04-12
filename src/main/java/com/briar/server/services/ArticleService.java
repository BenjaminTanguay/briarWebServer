package com.briar.server.services;

import com.briar.server.mapper.ArticleMapper;
import com.briar.server.model.domainmodelclasses.Article;
import com.briar.server.model.returnedtobriarclasses.BriarArticle;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ArticleService {

    private ArticleMapper articleMapper;

    public ArticleService(ArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
    }

    public List<BriarArticle> convertAllArticles(List<Article> articles){
        List<BriarArticle> briarArticles = new ArrayList<>();
        for(Article article: articles) {
            briarArticles.add(ArticleService.convertArticleToBriarArticle
                    (article));
        }
        return briarArticles;
    }

    private static BriarArticle convertArticleToBriarArticle(Article article) {
        String author = article.getAuthor();
        String publicationDate = parseDate(article.getPublicationDate());
        String title = article.getTitle();
        String[] body = parseBody(article.getBody());
        return new BriarArticle(author, publicationDate, title, body);
    }

    private static String parseDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy");
        String formattedDate = formatter.format(date);
        return formattedDate;
    }

    private static String[] parseBody(String string) {
        return string.split("%#%");
    }
}
