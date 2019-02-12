<template>
    <div class="login-wrap">
        <div class="ms-login">
            <el-form :model="loginForm" status-icon :rules="$rules" ref="loginForm" label-width="0px"
                     class="demo-ruleForm">
                <div class="ms-title">OM-PLATFORM</div>
                <el-form-item prop="userName">
                    <el-input v-model="loginForm.userName" placeholder="请输入用户名" suffix-icon="el-icon-fa-user"
                              @keyup.enter.native="submitForm('loginForm')">
                        <template slot="prepend">{{this.$t('user.username')}}</template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input type="password" placeholder="请输入密码" v-model="loginForm.password"
                              suffix-icon="el-icon-fa-lock" @keyup.enter.native="submitForm('loginForm')">
                        <template slot="prepend">{{this.$t('user.password')}}</template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="captcha">
                    <el-input :placeholder="this.$t('user.captcha')" prefix-icon="el-icon-fa-picture-o" v-model="loginForm.captcha"
                              @keyup.enter.native="submitForm('loginForm')">
                        <template slot="append">
                            <img :src="captcha" @click="refreshCaptcha" class="append-img" :title="this.$t('user.captcha_refresh')">
                        </template>
                    </el-input>
                </el-form-item>
                <div class="login-btn" style="margin-bottom:15px">
                    <el-button type="primary" @click="submitForm('loginForm')">{{this.$t('user.login')}}</el-button>
                </div>
                <div class="social-signup-container">
                  <span class="social-signup_label">扫码登陆:</span>
                  <a class="sign-btn" @click ='googleHandleClick'>
                    <span class="wx-svg-container dingtalk"></span> 钉钉
                  </a>
                </div>
            </el-form>
            <div id="box" v-show="showDialog" style="position:relative;top:-340px;left:-33px;">
	            <div id="login_container"></div>
	        </div>
        </div>
        
    </div>
</template>

<script>
import { mapState, mapGetters, mapMutations } from "vuex";
import openWindow from '@/utils/openWindow'
export default {
  components: { },
  data: function() {
    return {
      captcha: baseUrl + "/captcha?length=4&" + Math.random(),
      loginForm: {
        userName: "admin",
        password: "12345678",
        captcha: "",
        rememberMe: true
      },
      showDialog: true
    };
  },
  watch:{
    $route(e){
      console.log(e);
    }
  },
  mounted: function(){
	 	this.d({
	    id: 'login_container',
	    goto: 'https%3a%2f%2foapi.dingtalk.com%2fconnect%2foauth2%2fsns_authorize%3fappid%3ddingoawoyq6g22mupzd6mu%26response_type%3dcode%26scope%3dsnsapi_login%26state%3drk%26redirect_uri%3dhttp%3a%2f%2fmeal.rekoe.com%2foauth%2fdingding%2fcallback',
	    style: "",
	    href: "",
	    width: "300px",
	    height: "300px"
    });
    var hanndleMessage = function(event) {
        var loginTmpCode = event.data;
        var origin = event.origin;
        window.location.href="https://oapi.dingtalk.com/connect/oauth2/sns_authorize?appid=dingoawoyq6g22mupzd6mu&response_type=code&scope=snsapi_login&state=rk&redirect_uri=http%3a%2f%2fmeal.rekoe.com%2foauth%2fdingding%2fcallback&loginTmpCode=" + loginTmpCode
    };
    if (typeof window.addEventListener != 'undefined') {
        window.addEventListener('message', hanndleMessage, false);
    } else if (typeof window.attachEvent != 'undefined') {
        window.attachEvent('onmessage', hanndleMessage);
    }
  },
  computed: {
    ...mapState({
      loginUser: state => state.loginUser
    })
  },
  created() {
    if (this.loginUser.id) {
      this.$router.push({ path: "/dashboard" });
    }
  },
  methods: {
    ...mapMutations(["save", "remove"]),
    d(a) {
        var e, c = document.createElement("iframe"),
            d = "https://login.dingtalk.com/login/qrcode.htm?goto=" + a.goto ;
        d += a.style ? "&style=" + a.style : "",
            d += a.href ? "&href=" + a.href : "",
            c.src = d,
            c.frameBorder = "0",
            c.allowTransparency = "true",
            c.scrolling = "no",
            c.width =  "365px",
            c.height = "300px",
            e = document.getElementById(a.id),
            e.innerHTML = "",
            e.appendChild(c)
    },
    googleHandleClick() {
      //openWindow('/login/googleplus', 'Google+', 540, 540)
      if(this.showDialog){
          this.showDialog = false
      }else{
        this.showDialog = true
      }
    },
    refreshCaptcha() {
      this.captcha = baseUrl + "/captcha?length=4&" + Math.random();
    },
    submitForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.$api.User.login(this.loginForm, data => {
            let loginUser = data.loginUser;
            loginUser.roles = data.roles;
            loginUser.permissions = data.permissions;
            this.save(loginUser);
            this.$router.push({ path: "/dashboard" });
          });
        } else {
          return false;
        }
      });
    }
  }
};
</script>

<style scoped>
.login-wrap {
  position: relative;
  width: 100%;
  height: 100%;
  background-color: #f5f7f9;
  background-image: url(../assets/images/background.png);
  -moz-background-size: 100% 100%;
  background-size: 100% 100%;
}

.append-img {
  height: 39px;
  border-radius: 0 4px 4px 0;
  cursor: pointer;
  margin-left: -20px;
  margin-right: -20px;
  margin-bottom: -5px;
}

.ms-title {
  font-family: -webkit-pictograph;
  text-align: center;
  font-size: 30px;
  color: #018abd;
  padding-bottom: 10px;
}

.ms-login {
  position: absolute;
  left: 50%;
  top: 45%;
  width: 300px;
  height: 300px;
  margin: -150px 0 0 -190px;
  padding: 40px;
  border-radius: 5px;
  background: #fff;
}

.login-btn {
  text-align: center;
}

.login-btn button {
  width: 100%;
  height: 36px;
}
.thirdparty-button{
    position: absolute;
    right: 0px;
    bottom: 0px;
}
.social-signup_label {
  margin-right: 20px;
  vertical-align: top;
  display: inline-block;
  line-height: 40px;
}
.sign-btn {
  /* display: inline-block; */
  cursor: pointer;
  font-size: 0;
  margin-right: 15px;
}
.icon {
  color: #fff;
  font-size: 30px;
  margin-top: 6px;
}
.wx-svg-container,
.qq-svg-container {
  display: inline-block;
  width: 40px;
  height: 40px;
  line-height: 40px;
  text-align: center;
  padding-top: 1px;
  border-radius: 4px;
  margin-right: 5px;
}
.google{
  background: url(../assets/images/google.png) #8dc349 no-repeat;
  background-size: 40px 40px;
}
.github{
  background: url(../assets/images/github.png) #8dc349 no-repeat;
  background-size: 40px 40px;
}
.facebook{
  background: url(../assets/images/facebook.png) #8dc349 no-repeat;
  background-size: 40px 40px;
}
.dingtalk{
  background: url(../assets/images/dingtalk.png) #8dc349 no-repeat;
  background-size: 40px 40px;
}
.qq-svg-container {
  background-color: #6BA2D6;
  margin-left: 50px;
}
</style>
