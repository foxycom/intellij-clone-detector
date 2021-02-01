package com.github.foxycom.clone_detector.parser;

public class VectorSimilarity {

  public double simTokenReservedWord, simTokenType, simTokenLiteral;
  public double simTokenVariable, simTokenFunctionName, simTokenQualifiedName;
  public double simTokenOperator, simTokenMarker;

  public TokenList tokenList1 = new TokenList();
  public TokenList tokenList2 = new TokenList();

  // calculate the similarity of both TokenLists
  public double tokenListSim(TokenList tList1, TokenList tList2) {

    if ((tList1.size() == 0 && tList2.size() == 0)) {
      return 0.5;
    }
    if ((tList1.getFreqCount() >= tList2.getFreqCount() * 3) || (tList2.getFreqCount()
        >= tList1.getFreqCount() * 3)) {
      return 0;
    }

    int tokenCount1 = 0;
    int tokenCount2 = 0;
    double tokenListDis = 0;

    int pos1 = 0;
    int pos2 = 0;

    while (pos1 != tList1.size() && pos2 != tList2.size()) {
      if (tList1.getTokenVector(pos1).TokenName.compareTo(tList2.getTokenVector(pos2).TokenName)
          == 0) {
        tokenCount1 += tList1.getTokenVector(pos1).tokenCount;
        tokenCount2 += tList2.getTokenVector(pos2).tokenCount;
        tokenListDis += Math
            .abs(tList1.getTokenVector(pos1).tokenCount - tList2.getTokenVector(pos2).tokenCount);
        pos1++;
        pos2++;
      } else if (
          tList1.getTokenVector(pos1).TokenName.compareTo(tList2.getTokenVector(pos2).TokenName)
              > 0) {
        tokenCount2 += tList2.getTokenVector(pos2).tokenCount;
        tokenListDis += tList2.getTokenVector(pos2).tokenCount;
        pos2++;
      } else {
        tokenCount1 += tList1.getTokenVector(pos1).tokenCount;
        tokenListDis += tList1.getTokenVector(pos1).tokenCount;
        pos1++;
      }
    }

    if (pos1 == tList1.size()) {
      while (pos2 != tList2.size()) {
        tokenCount2 += tList2.getTokenVector(pos2).tokenCount;
        tokenListDis += tList2.getTokenVector(pos2).tokenCount;
        pos2++;
      }
    }

    if (pos2 == tList2.size()) {
      while (pos1 != tList1.size()) {
        tokenCount1 += tList1.getTokenVector(pos1).tokenCount;
        tokenListDis += tList1.getTokenVector(pos1).tokenCount;
        pos1++;
      }
    }

    if (tokenCount1 == 0 && tokenCount2 == 0) {
      return 0.5;
    } else {
      return 1 - tokenListDis / (tokenCount1 + tokenCount2);
    }
  }

  public double[] methodVectorSim(MethodVector mVector1, MethodVector mVector2) {

    // calculate token_ReservedWord's similarity
    tokenList1 = mVector1.methodReservedWordTokenList;
    tokenList2 = mVector2.methodReservedWordTokenList;
    simTokenReservedWord = tokenListSim(tokenList1, tokenList2);

    // calculate token_Type's similarity
    tokenList1 = mVector1.methodTypeTokenList;
    tokenList2 = mVector2.methodTypeTokenList;
    simTokenType = tokenListSim(tokenList1, tokenList2);

    // calculate token_Literal's similarity
    tokenList1 = mVector1.methodLiteralTokenList;
    tokenList2 = mVector2.methodLiteralTokenList;
    simTokenLiteral = tokenListSim(tokenList1, tokenList2);

    // calculate token_Variable's similarity
    tokenList1 = mVector1.methodVariableTokenList;
    tokenList2 = mVector2.methodVariableTokenList;
    simTokenVariable = tokenListSim(tokenList1, tokenList2);

    // calculate token_FunctionName's similarity
    tokenList1 = mVector1.methodFunctionNameTokenList;
    tokenList2 = mVector2.methodFunctionNameTokenList;
    simTokenFunctionName = tokenListSim(tokenList1, tokenList2);

    // calculate token_QualifiedName's similarity
    tokenList1 = mVector1.methodQualifiedNameTokenList;
    tokenList2 = mVector2.methodQualifiedNameTokenList;
    simTokenQualifiedName = tokenListSim(tokenList1, tokenList2);

    // calculate token_Operator's similarity
    tokenList1 = mVector1.methodOperatorTokenList;
    tokenList2 = mVector2.methodOperatorTokenList;
    simTokenOperator = tokenListSim(tokenList1, tokenList2);

    // calculate token_Marker's similarity
    tokenList1 = mVector1.methodMarkerTokenList;
    tokenList2 = mVector2.methodMarkerTokenList;
    simTokenMarker = tokenListSim(tokenList1, tokenList2);

    // return the similarity vector between two methods
    return new double[]{
        simTokenReservedWord,
        simTokenType,
        simTokenLiteral,
        simTokenVariable,
        simTokenFunctionName,
        simTokenQualifiedName,
        simTokenOperator,
        simTokenMarker
    };
  }
}
