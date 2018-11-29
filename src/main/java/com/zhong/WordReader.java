package com.zhong;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;


public class WordReader implements IRichSpout {
	
    private SpoutOutputCollector collector;
    private FileReader fileReader;
    private boolean completed = false;
    private TopologyContext context;
    
    public boolean isDistributed() {
    	return false;
    }
    public void ack(Object msgId) {
            System.out.println("OK:"+msgId);
    }
    public void close() {
    	
    }
    public void fail(Object msgId) {
         System.out.println("FAIL:"+msgId);
    }
    /**
     * 这个方法做的惟一一件事情就是分发文件中的文本行
     */
    public void nextTuple() {
    	
    /**
     * 这个方法会不断的被调用，直到整个文件都读完了，我们将等待并返回。
     */
         if(completed){
             try {
                 Thread.sleep(1000);
             } catch (InterruptedException e) {
                 //什么也不做
             }
            return;
         }
         String str;
         //创建reader
         BufferedReader reader = new BufferedReader(fileReader);
         try{
             //读所有文本行
            while((str = reader.readLine()) != null){
            System.out.println(">>>>>>>>>>>>>>>>"+str);
             /**
              * 按行发布一个新值
              */
//               this.collector.emit(new Values(str),str);
                this.collector.emit(new Values(str));
             }
            
         }catch(Exception e){
        	 
             throw new RuntimeException("Error reading tuple",e);
             
         }finally{
             completed = true;
         }
     }
     /**
      * 我们将创建一个文件并维持一个collector对象
      */
     public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
             try {
                 this.context = context;
                 this.fileReader = new FileReader(conf.get("wordsFile").toString());
                 
             } catch (FileNotFoundException e) {
                 throw new RuntimeException("Error reading file ["+conf.get("wordFile")+"]");
             }
             this.collector = collector;
     }
     
     /**
      * 声明输入域"word"
      */
     public void declareOutputFields(OutputFieldsDeclarer declarer) {
    	 
         declarer.declare(new Fields("line"));
         
     }
	@Override
	public void activate() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void deactivate() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}
}

