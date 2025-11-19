import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

// -------------------- BOOK CLASS --------------------
class Book {

    private String id;
    private String title;
    private String author;
    private boolean issued;

    public Book(String id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.issued = false;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public boolean isIssued() { return issued; }

    public void setIssued(boolean val) {
        this.issued = val;
    }

    @Override
    public String toString() {
        return "[Book ID: " + id +
                ", Title: " + title +
                ", Author: " + author +
                ", Status: " + (issued ? "Issued" : "Available") + "]";
    }
}


// -------------------- MEMBER CLASS --------------------
class Member {

    private String memberId;
    private String name;
    private List<String> borrowedList;

    public Member(String memberId, String name) {
        this.memberId = memberId;
        this.name = name;
        borrowedList = new ArrayList<>();
    }

    public String getId() { return memberId; }
    public String getName() { return name; }

    public void borrow(String bookId) {
        borrowedList.add(bookId);
    }

    public void returnBook(String bookId) {
        borrowedList.remove(bookId);
    }
}


// -------------------- CUSTOM EXCEPTIONS --------------------
class BookNotAvailableException extends Exception {
    public BookNotAvailableException(String msg) {
        super(msg);
    }
}

class InvalidReturnException extends Exception {
    public InvalidReturnException(String msg) {
        super(msg);
    }
}


// -------------------- LIBRARY CLASS --------------------
class Library {

    private HashMap<String, Book> books = new HashMap<>();
    private HashMap<String, Member> members = new HashMap<>();

    // Add a new book to library
    public void addBook(Book b) {
        books.put(b.getId(), b);
        writeLog("Added Book: " + b.getId());
    }

    // Add a new member
    public void addMember(Member m) {
        members.put(m.getId(), m);
        writeLog("Added Member: " + m.getId());
    }

    // Issue a book to a member
    public void issue(String bookId, String memberId) throws BookNotAvailableException {

        Book b = books.get(bookId);
        Member m = members.get(memberId);

        if (b == null)
            throw new BookNotAvailableException("Book not found.");

        if (m == null)
            throw new BookNotAvailableException("Member not found.");

        if (b.isIssued())
            throw new BookNotAvailableException("Book already issued.");

        b.setIssued(true);
        m.borrow(bookId);

        writeLog("Issued Book: " + bookId + " to Member " + memberId);
    }

    // Return a book with late fee
    public void returnBook(String bookId, String memberId, int lateDays)
            throws InvalidReturnException {

        Book b = books.get(bookId);
        Member m = members.get(memberId);

        if (b == null || m == null)
            throw new InvalidReturnException("Invalid Book/Member.");

        if (!b.isIssued())
            throw new InvalidReturnException("Book was not issued.");

        b.setIssued(false);
        m.returnBook(bookId);

        int fee = lateDays * 2;
        System.out.println("Book returned. Late Fee: ₹" + fee);

        writeLog("Returned Book: " + bookId + " by Member " + memberId +
                " | Late Fee: ₹" + fee);
    }

    // Display all books
    public void showAll() {
        if (books.isEmpty()) {
            System.out.println("Library has no books yet.");
            return;
        }

        for (Book b : books.values()) {
            System.out.println(b);
        }
    }

    // File logging
    private void writeLog(String msg) {
        try {
            FileWriter fw = new FileWriter("library_log.txt", true);
            fw.write(msg + "\n");
            fw.close();
        } catch (IOException e) {
            System.out.println("Unable to write log.");
        }
    }
}


// -------------------- MAIN CLASS --------------------
public class LibrarySystem {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        Library lib = new Library();

        while (true) {

            System.out.println("\n========= LIBRARY MENU =========");
            System.out.println("1. Add Book");
            System.out.println("2. Add Member");
            System.out.println("3. Issue Book");
            System.out.println("4. Return Book");
            System.out.println("5. Show Inventory");
            System.out.println("6. Exit");
            System.out.print("Choose: ");

            int ch;

            try {
                ch = sc.nextInt();
                sc.nextLine();
            } catch (Exception e) {
                System.out.println("Invalid choice!");
                sc.nextLine();
                continue;
            }

            switch (ch) {

                case 1:
                    System.out.print("Book ID: ");
                    String bid = sc.nextLine();

                    System.out.print("Title: ");
                    String title = sc.nextLine();

                    System.out.print("Author: ");
                    String auth = sc.nextLine();

                    lib.addBook(new Book(bid, title, auth));
                    break;

                case 2:
                    System.out.print("Member ID: ");
                    String mid = sc.nextLine();

                    System.out.print("Name: ");
                    String mname = sc.nextLine();

                    lib.addMember(new Member(mid, mname));
                    break;

                case 3:
                    System.out.print("Book ID: ");
                    String b1 = sc.nextLine();

                    System.out.print("Member ID: ");
                    String m1 = sc.nextLine();

                    try {
                        lib.issue(b1, m1);
                        System.out.println("Book Issued.");
                    } catch (BookNotAvailableException ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;

                case 4:
                    System.out.print("Book ID: ");
                    String br = sc.nextLine();

                    System.out.print("Member ID: ");
                    String mr = sc.nextLine();

                    System.out.print("Days Late: ");
                    int late = sc.nextInt();
                    sc.nextLine();

                    try {
                        lib.returnBook(br, mr, late);
                    } catch (InvalidReturnException ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;

                case 5:
                    lib.showAll();
                    break;

                case 6:
                    System.out.println("Goodbye!");
                    return;

                default:
                    System.out.println("Invalid option.");
            }
        }
    }
}
