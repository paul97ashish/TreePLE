import Vue from 'vue'
import Router from 'vue-router'
import TreePle from '@/components/TreePle'
import Login from '@/components/Login'
import AddTree from '@/components/addtree1'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'TreePLE',
      component: TreePle
    },
    {
      path: '/login',
      name: 'Login',
      component: Login
    },
    {
      path: '/addtree',
      name: 'addtree',
      component: AddTree
    }
  ]
})
