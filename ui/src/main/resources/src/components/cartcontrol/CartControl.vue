<template>
  <div class="cartcontrol">
    <transition name="move">
      <div class="cart-decrease icon-remove_circle_outline" @click.stop.prevent="decreaseCart" v-show="food.count && !this.orderedMeal"></div>
    </transition>
    <div class="cart-count" v-show="food.count">{{food.count}}</div>
    <div class="cart-add icon-add_circle" @click.stop.prevent="increaseCart" v-show="!this.orderedMeal">
      <i class="bg"></i>
    </div>
  </div>
</template>

<script>
  import Vue from 'vue'
  import { mapState ,mapActions} from "vuex";
  export default {
    data() {
      return {}
    },
    props: {
      food: {
        type: Object,
        default: {}
      }
    },
    methods: {
      ...mapActions(["updateMealOrder"]),
      decreaseCart() {
        if(!this.orderedMeal){
          if(this.food.count>0){
            this.food.count--;
            this.updateMealOrder(this.food);
          }
        } 
      },
      increaseCart() {
        if(!this.orderedMeal){
          console.log('increaseCart ..',this.food.id,this.food.name,this.food.count)
          if (!this.food.count) {
            Vue.set(this.food, "count", 1)
          } else {
            this.food.count++;
          }
          this.updateMealOrder(this.food);
        } 
      }
    },
    computed: {
    ...mapState({
      orderedMeal: state => state.orderedMeal,
      orderMeals: state => state.orderMeals
    }),
    selected(){
      let have = false;
      this.orderMeals.forEach(item => {
        if(item.id==this.food.id){
          have = true;
          this.food.count = item.count;
          return
        }
      });
      return have ;
    }
  }
  }
</script>
<style>
  .cartcontrol {
    font-size: 0;
  }

  .cartcontrol .cart-decrease {
    display: inline-block;
    width: 26px;
    height: 26px;
    font-size: 26px;
    color: #b4b4b4;
  }

  .cartcontrol .cart-add .bg {
    width: 20px;
    height: 20px;
    border-radius: 50%;
    background: black;
    position: absolute;
    left: 3px;
    top: 3px;
    z-index: -1;
  }

  .cartcontrol .cart-count {
    display: inline-block;
    width: 25px;
    text-align: center;
    font-size: 12px;
    line-height: 26px;
    vertical-align: top;
  }

  .cartcontrol .cart-add {
    display: inline-block;
    width: 26px;
    height: 26px;
    font-size: 26px;
    color: #ffd161;
    position: relative;
  }

  .move-enter-active, .move-leave-active {
    transition: all 0.3s linear;
  }

  .move-enter, .move-leave-to {
    transform: translateX(20px) rotate(180deg);
  }
</style>
