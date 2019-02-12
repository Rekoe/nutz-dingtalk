import http from '@/http'

export default {
 list(page, seller,success) {
   http.get('food/category/list', {
      page: page,
      seller: seller
    }, success);
 },
 search(page, key, success) {
    http.get('food/category/search', {
      page: page,
      key: key
    }, success);
 },
 status(id,success) {
     http.get('food/category/status', {
         id: id
     }, success);
 },
 type(id,type,success) {
     http.get('food/category/type', {
         id: id,
         type:type
     }, success);
 }
}