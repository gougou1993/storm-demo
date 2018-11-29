package com.zhong;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;

/**
 * 
 * @author admin
 *
 */
public class App {

	private static final String str1 = "test1";
	private static final String str2 = "test2";

	public static void main(String[] args) {

		// 定义一个拓扑
		TopologyBuilder builder = new TopologyBuilder();

		// 设置一个Executeor(线程)，默认一个
		builder.setSpout(str1, new TestSpout());

		builder.setBolt(str2, new TestBolt(), 1).setNumTasks(1).shuffleGrouping(str1);

		Config conf = new Config();

		conf.put("test", "test");// 设置配置

		try {
			// 运行拓扑
			if (args != null && args.length > 0) { // 有参数时，表示向集群提交作业，并把第一个参数当做topology名称
				System.out.println("远程模式");
				StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
			} else {// 没有参数时，本地提交
				// 启动本地模式
				System.out.println("本地模式");
				LocalCluster cluster = new LocalCluster();
				
				cluster.submitTopology("topology-name", conf, builder.createTopology());
				
				Thread.sleep(10000);
				
				// 关闭本地集群
				cluster.shutdown();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
