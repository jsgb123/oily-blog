/* 
    Created on : 2026-5-4
    Author     : oily.top
*/
// Markdown编辑器初始化
function initMarkdownEditor() {
    const textarea = document.getElementById('content');
    if (!textarea) return;
    
    // 工具栏按钮
    const toolbar = [
        { name: 'bold', icon: 'B', action: '**', description: '粗体' },
        { name: 'italic', icon: 'I', action: '*', description: '斜体' },
        { name: 'heading', icon: 'H', action: '# ', description: '标题' },
        { name: 'link', icon: '🔗', action: '[](url)', description: '链接' },
        { name: 'code', icon: '</>', action: '`code`', description: '代码' },
        { name: 'image', icon: '🖼', action: '![](url)', description: '图片' }
    ];
    
    // 创建工具栏
    const toolbarDiv = document.createElement('div');
    toolbarDiv.className = 'markdown-toolbar';
    
    toolbar.forEach(btn => {
        const button = document.createElement('button');
        button.innerHTML = btn.icon;
        button.title = btn.description;
        button.onclick = () => insertMarkdown(btn.action);
        toolbarDiv.appendChild(button);
    });
    
    textarea.parentNode.insertBefore(toolbarDiv, textarea);
    
    function insertMarkdown(text) {
        const start = textarea.selectionStart;
        const end = textarea.selectionEnd;
        const selectedText = textarea.value.substring(start, end);
        const newText = text.replace('text', selectedText);
        
        textarea.value = textarea.value.substring(0, start) + newText + textarea.value.substring(end);
        textarea.focus();
        textarea.selectionStart = start + newText.length;
        textarea.selectionEnd = start + newText.length;
    }
    
    // 实时预览
    const preview = document.createElement('div');
    preview.className = 'markdown-preview';
    preview.innerHTML = '<h3>预览</h3><div class="preview-content"></div>';
    textarea.parentNode.appendChild(preview);
    
    textarea.addEventListener('input', function() {
        const previewContent = preview.querySelector('.preview-content');
        previewContent.innerHTML = marked(this.value);
        // 代码高亮
        if (typeof hljs !== 'undefined') {
            previewContent.querySelectorAll('pre code').forEach((block) => {
                hljs.highlightElement(block);
            });
        }
    });
}

// 文章发布
function publishArticle(event) {
    event.preventDefault();
    
    const formData = new FormData(document.getElementById('articleForm'));
    
    fetch('/admin/doPublish', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert('发布成功！');
            window.location.href = '/admin/index';
        } else {
            alert('发布失败！');
        }
    });
}

// 删除确认
function deleteArticle(id) {
    if (confirm('确定要删除这篇文章吗？')) {
        window.location.href = '/admin/delete/' + id;
    }
}