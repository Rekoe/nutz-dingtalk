<template>
  <div class="nav">
    <template v-for="(operate,path,index) in navTo">
      <router-link class="nav-item" :to="{ path: operate.path }">{{operate.name}}<i class="line"></i></router-link>
    </template>
  </div>
</template>

<script>
import { mapActions,mapState } from "vuex";
  export default {
    data() {
      return {
        sellers:[],
        number:1,
        navTo:[]
      }
    },
    props: {
      commentNum: {
        type: Number,
        default: 0
      }
    },
    computed:{
      ...mapState({
        userProvince: state => state.userProvince
      })
    },
    methods:{
      _loadMeuns(){
        let userProvince = this.userProvince;
        this.$api.DingCan.sellers(userProvince,result => {
          this.sellers = result.data;
          let len = this.sellers.length;
          for(var i=0;i<len;i++){
            let seller = this.sellers[i];
            let id = seller.id;
            let name = seller.name;
            this.navTo.push({'name':name,"path":'/goods/'+id});
          }
        });
      }
    },
    created() {
      this._loadMeuns();
    }
  }
</script>

<style scoped>
  .nav {
    display: flex;
    width: 100%;
    height: 40px;
    line-height: 40px;
    border-bottom: 1px solid #e4e4e4;
  }

  .nav-item {
    flex: 1;
    text-align: center;
    text-decoration: none;
    color: #666666;
    position: relative;
  }

  .nav .active {
    color: #ffbb22;
  }

  .nav .active .line {
    width: 20px;
    height: 2px;
    background: #ffbb22;
    display: inline-block;
    position: absolute;
    bottom: 0;
    left: 50%;
    margin-left: -10px;
  }
</style>
