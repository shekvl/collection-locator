const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  publicPath: '/',
  outputDir: '../server/public/client',
  productionSourceMap: false,
  pages: {
    index: {
      entry: './src/main.ts',
      title: 'Collection Locator',
    },
  }
})
