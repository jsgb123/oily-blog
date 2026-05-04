# Oily Blog 开源公告

> 一个轻量级、SEO友好的Java博客系统

## 项目简介

Oily Blog 是一款轻量级、SEO友好的Java博客系统。它基于 JFinal 4.9 框架和 H2 嵌入式数据库构建，追求简洁、高效、易部署的理念。无论是技术博主、独立开发者，还是想搭建个人网站的学习者，Oily Blog 都能帮助你快速上线一个功能完整的博客站点。零配置、开箱即用，无需安装繁琐的数据库环境。

## 开源地址

- GitHub：[https://github.com/jsgb123/oily-blog](https://github.com/jsgb123/oily-blog)
- 演示站：[http://www.oily.top](http://www.oily.top)

## 后台管理

| 项目 | 信息 |
|------|------|
| 后台地址 | `/admin/login` |
| 管理员账号 | `admin` |
| 管理员密码 | `admin123` |

> 首次登录后请及时修改密码！

## 主要功能

### 文章管理
- ✍️ **Markdown编辑器** - 支持实时预览、工具栏快捷操作
- 📝 **文章发布** - 支持标题、分类、标签、摘要、置顶
- 🔄 **草稿功能** - 支持保存为草稿，定时发布
- 📊 **文章统计** - 浏览量统计、热门文章排行

### 分类管理
- 📂 **自定义分类** - 支持增删改查、排序
- 🔗 **SEO友好URL** - 支持自定义slug

### SEO优化
- 🎯 **语义化URL** - 文章链接使用自定义slug
- 📝 **Meta标签** - 支持自定义标题、关键词、描述
- 🗺️ **站点地图** - 自动生成sitemap.xml
- 🤖 **robots.txt** - 搜索引擎爬虫配置

### 系统特色
- 🚀 **轻量快速** - 基于JFinal框架，启动迅速
- 💾 **零配置** - 内置H2数据库，开箱即用
- 📱 **响应式设计** - 完美支持PC、平板、手机
- 🔒 **安全机制** - MD5密码加密，防SQL注入

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| JFinal | 4.9.20 | MVC框架 |
| H2 | 1.4.200 | 嵌入式数据库 |
| Druid | 1.2.16 | 数据库连接池 |
| Marked | 11.1.1 | Markdown解析 |
| Highlight.js | 11.9.0 | 代码高亮 |

## 快速部署

### 环境要求
- JDK 1.8+
- Tomcat 8/9
- Maven 3.5+

### 部署步骤

```bash
# 1. 克隆项目
git clone https://github.com/jsgb123/oily-blog.git

# 2. 进入项目目录
cd oily-blog

# 3. Maven打包
mvn clean package

# 4. 将target/oily-blog.war复制到Tomcat的webapps目录
cp target/oily-blog.war /path/to/tomcat/webapps/

# 5. 启动Tomcat
/path/to/tomcat/bin/startup.sh

# 6. 访问
# 前台：http://localhost:8080/oily-blog
# 后台：http://localhost:8080/oily-blog/admin/login
```

### Docker部署（即将支持）

```bash
docker run -d -p 8080:8080 oily-top/oily-blog:latest
```

## 项目结构

```
oily-blog/
├── src/main/java/oily/top/
│   ├── common/          # 公共配置
│   ├── controller/      # 控制器
│   ├── model/           # 数据模型
│   ├── service/         # 业务逻辑
│   └── handler/         # 拦截器
├── src/main/webapp/
│   ├── assets/          # 静态资源
│   ├── view/            # 模板文件
│   └── WEB-INF/         # web配置
└── pom.xml
```

## 数据库配置

数据库文件自动生成在 `C:/db/oily_blog_h2.mv.db`，如需修改，编辑 `src/main/resources/config.txt`：

```properties
jdbcUrl=jdbc:h2:C:/db/oily_blog_h2;MODE=MySQL;AUTO_SERVER=TRUE
user=sa
password=
```

## 开源协议

本项目采用 **Apache License 2.0** 协议，欢迎自由使用和二次开发。

## 致谢

- [JFinal](https://gitee.com/jfinal/jfinal) - 极简Java MVC框架
- [Marked](https://marked.js.org/) - Markdown解析器
- [Highlight.js](https://highlightjs.org/) - 代码高亮库

## 联系我们
- 微信公众号【会编码】
<img width="125" height="125" alt="qrcode_for_bytecode" src="https://github.com/user-attachments/assets/512f3798-ebfb-48c2-8c0d-5bf7575e55ea" />

- 作者：奥利顶
- 官网：[http://www.oily.top](http://www.oily.top)
- GitHub：[https://github.com/jsgb123/oily-blog](https://github.com/jsgb123/oily-blog)

## 后续计划

- [ ] 评论系统
- [ ] 友情链接
- [ ] 文章搜索优化
- [ ] 主题切换
- [ ] Docker镜像
- [ ] 更完善的API接口

---

**如果觉得不错，欢迎 Star ⭐ 支持一下！**
