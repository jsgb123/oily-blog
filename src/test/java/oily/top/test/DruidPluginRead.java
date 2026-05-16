package oily.top.test;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.druid.DruidPlugin;
import java.sql.SQLException;
import oily.top.model.Article;

/**
 *
 * @author 奥利顶<oily.top>
 */
public class DruidPluginRead {

    public static void main(String[] args) throws SQLException {
        // 1. 加载配置
        String jdbcUrl = "jdbc:h2:C:/db/oily_blog_h2;MODE=MySQL;AUTO_SERVER=TRUE";
        String user = "sa";
        String password = "";

        // 2. 初始化 Druid 连接池
        DruidPlugin dp = new DruidPlugin(jdbcUrl, user, password);
        dp.start();

        // 3. 初始化 ActiveRecord
        ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
        arp.addMapping("article", Article.class);
        arp.start();

        // 4. 查询
        String sql = "SELECT CONTENT_HTML FROM ARTICLE WHERE ID = 3";
        Record article = Db.findFirst(sql);
        String html = article.getStr("CONTENT_HTML");
        System.out.println("长度："+html.length());
        System.out.println("内容："+html);
        // 5. 关闭连接
        arp.stop();
        dp.stop();
    }

}
