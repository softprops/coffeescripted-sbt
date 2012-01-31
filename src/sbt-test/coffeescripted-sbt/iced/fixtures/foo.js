(function() {
  var iced, search, __iced_k;

  iced = require('iced-coffee-script').iced;
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