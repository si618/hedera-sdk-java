import com.google.errorprone.annotations.Var;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.FileAppendTransaction;
import com.hedera.hashgraph.sdk.FileContentsQuery;
import com.hedera.hashgraph.sdk.FileCreateTransaction;
import com.hedera.hashgraph.sdk.FileDeleteTransaction;
import com.hedera.hashgraph.sdk.FileInfoQuery;
import com.hedera.hashgraph.sdk.FileUpdateTransaction;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.KeyList;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileIntegrationTest {
    @Test
    void test() {
        assertDoesNotThrow(() -> {
            var client = IntegrationTestClientManager.getClient();
            var operatorKey = client.getOperatorPublicKey();
            assertNotNull(operatorKey);

            var response = new FileCreateTransaction()
                .setKeys(operatorKey)
                .setContents("[e2e::FileCreateTransaction]")
                .setMaxTransactionFee(new Hbar(5))
                .execute(client);

            var receipt = response.getReceipt(client);

            assertNotNull(receipt.fileId);
            assertTrue(Objects.requireNonNull(receipt.fileId).num > 0);

            var file = receipt.fileId;

            @Var var info = new FileInfoQuery()
                .setNodeAccountIds(Collections.singletonList(response.nodeId))
                .setFileId(file)
                .setQueryPayment(new Hbar(22))
                .execute(client);

            assertEquals(info.fileId, file);
            assertEquals(info.size, 28);
            assertFalse(info.isDeleted);
            assertNotNull(info.keys.getThreshold());
            var testKey = KeyList.of(Objects.requireNonNull(operatorKey)).setThreshold(info.keys.getThreshold());
            assertEquals(info.keys.toString(), testKey.toString());

            new FileAppendTransaction()
                .setNodeAccountIds(Collections.singletonList(response.nodeId))
                .setFileId(file)
                .setContents("[e2e::FileAppendTransaction]")
                .setMaxTransactionFee(new Hbar(5))
                .execute(client)
                .getReceipt(client);

            info = new FileInfoQuery()
                .setNodeAccountIds(Collections.singletonList(response.nodeId))
                .setFileId(file)
                .setQueryPayment(new Hbar(1))
                .execute(client);

            assertEquals(info.fileId, file);
            assertEquals(info.size, 56);
            assertFalse(info.isDeleted);
            assertNotNull(info.keys.getThreshold());
            testKey.setThreshold(info.keys.getThreshold());
            assertEquals(info.keys.toString(), testKey.toString());

            var contents = new FileContentsQuery()
                .setNodeAccountIds(Collections.singletonList(response.nodeId))
                .setFileId(file)
                .setQueryPayment(new Hbar(1))
                .execute(client);

            assertEquals(contents.toStringUtf8(), "[e2e::FileCreateTransaction][e2e::FileAppendTransaction]");

            new FileUpdateTransaction()
                .setNodeAccountIds(Collections.singletonList(response.nodeId))
                .setFileId(file)
                .setContents("[e2e::FileUpdateTransaction]")
                .setMaxTransactionFee(new Hbar(5))
                .execute(client)
                .getReceipt(client);

            info = new FileInfoQuery()
                .setNodeAccountIds(Collections.singletonList(response.nodeId))
                .setFileId(file)
                .setQueryPayment(new Hbar(1))
                .execute(client);

            assertEquals(info.fileId, file);
            assertEquals(info.size, 28);
            assertFalse(info.isDeleted);
            assertNotNull(info.keys.getThreshold());
            testKey.setThreshold(info.keys.getThreshold());
            assertEquals(info.keys.toString(), testKey.toString());

            new FileDeleteTransaction()
                .setNodeAccountIds(Collections.singletonList(response.nodeId))
                .setFileId(file)
                .setMaxTransactionFee(new Hbar(5))
                .execute(client)
                .getReceipt(client);

            info = new FileInfoQuery()
                .setNodeAccountIds(Collections.singletonList(response.nodeId))
                .setFileId(file)
                .setQueryPayment(new Hbar(1))
                .execute(client);

            assertEquals(info.fileId, file);
            assertTrue(info.isDeleted);
            assertNotNull(info.keys.getThreshold());
            testKey.setThreshold(info.keys.getThreshold());
            assertEquals(info.keys.toString(), testKey.toString());

            client.close();
        });
    }
}
