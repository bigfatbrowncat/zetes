package crossbase.ui;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class ImageView extends Canvas
{
	Image image = null;
	
	public ImageView(Composite arg0, int arg1)
	{
		super(arg0, arg1);
	
		addPaintListener(new PaintListener()
		{
			
			@Override
			public void paintControl(PaintEvent e)
			{
				draw(e.gc);
				
			}
		});
	}
	
	protected void draw(GC gc)
	{
		if (image != null) 
		{
			gc.drawImage(image, 0, 0);
		}
	}

	@Override
	public Point computeSize(int arg0, int arg1)
	{
		if (image == null)
			return super.computeSize(arg0, arg1);
		else
			return new Point(image.getImageData().width, image.getImageData().height);
	}

	public void setImage(Image image)
	{
		this.image = image;
		this.redraw();
	}
	
	public Image getImage()
	{	
		return image;
	}
}
