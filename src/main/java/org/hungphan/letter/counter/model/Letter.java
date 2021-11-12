package org.hungphan.letter.counter.model;

import akka.routing.ConsistentHashingRouter.ConsistentHashable;

public class Letter implements ConsistentHashable {

	private char letter;
	private int numberOfAppearance;
	
	public Letter(char letter, int numberOfAppearance) {
		super();
		this.letter = letter;
		this.numberOfAppearance = numberOfAppearance;
	}

	public char getLetter() {
		return letter;
	}

	public void setLetter(char letter) {
		this.letter = letter;
	}

	public int getNumberOfAppearance() {
		return numberOfAppearance;
	}

	public void setNumberOfAppearance(int numberOfAppearance) {
		this.numberOfAppearance = numberOfAppearance;
	}
	
	public void increaseNumberOfAppearance() {
		numberOfAppearance++;
	}

	@Override
	public Object consistentHashKey() {
		return letter;
	}

}
