package oily.top.model;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import java.util.List;
import oily.top.util.MarkdownUtil;
import org.commonmark.ext.gfm.tables.TablesExtension;

/**
 * 文章模型
 *
 * @author 奥利顶<oily.top>
 */
public class Article extends Model<Article> {

    public static final Article dao = new Article();

    /**
     * 分页查询文章（前台）
     */
    public Page<Article> paginateForFront(int pageNum, int pageSize, Integer categoryId, String keyword, String tag) {
        StringBuilder select = new StringBuilder("SELECT a.ID, a.TITLE, a.SLUG, a.SUMMARY, a.VIEW_COUNT, a.CREATE_TIME, c.NAME as CATEGORY_NAME, c.SLUG as CATEGORY_SLUG");
        StringBuilder sqlExceptSelect = new StringBuilder("FROM ARTICLE a LEFT JOIN CATEGORY c ON a.CATEGORY_ID = c.ID WHERE a.STATUS = 1");

        if (categoryId != null && categoryId > 0) {
            sqlExceptSelect.append(" AND a.CATEGORY_ID = ").append(categoryId);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sqlExceptSelect.append(" AND (a.TITLE LIKE '%").append(keyword).append("%' OR a.CONTENT LIKE '%").append(keyword).append("%')");
        }
        if (tag != null && !tag.trim().isEmpty()) {
            sqlExceptSelect.append(" AND a.TAGS LIKE '%").append(tag).append("%'");
        }

        sqlExceptSelect.append(" ORDER BY a.IS_TOP DESC, a.CREATE_TIME DESC");

        return Article.dao.paginate(pageNum, pageSize, select.toString(), sqlExceptSelect.toString());
    }

    /**
     * 分页查询文章（后台）
     */
    public Page<Article> paginateForAdmin(int pageNum, int pageSize, Integer status) {
        String select = "SELECT *";
        String sqlExceptSelect = "FROM article WHERE 1=1";

        if (status != null) {
            sqlExceptSelect += " AND status=" + status;
        }

        sqlExceptSelect += " ORDER BY create_time DESC";

        return Article.dao.paginate(pageNum, pageSize, select, sqlExceptSelect);
    }

    /**
     * 通过slug获取文章
     */
    public Article getBySlug(String slug) {
        return Article.dao.findFirst(
                "SELECT a.ID, a.TITLE, a.SLUG, a.SUMMARY, a.CONTENT, a.CONTENT_HTML, "
                + "a.CATEGORY_ID, a.TAGS, a.VIEW_COUNT, a.STATUS, a.IS_TOP, a.CREATE_TIME, "
                + "c.NAME as CATEGORY_NAME, c.SLUG as CATEGORY_SLUG "
                + "FROM ARTICLE a LEFT JOIN CATEGORY c ON a.CATEGORY_ID = c.ID "
                + "WHERE a.SLUG = ? AND a.STATUS = 1", slug
        );
    }

    /**
     * 获取上一篇/下一篇
     */
    public Article getPrevArticle(int id, int categoryId) {
        return Article.dao.findFirst(
                "SELECT ID, TITLE, SLUG FROM ARTICLE WHERE ID < ? AND STATUS = 1 AND CATEGORY_ID = ? ORDER BY ID DESC LIMIT 1",
                id, categoryId
        );
    }

    public Article getNextArticle(int id, int categoryId) {
        return Article.dao.findFirst(
                "SELECT ID, TITLE, SLUG FROM ARTICLE WHERE ID > ? AND STATUS = 1 AND CATEGORY_ID = ? ORDER BY ID ASC LIMIT 1",
                id, categoryId
        );
    }

    /**
     * 增加浏览次数
     */
    public void incrementViewCount(int id) {
        Article article = Article.dao.findById(id);
        if (article != null) {
            int currentViews = article.getInt("view_count") == null ? 0 : article.getInt("view_count");
            article.set("view_count", currentViews + 1).update();
        }
    }

    /**
     * 获取热门文章
     */
    public List<Article> getHotArticles(int limit) {
        return Article.dao.find(
                "SELECT ID, TITLE, SLUG, VIEW_COUNT FROM ARTICLE "
                + "WHERE STATUS = 1 ORDER BY VIEW_COUNT DESC LIMIT ?", limit
        );
    }

    /**
     * 获取相关文章
     */
    public List<Article> getRelatedArticles(int categoryId, int currentId, int limit) {
        return Article.dao.find(
                "SELECT ID, TITLE, SLUG, CREATE_TIME FROM ARTICLE "
                + "WHERE CATEGORY_ID = ? AND ID != ? AND STATUS = 1 "
                + "ORDER BY CREATE_TIME DESC LIMIT ?", categoryId, currentId, limit
        );
    }

    /**
     * 获取归档文章（按年月分组）
     */
    public List<Article> getArchiveList() {
        return Article.dao.find(
                "SELECT ID, TITLE, SLUG, CREATE_TIME FROM ARTICLE "
                + "WHERE STATUS = 1 ORDER BY CREATE_TIME DESC"
        );
    }
}
