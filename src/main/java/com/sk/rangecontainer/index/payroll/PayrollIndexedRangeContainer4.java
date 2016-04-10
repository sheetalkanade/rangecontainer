package com.sk.rangecontainer.index.payroll;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.log4j.Logger;

import com.sk.rangecontainer.IdIterator;
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
public class PayrollIndexedRangeContainer4 implements RangeContainer
{
	private final static Logger log = Logger.getLogger(PayrollIndexedRangeContainer4.class);
	private final NavigableMap<Long, Object> dataMap;

	public PayrollIndexedRangeContainer4(final long[] data)
			throws ThresholdExceededException
	{
		dataMap = new TreeMap<>();
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
			dataMap.subMap(fromValue, fromInclusive, toValue, toInclusive).values()
					.stream()
					.forEach(id -> addIdToList(id, ids));			
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
		PayrollIndexedRangeContainer4 other = (PayrollIndexedRangeContainer4) obj;
		if (dataMap == null)
		{
			if (other.dataMap != null)
				return false;
		} else if (!((TreeMap<Long, Object>) dataMap)
				.equals((TreeMap<Long, Object>) other.dataMap))
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

		IntStream.range(0, data.length).forEach(idx -> {
			Object existingValue = dataMap.get(data[idx]);

			// If key not present in map
			if (Objects.isNull(existingValue))
			{
				dataMap.put(data[idx], (short)idx);
			}
			// Handle duplicate values
			else if (existingValue instanceof Short)
			{
				List<Short> valueList = new ArrayList<>(2);
				valueList.add((short) existingValue);
				valueList.add((short) idx);
				dataMap.put(data[idx], valueList);
			}
			// key is mapped to a list of values
			else
			{
				List<Short> existingValuesList = (List<Short>) existingValue;
				List<Short> valueList = new ArrayList<>(existingValuesList.size() +1);
				valueList.addAll(existingValuesList);
				valueList.add((short) idx);
				dataMap.put(data[idx], valueList);	
			}
				
		});
		
		log.info(String.format("Create Range Container : data size %s, time taken %s ms",
				data.length, (double) (System.nanoTime() - startTime) / 1000000.0));
	}

}
