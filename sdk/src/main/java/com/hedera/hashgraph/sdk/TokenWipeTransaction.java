package com.hedera.hashgraph.sdk;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.sdk.proto.*;
import com.hedera.hashgraph.sdk.proto.TransactionResponse;
import io.grpc.MethodDescriptor;

import java.util.LinkedHashMap;

public class TokenWipeTransaction extends com.hedera.hashgraph.sdk.Transaction<TokenWipeTransaction> {
    private final TokenWipeAccountTransactionBody.Builder builder;

    TokenId tokenId;
    AccountId accountId;

    public TokenWipeTransaction() {
        builder = TokenWipeAccountTransactionBody.newBuilder();
    }

    TokenWipeTransaction(LinkedHashMap<TransactionId, LinkedHashMap<AccountId, com.hedera.hashgraph.sdk.proto.Transaction>> txs) throws InvalidProtocolBufferException {
        super(txs);

        builder = bodyBuilder.getTokenWipe().toBuilder();

        if (builder.hasToken()) {
            tokenId = TokenId.fromProtobuf(builder.getToken());
        }

        if (builder.hasAccount()) {
            accountId = AccountId.fromProtobuf(builder.getAccount());
        }
    }

    TokenWipeTransaction(com.hedera.hashgraph.sdk.proto.TransactionBody txBody) {
        super(txBody);

        builder = bodyBuilder.getTokenWipe().toBuilder();

        if (builder.hasToken()) {
            tokenId = TokenId.fromProtobuf(builder.getToken());
        }

        if (builder.hasAccount()) {
            accountId = AccountId.fromProtobuf(builder.getAccount());
        }
    }

    public TokenId getTokenId() {
        return tokenId;
    }

    public TokenWipeTransaction setTokenId(TokenId tokenId) {
        requireNotFrozen();
        this.tokenId = tokenId;
        return this;
    }

    public AccountId getAccountId() {
        return accountId;
    }

    public TokenWipeTransaction setAccountId(AccountId accountId) {
        requireNotFrozen();
        this.accountId = accountId;
        return this;
    }

    public long getAmount() {
        return builder.getAmount();
    }

    public TokenWipeTransaction setAmount(long amount) {
        requireNotFrozen();
        builder.setAmount(amount);
        return this;
    }

    TokenWipeAccountTransactionBody.Builder build() {
        if (tokenId != null) {
            builder.setToken(tokenId.toProtobuf());
        }

        if (accountId != null) {
            builder.setAccount(accountId.toProtobuf());
        }

        return  builder;
    }

    @Override
    void validateNetworkOnIds(Client client) {
        if (tokenId != null) {
            tokenId.validate(client);
        }

        if (accountId != null) {
            accountId.validate(client);
        }
    }

    @Override
    MethodDescriptor<com.hedera.hashgraph.sdk.proto.Transaction, TransactionResponse> getMethodDescriptor() {
        return TokenServiceGrpc.getWipeTokenAccountMethod();
    }

    @Override
    boolean onFreeze(TransactionBody.Builder bodyBuilder) {
        bodyBuilder.setTokenWipe(build());
        return true;
    }

    @Override
    void onScheduled(SchedulableTransactionBody.Builder scheduled) {
        scheduled.setTokenWipe(build());
    }
}

