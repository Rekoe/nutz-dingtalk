import Vue from 'vue'
import VueI18n from 'vue-i18n'
import Cookies from 'js-cookie'
import elementZhLocale from 'element-ui/lib/locale/lang/zh-CN'// element-ui lang
import elementEnLocale from 'element-ui/lib/locale/lang/en' // element-ui lang
import zhLocale from './zh'
import enLocale from './en'

Vue.use(VueI18n)
const messages = {
  en: {
	...enLocale,
	...elementEnLocale
  },
  zh: {
    ...zhLocale,
    ...elementZhLocale
  }
}
const i18n = new VueI18n({
  locale: Cookies.get('language') || 'zh',
  messages
})
ELEMENT.i18n((key, value) => i18n.t(key, value))
export default i18n
