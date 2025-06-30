import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createI18n } from 'vue-i18n'

import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

import PrimeVue from 'primevue/config'
import 'primevue/resources/themes/saga-blue/theme.css'
import 'primevue/resources/primevue.min.css'
import 'primeicons/primeicons.css'
import ToastService from 'primevue/toastservice'
import Tooltip from 'primevue/tooltip';
import vSelect from "vue-select";
import "vue-select/dist/vue-select.css";

const vuetify = createVuetify({
  components,
  directives,
})

const i18n = createI18n({
  locale: "en",
  fallbackLocale: 'en',
})


const app = createApp(App)
app.use(router)
app.use(i18n)
app.use(vuetify)
app.use(ElementPlus)
app.use(PrimeVue)
app.use(ToastService)
app.directive('tooltip', Tooltip);
app.component("v-select", vSelect);

app.mount('#app')
