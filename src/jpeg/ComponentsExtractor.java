package jpeg;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class ComponentsExtractor {

	private final BufferedImage image;
	private int width;
	private int height;
	private Map<String, int[][]> components = new HashMap<>();

	private int[][] red;
	private int[][] green;
	private int[][] blue;

	private static double[][] transformationCoeficients = new double[3][3];
	private static int[][] offsets = new int[1][3];

	private int heightOffset;
	private int widthOffset;

	static {
		transformationCoeficients[0][0] = 0.299;
		transformationCoeficients[0][1] = 0.587;
		transformationCoeficients[0][2] = 0.114;
		transformationCoeficients[1][0] = -0.169;
		transformationCoeficients[1][1] = -0.331;
		transformationCoeficients[1][2] = 0.500;
		transformationCoeficients[2][0] = 0.500;
		transformationCoeficients[2][1] = -0.419;
		transformationCoeficients[2][2] = -0.081;

		offsets[0][0] = 0;
		offsets[0][1] = 128;
		offsets[0][2] = 128;
	}

	public static ComponentsExtractor createComponentsExtractor(String filename) {
		try {
			return new ComponentsExtractor(filename);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Map<String, int[][]> extractRGBComponents() {

		components.put("Red", this.red);
		components.put("Green", this.green);
		components.put("Blue", this.blue);

		return components;

	}

	public Map<String, double[][]> extractYCrCbComponents(String filePath) {
		if (!this.components.isEmpty()) {
			return calculateYCrCb(components, width, height);
		}
		return calculateYCrCb(extractRGBComponents(), width, height);
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	private ComponentsExtractor(String filename) throws IOException {
		File inputFile = new File(filename);
		image = ImageIO.read(inputFile);

		width = image.getWidth();
		height = image.getHeight();

		adjustHeightAndWidth();
		extractRGBComponentsFromImage();
	}

	private void adjustHeightAndWidth() {
		heightOffset = this.height % 8;
		if (heightOffset != 0) {
			this.height = this.height + 8 - heightOffset;
		}
		widthOffset = this.width % 8;
		if (widthOffset != 0) {
			this.width = this.width + 8 - widthOffset;
		}
	}

	private void extractRGBComponentsFromImage() {

		adjustHeightAndWidth();

		red = new int[height][width];
		green = new int[height][width];
		blue = new int[height][width];

		for (int i = 0; i < height - heightOffset; i++) {
			for (int j = 0; j < width - widthOffset; j++) {
				Color color = new Color(image.getRGB(j, i));
				red[i][j] = color.getRed();
				green[i][j] = color.getGreen();
				blue[i][j] = color.getBlue();
			}
		}
	}

	private static Map<String, double[][]> calculateYCrCb(Map<String, int[][]> components, int width, int height) {

		Map<String, double[][]> yCrCbComponents = new HashMap<>();

		int red[][] = components.get("Red");
		int green[][] = components.get("Green");
		int blue[][] = components.get("Blue");

		double Y[][] = new double[height][width];
		double Cr[][] = new double[height][width];
		double Cb[][] = new double[height][width];

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {

				Y[i][j] = offsets[0][0] + transformationCoeficients[0][0] * red[i][j]
						+ transformationCoeficients[0][1] * green[i][j] + transformationCoeficients[0][2] * blue[i][j];
				Cr[i][j] = offsets[0][1] + transformationCoeficients[1][0] * red[i][j]
						+ transformationCoeficients[1][1] * green[i][j] + transformationCoeficients[1][2] * blue[i][j];

				Cb[i][j] = offsets[0][2] + transformationCoeficients[2][0] * red[i][j]
						+ transformationCoeficients[2][1] * green[i][j] + transformationCoeficients[2][2] * blue[i][j];

			}
		}

		yCrCbComponents.put("Y", Y);
		yCrCbComponents.put("Cr", Cr);
		yCrCbComponents.put("Cb", Cb);

		return yCrCbComponents;
	}

}
