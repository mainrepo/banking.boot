package org.microjava.model.command;

import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * This file is part of pre-tested advancej library. The PersonSpliterator is
 * for creating a custom stream processing with a Spliterator<T>.
 *
 * @author  Gaurav J.
 * @version 1.0
 * @since   May 20, 2019
 */

public class PersonSpliterator implements Spliterator<Person> {
	
	private final Spliterator<String> linesSpliterator;
	private String name;
	private String dob;
	private String gender;

	/**
	 * @param linesSplit
	 */
	public PersonSpliterator(Spliterator<String> linesSpliterator) { 
		this.linesSpliterator = linesSpliterator;
	}

	@Override
	public int characteristics() { 
		return linesSpliterator.characteristics();
	}

	@Override
	public long estimateSize() { 
		return linesSpliterator.estimateSize() / 3;
	}

	@Override
	public boolean tryAdvance(Consumer<? super Person> action) {
		if (linesSpliterator.tryAdvance(line -> name = line)
				&& linesSpliterator.tryAdvance(line -> dob = line)
				&& linesSpliterator.tryAdvance(line -> gender = line)) {
			
			action.accept(new Person(name, dob, gender.charAt(0)));
			return true;
		}

		return false;
	}

	@Override
	public Spliterator<Person> trySplit() { 
		return null;
	}

}
