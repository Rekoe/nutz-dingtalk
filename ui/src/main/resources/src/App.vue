<template>
  <div id="app">
    <keep-alive>
        <router-view></router-view>
    </keep-alive>
  </div>
</template>

<script>
  export default {
    name: 'app',
    data() {
      return {
        poiInfo: {},
       // commentNum: 0
      }
    },
    components: {
      //"app-header": Header,
      //"app-nav": Nav
    },
    created() {
      console.log('app ....  ')
      //this.$api.DingCan.new_goods(result => {
        //this.poiInfo = result.data.poi_info;
       // console.log(this.poiInfo)
      //});
      //this.$api.DingCan.new_ratings(result => {
       // this.commentNum = result.data.comment_num;
      //});
    }
  }
</script>

<style lang="scss">
body {
    margin: 0px;
        padding: 0px;
        font-family: Helvetica Neue, Helvetica, PingFang SC, Hiragino Sans GB,
        Microsoft YaHei, SimSun, sans-serif;
        font-size: 14px;
        -webkit-font-smoothing: antialiased;
}
#app {
      position: absolute;
        top: 0px;
        bottom: 0px;
        right: 0;
        left: 0;
}
a{
  text-decoration: none;
}
ul{
  margin: 0;padding:0;
}
li{
  list-style: none;
  margin: 0;padding:0;
}
h1,h2,h3,h4,h5,h6{
  margin:0;padding:0;
}

    section .el-row {
        margin-bottom: 10px;
    }

    .el-submenu [class^="fa"] {
        vertical-align: baseline;
        margin-right: 10px;
    }

    .el-menu-item [class^="fa"] {
        vertical-align: baseline;
        margin-right: 10px;
    }

    .toolbar {
        background: #f2f2f2;
        padding: 10px;
        //border:1px solid #dfe6ec;
        margin: 10px 0px;
        .el-form-item {
            margin-bottom: 10px;
        }
    }

    .fade-enter-active,
    .fade-leave-active {
        transition: all 0.2s ease;
    }

    .fade-enter,
    .fade-leave-active {
        opacity: 0;
    }

    .el-dialog__header {
        border-bottom: 1px solid #e0dbdb
    }

    .el-dialog__footer {
        border-top: 1px solid #e0dbdb
    }
</style>
