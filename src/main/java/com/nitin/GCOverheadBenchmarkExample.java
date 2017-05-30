package com.nitin;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.profile.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 * Microbenchmark to measure garbage collector performance of HashMap
 * and TreeMap.
 */
public class GCOverheadBenchmarkExample {

    @State(Scope.Thread)
    @Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
    @Fork(3)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public static class Maps {
        private Map<Integer, Integer> map;

        @Param({"hashmap", "treemap"})
        private String type;

        private int begin;
        private int end;

        public static void main(String[] args) throws RunnerException {
            Options opt = new OptionsBuilder()
                    .include(GCOverheadBenchmarkExample.Maps.class.getSimpleName())
                    //.addProfiler(StackProfiler.class)
                    .addProfiler(WinPerfAsmProfiler.class)
                    .build();

            new Runner(opt).run();
        }

        @Setup
        public void setup() {
            switch (type) {
                case "hashmap":
                    map = new HashMap<>();
                    break;
                case "treemap":
                    map = new TreeMap<>();
                    break;
                default:
                    throw new IllegalStateException("Unknown type: " + type);
            }

            begin = 1;
            end = 256;
            for (int i = begin; i < end; i++) {
                map.put(i, i);
            }
        }

        /*
         * ============================== HOW TO RUN THIS TEST: ====================================
         *
         * You can run this test:
         *
         * a) Via the command line:
         *    $ mvn clean install
         *    $ java -jar target/benchmarks.jar JMHSample_35.*Maps -prof stack
         *    $ java -jar target/benchmarks.jar JMHSample_35.*Maps -prof gc
         *
         * b) Via the Java API:
         *    (see the JMH homepage for possible caveats when running from IDE:
         *      http://openjdk.java.net/projects/code-tools/jmh/)
         */

        @Benchmark
        public void test(Blackhole bh) {
            for (int i = begin; i < end; i++) {
                bh.consume(map.get(i));
            }
        }
    }
}
