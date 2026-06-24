document.addEventListener("DOMContentLoaded", function () {
    const menuItems = document.querySelectorAll(".menu-item[data-tab]");
    const tabPanels = document.querySelectorAll(".tab-panel");

    /**
     * 1. HÀM CHUYỂN ĐỔI QUA LẠI GIỮA CÁC TAB TRÊN GIAO DIỆN
     */
    function switchTab(tabName) {
        // Gỡ bỏ trạng thái hoạt động (active) cũ
        menuItems.forEach(i => i.classList.remove("active"));
        tabPanels.forEach(p => p.classList.remove("active"));

        // Tìm menu và bảng nội dung tương ứng theo tabName
        const activeMenu = document.querySelector(`.menu-item[data-tab="${tabName}"]`);
        const activePanel = document.getElementById(`tab-${tabName}`);

        if (activeMenu && activePanel) {
            activeMenu.classList.add("active");
            activePanel.classList.add("active");
        }

        // ĐẶC BIỆT: Nếu chuyển trúng sang tab "orders", thực hiện kích hoạt nạp danh sách đơn hàng từ API
        if (tabName === "orders") {
            loadOrderHistory();
        }
    }

    /**
     * 2. KIỂM TRA ĐƯỜNG DẪN URL XEM CÓ YÊU CẦU MỞ RIÊNG TAB NÀO KHÔNG
     * Ví dụ: /profile?tab=orders hoặc /profile?tab=password
     */
    const urlParams = new URLSearchParams(window.location.search);
    const tabParam = urlParams.get('tab');
    if (tabParam) {
        switchTab(tabParam);
    }

    /**
     * 3. LẮNG NGHE SỰ KIỆN CLICK CHỌN MENU CỦA NGƯỜI DÙNG
     */
    menuItems.forEach(item => {
        item.addEventListener("click", function () {
            const tabName = this.getAttribute("data-tab");
            switchTab(tabName);
        });
    });

    /**
     * 4. TỰ ĐỘNG ẨN CÁC THÔNG BÁO ALERT (SUCCESS/ERROR) SAU 5 GIÂY
     */
    ['errorMsg', 'successMsg'].forEach(id => {
        const alertElement = document.getElementById(id);
        if (alertElement) {
            setTimeout(() => {
                const bsAlert = bootstrap.Alert.getOrCreateInstance(alertElement);
                if (bsAlert) {
                    bsAlert.close();
                }
            }, 5000);
        }
    });
});

/**
 * 5. HÀM FETCH API GỌI CHẠY NGẦM XUỐNG SPRING BOOT ĐỂ LẤY LỊCH SỬ ĐƠN HÀNG
 */
