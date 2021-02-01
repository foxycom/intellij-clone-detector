package com.github.foxycom.clone_detector.parser;

import java.util.Hashtable;
import java.util.List;
import java.util.Stack;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TypeLiteral;

public class MethodParser {

  MethodVector mv;

  TokenList methodReservedWordTokenList;
  TokenList methodMarkerTokenList;
  TokenList methodOperatorTokenList;
  TokenList methodTypeTokenList;
  TokenList methodLiteralTokenList;
  TokenList methodVariableTokenList;
  TokenList methodFunctionNameTokenList;
  TokenList methodQualifiedNameTokenList;
  boolean methodEntered;
  Stack<MethodVector> methodVectorStack;
  ASTParser astParser;

  public MethodParser() {
    astParser = ASTParser.newParser(AST.JLS_Latest);
    astParser.setResolveBindings(true);
    astParser.setKind(ASTParser.K_COMPILATION_UNIT);

    Hashtable<String, String> options = JavaCore.getOptions();
    options.put(JavaCore.COMPILER_SOURCE, "1.8");
    astParser.setCompilerOptions(options);
  }

  public void clear() {
    methodEntered = false;

    methodVectorStack = new Stack<>();
  }

  public MethodVector parse(String classDefinition, String methodName) {
    clear();

    astParser.setSource(classDefinition.toCharArray());
    final CompilationUnit cu = (CompilationUnit) astParser.createAST(null);

    cu.accept(new ASTVisitor() {
      @Override
      public boolean visit(MethodDeclaration method) {
        String identifier = method.getName().getIdentifier();
        if (!identifier.equals(methodName) || method.getBody() == null) {
          return false;
        }

        methodEntered = true;

        int startLineNum = cu.getLineNumber(method.getBody().getStartPosition());
        int endLineNum = cu.getLineNumber(
            method.getBody().getStartPosition() + method.getBody().getLength());
        String body = method.getBody().toString();
        MethodTokenParserTool methodTokenParser = new MethodTokenParserTool();

        methodTypeTokenList = new TokenList();
        methodLiteralTokenList = new TokenList();
        methodVariableTokenList = new TokenList();
        methodFunctionNameTokenList = new TokenList();
        methodQualifiedNameTokenList = new TokenList();

        methodReservedWordTokenList = methodTokenParser.getReservedWordTokenList(body);
        methodOperatorTokenList = methodTokenParser.getOperatorTokenList(body);
        methodMarkerTokenList = methodTokenParser.getMarkerTokenList(body);

        MethodVector methodVector = new MethodVector("SomeClass", startLineNum, endLineNum,
            methodReservedWordTokenList, methodTypeTokenList, methodLiteralTokenList,
            methodVariableTokenList, methodFunctionNameTokenList,
            methodQualifiedNameTokenList, methodOperatorTokenList, methodMarkerTokenList);
        methodVectorStack.push(methodVector);
        mv = methodVector;

        method.getBody().accept(this);

        return false;
      }

      //////////////////////////////////////////////////////
      // Add Literals into LiteralTokenList
      //////////////////////////////////////////////////////
      @Override
      public boolean visit(BooleanLiteral node) {
        if (methodEntered) {
          addTokenList(methodLiteralTokenList, node.toString());
        }
        return false;
      }

      @Override
      public boolean visit(CharacterLiteral node) {
        if (methodEntered) {
          addTokenList(methodLiteralTokenList, node.toString());
        }
        return false;
      }

      @Override
      public boolean visit(NullLiteral node) {
        if (methodEntered) {
          addTokenList(methodLiteralTokenList, node.toString());
        }
        return false;
      }

      @Override
      public boolean visit(NumberLiteral node) {
        if (methodEntered) {
          addTokenList(methodLiteralTokenList, node.toString());
        }
        return false;
      }

      @Override
      public boolean visit(StringLiteral node) {
        if (methodEntered) {
          addTokenList(methodLiteralTokenList, node.toString());
        }
        return false;
      }

      @Override
      public boolean visit(TypeLiteral node) {
        if (methodEntered) {
          addTokenList(methodLiteralTokenList, node.toString());
        }
        return false;
      }

      //////////////////////////////////////////////////////
      // Add Variables into VariableTokenList
      //////////////////////////////////////////////////////
      @Override
      public boolean visit(SimpleName node) {
        if (methodEntered) {
          addTokenList(methodVariableTokenList, node.toString());
        }
        return false;
      }

      //////////////////////////////////////////////////////
      // Add FunctionName into FunctionNameTokenList
      //////////////////////////////////////////////////////
      @Override
      public boolean visit(MethodInvocation node) {
        if (methodEntered) {
          addTokenList(methodFunctionNameTokenList, node.getName().toString());
          if (node.getExpression() != null) {
            node.getExpression().accept(this);
          }
          if (node.arguments().size() > 0) {
            List<ASTNode> list = node.arguments();
            for (ASTNode nodeInList : list) {
              nodeInList.accept(this);
            }
          }
        }
        return false;
      }

      //////////////////////////////////////////////////////
      // Add QualifiedName into QualifiedNameTokenList
      //////////////////////////////////////////////////////
      @Override
      public boolean visit(QualifiedName node) {
        if (methodEntered) {
          addTokenList(methodQualifiedNameTokenList, node.toString());
        }
        return false;
      }

      //////////////////////////////////////////////////////
      // Add Type into TypeTokenList
      //////////////////////////////////////////////////////
      @Override
      public boolean visit(SimpleType node) {
        if (methodEntered) {
          addTokenList(methodTypeTokenList, node.toString());
        }
        return false;
      }

      @Override
      public boolean visit(QualifiedType node) {
        if (methodEntered) {
          addTokenList(methodTypeTokenList, node.getQualifier().toString());
        }
        return false;
      }

      @Override
      public boolean visit(PrimitiveType node) {
        if (methodEntered) {
          addTokenList(methodTypeTokenList, node.toString());
        }
        return false;
      }

      @Override
      public void endVisit(MethodDeclaration node) {

        MethodVector methodVector;
        if (!methodVectorStack.empty()) {
          methodVector = methodVectorStack.pop();
          if (methodVector.endLineNumber - methodVector.startLineNumber + 1 >= 6) {
            methodVector.methodReservedWordTokenList.sortByName();
            methodVector.methodTypeTokenList.sortByName();
            methodVector.methodLiteralTokenList.sortByName();
            methodVector.methodVariableTokenList.sortByName();
            methodVector.methodFunctionNameTokenList.sortByName();
            methodVector.methodQualifiedNameTokenList.sortByName();
            methodVector.methodOperatorTokenList.sortByName();
            methodVector.methodMarkerTokenList.sortByName();
          }
        }

        if (!methodVectorStack.empty()) {
          methodVector = methodVectorStack.peek();
          methodVector.methodTypeTokenList.addTokenList(methodTypeTokenList);
          methodTypeTokenList = methodVector.methodTypeTokenList;
          methodVector.methodLiteralTokenList.addTokenList(methodLiteralTokenList);
          methodLiteralTokenList = methodVector.methodLiteralTokenList;
          methodVector.methodVariableTokenList.addTokenList(methodVariableTokenList);
          methodVariableTokenList = methodVector.methodVariableTokenList;
          methodVector.methodFunctionNameTokenList.addTokenList(methodFunctionNameTokenList);
          methodFunctionNameTokenList = methodVector.methodFunctionNameTokenList;
          methodVector.methodQualifiedNameTokenList.addTokenList(methodQualifiedNameTokenList);
          methodQualifiedNameTokenList = methodVector.methodQualifiedNameTokenList;
        } else {
          methodEntered = false;
        }
      }

    });

    return mv;
  }

  public void addTokenList(TokenList tokenList, String str) {
    int index;
    index = tokenList.getIndexByName(str);
    if (index != -1) {
      tokenList.setValueByIndex(index);
    } else {
      TokenVector tokenVector = new TokenVector(str);
      tokenList.addTokenVector(tokenVector);
    }
  }

  public void removeTokenList(TokenList tokenList, String str) {
    int index;
    index = tokenList.getIndexByName(str);
    if (index != -1) {
      tokenList.setValueByIndex(index);
    } else {
      TokenVector tokenVector = new TokenVector(str);
      tokenList.addTokenVector(tokenVector);
    }
  }
}
