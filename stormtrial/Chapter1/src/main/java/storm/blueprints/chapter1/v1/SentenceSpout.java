package storm.blueprints.chapter1.v1;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class SentenceSpout extends BaseRichSpout{
	//BaseRichSpout - convenient implementation of the ISpout and IComponent interfaces
	//Using this class allows us to focus only on the methods we need.
	private SpoutOutputCollector collector;
	
	private String[] sentences = {
			"i work for ebay",
			"we have some shared purposes and values",
			"we follow the shared purposes and values at work",
			"ebay is a great place to work at",
			"ebay is my first real job after masters",
			"masters was another great experience"
	};
	
	private int index = 0;
	
	//declareOutputFields() method is defined in the IComponent interface 
	//all Storm components (spouts and bolts) must implement this 
	//is used to tell Storm what streams a component will emit and the fields each stream's tuples will contain
	public void declareOutputFields(OutputFieldsDeclarer declarer){
		declarer.declare(new Fields("sentence"));
	}
	
	//Defined in ISpout interface, called whenever a spout component is initialized.
	//Map = contains the Storm Configuration
	//TopologyContext object = provides info about components placed in a topology
	//SpoutOutputCollector object = provides methods for emitting tuples.
	public void open(Map config, TopologyContext context, SpoutOutputCollector collector){
		this.collector = collector;
	}
	
	//spout emits tuples to output collector
	public void nextTuple(){
		this.collector.emit(new Values(sentences[index]));
		index++;
		if (index >= sentences.length){
			index = 0;
		}
		try {
			TimeUnit.MILLISECONDS.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
