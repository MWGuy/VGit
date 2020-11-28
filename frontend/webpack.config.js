const isDevelopment = process.env.NODE_ENV === 'development';
const reactScriptsConfig = require('react-scripts/config/webpack.config')(isDevelopment ? 'development' : 'production');
const path = require('path');

const contentBasePath = path.join(__dirname, '..', 'backend', 'src', 'main', 'resources', 'static');

const patchedConfig = Object.assign({}, reactScriptsConfig, {
    output: Object.assign({}, reactScriptsConfig.output, {
        path: contentBasePath
    })
})

const developmentConfig = Object.assign({}, patchedConfig, {
    devServer: {
        contentBase: contentBasePath,
        compress: true,
        historyApiFallback: true,
        port: 9000
    }
})

module.exports = isDevelopment ? developmentConfig : patchedConfig;
