export enum NodeType{
    Attribute,
    Function
}

export abstract class Node {
    name: string;
    children: Node[];
    type : NodeType;
    constructor(name: string) { 
        this.name = name;
        this.children = [];
    }
};

export class FunctionNode extends Node {
    constructor(name: string) {
        super(name);
        this.type = NodeType.Function;
    }; 
};

export class AttributeNode extends Node {
    constructor(name: string) {
        super(name);
        this.type = NodeType.Attribute;
    }; 
}   

function getType(token) {
    return token[token.length - 1] === ")" ? NodeType.Function : NodeType.Attribute;
}

function tokenizeAngularExpression(expr) {
    let attrs = [];
    let buffer = "";
    let depth = 0;
    for(let i = 0; i < expr.length ; ++i) {
        let c = expr[i];
        if(c === "(") depth--;
        if(c === ")") depth++;
        if(c === "." && buffer !== "" && depth == 0) {
            attrs.push(buffer);
            buffer = "";
        } else {
            buffer += c;
        }
    }

    if(buffer.length > 0) attrs.push(buffer);
    
    return attrs;
}

function tokenizeFunction(elem) {
    let i = elem.indexOf("(");
    let name = elem.substring(0, i);
    let content = elem.substring(i + 1, elem.length - 1);

    return {
        name: name,
        args : (content || "").split(",").filter((c => c.length > 0))
    }

}
export function getAngularTree(expression  : string , root = new AttributeNode("root")){
    var tokens = tokenizeAngularExpression(expression);
    var discard = false;
    var parent = root;
    tokens.forEach( t => {
        var type = getType(t)
      
        // After a function call we can't infer the type of the returned object
        if(!discard){
            if(type ==NodeType.Function){
                var fct = tokenizeFunction(t)
                var nodeFct = new FunctionNode(fct.name);
                parent.children.push(nodeFct)
                fct.args.forEach(e => {getAngularTree(e , root)});
            }
            else{
                var n = new AttributeNode(t)
                parent.children.push(n)
                parent = n;


            }
        }
        else if(type == NodeType.Function) {
            let fct = tokenizeFunction(t);
            fct.args.forEach(e => { getAngularTree(e , root); });
        }

        if(type === NodeType.Function){
            discard = true;
        }
    });

    return root;
}




function nodeToDotLabel(node : Node){
    var res = node.name
    if(node.constructor.name =="FunctionNode"){
        res+="()"
    }
    return res;
}
export function jsNodeToDep(n : Node){
    var res = ""
    n.children.forEach(n1 =>{
        
        res+="\""+(nodeToDotLabel(n))+"\"->\""+(nodeToDotLabel(n1))+"\"\n"
        res+=jsNodeToDep(n1)
    });
    return res;
}

export function jsNodeToDotGraph(n : Node){
    return "digraph "+n.name+" {\n"+jsNodeToDep(n)+"\n}";
}

