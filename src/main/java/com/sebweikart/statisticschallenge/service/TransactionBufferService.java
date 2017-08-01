package com.sebweikart.statisticschallenge.service;

import com.sebweikart.statisticschallenge.domain.Transaction;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TransactionBufferService {

    private List<Transaction> transactionsBuffer = Collections.synchronizedList(new ArrayList<Transaction>());

    public void createTransaction(Transaction transaction) {
        transactionsBuffer.add(transaction);
    }

    public List<Transaction> getTransactions() {
        return transactionsBuffer;
    }

    public void flushBuffer() {
        transactionsBuffer = Collections.synchronizedList(new ArrayList<Transaction>());
    }
}
