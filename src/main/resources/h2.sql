-- 1. 创建用户表
CREATE TABLE IF NOT EXISTS user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(50),
    email VARCHAR(100),
    role VARCHAR(20) DEFAULT 'admin',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. 创建分类表
CREATE TABLE IF NOT EXISTS category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    slug VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200),
    sort INT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. 创建文章表
CREATE TABLE IF NOT EXISTS article (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    slug VARCHAR(200) NOT NULL UNIQUE,
    summary VARCHAR(500),
    content TEXT NOT NULL,
    content_html TEXT,
    category_id INT,
    tags VARCHAR(500),
    view_count INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    is_top TINYINT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. 插入默认管理员 (密码: admin123)
INSERT INTO user (username, password, nickname, email, role) 
SELECT 'admin', '0192023a7bbd73250516f069df18b500', '博主', 'admin@oily.top', 'admin'
WHERE NOT EXISTS (SELECT 1 FROM user WHERE username = 'admin');

-- 5. 插入默认分类
INSERT INTO category (id, name, slug, description, sort) 
SELECT 1, '技术分享', 'tech', 'Java、Python、Rust、QT、前端开发', 1
WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 1);

INSERT INTO category (id, name, slug, description, sort) 
SELECT 2, '游戏逆向', 'gamerev', '游戏防护、逆向工程、反外挂、汇编分析、内存修改', 2
WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 2);

INSERT INTO category (id, name, slug, description, sort) 
SELECT 3, '游戏编程', 'gamedev', '游戏开发、Unity、Unreal、Cocos、游戏引擎', 3
WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 3);

INSERT INTO category (id, name, slug, description, sort) 
SELECT 4, '工具推荐', 'tools', '开发工具、效率软件、开源项目', 4
WHERE NOT EXISTS (SELECT 1 FROM category WHERE id = 4);


INSERT INTO article (title, slug, summary, content, content_html, category_id, tags, status, is_top, create_time) 
SELECT 
    'Oily Blog 开源公告',
    'OilyBlog',
    'Oily Blog 是一款基于 JFinal 4.9 + H2 数据库开发的轻量级个人博客系统，专为技术爱好者和独立开发者设计。',
    '',
    '<h1 id="oily-blog-开源公告">Oily Blog 开源公告</h1>
