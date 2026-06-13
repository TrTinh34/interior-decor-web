function increaseQty() {
    var qtyInput = document.getElementById('productQty');
    var max = parseInt(qtyInput.getAttribute('max')) || 99;
    var currentVal = parseInt(qtyInput.value);
    if (currentVal < max) {
        qtyInput.value = currentVal + 1;
    }
}

function decreaseQty() {
    var qtyInput = document.getElementById('productQty');
    var currentVal = parseInt(qtyInput.value);
    if (currentVal > 1) {
        qtyInput.value = currentVal - 1;
    }
}

function openTab(evt, tabId) {
    var i, tabcontent, tablinks;
    tabcontent = document.getElementsByClassName("tab-content");
    for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].classList.remove("active");
    }
    tablinks = document.getElementsByClassName("tab-link");
    for (i = 0; i < tablinks.length; i++) {
        tablinks[i].classList.remove("active");
    }
    document.getElementById(tabId).classList.add("active");
    evt.currentTarget.classList.add("active");
}

function triggerBuyNow(form) {
    // Thay đổi tạm thời điểm đến của Form thành đường dẫn checkout trực tiếp nếu hệ thống của bạn hỗ trợ
    form.action = '/cart/buy-now';
    form.submit();
}