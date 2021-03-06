/*
 * Licensed to STRATIO (C) under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.  The STRATIO (C) licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.stratio.connector.cassandra.engine;

import java.util.Arrays;

import org.apache.log4j.Logger;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.NoHostAvailableException;
import com.stratio.crossdata.common.exceptions.ConnectionException;

/**
 * Execution com.stratio.connector.cassandra that creates all entities required for processing an executing a query:
 * Additionally, it also maintains the {@link com.datastax.driver.core.Session} with the Cassandra backend.
 */
public class Engine {

    /**
     * Class logger.
     */
    private static final Logger LOG = Logger.getLogger(Engine.class.getName());
    /**
     * Datastax Java Driver session.
     */
    private final Session session;

    /**
     * Class constructor.
     *
     * @param config The {@link com.stratio.connector.cassandra.engine.EngineConfig}.
     */
    public Engine(EngineConfig config) throws ConnectionException {
        this.session = initializeDB(config);
    }

    /**
     * Initialize the connection to the underlying database.
     *
     * @param config The {@link com.stratio.connector.cassandra.engine.EngineConfig}.
     * @return A new Session.
     */
    private Session initializeDB(EngineConfig config) throws ConnectionException {
        Cluster cluster = Cluster.builder()
                .addContactPoints(config.getCassandraHosts())
                .withPort(config.getCassandraPort()).build();

        //TODO When credential exists change cluster builder to add withCredentials method to it

        LOG.info("Connecting to Cassandra Cluster on "
                + Arrays.toString(config.getCassandraHosts()) + ":" + config.getCassandraPort());
        Session result;

        try {
            result = cluster.connect();
        } catch (NoHostAvailableException nhae) {
            throw new ConnectionException(nhae.getMessage(), nhae);
        }

        return result;
    }

    /**
     * Close open connections.
     */
    public void shutdown() {

        session.close();

    }

    public Session getSession() {
        return session;
    }

}
