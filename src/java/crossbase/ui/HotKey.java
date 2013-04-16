package crossbase.ui;

import org.eclipse.swt.SWT;

public class HotKey
{
	public static final int MOD1  = 1;
	public static final int ALT   = 2;
	public static final int CTRL  = 4;
	public static final int SHIFT = 8;
	
	private int modifiers;
	private char code;
	
	public HotKey(int modifiers, char code)
	{
		this.modifiers = modifiers;
		this.code = code;
	}
	
	public int toAccelerator()
	{
		int res = 0;
		if ((modifiers & MOD1) != 0) res |= SWT.MOD1;
		if ((modifiers & CTRL) != 0) res |= SWT.CTRL;
		if ((modifiers & SHIFT) != 0) res |= SWT.SHIFT;
		if ((modifiers & ALT) != 0) res |= SWT.ALT;
		res += code;
		return res;
	}
	
	@Override
	public String toString() 
	{
		boolean isOSX = SWT.getPlatform().equals("cocoa");
		String res = "";
		
		if ((modifiers & MOD1) != 0 && !isOSX)
		{
			res += "Ctrl+";
		}
		
		if ((modifiers & CTRL) != 0)
		{
			if (isOSX)
				res += "^";
			else
				res += "Ctrl+";
		}
		
		if ((modifiers & ALT) != 0)
		{
			if (isOSX)
				res += "⌥";
			else
				res += "Alt+";
		}

		if ((modifiers & SHIFT) != 0)
		{
			if (isOSX)
				res += "⇧";
			else
				res += "Shift+";
		}
		
		if ((modifiers & MOD1) != 0 && isOSX)
		{
			res += "⌘";
		}

		res += Character.toString(code);
		
		return res;
	}
}
