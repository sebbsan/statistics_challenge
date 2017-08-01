package com.sebweikart.statisticschallenge.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sebweikart.statisticschallenge.Application;
import com.sebweikart.statisticschallenge.domain.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Calendar;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = Application.class)
@Profile("test")
public class TransactionControllerTests {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void initTests() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void noContentShouldReturnClientError() throws Exception {
        this.mockMvc.perform(post("/transactions").contentType(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().is4xxClientError());
    }

    @Test
    public void oldTransactionShouldReturnNoContent() throws Exception {
        this.mockMvc.perform(post("/transactions").content(toJson(mockOldTransaction()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isNoContent());
    }


    @Test
    public void recentTransactionShouldReturnOk() throws Exception {
        this.mockMvc.perform(post("/transactions").content(toJson(mockRecentTransaction()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isCreated());
    }


    private Transaction mockOldTransaction() {
        Transaction tx = new Transaction();
        tx.setAmount(22.5);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR, -1);
        tx.setTimestamp(cal.getTime().getTime());
        return tx;

    }

    private Transaction mockRecentTransaction() {
        Transaction tx = new Transaction();
        tx.setAmount(22.5);
        tx.setTimestamp(new Date().getTime());
        return tx;

    }

    private byte[] toJson(Object r) throws Exception {
        ObjectMapper map = new ObjectMapper();
        return map.writeValueAsBytes(r);
    }

}
