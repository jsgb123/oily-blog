package oily.top.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
/**
 * 
 * @author 奥利顶<oily.top>
 */
public class H2DatabaseTest {

    public static void main(String[] args) {
        testH2SqlFile();
//testH2Sql();
    }

public static void testH2SqlFile() {
    System.out.println("========== 测试 h2.sql 文件 ==========");
    
    // 先打印文件内容
    String sqlContent = readSqlFile("/h2.sql");
    System.out.println("========== h2.sql 文件内容 ==========");
    System.out.println(sqlContent);
    System.out.println("=====================================");
    
    Connection conn = null;
    Statement stmt = null;
    try {
        Class.forName("org.h2.Driver");
        String url = "jdbc:h2:C:/db/oily_blog_test;MODE=MySQL;DB_CLOSE_DELAY=-1";
        conn = DriverManager.getConnection(url, "sa", "");
        System.out.println("数据库连接成功！");

        stmt = conn.createStatement();

        if (sqlContent == null || sqlContent.isEmpty()) {
            System.err.println("SQL文件为空");
            return;
        }

        System.out.println("开始执行SQL文件...");
        System.out.println("========================================");

        // 按行分割，而不是按分号
        String[] lines = sqlContent.split("\n");
        StringBuilder currentSql = new StringBuilder();
        int sqlCount = 0;
        
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                continue;
            }
            currentSql.append(line).append(" ");
            if (trimmed.endsWith(";")) {
                sqlCount++;
                String sql = currentSql.toString();
                sql = sql.substring(0, sql.length() - 1); // 去掉分号
                System.out.println("\n执行第 " + sqlCount + " 条SQL:");
                System.out.println("SQL: " + sql.substring(0, Math.min(80, sql.length())));
                try {
                    stmt.execute(sql);
                    System.out.println("✓ 执行成功");
                } catch (Exception ex) {
                    System.err.println("✗ 执行失败: " + ex.getMessage());
                }
                currentSql.setLength(0);
            }
        }

    } catch (Exception e) {
        System.err.println("测试失败：" + e.getMessage());
        e.printStackTrace();
    } finally {
        try { if (stmt != null) stmt.close(); } catch (Exception e) {}
        try { if (conn != null) conn.close(); } catch (Exception e) {}
    }
}

    private static String readSqlFile(String path) {
        StringBuilder sb = new StringBuilder();
        try (InputStream is = H2DatabaseTest.class.getResourceAsStream(path);
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

    public static void testH2Sql() {
        System.out.println("========== 测试 h2.sql 文件 ==========");
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName("org.h2.Driver");
            String url = "jdbc:h2:C:/db/oily_blog_h2;MODE=MySQL;DB_CLOSE_DELAY=-1";
            conn = DriverManager.getConnection(url, "sa", "");
            System.out.println("数据库连接成功！");

            stmt = conn.createStatement();

            // 先手动创建表
            System.out.println("手动创建表...");
            stmt.execute("CREATE TABLE IF NOT EXISTS user ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "username VARCHAR(50) NOT NULL UNIQUE, "
                    + "password VARCHAR(255) NOT NULL, "
                    + "nickname VARCHAR(50), "
                    + "email VARCHAR(100), "
                    + "role VARCHAR(20) DEFAULT 'admin', "
                    + "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            stmt.execute("CREATE TABLE IF NOT EXISTS category ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "name VARCHAR(50) NOT NULL, "
                    + "slug VARCHAR(50) NOT NULL UNIQUE, "
                    + "description VARCHAR(200), "
                    + "sort INT DEFAULT 0, "
                    + "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            stmt.execute("CREATE TABLE IF NOT EXISTS article ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "title VARCHAR(200) NOT NULL, "
                    + "slug VARCHAR(200) NOT NULL UNIQUE, "
                    + "content TEXT NOT NULL, "
                    + "category_id INT, "
                    + "view_count INT DEFAULT 0, "
                    + "status TINYINT DEFAULT 1, "
                    + "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            System.out.println("表创建成功！");

            // 然后插入数据
            stmt.execute("INSERT INTO user (username, password, nickname, email, role) "
                    + "SELECT 'admin', '21232f297a57a5a743894a0e4a801fc3', '博主', 'admin@oily.top', 'admin' "
                    + "WHERE NOT EXISTS (SELECT 1 FROM user WHERE username = 'admin')");

            stmt.execute("INSERT INTO category (id, name, slug, description, sort) "
                    + "SELECT 1, '技术分享', 'tech', 'Java、Python、前端等技术文章', 1 "
                    + "WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 1)");

            System.out.println("数据插入成功！");

            // 验证
            ResultSet rs = stmt.executeQuery("SELECT * FROM user");
            while (rs.next()) {
                System.out.println("用户: " + rs.getString("username"));
            }
            rs.close();

        } catch (Exception e) {
            System.err.println("测试失败：" + e.getMessage());
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
}
