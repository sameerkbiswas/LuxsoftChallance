package com.dws.challenge.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferAmount {

  @NotNull
  @NotEmpty
  private final String accountFromId;

  @NotNull
  @NotEmpty
  private final String accountToId;

  @NotNull
  @Min(value = 0, message = "Amount to be transferred should not be less than zero.")
  private BigDecimal transferAmount;

  /**
   * A placeholder class to transfer amount from one account to another account
   * @param accountFromId
   * @param accountToId
   * @param transferAmount
   */
  @JsonCreator
  public TransferAmount(@JsonProperty("accountFromId") String accountFromId,
                        @JsonProperty("accountToId") String accountToId,
                        @JsonProperty("transferAmount") BigDecimal transferAmount) {
    this.accountFromId = accountFromId;
    this.accountToId = accountToId;
    this.transferAmount = transferAmount;
  }
}
