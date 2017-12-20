fs = require 'fs'
i =0
for file in fs.readdirSync '.' when file isnt 'inc.coffee' and file isnt 'inc.js'
  fs.renameSync file, "#{i++}.jpg"