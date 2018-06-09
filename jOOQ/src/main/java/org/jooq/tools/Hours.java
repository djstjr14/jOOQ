package org.jooq.tools;

public class Hours extends StopWatchTime{
	private String time;
	
	public Hours (String time) {
		this.time = time;
	}
	
	@Override
	public String getTime() {
		return this.time;
	}
}
