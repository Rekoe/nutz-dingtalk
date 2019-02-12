var path = require('path')
var utils = require('./utils')
var config = require('../config')
var vueLoaderConfig = require('./vue-loader.conf')
const webpack = require('webpack')
const vuxLoader = require('vux-loader')
function resolve(dir) {
	return path.join(__dirname, '..', dir)
}

const webpackConfig = module.exports = {
	externals : {
		'vue' : 'Vue',
		'vue-router' : 'VueRouter',
		'vuex' : 'Vuex',
		'axios' : 'axios',
		'element-ui' : 'ELEMENT'
	},
	entry : {
		app : './src/main.js'
	},
	output : {
		path : config.build.assetsRoot,
		filename : '[name].js',
		publicPath : process.env.NODE_ENV === 'production' ? config.build.assetsPublicPath
				: config.dev.assetsPublicPath
	},
	resolve : {
		extensions : [ '.js', '.vue', '.json' ],
		alias : {
			'vue$' : 'vue/dist/vue.esm.js',
			'@' : resolve('src'),
			'scss_vars' : '@/styles/vars.scss'
		}
	},
	module : {
		rules : [ {
			test : /\.vue$/,
			loader : 'vue-loader',
			options : vueLoaderConfig
		}, {
			test : /\.js$/,
			loader : 'babel-loader',
			include : [ resolve('src'), resolve('test') ]
		}, {
			test : /\.(png|jpe?g|gif|svg)(\?.*)?$/,
			loader : 'url-loader',
			exclude : [ resolve('src/icons') ],
			options : {
				limit : 10000,
				name : utils.assetsPath('img/[name].[hash:7].[ext]')
			}
		}, {
			test : /\.svg$/,
			loader : 'svg-sprite-loader',
			include : [ resolve('src/icons') ],
			options : {
				symbolId : 'icon-[name]'
			}
		}, {
			test : /\.(woff2?|eot|ttf|otf)(\?.*)?$/,
			loader : 'url-loader',
			options : {
				limit : 10000,
				name : utils.assetsPath('fonts/[name].[hash:7].[ext]')
			}
		} ]
	},
	plugins : [ new webpack.ProvidePlugin({
		$ : "jquery",
		jQuery : "jquery",
		'window.jQuery' : 'jquery'
	}) ]
}

module.exports = vuxLoader.merge(webpackConfig, { plugins: ['vux-ui'] })