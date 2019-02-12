<template>
  <div class="header">
    <!-- {{poiInfo.name}} -->
    <!-- 顶部通栏 开始 -->
    <div class="top-wrapper">
      <div class="back-wrapper">
        <span class="icon-arrow_lift"></span>
      </div>
      <form class="search-wrapper">
        <span class="search-icon"></span>
        <input class="search-bar" type="text" placeholder="搜索店内商品" @click="search"/>
      </form>
      <div class="more-wrapper">
        <a class="spelling-bt" @click="position">{{mapSite.province}}</a>
        <div class="more-bt">
          <!--i class="s-radius"></i>
          <i class="s-radius"></i>
          <i class="s-radius"></i-->
        </div>
      </div>
    </div>
    <!-- 顶部通栏 结束 -->
    <!-- 主题内容 开始 -->
    <div class="content-wrapper">
      <div class="icon" :style="head_bg">
        <!--<img :src="poiInfo.pic_url" />-->
      </div>
      <div class="name">
        <h3>{{poiInfo.name}}</h3>
      </div>
      <div class="collect">
        <span id="text">{{this.name}}</span>
      </div>
    </div>
    <!-- 主题内容 结束 -->
    <!-- 公告内容 开始 -->
    <div class="bulletin-wrapper">
      <img class="icon" v-if="poiInfo.discounts2" :src="poiInfo.discounts2[0].icon_url" />
      <span class="text" v-if="poiInfo.discounts2">{{poiInfo.discounts2[0].info}}</span>
      <div class="detail" v-if="poiInfo.discounts2" @click="showBulletin">
        公告详情...
        <span class="icon-keyboard_arrow_right"></span>
      </div>
    </div>
    <!-- 公告内容 结束 -->
    <!-- 背景内容 开始 -->
    <div class="bg-wrapper" :style="head_pic_url">
      <!-- <img :src="poiInfo.head_pic_url" /> -->
    </div>
    <!-- 定位 开始-->
    <transition name="bulletin-detail">
      <div class="bulletin-detail" v-show="isPosition">
        <div class="detail-wrapper">
          <!-- 相关内容容器 -->
          <div class="main-wrapper" :style="detail_bg">
            <div class="icon" :style="head_bg"></div>
            <div class="discounts">
              <p>
                <app-map-position ref="mapView" :mapSite="mapSite" poiInfo="poiInfo" avatar="avatar"></app-map-position>
              </p>
            </div>
          </div>
          <!-- 关闭内容容器 -->
          <div class="close-wrapper">
            <span class="icon-close" @click="closePosition"></span>
          </div>
        </div>
      </div>
    </transition>
    <!-- 背景内容 结束 -->
    <!-- 公告详情 开始 -->
    <transition name="bulletin-detail">
      <div class="bulletin-detail" v-show="isShow">
        <div class="detail-wrapper">
          <!-- 相关内容容器 -->
          <div class="main-wrapper" :style="detail_bg">
            <div class="icon" :style="head_bg"></div>
            <h3 class="name">{{poiInfo.name}}</h3>
            <!-- 星级评价 -->
            <div class="score">
              <app-star :score="poiInfo.wm_poi_score"></app-star>
              <span>{{poiInfo.wm_poi_score}}</span>
            </div>
            <p class="tip">
              {{poiInfo.min_price_tip}} <i>|</i> {{poiInfo.shipping_fee_tip}} <i>|</i> {{poiInfo.delivery_time_tip}}
            </p>
            <p class="time">
              配送时间: {{poiInfo.shipping_time}}
            </p>
            <div class="discounts" v-if="poiInfo.discounts2">
              <p>
                <img :src="poiInfo.discounts2[0].icon_url" />
                <span>{{poiInfo.discounts2[0].info}}</span>
              </p>
            </div>
          </div>
          <!-- 关闭内容容器 -->
          <div class="close-wrapper">
            <span class="icon-close" @click="closeBulletin"></span>
          </div>
        </div>
      </div>
    </transition>
    <!-- 公告详情 结束 -->
  </div>
</template>

