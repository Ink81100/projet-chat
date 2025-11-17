import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

public class TestLog {
    @Test
    void testLogs () {
        Logger logger = LogManager.getLogger(TestLog.class);
        logger.info("Début des tests");
        logger.error("");
    }
}
