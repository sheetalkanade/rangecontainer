package com.sk.rangecontainer;

public interface AdvancedRangeContainerFactory extends RangeContainerFactory {
	/**
	 * Builds an immutable RangeContainer of the desired implementation
	 * @param data
	 * @param advanced
	 * @return
	 */
	RangeContainer createContainer(long[] data, RangeContainerStrategyType strategyType);
	
}
