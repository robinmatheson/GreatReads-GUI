package ui;

import model.Book;
import model.BookStatus;
import model.Bookshelf;
import model.Event;
import model.EventLog;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

import static java.lang.Integer.parseInt;
import static javax.swing.JOptionPane.showInputDialog;

// opens a GUI that  allows you to create a bookshelf and add books to it
public class BookshelfGUI extends JFrame implements ActionListener {

    private JPanel panel;
    private JFrame frame;

    private JLabel bookshelfTitle;
    private JLabel labelDisplayGoal;
    private JLabel labelDisplayCard;

    private JTextField fieldTitle;
    private JTextField fieldAuthor;
    private JComboBox<String> statusCB;
    private JComboBox<String> ratingCB;
    private JTextField fieldBurnBook;
    private JTextField fieldSetGoal;
    private JTextField fieldChangeName;

    private DefaultTableModel dtmBooks;

    private GridBagConstraints c = new GridBagConstraints();

    private JsonWriter jsonWriter;
    private JsonReader jsonReader;
    private static final String JSON_STORE = "./data/bookshelf.json";

    private Bookshelf bs;

    // EFFECTS: calls JFrame super and method to begin building GUI
    public BookshelfGUI() {
        super("");
        initializeGUI();
    }

    // EFFECTS: opens pop-up window and constructs bookshelf; if no input or cancel button pressed, sets name of
    //          bookshelf to default "My Bookshelf"
    private void initializeGUI() {
        String inputName = showInputDialog("Please enter the name of your bookshelf:");
        if (Objects.equals(inputName, "") || inputName == null) {
            bs = new Bookshelf("My Bookshelf");
        } else {
            bs = new Bookshelf(inputName);
        }
        mainMenu();
    }

    // EFFECTS: opens a panel with the main menu
    private void mainMenu() {
        setUpMainMenu();
        setUpIcon();
        setUpAddBook();
        setUpBurnBook();
        setUpReadingGoal();
        setUpChangeBookshelfName();
        setUpPersistence();
        setUpShelfDisplay();
        setUpCardinality();
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
    }

    // MODIFIES: frame
    // EFFECTS: builds and adds frame and components
    private void setUpMainMenu() {
        frame = new JFrame();
        panel = new JPanel();
        add(panel);
        setUpClosingFunctions();
        setMinimumSize(new Dimension(1200, 600));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
        setLayout(new GridBagLayout());
    }

    // MODIFIES: frame
    // EFFECTS: puts icon in main menu frame
    private void setUpIcon() {
        ImageIcon imageIcon = new ImageIcon(
                getClass().getResource("/resources/img.png"));
        Image img = imageIcon.getImage();
        Image newImage = img.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        JLabel mmIcon = new JLabel(new ImageIcon(newImage));
        c.gridx = 0;
        c.gridy = 0;
        panel.add(mmIcon, c);
        addNameTitle();
    }

    // MODIFIES: frame
    // EFFECTS: adds bookshelf name as JLabel at top left of window
    private void addNameTitle() {
        bookshelfTitle = new JLabel(bs.getName());
        bookshelfTitle.setFont(new Font("SansSerif", Font.BOLD, 25));
        c.gridx = 2;
        c.gridy = 0;
        panel.add(bookshelfTitle, c);
    }

    // MODIFIES: frame
    // EFFECTS: constructs labels and fields for adding book functionality, sets constrains and adds to fame
    private void setUpAddBook() {
        // title
        c.insets = new Insets(40, 0, 0, 0);
        JLabel labelTitle = new JLabel("Title:");
        c.gridx = 0;
        c.gridy = 1;
        add(labelTitle, c);
        fieldTitle = new JTextField(15);
        c.gridx = 1;
        c.gridy = 1;
        add(fieldTitle, c);
        // author
        c.insets = new Insets(0, 0, 0, 0);
        JLabel labelAuthor = new JLabel("Author:");
        c.gridx = 0;
        c.gridy = 2;
        add(labelAuthor, c);
        fieldAuthor = new JTextField(15);
        c.gridx = 1;
        c.gridy = 2;
        add(fieldAuthor, c);
        // status
        JLabel labelStatus = new JLabel("Reading Status:");
        c.gridx = 0;
        c.gridy = 3;
        add(labelStatus, c);
        String[] statusChoices = new String[]{"to be read", "currently reading", "read"};
        statusCB = new JComboBox<>(statusChoices);
        statusCB.setSelectedItem("to be read");
        c.gridx = 1;
        c.gridy = 3;
        add(statusCB, c);
        // rating
        JLabel labelRating = new JLabel("Rating:");
        c.gridx = 0;
        c.gridy = 4;
        add(labelRating, c);
        String[] ratingChoices = new String[]{"0", "1", "2", "3", "4", "5"};
        ratingCB = new JComboBox<>(ratingChoices);
        ratingCB.setSelectedItem("0");
        c.gridx = 1;
        c.gridy = 4;
        add(ratingCB, c);
        // button
        JButton addBookButton = new JButton("Add Book");
        addBookButton.setActionCommand("addBookButton");
        addBookButton.addActionListener(this);
        c.gridx = 1;
        c.gridy = 5;
        add(addBookButton, c);
    }

