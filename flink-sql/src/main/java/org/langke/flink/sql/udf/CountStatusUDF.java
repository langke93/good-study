package org.langke.flink.sql.udf;

import org.apache.flink.table.functions.AggregateFunction;


public class CountStatusUDF extends AggregateFunction<Long, CountStatusUDF.CountAccum> {

    public enum ErrorStatus {
        OK,
        ERROR,
        SOA_ERROR,
        STATUS_3XX,
        STATUS_4XX,
        STATUS_5XX
    }
    //定义存放count UDAF状态的accumulator的数据的结构。
    public static class CountAccum {
        public long total;
    }

    //初始化count UDAF的accumulator。
    @Override
    public CountAccum createAccumulator() {
        CountAccum acc = new CountAccum();
        acc.total = 0;
        return acc;
    }

    //getValue提供了，如何通过存放状态的accumulator，计算count UDAF的结果的方法。
    @Override
    public Long getValue(CountAccum accumulator) {
        return accumulator.total;
    }

    //accumulate提供了，如何根据输入的数据，更新count UDAF存放状态的accumulator。
    public void accumulate(CountAccum accumulator, Integer iValue) {
        if(iValue>0){
            accumulator.total++;
        }
    }

    //accumulate提供了，如何根据输入的数据，更新count UDAF存放状态的accumulator。
    public void accumulate(CountAccum accumulator, Integer iValue,String errorStatus) {
        switch (ErrorStatus.valueOf(errorStatus)){
            case ERROR:
                if(iValue > 0 && iValue < 10 || iValue >= 500){
                    accumulator.total++;
                }
                break;
            case SOA_ERROR:
                if(iValue > 0 && iValue < 10){
                    accumulator.total++;
                }
                break;
            case STATUS_3XX:
                if(iValue >= 300 && iValue < 400){
                    accumulator.total++;
                }
                break;
            case STATUS_4XX:
                if(iValue >= 400 && iValue < 500){
                    accumulator.total++;
                }
                break;
            case STATUS_5XX:
                if(iValue >= 500){
                    accumulator.total++;
                }
                break;
           default:
               accumulator.total++;
        }
    }
    public void merge(CountAccum accumulator, Iterable<CountAccum> its) {
        for (CountAccum other : its) {
            accumulator.total += other.total;
        }
    }
}

