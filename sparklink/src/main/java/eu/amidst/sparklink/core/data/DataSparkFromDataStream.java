package eu.amidst.sparklink.core.data;

import eu.amidst.core.datastream.Attributes;
import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataStream;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.StructType;

import java.util.stream.Collectors;

/**
 * Created by rcabanas on 30/09/16.
 */
public class DataSparkFromDataStream implements DataSpark {

    final private DataStream<DataInstance> amidstDataStream;
    private JavaSparkContext jsc;

    public DataSparkFromDataStream(DataStream<DataInstance> input, JavaSparkContext jsc) {

        amidstDataStream = input;
        this.jsc = jsc;
    }

    @Override
    public DataFrame getDataFrame(SQLContext sql) {


        // Obtain the schema
        StructType schema = SchemaConverter.getSchema(getAttributes());

        // Transform the RDD
        JavaRDD<Row> rowRDD = DataFrameOps.toRowRDD(getDataSet(), getAttributes());

        // Create the DataFrame
        return sql.createDataFrame(rowRDD, schema);
    }

    @Override
    public Attributes getAttributes() {
        return amidstDataStream.getAttributes();
    }

    @Override
    public JavaRDD<DataInstance> getDataSet() {
        return jsc.parallelize(amidstDataStream.stream().collect(Collectors.toList()));

    }
}
