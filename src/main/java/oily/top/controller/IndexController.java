package oily.top.controller;

import com.jfinal.core.Controller;
import java.util.Date;
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
        setAttr("hotArticles", Article.dao.find("SELECT id,title,slug,view_count FROM article "
                + "WHERE status=1 ORDER BY view_count DESC LIMIT 10"));

        setAttr("currentCate", categoryId);
        setAttr("keyword", keyword);
        set("currentYear", java.time.LocalDate.now().getYear());

        render("index.html");
    }

    /**
     * 生成站点地图
     */
//    public void sitemap() {
//        // 获取所有已发布的文章
//        List<Article> articles = Article.dao.find(
//                "SELECT SLUG, CREATE_TIME FROM ARTICLE WHERE STATUS = 1 ORDER BY CREATE_TIME DESC"
//        );
//
//        // 获取所有分类
//        List<Category> categories = Category.dao.find("SELECT SLUG FROM CATEGORY");
//
//        setAttr("articles", articles);
//        setAttr("categories", categories);
//        setAttr("now", new Date());
//
//        // 设置响应类型为 XML
//        getResponse().setContentType("application/xml");
//        getResponse().setCharacterEncoding("UTF-8");
//
//        render("sitemap.xml");
//    }
public void sitemap() {
    // 1. 获取数据
    List<Article> articles = Article.dao.find(
            "SELECT SLUG, CREATE_TIME FROM ARTICLE WHERE STATUS = 1 ORDER BY CREATE_TIME DESC"
        );
    List<Category> categories = Category.dao.find("SELECT SLUG FROM CATEGORY");
    
    // 2. 手动构建 XML 字符串（这里使用 StringBuilder 提高性能）
    StringBuilder xml = new StringBuilder();
    xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");
    
    // 首页
    xml.append("    <url>\n");
    xml.append("        <loc>https://www.oily.top/</loc>\n");
    xml.append("        <lastmod>").append(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date())).append("</lastmod>\n");
    xml.append("        <changefreq>daily</changefreq>\n");
    xml.append("        <priority>1.0</priority>\n");
    xml.append("    </url>\n");
    
    // 所有文章
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
    for (Article article : articles) {
        xml.append("    <url>\n");
        xml.append("        <loc>https://www.oily.top/article/").append(article.getStr("SLUG")).append("</loc>\n");
        xml.append("        <lastmod>").append(sdf.format(article.getDate("CREATE_TIME"))).append("</lastmod>\n");
        xml.append("        <changefreq>daily</changefreq>\n");
        xml.append("        <priority>0.8</priority>\n");
        xml.append("    </url>\n");
    }
    
    // 所有分类
    for (Category cate : categories) {
        xml.append("    <url>\n");
        xml.append("        <loc>https://www.oily.top/c/").append(cate.getStr("SLUG")).append("</loc>\n");
        xml.append("        <changefreq>monthly</changefreq>\n");
        xml.append("        <priority>0.6</priority>\n");
        xml.append("    </url>\n");
    }
    
    xml.append("</urlset>");
    
    // 3. 输出
    getResponse().setContentType("application/xml");
    getResponse().setCharacterEncoding("UTF-8");
    renderText(xml.toString());
}
    /**
     * robots.txt
     */
    public void robots() {
        getResponse().setContentType("text/plain");
        renderText("User-agent: *\n"
                + "Allow: /\n"
                + "Disallow: /admin/\n\n"
                + "Sitemap: https://www.oily.top/sitemap.xml");
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
