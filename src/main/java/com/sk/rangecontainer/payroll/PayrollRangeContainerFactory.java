package com.sk.rangecontainer.payroll;

import org.apache.log4j.Logger;

import com.sk.rangecontainer.AdvancedRangeContainerFactory;
import com.sk.rangecontainer.RangeContainer;
import com.sk.rangecontainer.RangeContainerStrategyType;
import com.sk.rangecontainer.exceptions.ThresholdExceededException;
import com.sk.rangecontainer.index.payroll.PayrollIndexedRangeContainer;
import com.sk.rangecontainer.index.payroll.PayrollIndexedRangeContainer2;
import com.sk.rangecontainer.index.payroll.PayrollIndexedRangeContainer3;
import com.sk.rangecontainer.index.payroll.PayrollIndexedRangeContainer4;
import com.sk.rangecontainer.queryengine.payroll.PayrollCQERangeContainer;
import com.sk.rangecontainer.queryengine.payroll.PayrollCQERangeContainer2;

public class PayrollRangeContainerFactory implements AdvancedRangeContainerFactory
{
	private static final Logger log = Logger.getLogger(PayrollRangeContainerFactory.class);
	
	public static final int PAYROLLRANGECONTAINER_THRESHOLD = Short.MAX_VALUE + 1;
	@Override
	public RangeContainer createContainer(long[] data)
	{
		try
		{
			return new PayrollIndexedRangeContainer(data);
		} catch (ThresholdExceededException e)
		{
			log.error(e);
		}
		
		return null;
	}

	@Override
	public RangeContainer createContainer(long[] data, RangeContainerStrategyType queryType)
	{		
		try
		{
			if(queryType.equals(RangeContainerStrategyType.COLLECTION_QUERY_ENGINE)) {
				return new PayrollCQERangeContainer2(data);
			}
			return new PayrollIndexedRangeContainer(data);
		} catch (ThresholdExceededException e)
		{
			log.error(e);
		}
		return null;
	}

}
