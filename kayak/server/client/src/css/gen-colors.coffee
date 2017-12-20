fs = require 'fs'
colors = ["#5baaff","#ff5c4f","#a5e56e","#ff8426","#27ae60","#34495e","#2C82C9","#EEE657","#E01931","#71BA51","#b85c1f"]
alp = "-abcdefghijklmnopqrstuvwxyz"
genFunc = [
  (col,i) -> ".color-#{i} {background-color : #{col};}",
  (col ,i ) -> ".ct-series-#{alp[i]} .ct-line {stroke: #{col}; stroke-width:4 ;}",
  (col ,i ) -> ".ct-series-#{alp[i]} .ct-point {stroke: #{col};}",
  (col ,i ) -> ".ct-series-#{alp[i]} .ct-slice-pie {fill: #{col};}"
]
for func in genFunc
  for col , i in colors
    fs.appendFileSync "colors-stats.css" , func col , i+1

