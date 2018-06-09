package org.jooq.tools;

public class Minutes extends StopWatchTime{
	private String time;
	
	public Minutes (String time) {
		this.time = time;
	}
	
	@Override
	public String getTime() {
		return this.time;
	}
}
