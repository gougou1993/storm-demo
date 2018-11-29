package com.zhong;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

/**
 * 
 * Title: TestSpout Description: 发送信息 Version:1.0.0
 * 
 * @author pancm
 * @date 2018年3月6日
 */
public class TestSpout extends BaseRichSpout {

	private static final long serialVersionUID = 225243592780939490L;

	private SpoutOutputCollector collector;

	private boolean completed = false;

	private static final String field = "word";

	private int count = 1;

	private String[] message = { "My nickname is zrs", "My blog address is https://gougou1993.github.io",
			"My interest is playing games" };

	private FileReader fileReader;

	/**
	 * open()方法中是在ISpout接口中定义，在Spout组件初始化时被调用。 有三个参数: 1.Storm配置的Map;
	 * 2.topology中组件的信息; 3.发射tuple的方法;
	 */
	@Override
	public void open(Map conf, TopologyContext arg1, SpoutOutputCollector collector) {
		System.out.println("open:" + conf.get("test"));//
		try {
			this.fileReader = new FileReader(conf.get("wordsFile").toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Error reading file [" + conf.get("wordFile") + "]");
		}
		this.collector = collector;
	}

	/**
	 * nextTuple()方法是Spout实现的核心。 也就是主要执行方法，用于输出信息,通过collector.emit方法发射。
	 */
	@Override
	public void nextTuple() {
		/*
		if (count <= message.length) {
			System.out.println("第" + count + "次开始发送数据...");

			this.collector.emit(new Values(message[count - 1]));
		}
		count++;*/

		/**
		 * 这个方法会不断的被调用，直到整个文件都读完了，我们将等待并返回。
		 */
		
		if(completed) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// 什么也不做
			}
			return;
		}
		String str;
		// 创建reader
		BufferedReader reader = new BufferedReader(fileReader);
		try {
			// 读所有文本行
			while ((str = reader.readLine()) != null) {
				/**
				 * 按行发布一个新值
				 */
				// this.collector.emit(new Values(str),str);
				this.collector.emit(new Values(str));
			}

		} catch (Exception e) {
			
			throw new RuntimeException("Error reading tuple", e);

		} finally {
			completed = true;
		}
	}

	/**
	 * declareOutputFields是在IComponent接口中定义，用于声明数据格式。 即输出的一个Tuple中，包含几个字段。
	 */
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {

		System.out.println("定义格式...");
		declarer.declare(new Fields(field));//

	}

	/**
	 * 当一个Tuple处理成功时，会调用这个方法
	 */
	@Override
	public void ack(Object obj) {
		System.out.println("ack:" + obj);
	}

	/**
	 * 当Topology停止时，会调用这个方法
	 */
	@Override
	public void close() {
		System.out.println("关闭...");
	}

	/**
	 * 当一个Tuple处理失败时，会调用这个方法
	 */
	@Override
	public void fail(Object obj) {
		System.out.println("失败:" + obj);
	}

}