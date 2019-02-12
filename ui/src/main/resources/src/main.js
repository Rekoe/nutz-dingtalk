import Vue from 'vue'
import App from '@/App'
import store from '@/vuex/store'
import 'nprogress/nprogress.css'
import NProgress from 'nprogress'
import routes from '@/routes'
import 'font-awesome/css/font-awesome.min.css'
import '@/assets/style.less'
import 'normalize.css'
import '@/common/mixin.scss'
import api from "@/api";
import http from '@/http'
import rules from '@/rules'

import i18n from '@/lang'
import '@/icons' // icon
import BgaBackTop from 'bga-back-top-vue'
import infiniteScroll from 'vue-infinite-scroll'
Vue.use(BgaBackTop)
Vue.use(infiniteScroll)

import utils from '@/common/js/util'
import { AlertPlugin } from 'vux'
import  FastClick  from  'fastclick'
Vue.use(AlertPlugin)

Vue.prototype.$http = http;
Vue.prototype.$rules = rules;
Vue.prototype.$api = api;
Vue.prototype.$utils = utils;
//Vue.prototype.$alert = Alert;
global.baseUrl = process.env.NODE_ENV == "development" ? 'api' : '';

var ws;
var WS_URL = (process.env.NODE_ENV == "development"?"rekoe.ngrok.wendal.cn":window.location.host) + "/websocket"
Vue.prototype.websocket_init = function(){
	this.ws  = new WebSocket('ws://'+WS_URL);
}
Vue.prototype.websocket_join = function(room){
	this.ws.onopen = function (evt) {
		this.send(JSON.stringify({room:room,action:'join'}));
    }
}
FastClick.attach(document.body);
// NProgress.configure({ showSpinner: false });
const router = new VueRouter({
    routes,
    //mode:"history",
    linkActiveClass:"active"
})

router.beforeEach((to, from, next) => {
    NProgress.start();
    if (to.path === '/') {
        store.commit('remove');
    }
    next()
})

router.afterEach(transition => {
    NProgress.done();
});
Vue.config.productionTip = false
new Vue({
    router,
    store,
    i18n,
    render: h => h(App)
}).$mount('#app')