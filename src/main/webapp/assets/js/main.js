/* 
    Created on : 2026-5-4
    Author     : www.oily.top
*/
// 导航栏滚动效果
window.addEventListener('scroll', function() {
    var header = document.querySelector('.header');
    if (window.scrollY > 0) {
        header.style.boxShadow = '0 2px 8px rgba(0,0,0,0.15)';
    } else {
        header.style.boxShadow = '0 2px 4px rgba(0,0,0,0.1)';
    }
});