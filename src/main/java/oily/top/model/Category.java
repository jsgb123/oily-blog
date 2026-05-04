package oily.top.model;

import com.jfinal.plugin.activerecord.Model;
import java.util.List;

/**
 * 分类模型
 *
 * @author 奥利顶<oily.top>
 */
public class Category extends Model<Category> {

    public static final Category dao = new Category();

    /**
     * 获取所有分类
     */
    public List<Category> getAllCategories() {
        return Category.dao.find("SELECT * FROM category ORDER BY SORT ASC, id ASC");
    }

    /**
     * 通过slug获取分类
     */
    public Category getBySlug(String slug) {
        return Category.dao.findFirst("SELECT * FROM category WHERE SLUG=?", slug);
    }

    // 获取分类下的文章数量
    public long getArticleCount(int categoryId) {
        try {
            String sql = "SELECT COUNT(*) as count FROM article WHERE category_id=? AND status=1";
            Article article = Article.dao.findFirst(sql, categoryId);
            return article != null ? article.getLong("count") : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public Category findById(Object id) {
        return findFirst("SELECT * FROM CATEGORY WHERE ID = ?", id);
    }

    /**
     * 获取所有分类及文章数量
     */
    public List<Category> getCategoriesWithCount() {
        List<Category> categories = getAllCategories();
        for (Category cate : categories) {
            long count = getArticleCount(cate.getInt("id"));
            cate.put("article_count", count);
        }
        return categories;
    }
}
