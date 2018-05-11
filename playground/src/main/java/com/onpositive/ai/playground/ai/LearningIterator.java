package com.onpositive.ai.playground.ai;

import java.util.Iterator;

public class LearningIterator<E> implements Iterator<E> {
	
	private ISamplesProvider<E> provider;
	private Iterator<E> nextBatchIterator;

	public LearningIterator(ISamplesProvider<E> provider) {
		this.provider = provider;
	}

	@Override
	public boolean hasNext() { 
		if (nextBatchIterator == null ||  !nextBatchIterator.hasNext()) {
			nextBatchIterator = obtainNextIterator();
		}
		return true;
	}

	protected Iterator<E> obtainNextIterator() {
		return provider.provideNextBatch().iterator();
	}

	@Override
	public E next() {
		if (nextBatchIterator == null || !nextBatchIterator.hasNext()) {
			nextBatchIterator = obtainNextIterator();
		}
		return nextBatchIterator.next();
	}

}
