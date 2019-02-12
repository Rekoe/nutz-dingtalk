<template>
    <section>
		<el-row class="panel-group" :gutter="40">
		    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
		      <div class='card-panel' @click="handleSetLineChartData('rollback')">
		        <div class="card-panel-icon-wrapper icon-people">
		          <svg-icon icon-class="peoples" class-name="card-panel-icon" />
		        </div>
		        <div class="card-panel-description">
		          <div class="card-panel-text">RollBack</div>
		        </div>
		      </div>
		    </el-col>
		    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
		      <div class="card-panel" @click="handleSetLineChartData('pushAll')">
		        <div class="card-panel-icon-wrapper icon-message">
		          <svg-icon icon-class="message" class-name="card-panel-icon" />
		        </div>
		        <div class="card-panel-description">
		          <div class="card-panel-text">PushAll</div>
		        </div>
		      </div>
		    </el-col>
		    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
		      <div class="card-panel" @click="handleSetLineChartData('purchases')">
		        <div class="card-panel-icon-wrapper icon-money">
		          <svg-icon icon-class="money" class-name="card-panel-icon" />
		        </div>
		        <div class="card-panel-description">
		          <div class="card-panel-text">Purchases</div>
		        </div>
		      </div>
		    </el-col>
		    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
		      <div class="card-panel" @click="handleSetLineChartData('shoppings')">
		        <div class="card-panel-icon-wrapper icon-shoppingCard">
		          <svg-icon icon-class="shoppingCard" class-name="card-panel-icon" />
		        </div>
		        <div class="card-panel-description">
		          <div class="card-panel-text">Shoppings</div>
		        </div>
		      </div>
		    </el-col>
		  </el-row>
        <el-row>
		  <el-col :span="8" :offset="2">
		    <el-card class="box-card">
		      <div slot="header" class="clearfix">
			    <span>汇率</span>
			    <el-button style="float: right; padding: 3px 0" type="text" @click='hui_lv'>刷新汇率</el-button>
			  </div>
			  <div v-if="huilv.id > 0">
				  <div class="text item">{{huilv.rate}}</div>
				  <div class="text item">{{huilv.ratenm}}</div>
				  <div class="text item">{{huilv.update}}</div>
			  </div>
		    </el-card>
		  </el-col>
		  <el-col :span="8" :offset="2">
		    <el-card class="box-card">
		      <div slot="header" class="clearfix">
			    <span>Analysis</span>
			    <el-button style="float: right; padding: 3px 0" type="text" @click='analysis'>查询</el-button>
			  </div>
			 <div v-if="shopify.id > 0">
				  <div class="text item">总商品:{{shopify.all}}</div>
				  <div class="text item">单属性:{{shopify.one}}</div>
				  <div class="text item">英文翻译:{{shopify.translate}}</div>
				  <div class="text item">混合属性:{{shopify.other}}</div>
				  <div class="text item">已推送:{{shopify.pushed}}</div>
			  </div>		    
			</el-card>
		  </el-col>
		</el-row>
    </section>
</template>
<style>
</style>
<script>
import { mapState, mapGetters, mapMutations } from "vuex";
export default {
	data() {
   		 return {
   		 	searchKey: "",
      		huilv: {
	          id: 0,
	          rate: 0.0,
	          ratenm: "",
	          update: ""
	        },
	        shopify: {
	          id: 0,
	          one: 0,
	          all: 0,
	          other: 0,
	          pushed: 0,
	          translate: 0
	        }
    		};
  },
  computed: {
    ...mapState({
      loginUser: state => state.loginUser
    })
  },
  methods: {
  	hui_lv(){
	  this.$api.Tools.huilv(result => {
	    	this.huilv.id=1;
	   	this.huilv.rate=result.rate;
	    	this.huilv.ratenm=result.ratenm;
	    	this.huilv.update=result.update;
	 });
  },
  handleSetLineChartData(data){
  	console.log(data);
  	if(data == 'rollback'){
  		this.$api.Tools.rollback(result => {
        		this.$message({type: "success", message: "success"});
   	 	});
  	}
  	if(data == 'pushAll'){
  		this.$api.Tools.pushAll(result => {
        		this.$message({type: "success", message: "success"});
   	 	});
  	}
  	if(data == 'shoppings'){
  		this.$api.Tools.login(result => {
  			
        		this.$message({type: "success", message: "success"});
   	 	});
  	}
  },
  analysis(){
  	this.$api.Tools.analysis(result => {
         this.shopify.id=1;
         this.shopify.all=result.all;
         this.shopify.one=result.one;
         this.shopify.other=result.other;
         this.shopify.pushed=result.pushed;
         this.shopify.translate=result.translate;
    	});
  },
  ...mapMutations(["save", "remove"]),
    loadData() {
      this.$api.User.load(result => {
        let loginUser = result.loginUser;
        loginUser.roles = result.roles;
        loginUser.permissions = result.permissions;
        this.save(loginUser);
      });
    }
  },
  created: function() {
    this.loadData();
  }
};
</script>
<style rel="stylesheet/scss" lang="scss" scoped>
.text {
    font-size: 14px;
  }

  .item {
    margin-bottom: 18px;
  }

  .clearfix:before,
  .clearfix:after {
    display: table;
    content: "";
  }
  .clearfix:after {
    clear: both
  }

  .box-card {
    width: 480px;
  }
.panel-group {
  margin-top: 18px;
  .card-panel-col{
    margin-bottom: 32px;
  }
  .card-panel {
    height: 108px;
    cursor: pointer;
    font-size: 12px;
    position: relative;
    overflow: hidden;
    color: #666;
    background: #fff;
    box-shadow: 4px 4px 40px rgba(0, 0, 0, .05);
    border-color: rgba(0, 0, 0, .05);
    &:hover {
      .card-panel-icon-wrapper {
        color: #fff;
      }
      .icon-people {
         background: #40c9c6;
      }
      .icon-message {
        background: #36a3f7;
      }
      .icon-money {
        background: #f4516c;
      }
      .icon-shoppingCard {
        background: #34bfa3
      }
    }
    .icon-people {
      color: #40c9c6;
    }
    .icon-message {
      color: #36a3f7;
    }
    .icon-money {
      color: #f4516c;
    }
    .icon-shoppingCard {
      color: #34bfa3
    }
    .card-panel-icon-wrapper {
      float: left;
      margin: 14px 0 0 14px;
      padding: 16px;
      transition: all 0.38s ease-out;
      border-radius: 6px;
    }
    .card-panel-icon {
      float: left;
      font-size: 48px;
    }
    .card-panel-description {
      float: right;
      font-weight: bold;
      margin: 26px;
      margin-left: 0px;
      .card-panel-text {
        line-height: 18px;
        color: rgba(0, 0, 0, 0.45);
        font-size: 16px;
        margin-bottom: 12px;
      }
      .card-panel-num {
        font-size: 20px;
      }
    }
  }
}
</style>
