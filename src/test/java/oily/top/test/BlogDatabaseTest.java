package oily.top.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
/**
 * 
 * @author 奥利顶<oily.top>
 */
public class BlogDatabaseTest {
    
    public static void main(String[] args) {
        Connection conn = null;
        try {
            Class.forName("org.h2.Driver");
            
            String url = "jdbc:h2:~/oily_blog_db;MODE=MySQL;DB_CLOSE_DELAY=-1;FILE_LOCK=NO";
            conn = DriverManager.getConnection(url, "sa", "");
            
            System.out.println("数据库连接成功！");
            
            Statement stmt = conn.createStatement();
            
            // 创建用户表
            stmt.execute("CREATE TABLE IF NOT EXISTS user (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "username VARCHAR(50) NOT NULL UNIQUE," +
                "password VARCHAR(255) NOT NULL," +
                "nickname VARCHAR(50)," +
                "email VARCHAR(100)," +
                "role VARCHAR(20) DEFAULT 'admin')");
            System.out.println("用户表创建成功");
            
            // 创建分类表
            stmt.execute("CREATE TABLE IF NOT EXISTS category (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(50) NOT NULL," +
                "slug VARCHAR(50) NOT NULL UNIQUE," +
                "description VARCHAR(200)," +
                "sort INT DEFAULT 0)");
            System.out.println("分类表创建成功");
            
            // 创建文章表
            stmt.execute("CREATE TABLE IF NOT EXISTS article (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "title VARCHAR(200) NOT NULL," +
                "slug VARCHAR(200) NOT NULL UNIQUE," +
                "summary VARCHAR(500)," +
                "content TEXT NOT NULL," +
                "content_html TEXT," +
                "category_id INT," +
                "tags VARCHAR(500)," +
                "view_count INT DEFAULT 0," +
                "status TINYINT DEFAULT 1," +
                "is_top TINYINT DEFAULT 0," +
                "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            System.out.println("文章表创建成功");
            
            // 插入测试数据
            stmt.execute("INSERT INTO user (username, password, nickname, role) VALUES " +
                "('admin', '21232f297a57a5a743894a0e4a801fc3', '博主', 'admin')");
            System.out.println("管理员插入成功");
            
            stmt.execute("INSERT INTO category (id, name, slug, description, sort) VALUES " +
                "(1, '技术分享', 'tech', 'Java、Python、前端等技术文章', 1)");
            System.out.println("分类插入成功");
            
            stmt.execute("INSERT INTO article (title, slug, summary, content, category_id, status) VALUES " +
                "('测试文章', 'test-article', '这是一篇测试文章', '# 测试标题\\n\\n这是测试内容', 1, 1)");
            System.out.println("文章插入成功");
            
            System.out.println("\n所有测试通过！数据库初始化成功！");
            
            stmt.close();
            
        } catch (Exception e) {
            System.err.println("测试失败：" + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }
}