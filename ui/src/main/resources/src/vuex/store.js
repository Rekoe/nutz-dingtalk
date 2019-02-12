import Vue from 'vue'
import Vuex from 'vuex'
import createPersistedState from 'vuex-persistedstate'
import Mint from 'mint-ui';
import 'mint-ui/lib/style.css';
import Cookies from 'js-cookie';
Vue.use(Vuex)
Vue.use(Mint);
export default new Vuex.Store({
    mutations: {
        save(state, user) {
            state.loginUser = user;
        },
        remove(state) {
            state.loginUser = {
                name: '',
                id: 0,
                mobile: '',
                roles: [],
                permissions: []
            }
        },
        updateAvatar(state,key){
            state.loginUser.headKey = key;
        },
        SET_LANGUAGE: (state, language) => {
            state.language = language
            Cookies.set('language', language)
        },
        order_meal:(state)=>{
            state.orderedMeal = true;
        },
        cancel_meal:(state)=>{
            state.orderedMeal = false;
        },
        updateMealOrder:(state,food)=>{
            let have = false;
            var a = state.orderMeals
            a.forEach((order, index) => {
                if (order.id==food.id) {
                    if(food.count==0){
                        a.splice(index,1)
                    }
                    order.count = food.count;
                    have = true;
                }
            });
            if(!have){
                state.orderMeals.push({'id':food.id,'count':1,'price':food.price,'name':food.name}); 
            }
        },
        initOrderInfo:(state,food)=>{
            var a = state.orderMeals
            let length = a.length
            for(var i=0;i<length;i++){
                state.orderMeals.pop();
            }
            state.orderMeals.push({'id':food.item_id,'count':food.number,'price':food.price,'name':food.name}); 
        },
        clearMealOrders:(state)=>{
           var a = state.orderMeals
           let length = a.length
           for(var i=0;i<length;i++){
            state.orderMeals.pop();
           }
        },
        setDingTalkId:(state,dingTalkId)=>{
            state.dingTalkId = dingTalkId;
        },
        updateUserProvince:(state,userProvince)=>{
            state.userProvince = userProvince;
        },
        loginSuccess:(state)=>{
            state.isFirstLogin = false;
        }
    },
    getters: {
        hasRole: (state, getters) => (role) => {
            return state.loginUser.roles.filter(r => r === role).length > 0;
        },
        hasPermission: (state, getters) => (permission) => {
            return state.loginUser.permissions.filter(p => p === '*' || p === permission).length > 0;
        },
        canCommitMeal:(state,getters) => ()=>{
            return state.orderedMeal;
        },
        orderList:(state) => ()=>{
            return state.orderMeals;
        },
        getDingTalkId:(state)=>()=>{
            return state.dingTalkId;
        },
        getUserProvince:(state)=>()=>{
            return state.userProvince;
        },
        getFirstLoginStatus:(state)=>()=>{
            return state.isFirstLogin;
        },
    },
    state: {
        loginUser: {
            name: '',
            id: 0,
            mobile: '',
            roles: [],
            permissions: []
        },
        language: Cookies.get('language') || 'en',
        orderedMeal: false,
        orderMeals:[],
        dingTalkId:'',
        userProvince:'定位...',
        isFirstLogin:true
    },
    actions: {
        setLanguage({ commit }, language) {
            commit('SET_LANGUAGE', language);
        },
        commitMeal({commit}){
            commit('order_meal');
        },
        cancelMeal({commit}){
            commit('cancel_meal');
        },
        updateMealOrder({commit},food){
            commit('updateMealOrder',food);
        },
        clearMealOrders({commit}){
            commit('clearMealOrders')
        },
        setDingTalkId({commit},dingTalkId){
            commit('setDingTalkId',dingTalkId);
        },
        initOrderInfo({commit},food){
            commit('initOrderInfo',food);
        },
        updateUserProvince({commit},provinc){
            commit('updateUserProvince',provinc);
        },
        loginSuccess({commit}){
            commit('loginSuccess');
        }
      },
    strict: process.env.NODE_ENV !== 'production',
    plugins: [createPersistedState()]
})
