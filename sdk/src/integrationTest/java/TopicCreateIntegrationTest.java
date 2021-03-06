import com.hedera.hashgraph.sdk.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class TopicCreateIntegrationTest {
    @Test
    @DisplayName("Can create topic")
    void canCreateTopic() {
        assertDoesNotThrow(() -> {
            var testEnv = new IntegrationTestEnv();

            var response = new TopicCreateTransaction()
                .setNodeAccountIds(testEnv.nodeAccountIds)
                .setAdminKey(testEnv.operatorKey)
                .setTopicMemo("[e2e::TopicCreateTransaction]")
                .execute(testEnv.client);

            var topicId = Objects.requireNonNull(response.getReceipt(testEnv.client).topicId);

            new TopicDeleteTransaction()
                .setTopicId(topicId)
                .execute(testEnv.client)
                .getReceipt(testEnv.client);

            testEnv.client.close();
        });
    }

    @Test
    @DisplayName("Can create topic with no field set")
    void canCreateTopicWithNoFieldsSet() {
        assertDoesNotThrow(() -> {
            var testEnv = new IntegrationTestEnv();

            var response = new TopicCreateTransaction()
                .setNodeAccountIds(testEnv.nodeAccountIds)
                .execute(testEnv.client);
            assertNotNull(response.getReceipt(testEnv.client).topicId);

            testEnv.client.close();
        });
    }
}
