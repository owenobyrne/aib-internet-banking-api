// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 17/03/2013 01:04:34
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   CassandraService.java

package com.owenobyrne.aibibs.services;

import javax.annotation.PreDestroy;

import org.springframework.stereotype.Service;

import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolType;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;

// If you want to use this implementation uncomment the annotation below and 
// change the @Qualifier in the *Resource classes to "cassandraStorage"
//@Service
public class CassandraService implements StorageService {

    private Keyspace keyspace;
    private AstyanaxContext<Keyspace> context;
    public static ColumnFamily<String, String> CF_SESSIONS = new ColumnFamily<String, String>("Sessions", StringSerializer.get(), StringSerializer.get());

	@PreDestroy
    public void cleanUp() throws Exception {
        System.out.println("Shutting down Astayanax Cassandra connection...");
        context.shutdown();
        System.out.println("Astayanax Cassandra connection shutdown.");
    }

    public CassandraService()
    {
        System.out.println("Firing up connection to Cassandra...");
        context = (new com.netflix.astyanax.AstyanaxContext.Builder()).forCluster("Test Cluster").forKeyspace("OwenTest").withAstyanaxConfiguration((new AstyanaxConfigurationImpl()).setDiscoveryType(NodeDiscoveryType.RING_DESCRIBE).setConnectionPoolType(ConnectionPoolType.TOKEN_AWARE)).withConnectionPoolConfiguration((new ConnectionPoolConfigurationImpl("MyConnectionPool")).setPort(9160).setMaxConnsPerHost(3).setSeeds("10.10.1.2:9160")).withConnectionPoolMonitor(new CountingConnectionPoolMonitor()).buildKeyspace(ThriftFamilyFactory.getInstance());
        context.start();
        keyspace = (Keyspace)context.getEntity();
    }

    public Keyspace getKeyspace()
    {
        return keyspace;
    }

    public void setKeyspace(Keyspace keyspace)
    {
        this.keyspace = keyspace;
    }

    public void addData(String key, String value, int ttl)
    {
        MutationBatch mb = keyspace.prepareMutationBatch();
        mb.withRow(CF_SESSIONS, key).putColumn("page", value, Integer.valueOf(ttl));
        
        try
        {
            mb.execute();
        }
        catch(ConnectionException e)
        {
            e.printStackTrace();
        }
    }

    public String getData(String row)
    {
        OperationResult<ColumnList<String>> result = null;
        try
        {
            result = keyspace.prepareQuery(CF_SESSIONS).getKey(row).execute();
        }
        catch(ConnectionException e1)
        {
            e1.printStackTrace();
        }
        ColumnList<String> columns = (ColumnList<String>)result.getResult();
        Column<String> c = columns.getColumnByName("page");
        String value = null;
        if(c != null)
            value = c.getStringValue();
        return value;
    }

    public void deleteData(String row)
    {
        MutationBatch mb = keyspace.prepareMutationBatch();
        mb.withRow(CF_SESSIONS, row).delete();
        
        try
        {
        	 mb.execute();
        }
        catch(ConnectionException e) { }
    }

}