package jpeg;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import helperClasses.IntTuple;
import huffman.HuffmanTree;
import image_transformation.BlockPartitioner;
import image_transformation.ComponentsExtractor;
import image_transformation.DiscreteCosineTransform;

public class JpegEncoder {

	private final static IntTuple[] zigzagPositions;
	private final Map<Integer,Byte[]> huffmanTable;
	private final List<Byte[]> codedData;

	static {
		zigzagPositions = new IntTuple[64];
		zigzagPositions[0] = IntTuple.tupleOf(0, 0);
		zigzagPositions[1] = IntTuple.tupleOf(0, 1);
		zigzagPositions[2] = IntTuple.tupleOf(1, 0);
		zigzagPositions[3] = IntTuple.tupleOf(2, 0);
		zigzagPositions[4] = IntTuple.tupleOf(1, 1);
		zigzagPositions[5] = IntTuple.tupleOf(0, 2);
		zigzagPositions[6] = IntTuple.tupleOf(0, 3);
		zigzagPositions[7] = IntTuple.tupleOf(1, 2);
		zigzagPositions[8] = IntTuple.tupleOf(2, 1);
		zigzagPositions[9] = IntTuple.tupleOf(3, 0);
		zigzagPositions[10] = IntTuple.tupleOf(4, 0);
		zigzagPositions[11] = IntTuple.tupleOf(3, 1);
		zigzagPositions[12] = IntTuple.tupleOf(2, 2);
		zigzagPositions[13] = IntTuple.tupleOf(1, 3);
		zigzagPositions[14] = IntTuple.tupleOf(0, 4);
		zigzagPositions[15] = IntTuple.tupleOf(0, 5);
		zigzagPositions[16] = IntTuple.tupleOf(1, 4);
		zigzagPositions[17] = IntTuple.tupleOf(2, 3);
		zigzagPositions[18] = IntTuple.tupleOf(3, 2);
		zigzagPositions[19] = IntTuple.tupleOf(4, 1);
		zigzagPositions[20] = IntTuple.tupleOf(5, 0);
		zigzagPositions[21] = IntTuple.tupleOf(6, 0);
		zigzagPositions[22] = IntTuple.tupleOf(5, 1);
		zigzagPositions[23] = IntTuple.tupleOf(4, 2);
		zigzagPositions[24] = IntTuple.tupleOf(3, 3);
		zigzagPositions[25] = IntTuple.tupleOf(2, 4);
		zigzagPositions[26] = IntTuple.tupleOf(1, 5);
		zigzagPositions[27] = IntTuple.tupleOf(0, 6);
		zigzagPositions[28] = IntTuple.tupleOf(0, 7);
		zigzagPositions[29] = IntTuple.tupleOf(1, 6);
		zigzagPositions[30] = IntTuple.tupleOf(2, 5);
		zigzagPositions[31] = IntTuple.tupleOf(3, 4);
		zigzagPositions[32] = IntTuple.tupleOf(4, 3);
		zigzagPositions[33] = IntTuple.tupleOf(5, 2);
		zigzagPositions[34] = IntTuple.tupleOf(6, 1);
		zigzagPositions[35] = IntTuple.tupleOf(7, 0);
		zigzagPositions[36] = IntTuple.tupleOf(7, 1);
		zigzagPositions[37] = IntTuple.tupleOf(6, 2);
		zigzagPositions[38] = IntTuple.tupleOf(5, 3);
		zigzagPositions[39] = IntTuple.tupleOf(4, 4);
		zigzagPositions[40] = IntTuple.tupleOf(3, 5);
		zigzagPositions[41] = IntTuple.tupleOf(2, 6);
		zigzagPositions[42] = IntTuple.tupleOf(1, 7);
		zigzagPositions[43] = IntTuple.tupleOf(2, 7);
		zigzagPositions[44] = IntTuple.tupleOf(3, 6);
		zigzagPositions[45] = IntTuple.tupleOf(4, 5);
		zigzagPositions[46] = IntTuple.tupleOf(5, 4);
		zigzagPositions[47] = IntTuple.tupleOf(6, 3);
		zigzagPositions[48] = IntTuple.tupleOf(7, 2);
		zigzagPositions[49] = IntTuple.tupleOf(7, 3);
		zigzagPositions[50] = IntTuple.tupleOf(6, 4);
		zigzagPositions[51] = IntTuple.tupleOf(5, 5);
		zigzagPositions[52] = IntTuple.tupleOf(4, 6);
		zigzagPositions[53] = IntTuple.tupleOf(3, 7);
		zigzagPositions[54] = IntTuple.tupleOf(4, 7);
		zigzagPositions[55] = IntTuple.tupleOf(5, 6);
		zigzagPositions[56] = IntTuple.tupleOf(6, 5);
		zigzagPositions[57] = IntTuple.tupleOf(7, 4);
		zigzagPositions[58] = IntTuple.tupleOf(7, 5);
		zigzagPositions[59] = IntTuple.tupleOf(6, 6);
		zigzagPositions[60] = IntTuple.tupleOf(5, 7);
		zigzagPositions[61] = IntTuple.tupleOf(6, 7);
		zigzagPositions[62] = IntTuple.tupleOf(7, 6);
		zigzagPositions[63] = IntTuple.tupleOf(7, 7);

	}

