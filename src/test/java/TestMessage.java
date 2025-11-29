import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.projetchat.Message;

public class TestMessage {
    @Test
    void testMessageToJsonToMessage() throws JsonProcessingException {
        Message message = new Message("A", "test");

        String json = message.toJson();
        
        Message messageJson = Message.fromJson(json);  
        
        assertTrue(message.equals(messageJson));
    }
}