    // MODIFIES: frame
    // EFFECTS: constructs labels and field to remove a book from the bookshelf, sets constraints and adds to panel
    private void setUpBurnBook() {
        c.insets = new Insets(30, 0, 10, 0);
        JLabel labelBurnBook = new JLabel("Enter title of the book you want to remove:");
        c.gridx = 0;
        c.gridy = 6;
        add(labelBurnBook, c);
        fieldBurnBook = new JTextField(15);
        c.gridx = 1;
        c.gridy = 6;
        add(fieldBurnBook, c);
        JButton burnBookButton = new JButton("Burn Book");
        c.gridx = 2;
        c.gridy = 6;
        add(burnBookButton, c);
        burnBookButton.setActionCommand("burnBookButton");
        burnBookButton.addActionListener(this);
    }

    // MODIFIES: frame
    // EFFECTS: constructs label and button associated with creating and displaying a reading goal, sets constraints
    //          and adds to frame
    private void setUpReadingGoal() {
        c.insets = new Insets(0, 0, 10, 0);
        JLabel labelSetGoal = new JLabel("Enter a new reading goal:");
        c.gridx = 0;
        c.gridy = 7;
        add(labelSetGoal, c);
        fieldSetGoal = new JTextField(8);
        c.gridx = 1;
        c.gridy = 7;
        add(fieldSetGoal, c);
        JButton setGoalButton = new JButton("Set Goal");
        c.gridx = 2;
        c.gridy = 7;
        add(setGoalButton, c);
        setGoalButton.setActionCommand("setGoalButton");
        setGoalButton.addActionListener(this);
        // display
        labelDisplayGoal = new JLabel("");
        c.gridx = 0;
        c.gridy = 10;
        c.gridwidth = 2;
        add(labelDisplayGoal, c);
        updateReadingGoal();
    }

    // MODIFIES: frame
    // EFFECTS: constructs labels and fields for changing the name of the bookshelf, sets constraints and
    //          adds them to frame
    private void setUpChangeBookshelfName() {
        c.gridwidth = 1;
        JLabel labelChangeName = new JLabel("Enter a new name for your bookshelf:");
        c.gridx = 0;
        c.gridy = 8;
        add(labelChangeName, c);
        fieldChangeName = new JTextField(15);
        c.gridx = 1;
        c.gridy = 8;
        add(fieldChangeName, c);
        JButton changeNameButton = new JButton("Enter");
        c.gridx = 2;
        c.gridy = 8;
        add(changeNameButton, c);
        changeNameButton.setActionCommand("changeNameButton");
        changeNameButton.addActionListener(this);
    }

    // MODIFIES: frame
    // EFFECTS: constructs labels and fields for persistence, sets constraints and adds them to frame
    private void setUpPersistence() {
        JButton saveBookshelfButton = new JButton("Save Bookshelf");
        c.gridx = 0;
        c.gridy = 9;
        add(saveBookshelfButton, c);
        saveBookshelfButton.setActionCommand("saveBookshelf");
        saveBookshelfButton.addActionListener(this);
        JButton loadBookshelfButton = new JButton("Load Bookshelf");
        c.gridx = 1;
        c.gridy = 9;
        add(loadBookshelfButton, c);
        loadBookshelfButton.setActionCommand("loadBookshelf");
        loadBookshelfButton.addActionListener(this);
    }

    // MODIFIES: frame
    // EFFECTS: constructs labels and fields for viewing list of books in the bookshelf, sets constraints and adds
    //          to frame
    private void setUpShelfDisplay() {
        String[] colNames = {"Title", "Author", "Status", "Rating"};
        dtmBooks = new DefaultTableModel(colNames, 1)
        {
            public Class getColumnClass(int column) {
                if (column == 3) {
                    return Icon.class;
                } else {
                    return super.getColumnClass(column);
                }
            }
        };
        JTable table = new JTable(dtmBooks);
        JScrollPane scroller = new JScrollPane(table);
        c.gridx = 3;
        c.gridy = 1;
        c.gridwidth = 3;
        c.gridheight = 9;
        add(scroller, c);
    }

    // MODIFIES: frame
    // EFFECTS: constructs label for the number of books shelved, sets constraints and adds to frame
    public void setUpCardinality() {
        labelDisplayCard = new JLabel("");
        c.gridx = 4;
        c.gridy = 11;
        c.anchor = GridBagConstraints.CENTER;
        add(labelDisplayCard, c);
        updateCardinality();
    }

