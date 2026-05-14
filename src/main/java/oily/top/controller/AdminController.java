package oily.top.controller;

import oily.top.model.Article;
import oily.top.model.Category;
import oily.top.model.User;
import com.jfinal.core.Controller;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

/**
 * 后台管理控制器
 *
 * @author 奥利顶<oily.top>
 */
public class AdminController extends Controller {

    /**
     * 登录页面
     */
    public void login() {
        render("admin/login.html");
    }

    /**
     * 执行登录
     */
    public void doLogin() {
        String username = getPara("username");
        String password = getPara("password");

        if (StrKit.isBlank(username) || StrKit.isBlank(password)) {
            setAttr("error", "用户名和密码不能为空");
            render("admin/login.html");
            return;
        }

        User user = User.dao.login(username, password);
        if (user != null) {
            setSessionAttr("admin", user);
            redirect("/admin/index");
        } else {
            setAttr("error", "用户名或密码错误");
            render("admin/login.html");
        }
    }

    /**
     * 后台首页
     */
    public void index() {
        if (!isLogin()) {
            return;
        }
        User admin = getSessionAttr("admin");
        setAttr("admin", admin);

        // 使用 Db.queryLong 更安全
        Long articleCount = Db.queryLong("SELECT COUNT(*) FROM article");
        Long todayCount = Db.queryLong("SELECT COUNT(*) FROM article WHERE DATE(create_time)=CURDATE()");
        Long draftCount = Db.queryLong("SELECT COUNT(*) FROM article WHERE status=0");
        Long totalViews = Db.queryLong("SELECT SUM(view_count) FROM article");
        Long categoryCount = Db.queryLong("SELECT COUNT(*) FROM category");

        setAttr("articleCount", articleCount != null ? articleCount : 0);
        setAttr("todayCount", todayCount != null ? todayCount : 0);
        setAttr("draftCount", draftCount != null ? draftCount : 0);
        setAttr("totalViews", totalViews != null ? totalViews : 0);
        setAttr("categoryCount", categoryCount != null ? categoryCount : 0);

        // 最近文章
        setAttr("recentArticles", Article.dao.find("SELECT a.*, c.name as category_name FROM article a LEFT JOIN category c ON a.category_id=c.id ORDER BY a.create_time DESC LIMIT 10"));

        // 热门文章
        setAttr("hotArticles", Article.dao.find("SELECT * FROM article WHERE status=1 ORDER BY view_count DESC LIMIT 5"));

        render("admin/index.html");
    }

    /**
     * 文章列表
     */
    public void articles() {
        if (!isLogin()) {
            return;
        }

        int pageNum = getParaToInt("page", 1);
        Integer status = getParaToInt("status");

        setAttr("articlePage", Article.dao.paginateForAdmin(pageNum, 15, status));
        setAttr("status", status);

        render("admin/articles.html");
    }

    /**
     * 发布文章页面
     */
    public void publish() {
        if (!isLogin()) {
            return;
        }

        setAttr("categories", Category.dao.getAllCategories());
        render("admin/publish.html");
    }

    /**
     * 执行发布
     */
    public void doPublish() {
        if (!isLogin()) {
            return;
        }

        Map<String, Object> result = new HashMap<>();

        try {
            String title = getPara("title");
            String slug = getPara("slug");
            String summary = getPara("summary");
            String content = getPara("content");
            Integer categoryId = getParaToInt("category_id");
            String tags = getPara("tags");
            Integer status = getParaToInt("status", 1);
            Integer isTop = getParaToInt("is_top", 0);

// 验证必填字段
            if (title == null || title.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "标题不能为空");
                renderJson(result);
                return;
            }

            if (content == null || content.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "内容不能为空");
                renderJson(result);
                return;
            }
// 检测slug
            slug = generateSlug(title);

