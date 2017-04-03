package huffman;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HuffmanTree<T> {

	private final Set<? extends T> vocabulary;
	private final List<? extends T> data;
	private final InternalNode<? extends T> rootNode;
	private final Map<T,Byte[]> huffmanTable;

	private PriorityQueue<LeafNode<? extends T>> leafNodes;

	public HuffmanTree(Set<? extends T> vocabulary, List<? extends T> data) {
		super();
		this.vocabulary = vocabulary;
		this.data = data;
		this.leafNodes = createLeafNodes();
		this.rootNode = createRoot();
		this.huffmanTable = representCodesAsByteArr(createHuffmanTable());
	}
	
	public PriorityQueue<LeafNode<? extends T>> getLeafNodes() {
		return leafNodes;
	}

	public void setLeafNodes(PriorityQueue<LeafNode<? extends T>> leafNodes) {
		this.leafNodes = leafNodes;
	}

	public Set<? extends T> getVocabulary() {
		return vocabulary;
	}

	public List<? extends T> getData() {
		return data;
	}


	public InternalNode<? extends T> getRootNode() {
		return rootNode;
	}

	public Map<T, Byte[]> getHuffmanTable() {
		return huffmanTable;
	}

	private PriorityQueue<LeafNode<? extends T>> createLeafNodes() {
		return vocabulary.stream().map(word -> {
			return new LeafNode<T>(word, data.stream().filter(w -> w.equals(word)).count(), null);
		}).collect(Collectors.toCollection(PriorityQueue::new));
	}

	private InternalNode<? extends T> createRoot() {

		PriorityQueue<HuffmanNode> nodes = new PriorityQueue<>(leafNodes);
		return (InternalNode<? extends T>) iterateTroughNodes(nodes).poll();

	}

	private Map<T, List<Boolean>> createHuffmanTable() {

		return asignCodes(this.rootNode, new HashMap<T, List<Boolean>>(), new LinkedList<Boolean>() {
			{
				add(false);
			}
		}, false);

	}

	private Map<T, List<Boolean>> asignCodes(HuffmanNode node, Map<T, List<Boolean>> table,
			List<Boolean> code, Boolean lr) {

		List<Boolean> symbolCode = new LinkedList<>(code);
		symbolCode.add(lr);
		if (node.isLeaf()) {
			LeafNode<T> leaf = (LeafNode<T>) node;
			return new HashMap<T, List<Boolean>>(table) {
				{
					put((T) leaf.getSymbol(), symbolCode);
				}
			};
		} else if (!node.isLeaf()) {
			InternalNode<T> internalNode = (InternalNode<T>) node;
			return concatenate(asignCodes(internalNode.getRightChild(), table, symbolCode, true),
					asignCodes(internalNode.getLeftChild(), table, symbolCode, false));
		}
		return table;
	}

	private static <U, V, K> Map<U, V> concatenate(Map<U, V> map1, Map<K, V> map2) {
		Map<U, V> map3 = new HashMap<>();
		map3.putAll(map1);
		map3.putAll((Map<? extends U, ? extends V>) map2);
		return map3;
	}

	private PriorityQueue<HuffmanNode> iterateTroughNodes(PriorityQueue<HuffmanNode> nodes) {

		return nodes.size() == 1 ? nodes : iterateTroughNodes(createInternalNodeAndReturnNewQueue(nodes));

	}

	private PriorityQueue<HuffmanNode> createInternalNodeAndReturnNewQueue(PriorityQueue<HuffmanNode> queue) {
		PriorityQueue<HuffmanNode> nQueue = new PriorityQueue<>(queue);
		HuffmanNode leftChild = nQueue.poll();
		HuffmanNode rightChild = nQueue.poll();
		InternalNode<? extends T> internalNode = new InternalNode<>(leftChild, rightChild, null);
		leftChild.setParent(internalNode);
		rightChild.setParent(internalNode);
		nQueue.add(internalNode);
		return nQueue;
	}
	
	private Map<T,Byte[]> representCodesAsByteArr(Map<T,List<Boolean>> huffmanTable){
		return new HashMap<T,Byte[]>(){{
			huffmanTable.entrySet().forEach(entry->{
				T key = entry.getKey();
				put(key, generateCodeFromListOfBooleans(key, entry.getValue()));
			});
		}};
	}

	private Byte[] generateCodeFromListOfBooleans(T symbol, List<Boolean> code) {
		int offset = code.size() % 8;
		for (int i = 0; i < 8 - offset; i++) {
			code.add(false);
		}
		int size = code.size() / 8;
		Byte[] symbolCode = new Byte[size];
		for (int index = 0; index < size-1; index++) {
			byte b = generateByte(code.subList(index*8, (index+1)*8));
			symbolCode[index] = b;
		}
		return symbolCode;
	}

	private static Byte generateByte(List<Boolean> subSection) {
		Byte b = 0x00;
		for (int index = 0; index < subSection.size(); index++) {
			Byte mask = (byte) ((subSection.get(index) ? 0x80 : 0x00) >>> index);
			b = (byte) (b | mask);
		}
		return b;
	}

	private static <T> Map<T, String> stringRepresentationOfCodes(Map<? extends T, List<Boolean>> table) {
		return new HashMap<T, String>() {
			{
				table.entrySet().stream().forEach(entry -> put(entry.getKey(), strRepr(entry.getValue())));
			}
		};
	}

	private static String strRepr(List<Boolean> code) {
		StringBuilder builder = new StringBuilder();
		code.forEach(bool -> {
			builder.append(bool ? "1" : "0");
		});
		return builder.toString();
	}

	public void printLeafNodes() {
		for (LeafNode<? extends T> node : leafNodes) {
			System.out.println("Symbol: " + node.getSymbol() + " Frequency:" + node.getFrequency());
		}

		// while(!leafNodes.isEmpty()){
		// LeafNode<? extends T> poll = leafNodes.poll();
		// System.out.println(poll);
		// }
	}

	public static void main(String[] args) {
		List<Integer> data = Arrays.asList(1, 1, 3, 3, 9, 3, 1, 4, 5, 6, 7, 1, 10, 10, 4, 7, 6, 5, 5, 5, 7, 6, 10, 8, 4,
				1, 2, 2, 2, 2, 3);
		Set<Integer> vocabulary = IntStream.rangeClosed(1, 10).boxed().collect(Collectors.toSet());

		HuffmanTree<Integer> treeOfInts = new HuffmanTree<>(vocabulary, data);
		// treeOfInts.printLeafNodes();
		// System.out.println(treeOfInts.createRoot());
		 System.out.println(stringRepresentationOfCodes(treeOfInts.createHuffmanTable()));
		//System.out.println(treeOfInts.getHuffmanTable());

		// System.out.println(Integer.toHexString(
		// HuffmanTree.generateByte(Arrays.asList(true, true, true, false, true,
		// true, true, false))));

	}

}
