import http from '@/http'

export default {
	list(page, success) {
        http.get('seller/order/list', {
            page: page
        }, success);
    }
}