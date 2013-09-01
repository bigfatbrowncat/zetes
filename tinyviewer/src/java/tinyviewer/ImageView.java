package tinyviewer;

import org.eclipse.swt.events.PaintEvent;

import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class ImageView extends Canvas
{
	Image image = null;
	Image zoomedImage = null;
	double zoom = 1.0;
	
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
			checkZoomedImage();
			Point viewSize = getSize();
			Point imageSize = new Point(zoomedImage.getBounds().width, zoomedImage.getBounds().height);
			
			int xpos = viewSize.x > imageSize.x ? viewSize.x / 2 - imageSize.x / 2 : 0;
			int ypos = viewSize.y > imageSize.y ? viewSize.y / 2 - imageSize.y / 2 : 0;
			
			gc.drawImage(zoomedImage, xpos, ypos);
		}
	}

	public Point desiredSize()
	{
		if (image == null)
			return new Point(1, 1);
		else
			return new Point((int)(image.getImageData().width * zoom), (int)(image.getImageData().height * zoom));
	}

	protected void checkZoomedImage()
	{
		if (zoomedImage == null || zoomedImage.isDisposed())
		{
			Rectangle bounds = image.getBounds();
			zoomedImage = new Image(image.getDevice(), (int)(bounds.width * zoom), (int)(bounds.height * zoom));
			GC gc = new GC(zoomedImage);
			gc.drawImage(image, 0, 0, bounds.width,               bounds.height, 
	                            0, 0, (int)(bounds.width * zoom), (int)(bounds.height * zoom));
			gc.dispose();
		}
	}
	
	public void setImage(Image image)
	{
		this.image = image;
		if (zoomedImage != null) zoomedImage.dispose();
		this.redraw();
	}
	
	public Image getImage()
	{	
		return image;
	}
	
	public void setZoom(double zoom)
	{
		this.zoom = zoom;
		if (zoomedImage != null) zoomedImage.dispose();
		this.redraw();
	}
	
	public double getZoom()
	{
		return zoom;
	}
}
