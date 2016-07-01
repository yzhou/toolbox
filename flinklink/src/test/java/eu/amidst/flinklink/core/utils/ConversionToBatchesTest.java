/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 *
 */

package eu.amidst.flinklink.core.utils;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.flinklink.core.data.DataFlink;
import eu.amidst.flinklink.core.io.DataFlinkLoader;
import junit.framework.TestCase;
import org.apache.flink.api.java.ExecutionEnvironment;

/**
 * Created by andresmasegosa on 12/5/16.
 */
public class ConversionToBatchesTest extends TestCase {

    public static void test1() throws Exception{
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        DataFlink<DataInstance> dataInstances = DataFlinkLoader.loadDataFromFile(env, "/Users/andresmasegosa/Dropbox/Amidst/datasets/uci-text/docword.kos.arff", false);

        System.out.println(dataInstances.getBatchedDataSet(100).count());
        System.out.println(dataInstances.getBatchedDataSet(100,ConversionToBatches::toBatchesBySeqID).count());

    }
}