<script>
  import Star from '@/components/star/Star';
  import MapPosition from '@/components/MapPosition'
  import { mapActions,mapState } from "vuex";
  
  export default {
    data(){
      return {
        isShow:false,
        isPosition:false,
        name:"游客",
        poiInfo:{},
        avatar:'http://momshop-media-hk.oss-cn-hongkong.aliyuncs.com/momshop-image-temp/15483326259653F.jpg',
        mapSite:{
          province:'定位..',
          resultCode:1
        },
        mapOpen:false,
        showCodeErrorDialog:false
      }
    },
    watch:{
      mapSite(mapSite){
        this.$vux.alert.show({title: '提示',content: mapSite})
        if(mapSite.resultCode == 0){
          let that = this;
          dd.biz.map.locate({
              latitude: mapSite.latitude, // 纬度，非必须
              longitude: mapSite.longitude, // 经度，非必须
              onSuccess: function (result) {
                that.mapSite = result;
                that.$vux.alert.show({title: '提示',content: result})
                let place =result.province;
                that.$api.DingTalk.update_work_place(place, result1 => {
                  that._loginSuccess();
                });
              },
              onFail: function (err) {
                that.result = {error:'失败',resultCode:1}
              }
          });
        }
      }
    },
    components:{
      "app-star":Star,
      "app-map-position":MapPosition
    },
    computed:{
      ...mapState({
        orderedMeal: state => state.orderedMeal,
        orderMeals: state => state.orderMeals,
        userProvince: state => state.userProvince,
        isFirstLogin: state => state.isFirstLogin
      }),
      head_pic_url(){
        return "background-image: url(" + this.poiInfo.head_pic_url + ");"
      },
      head_bg(){
        return "background-image: url(" + this.avatar + ");"
      },
      detail_bg(){
        return "background-image: url(" + this.poiInfo.poi_back_pic_url + ");"
      }
    },
    methods:{
      ...mapActions(["setDingTalkId","commitMeal","cancelMeal","initOrderInfo","updateUserProvince","loginSuccess"]),
      showBulletin(){
        this.isShow = true
      },
      search(){
        let message = this.$message;
        this.$vux.alert.show({title: '系统提示',content: '功能未开放'})
      },
      position(){
        this.isPosition = true;
      },
      _loginSuccess(){
        this.loginSuccess();
        this.updateUserProvince(this.mapSite.province);
      },
      closeBulletin(){
        this.isShow = false;
      },
      closePosition(){
        this.isPosition = false;
      },
      _setDingTalkId(dingTalkId){
        this.setDingTalkId(dingTalkId)
      },
      _updateStatus(meal){
        if(meal){
         this.commitMeal();
        }else{
         this.cancelMeal();
        }
      },
      _initOrderInfo(food){
        this.initOrderInfo(food);
      },
     async _site(){
        let message = this.$message;
        let that = this;
        dd.device.geolocation.get({
          targetAccuracy : 50,
          coordinate : 1,
          withReGeocode : false,
          useCache:true, 
          onSuccess : function(result2) {
            that.mapSite = result2;
          },
          onFail : function(err) {
            message({type: "error", message: 'geolocation ... '+JSON.stringify(err)});
          }
        }); 
      },
     async _load(){
        let message = this.$message;
        let api = this.$api;
        let that = this;
        /* eslint-disable */
        let val =  this.value;
        dd.runtime.permission.requestAuthCode({
          corpId : "ding61279fcb45e99b9735c2f4657eb6378f",
          onSuccess : function(res) {
            api.DingTalk.auth(res.code, result => {
              that.name = result.name;
              that.avatar = result.avatar;
              let isPosition = result.is_position;
              that._setDingTalkId(result.user_id);
              if(result.meal && !that.orderedMeal){
                that.cancelMeal();
                result.order_info.forEach(element => {
                  that._initOrderInfo(element);
                });
              }
              that._updateStatus(result.meal);
              
              that.updateUserProvince(result.province);
              if(isPosition  && !that.mapOpen && that.isFirstLogin){          
                that.$vux.alert.show({title: '系统引导',content: '请先设置您的位置'})
                //that._site();
                that.position();
              }
            });
          },
          onFail : function(err) {
            message({type: "error", message: "加载失败"});
          }
        });
      }
    },
    mounted(){
      let that = this;
      that.mapSite.province = this.userProvince;
      //console.log(this.userProvince)
      let message = this.$message;
      if(this.mapOpen){
        let url = window.location.host;
        /* eslint-disable */
        this.$api.DingTalk.sign(url,res => {
          dd.config(res);
          dd.error(function(err){
              message({type: "error", message: '钉钉config失败：'+JSON.stringify(err)});
          });
          dd.ready(function() {
            that._load();
            that._site();
          });
        }) 
      }else{
        //this.$vux.alert.show({title: '系统引导',content: this.isFirstLogin})
        //this.isFirstLogin = true;
        if(this.isFirstLogin){
          let url = window.location.host;
        /* eslint-disable */
          this.$api.DingTalk.sign(url,res => {
            dd.config(res);
            dd.error(function(err){
                message({type: "error", message: '钉钉config失败：'+JSON.stringify(err)});
            });
            dd.ready(function() {
              that._load();
            });
          }) 
        }else{
          dd.ready(function() {
            that._load();
          });
        }
      }
    },
    created() {
      this.$api.DingCan.header(result => {
        this.poiInfo = result.data;
      });
    }
  }
