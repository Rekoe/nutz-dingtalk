'use strict'

import axios from 'axios'
import qs from 'qs'
import store from '../vuex/store'
import {
    Loading,
    Message
} from 'element-ui'
import i18n from '.././lang'
var loadinginstace;
axios.defaults.timeout = 60000
axios.defaults.baseURL = process.env.NODE_ENV == "development" ? 'http://localhost:8888/api' : '';
// 请求前拦截
let status = false;
let store1 =  store;
axios.interceptors.request.use(config => {
    if(store1.state.dingTalkId){
        config.headers['X-Auth-Token'] = store1.state.dingTalkId;
    }
    if (config.url.indexOf('metrics') >= 0) {
        return config;
    }
    if (loadinginstace && loadinginstace.visible && !status) {
        return config;
    } else {
        status = true;
        loadinginstace = Loading.service({
            target: '.content-container',
            lock: true,
            text: i18n.t('http.loading')
        });
    }
    return config
}, error => {
    if (loadinginstace) {
        loadinginstace.close();
        status = false;
    }
    return Promise.reject(i18n.t('http.timeout'))
})
// 设置response统一处理
axios.interceptors.response.use(response => {
    if (loadinginstace) {
        status = false;
        loadinginstace.close();
    }
    if (response.config.url.indexOf('druid') > 0 || response.config.url.indexOf('metrics') > 0) {
        if (response.status == 200) {
            return Promise.resolve(response.data);
        } else {
            return Promise.reject(i18n.t('http.fail'));
        }
    }
    if (response.data.operationState == 'SUCCESS') { // 数据成功
        return Promise.resolve(response.data.data)
    } else { // 数据失败直接reject
        return Promise.reject(response.data.errors[0]);
    }
}, error => { // http失败
    if (loadinginstace) {
        loadinginstace.close();
        status = false;
    }
    switch (error.response.status) {
        case 403:
            location.href = '/';
        case 401:
            location.href = '/';
        case 404:
            return Promise.reject(i18n.t('http.code_404'));
        case 500:
            return Promise.reject(i18n.t('http.code_500'))
        case 504:
            return Promise.reject(i18n.t('http.code_504'))
        case 502:
            return Promise.reject(i18n.t('http.code_502'))
        default:
            return Promise.reject(error.response.data.msg[0]);
    }
})


export default {
    /**
	 * 发送post请求(form表单)
	 * 
	 * @param {string}
	 *            url 地址
	 * @param {object}
	 *            data 请求数据
	 * @param {Function}
	 *            done 成功回调
	 * @param {Function}
	 *            fail 失败回调(可选)
	 */
	postByHeaders(url, data, headers,done, fail) {
        return axios({method: 'post',url,data: data,headers:headers}).then(
            data => done(data)
        ).catch(error => {
            if (fail) {
                fail(error)
            } else {
                Message.error({
                    message: error
                });
            }
        })
    },
    post(url, data, done, fail) {
        return axios({
            method: 'post',
            url,
            data: qs.stringify(data),
            headers: {
                'X-Requested-With': 'XMLHttpRequest',
                'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
            }
        }).then(
            data => done(data)
        ).catch(error => {
            if (fail) {
                fail(error)
            } else {
                Message.error({
                    message: error
                });
            }
        })
    },
    postUpload(url, data, done, fail) {
        return axios({
            method: 'post',
            url,
            data: data,
            headers: {
                'X-Requested-With': 'XMLHttpRequest',
                'Content-Type': 'multipart/form-data'
            }
        }).then(
            data => done(data)
        ).catch(error => {
            if (fail) {
                fail(error)
            } else {
                Message.error({
                    message: error
                });
            }
        })
    },
    post(url, data, done, fail) {
        return axios({
            method: 'post',
            url,
            data: qs.stringify(data),
            headers: {
                'X-Requested-With': 'XMLHttpRequest',
                'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
            }
        }).then(
            data => done(data)
        ).catch(error => {
            if (fail) {
                fail(error)
            } else {
                Message.error({
                    message: error
                });
            }
        })
    },
    /**
	 * 发送post请求(body流)
	 * 
	 * @param {string}
	 *            url 地址
	 * @param {object}
	 *            data 请求数据
	 * @param {Function}
	 *            done 成功回调
	 * @param {Function}
	 *            fail 失败回调(可选)
	 */
    postBody(url, data, done, fail) {
        return axios.post(url, data, {
            headers: {
            		'X-Requested-With': 'XMLHttpRequest'
          }
      }).then(data => done(data))
            .catch(error => {
                if (fail) {
                    fail(error)
                } else {
                    Message.error({
                        message: error
                    });
                }
            });
    },
    /**
	 * 发送get请求
	 * 
	 * @param {string}
	 *            url 请求地址
	 * @param {object}
	 *            data 请求数据(可选)
	 * @param {Function}
	 *            done 成功回调
	 * @param {Function}
	 *            fail 失败回调(可选)
	 */
    get(url, ...options) {
        let params, done, fail;
        if (typeof options[0] === 'object') {
            params = options[0];
            done = options[1];
            fail = options[2];
        } else {
            params = {};
            done = options[0];
            fail = options[1];
        }
        return axios({
            method: 'get',
            url,
            params, // get 请求时带的参数
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        }).then(
            data => done(data)
        ).catch(error => {
            if (fail) {
                fail(error)
            } else {
                Message.error({
                    message: error
                });
            }
        })
    }
}