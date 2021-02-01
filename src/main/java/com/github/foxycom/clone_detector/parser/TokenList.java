package com.github.foxycom.clone_detector.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TokenList {

  // ArrayList of TokenVector
  public ArrayList<TokenVector> tokenList = new ArrayList<>();

  // get tokenVector
  public TokenVector getTokenVector(int index) {
    return tokenList.get(index);
  }

  // add function
  public void addTokenVector(TokenVector tv) {
    tokenList.add(tv);
  }

  // add token list
  public void addTokenList(TokenList tl) {
    int i, j;
    for (i = 0; i < tl.size(); i++) {
      for (j = 0; j < tokenList.size(); j++) {
        if (tokenList.get(j).TokenName.equals(tl.getTokenVector(i).TokenName)) {
          tokenList.get(j).tokenCount += tl.getTokenVector(i).tokenCount;
          break;
        }
      }
      if (j == tokenList.size()) {
        tokenList.add(tl.getTokenVector(i));
      }
    }
  }

  // size
  public int size() {
    return tokenList.size();
  }

  // clear
  public void clear() {
    tokenList.clear();
  }

  // reset tag
  public void resetTag() {
    for (TokenVector tokenVector : tokenList) {
      tokenVector.tokenUniTag = false;
    }
  }

  // get frequency count
  public int getFreqCount() {
    int tmp = 0;
    for (TokenVector tokenVector : tokenList) {
      tmp = tokenVector.tokenCount;
    }
    return tmp;
  }

  // look up with TokenName
  public int getIndexByName(String name) {
    for (int i = 0; i < tokenList.size(); i++) {
      if (tokenList.get(i).TokenName.equals(name)) {
        return i;
      }
    }
    return -1;
  }

  // remove TokenVector
  public void removeVectorByName(String name) {
    if (getIndexByName(name) != -1) {
      tokenList.remove(getIndexByName(name));
    }
  }

  // increase TokenCount
  public void setValueByIndex(int index) {
    tokenList.get(index).tokenCount++;
  }

  // sort ArrayList by TokenName
  public void sortByName() {
    Collections.sort(tokenList, new Comparator<TokenVector>() {
      public int compare(TokenVector arg0, TokenVector arg1) {
        return arg0.TokenName.compareTo(arg1.TokenName);
      }
    });
  }

  // sort ArrayList by TokenCount
  public void sortListByCount() {
    tokenList.sort((arg0, arg1) -> arg1.tokenCount - arg0.tokenCount);
  }

  // print
  public void print() {
    for (TokenVector tokenVector : tokenList) {
      tokenVector.print();
    }
  }
}