</script>

<style scoped>

  @import url(../../common/css/icon.css);

  .header{
    height: 130px;
    padding-top: 20px;
  }

  /* 顶部通栏样式 */
  .header .top-wrapper {
    position: relative;
  }

  .header .top-wrapper .back-wrapper {
    width: 50px;
    height: 31px;
    position: absolute;
    left: 0;
    top: 0;
    text-align: center;
    line-height: 31px;
  }

  .header .top-wrapper .back-wrapper span {
    display: inline-block;
    color: white;
  }

  .header .top-wrapper .search-wrapper {
    width: 100%;
    height: 31px;
    /* background: pink; */
    padding: 0 104px 0 50px;
    /* 设置盒子从边框开始计算*/
    box-sizing: border-box;
  }

  .header .top-wrapper .search-wrapper .search-icon {
    width: 28px;
    height: 31px;
    background: url(./img/search.png) no-repeat 11px center;
    background-size: 13px 13px;
    position: absolute;
  }

  .header .top-wrapper .search-wrapper .search-bar {
    width: 100%;
    height: 31px;
    border: 0;
    /* 设置盒子从边框开始计算*/
    box-sizing: border-box;
    background: #cdcdcc;
    border-radius: 25px;
    padding-left: 28px;
    /* 去除选中时蓝色边框*/
    outline: none;
  }

  .header .top-wrapper .more-wrapper {
    width: 65px;
    height: 24px;
    /* background: orange; */
    position: absolute;
    right: 0;
    top: 0;
    padding: 7px 15px 0 24px;
  }

  .header .top-wrapper .more-wrapper .spelling-bt {
    width: 45px;
    height: 17px;
    color: white;
    line-height: 17px;
    border: 1px solid white;
    text-align: center;
    float: left;
    text-decoration: none;
    font-size: 10px;
  }

  .header .top-wrapper .more-wrapper .more-bt {
    float: right;
    width: 20px;
    height: 24px;
    margin-left: 13px;
    margin-top: 6px;
  }

  .header .top-wrapper .more-wrapper .more-bt .s-radius {
    width: 3px;
    height: 3px;
    border-radius: 50%;
    border: 1px solid white;
    display: block;
    float: left;
    margin-right: 1px;
  }

  /* 背景图片样式 */
  .header .bg-wrapper {
    width: 100%;
    height: 150px;
    position: absolute;
    left: 0;
    top: 0;
    z-index: -1;
    background-size: 100% 135%;
    background-position: center -12px;
  }

  /* 主题内容 样式 */
  .header .content-wrapper {
    padding: 17px 10px 11px;
    height: 50px;
  }

  .header .content-wrapper .icon {
    width: 50px;
    height: 50px;
    background-size: 100% 100%;
    background-position: center;
    border-radius: 5px;
    float: left;
  }

  .header .content-wrapper .name {
    float: left;
    padding: 18px 0 0 12px;
  }

  .header .content-wrapper .name h3 {
    font-size: 16px;
    font-weight: bold;
    color: white;
  }

  .header .content-wrapper .collect {
    width: 25px;
    height: 37px;
    float: right;
    text-align: center;
    padding-top: 6px;
  }

  .header .content-wrapper .collect text {
    width: 200px;
    height: 200px;
  }

  .header .content-wrapper .collect span {
    margin-top: 7px;
    color: white;
    font-size: 11px;
  }

  /* 公告内容样式 */
  .header .bulletin-wrapper {
    height: 16px;
    padding: 0 10px;
  }

  .header .bulletin-wrapper .icon {
    width: 30px;
    height: 16px;
    float: left;
    margin-right: 6px;
  }

  .header .bulletin-wrapper .text {
    font-size: 11px;
    color: white;
    float: left;
    line-height: 16px;
  }

  .header .bulletin-wrapper .detail {
    color: white;
    float: right;
    font-size: 11px;
    line-height: 16px;
  }

  .header .bulletin-wrapper .detail span {
    font-size: 16px;
    line-height: 16px;
    float: right;
  }

  /* 公告详情 样式 */
  .header .bulletin-detail {
    width: 100%;
    height: 100%;
    position: absolute;
    left: 0;
    top: 0;
    background: rgba(98, 98, 98, 0.8);
    z-index: 999;
  }

  .header .bulletin-detail .detail-wrapper {
    width: 100%;
    height: 100%;
    padding: 43px 20px 125px;
    box-sizing: border-box;
  }

  .header .bulletin-detail .detail-wrapper .main-wrapper {
    width: 100%;
    height: 60%;
    background-size: 100% 100%;
    border-radius: 10px;
    text-align: center;
  }

  .header .bulletin-detail .detail-wrapper .main-wrapper .icon {
    width: 60px;
    height: 60px;
    background-size: 100% 100%;
    background-position: center;
    border-radius: 5px;
    display: inline-block;
    margin-top: 40px;
  }

  .header .bulletin-detail .detail-wrapper .main-wrapper .name {
    font-size: 15px;
    color: white;
    margin-top: 13px;
  }

  .header .bulletin-detail .detail-wrapper .main-wrapper .score {
    height: 10px;
    margin-top: 6px;
    text-align: center;
    font-size: 0;
  }

  .header .bulletin-detail .detail-wrapper .main-wrapper .score .star {
    display: inline-block;
    margin-right: 7px;
  }

  .header .bulletin-detail .detail-wrapper .main-wrapper .score span {
    display: inline-block;
    font-size: 10px;
    color: white;
  }

  .header .bulletin-detail .detail-wrapper .main-wrapper .tip {
    font-size: 11px;
    color: #bababc;
    margin-top: 8px;
  }

  .header .bulletin-detail .detail-wrapper .main-wrapper .tip i {
    margin: 0 7px;
  }

  .header .bulletin-detail .detail-wrapper .main-wrapper .time {
    font-size: 11px;
    color: #bababc;
    margin-top: 13px;
  }

  .header .bulletin-detail .detail-wrapper .main-wrapper .discounts {
    margin-top: 20px;
    padding: 0 20px;
  }

  .header .bulletin-detail .detail-wrapper .main-wrapper .discounts p {
    padding-top: 20px;
    border-top: 1px solid #BABABC;
  }

  .header .bulletin-detail .detail-wrapper .main-wrapper .discounts img {
    width: 30px;
    height: 16px;
    vertical-align: middle;
  }

  .header .bulletin-detail .detail-wrapper .main-wrapper .discounts span {
    font-size: 11px;
    line-height: 16px;
    color: white;
  }

  .header .bulletin-detail .detail-wrapper .close-wrapper {
    padding-top: 20px;
    height: 40px;
    text-align: center;
  }

  .header .bulletin-detail .detail-wrapper .close-wrapper span {
    width: 40px;
    height: 40px;
    line-height: 40px;
    border-radius: 50%;
    font-size: 14px;
    color: white;
    display: inline-block;
    background: rgba(118, 118, 118, 0.7);
    border: 1px solid rgba(140, 140, 140, 0.9);
  }

  /* 动画效果 */
  .bulletin-detail-enter-active,
  .bulletin-detail-leave-active {
    transition: 2s all;
  }

  .bulletin-detail-enter,
  .bulletin-detail-leave-to {
    opacity: 0;
  }

  .bulletin-detail-enter-to,
  .bulletin-detail-leave {
    opacity: 1;
  }

</style>
