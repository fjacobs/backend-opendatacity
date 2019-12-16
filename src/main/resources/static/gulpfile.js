function defaultTask(cb) {
    browserify().transform("babelify", {presets: ["es2015"]});
    cb();
}

exports.default = defaultTask