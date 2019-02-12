import http from '@/http'

export default {
	list(page, success) {
        http.get('meal/food/list', {
            page: page
        }, success);
    },
    search(page, key, success) {
        http.get('meal/food/search', {
            page: page,
            key: key
        }, success);
    },
    status(id,success) {
        http.get('meal/food/status', {
            id: id
        }, success);
    }
}