<blockquote>
<p>一个轻量级、SEO友好的Java博客系统</p>
</blockquote>
<h2 id="项目简介">项目简介</h2>
<p>Oily Blog 是一款基于 JFinal 4.9 + H2 数据库开发的轻量级个人博客系统。它专为技术爱好者和独立开发者设计，追求简洁、高效、易部署的理念。</p>
<h2 id="开源地址">开源地址</h2>
<ul>
<li>GitHub：<a href="https://github.com/jsgb123/oily-blog">https://github.com/jsgb123/oily-blog</a></li>
<li>演示站：<a href="http://www.oily.top">http://www.oily.top</a></li>
</ul>
<h2 id="后台管理">后台管理</h2>
<table>
<thead>
<tr class="header">
<th>项目</th>
<th>信息</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td>后台地址</td>
<td>&nbsp;&nbsp;<code>/admin/login</code></td>
</tr>
<tr class="even">
<td>管理员账号</td>
<td>&nbsp;&nbsp;<code>admin</code></td>
</tr>
<tr class="odd">
<td>管理员密码</td>
<td>&nbsp;&nbsp;<code>admin123</code></td>
</tr>
</tbody>
</table>
<blockquote>
<p>首次登录后请及时修改密码！</p>
</blockquote>
<h2 id="主要功能">主要功能</h2>
<h3 id="文章管理">文章管理</h3>
<ul>
<li>✍️ <strong>Markdown编辑器</strong> - 支持实时预览、工具栏快捷操作</li>
<li>📝 <strong>文章发布</strong> - 支持标题、分类、标签、摘要、置顶</li>
<li>🔄 <strong>草稿功能</strong> - 支持保存为草稿，定时发布</li>
<li>📊 <strong>文章统计</strong> - 浏览量统计、热门文章排行</li>
</ul>
<h3 id="分类管理">分类管理</h3>
<ul>
<li>📂 <strong>自定义分类</strong> - 支持增删改查、排序</li>
<li>🔗 <strong>SEO友好URL</strong> - 支持自定义slug</li>
</ul>
<h3 id="seo优化">SEO优化</h3>
<ul>
<li>🎯 <strong>语义化URL</strong> - 文章链接使用自定义slug</li>
<li>📝 <strong>Meta标签</strong> - 支持自定义标题、关键词、描述</li>
<li>🗺️ <strong>站点地图</strong> - 自动生成sitemap.xml</li>
<li>🤖 <strong>robots.txt</strong> - 搜索引擎爬虫配置</li>
</ul>
<h3 id="系统特色">系统特色</h3>
<ul>
<li>🚀 <strong>轻量快速</strong> - 基于JFinal框架，启动迅速</li>
<li>💾 <strong>零配置</strong> - 内置H2数据库，开箱即用</li>
<li>📱 <strong>响应式设计</strong> - 完美支持PC、平板、手机</li>
<li>🔒 <strong>安全机制</strong> - MD5密码加密，防SQL注入</li>
</ul>
<h2 id="技术栈">技术栈</h2>
<table>
<thead>
<tr class="header">
<th>技术</th>
<th>版本</th>
<th>说明</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td>JFinal</td>
<td>&nbsp;4.9.20</td>
<td>&nbsp;&nbsp;MVC框架</td>
</tr>
<tr class="even">
<td>H2</td>
<td>&nbsp;1.4.200</td>
<td>&nbsp;&nbsp;嵌入式数据库</td>
</tr>
<tr class="odd">
<td>Druid</td>
<td>&nbsp;1.2.16</td>
<td>&nbsp;&nbsp;数据库连接池</td>
</tr>
<tr class="even">
<td>Marked</td>
<td>&nbsp;11.1.1</td>
<td>&nbsp;&nbsp;Markdown解析</td>
</tr>
<tr class="odd">
<td>Highlight.js</td>
<td>&nbsp;11.9.0</td>
<td>&nbsp;&nbsp;代码高亮</td>
</tr>
</tbody>
</table>
<h2 id="快速部署">快速部署</h2>
<h3 id="环境要求">环境要求</h3>
<ul>
<li>JDK 1.8+</li>
<li>Tomcat 8/9</li>
<li>Maven 3.5+</li>
</ul>

前台：http://localhost:8080/oily-blog
<p>
后台：http://localhost:8080/oily-blog/admin/login

<h2 id="数据库配置">数据库配置</h2>
<p>数据库文件自动生成在 <code>C:/db/oily_blog_h2.mv.db</code>，如需修改，编辑 <code>src/main/resources/config.txt</code>：</p>
<code>jdbcUrl=jdbc:h2:C:/db/oily_blog_h2;MODE=MySQL;AUTO_SERVER=TRUE</code><br>
<code>user=sa</code><br>
<code>password=</code><br>
<h2 id="开源协议">开源协议</h2>
<p>本项目采用 <strong>Apache License 2.0</strong> 协议，欢迎自由使用和二次开发。</p>
<h2 id="致谢">致谢</h2>
<ul>
<li><a href="https://gitee.com/jfinal/jfinal">JFinal</a> - 极简Java MVC框架</li>
<li><a href="https://marked.js.org/">Marked</a> - Markdown解析器</li>
<li><a href="https://highlightjs.org/">Highlight.js</a> - 代码高亮库</li>
</ul>
<h2 id="联系我们">联系我们</h2>
<ul>
<li>作者：奥利顶</li>
<li>官网：<a href="http://www.oily.top">http://www.oily.top</a></li>
<li>GitHub：<a href="https://github.com/jsgb123/oily-blog">https://github.com/jsgb123/oily-blog</a></li>
</ul>
<h2 id="后续计划">后续计划</h2>
<ul>
<li>[ ] 评论系统</li>
<li>[ ] 友情链接</li>
<li>[ ] 文章搜索优化</li>
<li>[ ] 主题切换</li>
<li>[ ] Docker镜像</li>
<li>[ ] 更完善的API接口</li>
</ul>
<hr />
<p><strong>如果觉得不错，欢迎关注页面底部微信公众号支持一下！</strong></p>',
    4, 'OilyBlog', 1, 1, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM article WHERE slug = 'OilyBlog');
