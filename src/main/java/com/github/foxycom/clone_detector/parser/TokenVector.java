package com.github.foxycom.clone_detector.parser;

public class TokenVector {

	// variables
	String TokenName;
	int TokenCount;
	boolean tokenUniTag;

	// constructor
	TokenVector(String name) {
		this.TokenName = name;
		this.TokenCount = 1;
		this.tokenUniTag = false;
	}

	// print
	public void print() {
		System.out.printf("%15s%15d\n", this.TokenName, this.TokenCount);
	}
}
