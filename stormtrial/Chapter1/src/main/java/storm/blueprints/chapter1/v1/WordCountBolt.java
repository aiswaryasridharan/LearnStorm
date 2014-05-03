package storm.blueprints.chapter1.v1;

import java.util.HashMap;
import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class WordCountBolt extends BaseRichBolt{
	private OutputCollector collector;
	private HashMap<String, Long> counts = null;
	
	//Best practice to instantiate all non-serializable objects in prepare method.
	//Limit constructor initialization to primitives and serializable objects
	//Could fail deployment, if not, and throw NotSerializableException.
	public void prepare(Map config, TopologyContext context, OutputCollector collector){
		this.collector = collector;
		this.counts = new HashMap<String, Long>();
	}
	
	//look up count for word received, increment and store count.
	//emit a new tuple, consisting of the word and the count for that word.
	//why emit count as a stream? allow other bolts in the topology to subscribe to the stream for further processing.
	public void execute(Tuple tuple){
		String word = tuple.getStringByField("word");
		Long count = this.counts.get(word);
		
		if(count == null){
			count = 0L;
		}
		count ++;
		this.counts.put(word, count);
		this.collector.emit(new Values(word, count));
	}
	
	//declares a stream - contain both the word received and corresponding count
	public void declareOutputFields(OutputFieldsDeclarer declarer){
		declarer.declare(new Fields("word", "count"));
	}
}
