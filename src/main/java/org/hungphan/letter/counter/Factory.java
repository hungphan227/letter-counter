package org.hungphan.letter.counter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hungphan.letter.counter.controller.Controller;
import org.hungphan.letter.counter.controller.LetterCounter;
import org.hungphan.letter.counter.controller.LetterStorage;
import org.hungphan.letter.counter.controller.ParagraphSplitter;
import org.hungphan.letter.counter.controller.ParagraphStorage;
import org.hungphan.letter.counter.model.Paragraph;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.ActorRefRoutee;
import akka.routing.ConsistentHashingPool;
import akka.routing.RoundRobinPool;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;
import io.vertx.core.Vertx;

public class Factory {

	private static Factory factory = new Factory();

	private ActorSystem system;
	private ActorRef paragraphSplitterActor;
	// private ActorRef letterCounterActor;
	private ActorRef letterStorage;

	private ActorRef letterCounter;

	private Factory() {
	}

	public void instantiate() {
		system = ActorSystem.create("letter-counter");
		
		letterStorage = system.actorOf(new ConsistentHashingPool(8).props(Props.create(LetterStorage.class)));
		
		letterCounter = system.actorOf(new RoundRobinPool(8).props(Props.create(LetterCounter.class)));

//		List<Routee> routees = new ArrayList<Routee>();
//		for (int i = 1; i <= 7; i++) {
//			ActorRef letterCounterActor = system.actorOf(LetterCounter.props());
//			routees.add(new ActorRefRoutee(letterCounterActor));
//		}
//		letterCounterRouter = new Router(new RoundRobinRoutingLogic(), routees);

		paragraphSplitterActor = system.actorOf(ParagraphSplitter.props(), "paragraphSplitterActor");

		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new Controller());
		vertx.deployVerticle(new ParagraphStorage());
		vertx.eventBus().registerDefaultCodec(Paragraph.class, new Paragraph());
	}

	public static Factory getInstance() {
		return factory;
	}

	public ActorSystem getSystem() {
		return system;
	}

	public void setSystem(ActorSystem system) {
		this.system = system;
	}

	public ActorRef getParagraphSplitterActor() {
		return paragraphSplitterActor;
	}

	public void setParagraphSplitterActor(ActorRef paragraphSplitterActor) {
		this.paragraphSplitterActor = paragraphSplitterActor;
	}

	public ActorRef getLetterStorage() {
		return letterStorage;
	}

	public ActorRef getLetterCounter() {
		return letterCounter;
	}

}
