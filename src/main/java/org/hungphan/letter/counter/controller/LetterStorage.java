package org.hungphan.letter.counter.controller;

import java.util.HashMap;
import java.util.Map;

import org.hungphan.letter.counter.model.Letter;

import akka.actor.AbstractActor;
import akka.actor.Props;

public class LetterStorage extends AbstractActor {
	
	private Map<Character, Integer> letterStorage = new HashMap<>();
	
	public static Props props() {
		return Props.create(LetterStorage.class, () -> new LetterStorage());
	}
	
	@Override
	public Receive createReceive() {
		return receiveBuilder().match(Letter.class, letter -> {
			if(letterStorage.containsKey(letter.getLetter())) {
				int newCount = letterStorage.get(letter.getLetter()) + letter.getNumberOfAppearance();
				letterStorage.put(letter.getLetter(), newCount);
			} else {
				letterStorage.put(letter.getLetter(), letter.getNumberOfAppearance());
			}
		}).match(Character.class, msg -> {
            Integer count = letterStorage.get(msg) != null ? letterStorage.get(msg) : 0;
			getSender().tell(count, getSelf());
		}).build();
	}

}
