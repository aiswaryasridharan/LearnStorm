package storm.blueprints.chapter1.v1;

import java.util.concurrent.TimeUnit;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

public class WordCountTopology {

	private static final String SENTENCE_SPOUT_ID = "sentence-spout";
    private static final String SPLIT_BOLT_ID = "split-bolt";
    private static final String COUNT_BOLT_ID = "count-bolt";
    private static final String REPORT_BOLT_ID = "report-bolt";
    private static final String TOPOLOGY_NAME = "word-count-topology";
	
	public static void main(String[] args) throws Exception {
		SentenceSpout spout = new SentenceSpout();
		SplitSentenceBolt splitBolt = new SplitSentenceBolt();
		WordCountBolt countBolt = new WordCountBolt();
		ReportBolt reportBolt = new ReportBolt();
		
		TopologyBuilder builder = new TopologyBuilder();
		
		//register the sentence spout and assign it a unique id
		builder.setSpout(SENTENCE_SPOUT_ID, spout);
		
		//register SplitBolt, establish subscription to stream emitted by SentenceSpout
		//setBolt registers a bolt to TopologyBuilder class. 
		//Returns an instance of BoltDeclarer - exposes methods for defining input sources for a bolt
		//sufflegrouping - tells Storm to shuffle tuples emitted by SentenceSpout, 
		//distribute them evenly across instances of SplitSentenceBolt 
		builder.setBolt(SPLIT_BOLT_ID, splitBolt).shuffleGrouping(SENTENCE_SPOUT_ID);
		
		//establish connection between SplitSentence and WordCount Bolts
		//Tuples containing a certain data be routed to a specific instance of a bolt =  FieldsGrouping
		builder.setBolt(COUNT_BOLT_ID, countBolt).fieldsGrouping(SPLIT_BOLT_ID, new Fields("word"));
		
		//Route the stream of tuples emitted by WordCountBolt to ReportBolt
		//GlobalGrouping = all tuples emitted by WordCountBolt routed to a single ReportBolt task
		builder.setBolt(REPORT_BOLT_ID, reportBolt).globalGrouping(COUNT_BOLT_ID);		
		
		//Extension of HashMap<String, Object> - defines storm-specific constants and convenience methods
		//Configuring a topology's runtime behavior.
		//Topology is submitted => Storm will merge config with its predefined default configuration values
		//Result will be passed to open() and prepare() of spouts and bolts
		//=>Config represents a set of configuration parameters - global to all components in a topology
		Config config = new Config();
		
		//Stimulates a full blown storm cluster within local dev envt (without overhead of deploying to a distributed cluster)
		LocalCluster cluster = new LocalCluster();
		
		cluster.submitTopology(TOPOLOGY_NAME, config, builder.createTopology());
		TimeUnit.SECONDS.sleep(10);
		cluster.shutdown();		
	}

}
