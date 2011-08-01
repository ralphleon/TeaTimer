package goo.TeaTimer;

public class TimerUtils {
	

	/**
	 * Returns the suggested text size for the string. A hack.
	 * @param str the time string
	 * @return the suggested text size to accommodate the string
	 */
	public static int textSize(String str)
	{
		if(str.length() > 5){ 
			return 60;
		}else{
			return 80;
		}
	}
 
	
	/** Converts a millisecond time to a string time 
	 * @param time is the time in milliseconds
	 * @return the formated string
	 */
	public static String time2str(int ms)
	{	
		int [] time = time2Mhs(ms);
 
		if(time[0] == 0){
			return String.format("%02d:%02d",time[1],time[2]);
		}else{
			return String.format("%02d:%02d:%02d",time[0],time[1],time[2]);
		}
	}
 
	/** Creates a time vector
	 *  @param ms the time in milliseconds
	 *  @return [hour,minutes,seconds,ms]
	 */
	public static int [] time2Mhs(int time)
	{
		int ms = time % 1000;
		int seconds = (int) (time / 1000);
		int minutes = seconds / 60;
		int hour = minutes / 60;
 
		minutes = minutes % 60;
   		seconds = seconds % 60;
   		
		int [] timeVec = new int[4];
		timeVec[0] = hour;
		timeVec[1] = minutes;
		timeVec[2] = seconds;
		timeVec[3] = ms;
		return timeVec;
	}
 
	public static String time2humanStr(int time)
	{
		int [] timeVec = time2Mhs(time);
		int hour = timeVec[0], minutes=timeVec[1];
 
   		String r = new String();
   		
   		// Ugly string formating
   		if(hour != 0){	
   			r += hour + " hour";					
   			if(hour == 1) r+= "s";
   			r+= " and ";
   		}
   		
   		r += minutes + " min";
   		if(minutes != 1) r+= "s";
 
   		return r;
	}
}