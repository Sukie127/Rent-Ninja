const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    proxy: {
      '/api': {
        target: 'https://api.rentalninja.link', 
        changeOrigin: true, 
        pathRewrite: {
          '^/api': '', 
        },
      },
    },
  },
});
