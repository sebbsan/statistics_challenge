package com.sebweikart.statisticschallenge.api;

import com.sebweikart.statisticschallenge.domain.Statistic;
import com.sebweikart.statisticschallenge.domain.Transaction;
import com.sebweikart.statisticschallenge.service.TransactionBufferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping(value = "/statistics")
public class StatisticController {

    @Autowired
    private TransactionBufferService transactionService;


    @RequestMapping(value = "",
            method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity getStatistics() {
        Statistic stat = new Statistic();
        CompletableFuture<Void> sumFuture
                = CompletableFuture.supplyAsync(
                        () -> transactionService.getTransactions().parallelStream().mapToDouble(Transaction::getAmount).sum())
                        .thenAccept(sum -> stat.setSum(sum));
        CompletableFuture<Void> avgFuture
                = CompletableFuture.supplyAsync(
                () -> transactionService.getTransactions().parallelStream().mapToDouble(Transaction::getAmount).average())
                .thenAccept(avg -> stat.setAvg(avg.orElse(0)));
        CompletableFuture<Void> maxFuture
                = CompletableFuture.supplyAsync(
                () -> transactionService.getTransactions().parallelStream().mapToDouble(Transaction::getAmount).max())
                .thenAccept(max -> stat.setMax(max.orElse(0)));
        CompletableFuture<Void> minFuture
                = CompletableFuture.supplyAsync(
                () -> transactionService.getTransactions().parallelStream().mapToDouble(Transaction::getAmount).min())
                .thenAccept(min -> stat.setMin(min.orElse(0)));
        CompletableFuture<Void> countFuture
                = CompletableFuture.supplyAsync(
                () -> transactionService.getTransactions().size())
                .thenAccept(count -> stat.setCount(count.longValue()));
        CompletableFuture<Void> statisticsFuture
                = CompletableFuture.allOf(sumFuture, avgFuture, maxFuture, minFuture, countFuture);
        try {
            statisticsFuture.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // flush the statistics buffer
        transactionService.flushBuffer();
        return new ResponseEntity(stat, HttpStatus.OK);
    }
}
