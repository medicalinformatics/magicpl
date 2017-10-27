package de.mainzelliste.paths.util;

import java.util.AbstractList;

import de.mainzelliste.paths.processorio.AbstractProcessorIo;

/**
 * Utility class to construct a list containing a given number of equal class
 * objects. Used to ease implementations of {@link AbstractProcessorIo} in cases
 * where only a single content type is used.
 */
public class ScalarContentTypeList extends AbstractList<Class<?>> {

	private int size;
	private Class<?> contentType;

	/**
	 * Construct an instance.
	 * 
	 * @param contentType
	 *            The class to set as content type.
	 * @param size
	 *            Desired size of the list.
	 */
	public ScalarContentTypeList(Class<?> contentType, int size) {
		if (size <= 0)
			throw new IllegalArgumentException("size must be a positive number");
		this.size = size;
		this.contentType = contentType;
	}

	@Override
	public Class<?> get(int index) {
		if (index < 0 || index >= size)
			throw new IndexOutOfBoundsException();
		return contentType;
	}

	@Override
	public int size() {
		return size;
	}

}
