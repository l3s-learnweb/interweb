package de.l3s.interwebj.connector.bing;

import java.awt.image.BufferedImage;

import gui.ava.html.image.generator.HtmlImageGenerator;

public class Test {
	public static void main(String[] args) {
		HtmlImageGenerator imageGenerator = new HtmlImageGenerator();
		
		//imageGenerator.loadHtml("<b>Hello World!</b> Please goto <a title=\"Goto Google\" href=\"http://www.google.com\">Google</a>.");
		imageGenerator.loadUrl("http://vingrad.ru");
		BufferedImage img = imageGenerator.getBufferedImage();
		int with = img.getWidth();
		/*
		BufferedImage scaledImage = new BufferedImage(
				width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics2D = scaledImage.createGraphics();
			AffineTransform xform = AffineTransform.getScaleInstance(scale, scale);
			graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			graphics2D.drawImage(image, xform, null);
			graphics2D.dispose();
			*/
		imageGenerator.saveAsImage("hello-world.png");
		imageGenerator.saveAsHtmlWithMap("hello-world.html", "hello-world.png");
	}
}