	public JpegEncoder(String filename) {
		/*
		 * Extract Y Cr Cb components from image Filename represents the
		 * complete path to the image + extension of the image
		 */
		ComponentsExtractor extractor = ComponentsExtractor.createComponentsExtractor(filename);
		Map<String, double[][]> yCrCbComponents = extractor.extractYCrCbComponents();

		/*
		 * Partition the 3 matrixes into 8x8 blocks The keys of the map are Y,Cr
		 * and Cb
		 */
		BlockPartitioner blockPartitioner = new BlockPartitioner();
		Map<String, List<double[][]>> partitionedBlocks = blockPartitioner.partitionBlocks(BlockPartitioner.YCrCbkeys,
				yCrCbComponents);

		/*
		 * Applies the discrete cosine transform and quantization on each block
		 * The map contains the keys for each component
		 */
		DiscreteCosineTransform discreteCosineTransformer = new DiscreteCosineTransform();
		Map<String, List<int[][]>> blocks = discreteCosineTransformer
				.calculateDCTandQuantizeYCrCbBlocks(partitionedBlocks);
		
		/* Based on the 3 matrixes create a list containing all the data*/
		List<Integer> data = applyZigZagToComponents(blocks);
		Set<Integer> vocabulary = createVocabulary(data);
		
		/* Create Huffman Tree and get the associated huffman table*/
		HuffmanTree<Integer> huffmanTree = new HuffmanTree<>(vocabulary, data);
		this.huffmanTable = huffmanTree.getHuffmanTable();
		
		/*Create list of coded data*/
		this.codedData = replaceSymbols(data, huffmanTable);
	}
	
	public Map<Integer, Byte[]> getHuffmanTable() {
		return huffmanTable;
	}

	public List<Byte[]> getCodedData() {
		return codedData;
	}

	private List<Byte[]> replaceSymbols(List<Integer> data,Map<Integer,Byte[]> huffmanTable){
		return data.stream().map(symbol->huffmanTable.get(symbol)).collect(Collectors.toList());
	}
	
	private List<Integer> applyZigZagToComponents(Map<String, List<int[][]>> blocks) {
		return blocks.entrySet().stream()
								.map(entry -> entry.getValue())
								.flatMap(list -> zigzagedBlocks(list).stream())
								.flatMap(arr->Arrays.asList(boxedArray(arr)).stream())
								.collect(Collectors.toList());
		
	}

	private Integer[] boxedArray(int[] arr) {
		Integer[] arrOfIntegers = new Integer[arr.length];
		for (int i = 0; i < arr.length; i++) {
			arrOfIntegers[i] = arr[i];
		}
		return arrOfIntegers;
	}

	private static List<int[]> zigzagedBlocks(List<int[][]> blocks) {
		return blocks.stream().map(JpegEncoder::zigzagBlock).collect(Collectors.toList());
	}

	private static int[] zigzagBlock(int[][] matrix) {
		int[] block = new int[64];
		for (int i = 0; i < 64; i++) {
			IntTuple position = zigzagPositions[i];
			block[i] = matrix[position.getY()][position.getX()];
		}
		return block;
	}
	
	private Set<Integer> createVocabulary(List<Integer> data){
		return data.stream().distinct().collect(Collectors.toSet());
	}
	
	public static void main(String[] args) {
		/*
		 * 
		 * 
		 * 
		 * 
		 * 
		 * */
		
		JpegEncoder encoder = new JpegEncoder("C:/Users/Iancu422/Desktop/muaddibthrone.jpg");
		List<Byte[]> data = encoder.getCodedData();
		data.forEach(b->System.out.println(b));
		
	}

}
