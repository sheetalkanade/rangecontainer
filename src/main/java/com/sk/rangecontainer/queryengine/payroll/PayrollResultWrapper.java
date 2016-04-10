package com.sk.rangecontainer.queryengine.payroll;

import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.query.option.QueryOptions;

/**
 * A wrapper class to add Search or Query attributes to PayrollResult fields.
 * @author Sheetal Kanade
 *
 */
public class PayrollResultWrapper {

	PayrollResult payrollResult;
	
	public PayrollResultWrapper(PayrollResult payrollResult)
	{
		this.payrollResult = payrollResult;
	}
	
	// -------------------------- Attributes --------------------------
    public static final Attribute<PayrollResult, Long> WORKER = new SimpleAttribute<PayrollResult, Long>("worker") {
        public Long getValue(PayrollResult payrollResult, QueryOptions queryOptions) { return payrollResult.getWorker(); }
    };

    public static final Attribute<PayrollResult, Long> SALARY = new SimpleAttribute<PayrollResult, Long>("netPay") {
        public Long getValue(PayrollResult payrollResult, QueryOptions queryOptions) { return payrollResult.getNetPay(); }
    };
    
    public static final Attribute<PayrollResult, Long> DATE = new SimpleAttribute<PayrollResult, Long>("date") {
        public Long getValue(PayrollResult payrollResult, QueryOptions queryOptions) { return payrollResult.getDate(); }
    };
    
	
}
