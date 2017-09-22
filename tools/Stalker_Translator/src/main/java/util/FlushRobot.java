package util;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import jdk.nashorn.internal.ir.Flags;

public class FlushRobot implements Runnable{
	private boolean flag = false;

	public static void main(String[] args) {
		System.out.println(System.getProperty("os.name"));
	}
	
	@Override
	public void run(){
		// TODO Auto-generated method stub
		try {
			Robot robot = new Robot();
			Thread.sleep(5000);
			if(System.getProperty("os.name").toLowerCase().contains("windows"))
				flag = true;
			while (flag) {
				robot.keyPress(KeyEvent.VK_RIGHT);
				robot.keyRelease(KeyEvent.VK_RIGHT);
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void stop() {
		flag = false;
	}
}
