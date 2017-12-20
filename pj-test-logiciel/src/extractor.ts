import * as cheerio from "cheerio"
import * as js from "./jsParser"
import * as ts from "typescript"
import {readFileSync} from "fs"

export enum DataType {
  OneWayData,
  OneWayEvent,
  TwoWay
}

export class AngularExpression {
  type: DataType;
  node: js.Node;
  rawString: string;

  constructor(type: DataType, node: js.Node, rawString: string) {
    this.type = type;
    this.rawString = rawString;
    this.node = node;
  }
}

export class Node {
  type: string;
  name: string;
  attributes: any;
  children: Node[];
  text: string;

  constructor(type: string, name: string, attributes: any, children: Node[], text: string) {
    this.type = type;
    this.name = name;
    this.attributes = attributes;
    this.children = children;
    this.text = text;
  }
}

export class Extractor {
  private template: string;

  constructor(node: ts.Node) {
    let template;
    node.decorators.forEach(decorator => {
      if (decorator.expression.kind === ts.SyntaxKind.CallExpression) {
        let callExpression = <ts.CallExpression>decorator.expression;
        if (callExpression.expression.getFullText() === "Component") {
          if (callExpression.arguments && callExpression.arguments.length >= 1) {
            let firstArg = callExpression.arguments[0];
            if (firstArg.kind === ts.SyntaxKind.ObjectLiteralExpression) {
              let object = <ts.ObjectLiteralExpression>firstArg;
              object.properties.forEach(p => {
                if (p.kind === ts.SyntaxKind.PropertyAssignment && (<ts.PropertyAssignment>p).name.getText() == "template")
                  this.template = (<ts.PropertyAssignment>p).initializer.getText();
                else if(p.kind === ts.SyntaxKind.PropertyAssignment && (<ts.PropertyAssignment>p).name.getText() == "templateUrl") {
                  let path: string[] = node.parent.fileName.split('/');
                  path.pop();
                  this.template = readFileSync((<ts.PropertyAssignment>p).initializer.getText().replace('./', path.join('/') + '/').split('\'').join('')).toString();
                }
              });
            }
          }
        }
      }
    });
  }

  private getDataType(attribute: string): DataType {
    let oneWayDataBinding = [
      "*ng",
      "{{",
      "[",
      "bind-target"
    ];
    for (const key in oneWayDataBinding)
      if (attribute.startsWith(oneWayDataBinding[key]))
        return DataType.OneWayData;

    let oneWayEventBinding = [
      "(",
      "on-target"
    ];
    for (const key in oneWayEventBinding)
      if (attribute.startsWith(oneWayEventBinding[key]))
        return DataType.OneWayEvent;

    let twoWayDataBinding = [
      "[(",
      "bindon-target"
    ];
    for (const key in twoWayDataBinding) {
      if (attribute.startsWith(twoWayDataBinding[key]))
        return DataType.TwoWay;
    }

    return null;
  }

  getAttributesTree(): Node {
    let $ = cheerio.load(this.template);
    let tree: Node;

    let recAttributesTree = (object: any, r: any): Node => {
      // Selects the Angular attributes
      let attributes: any = {};
      let replace = r;
      Object.keys(object.attribs || {}).forEach(key => {
        if (this.getDataType(key) !== null) {
          for (let i = 0; i < replace.length; i++)
            object.attribs[key] = object.attribs[key].replace(replace[i][0], replace[i][1] + '[0]');

          if (key == '*ngfor') {
            if (object.attribs[key].startsWith('let'))
              object.attribs[key] = object.attribs[key].replace('let ', '').replace(' of ', ' in ');

            replace.push(object.attribs[key].split(' in '))
          }
          attributes[key] = object.attribs[key];
        }
      });

      // Removes useless node
      let children: Node[] = [];
      (object.children || []).map((child: any) => {
        if (child.hasOwnProperty("data")) {
          let data: String = child.data;
          // Deletes empty node
          if (data.startsWith('\n') || data.startsWith('`') || data.startsWith('\"') || data.startsWith('\''))
            return;
        }

        children.push(recAttributesTree(child, replace));
      })

      let data = object.data;
      if (data)
        for (let i = 0; i < replace.length; i++)
          data = data.replace(replace[i][0], replace[i][1] + '[0]');

      return new Node(
        object.type,
        object.name,
        attributes,
        children,
        data
      );
    }

    // Gets the HTML tag
    $('*').each((key: number, value: any) => {
      if (value.parent === null)
        tree = recAttributesTree(value, []);
    });

    return tree;
  }

  getAngularExpressions(): AngularExpression[] {
    let expressions: AngularExpression[] = [];
    let tree = this.getAttributesTree();

    let recAngularExpressions = (node: Node) => {

      for (const key in node.attributes)
        expressions.push(new AngularExpression(this.getDataType(node.attributes[key]), js.getAngularTree(node.attributes[key]), node.attributes[key]));

      if (node.children)
        node.children.forEach(child => {
          if (child.type == 'text') {
            let matcher = /{{(?: )*([^ ]+)(?: )*}}/g;
            let m;

            while ((m = matcher.exec(child.text)) != null)
              expressions.push(new AngularExpression(DataType.OneWayData, js.getAngularTree(m[1]), m[1]));

          } else
            recAngularExpressions(child);
        });
    };

    recAngularExpressions(tree);

    return expressions;
  }
}
