<template>
  <div class="social-signup-container">
		<span class="social-signup_label">第三方登录:</span>
    <a class="sign-btn" :href ="'/login/googleplus'">
      <span class="wx-svg-container google"><svg-icon icon-class="googleplus" class="icon"></svg-icon></span> Google+
    </a>
		<a class="sign-btn" :href ="'/login/github'">
      <span class="wx-svg-container github"><svg-icon icon-class="github" class="icon"></svg-icon></span> Github
    </a>
		<a class="sign-btn" :href ="'/login/facebook'">
      <span class="wx-svg-container facebook"><svg-icon icon-class="github" class="icon"></svg-icon></span> Facebook
    </a>
  </div>
</template>

<script>
export default {
  methods: {
    googleHandleClick(thirdpart) {
      
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
	.social-signup-container {
		.social-signup_label {
			margin-right: 20px;
			vertical-align: top;
			display: inline-block;
			line-height: 40px;
		}
	  .sign-btn {
	    // display: inline-block;
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
	  .qq-svg-container {
	    background-color: #6BA2D6;
	    margin-left: 50px;
	  }
	}
</style>
