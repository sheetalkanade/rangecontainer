package com.sk.rangecontainer.index.payroll;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.log4j.Logger;

import com.sk.rangecontainer.IdIterator;
import com.sk.rangecontainer.Ids;
import com.sk.rangecontainer.RangeContainer;
import com.sk.rangecontainer.exceptions.ThresholdExceededException;
import com.sk.rangecontainer.payroll.PayrollRangeContainerFactory;

/**
 * PayrollRangeContainer Implementation using NavigableMap.
 * 
 * @author Sheetal Kanade
 *
 */
public class PayrollIndexedRangeContainer implements RangeContainer
{
	private final static Logger log = Logger.getLogger(PayrollIndexedRangeContainer.class);
	private Map<Long, List<Short>> dataMap;

	public PayrollIndexedRangeContainer(final long[] data)
			throws ThresholdExceededException
	{
		dataMap = new LinkedHashMap<>();
		if (Objects.nonNull(data))
		{
			if (data.length > PayrollRangeContainerFactory.PAYROLLRANGECONTAINER_THRESHOLD)
			{
				throw new ThresholdExceededException(
						"Data exceeds threshold of "
								+ PayrollRangeContainerFactory.PAYROLLRANGECONTAINER_THRESHOLD);
			}
			processPayrollResultData(data);
		}

	}

	/**
	 * Creates a submap based on the range criteria, sorts and returns Ids
	 */
	public Ids findIdsInRange(final long fromValue, final long toValue,
			final boolean fromInclusive, final boolean toInclusive)
	{
		Predicate<Long> inputMaxCheck = p -> p > Long.MAX_VALUE;
		Predicate<Long> inputMinCheck = p -> p < 0;
		
		if(inputMinCheck.or(inputMaxCheck).test(fromValue) || inputMinCheck.or(inputMaxCheck).test(toValue)) {
			String errorMessage = String.format("Input out of range fromValue %s, toValue %s", fromValue, toValue);
			throw new ArithmeticException(errorMessage);
		}
		Iterable<Short> sortedIds = null;
		long startTime = System.nanoTime();
		if(dataMap.size() > 0) {
			
			// create a submap using the input range
			if(fromValue > toValue) {
				throw new ArithmeticException("Invalid from and To");
			}
			long fromRange = (fromInclusive)? fromValue : fromValue + 1;
			long toRange = (toInclusive)? toValue : toValue -1;
			Predicate<Map.Entry<Long, List<Short>>> fromRangeCheck = (Map.Entry<Long, List<Short>> p) -> p.getKey() >= fromRange;
			Predicate<Map.Entry<Long, List<Short>>> toRangeCheck = (Map.Entry<Long, List<Short>> p) -> p.getKey() <= toRange;
			Predicate<Map.Entry<Long, List<Short>>> rangeCheck = fromRangeCheck.and(toRangeCheck);
			sortedIds = dataMap.entrySet().stream()
				   .filter(rangeCheck)
				   .flatMap(l -> l.getValue().stream())
				   .sorted()
				   .collect(Collectors.toList());
	
		}
		Ids idIterator = new IdIterator(sortedIds);
		log.info(String.format("findInRange : (%s, %s, %s, %s), time taken %s ms",
				fromValue, toValue, fromInclusive, toInclusive, (double) (System.nanoTime() - startTime) / 1000000.0));

		return idIterator;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataMap == null) ? 0 : dataMap.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PayrollIndexedRangeContainer other = (PayrollIndexedRangeContainer) obj;
		if (dataMap == null)
		{
			if (other.dataMap != null)
				return false;
		} else if (!((Map<Long, List<Short>>) dataMap)
				.equals((Map<Long, List<Short>>) other.dataMap))
			return false;
		return true;
	}

	/**
	 * Iterate over the array and map data to a Navigable Map. key => data[idx]
	 * and value => data Index The values can be duplicate. For space optimization
	 * use List<Short> only if there are duplicate values else store
	 * as a Short.  
	 */
	@SuppressWarnings("unchecked")
	private void processPayrollResultData(long[] data)
	{
		long startTime = System.nanoTime();

//		Map<Long, List<Short>> tempDataMap = new HashMap<>();
		IntStream.range(0, data.length).forEach(idx -> {
			List<Short> dataIndex = dataMap.get((long) data[idx]);
			
			// If key not present in map
			if (Objects.isNull(dataIndex))
			{
				dataIndex = new ArrayList<>(1);
			}
			dataIndex.add((short)idx);
			dataMap.put(data[idx], dataIndex);
		});
//		Map<Long, List<Short>> sortedDataMap = new LinkedHashMap<>();
//		
//		tempDataMap.entrySet()
//			   .stream()
//			   .sorted(Map.Entry.comparingByKey())
//			   .forEachOrdered(entry -> sortedDataMap.put(entry.getKey(), entry.getValue()));
//					     //.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, LinkedHashMap::new));
//		dataMap = tempDataMap;
		log.info(String.format("Create Range Container : data size %s, time taken %s ms",
				data.length, (double) (System.nanoTime() - startTime) / 1000000.0));
	}

}
