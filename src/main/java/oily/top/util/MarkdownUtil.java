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
}
