(function() {
  var iced, parallelSearch, __iced_k,
    __slice = [].slice;

  iced = {
    Deferrals: (function() {

      function _Class(_arg) {
        this.continuation = _arg;
        this.count = 1;
        this.ret = null;
      }

      _Class.prototype._fulfill = function() {
        if (!--this.count) return this.continuation(this.ret);
      };

      _Class.prototype.defer = function(defer_params) {
        var _this = this;
        ++this.count;
        return function() {
          var inner_params, _ref;
          inner_params = 1 <= arguments.length ? __slice.call(arguments, 0) : [];
          if (defer_params != null) {
            if ((_ref = defer_params.assign_fn) != null) {
              _ref.apply(null, inner_params);
            }
          }
          return _this._fulfill();
        };
      };

      return _Class;

    })(),
    findDeferral: function() {
      return null;
    }
  };
  __iced_k = function() {};

  parallelSearch = function(keywords, cb) {
    var i, k, out, ___iced_passed_deferral, __iced_deferrals,
      _this = this;
    ___iced_passed_deferral = iced.findDeferral(arguments);
    out = [];
    (function(__iced_k) {
      var _i, _len;
      __iced_deferrals = new iced.Deferrals(__iced_k, {
        parent: ___iced_passed_deferral,
        funcname: "parallelSearch"
      });
      for (i = _i = 0, _len = keywords.length; _i < _len; i = ++_i) {
        k = keywords[i];
        search(k, __iced_deferrals.defer({
          assign_fn: (function(__slot_1, __slot_2) {
            return function() {
              return __slot_1[__slot_2] = arguments[0];
            };
          })(out, i),
          lineno: 5
        }));
      }
      __iced_deferrals._fulfill();
    })(function() {
      return cb(out);
    });
  };

}).call(this);
