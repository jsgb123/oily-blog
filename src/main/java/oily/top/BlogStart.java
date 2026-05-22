package oily.top;

import com.jfinal.core.JFinal;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;

public class BlogStart {

    // 或者"/oily-blog"
    private static final String SITE = "/";

    /**
     * 只能访问项目 webapp 目录下的资源，无法访问外部目录。
     */
    static void start1() {
        JFinal.start("src/main/webapp", 8080, "/", 5);
        System.out.println("========================================");
        System.out.println("Oily Blog 启动成功！");
        System.out.println("访问地址：http://localhost:8080");
        System.out.println("后台地址：http://localhost:8080/admin/login");
        System.out.println("========================================");
    }

    public static void main(String[] args) throws Exception {
        start2();
    }

    static void start2() throws Exception {

        Server server = new Server(8080);

        // 配置外部图片目录的静态资源处理器
        String externalUploads = "C:/db/oilyblog/uploads";
        File uploadsDir = new File(externalUploads);
        if (!uploadsDir.exists()) {
            uploadsDir.mkdirs();
        }

        // 创建 ResourceHandler 专门处理外部图片
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(false); // 禁止列出目录
        resourceHandler.setResourceBase(externalUploads); // 指向外部物理路径

        // 创建 ContextHandler 绑定到 /uploads 路径
        ContextHandler uploadsContext = new ContextHandler(SITE.length() > 1 ? SITE + "/uploads" : "/uploads");
        uploadsContext.setHandler(resourceHandler);

        // 创建 WebAppContext 处理动态请求 (JFinal 等)
        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath(SITE);
        // 这里只需要保留项目本身的 webapp 目录即可
        webapp.setResourceBase("src/main/webapp");
        webapp.setInitParameter("scanIntervalSeconds", "5");

        // 使用 HandlerList 组合处理器 (注意顺序：先静态，后动态)
        HandlerList handlers = new HandlerList();
        handlers.addHandler(uploadsContext); // 优先处理
        handlers.addHandler(webapp);         // 剩下的交给 WebAppContext 处理

        server.setHandler(handlers);

        // 启动服务器
        server.start();
        System.out.println("Oily Blog 启动成功！");
        server.join();
    }
}
