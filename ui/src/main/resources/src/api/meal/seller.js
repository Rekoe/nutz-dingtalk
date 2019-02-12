import http from '@/http'

export default {
	list(page, success) {
        http.get('meal/seller/list', {
            page: page
        }, success);
    },
    search(page, key, success) {
        http.get('meal/seller/search', {
            page: page,
            key: key
        }, success);
    },
    save(seller, success) {
        http.get('meal/seller/collection', {
            url: seller.url,
            province:seller.province
        }, success);
    },
    status(id,success) {
        http.get('meal/seller/status', {id:id}, success);
    }
}