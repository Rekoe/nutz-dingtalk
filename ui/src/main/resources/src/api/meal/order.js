import http from '@/http'

export default {
 list(page,success) {
   http.get('meal/order/list', {
      page: page
    }, success);
 },
 search(page, key, success) {
    http.get('meal/order/search', {
      page: page,
      key: key
    }, success);
 }
}