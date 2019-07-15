package clientBiblioteca;

public class Envelope<T> {
	
	String object;
	T content;

	public Envelope(String object, T content) {
		this.object = object;
		this.content = content;
	}
	
	public Envelope() {
		this.object = null;
		this.content = null;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public T getContent() {
		return content;
	}

	public void setContent(T content) {
		this.content = content;
	}
	
}
