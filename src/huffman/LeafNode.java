package huffman;

public class LeafNode<T> extends HuffmanNode {

	private final T symbol;
	
	public LeafNode(T symbol, long frequency,HuffmanNode parent) {
		super(frequency,parent);
		this.symbol = symbol;
	}

	public T getSymbol() {
		return symbol;
	}

	@Override
	public String toString() {
		return "LeafNode [symbol=" + symbol + ", getFrequency()=" + getFrequency() + "]";
	}

	@Override
	public boolean isLeaf() {
		return true;
	}
	
}
