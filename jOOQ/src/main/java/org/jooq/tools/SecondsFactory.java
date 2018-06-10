package org.jooq.tools;

public class SecondsFactory implements TimeAbstractFactory{
	private String time;
	
	public SecondsFactory(long time) {
		if(time<10)
			this.time = "0"+time;
		else
			this.time = ""+time;
	}
	
	public StopWatchTime createTime() {
		return new Seconds(time);
	}
}
