package persistence;

import model.Book;
import model.BookStatus;
import model.Bookshelf;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

// source: JsonSerializationDemo
public class JsonReaderTest extends JsonTest {

    @Test
    void testReaderNonExistentFile() {
        JsonReader reader = new JsonReader("./data/noSuchFile.json");
        try {
            Bookshelf bs = reader.read();
            fail("IOException expected");
        } catch (IOException e) {
            //expected
        }
    }

    @Test
    void testReaderEmptyBookshelf() {
        try {
            Bookshelf bs = new Bookshelf("My Bookshelf");
            JsonWriter writer = new JsonWriter("./data/testReaderEmptyBookshelf.json");
            writer.open();
            writer.write(bs);
            writer.close();

            JsonReader reader = new JsonReader("./data/testReaderEmptyBookshelf.json");
            Bookshelf newBS = reader.read();
            assertEquals("My Bookshelf", newBS.getName());
            assertEquals(0, newBS.getCardinality());
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    void testReaderGeneralBookshelf() {
        try {
            Bookshelf bs = new Bookshelf("My Bookshelf");
            bs.shelveBook(new Book("Kingdom of Ash", "Sarah J. Maas", "cr",
                    0));
            bs.shelveBook(new Book("Heartstopper", "Alice Oseman", "cr", 5));
            JsonWriter writer = new JsonWriter("./data/testReaderGeneralBookshelf.json");
            writer.open();
            writer.write(bs);
            writer.close();

            JsonReader reader = new JsonReader("./data/testReaderGeneralBookshelf.json");
            Bookshelf newBS = reader.read();
            assertEquals("My Bookshelf", newBS.getName());
            HashMap<String, Book> books = newBS.getBooks();
            assertEquals(2, books.size());
            checkBook("Kingdom of Ash", "Sarah J. Maas", BookStatus.CURRENTLYREADING, 0,
                    books.get("Kingdom of Ash"));
            checkBook("Heartstopper", "Alice Oseman", BookStatus.CURRENTLYREADING, 5, books.get("Heartstopper"));
        } catch (IOException e) {
            fail();
        }
    }
}
