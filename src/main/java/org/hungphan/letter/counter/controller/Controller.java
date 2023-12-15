package org.hungphan.letter.counter.controller;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import org.hungphan.letter.counter.Factory;
import org.hungphan.letter.counter.model.Paragraph;

import akka.actor.ActorRef;
import akka.pattern.Patterns;
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class Controller extends AbstractVerticle {

	private HttpServer httpServer = null;

	private ActorRef letterCounter = Factory.getInstance().getLetterCounter();
	private ActorRef letterStorage = Factory.getInstance().getLetterStorage();

	@Override
	public void start() throws Exception {
		httpServer = vertx.createHttpServer();
		
		Router router = Router.router(vertx);
		
		router.route().handler(BodyHandler.create());
		
		router.route(HttpMethod.POST, "/paragraph").handler(requestHandler -> {
			String content = requestHandler.getBodyAsString();
			System.out.println(content);
			Paragraph paragraph = new Paragraph();
			paragraph.setContent(content);
			HttpServerResponse response = requestHandler.response();

			vertx.eventBus().send("insert_paragraph", paragraph, reply -> {
				if (reply.succeeded()) {
					CompletableFuture<Object> future = Patterns.ask(letterCounter, paragraph.getContent(), Duration.ofDays(1)).toCompletableFuture();
					future.thenAccept(result -> {
						System.out.println(result);
						response.setStatusCode(200);
						response.end(reply.result().body().toString());
					});
					
					//Patterns.pipe(future, Factory.getInstance().getSystem().dispatcher()).to(paragraphSplitterActor);
					
//					paragraphSplitterActor.tell(paragraph, ActorRef.noSender());
//					response.setStatusCode(200);
//					response.end();
				} else {
					response.setStatusCode(400);
					response.end();
				}
			});
		});
		
		router.route(HttpMethod.GET, "/paragraph/total_number").handler(requestHandler -> {
			HttpServerResponse response = requestHandler.response();
			vertx.eventBus().send("number_of_paragraphs", "", reply -> {
				if (reply.succeeded()) {
					response.setStatusCode(200);
					response.end(reply.result().body().toString());
				} else {
					response.setStatusCode(400);
					response.end();
				}
			});
		});

		router.route(HttpMethod.GET, "/paragraph/max_length").handler(requestHandler -> {
			HttpServerResponse response = requestHandler.response();
			vertx.eventBus().send("max_paragraph_length", "", reply -> {
				if (reply.succeeded()) {
					response.setStatusCode(200);
					response.end(reply.result().body().toString());
				} else {
					response.setStatusCode(400);
					response.end();
				}
			});
		});

		router.route(HttpMethod.GET, "/letter/:letter").handler(requestHandler -> {
			HttpServerResponse response = requestHandler.response();
			Character letter = requestHandler.request().getParam("letter").charAt(0);
			CompletableFuture<Object> future = Patterns.ask(letterStorage, new ConsistentHashableEnvelope(letter, letter), Duration.ofDays(1)).toCompletableFuture();
			future.thenAccept(result -> {
				System.out.println(result);
				response.setStatusCode(200);
				response.end(result.toString());
			});
		});

		httpServer.requestHandler(router).listen(8080);

		System.out.println("Server started!!!");
	}

}
