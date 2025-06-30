import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import HomeView from '../views/HomeView.vue'

import DefinitionHome from '../views/definitions/DefinitionHome.vue';
import DefinitionsList from '../views/definitions/DefinitionsList.vue';
import DefinitionNew from '../views/definitions/DefinitionNew.vue';
import DefinitionEdit from '../views/definitions/DefinitionEdit.vue';
import DefinitionActions from '../views/definitions/DefinitionActions.vue';
import UploadAnonymizedDataView from "@/views/UploadAnonymizedDataView.vue";

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/fileupload',
    name: 'fileupload',

    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import('../views/UploadView.vue')
  },
  {
    path: '/qualitymodelupload',
    name: 'qualitymodelupload',

    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import('../views/QualityModelUploadView.vue')
  },
  {
    path: '/browser',
    name: 'browser',
    component: () => import('../views/BrowserView.vue')
  },
  { path: '/definitions-home', name: 'DefinitionHome', component: DefinitionHome },
  { path: '/definitions/new', name: 'DefinitionNew', component: DefinitionNew },
  { path: '/definitions/:id/edit', name: 'DefinitionEdit', component: DefinitionEdit, props: true },
  { path: '/upload-anonymized-data', name: 'UploadAnonymizedData', component: UploadAnonymizedDataView },
]

//import DefinitionHome from '@/views/definitions/DefinitionHome.vue';
// import DefinitionsList from '@/views/definitions/DefinitionsList.vue';
// import DefinitionNew from '@/views/definitions/DefinitionNew\.vue';
// import DefinitionEdit from '@/views/definitions/DefinitionEdit.vue';
// import DefinitionActions from '@/views/definitions/DefinitionActions.vue';
//
// const routes = \[
// { path: '/', name: 'QualityModelUpload', component: QualityModelUploadView },
// { path: '/definitions-home', name: 'DefinitionHome', component: DefinitionHome },
// { path: '/definitions', name: 'DefinitionsList', component: DefinitionsList },
// { path: '/definitions/new', name: 'DefinitionNew', component: DefinitionNew },
// { path: '/definitions/\:id/edit', name: 'DefinitionEdit', component: DefinitionEdit, props: true },
// { path: '/actions', name: 'DefinitionActions', component: DefinitionActions }
// ];
//
// const router = createRouter({
// history: createWebHistory(import.meta.env.BASE\_URL),
// routes
// });
// export default router;

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
  scrollBehavior(to, from, savePosition) {
    return { top: 0 } // always scroll to top
  }
})

export default router
