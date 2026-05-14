package oily.top.common;

import oily.top.controller.*;
import oily.top.handler.SeoHandler;
import oily.top.interceptor.SeoInterceptor;
import oily.top.model.*;
import com.jfinal.config.*;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.render.ViewType;
import com.jfinal.template.Engine;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * 
 * @author 奥利顶<oily.top>
 */
public class AppConfig extends JFinalConfig {

    @Override
    public void configConstant(Constants me) {
        PropKit.use("config.txt", "UTF-8");
        me.setDevMode(PropKit.getBoolean("devMode", true));
        me.setViewType(ViewType.JFINAL_TEMPLATE);
        me.setEncoding("UTF-8");
        me.setError404View("/view/404.html");
        me.setError500View("/view/500.html");
    }

    @Override
    public void configRoute(Routes me) {
        me.add("/", IndexController.class, "view");
        me.add("/admin", AdminController.class, "view");
        me.add("/article", ArticleController.class, "view");
    }

    @Override
    public void configEngine(Engine me) {
        me.addSharedFunction("/view/common/header.html");
        me.addSharedFunction("/view/common/footer.html");
        me.setDevMode(PropKit.getBoolean("devMode", true));
    }

    @Override
    public void configPlugin(Plugins me) {
        String tcpUrl = PropKit.get("jdbcUrl");
        String embeddedUrl = PropKit.get("embeddedUrl");
        String user = PropKit.get("user");
        String password = PropKit.get("password");

        // 先创建表
        createTablesByFile(embeddedUrl, user, password);

        // 配置Druid数据源
        DruidPlugin dp = new DruidPlugin(embeddedUrl, user, password);
        dp.setInitialSize(1);
        dp.setMinIdle(1);
        dp.setMaxActive(5);
        me.add(dp);

        // 注册模型
        ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
        arp.setShowSql(PropKit.getBoolean("devMode", true));
        arp.addMapping("article", Article.class);
        arp.addMapping("category", Category.class);
        arp.addMapping("user", User.class);
        me.add(arp);

    }

    private void createTables(String jdbcUrl, String user, String pw) {
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection(jdbcUrl, user, pw);
            stmt = conn.createStatement();

            // 检查表是否存在
            try {
                stmt.executeQuery("SELECT COUNT(*) FROM article");
                System.out.println("数据库表已存在");
                return;
            } catch (Exception e) {
                System.out.println("开始创建数据库表...");
            }

            // 创建用户表
            stmt.execute("CREATE TABLE IF NOT EXISTS user ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "username VARCHAR(50) NOT NULL UNIQUE, "
                    + "password VARCHAR(255) NOT NULL, "
                    + "nickname VARCHAR(50), "
                    + "email VARCHAR(100), "
                    + "role VARCHAR(20) DEFAULT 'admin', "
                    + "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            // 创建分类表
            stmt.execute("CREATE TABLE IF NOT EXISTS category ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "name VARCHAR(50) NOT NULL, "
                    + "slug VARCHAR(50) NOT NULL UNIQUE, "
                    + "description VARCHAR(200), "
                    + "sort INT DEFAULT 0, "
                    + "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            // 创建文章表
            stmt.execute("CREATE TABLE IF NOT EXISTS article ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "title VARCHAR(200) NOT NULL, "
                    + "slug VARCHAR(200) NOT NULL UNIQUE, "
                    + "summary VARCHAR(500), "
                    + "content TEXT NOT NULL, "
                    + "content_html TEXT, "
                    + "category_id INT, "
                    + "tags VARCHAR(500), "
                    + "view_count INT DEFAULT 0, "
                    + "status TINYINT DEFAULT 1, "
                    + "is_top TINYINT DEFAULT 0, "
                    + "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            // 插入默认管理员(密码admin123)
            stmt.execute("INSERT INTO user (username, password, nickname, email, role) "
                    + "SELECT 'admin', '0192023a7bbd73250516f069df18b500', '博主', 'admin@oily.top', 'admin' "
                    + "WHERE NOT EXISTS (SELECT 1 FROM user WHERE username = 'admin')");

            // 插入默认分类
            stmt.execute("INSERT INTO category (id, name, slug, description, sort) "
                    + "SELECT 1, '技术分享', 'tech', 'Java、Python、前端等技术文章', 1 "
                    + "WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 1)");

            stmt.execute("INSERT INTO category (id, name, slug, description, sort) "
                    + "SELECT 2, '生活感悟', 'life', '生活随笔、感悟分享', 2 "
                    + "WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 2)");

            stmt.execute("INSERT INTO category (id, name, slug, description, sort) "
                    + "SELECT 3, '读书笔记', 'reading', '读书心得、好书推荐', 3 "
                    + "WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 3)");

            stmt.execute("INSERT INTO category (id, name, slug, description, sort) "
                    + "SELECT 4, '工具推荐', 'tools', '开发工具、效率软件推荐', 4 "
                    + "WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 4)");

            System.out.println("数据库初始化成功！");

        } catch (Exception e) {
            System.err.println("创建表失败：" + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (Exception e) {
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
            }
        }
    }

    private void createTablesByFile(String jdbcUrl, String user, String password) {
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection(jdbcUrl, user, password);
            stmt = conn.createStatement();

            // 检查表是否存在
            try {
                stmt.executeQuery("SELECT COUNT(*) FROM article");
                System.out.println("数据库表已存在");
                return;
            } catch (Exception e) {
                System.out.println("开始创建数据库表...");
            }

            // 读取并执行 h2.sql 文件
            String sql = readSqlFile("/h2.sql");
            if (sql == null || sql.isEmpty()) {
                System.err.println("SQL文件为空");
                return;
            }

            // 按行分割执行
            String[] lines = sql.split("\n");
            StringBuilder currentSql = new StringBuilder();

            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                    continue;
                }
                currentSql.append(line).append(" ");
                if (trimmed.endsWith(";")) {
                    String sqlStatement = currentSql.toString();
                    sqlStatement = sqlStatement.substring(0, sqlStatement.length() - 1); // 去掉分号
                    try {
                        stmt.execute(sqlStatement);
                        System.out.println("执行成功: " + sqlStatement.substring(0, Math.min(50, sqlStatement.length())));
                    } catch (Exception ex) {
                        System.err.println("执行失败: " + ex.getMessage());
                    }
                    currentSql.setLength(0);
                }
            }

            System.out.println("数据库初始化成功！");

        } catch (Exception e) {
            System.err.println("创建表失败：" + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (Exception e) {
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
            }
        }
    }

    private String readSqlFile(String path) {
        StringBuilder sb = new StringBuilder();
        try (InputStream is = getClass().getResourceAsStream(path);
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            System.err.println("读取SQL文件失败：" + e.getMessage());
            return null;
        }
    }

    @Override
    public void configInterceptor(Interceptors me) {
        me.add(new SeoInterceptor());
    }

    @Override
    public void configHandler(Handlers me) {
        me.add(new SeoHandler());
    }
}
