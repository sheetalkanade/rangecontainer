package com.sk.rangecontainer.queryengine.payroll;

public class PayrollResult extends ResultBase
{
	private final long netPay;
	private final long date;
	
	public PayrollResult(PayrollResultBuilder builder) {
		super(builder.worker);
		this.netPay = builder.netPay;
		this.date = builder.date;
	}
	
	public long getWorker()
	{
		return getId();
	}
	public long getNetPay()
	{
		return netPay;
	}
	
	public long getDate() {
		return date;
	}
	
	public static class PayrollResultBuilder {
		private final long worker;
		private long netPay;
		private long date;
		
		public PayrollResultBuilder(long worker) {
			this.worker = worker;
		}
		
		public PayrollResultBuilder netPay(long netPay) {
			this.netPay = netPay;
			return this;
		}
		
		public PayrollResultBuilder date(long date) {
			this.date = date;
			return this;
		}
		
		public PayrollResult build() {
			return new PayrollResult(this);
		}
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (date ^ (date >>> 32));
		result = prime * result + (int) (netPay ^ (netPay >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PayrollResult other = (PayrollResult) obj;
		if (date != other.date)
			return false;
		if (netPay != other.netPay)
			return false;
		return true;
	}
	
}
