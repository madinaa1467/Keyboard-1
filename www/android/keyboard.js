var argscheck = require('cordova/argscheck'),
  utils = require('cordova/utils'),
  exec = require('cordova/exec'),
  channel = require('cordova/channel');


var Keyboard = function() {
};

Keyboard.hideKeyboardAccessoryBar = function(hide) {
  exec(null, null, "Keyboard", "hideKeyboardAccessoryBar", [hide]);
};

Keyboard.close = function() {
  exec(null, null, "Keyboard", "close", []);
};

Keyboard.show = function() {
  exec(null, null, "Keyboard", "show", []);
};

Keyboard.disableScroll = function(disable) {
  exec(null, null, "Keyboard", "disableScroll", [disable]);
};

/*
Keyboard.styleDark = function(dark) {
 exec(null, null, "Keyboard", "styleDark", [dark]);
};
*/

Keyboard.isVisible = false;

channel.onCordovaReady.subscribe(function() {
  exec(success, null, 'Keyboard', 'init', []);
  function success(msg) {
    var action = msg.charAt(0);
    if ( action === 'S' ) {
      var heights = msg.substr(1).split(";");
      var keyboardHeight = heights[0];
      var nav = 0;
      if(heights[1]) {
        nav = heights[1];
      }
      // var keyboardHeight = msg.substr(1);
      cordova.plugins.Keyboard.isVisible = true;
      cordova.fireWindowEvent('native.keyboardshow', {'keyboardHeight': +keyboardHeight, 'nav': +nav});

      //deprecated
      cordova.fireWindowEvent('native.showkeyboard', {'keyboardHeight': +keyboardHeight, 'nav': +nav});

    } else if (action === 'H') {
      cordova.plugins.Keyboard.isVisible = false;
      cordova.fireWindowEvent('native.keyboardhide');

      //deprecated
      cordova.fireWindowEvent('native.hidekeyboard');
    }
  }
});

module.exports = Keyboard;