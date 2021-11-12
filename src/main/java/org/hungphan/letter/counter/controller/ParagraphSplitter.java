package org.hungphan.letter.counter.controller;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.hungphan.letter.counter.Factory;
import org.hungphan.letter.counter.model.Paragraph;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.pattern.Patterns;

public class ParagraphSplitter extends AbstractActor {
	
	//private ActorRef letterCounterActor = Factory.getInstance().getLetterCounterActor();
	
	private ActorRef letterCounter = Factory.getInstance().getLetterCounter();
	
	public static Props props() {
		return Props.create(ParagraphSplitter.class, () -> new ParagraphSplitter());
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder().match(Paragraph.class, paragraph -> {
			String[] lines = paragraph.getContent().split(System.lineSeparator());
			
//			for(String line : lines) {
//				letterCounter.tell(line, getSelf());
//				//letterCounterActor.tell(line, getSelf());
//			}
//			
//			getSender().tell("return from ParagraphSplitter", getSelf());
			
			List<CompletableFuture<Object>> list = new ArrayList<>();
			for(String line : lines) {
				CompletableFuture<Object> future = Patterns.ask(letterCounter, line, Duration.ofDays(1)).toCompletableFuture();
				list.add(future);
			}
			
			CompletableFuture<String> finalFuture = CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).thenApply(result -> {
				for(CompletableFuture<Object> future : list) {
					System.out.println(future.join());
				}
				return "return from ParagraphSplitter";
			});
			
			Patterns.pipe(finalFuture, Factory.getInstance().getSystem().dispatcher()).to(getSender());
		}).build();
	}
	
}