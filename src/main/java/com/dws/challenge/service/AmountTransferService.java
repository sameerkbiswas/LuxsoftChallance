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

    private static final String NOTIFICATION_MESSAGE_TEMPLATE = "Your account %s has been %s by %s";

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

        String fromAccountId = transferAmount.getAccountFromId();
        String toAccountId = transferAmount.getAccountToId();
        BigDecimal amountToTransfer = transferAmount.getTransferAmount();

        // Checking if the accounts are valid
        if(accountsRepository.getAccount(fromAccountId) == null || accountsRepository.getAccount(toAccountId) == null) {
            throw new AccountDoesNotExistException(String.format("Account id %s or %s does not exist ", fromAccountId, toAccountId));
        }

        // Checking if the account to be debited has sufficient balance
        if (accountsRepository.getAccount(fromAccountId).getBalance().compareTo(amountToTransfer) < 0) {
            throw new InsufficientBalanceException(String.format("Insufficient balance in your account %s", fromAccountId));
        } else {
            accountsRepository.debitAccount(fromAccountId, amountToTransfer);
            accountsRepository.creditAccount(toAccountId, amountToTransfer);
        }

        // Sending notification to the account holders for debit transactions
        notificationService.notifyAboutTransfer(accountsRepository.getAccount(fromAccountId), String.format(NOTIFICATION_MESSAGE_TEMPLATE, fromAccountId, "debited", String.valueOf(amountToTransfer)));
        // Sending notification to the account holders for credit transactions
        notificationService.notifyAboutTransfer(accountsRepository.getAccount(toAccountId), String.format(NOTIFICATION_MESSAGE_TEMPLATE, toAccountId, "credited", String.valueOf(amountToTransfer)));
    }

}
