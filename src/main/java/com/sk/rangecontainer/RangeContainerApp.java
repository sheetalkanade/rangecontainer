package com.sk.rangecontainer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.sk.rangecontainer.payroll.PayrollRangeContainerFactory;


public class RangeContainerApp
{
	private final static Logger log = Logger.getLogger(RangeContainerApp.class);
	public static void main(String[] args)
	{
		long[] data = null;
		if(Objects.nonNull(args) && args.length > 0) {
			String fileName = args[0];
			Path filePath = Paths.get(fileName);
			try(FileInputStream fin = new FileInputStream(filePath.toFile()))
			{
				data = createInputDataArray(fin);
			} 
			catch (FileNotFoundException e)
			{
				log.error("Input File not found ",e);
			}
			catch (IOException e)
			{
				log.error("IOException reading input ",e);
			}
		} else {
			data = createInputDataArray(System.in);
		}
		
//		try
//		{
//			Thread.sleep(20000);
//		} catch (InterruptedException e1)
//		{
//			// TODO Auto-generated catch block
//			log.error("Exception in main :",e1);
//		}
//		int count = 0 ;
//		while(count < 100) {
//		
//			long startTime = System.nanoTime();
//			RangeContainerFactory rcFactory = new PayrollRangeContainerFactory();
//			RangeContainer rc = rcFactory.createContainer(data);
//			Ids ids = rc.findIdsInRange(0, 100000, true, true);
//			short id = ids.nextId();
//			int matchCount = 0;
//			while(id != Ids.END_OF_IDS) {
//				matchCount++;
//				id = ids.nextId();
//			}
//			log.info(String.format("findInRange : (%s, %s, %s, %s), match %s, time taken %s ms",
//					0, 100000, true, true, matchCount, (double) (System.nanoTime() - startTime) / 1000000.0));
//			try
//			{
//				Thread.sleep(5000);
//			} catch (InterruptedException e)
//			{
//				log.error("Exception in main :",e);
//			}
//			count++;
//		}
		
//		AdvancedRangeContainerFactory rcFactory = new PayrollRangeContainerFactory();
//		RangeContainer rc = rcFactory.createContainer(data, RangeContainerStrategyType.COLLECTION_QUERY_ENGINE);

		RangeContainerFactory rcFactory = new PayrollRangeContainerFactory();
		RangeContainer rc = rcFactory.createContainer(data);
		
//		Runnable task = () -> {
//			long fromValue = (new Random()).nextInt(14000);
//			long toValue = fromValue + (new Random()).nextInt(32000);
//	    	Ids ids = rc.findIdsInRange(fromValue, toValue, true, true);
//	    	
//	    	long startTime = System.nanoTime();
//	    	short id = ids.nextId();
//	    	int matchCount = 0;
//			while(id != Ids.END_OF_IDS) {
//				matchCount++;
//				id = ids.nextId();
//			}
//			log.info(String.format("Count Ids : (%s, %s, %s, %s), match %s, time taken %s ms",
//					fromValue, toValue, true, true, matchCount, (double) (System.nanoTime() - startTime) / 1000000.0));
//		};
//				
//		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
//		long startTime = System.nanoTime();
//		for(int count = 0; count < 10000; count++) {
//			executor.execute(task);
//		}
//		executor.shutdown();
//		try {
//			executor.awaitTermination(10, TimeUnit.MINUTES);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		log.info(String.format("Iterations %s, time taken %s ms",
//				10000, (double) (System.nanoTime() - startTime) / 1000000.0));

		
		
//		Callable<Ids> task = () -> {
//			long fromValue = (new Random()).nextInt(32000);
//			long toValue = fromValue + (new Random()).nextInt(32000);
//	    	return rc.findIdsInRange(fromValue, toValue, true, true);
//		};
//		long startTimee = System.nanoTime();	
//		Queue<Future<Ids>> returns = new LinkedList<>();
//		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
//		CompletionService<Ids> compService = new ExecutorCompletionService<>(executor);
//		int count = 0;
//		while(count < 10000) {
//			returns.add(executor.submit(task));
//			count++;
//		}
//		
//		Runnable resultTask = () -> {
//			Ids ids;
//			try {
//				ids = returns.poll().get();
//				long startTime = System.nanoTime();
//		    	short id = ids.nextId();
//		    	int matchCount = 0;
//				while(id != Ids.END_OF_IDS) {
//					matchCount++;
//					id = ids.nextId();
//				}
//				log.info(String.format("Count Ids :  match %s, time taken %s ms",
//						 matchCount, (double) (System.nanoTime() - startTime) / 1000000.0));
//			} catch (InterruptedException | ExecutionException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//		};
//		
//		while(count < 20000) {
//			executor.submit(resultTask);
//			count++;
//		}
//		
//		executor.shutdown();
//		try {
//			executor.awaitTermination(10, TimeUnit.MINUTES);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		log.info(String.format("Iterations %s, time taken %s ms",
//				10000, (double) (System.nanoTime() - startTimee) / 1000000.0));
		
		Callable<Ids> task = () -> {
			long fromValue = (new Random()).nextInt(32000);
			long toValue = fromValue + (new Random()).nextInt(32000);
	    	return rc.findIdsInRange(fromValue, toValue, true, true);
		};
		long startTimee = System.nanoTime();	
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		CompletionService<Ids> compService = new ExecutorCompletionService<>(executor);
		int count = 0;
		while(count < 10000) {
			compService.submit(task);
			count++;
		}
		
		Runnable resultTask = () -> {
			Ids ids;
			try {
				ids = compService.take().get();
				long startTime = System.nanoTime();
		    	short id = ids.nextId();
		    	int matchCount = 0;
				while(id != Ids.END_OF_IDS) {
					matchCount++;
					id = ids.nextId();
				}
				log.info(String.format("Count Ids :  match %s, time taken %s ms",
						 matchCount, (double) (System.nanoTime() - startTime) / 1000000.0));
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		};
		int rcount = 0;
		while(rcount < 10000) {
			executor.submit(resultTask);
			rcount++;
		}

		executor.shutdown();
		try {
			executor.awaitTermination(10, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info(String.format("Iterations %s, time taken %s ms",
				10000, (double) (System.nanoTime() - startTimee) / 1000000.0));
	}
	
	private static long[] createInputDataArray(InputStream in) {
		List<Long> dataAsList = new ArrayList<>();
		
		try(Scanner scanner = new Scanner(in))
		{
			while (scanner.hasNext()) {
				dataAsList.add(scanner.nextLong());
			}
			return dataAsList.stream().mapToLong(Long::longValue).toArray();
		}
		
	}
}
