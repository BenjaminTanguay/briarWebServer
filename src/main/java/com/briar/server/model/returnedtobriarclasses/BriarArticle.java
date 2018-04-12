package com.briar.server.model.returnedtobriarclasses;

public class BriarArticle {

    String author;
    String publicationDate;
    String title;
    String[] body;

    public BriarArticle() {
    }

    public BriarArticle(String author, String publicationDate,
                        String title, String[] body) {
        this.author = author;
        this.publicationDate = publicationDate;
        this.title = title;
        this.body = body;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getBody() {
        return body;
    }

    public void setBody(String[] body) {
        this.body = body;
    }
}
