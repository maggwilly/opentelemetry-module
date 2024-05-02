package org.mule.extension.opentelemetry.module.internal.connection;

import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionHandler;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.internal.connection.ConnectionHandlerAdapter;

public final class CachedConnectionManagementStrategy<C> extends ConnectionManagementStrategy<C> {
    private final ConnectionHandlerAdapter<C> connection;

    public CachedConnectionManagementStrategy(ConnectionProvider<C> connectionProvider, MuleContext muleContext) {
        super(connectionProvider, muleContext);
        this.connection = new CachedConnectionHandler<>(connectionProvider, muleContext);
    }

    public ConnectionHandler<C> getConnectionHandler() throws ConnectionException {
        return this.connection;
    }

    public void close() throws MuleException {
        this.connection.close();
    }
}
