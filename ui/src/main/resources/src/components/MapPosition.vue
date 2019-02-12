<template>
<transition name="map-position">
    <div ref="mapView" class="header">
        <p class="tip" v-show="result.resultCode==1">{{result.province}}-{{result.adName}}-{{result.snippet}}</p>
            <div class="discounts">
                <el-button type="primary" size="small" @click="location" :loading="loading">获取定位</el-button>
                <!--el-button type="default" size="mini" @click="startPosition" :loading="loading1" round>开始定位</el-button>
                <el-button type="default" size="mini" @click="stopPosition" :loading="loading2" round>结束定位</el-button-->
            </div>
    </div>
</transition>
</template>

<script>
import { mapState,mapActions } from "vuex";
export default {
    data(){
        return {
            result:{
                province:this.mapSite.province,
                resultCode:2,
                adName:'未知',
                snippet:'未知',
            },
            flag:true,
            loading:false,
            loading1:false,
            loading2:false
        }
    },
    watch:{
        result(result){
            if(result.resultCode == 0){
                let that = this;
                that._view(result.latitude,result.longitude);
            }
        }
    },
    props: {
      mapSite:{
        type: Object,
        default(){
            return{
                province:'定位..',
                resultCode:1
            }
        }
      },
      poiInfo:{},
      avatar:''
    },
    methods:{
        ...mapActions(["updateUserProvince","loginSuccess"]),
        _view(latitude,longitude){
            let that = this;
            dd.biz.map.locate({
                latitude: latitude, // 纬度，非必须
                longitude: longitude, // 经度，非必须
                onSuccess: function (result) {
                    that.result = result;
                    that.result.resultCode=1;
                    that.flag = false;
                    that.mapSite.province = result.province;
                    that.mapSite.resultCode = result.resultCode;
                    let place =result.province;
                    that.$api.DingTalk.update_work_place(place, result1 => {
                        that._loginSuccess();
                    });
                    that.updateUserProvince(place);
                },
                onFail: function (err) {
                    that.result = {error:'失败',resultCode:1,'province':'定位..'}
                }
            });
        },
       async location(){
            let message = this.$message;
            let url = window.location.host;
            let that = this;
            this.$api.DingTalk.sign(url,res => {
                dd.config(res);
                dd.error(function(err){
                    message({type: "error", message: '钉钉config失败：'+JSON.stringify(err)});
                })
                dd.ready(function() {
                    that.loading = true;
                    dd.device.geolocation.get({
                        targetAccuracy : 50,
                        coordinate : 1,
                        withReGeocode : false,
                        useCache:true, //默认是true，如果需要频繁获取地理位置，请设置false
                        onSuccess : function(result2) {
                            that.result = result2;
                            that.loading = false;
                        },
                        onFail : function(err) {
                            message({type: "error", message: 'geolocation ... '+JSON.stringify(err)});
                        }
                    }); 
                });
            });
        },
        site(){
            console.log('map poisition .... ');
            return "定位"
        },
        _loginSuccess(){
            this.loginSuccess();
        },
        async startPosition(){
            let that = this;
            let message = this.$message;
            let url = window.location.host;
            await this.$api.DingTalk.sign(url,res => {
                dd.config(res);
                dd.error(function(err){
                    message({type: "error", message: '钉钉config失败：'+JSON.stringify(err)});
                })
                dd.device.geolocation.start({
                    targetAccuracy : 1, // 期望精确度
                    iOSDistanceFilter: 1, // 变更感知精度(iOS端参数)
                    withReGeocode : true, // 是否返回逆地理信息,默认否
                    callBackInterval : 300, //回传时间间隔，ms
                    sceneId: "home", // 定位场景id,
                    onSuccess : function(result) {
                        that.result = result;
                        that.loading1 = false;
                    },
                    onFail : function(err) {
                        message({type: "error", message: 'start ... '+JSON.stringify(err)});
                    }
                });
            });
        },
        async stopPosition(){
            let that = this;
            let message = this.$message;
            let url = window.location.host;
            await this.$api.DingTalk.sign(url,res => {
                dd.config(res)
                dd.error(function (error) {
                    message({type: "error", message: JSON.stringify(error)});
                });
                dd.device.geolocation.stop({
                    sceneId: 'home', // 需要停止定位场景id
                    onSuccess : function(result) {
                        that.result = result;
                        that.loading2 = false;
                    },
                    onFail : function(err) {
                        message({type: "error", message: JSON.stringify(err)});
                    }
                });
            });
        }
    },
    mounted(){
 
    },
    computed:{
      ...mapState({
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
    }
}
</script>

<style scoped>

  @import url(../common/css/icon.css);

  .header{
    height: 130px;
    padding-top: 20px;
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

  /* 公告内容样式 */
  .header .bulletin-wrapper {
    height: 16px;
    padding: 0 10px;
  }

  .header .bulletin-detail .detail-wrapper .main-wrapper .tip {
    font-size: 11px;
    color: #bababc;
    margin-top: 8px;
  }

  .header .bulletin-detail .detail-wrapper .main-wrapper .discounts {
    margin-top: 20px;
    padding: 0 20px;
  }
</style>

