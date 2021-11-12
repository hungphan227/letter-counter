package org.hungphan.letter.counter.controller;

import java.util.HashMap;
import java.util.Map;

import org.hungphan.letter.counter.model.Paragraph;

import io.vertx.core.AbstractVerticle;

public class ParagraphStorage extends AbstractVerticle {
	
	private int idCount;

	private Map<Integer, Paragraph> paragraphStorage = new HashMap<>();

	@Override
	public void start() throws Exception {
		vertx.eventBus().consumer("insert_paragraph", (message) -> {
			Paragraph paragraph = (Paragraph) message.body();
			paragraph.setId(++idCount);
			paragraphStorage.put(paragraph.getId(), paragraph);
			message.reply("");
		});
		
		vertx.eventBus().consumer("number_of_paragraphs", (message) -> {
			message.reply(paragraphStorage.keySet().size());
		});
		
		vertx.eventBus().consumer("max_paragraph_length", (message) -> {
			int maxLength = 0;
			for(Paragraph paragraph : paragraphStorage.values()) {
				if(maxLength < paragraph.getContent().length()) {
					maxLength = paragraph.getContent().length();
				}
			}
			message.reply(maxLength);
		});
	}

}
