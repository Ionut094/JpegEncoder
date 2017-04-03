package image_transformation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DiscreteCosineTransform {

	private static int[] quantum_luminance = new int[64];
	private final static List<String> keys = Arrays.asList(BlockPartitioner.YCrCbkeys);
	private static final Function<? super double[][], ? extends double[][]> DCTFunction = DiscreteCosineTransform::shiftValuesAndCalculateDCTforSingleMatrix;
	private static final Function<? super double[][], ? extends int[][]> quantizationFunction = DiscreteCosineTransform::createQuantizedBlock;
	
	
	static {
		quantum_luminance[0] = 16;
		quantum_luminance[1] = 11;
		quantum_luminance[2] = 10;
		quantum_luminance[3] = 16;
		quantum_luminance[4] = 24;
		quantum_luminance[5] = 40;
		quantum_luminance[6] = 51;
		quantum_luminance[7] = 61;
		quantum_luminance[8] = 12;
		quantum_luminance[9] = 12;
		quantum_luminance[10] = 14;
		quantum_luminance[11] = 19;
		quantum_luminance[12] = 26;
		quantum_luminance[13] = 58;
		quantum_luminance[14] = 60;
		quantum_luminance[15] = 55;
		quantum_luminance[16] = 14;
		quantum_luminance[17] = 13;
		quantum_luminance[18] = 16;
		quantum_luminance[19] = 24;
		quantum_luminance[20] = 40;
		quantum_luminance[21] = 57;
		quantum_luminance[22] = 69;
		quantum_luminance[23] = 56;
		quantum_luminance[24] = 14;
		quantum_luminance[25] = 17;
		quantum_luminance[26] = 22;
		quantum_luminance[27] = 29;
		quantum_luminance[28] = 51;
		quantum_luminance[29] = 87;
		quantum_luminance[30] = 80;
		quantum_luminance[31] = 62;
		quantum_luminance[32] = 18;
		quantum_luminance[33] = 22;
		quantum_luminance[34] = 37;
		quantum_luminance[35] = 56;
		quantum_luminance[36] = 68;
		quantum_luminance[37] = 109;
		quantum_luminance[38] = 103;
		quantum_luminance[39] = 77;
		quantum_luminance[40] = 24;
		quantum_luminance[41] = 35;
		quantum_luminance[42] = 55;
		quantum_luminance[43] = 64;
		quantum_luminance[44] = 81;
		quantum_luminance[45] = 104;
		quantum_luminance[46] = 113;
		quantum_luminance[47] = 92;
		quantum_luminance[48] = 49;
		quantum_luminance[49] = 64;
		quantum_luminance[50] = 78;
		quantum_luminance[51] = 87;
		quantum_luminance[52] = 103;
		quantum_luminance[53] = 121;
		quantum_luminance[54] = 120;
		quantum_luminance[55] = 101;
		quantum_luminance[56] = 72;
		quantum_luminance[57] = 92;
		quantum_luminance[58] = 95;
		quantum_luminance[59] = 98;
		quantum_luminance[60] = 112;
		quantum_luminance[61] = 100;
		quantum_luminance[62] = 103;
		quantum_luminance[63] = 99;
	}

	public static Map<String, List<double[][]>> calculateDCTforYCrCbBlocks(Map<String, List<double[][]>> yCrCbBlocks) {
		return new HashMap<String, List<double[][]>>() {
			{
				keys.forEach(key -> {
					put(key, applyDCTtoList(yCrCbBlocks.get(key)));
				});
			}
		};
	}

	public static Map<String, List<int[][]>> calculateDCTandQuantizeYCrCbBlocks(
			Map<String, List<double[][]>> yCrCbBlocks) {

		return new HashMap<String, List<int[][]>>() {
			{
				keys.forEach(key -> {
					put(key, applyDCTAndQuantizeBlock(yCrCbBlocks.get(key)));
				});
			}
		};
	}

	private static List<double[][]> applyDCTtoList(List<double[][]> blocks) {
		return blocks.stream().map(DCTFunction).collect(Collectors.toList());
	}

	private static List<int[][]> applyDCTAndQuantizeBlock(List<double[][]> blocks) {
		return blocks.stream().map(DCTFunction.andThen(quantizationFunction))
				.collect(Collectors.toList());
	}

	private static double[][] shiftValuesAndCalculateDCTforSingleMatrix(double[][] matrix) {
		double[][] calculatedMatrix = new double[8][8];
		for (int u = 0; u < 8; u++) {
			double uCoef = (u == 0 ? 1 / Math.sqrt(2) : 1);
			for (int v = 0; v < 8; v++) {
				double sum = 0;
				double vCoef = (v == 0 ? 1 / Math.sqrt(2) : 1);
				for (int x = 0; x < 8; x++) {
					for (int y = 0; y < 8; y++) {
						sum += (matrix[x][y] - 128) * Math.cos((2 * x + 1) * Math.PI * u / 16)
								* Math.cos((2 * y + 1) * Math.PI * v / 16);
					}
				}
				calculatedMatrix[u][v] = vCoef * uCoef * sum / 4;
			}
		}
		return calculatedMatrix;
	}

	private static int[][] createQuantizedBlock(double[][] block) {

		int[][] quantizedBlock = new int[8][8];
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				quantizedBlock[i][j] = (int) Math.round(block[i][j] / quantum_luminance[i * 8 + j]);
			}
		}
		return quantizedBlock;
	}

}
