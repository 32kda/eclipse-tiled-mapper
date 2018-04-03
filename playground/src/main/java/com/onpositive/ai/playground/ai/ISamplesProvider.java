package com.onpositive.ai.playground.ai;

import java.util.Collection;

public interface ISamplesProvider<T> {
	
	public Collection<T> provideNextBatch();

}
