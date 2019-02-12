import Login from '@/views/Login.vue'
import Home from '@/views/Home.vue'
import NotFound from '@/views/404.vue'
//import DingCan from '@/views/DingCan.vue'
import Dashboard from '@/views/dashbord/DashBoard.vue'

import User from '@/views/acl/User.vue'
import Role from '@/views/acl/Role.vue'
import Permission from '@/views/acl/Permission.vue'
import MSeller from '@/views/meal/Seller.vue'
import FoodCategory from '@/views/meal/FoodCategory.vue'
import MFood from '@/views/meal/Food.vue'
import MOrder from '@/views/meal/MealOrder.vue'
import MSellerOrder from '@/views/meal/SellerOrder.vue'

import goods from '@/components/goods/Goods'
import ratings from '@/components/ratings/Ratings'
import seller from '@/components/seller/Seller'

import MapPosition from '@/components/MapPosition.vue'

let routes = [{
        path: '/',
        component: Login,
        name: '登录',
        hidden: true,
        meta:{
            title: '着迷网话题详情页',
            keepAlive: false
          }
    },
    {
        path: '/404',
        component: NotFound,
        name: '',
        hidden: true
    },
    {
    	path: '/goods/:id',
    	component: goods,
    	name: '菜单',
    	hidden: true
    },
    {
    	path: '/map',
    	component: MapPosition,
    	name: '地图',
    	hidden: true
    },
    {
    	path: '/goods',
    	component: goods,
    	name: '菜单',
    	hidden: true
    },
    {
    	path: '/ratings',
    	component: ratings,
    	name: 'ratings',
    	hidden: true
    },
    {
    	path: '/seller',
    	component: seller,
    	name: 'seller',
    	hidden: true
    },
    {
        path: '/',
        component: Home,
        name: 'main.index',
        leaf: true,
        iconCls: 'el-icon-fa-dashboard', // 图标样式class
        children: [
            {
            path: '/dashboard',
            iconCls: 'el-icon-fa-dashboard',
            component: Dashboard,
            name: 'main.dashboard'
        }]
    },
    {
        path: '/',
        component: Home,
        name: 'acl.visit',
        iconCls: 'el-icon-fa-users', // 图标样式class
        children: [
            {
                path: '/user',
                iconCls: 'el-icon-fa-user',
                component: User,
                meta: {
                    p: 'user.list'
                },
                name: 'acl.user_manager'
            },
            {
                path: '/role',
                iconCls: 'el-icon-fa-lock',
                component: Role,
                meta: {
                    p: 'role.list'
                },
                name: 'acl.role_manager'
            },
            {
                path: '/permission',
                iconCls: 'el-icon-fa-eye',
                component: Permission,
                meta: {
                    p: 'permission.list'
                },
                name: 'acl.permission_manager'
            }
        ]
    },
    {
        path: '/',
        component: Home,
        name: 'meal.manager',
        iconCls: 'el-icon-fa-users', // 图标样式class
        children: [
            {
                path: '/MSeller',
                iconCls: 'el-icon-fa-user',
                component: MSeller,
                meta: {
                    p: 'meal.seller'
                },
                name: 'meal.seller'
            },
            {
                path: '/MCategory',
                iconCls: 'el-icon-fa-user',
                component: FoodCategory,
                meta: {
                    p: 'meal.food.category.list'
                },
                name: 'meal.category'
            },
            {
                path: '/MFood',
                iconCls: 'el-icon-fa-user',
                component: MFood,
                meta: {
                    p: 'meal.food.list'
                },
                name: 'meal.food'
            },
            {
                path: '/MOrder',
                iconCls: 'el-icon-fa-user',
                component: MOrder,
                meta: {
                    p: 'meal.order.list'
                },
                name: 'meal.order'
            },
            {
                path: '/MSellerOrder',
                iconCls: 'el-icon-fa-user',
                component: MSellerOrder,
                meta: {
                    p: 'meal.seller.order.list'
                },
                name: 'meal.seller_order'
            }
        ]
    }
];

export default routes;