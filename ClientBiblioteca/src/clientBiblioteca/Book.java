package clientBiblioteca;

public class Book {
	private int bookID;
	private String title;
	private String author;
	private int nPages;
	private String publisher;
	private String language;
	private String description;
	private int ISBN;
	private int nBooks;
	private String image;
	private int lendingPeriod;
	private int fineIncrement;

	public Book(int bookID, String title, String author, int nPages, String publisher, String language,
			String description, int iSBN, int nBooks, String image, int lendingPeriod, int fineIncrement) {
		super();
		this.bookID = bookID;
		this.title = title;
		this.author = author;
		this.nPages = nPages;
		this.publisher = publisher;
		this.language = language;
		this.description = description;
		ISBN = iSBN;
		this.nBooks = nBooks;
		this.image = image;
		this.lendingPeriod = lendingPeriod;
		this.fineIncrement = fineIncrement;
	}


	public int getBookID() {
		return bookID;
	}

	public void setBookID(int bookID) {
		this.bookID = bookID;
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
	
	public String getImage() {
		return image;
	}
	
	public void setImage(String image) {
		this.image = image;
	}
	
	public int getLendingPeriod() {
		return lendingPeriod;
	}


	public void setLendingPeriod(int lendingPeriod) {
		this.lendingPeriod = lendingPeriod;
	}


	public int getFineIncrement() {
		return fineIncrement;
	}


	public void setFineIncrement(int fineIncrement) {
		this.fineIncrement = fineIncrement;
	}

}
