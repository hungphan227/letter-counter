package org.hungphan.letter.counter.controller;

import java.util.HashMap;
import java.util.Map;

import org.hungphan.letter.counter.Factory;
import org.hungphan.letter.counter.model.Letter;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.actor.ActorRef;

public class LetterCounter extends AbstractActor {
	
	private ActorRef letterStorage = Factory.getInstance().getLetterStorage();
	
	public static Props props() {
		return Props.create(LetterCounter.class, () -> new LetterCounter());
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder().match(String.class, line -> {
			Map<Character, Integer> map = new HashMap<>();
			for(char c : line.toCharArray()) {
				if(Character.isAlphabetic(c)) {
					if(map.containsKey(c)) {
						Integer count = map.get(c);
						count++;
						map.put(c, count);
					} else {
						map.put(c, 1);
					}
				}
			}
			
			for(Character character : map.keySet()) {
				letterStorage.tell(new Letter(character, map.get(character)), getSelf());
			}
			
			getSender().tell("return from LetterCounter", getSelf());
		}).build();
	}
	
}
