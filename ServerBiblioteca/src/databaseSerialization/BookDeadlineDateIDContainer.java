package databaseSerialization;

public class BookDeadlineDateIDContainer {
	private int bookID;
	private String deadlineDate;
	
	public BookDeadlineDateIDContainer(int bookID, String deadlineDate) {
		super();
		this.bookID = bookID;
		this.deadlineDate = deadlineDate;
	}

	public int getBookID() {
		return bookID;
	}

	public void setBookID(int bookID) {
		this.bookID = bookID;
	}

	public String getDeadlineDate() {
		return deadlineDate;
	}

	public void setDeadlineDate(String deadlineDate) {
		this.deadlineDate = deadlineDate;
	}
}
