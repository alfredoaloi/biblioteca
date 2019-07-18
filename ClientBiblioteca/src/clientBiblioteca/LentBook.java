package clientBiblioteca;

public class LentBook extends Book {
	
	private String userID;
	private String deadlineDate;
	private double fine;

	public LentBook(int bookID, String title, String author, int nPages, String publisher, String language,
			String description, int iSBN, int nBooks, String image, int lendingPeriod, int fineIncrement, String userID,
			String deadlineDate, double fine) {
		super(bookID, title, author, nPages, publisher, language, description, iSBN, nBooks, image, lendingPeriod,
				fineIncrement);
		this.userID = userID;
		this.deadlineDate = deadlineDate;
		this.fine = fine;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getDeadlineDate() {
		return deadlineDate;
	}

	public void setDeadlineDate(String deadlineDate) {
		this.deadlineDate = deadlineDate;
	}

	public double getFine() {
		return fine;
	}

	public void setFine(double fine) {
		this.fine = fine;
	}

}
