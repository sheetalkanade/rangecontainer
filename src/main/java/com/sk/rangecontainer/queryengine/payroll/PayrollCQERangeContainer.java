package com.sk.rangecontainer.queryengine.payroll;

import static com.googlecode.cqengine.query.QueryFactory.ascending;
import static com.googlecode.cqengine.query.QueryFactory.between;
import static com.googlecode.cqengine.query.QueryFactory.orderBy;
import static com.googlecode.cqengine.query.QueryFactory.queryOptions;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import org.apache.log4j.Logger;

import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.index.navigable.NavigableIndex;
import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.resultset.ResultSet;
import com.sk.rangecontainer.Ids;
import com.sk.rangecontainer.RangeContainer;
import com.sk.rangecontainer.exceptions.ThresholdExceededException;
import com.sk.rangecontainer.payroll.PayrollRangeContainerFactory;

/**
 * PayrollRangeContainer implemented using Collection Query Engine. This is a NOSQL indexing and query engine for retrieving objects from java collections using SQL-like queries
 * https://github.com/npgall/cqengine
 * 
 * Note: I wanted to present this option but did not have enough time to design
 * it for this exercise. e.g. get the query indexes as input. Maybe the
 * container creation takes in more than just a longp[ data and gives more
 * information to add date and or other information to search on.
 * 
 * @author Sheetal Kanade
 * 
 *
 */
public class PayrollCQERangeContainer implements RangeContainer
{
	private final static Logger log = Logger.getLogger(PayrollCQERangeContainer.class);
	private final IndexedCollection<PayrollResult> payrollResultColl;

	public PayrollCQERangeContainer(long[] data) throws ThresholdExceededException
	{
		payrollResultColl = new ConcurrentIndexedCollection<PayrollResult>();
		if (Objects.nonNull(data)) {
			if(data.length > PayrollRangeContainerFactory.PAYROLLRANGECONTAINER_THRESHOLD ) {
				throw new ThresholdExceededException(
						"Data exceeds threshold of "
								+ PayrollRangeContainerFactory.PAYROLLRANGECONTAINER_THRESHOLD);
			}
			processPayrollResultData(data);
		} 
	}
	

	/**
	 * Use SQL-like query to get subset of data in range. 
	 */
	public Ids findIdsInRange(long fromValue, long toValue,
			boolean fromInclusive, boolean toInclusive)
	{
		Predicate<Long> inputMaxCheck = p -> p > Long.MAX_VALUE;
		Predicate<Long> inputMinCheck = p -> p < 0;

		if(inputMinCheck.or(inputMaxCheck).test(fromValue) || inputMinCheck.or(inputMaxCheck).test(toValue)) {
			String errorMessage = String.format("Input out of range fromValue %s, toValue %s", fromValue, toValue);
			throw new ArithmeticException(errorMessage);
		}
		
		// The find operation happens in microseconds even for 32K data i.e. irrespective of data size.
		long startTime = System.nanoTime();
		
		Ids idIterator = null;
		if(!payrollResultColl.isEmpty()) {
			// Query to get data in range
			Query<PayrollResult> query = between(PayrollResultWrapper.SALARY,
					fromValue, fromInclusive, toValue, toInclusive);
			// get result set ordered by ID
			ResultSet<PayrollResult> result = payrollResultColl.retrieve(query,
					queryOptions(orderBy(ascending(PayrollResultWrapper.WORKER))));
			
			log.info(String.format("CQE result after sorting subset by Id: (%s, %s, %s, %s), time taken %s ms",
					fromValue, toValue, fromInclusive, toInclusive, (double) (System.nanoTime() - startTime) / 1000000.0));
			
			// This takes longer. So need to optimize.
			startTime = System.nanoTime();
			final Queue<Short> ids = new ConcurrentLinkedQueue<>();
			StreamSupport.stream(result.spliterator(), true).forEach(item -> {
				ids.add((short) item.getWorker());
			});
			idIterator = new com.sk.rangecontainer.IdIterator(ids);
		} else {
			idIterator = new PayrollResultIterator(null);
		}
		
		log.info(String.format("findInRange Id from the ResultObject: (%s, %s, %s, %s), time taken %s ms",
				fromValue, toValue, fromInclusive, toInclusive,  (double) (System.nanoTime() - startTime) / 1000000.0));
		log.info("----------------------------------------------------------------------------------------------------");
		return idIterator;
	}
	
	private void processPayrollResultData(long[] data) {
		payrollResultColl.addIndex(
				NavigableIndex.onAttribute(PayrollResultWrapper.WORKER));
		payrollResultColl.addIndex(
				NavigableIndex.onAttribute(PayrollResultWrapper.SALARY));

		long startTime = System.nanoTime();

		IntStream.range(0, data.length).forEach(idx -> {
			PayrollResult item = new PayrollResult.PayrollResultBuilder(idx)
					.netPay(data[idx]).build();
			payrollResultColl.add(item);
		});

		log.info(String.format("Create Range Container : data size %s, time taken %s ms",
				data.length, (double) (System.nanoTime() - startTime) / 1000000.0));
	}

}
