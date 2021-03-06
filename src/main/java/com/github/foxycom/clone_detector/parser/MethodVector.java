package com.github.foxycom.clone_detector.parser;

public class MethodVector {

  // variables
  String methodIdentifier;
  String classIdentifier;
  int startLineNumber;
  int endLineNumber;
  TokenList methodReservedWordTokenList;
  TokenList methodTypeTokenList;
  TokenList methodLiteralTokenList;
  TokenList methodVariableTokenList;
  TokenList methodFunctionNameTokenList;
  TokenList methodQualifiedNameTokenList;
  TokenList methodOperatorTokenList;
  TokenList methodMarkerTokenList;


  // constructor
  MethodVector(int start, int end,
      TokenList r_List, TokenList t_List, TokenList l_List, TokenList v_List4,
      TokenList f_List, TokenList q_List, TokenList o_List, TokenList m_List) {
    this.startLineNumber = start;
    this.endLineNumber = end;
    this.methodReservedWordTokenList = r_List;
    this.methodTypeTokenList = t_List;
    this.methodLiteralTokenList = l_List;
    this.methodVariableTokenList = v_List4;
    this.methodFunctionNameTokenList = f_List;
    this.methodQualifiedNameTokenList = q_List;
    this.methodOperatorTokenList = o_List;
    this.methodMarkerTokenList = m_List;
  }

  // print
  public void print() {
    System.out.println("File name: " + classIdentifier);
    System.out.println("Start #: " + startLineNumber);
    System.out.println("End #: " + endLineNumber);

    System.out.println("ReservedWord Token:");
    methodReservedWordTokenList.print();
    System.out.println("--------------------------------------");

    System.out.println("Type Token:");
    methodTypeTokenList.print();
    System.out.println("--------------------------------------");

    System.out.println("Literal Token:");
    methodLiteralTokenList.print();
    System.out.println("--------------------------------------");

    System.out.println("Variable Token:");
    methodVariableTokenList.print();
    System.out.println("--------------------------------------");

    System.out.println("FunctionName Token:");
    methodFunctionNameTokenList.print();
    System.out.println("--------------------------------------");

    System.out.println("QualifiednName Token:");
    methodQualifiedNameTokenList.print();
    System.out.println("--------------------------------------");

    System.out.println("Operator Token:");
    methodOperatorTokenList.print();
    System.out.println("--------------------------------------");

    System.out.println("Marker Token:");
    methodMarkerTokenList.print();
    System.out.println("--------------------------------------");

    System.out.println();
  }
}
