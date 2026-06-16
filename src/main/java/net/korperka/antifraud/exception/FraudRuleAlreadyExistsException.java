package net.korperka.antifraud.exception;

public class FraudRuleAlreadyExistsException extends RuntimeException {
  public FraudRuleAlreadyExistsException() {
    super("Правило с таким именем уже существует");
  }
}
