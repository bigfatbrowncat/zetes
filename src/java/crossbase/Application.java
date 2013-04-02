package crossbase;

public class Application
{
	public static void main(String... args)
	{
		System.out.println("This is a crossplatform monolith application with Java code inside. Freedom to Java apps!");
		for (int i = 0; i < args.length; i++)
		{
			System.out.println("args[" + i + "] = " + args[i]);
		}
	}
}
