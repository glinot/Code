
import *  as js from "./jsParser"

var root = new  js.AttributeNode("Expression");
js.getAngularTree("fonction1(attr1,attr2,attr3.attr31.fonction11()).fonction2()",root)
console.log(js.jsNodeToDotGraph(root))