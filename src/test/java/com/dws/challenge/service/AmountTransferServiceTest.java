package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.TransferAmount;
import com.dws.challenge.exception.AccountDoesNotExistException;
import com.dws.challenge.exception.InsufficientBalanceException;
import com.dws.challenge.repository.AccountsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AmountTransferServiceTest {

    @Mock
    private AccountsRepository accountsRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AmountTransferService amountTransferService;

    private TransferAmount transferAmount;
    private Account fromAccount;
    private Account toAccount;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        fromAccount = new Account("1", new BigDecimal("15.000"));
        toAccount = new Account("2", new BigDecimal("20.000"));

    }

    @Test
    void transferAmount() {
        transferAmount = new TransferAmount("1", "2", new BigDecimal("10.000"));
        Mockito.when(accountsRepository.getAccount("1")).thenReturn(fromAccount);
        Mockito.when(accountsRepository.getAccount("2")).thenReturn(toAccount);
        amountTransferService.transferAmount(transferAmount);
    }

    @Test
    void transferAmount_when_insufficient_balance() {
        transferAmount = new TransferAmount("1", "2", new BigDecimal("100.000"));
        Mockito.when(accountsRepository.getAccount("1")).thenReturn(fromAccount);
        Mockito.when(accountsRepository.getAccount("2")).thenReturn(toAccount);
        try {
            amountTransferService.transferAmount(transferAmount);
            fail("Insufficient balance");
        } catch (InsufficientBalanceException insufficientBalanceException) {
            assertThat(insufficientBalanceException.getMessage()).isEqualTo(String.format("Insufficient balance in your account %s", fromAccount.getAccountId()));
        }
    }

    @Test
    void transferAmount_when_from_account_does_not_exist() {
        transferAmount = new TransferAmount("10", "2", new BigDecimal("101.000"));
        Mockito.when(accountsRepository.getAccount("1")).thenReturn(fromAccount);
        Mockito.when(accountsRepository.getAccount("2")).thenReturn(toAccount);
        try {
            amountTransferService.transferAmount(transferAmount);
            fail("Account does not exist");
        } catch (AccountDoesNotExistException accountDoesNotExistException) {
            assertThat(accountDoesNotExistException.getMessage()).isEqualTo(
                    String.format("Account id %s or %s does not exist ", transferAmount.getAccountFromId(), transferAmount.getAccountToId()));
        }
    }

    @Test
    void transferAmount_when_to_account_does_not_exist() {
        transferAmount = new TransferAmount("1", "20", new BigDecimal("1.000"));
        Mockito.when(accountsRepository.getAccount("1")).thenReturn(fromAccount);
        Mockito.when(accountsRepository.getAccount("2")).thenReturn(toAccount);
        try {
            amountTransferService.transferAmount(transferAmount);
            fail("Account does not exist");
        } catch (AccountDoesNotExistException accountDoesNotExistException) {
            assertThat(accountDoesNotExistException.getMessage()).isEqualTo(
                    String.format("Account id %s or %s does not exist ", transferAmount.getAccountFromId(), transferAmount.getAccountToId()));
        }
    }
}