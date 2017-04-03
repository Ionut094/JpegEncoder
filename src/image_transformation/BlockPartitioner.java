package image_transformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockPartitioner {

	public final static String[] YCrCbkeys = { "Y", "Cr", "Cb" };

	// Default width and height of blocks
	private int N = 8;

	public Map<String, List<double[][]>> partitionBlocks(String[] keys, Map<String, double[][]> listOfMatrixes) {

		Map<String, List<double[][]>> blocks = new HashMap<>();

		Arrays.asList(keys).forEach(key -> {
			blocks.put(key, extractBlocks(listOfMatrixes.get(key)));
		});
		return blocks;
	}

	private List<double[][]> extractBlocks(double[][] matrix) {

		List<double[][]> blocks = new ArrayList<>();

		for (int i = 0; i < matrix.length; i += N) {
			for (int j = 0; j < matrix[0].length; j += N) {
				blocks.add(substractBlock(matrix, i, j));
			}
		}
		return blocks;
	}

	private double[][] substractBlock(double[][] matrix, int xOffset, int yOffset) {
		double[][] subSection = new double[N][N];
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				subSection[i][j] = matrix[i + xOffset][j + yOffset];
			}
		}
		return subSection;
	}

}