function loadOrderHistory() {
    const container = document.getElementById("orders-container");
    if (!container) return;

    // Thiết lập trạng thái loading xoay tròn trong lúc đợi phản hồi từ server
    container.innerHTML = `
        <div style="text-align: center; padding: 40px; color: #9A9488;">
            <i class="fas fa-spinner fa-spin" style="font-size: 24px; margin-bottom: 8px;"></i>
            <p style="font-family: 'Inter', sans-serif; font-size: 14px;">Đang tải lịch sử mua hàng...</p>
        </div>`;

    // Gọi API ngầm lấy dữ liệu
    fetch("/api/orders/history")
        .then(response => {
            if (response.status === 401) {
                // Nếu chưa đăng nhập hoặc phiên làm việc hết hạn -> đá về trang Login
                window.location.href = "/login";
                return;
            }
            return response.json();
        })
        .then(orders => {
            container.innerHTML = ""; // Xóa sạch icon xoay loading ban đầu

            // TH1: Tài khoản chưa mua bất cứ đơn hàng nào
            if (!orders || orders.length === 0) {
                container.innerHTML = `
                    <div style="text-align: center; padding: 40px; color: #9A9488;">
                        <i class="fa-solid fa-box-open" style="font-size: 36px; margin-bottom: 12px; opacity: 0.5;"></i>
                        <p style="font-size: 15px;">Bạn chưa có đơn hàng nào.</p>
                    </div>`;
                return;
            }

            // TH2: Có đơn hàng -> Chạy vòng lặp vẽ giao diện danh sách đơn hàng đổ ra
            // Chạy vòng lặp vẽ giao diện danh sách đơn hàng đổ ra
            orders.forEach(order => {
                let itemsHtml = "";

                order.items.forEach(item => {
                    // 1. Mặc định ban đầu dùng ảnh placeholder
                    let imgUrl = "https://placehold.co/100x100?text=No+Image";

                    // 2. Xử lý bóc tách chuỗi ảnh thông minh từ DB
                    if (item.productImage && item.productImage.trim() !== "" && item.productImage !== "null") {
                        let rawPath = item.productImage.trim();

                        if (rawPath.startsWith("http")) {
                            // Nếu là link ảnh online (unsplash,...) -> giữ nguyên
                            imgUrl = rawPath;
                        } else {
                            // Nếu trong DB đã có sẵn chữ "/uploads/" hoặc "uploads/" ở đầu
                            if (rawPath.startsWith("/uploads/")) {
                                rawPath = rawPath.replace("/uploads/", ""); // Xóa bỏ để tránh trùng lặp
                            } else if (rawPath.startsWith("uploads/")) {
                                rawPath = rawPath.replace("uploads/", "");
                            }

                            // Tiến hành encode mã hóa ký tự tên file và nối vào cổng static uploads của server
                            imgUrl = `/uploads/${encodeURIComponent(rawPath)}`;
                        }
                    }

                    itemsHtml += `
        <div class="order-item" style="display: flex; gap: 16px; align-items: center; margin-bottom: 12px;">
            <div style="width: 52px; height: 52px; background: #FBF9F6; border-radius: 6px; overflow:hidden; border: 1px solid #E8E4DE; flex-shrink: 0;">
                <img src="${imgUrl}" 
                     style="width:100%; height:100%; object-fit:cover;"
                     onerror="this.onerror=null; this.src='https://placehold.co/100x100?text=No+Image';">
            </div>
            <div style="flex: 1;">
                <h5 style="margin: 0 0 2px 0; font-size: 13.5px; color: #1A1A1A; font-weight: 500;">${item.productName || 'Sản phẩm'}</h5>
                <span style="font-size: 12px; color: #9A9488;">Số lượng: ${item.quantity || 1}</span>
            </div>
            <span style="font-size: 13.5px; font-weight: 500; color: #1A1A1A;">${Number(item.unitPrice || 0).toLocaleString('vi-VN')} đ</span>
        </div>
    `;
                });
                // Phân tích trạng thái đơn hàng
                let badgeStyle = "background: #F0F2EE; color: #6E7762;";
                let statusText = order.status;

                if (order.status === 'NEW' || order.status === 'PENDING') {
                    statusText = "Chờ xác nhận";
                    badgeStyle = "background: #FFFBEB; color: #B45309;";
                } else if (order.status === 'SHIPPING') {
                    statusText = "Đang giao hàng";
                    badgeStyle = "background: #EFF6FF; color: #1D4ED8;";
                } else if (order.status === 'COMPLETED' || order.status === 'PAID') {
                    statusText = "Đã hoàn thành";
                    badgeStyle = "background: #ECFDF5; color: #047857;";
                } else if (order.status === 'CANCELLED') {
                    statusText = "Đã hủy";
                    badgeStyle = "background: #FEF2F2; color: #B91C1C;";
                }

                // Khởi tạo thiết kế Card Đơn hàng
                const orderCard = `
                    <div class="order-card" style="background: #ffffff; border: 1px solid #E8E4DE; border-radius: 12px; padding: 20px; margin-bottom: 20px; text-align: left;">
                        <div class="order-header" style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #F0F2EE; padding-bottom: 12px; margin-bottom: 12px;">
                            <div>
                                <span style="font-weight: 600; color: #1A1A1A; font-size: 14px;">Đơn hàng #${order.id}</span>
                                <span style="color: #9A9488; margin-left: 12px; font-size: 12.5px;">Ngày đặt: ${order.orderDate ? new Date(order.orderDate).toLocaleDateString('vi-VN') : 'Chưa rõ'}</span>
                            </div>
                            <span class="status-badge" style="${badgeStyle} padding: 4px 12px; border-radius: 20px; font-size: 11.5px; font-weight: 500;">
                                ${statusText}
                            </span>
                        </div>
                        
                        <div class="order-body" style="padding: 4px 0;">
                            ${itemsHtml}
                        </div>
                        
                        <div class="order-footer" style="display: flex; justify-content: space-between; align-items: center; border-top: 1px solid #F0F2EE; margin-top: 12px; padding-top: 12px;">
                            <span style="color: #9A9488; font-size: 12.5px; max-width: 55%; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;" title="${order.shippingAddress || ''}">
                                Địa chỉ: ${order.shippingAddress || 'Chưa cập nhật'}
                            </span>
                            <div>
                                <span style="color: #9A9488; font-size: 13px;">Tổng thanh toán: </span>
                                <span style="font-size: 16px; font-weight: 600; color: #798661; margin-left: 4px;">${Number(order.totalAmount).toLocaleString('vi-VN')} đ</span>
                            </div>
                        </div>
                    </div>
                `;

                container.innerHTML += orderCard;

            });
        })
        .catch(err => {
            console.error("Lỗi nạp lịch sử đơn hàng:", err);
            container.innerHTML = `<div style="color: #DC2626; text-align: center; padding: 20px;">Không thể tải dữ liệu đơn hàng vào lúc này. Vui lòng thử lại sau!</div>`;
        });
}