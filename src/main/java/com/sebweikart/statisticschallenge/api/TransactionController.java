package com.sebweikart.statisticschallenge.api;

import com.sebweikart.statisticschallenge.domain.Transaction;
import com.sebweikart.statisticschallenge.service.TransactionBufferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping(value = "/transactions")
public class TransactionController {

    @Autowired
    private TransactionBufferService transactionService;

    private static final long MAX_AGE = 60;

    @RequestMapping(value = "",
            method = RequestMethod.POST,
            consumes = {"application/json", "application/xml"},
            produces = {"application/json", "application/xml"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity createTransaction(@RequestBody Transaction transaction) {
        long ageOfTx = (new Date().getTime()-transaction.getTimestamp()) / 1000;
        System.out.println("got timestamp " + transaction.getTimestamp());
        System.out.println("got now " + new Date().getTime());
        if(transaction.getTimestamp()!= null && transaction.getAmount()!=null) {
            if(ageOfTx > MAX_AGE) {
                return new ResponseEntity(HttpStatus.NO_CONTENT);
            }
            this.transactionService.createTransaction(transaction);
            return new ResponseEntity(HttpStatus.CREATED);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
}
