package org.mule.extension.opentelemetry.module.internal.connection;

import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionHandler;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.core.api.Closeable;
import org.mule.runtime.core.api.MuleContext;

public abstract class ConnectionManagementStrategy<C> implements Closeable {
    protected final ConnectionProvider<C> connectionProvider;
    protected final MuleContext muleContext;

    ConnectionManagementStrategy(ConnectionProvider<C> connectionProvider, MuleContext muleContext) {
        this.connectionProvider = connectionProvider;
        this.muleContext = muleContext;
    }

    public abstract ConnectionHandler<C> getConnectionHandler() throws ConnectionException;

    public abstract void close() throws MuleException;
}