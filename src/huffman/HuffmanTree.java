package huffman;

import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HuffmanTree<T> {

	private Set<? extends T> vocabulary;
	private List<? extends T> data;

	private PriorityQueue<LeafNode<? extends T>> leafNodes;

	public HuffmanTree(Set<? extends T> vocabulary, List<? extends T> data) {
		super();
		this.vocabulary = vocabulary;
		this.data = data;
		this.leafNodes = createLeafNodes();
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
				3);
		Set<Integer> vocabulary = IntStream.rangeClosed(1, 10).boxed().collect(Collectors.toSet());

		HuffmanTree<Integer> treeOfInts = new HuffmanTree<>(vocabulary, data);
		//treeOfInts.printLeafNodes();
		System.out.println(treeOfInts.createRoot());
	}

}
