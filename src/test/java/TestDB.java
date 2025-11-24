import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.projetchat.serveur.modele.DBHandler;

public class TestDB {
    @TempDir
    Path tempDBTestPath;

    @Test
    void testCreationDB() {
        try {
            File db = tempDBTestPath.resolve("messages.db").toFile();
            assertFalse(db.exists());
            DBHandler dbHandler = new DBHandler(db.getCanonicalPath());
            dbHandler.creerDB();
            assertTrue(dbHandler.isInit());
        } catch (SQLException | IOException e) {
            System.out.println("Une erreur est survenue : " + e);
        }
    }
}
