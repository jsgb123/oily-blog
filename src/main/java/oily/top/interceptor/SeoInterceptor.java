package oily.top.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import oily.top.model.Article;
import oily.top.model.Category;

/**
 * 经过控制器再到这里
 * @author 奥利顶<oily.top>
 */
public class SeoInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation inv) {
        Controller c = inv.getController();

        // 设置全局SEO变量
        c.setAttr("siteName", "奥利顶 Oily Blog");
        c.setAttr("siteDescription", "奥利顶 Oily Blog - 分享编程技术、游戏攻防、游戏逆向、反外挂研究的个人博客");
        c.setAttr("siteUrl", "http://www.oily.top");

        inv.invoke();

        // 根据不同的action设置SEO信息
        String actionKey = inv.getActionKey();
        if (actionKey.equals("/")) {
            c.setAttr("seo_title", "奥利顶 Oily Blog - 编程技术 | 游戏逆向 | 反外挂 | 游戏防护 | 游戏引擎");
            c.setAttr("seo_keywords", "技术博客,Java,Python,Rust,游戏逆向,前端开发,全栈编程");
            c.setAttr("seo_description", "奥利顶 Oily Blog - 分享编程技术、游戏攻防、游戏逆向、反外挂研究的个人博客");
        } else if (actionKey.equals("/article") && c.getAttr("article") != null) {
            Article article = c.getAttr("article");
            c.setAttr("seo_title", article.getStr("TITLE") + " - 奥利顶 Oily Blog");
            c.setAttr("seo_keywords", article.getStr("TAGS"));
            c.setAttr("seo_description", article.getStr("SUMMARY"));

        } else if (actionKey.equals("/category") && c.getAttr("category") != null) {
            Category category = c.getAttr("category");
            c.setAttr("seo_title", category.getStr("NAME") + " - 奥利顶 Oily Blog");
            c.setAttr("seo_keywords", category.getStr("NAME"));
            c.setAttr("seo_description", category.getStr("DESCRIPTION"));

        }
                c.setAttr("ctx", c.getRequest().getContextPath());// 项目名=/oily-blog，ROOT=/
    }
}
