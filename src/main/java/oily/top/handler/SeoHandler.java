package oily.top.handler;

import com.jfinal.handler.Handler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * SEO处理器 - 处理静态资源和SEO优化 这里执行后到路由configRoute(Routes me)
 *
 * @author 奥利顶<oily.top>
 */
public class SeoHandler extends Handler {

    @Override
    public void handle(String target, HttpServletRequest request,
            HttpServletResponse response, boolean[] isHandled) {

        // 处理静态资源
        if (target.startsWith("/assets/")
                || target.startsWith("/uploads/")
                || target.endsWith(".css")
                || target.endsWith(".js")
                || target.endsWith(".png")
                || target.endsWith(".jpg")
                || target.endsWith(".gif")
                || target.endsWith(".ico")) {
            System.out.println("oily.top.handler.SeoHandler.handle()");
            return;
        }
        // 处理 
        if (target.endsWith("/index.html")) {
            next.handle("/index", request, response, isHandled);
            return;
        }
        // 处理 sitemap
        if (target.equals("/sitemap") || target.equals("/sitemap.xml")) {
            System.out.println("oily.top.handler.SeoHandler.handle()" + target);
            next.handle("/sitemap", request, response, isHandled);
            return;
        }

        // 处理 robots.txt
        if (target.equals("/robots.txt")) {
            next.handle("/robots", request, response, isHandled);
            return;
        }

        // 处理分类路由 /c/tech
        if (target.startsWith("/c/")) {
            String categorySlug = target.substring(3);
            request.setAttribute("target", "/article/category");
            request.setAttribute("slug", categorySlug);
            System.out.println("oily.top.handler.SeoHandler.handle()" + categorySlug);
            next.handle("/article/category", request, response, isHandled);

            return;
        }

        // 处理标签路由 /t/java
        if (target.startsWith("/t/")) {
            String tag = target.substring(3);
            // URL 解码中文标签
            if (tag != null && tag.contains("%")) {
                try {
                    tag = java.net.URLDecoder.decode(tag, "UTF-8");
                } catch (Exception e) {
                }
            }
            request.setAttribute("target", "/article/tag");
            request.setAttribute("tag", tag);
            next.handle("/article/tag", request, response, isHandled);
            return;
        }

        // 处理归档路由 /archive
        if (target.startsWith("/archive")) {
            next.handle("/article/archive", request, response, isHandled);
            return;
        }

        // 处理搜索路由 /search
        if (target.startsWith("/search")) {
            next.handle("/article/search", request, response, isHandled);
            return;
        }

        next.handle(target, request, response, isHandled);
    }
}
