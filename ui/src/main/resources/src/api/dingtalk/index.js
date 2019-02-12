import http from '@/http'

export default {
    auth(code, success) {
        http.get('dingtalk/auth', {
            code: code
        }, success);
    },
    sign(url,success) {
        http.get('sign', {url:url}, success);
    },
    update_work_place(work_place,success) {
        http.get('update_work_place', {work_place:work_place}, success);
    }
}