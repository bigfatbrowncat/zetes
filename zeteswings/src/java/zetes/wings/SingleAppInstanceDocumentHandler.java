package zetes.wings;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class SingleAppInstanceDocumentHandler
{
	private static final String pipeName = WinLinMacApi.getAppId() + "_pipe";
	private static final String mutexName = WinLinMacApi.getAppId() + "_mutex";

	@SuppressWarnings("serial")
	public class MutexError extends Error
	{
		MutexError(String message)
		{
			super(message);
		}
	}
	
	@SuppressWarnings("serial")
	public class FileNamesSendingFailed extends Exception
	{
		public FileNamesSendingFailed(String message)
		{
			super(message);
		}
	}
	
	private native static long globalLock(String name);
	private native static boolean globalUnlock(long hMutex);
	private native static boolean isLocked(String name);

	private native static String readFromPipe(String name);
	private native static boolean writeToPipe(String name, String data);

	private long globalLockX(String name) throws MutexError
	{
		long hMutex = globalLock(name);
		if (hMutex == 0)
		{
			throw new MutexError("Can't lock the mutex");
		}
		return hMutex;
	}
	
	private void globalUnlockX(long hMutex) throws MutexError
	{
		if (!globalUnlock(hMutex))
		{
			throw new MutexError("Can't unlock the mutex");
		}
	}
	
	private boolean isServer = false;
	private volatile boolean finishListening = false;
	private Listener fileHandler;

	private long hMutex;
	
	private Thread filesListener = new Thread(new Runnable()
	{
		@Override
		public void run()
		{
			while (!finishListening)
			{
				// TODO Change pipe name!!!
				String res = readFromPipe(pipeName);
				{
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
									try
									{
										Event evt = new Event();
										evt.text = fileName;
										fileHandler.handleEvent(evt);
									}
									catch (Exception e)
									{
										e.printStackTrace();
									}
								}
							});
						}
					}
				}
				
			}	
		}	
	});
	
	
	public SingleAppInstanceDocumentHandler(String[] fileNames, final Listener fileHandler) throws FileNamesSendingFailed
	{
		this.fileHandler = fileHandler;

		// TODO Change mutex name!!!
		if (isLocked(mutexName))
		{
			isServer = false;
			
			System.out.println("We are not the first instance. Trying to send file names to the first one...");
			String files = "FILES\n";
			for (int i = 0; i < fileNames.length; i++)
			{
				files += fileNames[i] + "\n";
			}

			// TODO Change pipe name!!!
			if (writeToPipe(pipeName, files))
			{
				System.out.println("We have successfully sent the files. Bye.");
			}
			else
			{
				System.out.println("We tried hard, but couldn't send the files. Maybe something's wrong with the server.");
				throw new FileNamesSendingFailed("Couldn't send the file names");		
			}
		}
		else
		{
			isServer = true;

			System.out.println("We are the first instance...");
			// TODO Change mutex name!!!
			hMutex = globalLockX(mutexName);
	
			filesListener.start();

			// Opening the files from our command line 
			for (int i = 0; i < fileNames.length; i++)
			{
				final Event evt = new Event();
				evt.text = fileNames[i];
				Display.getDefault().asyncExec(new Runnable()
				{
					
					@Override
					public void run()
					{
						fileHandler.handleEvent(evt);						
					}
				});
							
			}
		}
	}
	
	public boolean isServer()
	{
		return isServer;
	}
	
	public void stop()
	{
		if (filesListener.isAlive())
		{
			// Stopping the server
			finishListening = true;
			// TODO Change pipe name!!!
			writeToPipe(pipeName, "BOO!");	// This is to unblock reading
			globalUnlockX(hMutex);
		}
	}
	
	@Override
	protected void finalize() throws Throwable
	{
		stop();
		super.finalize();
	}
}
