
    function changeQuantity(itemId, newQuantity) {
    // Nếu người dùng giảm số lượng về 0, kích hoạt hàm xóa luôn
    if (newQuantity <= 0) {
    removeItem(itemId);
    return;
}

    // Tạo FormData gửi lên khớp với cơ chế @RequestParam của Controller bạn viết
    const formData = new FormData();
    formData.append('quantity', newQuantity);

    fetch(`/cart/update/${itemId}`, {
    method: 'POST',
    body: formData
}).then(response => {
    if (response.ok) {
    window.location.reload();
}
});
}

    function removeItem(itemId) {
    // if (confirm("Bạn có chắc chắn muốn xóa sản phẩm này khỏi giỏ hàng?")) {
    fetch(`/cart/remove/${itemId}`, {
        method: 'POST'
    }).then(response => {
        if (response.ok) {
            window.location.reload();
        }
    });
}
    // }
    document.addEventListener("DOMContentLoaded", function () {
        // 1. Lắng nghe sự kiện click trên tất cả các nút "Thêm vào giỏ"
        const addButtons = document.querySelectorAll(".btn-add-to-cart");

        addButtons.forEach(button => {
            button.addEventListener("click", function (event) {
                event.preventDefault(); // Chặn không cho trang web bị tải lại

                const productId = this.getAttribute("data-product-id");

                // 2. Dùng Fetch API gửi yêu cầu chạy ngầm (Ajax) lên Backend
                fetch(`/cart/add-ajax?productId=${productId}`, {
                    method: "POST"
                })
                    .then(response => response.json()) // Nhận con số tổng số lượng mới từ Server trả về
                    .then(totalItems => {
                        // 3. Tìm đến cái Badge số lượng trên Header
                        let badge = document.querySelector(".cart-badge");

                        if (badge) {
                            // Nếu đã có badge, cập nhật con số mới nhất
                            badge.innerText = totalItems;
                            badge.style.display = totalItems > 0 ? "flex" : "none";
                        }

                        alert("Đã thêm sản phẩm vào giỏ hàng thành công!");
                    })
                    .catch(error => console.error("Lỗi thêm vào giỏ hàng:", error));
            });
        });
    });