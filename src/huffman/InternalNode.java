package huffman;

public class InternalNode<T> extends HuffmanNode {

	private HuffmanNode leftChild;
	private HuffmanNode rightChild;
	
	public InternalNode(HuffmanNode leftChild,HuffmanNode rightChild,HuffmanNode parent) {
		super(leftChild.getFrequency()+rightChild.getFrequency(),parent);
		this.leftChild = leftChild;
		this.rightChild = rightChild;
	}

	public HuffmanNode getLeftChild() {
		return leftChild;
	}

	public void setLeftChild(HuffmanNode leftChild) {
		this.leftChild = leftChild;
	}

	public HuffmanNode getRightChild() {
		return rightChild;
	}

	public void setRightChild(HuffmanNode rightChild) {
		this.rightChild = rightChild;
	}

	@Override
	public String toString() {
		return "InternalNode [leftChild=" + leftChild + ", rightChild=" + rightChild + "]";
	}
	
}
