package oily.top.test;

import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
/**
 * 
 * @author 奥利顶<oily.top>
 */
public class DruidTest {
    
    public static void main(String[] args) {
        DruidDataSource ds = null;
        try {
            // 创建Druid数据源
            ds = new DruidDataSource();
            ds.setUrl("jdbc:h2:~/oily_blog_db;MODE=MySQL;DB_CLOSE_DELAY=-1;FILE_LOCK=NO");
            ds.setUsername("sa");
            ds.setPassword("");
            ds.setInitialSize(1);
            ds.setMinIdle(1);
            ds.setMaxActive(2);
            
            System.out.println("Druid数据源初始化成功");
            
            // 获取连接
            Connection conn = ds.getConnection();
            System.out.println("获取连接成功");
            
            // 执行SQL
            Statement stmt = conn.createStatement();
            
            // 创建表
            try {
                stmt.execute("CREATE TABLE test_druid (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "name VARCHAR(100), " +
                            "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
                System.out.println("创建表成功");
            } catch (Exception e) {
                System.out.println("表已存在或创建失败：" + e.getMessage());
            }
            
            // 插入数据
            stmt.executeUpdate("INSERT INTO test_druid (name) VALUES ('Druid测试数据')");
            System.out.println("插入数据成功");
            
            // 查询数据
            ResultSet rs = stmt.executeQuery("SELECT * FROM test_druid");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name"));
            }
            rs.close();
            stmt.close();
            conn.close();
            
            System.out.println("Druid测试成功！");
            
        } catch (Exception e) {
            System.err.println("Druid测试失败：" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (ds != null) {
                ds.close();
            }
        }
    }
}