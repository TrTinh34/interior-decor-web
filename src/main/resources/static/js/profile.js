document.addEventListener("DOMContentLoaded", function () {
    const menuItems = document.querySelectorAll(".menu-item[data-tab]");
    const tabPanels = document.querySelectorAll(".tab-panel");

    // Hàm kích hoạt hiển thị tab dựa trên tên truyền vào
    function switchTab(tabName) {
        menuItems.forEach(i => i.classList.remove("active"));
        tabPanels.forEach(p => p.classList.remove("active"));

        const activeMenu = document.querySelector(`.menu-item[data-tab="${tabName}"]`);
        const activePanel = document.getElementById(`tab-${tabName}`);

        if (activeMenu && activePanel) {
            activeMenu.classList.add("active");
            activePanel.classList.add("active");
        }
    }

    // Kiểm tra xem trên thanh địa chỉ URL có yêu cầu mở riêng tab nào không (ví dụ: ?tab=password)
    const urlParams = new URLSearchParams(window.location.search);
    const tabParam = urlParams.get('tab');
    if (tabParam) {
        switchTab(tabParam);
    }

    // Lắng nghe sự kiện click chọn menu của người dùng
    menuItems.forEach(item => {
        item.addEventListener("click", function () {
            const tabName = this.getAttribute("data-tab");
            switchTab(tabName);
        });
    });
});