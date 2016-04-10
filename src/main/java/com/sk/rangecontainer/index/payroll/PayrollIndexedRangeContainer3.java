package com.sk.rangecontainer.index.payroll;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import org.apache.log4j.Logger;

import com.sk.rangecontainer.IdIteratorLazy;
import com.sk.rangecontainer.IdIteratorLazy2;
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
public class PayrollIndexedRangeContainer3 implements RangeContainer
{
	private final static Logger log = Logger.getLogger(PayrollIndexedRangeContainer3.class);
	private Map<Long, Object> dataMap;

	public PayrollIndexedRangeContainer3(final long[] data)
			throws ThresholdExceededException
	{
		dataMap = new HashMap<>();
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
		
		final List<Short> ids = new ArrayList<>();
		long startTime = System.nanoTime();
		if(dataMap.size() > 0) {
			
			// create a submap using the input range
			if(fromValue > toValue) {
				throw new ArithmeticException("Invalid from and To");
			}
			long fromRange = (fromInclusive)? fromValue : fromValue + 1;
			long toRange = (toInclusive)? toValue : toValue -1;
			Predicate<Map.Entry<Long, Object>> fromRangeCheck = (Map.Entry<Long, Object> p) -> p.getKey() >= fromRange;
			Predicate<Map.Entry<Long, Object>> toRangeCheck = (Map.Entry<Long, Object> p) -> p.getKey() <= toRange;
			Predicate<Map.Entry<Long, Object>> rangeCheck = fromRangeCheck.and(toRangeCheck);
			dataMap.entrySet().stream()
				   .filter(rangeCheck)
				   .forEach(entry -> addIdToList(entry.getValue(), ids));	
		}
		Ids idIterator = new IdIteratorLazy2(ids);
		log.info(String.format("findInRange : (%s, %s, %s, %s), time taken %s ms",
				fromValue, toValue, fromInclusive, toInclusive, (double) (System.nanoTime() - startTime) / 1000000.0));

		return idIterator;
	}

	@SuppressWarnings("unchecked")
	private void addIdToList(final Object id, List<Short> ids)
	{
		if (id instanceof Short)
		{
			ids.add((short) id);
		} else
		{
			ids.addAll((List<Short>) id);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataMap == null) ? 0 : dataMap.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PayrollIndexedRangeContainer3 other = (PayrollIndexedRangeContainer3) obj;
		if (dataMap == null) {
			if (other.dataMap != null)
				return false;
		} else if (!dataMap.equals(other.dataMap))
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

//		Map<Long, Object> tempDataMap = new HashMap<>();
		IntStream.range(0, data.length).forEach(idx -> {
			Object dataIndex = dataMap.get((long) data[idx]);

			// If key not present in map
			if (Objects.isNull(dataIndex))
			{
				dataIndex = (short) idx;
			}
			// Handle duplicate values
			else
			{
				List<Short> dataIndexList = new ArrayList<>(1);
				// If key is mapped to a single value
				if (dataIndex instanceof Short)
				{
					dataIndexList.add((short) dataIndex);
				}
				// key is mapped to a list of values
				else
				{
					dataIndexList.addAll((List<Short>) dataIndex);
				}
				dataIndexList.add((short) idx);
				dataIndex = dataIndexList;
			}

			dataMap.put((long) data[idx], dataIndex);
		});
//		Map<Long, Object> sortedDataMap = new LinkedHashMap<>();
		
//		tempDataMap.entrySet()
//			   .stream()
//			   .sorted(Map.Entry.comparingByKey())
//			   .forEachOrdered(entry -> sortedDataMap.put(entry.getKey(), entry.getValue()));
					     //.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, LinkedHashMap::new));
//		dataMap = tempDataMap;
		log.info(String.format("Create Range Container : data size %s, time taken %s ms",
				data.length, (double) (System.nanoTime() - startTime) / 1000000.0));
	}

}
