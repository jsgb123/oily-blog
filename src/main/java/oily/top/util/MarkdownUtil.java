package oily.top.util;

import java.util.Arrays;
import java.util.List;
import org.commonmark.Extension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.ext.gfm.tables.TablesExtension;

/**
 *
 * @author 奥利顶<oily.top>
 */
public class MarkdownUtil {

// 配置 CommonMark 解析器，支持表格等扩展
    private static final List<Extension> EXTENSIONS = Arrays.asList(TablesExtension.create());
    private static final Parser PARSER = Parser.builder().extensions(EXTENSIONS).build();
    private static final HtmlRenderer RENDERER = HtmlRenderer.builder().extensions(EXTENSIONS).build();

    /**
     * 将 Markdown 转换为 HTML
     *
     * @param markdown Markdown 文本
     * @return HTML 文本
     */
    public static String toHtml(String markdown) {
        if (markdown == null || markdown.trim().isEmpty()) {
            return "";
        }
        try {

            String html = RENDERER.render(PARSER.parse(markdown));
            return html;
        } catch (Exception e) {
            return "Markdown转Html格式转换错误";
        }
    }

    /**
     * 添加处理图片路径
     *
     * @param html
     * @param contextPath
     * @return
     */
    public static String processImagePath(String html, String contextPath) {
        if (html == null || html.trim().isEmpty()) {
            return html;
        }

        if (contextPath == null || contextPath.isEmpty() || "/".equals(contextPath)) {
            return html;
        }

        // 匹配 img 标签中的 src 属性，且路径以 /uploads/ 开头
        // 使用 Pattern 和 Matcher 进行精确匹配
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                "(<img\\s+[^>]*src=\")(/uploads/[^\"]+)\"",
                java.util.regex.Pattern.CASE_INSENSITIVE
        );

        java.util.regex.Matcher matcher = pattern.matcher(html);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String replacement = matcher.group(1) + contextPath + matcher.group(2) + "\"";
            matcher.appendReplacement(sb, java.util.regex.Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);

        return sb.toString();
    }
}
