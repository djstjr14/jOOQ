package org.jooq.tools;

public class HoursFactory implements TimeAbstractFactory{
	private String time;
	
	public HoursFactory(long time) {
		if(time<10)
			this.time = "0"+time+":";
		else
			this.time = time+":";
	}
	public StopWatchTime createTime() {
		return new Hours(time);
	}
}
