package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.TransferAmount;
import com.dws.challenge.exception.AccountDoesNotExistException;
import com.dws.challenge.exception.InsufficientBalanceException;
import com.dws.challenge.repository.AccountsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * The Service class to process business logic to transfer amount from one account to another
 */
@Service
public class AmountTransferService {

    private static final String notificationFormat = "Your account %s has been %s by %s";

    @Autowired
    private AccountsRepository accountsRepository;

    @Autowired
    private NotificationService notificationService;

    /**
     * This method transfer the amount @transferAmount from one account @accountFromId to another account @accountToId
     * If both the account exists then it check for the available balance in the @accountFromId, if available then
     * debit the account by the @transferAmount and credits the @accountToId by @transferAmount
     * At the end Notification service is called to send the notification to the respective accounts
     * @param transferAmount The placeholder for @accountFromId, @accountToId and @transferAmount
     * @throws InsufficientBalanceException - When the account don't have the sufficient balance to perform the operation
     * @throws AccountDoesNotExistException - If the account doesn't exist
     */
    public void transferAmount(TransferAmount transferAmount) throws InsufficientBalanceException, AccountDoesNotExistException {
        BigDecimal amountToTransfer = transferAmount.getTransferAmount();
        Account fromAccount = accountsRepository.getAccount(transferAmount.getAccountFromId());
        Account toAccount = accountsRepository.getAccount(transferAmount.getAccountToId());
        if(fromAccount == null || toAccount == null) {
            throw new AccountDoesNotExistException(String.format("Account id %s or %s does not exist ", transferAmount.getAccountFromId(), transferAmount.getAccountToId()));
        }
        BigDecimal availableBalance = fromAccount.getBalance();
        // This is the critical block to perform the debit and credit operations in a multithreaded environment
        synchronized (this) {
            fromAccount = accountsRepository.getAccount(transferAmount.getAccountFromId());
            if (availableBalance.compareTo(amountToTransfer) < 0) {
                throw new InsufficientBalanceException("Insufficient balance in account " + fromAccount.getAccountId());
            } else {
                toAccount = accountsRepository.getAccount(transferAmount.getAccountToId());
                toAccount.setBalance(toAccount.getBalance().add(amountToTransfer));
                fromAccount.setBalance(fromAccount.getBalance().subtract(amountToTransfer));
            }
        }
        notificationService.notifyAboutTransfer(fromAccount, String.format(notificationFormat, fromAccount.getAccountId(), "debited", String.valueOf(transferAmount)));
        notificationService.notifyAboutTransfer(fromAccount, String.format(notificationFormat, toAccount.getAccountId(), "credited", String.valueOf(transferAmount)));
    }

}
