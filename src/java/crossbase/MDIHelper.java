package crossbase;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class MDIHelper
{
	private native static String readFromPipe(String name);
	private native static boolean writeToPipe(String name, String data);

	private boolean filesSent = false;
	private volatile boolean finishListening = false;
	private Listener fileHandler;
	
	private Thread filesListener = new Thread(new Runnable()
	{
		@Override
		public void run()
		{
			while (!finishListening)
			{
				String res = readFromPipe("\\\\.\\pipe\\mynamedpipe");
				String[] resParsed = res.split("\n");
				if (resParsed.length > 0 && resParsed[0].equals("FILES"))
				{
					for (int i = 1; i < resParsed.length; i++)
					{
						final String fileName = resParsed[i];
						Display.getDefault().asyncExec(new Runnable()
						{
							@Override
							public void run()
							{
								Event evt = new Event();
								evt.text = fileName;
								fileHandler.handleEvent(evt);
							}
						});
					}
				}
			}	
		}	
	});
	

	public MDIHelper(String[] fileNames, Listener fileHandler)
	{
		this.fileHandler = fileHandler;
		
		System.out.println("Trying to send file names to our other instance...");
		String files = "FILES\n";
		for (int i = 0; i < fileNames.length; i++)
		{
			files += fileNames[i] + "\n";
		}
		
		if (writeToPipe("\\\\.\\pipe\\mynamedpipe", files))
		{
			System.out.println("We have successfully sent the files. Bye.");
			filesSent = true;
		}
		else
		{
			filesListener.start();
		}
		
	}
	
	public boolean areFilesSent()
	{
		return filesSent;
	}
	
	public void stop()
	{
		if (filesListener.isAlive())
		{
			// Stopping the server
			finishListening = true;
			writeToPipe("\\\\.\\pipe\\mynamedpipe", "BOO!");	// This is for unblocking the reading
		}		
	}
}
