package com.sk.rangecontainer;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import org.junit.BeforeClass;
import org.junit.Test;

import com.sk.rangecontainer.AdvancedRangeContainerFactory;
import com.sk.rangecontainer.Ids;
import com.sk.rangecontainer.RangeContainer;
import com.sk.rangecontainer.RangeContainerStrategyType;
import com.sk.rangecontainer.index.payroll.PayrollIndexedRangeContainer;
import com.sk.rangecontainer.payroll.PayrollRangeContainerFactory;
import com.sk.rangecontainer.queryengine.payroll.PayrollCQERangeContainer;

import junit.framework.Assert;

public class RangeQueryCQERangeContainerTest
{
	private static AdvancedRangeContainerFactory rcFactory;
	private static RangeContainer rc;
	private static RangeContainer rcLarge;

	
	@BeforeClass
	public static void setUp(){
		rcFactory = new PayrollRangeContainerFactory();
		rc = rcFactory.createContainer(new long[]{10,12,17,21,2,15,16}, RangeContainerStrategyType.COLLECTION_QUERY_ENGINE);
	}
	
	private void setupRangeContainerWithLargeData() {
		long[] data = new long[32768];
		String fileName = "largeInput.txt";
		Path path = Paths.get(fileName);
	//	generateFileWithLargeData(data.length, fileName);
		
		try(Scanner scanner = new Scanner(path.toFile()))
		{
			int idx = 0;
			   while (scanner.hasNext()) {
			    data[idx] = scanner.nextLong();
			    idx++;
			   }
		} catch (IOException e) {}
	
		rcLarge = rcFactory.createContainer(data, RangeContainerStrategyType.COLLECTION_QUERY_ENGINE);
	}
	
	@Test
	public void checkRangeContainerInstance() {
		assertEquals(true, rc instanceof PayrollCQERangeContainer);
		assertEquals(false, rc instanceof PayrollIndexedRangeContainer);
	}
	
	@Test
	public void runARangeQuery(){
		
		Ids ids = rc.findIdsInRange(14, 17, true, true);
		assertEquals(2, ids.nextId());
		assertEquals(5, ids.nextId());
		assertEquals(6, ids.nextId());
		assertEquals(Ids.END_OF_IDS, ids.nextId());
		
		ids = rc.findIdsInRange(14, 17, true, false);
		assertEquals(5, ids.nextId());
		assertEquals(6, ids.nextId());
		assertEquals(Ids.END_OF_IDS, ids.nextId());
		
		ids = rc.findIdsInRange(20, Long.MAX_VALUE, false, true);
		assertEquals(3, ids.nextId());
		assertEquals(Ids.END_OF_IDS, ids.nextId());

	}
	
	@Test(expected = ArithmeticException.class) 
	public void runAsRangeCheckQuery() {
		rc.findIdsInRange(20, Long.MAX_VALUE + 1, false, true);
		
		rc.findIdsInRange(Long.MAX_VALUE + 1, Long.MAX_VALUE + 1, false, true);
		
		rc.findIdsInRange(-1, 0, true, false);
		
		rc.findIdsInRange(-2, -1, true, false);
	}
	
	@Test() 
	public void runAsNullInputQuery() {
		RangeContainer rcNullData = rcFactory.createContainer(null);
		Ids nullIds = rcNullData.findIdsInRange(1, 29, true, false);
		assertEquals(Ids.END_OF_IDS, nullIds.nextId());
	}
	
	@Test
	public void runAsBoundaryRangeQuery(){
			
		setupRangeContainerWithLargeData();
		
		Ids ids = rcLarge.findIdsInRange(19045, 19045, true, false);
		assertEquals(Ids.END_OF_IDS, ids.nextId());
		
		ids = rcLarge.findIdsInRange(19045, 19045, false, false);
		assertEquals(Ids.END_OF_IDS, ids.nextId());
		
		ids = rcLarge.findIdsInRange(19045, 19045, false, true);
		assertEquals(Ids.END_OF_IDS, ids.nextId());
		
		ids = rcLarge.findIdsInRange(19045, 19045, true, true);
		assertEquals(11937, ids.nextId());
		assertEquals(12101, ids.nextId());
		assertEquals(13471, ids.nextId());
		assertEquals(31999, ids.nextId());
		assertEquals(Ids.END_OF_IDS, ids.nextId());
		
		ids = rcLarge.findIdsInRange(19044, 19045, false, true);
		assertEquals(11937, ids.nextId());
		assertEquals(12101, ids.nextId());
		assertEquals(13471, ids.nextId());
		assertEquals(31999, ids.nextId());
		assertEquals(Ids.END_OF_IDS, ids.nextId());
		
		ids = rcLarge.findIdsInRange(19045, 19046, true, false);
		assertEquals(11937, ids.nextId());
		assertEquals(12101, ids.nextId());
		assertEquals(13471, ids.nextId());
		assertEquals(31999, ids.nextId());
		assertEquals(Ids.END_OF_IDS, ids.nextId());
		
		ids = rcLarge.findIdsInRange(0, 100000, true, false);
		int count = 0;
		short nextId = ids.nextId();
		while(nextId != Ids.END_OF_IDS){
			count++;
			nextId = ids.nextId();
		}
		assertEquals(32768, count);
	}
	
//	private void writeToFile(FileWriter fw, String line) {
//		try {
//			fw.write(String.format("%s%n", line));
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
//	}

//	private void generateFileWithLargeData(int numberOfEntries, String fileName) {
//		Path path = Paths.get(fileName);
//		if(!Files.isRegularFile(path)) {
//			Random rnd = new Random();
//			try(final FileWriter fw = new FileWriter(fileName)) {
//				IntStream.range(0, numberOfEntries)
//						 .parallel()
//						 .forEach(idx -> {				
//							writeToFile(fw, Long.toString( rnd.nextInt(numberOfEntries)));						
//						 });
//			} catch (IOException e){
//				System.out.println("Failed to write to file ");
//				e.printStackTrace();
//			} 
//		}
//	}
	
}
