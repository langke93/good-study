package org.langke.flink.sql;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.descriptors.Json;
import org.apache.flink.table.descriptors.Kafka;
import org.apache.flink.table.descriptors.Rowtime;
import org.apache.flink.table.descriptors.Schema;
import org.langke.flink.sql.udf.CountStatusUDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Flink SQL与kafka整合的那些事儿
 * @date 2020/1/10
 * https://mp.weixin.qq.com/s?src=11&timestamp=1578559730&ver=2085&signature=A2-wm1wlymro2GYiuzJ46Ob0*uNLWDHoiR7ZwSPxCV-sPeTkSOWxQBnDcb9RT7a1Uo10uNU4QC879D0sdw5jKbX43lgofvj3h2jwnJ*rpqZKlVt0mWhvnyGPWTt*egNM&new=1
 *
 * Flink SQL 系列 | 5 个 TableEnvironment 我该用哪个？  https://juejin.im/post/5da6de66e51d45781c6fcd29
 *
 * 问题1：Caused by: org.apache.flink.table.api.NoMatchingTableFactoryException: Could not find a suitable table factory for 'org.apache.flink.table.factories.TableSinkFactory' in
 * the classpath.
 * 原因：kafka版本不对，kafka0.8版本对应maven依赖包：flink-connector-kafka-0.8，代码连接版本为.version("0.8");
 * kafka 0.11+使用universal version标识，maven依赖包使用flink-connector-kafka；  参考：https://ci.apache.org/projects/flink/flink-docs-release-1.9/dev/table/connect.html#kafka-connector
 */
public class Kafka2kafka {
    private static Logger logger = LoggerFactory.getLogger(Kafka2kafka.class);
    private static final String KAFKA_INPUT_TOPIC = "log_v1";
    private static final String KAFKA_INPUT_GROUP_ID = "flink";
    private static final String KAFKA_INPUT_BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String FLINK_TABLE_SOURCE = "global_soa_logs";
    private static final String KAFKA_OUTPUT_TOPIC = "nginx_logs_stat";
    private static final String FLINK_TABLE_SINK = "nginx_logs_stat";
    private static final String KAFKA_OUTPUT_BOOTSTRAP_SERVERS = "localhost:9092";
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //用户使用 Blink planner，进行流计算的 Table 程序的开发。这种场景下，用户可以使用 StreamTableEnvironment 或 TableEnvironment ，两者的区别是 StreamTableEnvironment 额外提供与 DataStream API 交互的接口。用户在 EnvironmentSettings 中声明使用 Blink planner ，将执行模式设置为 StreamingMode 即可
        EnvironmentSettings bsSettings = EnvironmentSettings.newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        //env.setParallelism(1);
        //env.enableCheckpointing(5000);
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env,bsSettings);
        // or TableEnvironment bsTableEnv = TableEnvironment.create(bsSettings);
        tEnv.registerFunction("countStatus",new CountStatusUDF());
        tEnv.connect(new Kafka().version("universal")
                .topic(KAFKA_INPUT_TOPIC)
                //.property("zookeeper.connect", "10.5.121.143:2181")
                .property("bootstrap.servers", KAFKA_INPUT_BOOTSTRAP_SERVERS)
                .property("group.id", KAFKA_INPUT_GROUP_ID)
                .property("enable.auto.commit", "true")
                .startFromLatest())
                .withFormat(new Json().failOnMissingField(false).deriveSchema())
                .withSchema(new Schema()
                        .field("rowtime", Types.SQL_TIMESTAMP)
                        .rowtime(new Rowtime().timestampsFromField("@timestamp")
                                        .watermarksPeriodicBounded(2000))
                        .field("requestTime",Types.LONG)
                        .field("log_type",Types.INT)
                        .field("serverAppId", Types.STRING)
                        .field("serverInterface",Types.STRING)
                        .field("serverMethod",Types.STRING)
                        .field("logSide",Types.STRING)
                        .field("requestLegnth",Types.LONG)
                        .field("responseLength",Types.LONG)
                        .field("processTime",Types.LONG)
                        .field("status",Types.INT)
                )
                .inAppendMode()
                .registerTableSource(FLINK_TABLE_SOURCE);
        tEnv.connect(new Kafka().version("universal")
                .topic(KAFKA_OUTPUT_TOPIC)
                .property("acks", "all")
                .property("retries", "0")
                .property("batch.size", "16384")
                .property("linger.ms", "10")
                .property("bootstrap.servers", KAFKA_OUTPUT_BOOTSTRAP_SERVERS)
                .sinkPartitionerFixed())
                .inAppendMode()
                .withFormat(new Json().deriveSchema())
                .withSchema(new Schema()
                        .field("date", Types.SQL_TIMESTAMP)
                        .field("requestTime", Types.LONG)
                        .field("tableDate",Types.STRING)
                        .field("logType", Types.INT)
                        .field("serverAppId", Types.STRING)
                        .field("serverInterface", Types.STRING)
                        .field("serverMethod", Types.STRING)
                        .field("logSide", Types.STRING)
                        .field("maxRequestLegnth", Types.LONG)
                        .field("sumRequestLegnth", Types.LONG)
                        .field("maxResponseLegnth", Types.LONG)
                        .field("sumResponseLegnth", Types.LONG)
                        .field("maxProcessTime", Types.LONG)
                        .field("sumProcessTime", Types.LONG)
                        .field("count", Types.LONG)
                        .field("errorCount", Types.LONG)
                        .field("soaErrorCount", Types.LONG)
                        .field("status3xxcount", Types.LONG)
                        .field("status4xxcount", Types.LONG)
                        .field("status5xxcount", Types.LONG)

                )
                .registerTableSink(FLINK_TABLE_SINK);
        tEnv.sqlUpdate("insert into " + FLINK_TABLE_SINK + " " +
                "select TUMBLE_END(rowtime, INTERVAL '60' SECOND)," +
                "requestTime/(1000*60)*(1000*60)," +
                "FROM_UNIXTIME(requestTime/1000,'yyyyMMdd')," +
                "log_type," +
                "serverAppId," +
                "serverInterface," +
                "serverMethod," +
                "logSide," +
                "max(requestLegnth)," +
                "sum(requestLegnth)," +
                "max(responseLength)," +
                "sum(responseLength)," +
                "max(processTime)," +
                "sum(processTime)," +
                "count(requestTime), " +
                "countStatus(status,'"+CountStatusUDF.ErrorStatus.ERROR+"'), " +
                "countStatus(status,'"+CountStatusUDF.ErrorStatus.SOA_ERROR+"'), " +
                "countStatus(status,'"+CountStatusUDF.ErrorStatus.STATUS_3XX+"'), " +
                "countStatus(status,'"+CountStatusUDF.ErrorStatus.STATUS_4XX+"'), " +
                "countStatus(status,'"+CountStatusUDF.ErrorStatus.STATUS_5XX+"') " +
                "from "+FLINK_TABLE_SOURCE+" group by FROM_UNIXTIME(requestTime/1000,'yyyyMMdd'),TUMBLE(rowtime, INTERVAL '60' SECOND),requestTime/(1000*60)*(1000*60),log_type,serverAppId,serverInterface,serverMethod,logSide");
        logger.info(bsSettings.getBuiltInCatalogName());
        logger.info(tEnv.getConfig().getConfiguration().toString());
        env.execute();
        logger.info("done");
    }
}
