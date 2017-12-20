import * as ts from "typescript";
import * as fs from "fs";
import * as js from "./jsParser"
import { readdirSync } from "fs";
import { readFileSync } from "fs";
import * as ex from "./extractor"

function linterComponent(node: ts.Node, componentNode : js.Node) {
   var e = new ex.Extractor(node)
   var exprs = e.getAngularExpressions();
   exprs.forEach(linterAngularExpression);
   return ;
   function linterAngularExpression(expr : ex.AngularExpression){
        checkErrors(expr.node , componentNode);
        return ;
        function checkErrors(partialNode : js.Node,compNode : js.Node){


            if(partialNode && compNode && partialNode.name === compNode.name && partialNode.constructor.name === compNode.constructor.name){
                if( partialNode.type == js.NodeType.Function && compNode.type == js.NodeType.Attribute){
                    console.log("[ERROR]: "+expr.rawString + " is not a function ");
                    return ;
                }
                partialNode.children.forEach( cPn => {
                    var cCn = compNode.children.filter(cc=> cc.name ==  cPn.name).pop()
                    checkErrors(cPn ,cCn);
                });
            }
            if(partialNode && ! compNode ){
                console.log("[ERROR]: " + expr.rawString+ " "+ partialNode.name+" not defined");
            }
            return;

        }

    }

}

function buildObjectTree(node : ts.Symbol , jsNode : js.Node){
    node.members.forEach((value: ts.Symbol, key) => {
        buildJsTreeFromTs(value,jsNode);
    });
}

function buildPropertyTree(node : ts.Symbol , jsNode : js.Node){
    if(node.declarations){
        var declaration = node.declarations[0]
        if(declaration && declaration.initializer){
            var s = declaration.initializer.symbol;
            if( declaration.initializer.symbol){
                if(declaration.initializer.symbol.flags == ts.SymbolFlags.Function){
                    var funcNode = new js.FunctionNode(node.getName())
                    jsNode.children.push(funcNode)
                }
                else if(s.flags == ts.SymbolFlags.ObjectLiteral){
                    var attNode = new js.AttributeNode(node.getName())
                    jsNode.children.push(attNode)
                    buildJsTreeFromTs(s.declarations[0].symbol , attNode)
                }
            }
            else{
                var attNode = new js.AttributeNode(node.getName())
                jsNode.children.push(attNode)
            }
        }
    }
}

function buildFunctionTree(node : ts.Symbol , jsNode : js.Node){
    jsNode.children.push(new js.FunctionNode(node.getName()))
}

function buildJsTreeFromTs(node : ts.Symbol , jsNode : js.Node){
    if(!node)return ;
    if(node.flags == ts.SymbolFlags.Class){
        buildObjectTree(node,jsNode);
    }
    else if (node.flags == ts.SymbolFlags.Property){
        buildPropertyTree(node,jsNode);
    }
    else if (node.flags == ts.SymbolFlags.Function){
        buildFunctionTree(node,jsNode);
    }
    else if (node.flags == ts.SymbolFlags.ObjectLiteral){
        buildObjectTree(node,jsNode);
    }
}

function checkComponent(node : ts.Node)
{
    return (node.decorators  || []).filter(d => d.expression.kind == ts.SyntaxKind.CallExpression).filter((d)=>d.expression.expression.getFullText()==="Component").length == 1;
}

/** Generate documentation for all classes in a set of .ts files */
function parseFiles(fileNames: string[], options: ts.CompilerOptions): void {

    // Build a program using the set of root file names in fileNames
    let program = ts.createProgram(fileNames, options);

    // Get the checker, we will use it to find more about classes
    let checker = program.getTypeChecker();

    // Visit every sourceFile in the program
    for (const sourceFile of program.getSourceFiles()) {
        if (!sourceFile.isDeclarationFile) {
            // Walk the tree to search for classes
            console.log("------------------ " + sourceFile.fileName+ "-----------------");
            ts.forEachChild(sourceFile, visit);
        }
    }
    return;
    /** visit nodes finding exported classes */
    function visit(node: ts.Node) {
        // Only consider exported nodes
        if (!isNodeExported(node)) {
            return;
        }

        if (ts.isClassDeclaration(node) && node.name && checkComponent(node)) {

            // This is a top level class, get its symbol

            let symbol = checker.getSymbolAtLocation(node.name);
            if (symbol) {
                var root = new js.AttributeNode("root")
                buildJsTreeFromTs(symbol,root)
                linterComponent(node,root)
            }
        }
        else if (ts.isModuleDeclaration(node)) {
            // This is a namespace, visit its children
            ts.forEachChild(node, visit);
        }
    }
    /** True if this is visible outside this file, false otherwise */
    function isNodeExported(node: ts.Node): boolean {
        return (ts.getCombinedModifierFlags(node) & ts.ModifierFlags.Export) !== 0 || (!!node.parent && node.parent.kind === ts.SyntaxKind.SourceFile);
    }
}


/* ----- Main  ----- */

var walkSync = function(dir, filelist) {

  var fs = fs || require('fs'),
      files = fs.readdirSync(dir);

  filelist = filelist || [];

  files.forEach(function(file) {

    if(file != "node_modules") {
      if (fs.statSync(dir + file).isDirectory()) filelist = walkSync(dir + file + '/', filelist);
      else filelist.push(dir + file);
    }

  });

  return filelist;

};

// Iterates through every file
parseFiles(walkSync("./test/").filter(filename => filename.split(".").pop() == "ts"), {
    target: ts.ScriptTarget.ES5, module: ts.ModuleKind.CommonJS
})
