package com.github.foxycom.clone_detector.parser;

public class TokenVector {

	// variables
	String TokenName;
	int tokenCount;
	boolean tokenUniTag;

	// constructor
	TokenVector(String name) {
		this.TokenName = name;
		this.tokenCount = 1;
		this.tokenUniTag = false;
	}

	// print
	public void print() {
		System.out.printf("%15s%15d\n", this.TokenName, this.tokenCount);
	}
}
