package huffman;

import java.util.Optional;

public abstract class HuffmanNode implements Comparable<HuffmanNode>{
	
	private final long frequency;
	private Optional<HuffmanNode> parent;
	
	public HuffmanNode(long frequency,HuffmanNode parent) {
		super();
		this.frequency = frequency;
		this.parent = Optional.ofNullable(parent);
	}

	public long getFrequency() {
		return frequency;
	}
	
	@Override
	public int compareTo(HuffmanNode node) {
		return Long.compare(this.getFrequency(),node.getFrequency());
	}

	public Optional<HuffmanNode> getParent() {
		return parent;
	}

	public void setParent(HuffmanNode parent) {
		this.parent = Optional.ofNullable(parent);
	}
	
}
