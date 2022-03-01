package com.nttdata.proyect.accounts.controller;

import com.nttdata.proyect.accounts.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AccountControllerTest
{
    @Mock
    AccountService accountService;

    @InjectMocks
    AccountController accountController;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    
}
