package me.loper.bungeeauth.storage.implementation.nosql;

import com.lambdaworks.redis.ClientOptions;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.codec.RedisCodec;
import com.lambdaworks.redis.resource.DefaultClientResources;
import me.loper.bungeeauth.storage.ConnectionFactory;
import me.loper.bungeeauth.storage.StorageCredentials;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.function.Function;
import java.nio.charset.StandardCharsets;

public class RedisConnectionFactory<T> implements ConnectionFactory<StatefulRedisConnection<String, T>> {

    private StatefulRedisConnection<String, T> connection;
    private final StorageCredentials credentials;
    private DefaultClientResources resources;
    private RedisClient client;

    public RedisConnectionFactory(StorageCredentials credentials) {
        this.credentials = credentials;
    }

    @Override
    public String getImplementationName() {
        return "Redis";
    }

    @Override
    public void init() {
        this.resources = DefaultClientResources.builder()
                .ioThreadPoolSize(credentials.getMaxPoolSize())
                .computationThreadPoolSize(credentials.getMaxPoolSize())
                .build();

        this.client = RedisClient.create(resources, createRedisURI(credentials));
        this.client.setOptions(ClientOptions.builder().autoReconnect(true).build());
    }

    private RedisURI createRedisURI(StorageCredentials credentials) {
        RedisURI uri = new RedisURI();

        String address = credentials.getAddress();
        String[] addressSplit = address.split(":");
        address = addressSplit[0];
        int port = addressSplit.length > 1 ? Integer.parseInt(addressSplit[1]) : 6379;

        String password = credentials.getPassword();

        uri.setHost(address);
        uri.setPort(port);
        uri.setTimeout(credentials.getConnectionTimeout());

        if (null != password && 0 != password.length()) {
            uri.setPassword(password);
        }

        return uri;
    }

    @Override
    public void shutdown() {
        this.connection.close();
        this.resources.shutdown();
        this.client.shutdown();
    }

    @Override
    public StatefulRedisConnection<String, T> getConnection() {
        if (connection == null || !connection.isOpen()) {
            connection = client.connect(new ObjectTypeByteArrayCodec());
        }

        return this.connection;
    }

    @Override
    public Function<String, String> getStatementProcessor() {
        return null;
    }

    private class ObjectTypeByteArrayCodec implements RedisCodec<String, T> {

        private final Charset charset = StandardCharsets.UTF_8;

        @Override
        public String decodeKey(ByteBuffer bytes) {
            return charset.decode(bytes).toString();
        }

        @SuppressWarnings("unchecked")
        @Override
        public T decodeValue(ByteBuffer bytes) {
            try {
                byte[] array = new byte[bytes.remaining()];
                bytes.get(array);
                ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(array));
                return (T) is.readObject();
            } catch (Exception ex) {
                throw new IllegalStateException(ex);
            }
        }

        @Override
        public ByteBuffer encodeKey(String key) {
            return charset.encode(key);
        }

        @Override
        public ByteBuffer encodeValue(T value) {
            try {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                ObjectOutputStream os = new ObjectOutputStream(bytes);
                os.writeObject(value);
                return ByteBuffer.wrap(bytes.toByteArray());
            } catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
        }
    }
}