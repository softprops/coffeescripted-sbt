(function() {
  var iced, search, __iced_k,
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

  search = function(keyword, cb) {
    var host, json, url, ___iced_passed_deferral, __iced_deferrals,
      _this = this;
    ___iced_passed_deferral = iced.findDeferral(arguments);
    host = "http://search.twitter.com/";
    url = "" + host + "/search.json?q=" + keyword + "&callback=?";
    (function(__iced_k) {
      __iced_deferrals = new iced.Deferrals(__iced_k, {
        parent: ___iced_passed_deferral,
        funcname: "search"
      });
      $.getJSON(url, __iced_deferrals.defer({
        assign_fn: (function() {
          return function() {
            return json = arguments[0];
          };
        })(),
        lineno: 4
      }));
      __iced_deferrals._fulfill();
    })(function() {
      return cb(json.results);
    });
  };

}).call(this);
