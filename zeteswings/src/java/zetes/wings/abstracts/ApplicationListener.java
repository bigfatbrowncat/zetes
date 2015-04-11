package zetes.wings.abstracts;

public interface ApplicationListener
{
	void started(Application<?,?,?,?,?> application);
	void stopped(Application<?,?,?,?,?> application);
}
