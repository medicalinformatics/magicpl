package de.mainzelliste.paths.implementations;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// TODO: List-Interface implementieren
public abstract class ProcessorIo implements List<Object> {

	private ArrayList<Object> contentList;
	
	public abstract List<Class<?>> getContentTypes();

	
	public ProcessorIo(Object... content) {
		this.contentList = new ArrayList<>(content.length); 
		Iterator<Class<?>> typeIterator = this.getContentTypes().iterator();
		for (Object item : content) {
			Class<?> type = typeIterator.next();
			if (!type.isAssignableFrom(item.getClass()))
				throw new Error();
			contentList.add(item);
		}
	}
	
	
}
