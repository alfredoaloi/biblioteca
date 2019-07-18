package clientBiblioteca;

public class Category {
	private String categoryType;
	private Book[] books;
	
	public Category(String categoryType) {
		this.categoryType = categoryType;
	}

	public String getCategoryType() {
		return categoryType;
	}

	public void setCategoryType(String categoryType) {
		this.categoryType = categoryType;
	}

	public Book[] getBooks() {
		return books;
	}

	public void setBooks(Book[] books) {
		this.books = books;
	}
}
