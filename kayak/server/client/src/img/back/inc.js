// Generated by CoffeeScript 1.10.0
(function() {
  var file, fs, i, j, len, ref;

  fs = require('fs');

  i = 0;

  ref = fs.readdirSync('.');
  for (j = 0, len = ref.length; j < len; j++) {
    file = ref[j];
    if (file !== 'inc.coffee' && file !== 'inc.js') {
      fs.renameSync(file, (i++) + ".jpg");
    }
  }

}).call(this);
