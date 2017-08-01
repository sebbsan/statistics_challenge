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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = Application.class)
@Profile("test")
public class StatisticsControllerTests {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void initTests() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }



    @Test
    public void oneRecentTransactionShouldReturnValidStatistics() throws Exception {
        this.mockMvc.perform(post("/transactions").content(toJson(mockRecentTransaction()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isCreated());
        this.mockMvc.perform(get("/statistics"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.avg", is(22.5)))
                .andExpect(jsonPath("$.count", is(1)));
    }

    @Test
    public void oneRecentandOneOldTransactionShouldReturnValidStatistics() throws Exception {
        this.mockMvc.perform(post("/transactions").content(toJson(mockRecentTransaction()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isCreated());
        this.mockMvc.perform(post("/transactions").content(toJson(mockOldTransaction()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isNoContent());;

        this.mockMvc.perform(get("/statistics"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.avg", is(22.5)))
                .andExpect(jsonPath("$.count", is(1)));
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
