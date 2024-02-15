package com.diep.libraryManagement.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataExecutor {

  private static final Logger LOG = LoggerFactory.getLogger(DataExecutor.class);
  private final int numberOfExecutors;
  private final ExecutorService[] executors;
  private final Random random;

  private static final DataExecutor DATA_EXECUTOR = new DataExecutor();

  public static final DataExecutor getDataExecutor() {
    return DATA_EXECUTOR;
  }

  private DataExecutor() {
    this.random = new Random();
    this.numberOfExecutors = Math.max(2, Runtime.getRuntime().availableProcessors()-2);
    this.executors = new ExecutorService[numberOfExecutors];

    LOG.debug("Number of executors = {}", numberOfExecutors);
    for (int i = 0; i < numberOfExecutors; i ++) {
      executors[i] = Executors.newSingleThreadExecutor();
    }
  }

  public ExecutorService getExecutor(int bookId) {
    LOG.debug("Book {} assigned with executor {} ", bookId, bookId % numberOfExecutors);
    return executors[bookId % numberOfExecutors];
  }

  public ExecutorService getRandomExecutor() {
    int randomInt = random.nextInt(numberOfExecutors);
    LOG.debug("Assign random executor {}", randomInt);
    return executors[randomInt];
  }
}
