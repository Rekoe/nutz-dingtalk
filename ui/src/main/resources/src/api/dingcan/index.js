import http from '@/http'

export default {
    query(success) {
      http.get('dingcan/query', {}, success);
    },
    submit_order(orders,success){
      http.postBody('dingcan/submit_order', orders, success)
    },
    cancel_order(success){
      http.get('dingcan/cancel_order', {}, success);
    },
    goods(id,success){
      http.get('dingcan/goods', {id:id}, success);
    },
    new_goods(id,userProvince,success){
      http.get('dingcan/new_goods', {id:id,user_province:userProvince}, success);
    },
    new_ratings(success){
      http.get('dingcan/new_ratings', {}, success);
    }, 
    sellers(userProvince,success){
      http.get('dingcan/sellers', {user_province:userProvince}, success);
    },
    new_seller(success){
      http.get('dingcan/new_seller', {}, success);
    },
    header(success){
      http.get('dingcan/header', {}, success);
    }
}