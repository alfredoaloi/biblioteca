package clientBiblioteca;

public class Book {
	private String title;
	private String author;
	private int nPages;
	private String publisher;
	private String language;
	private String description;
	private int ISBN;
	private int nBooks;
	/* LE IMMAGINI MI RACCOMANDO */

	public Book(String title, String author, int nPages, String publisher, String language, String description,
			int ISBN, int nBooks) {
		super();
		this.title = title;
		this.author = author;
		this.nPages = nPages;
		this.publisher = publisher;
		this.language = language;
		this.description = description;
		this.ISBN = ISBN;
		this.nBooks = nBooks;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public int getnPages() {
		return nPages;
	}

	public void setnPages(int nPages) {
		this.nPages = nPages;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getISBN() {
		return ISBN;
	}

	public void setISBN(int ISBN) {
		this.ISBN = ISBN;
	}

	public int getnBooks() {
		return nBooks;
	}

	public void setnBooks(int nBooks) {
		this.nBooks = nBooks;
	}

	@Override
	public String toString() {
		return "title:" + title + " author:" + author + " nPages:" + nPages + " publisher:" + publisher + " language:"
				+ language + " description: " + description + " ISBN: " + ISBN + " nBooks: " + nBooks;
	}
}
