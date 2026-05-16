package oily.top.test;

import com.jfinal.plugin.activerecord.Db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 读取正确
 *
 * @author 奥利顶<oily.top>
 */
public class H2ReadTest {

    public static void main(String[] args) {
        String url = "jdbc:h2:C:/db/oily_blog_h2;MODE=MySQL;AUTO_SERVER=TRUE";
        String user = "sa";
        String password = "";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection(url, user, password);
            stmt = conn.createStatement();

            // 查询文章（替换为实际的文章ID或slug）
            rs = stmt.executeQuery("SELECT ID, TITLE, CONTENT_HTML FROM ARTICLE WHERE ID = 3");

            while (rs.next()) {
                String html = rs.getString("CONTENT_HTML");
                System.out.println("长度：" + html.length());
                System.out.println("内容：" + html);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
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
