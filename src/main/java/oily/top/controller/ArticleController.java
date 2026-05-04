package oily.top.controller;

import oily.top.model.Article;
import oily.top.model.Category;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import java.util.List;

/**
 * 文章控制器 - 处理文章前台展示
 *
 * @author 奥利顶<oily.top>
 */
public class ArticleController extends Controller {

    /**
     * 文章详情页
     */
    public void index() {
        String slug = getPara(0);
        if (slug == null || slug.trim().isEmpty()) {
            redirect("/");
            return;
        }

        // 获取文章
        Article article = Article.dao.findFirst(
                "SELECT * FROM ARTICLE WHERE SLUG = ? AND STATUS = 1", slug
        );
        if (article == null) {
            redirect("/");
            return;
        }
// 增加浏览量
        Db.update("UPDATE ARTICLE SET VIEW_COUNT = VIEW_COUNT + 1 WHERE ID = ?", article.getInt("ID"));

        // 获取分类
        Category category = Category.dao.findById(article.getInt("CATEGORY_ID"));

        // 获取上一篇
        Article prevArticle = Article.dao.findFirst(
                "SELECT ID, TITLE, SLUG FROM ARTICLE WHERE ID < ? AND STATUS = 1 ORDER BY ID DESC LIMIT 1",
                article.getInt("ID")
        );

        // 获取下一篇
        Article nextArticle = Article.dao.findFirst(
                "SELECT ID, TITLE, SLUG FROM ARTICLE WHERE ID > ? AND STATUS = 1 ORDER BY ID ASC LIMIT 1",
                article.getInt("ID")
        );

        // 获取相关文章（同分类）
        List<Article> relatedArticles = Article.dao.find(
                "SELECT ID, TITLE, SLUG FROM ARTICLE WHERE CATEGORY_ID = ? AND ID != ? AND STATUS = 1 LIMIT 5",
                article.getInt("CATEGORY_ID"), article.getInt("ID")
        );

        // 获取分类列表
        List<Category> categories = Category.dao.find("SELECT * FROM CATEGORY ORDER BY SORT ASC");

        setAttr("article", article);
        setAttr("category", category);
        setAttr("prevArticle", prevArticle);
        setAttr("nextArticle", nextArticle);
        setAttr("relatedArticles", relatedArticles);
        setAttr("categories", categories);
        
        render("article.html");
    }

    /**
     * 分类文章列表
     */
    public void category() {
        String slug = getAttr("slug");
        if (slug == null || slug.trim().isEmpty()) {
            redirect("/");
            return;
        }
        int pageNum = getParaToInt("page", 1);

        Category category = Category.dao.getBySlug(slug);
        if (category == null) {
            renderError(404);
            return;
        }

        Page<Article> articlePage = Article.dao.paginateForFront(pageNum, 10, category.getInt("ID"), null, null);

        setAttr("articlePage", articlePage);
        setAttr("category", category);
        setAttr("categories", Category.dao.find("SELECT * FROM CATEGORY ORDER BY SORT ASC"));
        setAttr("hotArticles", Article.dao.getHotArticles(10));

        render("category.html");
    }

    /**
     * 标签文章列表
     */
    public void tag() {
        String tag = getPara(0);
        int pageNum = getParaToInt("page", 1);

        if (tag == null || tag.trim().isEmpty()) {
            redirect("/");
            return;
        }

        // 设置SEO信息
        setAttr("seo_title", tag + " - 标签 -奥利顶 Oily Blog");
        setAttr("seo_keywords", tag);
        setAttr("seo_description", "标签：" + tag + " 相关文章");

        // 获取标签下的文章
        setAttr("articlePage", Article.dao.paginateForFront(pageNum, 10, null, null, tag));
        setAttr("currentTag", tag);

        // 获取分类列表
        List<Category> categories = Category.dao.getCategoriesWithCount();
        setAttr("categories", categories);

        // 获取热门文章
        List<Article> hotArticles = Article.dao.getHotArticles(10);
        setAttr("hotArticles", hotArticles);

        render("tag.html");
    }

    /**
     * 搜索文章
     */
    public void search() {
        String keyword = getPara("keyword");
        int pageNum = getParaToInt("page", 1);

        if (keyword == null || keyword.trim().isEmpty()) {
            redirect("/");
            return;
        }

        // 设置SEO信息
        setAttr("seo_title", "搜索：" + keyword + " -奥利顶 Oily Blog");
        setAttr("seo_keywords", keyword);
        setAttr("seo_description", "搜索关键词：" + keyword);

        // 搜索文章
        setAttr("articlePage", Article.dao.paginateForFront(pageNum, 10, null, keyword, null));
        setAttr("keyword", keyword);

        // 获取分类列表
        List<Category> categories = Category.dao.getCategoriesWithCount();
        setAttr("categories", categories);

        // 获取热门文章
        List<Article> hotArticles = Article.dao.getHotArticles(10);
        setAttr("hotArticles", hotArticles);

        render("search.html");
    }

    /**
     * 文章归档
     */
    public void archive() {
        int year = getParaToInt("year", 0);
        int month = getParaToInt("month", 0);

        // 设置SEO信息
        setAttr("seo_title", "文章归档 -奥利顶 Oily Blog");
        setAttr("seo_keywords", "文章归档,历史文章");
        setAttr("seo_description", "所有文章按照时间归档");

        // 获取所有文章（按年月分组）
        List<Article> archives = Article.dao.getArchiveList();
        setAttr("archives", archives);

        // 获取分类列表
        List<Category> categories = Category.dao.getCategoriesWithCount();
        setAttr("categories", categories);

        // 获取热门文章
        List<Article> hotArticles = Article.dao.getHotArticles(10);
        setAttr("hotArticles", hotArticles);

        render("archive.html");
    }
}
