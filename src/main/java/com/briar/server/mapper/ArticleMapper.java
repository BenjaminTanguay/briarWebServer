package com.briar.server.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ArticleMapper {

    @Insert("insert into articles" +
            "(publication_date, author, title, body, created, added_by) " +
            " values (#{publicationDate}, #{author}, #{title}, #{body}," +
            " current_timestamp(), #{addedBy});")
    void addArticle(Article article);

    @Select("select publication_date, author, title, body " +
            "from articles " +
            "order by id desc")
    List<Article> retrieveAllArticles();
}
