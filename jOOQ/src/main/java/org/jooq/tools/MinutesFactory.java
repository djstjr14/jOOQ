package org.jooq.tools;

public class MinutesFactory implements TimeAbstractFactory{
	private String time;
	
	public MinutesFactory(long time) {
		if(time<10)
			this.time = "0"+time+":";
		else
			this.time = time+":";
	}
	
	public StopWatchTime createTime() {
		return new Minutes(time);
	}
}
