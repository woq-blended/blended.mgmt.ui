const path = require('path');

module.exports = {
  entry: '/home/andreas/projects/blended/blended.mgmt.ui/out/blended/mgmt/ui/sampleApp/fastOpt/dest/out.js',
  output: {
    filename: 'main.js',
    path: path.resolve(__dirname, 'dist'),
  },
  devtool: "source-map",
  "module": {
    "rules": [{
      "test": new RegExp("\\.js$"),
      "enforce": "pre",
      "use": ["source-map-loader"]
    }]
  }
};
