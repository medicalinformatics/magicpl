package de.mainzelliste.paths.processorio;
import java.util.*;

/**
 * Immutable input or output (I/O) for Processors.
 */
public abstract class AbstractProcessorIo implements List<Object> {
	private ArrayList<Object> contentList;

	abstract List<Class<?>> getContentTypes();

	public AbstractProcessorIo(Object... content) throws ClassCastException {
		this.contentList = new ArrayList<>(content.length);
		Iterator<Class<?>> typeIterator = this.getContentTypes().iterator();
		for (Object item : content) {
			Class<?> type = typeIterator.next();
			if (!type.isAssignableFrom(item.getClass()))
				throw new ClassCastException(String.format("%s cannot be cast into %s",
						item.getClass().getSimpleName(),
						type.getSimpleName()));
			contentList.add(item);
		}
	}

	/**
	 * Rather than extending ArrayList<?>, we implement the List interface.
	 * This makes sure that we are immutable.
	 */

	@Override
	public int size() {
		return contentList.size();
	}

	@Override
	public boolean isEmpty() {
		return contentList.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return contentList.contains(o);
	}

	@Override
	public Iterator<Object> iterator() {
		return contentList.iterator();
	}

	@Override
	public Object[] toArray() {
		return new Object[0];
	}

	@Override
	public <T> T[] toArray(T[] ts) {
		return contentList.toArray(ts);
	}

	@Override
	public boolean add(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> collection) {
		return contentList.containsAll(collection);
	}

	@Override
	public boolean addAll(Collection<?> collection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(int i, Collection<?> collection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> collection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> collection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object get(int i) {
		return contentList.get(i);
	}

	@Override
	public Object set(int i, Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void add(int i, Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object remove(int i) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int indexOf(Object o) {
		return contentList.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return contentList.lastIndexOf(o);
	}

	@Override
	public ListIterator<Object> listIterator() {
		return contentList.listIterator();
	}

	@Override
	public ListIterator<Object> listIterator(int i) {
		return contentList.listIterator(i);
	}

	@Override
	public List<Object> subList(int i, int i1) {
		return contentList.subList(i, i1);
	}
}
