package com.briar.server.model.domainmodelclasses;

import java.util.Date;

public class Article {

    Date publicationDate;
    String author;
    String title;
    String body;

    public Article() {

    }

    public Article(Date publicationDate, String author, String title,
                   String body) {
        this.publicationDate = publicationDate;
        this.author = author;
        this.title = title;
        this.body = body;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Article{" +
                "publicationDate=" + publicationDate +
                ", author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Article)) {
            return false;
        }

        Article article = (Article) o;

        if (getPublicationDate() != null ?
                !getPublicationDate().equals(article.getPublicationDate()) :
                article.getPublicationDate() != null) {
            return false;
        }
        if (getAuthor() != null ? !getAuthor().equals(article.getAuthor()) :
                article.getAuthor() != null) {
            return false;
        }
        if (getTitle() != null ? !getTitle().equals(article.getTitle()) :
                article.getTitle() != null) {
            return false;
        }
        return getBody() != null ? getBody().equals(article.getBody()) :
                article.getBody() == null;

    }

    @Override
    public int hashCode() {
        int result =
                getPublicationDate() != null ? getPublicationDate().hashCode() :
                        0;
        result = 31 * result +
                (getAuthor() != null ? getAuthor().hashCode() : 0);
        result = 31 * result + (getTitle() != null ? getTitle().hashCode() : 0);
        result = 31 * result + (getBody() != null ? getBody().hashCode() : 0);
        return result;
    }
}
