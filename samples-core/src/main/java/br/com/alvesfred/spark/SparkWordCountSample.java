package br.com.alvesfred.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

import java.util.Arrays;

/**
 * Sample code for Spark API - count words
 *
 * @author fred
 *
 */
public class SparkWordCountSample {
	@SuppressWarnings("serial")
	private static final FlatMapFunction<String, String> WORDS_MAP = 
			new FlatMapFunction<String, String>() {

		@Override
		public Iterable<String> call(String s) throws Exception {
			return Arrays.asList(s.split(" "));
		}
	};

	@SuppressWarnings("serial")
	private static final PairFunction<String, String, Integer> WORDS_TUPLE = 
			new PairFunction<String, String, Integer>() {

		@Override
		public Tuple2<String, Integer> call(String s) throws Exception {
			return new Tuple2<String, Integer>(s, 1);
		}
	};

	@SuppressWarnings("serial")
	private static final Function2<Integer, Integer, Integer> WORDS_REDUCER = 
			new Function2<Integer, Integer, Integer>() {

		@Override
		public Integer call(Integer a, Integer b) throws Exception {
			return a + b;
		}
	};

	private static final void COUNT(String fileName) {
		//SparkConf conf = new SparkConf().setAppName(
		//		SparkWordCountSample.class.getName()).setMaster("Samples");
		SparkConf conf = new SparkConf().setAppName("samples-core")
				.setMaster("samples-core-spark");
			    //.setMaster("spark://localhost:4041").set("spark.ui.port","4041");

		@SuppressWarnings("resource")
		JavaSparkContext context = new JavaSparkContext(conf);

		JavaRDD<String> file = context.textFile(fileName);
		JavaRDD<String> words = file.flatMap(WORDS_MAP);
		JavaPairRDD<String, Integer> pairs = words.mapToPair(WORDS_TUPLE);
		JavaPairRDD<String, Integer> counter = pairs.reduceByKey(WORDS_REDUCER);

		pairs.saveAsTextFile("spark-output-pairs.txt");
		counter.saveAsTextFile("spark-output-counter.txt");
	}

	public static void main(String[] args) {
		//if (args.length < 1) {
		//	System.err.println(
		//			"Please provide the input file!");
		//	System.exit(0);
		//}
		
		//COUNT(args[0]);
		COUNT("samples.txt");
	}
}