            // 检查slug是否唯一
            Article existArticle = Article.dao.findFirst("SELECT id FROM article WHERE slug=?", slug);
            System.out.println("oily.top.controller.AdminController.doPublish()" + (existArticle == null));
            if (existArticle != null) {
                slug = slug + System.currentTimeMillis();
            }

            // 创建文章对象
            Article article = new Article();
            article.set("TITLE", title);
            article.set("SLUG", slug);
            article.set("SUMMARY", summary != null ? summary : "");
            article.setContentAndRender(content);
            article.set("CATEGORY_ID", categoryId);
            article.set("TAGS", tags != null ? tags : "");
            article.set("VIEW_COUNT", 0);
            article.set("STATUS", status);
            article.set("IS_TOP", isTop);
            article.set("CREATE_TIME", new Date());

            if (article.save()) {
                result.put("success", true);
                result.put("message", "发布成功");
                result.put("id", article.getInt("id"));
            } else {
                result.put("success", false);
                result.put("message", "发布失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", e.getMessage());
        }

        renderJson(result);
    }

    /**
     * 编辑文章页面
     */
    public void edit() {
        if (!isLogin()) {
            return;
        }

        int id = getParaToInt(0);
        Article article = Article.dao.findById(id);
        if (article == null) {
            redirect("/admin/articles");
            return;
        }

        setAttr("article", article);
        setAttr("categories", Category.dao.getAllCategories());
        render("admin/edit.html");
    }

    /**
     * 执行编辑
     */
    public void doEdit() {
        if (!isLogin()) {
            return;
        }

        Map<String, Object> result = new HashMap<>();

        try {
            int id = getParaToInt("id");
            String title = getPara("title");
            //String slug = getPara("slug");
            String summary = getPara("summary");
            String content = getPara("content");
            Integer categoryId = getParaToInt("category_id");
            String tags = getPara("tags");
            Integer status = getParaToInt("status", 1);
            Integer isTop = getParaToInt("is_top", 0);

            Article article = Article.dao.findById(id);
            if (article == null) {
                result.put("success", false);
                result.put("message", "文章不存在");
                renderJson(result);
                return;
            }

            // 生成 HTML
            String contentHtml = convertMarkdownToHtml(content);

            // 使用 Db.update 直接更新
            int updateResult = Db.update(
                    "UPDATE article SET title = ?, summary = ?, content = ?, content_html = ?, category_id = ?, tags = ?, status = ?, is_top = ? WHERE id = ?",
                    title, summary != null ? summary : "", content, contentHtml,
                    categoryId, tags != null ? tags : "", status, isTop, id
            );

            if (updateResult > 0) {
                result.put("success", true);
                result.put("message", "保存成功");
            } else {
                result.put("success", false);
                result.put("message", "保存失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", e.getMessage());
        }

        renderJson(result);
    }

    /**
     * 删除文章
     */
    public void delete() {
        if (!isLogin()) {
            return;
        }

        int id = getParaToInt(0);
        boolean deleted = Article.dao.deleteById(id);

        if (deleted) {
            redirect("/admin/articles");
        } else {
            setAttr("error", "删除失败");
            render("admin/error.html");
        }
    }

    /**
     * 批量删除
     */
    public void batchDelete() {
        if (!isLogin()) {
            return;
        }

        String ids = getPara("ids");
        if (StrKit.notBlank(ids)) {
            String[] idArray = ids.split(",");
            for (String id : idArray) {
                Article.dao.deleteById(Integer.parseInt(id));
            }
        }
        redirect("/admin/articles");
    }

    /**
     * 分类管理
     */
    public void categories() {
        if (!isLogin()) {
            return;
        }

        setAttr("categories", Category.dao.getAllCategories());
        render("admin/categories.html");
    }

    /**
     * 添加分类
     */
    public void addCategory() {
        if (!isLogin()) {
            return;
        }

        String name = getPara("name");
        String slug = getPara("slug");
        String description = getPara("description");
        Integer sort = getParaToInt("sort", 0);

        slug = generateSlug(name);

        Category category = new Category();
        category.set("NAME", name);
        category.set("SLUG", slug);
        category.set("DESCRIPTION", description);
        category.set("SORT", sort);

        boolean success = category.save();
        renderJson("success", success);
    }

    /**
     * 更新分类
     */
    public void updateCategory() {
        if (!isLogin()) {
            return;
        }

        Integer id = getParaToInt("id");
        String name = getPara("name");
        String slug = getPara("slug");
        String description = getPara("description");
        Integer sort = getParaToInt("sort", 0);
        if (id == null) {
            renderJson("success", false);
            return;
        }

        // 直接更新，不查询
        int result = Db.update("UPDATE CATEGORY SET NAME = ?, SLUG = ?, DESCRIPTION = ?, SORT = ? WHERE ID = ?",
                name, slug, description, sort, id);
        renderJson("success", result > 0);
    }

    /**
     * 删除分类
     */
    public void deleteCategory() {
        if (!isLogin()) {
            return;
        }

        int id = getParaToInt(0);

        Db.update("UPDATE article SET CATEGORY_ID=NULL WHERE CATEGORY_ID =?", id);
        boolean deleted = Category.dao.deleteById(id);
        redirect("/admin/categories");
    }

    /**
     * 个人设置页面
     */
    public void profile() {
        if (!isLogin()) {
            return;
        }
        User admin = getSessionAttr("admin");
        setAttr("admin", admin);
        render("admin/profile.html");
    }

    /**
     * 更新个人资料
     */
    public void updateProfile() {
        if (!isLogin()) {
            return;
        }

        User admin = getSessionAttr("admin");
        String nickname = getPara("nickname");
        String email = getPara("email");
        String bio = getPara("bio");

        int result = Db.update("UPDATE USER SET NICKNAME = ?, EMAIL = ? WHERE ID = ?",
                nickname, email, admin.getInt("ID"));

        // 更新session中的用户信息
        User newAdmin = User.dao.findById(admin.getInt("ID"));
        setSessionAttr("admin", newAdmin);

        renderJson("success", result > 0);
    }

    /**
     * 修改密码
     */
    public void changePassword() {
        if (!isLogin()) {
            return;
        }

        User admin = getSessionAttr("admin");
        String oldPassword = getPara("oldPassword");
        String newPassword = getPara("newPassword");

        // 验证旧密码
        String oldMd5 = HashKit.md5(oldPassword);
        String dbPassword = admin.getStr("PASSWORD");

        if (!dbPassword.equals(oldMd5)) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "当前密码错误");
            renderJson(result);
            return;
        }

        // 更新密码
        String newMd5 = HashKit.md5(newPassword);
        int result = Db.update("UPDATE USER SET PASSWORD = ? WHERE ID = ?", newMd5, admin.getInt("ID"));

        renderJson("success", result > 0);
    }

    /**
     * 退出登录
     */
    public void logout() {
        removeSessionAttr("admin");
        redirect("/admin/login");
    }

    /**
     * 检查登录状态
     */
    private boolean isLogin() {
        if (getSessionAttr("admin") == null) {
            redirect("/admin/login");
            return false;
        }
        return true;
    }

    /**
     * 生成URL slug
     */
    private String generateSlug(String title) {
        if (title == null || title.trim().isEmpty()) {
            return "post";
        }

        String slug = title.toLowerCase()
                .replaceAll("[^a-z0-9\\s_]", "") // 只保留字母、数字、空格、下划线
                .replaceAll("\\s+", "_");         // 空格转下划线
        return slug.isEmpty() ? "post" : slug;
    }

    private String convertMarkdownToHtml(String content) {
        try {
            Parser parser = Parser.builder().build();
            HtmlRenderer renderer = HtmlRenderer.builder().build();
            String html = renderer.render(parser.parse(content));
            return html;
        } catch (Exception e) {
            return content;
        }
    }
}
