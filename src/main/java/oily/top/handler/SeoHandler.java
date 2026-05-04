package oily.top.handler;

import com.jfinal.handler.Handler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * SEO处理器 - 处理静态资源和SEO优化
 * 这里执行后到路由configRoute(Routes me)
 * @author 奥利顶<oily.top>
 */
public class SeoHandler extends Handler {
    
    @Override
    public void handle(String target, HttpServletRequest request, 
                       HttpServletResponse response, boolean[] isHandled) {
        
        // 处理静态资源
        if (target.startsWith("/assets/") || 
            target.startsWith("/uploads/") ||
            target.endsWith(".css") || 
            target.endsWith(".js") || 
            target.endsWith(".png") || 
            target.endsWith(".jpg") || 
            target.endsWith(".gif") ||
            target.endsWith(".ico")) {
            return;
        }
        
        // 处理sitemap
        if (target.equals("/sitemap") || target.equals("/sitemap.xml")) {
            request.setAttribute("target", "/sitemap");
            next.handle(target, request, response, isHandled);
            return;
        }
        
        // 处理robots.txt
        if (target.equals("/robots.txt")) {
            request.setAttribute("target", "/robots");
            next.handle(target, request, response, isHandled);
            return;
        }
        
        // 处理分类路由 /c/tech
        if (target.startsWith("/c/")) {
            String categorySlug = target.substring(3);
            request.setAttribute("target", "/article/category");
            request.setAttribute("slug", categorySlug);
            System.out.println("oily.top.handler.SeoHandler.handle()"+categorySlug);
            next.handle("/article/category", request, response, isHandled);

            return;
        }
        
        // 处理标签路由 /t/java
        if (target.startsWith("/t/")) {
            String tag = target.substring(3);
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