    // EFFECTS: listens for actions performed and executes methods when detected
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("addBookButton")) {
            String title = fieldTitle.getText();
            String author = fieldAuthor.getText();
            String status = statusCB.getSelectedItem().toString();
            int rating = parseInt(ratingCB.getSelectedItem().toString());
            Book book = new Book(title, author, status, rating);
            bs.shelveBook(book);
            addBookToBooksInfo(book);
            if (status.equals("read")) {
                updateReadingGoal();
            }
            updateCardinality();
        }
        if (e.getActionCommand().equals("burnBookButton")) {
            String titleToBurn = fieldBurnBook.getText();
            removeBookFromBooksInfo(bs.getBook(titleToBurn));
            if (bs.getBook(titleToBurn).getStatus() == BookStatus.READ) {
                updateReadingGoal();
            }
            bs.burnBook(titleToBurn);
            updateReadingGoal();
            updateCardinality();
        }
        if (e.getActionCommand().equals("setGoalButton")) {
            bs.setGoal(parseInt(fieldSetGoal.getText()));
            updateReadingGoal();
        }
        if (e.getActionCommand().equals("changeNameButton")) {
            bs.setName(fieldChangeName.getText());
            bookshelfTitle.setText(fieldChangeName.getText());
        }
        if (e.getActionCommand().equals("saveBookshelf")) {
            try {
                jsonWriter = new JsonWriter(JSON_STORE);
                jsonWriter.open();
                jsonWriter.write(bs);
                jsonWriter.close();
            } catch (FileNotFoundException exc) {
                //
            }
        }
        if (e.getActionCommand().equals("loadBookshelf")) {
            try {
                jsonReader = new JsonReader(JSON_STORE);
                bs = jsonReader.read();
                bookshelfTitle.setText(bs.getName());
                loadedShelfAddBooks();
                updateCardinality();
                updateReadingGoal();
            } catch (IOException exc) {
                //
            }
        }
    }

    private void loadedShelfAddBooks() {
        // TODO
//        booksInfo.clear();
//        Collection<Book> books = bs.getAllBooks();
//        for (Book b : books) {
//            addBookToBooksInfo(b);
//        }
    }

    private void updateCardinality() {
        labelDisplayCard.setText("Number of books shelved: " + bs.getCardinality());
    }

    private void updateReadingGoal() {
        if (bs.getGoal() == -1) {
            labelDisplayGoal.setText("No reading goal set.");
        } else {
            int num = bs.getNumberRead();
            if (num == 1) {
                labelDisplayGoal.setText("You have read " + num + " book out of your goal of " + bs.getGoal()
                        + "!");
            } else {
                labelDisplayGoal.setText("You have read " + num + " books out of your goal of " + bs.getGoal()
                        + "!");
            }
        }
    }

    // TODO
    // MODIFIES: this
    // EFFECTS: removes info of deleted book from booksInfo
    private void removeBookFromBooksInfo(Book b) {
//        String val = convertBookToDisplayString(b);
//        int idx = -1;
//        for (int i = 0; i < booksInfo.size(); i++) {
//            String elt = booksInfo.elementAt(i);
//            if (elt.equals(val)) {
//                idx = i;
//                break;
//            }
//        }
//        if (idx != -1) {
//            booksInfo.remove(idx);
//        }
    }

    // MODIFIES: this
    // EFFECTS: adds info of added book to booksInfo
    private void addBookToBooksInfo(Book b) {
        Object[] bookData = {b.getTitle(), b.getAuthor(), statusToNiceString(b.getStatus()),
                selectRatingImage(b.getRating())};
        dtmBooks.addRow(bookData);
    }

    // EFFECTS: converts rating to a string of star characters
    private ImageIcon selectRatingImage(int rating) {
        if (rating == 0) {
            return new ImageIcon();
        } else {
            ImageIcon imageIcon = null;
            switch (rating) {
                case 1:
                    imageIcon = new ImageIcon(getClass().getResource("/resources/1-star-transparent.png"));
                    break;
                case 2:
                    imageIcon = new ImageIcon(getClass().getResource("/resources/2-stars-transparent.png"));
                    break;
                case 3:
                    imageIcon = new ImageIcon(getClass().getResource("/resources/3-stars-transparent.png"));
                    break;
                case 4:
                    imageIcon = new ImageIcon(getClass().getResource("/resources/4-stars-transparent.png"));
                    break;
                case 5:
                    imageIcon = new ImageIcon(getClass().getResource("/resources/5-stars-transparent.png"));
                    break;
            }
            Image img = imageIcon.getImage();
            Image scaled = img.getScaledInstance(70, 13, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        }
    }

    // EFFECTS: converts BookStatus to string to display
    private String statusToNiceString(BookStatus status) {
        if (status == BookStatus.READ) {
            return "read";
        } else if (status == BookStatus.TOBEREAD) {
            return "to be read";
        } else {
            return "currently reading";
        }
    }

    // EFFECTS: calls printLog() function upon window closing
    public void setUpClosingFunctions() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                printLog();
            }
        });
    }

    // EFFECTS: prints event log to console
    private void printLog() {
        for (Event event : EventLog.getInstance()) {
            System.out.println(event.toString() + "\n");
        }
        frame.dispose();
        System.exit(0);
    }

}

