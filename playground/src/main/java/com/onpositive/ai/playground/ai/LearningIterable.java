package com.onpositive.ai.playground.ai;

import java.util.Iterator;

public class LearningIterable<T> implements Iterable<T> {
	
	private ISamplesProvider<T> provider;

	public LearningIterable(ISamplesProvider<T> provider) {
		this.provider = provider;
	}

	@Override
	public Iterator<T> iterator() {
		return new LearningIterator<T>(provider);
	}

}
