import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.projetchat.serveur.modele.DBHandler;

public class TestDB {
    @TempDir
    static Path tempDBTestPath;

    @BeforeAll
    static void initDB() throws IOException {
        File db = tempDBTestPath.resolve("messages.db").toFile();
        DBHandler.setUrl(db.getCanonicalPath());
    }

    @BeforeEach
    void creerDB() throws SQLException, IOException {
        DBHandler.creerDB();
    }

    @Test
    void testCreationDB() {
        try {
            DBHandler.creerDB();
            assertTrue(DBHandler.isInit());
        } catch (SQLException | IOException e) {
            System.out.println("Une erreur est survenue : " + e);
        }
    }

    @Test
    void ajouteMessage() {
        DBHandler.addMessage("A", "test");
        assertEquals(1, DBHandler.size(), "La base n'est pas de la bonne taiile");
    }
}
