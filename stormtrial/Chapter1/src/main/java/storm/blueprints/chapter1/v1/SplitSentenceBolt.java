package storm.blueprints.chapter1.v1;

import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class SplitSentenceBolt extends BaseRichBolt {
	//BaseRichBolt = convenience class implementing IComponent and IBolt
	private OutputCollector collector;
	
	//defined by IBolt. Analogous to open() in ISpout. 
	//Prepare resources such as database connections during bolt initializations.
	//here - saves a reference to the OutputCollector object
	public void prepare(Map config, TopologyContext context, OutputCollector collector){
		this.collector = collector;
	}
	
	//defined by IBolt. called every time the bolt receives a tuple from a stream to which it subscribes
	//here - looks up the value of "sentence" field of incoming tuple as a string, splits the value into individual words
	//emits a new tuple 
	public void execute(Tuple tuple){
		String sentence = tuple.getStringByField("sentence");
		String[] words = sentence.split(" ");
		for(String word: words){
			this.collector.emit(new Values(word));
		}
	}
	
	//declares a single stream of tuples, each containing one field -"word"
	public void declareOutputFields(OutputFieldsDeclarer declarer){
		declarer.declare(new Fields("word"));
	}
}
