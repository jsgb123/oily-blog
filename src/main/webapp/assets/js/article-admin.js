// 统一处理工具栏点击
document.querySelector('.toolbar').addEventListener('click', function (e) {
    if (e.target.tagName === 'BUTTON') {
        let syntax = e.target.getAttribute('data-markdown');
        if (syntax) {
            insertMarkdown(syntax);
        }
    }
});
// 标签管理
let tags = [];
const tagContainer = document.getElementById('tagContainer');
const tagInput = document.getElementById('tagInput');
const tagsHidden = document.getElementById('tags');
// 初始化已有标签 
if (tagsHidden.value && tagsHidden.value.trim() !== '') {
    tags = tagsHidden.value.split(',').map(t => t.trim()).filter(t => t);
    console.log('初始化已有标签');
}

tagInput.addEventListener('keydown', function (e) {
    if (e.key === 'Enter' || e.key === ',') {
        e.preventDefault();
        let tag = this.value.trim();
        if (tag && !tags.includes(tag)) {
            tags.push(tag);
            updateTagDisplay();
        }
        this.value = '';
    }
});

function updateTagDisplay() {
    tagContainer.innerHTML = '';
    tags.forEach((tag, index) => {
        const tagSpan = document.createElement('span');
        tagSpan.className = 'tag';
        tagSpan.innerHTML = `${tag} <span class="remove" onclick="removeTag(${index})">×</span>`;
        tagContainer.appendChild(tagSpan);
    });
    tagContainer.appendChild(tagInput);
    tagsHidden.value = tags.join(',');
}

updateTagDisplay();

function removeTag(index) {
    tags.splice(index, 1);
    updateTagDisplay();
}

// Markdown预览
let contentTextarea = document.getElementById('content');
let previewDiv = document.getElementById('preview');

function updatePreview() {
    let markdown = contentTextarea.value;
    if (typeof marked !== 'undefined') {
        let html = marked.parse(markdown);
        previewDiv.innerHTML = html;
        if (typeof hljs !== 'undefined') {
            document.querySelectorAll('#preview pre code').forEach((block) => {
                hljs.highlightElement(block);
            });
        }
    }
}

contentTextarea.addEventListener('input', updatePreview);

// 插入Markdown语法
function insertMarkdown(syntax) {
    let start = contentTextarea.selectionStart;
    let end = contentTextarea.selectionEnd;
    let text = contentTextarea.value;
    let selectedText = text.substring(start, end);

    // 核心逻辑：如果用户选中了文字，就用选中的文字替换模板里的“文本/代码/url”
    // 如果没有选中文字，就保留模板里的默认提示词（如“文本”、“链接”）
    let insertText = syntax
            .replace('文本', selectedText || '文本')
            .replace('代码', selectedText || '代码内容')
            .replace('链接', selectedText || '链接描述')
            .replace('url', selectedText || 'url');

    let newText = text.substring(0, start) + insertText + text.substring(end);
    contentTextarea.value = newText;

    let newCursorPos = start + insertText.length;
    contentTextarea.setSelectionRange(newCursorPos, newCursorPos);
    contentTextarea.focus();
    updatePreview();
}