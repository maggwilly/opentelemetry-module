package org.mule.extension.opentelemetry.module.internal.connection;

import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.util.LazyValue;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.lifecycle.LifecycleUtils;
import org.mule.runtime.core.internal.connection.ConnectionHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Exceptions;

public class CachedConnectionHandler<C> implements ConnectionHandlerAdapter<C> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CachedConnectionHandler.class);
    private final ConnectionProvider<C> connectionProvider;
    private final MuleContext muleContext;
    private LazyValue<C> connection;

    public CachedConnectionHandler(ConnectionProvider<C> connectionProvider, MuleContext muleContext) {
        this.connectionProvider = connectionProvider;
        this.muleContext = muleContext;
        this.lazyConnect();
    }

    public C getConnection() throws ConnectionException {
        try {
            return this.connection.get();
        } catch (Throwable var2) {
            Throwable t = Exceptions.unwrap(var2);
            if (t instanceof ConnectionException) {
                throw (ConnectionException)t;
            } else {
                throw new ConnectionException(t.getMessage(), t);
            }
        }
    }

    private C createConnection() throws ConnectionException {
        LifecycleUtils.assertNotStopping(this.muleContext, "Mule is shutting down... Cannot establish new connections");
        return this.connectionProvider.connect();
    }

    public void release() {
    }

    public void close() {
        this.disconnectAndCleanConnection();
    }

    public void invalidate() {
        this.disconnectAndCleanConnection();
        this.lazyConnect();
    }

    public ConnectionProvider<C> getConnectionProvider() {
        return this.connectionProvider;
    }

    private void disconnectAndCleanConnection() {
        this.connection.ifComputed((c) -> {
            try {
                this.connectionProvider.disconnect(c);
            } catch (Exception var6) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(String.format("Error disconnecting cached connection %s. %s", c, var6.getMessage()), var6);
                }
            } finally {
                this.connection = null;
            }

        });
    }

    private void lazyConnect() {
        this.connection = new LazyValue<>(() -> {
            try {
                return createConnection();
            } catch (ConnectionException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
