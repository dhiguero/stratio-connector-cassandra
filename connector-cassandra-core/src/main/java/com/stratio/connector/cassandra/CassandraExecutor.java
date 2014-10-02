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

package com.stratio.connector.cassandra;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.DriverException;
import com.stratio.connector.cassandra.utils.Utils;
import com.stratio.meta.common.exceptions.ExecutionException;
import com.stratio.meta.common.exceptions.UnsupportedException;
import com.stratio.meta2.common.data.ColumnName;
import org.apache.log4j.Logger;

import java.util.Map;

public final class CassandraExecutor {

    /**
     * Class logger.
     */
    private static final Logger LOG = Logger.getLogger(CassandraExecutor.class);

    /**
     * The {@link com.stratio.connector.cassandra.utils.Utils}.
     */
    private static Utils utils = new Utils();

    /**
     * Private class constructor as all methods are static.
     */
    private CassandraExecutor() {
    }


    /**
     * Executes a query from a String.
     *
     * @param query   The query in a String.
     * @param session Cassandra datastax java driver session.
     * @return a {@link com.stratio.meta.common.result.Result}.
     */
    public static com.stratio.meta.common.result.Result execute(String query, Session session)
        throws UnsupportedException, ExecutionException {

        try {
            ResultSet resultSet = session.execute(query);
            return com.stratio.meta.common.result
                .QueryResult.createQueryResult(utils.transformToMetaResultSet(resultSet));
        } catch (UnsupportedOperationException unSupportException) {
            LOG.debug("Cassandra executor failed", unSupportException);
            return com.stratio.meta.common.result.Result.createExecutionErrorResult(
                "Unsupported operation by C*: " + unSupportException.getMessage());
        } catch (DriverException dex) {
            return com.stratio.meta.common.result.Result
                .createCriticalOperationErrorResult(dex.getMessage());
        } catch (Exception ex) {
            return processException(ex);
        }

    }



    /**
     * Executes a query from a String and add the alias in the Result for Selects .
     *
     * @param query        The query in a String.
     * @param aliasColumns The Map with the alias
     * @param session      Cassandra datastax java driver session.
     * @return a {@link com.stratio.meta.common.result.Result}.
     */
    public static com.stratio.meta.common.result.Result execute(String query,
        Map<ColumnName, String> aliasColumns, Session session)
        throws UnsupportedException, ExecutionException {
        try {
            ResultSet resultSet = session.execute(query);
            return com.stratio.meta.common.result
                .QueryResult
                .createQueryResult(utils.transformToMetaResultSet(resultSet, aliasColumns));
        } catch (UnsupportedOperationException unSupportException) {
            LOG.debug("Cassandra executor failed", unSupportException);
            return com.stratio.meta.common.result.Result.createExecutionErrorResult(
                "Unsupported operation by C*: " + unSupportException.getMessage());
        } catch (DriverException dex) {
            return com.stratio.meta.common.result.Result
                .createCriticalOperationErrorResult(dex.getMessage());
        } catch (Exception ex) {
            return processException(ex);
        }
    }



    /**
     * Process exception generated by Cassandra Executor.
     *
     * @param ex Exception catched.
     * @return a {@link com.stratio.meta.common.result.Result} with errors.
     */
    public static com.stratio.meta.common.result.Result processException(Exception ex) {
        if (ex.getMessage() == null) {
            return com.stratio.meta.common.result.Result
                .createExecutionErrorResult("Unknown exception");
        } else {
            return com.stratio.meta.common.result.Result
                .createExecutionErrorResult(ex.getMessage());
        }
    }



}
