package org.hungphan.letter.counter.model;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

public class Paragraph implements MessageCodec<Paragraph, Paragraph> {

	private int id;
	private String content;
	private int length;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	@Override
	public void encodeToWire(Buffer buffer, Paragraph s) {
		System.out.println("encodeToWire");
	}

	@Override
	public Paragraph decodeFromWire(int pos, Buffer buffer) {
		System.out.println("decodeFromWire");
        return new Paragraph();
	}

	@Override
	public Paragraph transform(Paragraph s) {
		System.out.println("transform");
		return s;
	}

	@Override
	public String name() {
		return "PPPAAARRRAAA";
	}

	@Override
	public byte systemCodecID() {
		return -1;
	}
	
}
