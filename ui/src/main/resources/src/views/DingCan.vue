<template>
<div id=top>
  <common-header :seller="seller" ></common-header>
  <div id="goods">
    
    <div id="menu_warpper" ref="menu-warpper">
      <ul>
        <li v-if="item" v-for="(item, index) in goods" :key="index" class="menu_item menu_item_hook" :class="{'current':  index === currentIndex}" @click="select_menu(index)">
          <span class="menu_text">
            <preferential v-if="item.type != -1" :type="item.type" :size="'small'" :oppo="true"></preferential>{{item.name}}</span>
        </li>
      </ul>
    </div>
    <div id="goods_items"  ref="goods-warpper">
      <div id="for-scroll">
        <div v-for="(type,typeIndex) in goods" :key="typeIndex" class="goods_type goods_type_hook">
          <h5 class="goods_title">{{type.name}}</h5>
          <div v-for="(item,itemIndex) in type.foods" :key="itemIndex" class="goods_item" @click="select_food(item)">
            <div class="for_border_bottom">
              <img :src="item.icon" class="goods_item_image">
              <div class="text_warpper">
                <h6 class="goods_item_title">{{item.name}}</h6>
                <span v-if="item.description" class="goods_item_desc">{{item.description}}</span>
                <span class="sellCount_rating">月售{{item.sellCount}}份 &nbsp;&nbsp;&nbsp; 好评率{{item.rating}}%</span>
                <div class="price">
                  <span class="goods_item_price">￥{{item.price}}</span>
                  <span v-if="item.oldPrice" class="goods_item_oldPrice">￥{{item.oldPrice}}</span>
                  <cartControl :food.sync="goods[typeIndex].foods[itemIndex]"></cartControl>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <cart :foodList="foodList" v-if="foodList" :seller="seller"></cart>
    <food v-if="selectedFood.name" :selectedFood='selectedFood'  ref='food-detail' ></food>
  </div>
  </div>
</template>

<script>
import commonHeader from '@/components/header/header'
import BetterScroll from 'better-scroll'
import preferential from '@/components/preferential/preferential'
import cart from '@/components/cart/cart'
import cartControl from '@/components/cartControl/cartControl'
import food from '@/components/food/food'
export default {
  props: {
    seller: {},
    goods: {}
  },
  components: {
    commonHeader,
    preferential,
    cart,
    cartControl,
    food
  },
  data () {
    return {
      goodsHeight: [0],
      goodsList: [],
      menuList: [],
      menuScroll: {},
      goodsScroll: {},
      currentY: 0,
      selectedFood: {},
      //goods:[]
    }
  },
  computed: {
    currentIndex: function () {
      for (let i = 0; i < this.goodsHeight.length; i++) {
        if (this.currentY >= this.goodsHeight[i] && this.currentY < this.goodsHeight[i + 1]) {
          return i
        }
      }
    },
    foodList: function () {
      var arr = []
      // eslint-disable-next-line
      this.goods.forEach(type => {
        type.foods.forEach(food => {
          arr.push(food)
        })
      })
      return arr
    }
  },
  methods: {
    _initScroll () {
      this.menuScroll = new BetterScroll(this.$refs['menu-warpper'], {click: true})
      this.goodsScroll = new BetterScroll(this.$refs['goods-warpper'], {probeType: 3, click: true})
      this.goodsList = this.$refs['goods-warpper'].getElementsByClassName('goods_type_hook')
      this.menuList = this.$refs['menu-warpper'].getElementsByClassName('menu_item_hook')
      var height = 0
      for (let i = 0; i < this.goodsList.length; i++) {
        height += this.goodsList[i].clientHeight
        this.goodsHeight.push(height)
      }
      this._calculateY(this.goodsScroll)
    },
    _calculateY (goodsDOMList) {
      var _this = this
      goodsDOMList.on('scroll', function (pos) {
        _this.currentY = Math.abs(Math.round(pos.y))
        _this.menuScroll.scrollToElement(_this.menuList[_this.currentIndex], 300)
      })
    },
    select_menu (index) {
      this.goodsScroll.scrollToElement(this.goodsList[index], 300)
    },
    showFood () {
      this.$refs['food-detail'].show_food()
    },
    select_food (food) {
      this.selectedFood = food
      this.$nextTick(() => {
        this.showFood()
      })
    }
  },
  mounted () {
    this.$nextTick(() => {
      this._initScroll()
    });
  }
}
</script>

<style lang="scss" scoped>
@import "../common/mixin.scss";
#goods {
  display: flex;
  width: 100%;
  position: absolute;
  top: 176px;
  bottom: 46px;
  overflow: hidden;
  #menu_warpper {
    flex: 0 0 80px;
    width: 80px;
    .menu_item {
      display: table;
      box-sizing: border-box;
      width: 100%;
      height: 53px;
      padding: 0 12px;
      background: #f3f5f7;
      &.current{
        background-color: #fff;
        transition: all 0.5s;
        .menu_text{
          @include clear_bottom_1px();
        }
      }
      &:last-child {
        .menu_text {
          border-bottom: none;
        }
      }
      .menu_text {
        display: table-cell;
        vertical-align: middle;
        text-align: center;
        font-size: 12px;
        color: rgb(24, 20, 20);
        @include border-bottom-1px (rgba(7,17,27,.2));
      }
    }
  }
  #goods_items {
    flex: 1;
    overflow: hidden;
    .goods_type {
      width: 100%;
      .goods_title {
        background-color: #f3f5f7;
        color: rgba(7, 17, 27, 0.5);
        height: 26px;
        font-size: 12px;
        font-weight: 200;
        line-height: 26px;
        border-left: 2px solid rgba(7, 17, 27, 0.1);
        padding-left: 18px;
      }
      .goods_item {
        box-sizing: border-box;
        padding: 0 18px;
        font-size: 0;
        .for_border_bottom {
          padding: 18px 0;
        }
        &:not(:last-child) {
          .for_border_bottom {
            @include border_bottom_1px(rgba(7,17,27,0.2));
          }
        }
        .goods_item_image {
          width: 57px;
          height: 57px;
          float: left;
          margin-right: 10px;
        }
        .text_warpper {
          padding-left: 67px;
        }
        .goods_item_title {
          display: block;
          font-size: 14px;
          font-weight: 200;
          line-height: 14px;
          margin-bottom: 8px;
        }
        .goods_item_desc {
          display: block;
          font-size: 10px;
          color: rgb(147, 153, 159);
          line-height: 10px;
        }
        .sellCount_rating {
          display: block;
          font-size: 10px;
          color: rgb(147, 153, 159);
          line-height: 10px;
          margin-top: 8px;
        }
        .goods_item_price {
          font-size: 14px;
          color: rgb(240, 20, 20);
          font-weight: 700;
          line-height: 24px;
          margin-right: 8px;
        }
        .goods_item_oldPrice {
          font-size: 10px;
          color: rgb(147, 153, 159);
          font-weight: bold;
          line-height: 24px;
          text-decoration: line-through;
        }
      }
    }
  }
}
</style>
