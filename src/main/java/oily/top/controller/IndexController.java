
package oily.top.controller;

import com.jfinal.core.Controller;
import java.util.List;
import oily.top.model.Article;
import oily.top.model.Category;
/**
 *
 * @author 奥利顶<oily.top>
 */
public class IndexController extends Controller {
    
    public void index() {
        int pageNum = getParaToInt("page", 1);
        Integer categoryId = getParaToInt("cate");
        String keyword = getPara("keyword");
        
        // 设置SEO信息
        setAttr("seo_title", getSiteTitle());
        setAttr("seo_keywords", getSiteKeywords());
        setAttr("seo_description", getSiteDescription());
        
        // 获取文章列表
        setAttr("articlePage", Article.dao.paginateForFront(pageNum, 10, categoryId, keyword, null));
        
        // 获取分类列表
        List<Category> categories = Category.dao.find("SELECT * FROM category ORDER BY sort");
        setAttr("categories", categories);
        
        // 获取热门文章
        setAttr("hotArticles", Article.dao.find("SELECT id,title,slug,view_count FROM article " +
                                                "WHERE status=1 ORDER BY view_count DESC LIMIT 10"));
        
        setAttr("currentCate", categoryId);
        setAttr("keyword", keyword);
        set("currentYear", java.time.LocalDate.now().getYear());
        
        System.out.println("oily.top.controller.IndexController.index()"+getAttr("seo_title"));
        render("index.html");
    }
    
    public void sitemap() {
        List<Article> articles = Article.dao.find("SELECT slug,update_time FROM article WHERE status=1");
        setAttr("articles", articles);
        render("sitemap.xml");
    }
    
    private String getSiteTitle() {
        return "奥利顶 Oily Blog - 编程技术 | 游戏逆向 | 反外挂 | 游戏防护 | 游戏引擎";
    }
    
    private String getSiteKeywords() {
        return "技术博客,Java,Python,Rust,游戏逆向,前端开发,全栈编程";
    }
    
    private String getSiteDescription() {
        return "奥利顶 Oily Blog - 分享编程技术、游戏攻防、游戏逆向、反外挂研究的个人博客";
    }
}