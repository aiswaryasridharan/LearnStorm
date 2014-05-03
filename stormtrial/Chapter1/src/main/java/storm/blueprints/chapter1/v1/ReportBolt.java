package storm.blueprints.chapter1.v1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

//Terminal bolt. Only receives tuples. Does not emit anything. **
public class ReportBolt extends BaseRichBolt{

	public HashMap<String, Long> counts = null;
	
	public void prepare(Map config, TopologyContext context, OutputCollector collector){
		this.counts = new HashMap<String, Long>();
	}
	
	public void execute(Tuple tuple){
		String word = tuple.getStringByField("word");
		Long count = tuple.getLongByField("count");
		this.counts.put(word, count);
	}
	
	//** is why this is empty
	public void declareOutputFields(OutputFieldsDeclarer declarer){
		//Nothing is emitted by this bolt.
	}
	
	//Defined in IBolt.Storm calls this when a bolt is about to be shutdown.
	//repurpose it here, to print the result. Typically used to close database connections or release resources used by a bolt.
	//No guarantee that storm will call it when a topology is running on a cluster.
	//In dev mode - cleanup will definitely be called. i think because we will manually kill the run.
	public void cleanup(){
		System.out.print("These are the final counts!!");
		List<String> keys = new ArrayList<String>();
		keys.addAll(this.counts.keySet());
		Collections.sort(keys);
		for (String key: keys){
			System.out.println(key + " : " + this.counts.get(key));
		}
		
		System.out.println("And.... Thats all folks!!");
	}
}
