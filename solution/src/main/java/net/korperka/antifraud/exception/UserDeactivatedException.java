package net.korperka.antifraud.exception;

public class UserDeactivatedException extends RuntimeException {
  public UserDeactivatedException() {
    super("Пользователь деактивирован");
  }
}
