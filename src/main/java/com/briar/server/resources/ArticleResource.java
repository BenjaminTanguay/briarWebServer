package com.briar.server.resources;

import com.briar.server.mapper.ArticleMapper;
import com.briar.server.model.domainmodelclasses.Article;
import com.briar.server.model.returnedtobriarclasses.BriarArticle;
import com.briar.server.services.ArticleService;
import io.swagger.annotations.Api;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Component
@Path("/")
@Api
public class ArticleResource {
    private ArticleMapper articleMapper;
    private ArticleService articleService;

    public ArticleResource(ArticleMapper articleMapper){
        this.articleMapper = articleMapper;
        this.articleService = new ArticleService(articleMapper);
    }

    /**
     * Type: GET
     * Route: /articles
     * Method used to retieve all articles
     *
     * @return
     * {
     *     [
     *          {
     *              "publicationDate": "2018-04-14",
     *              "author": "String",
     *              "title": "String",
     *              "body": [
     *                          "Paragraph 1",
     *                          "Paragraph 2"
     *                      ]
     *          }
     *     ]
     * }
     */
    @GET
    @Path("/articles")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllArticles() {
        Response response;
        List<Article> articles = this.articleMapper.retrieveAllArticles();
        List<BriarArticle> briarArticles = this.articleService
                .convertAllArticles(articles);
        response = Response.status(Response.Status.OK).entity(briarArticles)
                .build();
        System.out.println(response);
        return response;
    }
}