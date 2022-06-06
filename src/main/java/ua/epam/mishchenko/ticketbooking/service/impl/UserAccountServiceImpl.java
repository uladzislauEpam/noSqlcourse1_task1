package ua.epam.mishchenko.ticketbooking.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ua.epam.mishchenko.ticketbooking.model.UserAccount;
import ua.epam.mishchenko.ticketbooking.repository.UserAccountRepository;
import ua.epam.mishchenko.ticketbooking.repository.UserRepository;
import ua.epam.mishchenko.ticketbooking.service.UserAccountService;

import java.math.BigDecimal;

@Service
public class UserAccountServiceImpl implements UserAccountService {

    private static final Logger log = LoggerFactory.getLogger(UserAccountServiceImpl.class);

    private final UserRepository userRepository;

    private final UserAccountRepository userAccountRepository;

    public UserAccountServiceImpl(UserRepository userRepository, UserAccountRepository userAccountRepository) {
        this.userRepository = userRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public UserAccount refillAccount(long userId, BigDecimal money) {
        log.info("Refilling user account for user with id: {}", userId);
        try {
            thrownRuntimeExceptionIfMoneyLessZero(money);
            throwRuntimeExceptionIfUserNotExist(userId);
            UserAccount userAccount = getUserAccountAndRefillIfNotExistCreate(userId, money);
            userAccount = userAccountRepository.save(userAccount);
            log.info("The user account with user id {} successfully refilled", userId);
            return userAccount;
        } catch (RuntimeException e) {
            log.warn("Can not to refill account with user id: {}", userId);
            return null;
        }
    }

    private void thrownRuntimeExceptionIfMoneyLessZero(BigDecimal money) {
        if (money.compareTo(BigDecimal.ZERO) < 1) {
            throw new RuntimeException("The money can not to be less zero");
        }
    }

    private UserAccount getUserAccountAndRefillIfNotExistCreate(long userId, BigDecimal money) {
        UserAccount userAccount = userAccountRepository.findById(userId).orElse(null);
        if (userAccount == null) {
            return createNewUserAccount(userId, money);
        }
        BigDecimal money1 = userAccount.getMoney();
        userAccount.setMoney(money1.add(money));
        return userAccount;
    }

    private UserAccount createNewUserAccount(long userId, BigDecimal money) {
        log.info("The user account with user id {} does not exist", userId);
        log.info("Creating new user account for user with id {}", userId);
        UserAccount userAccount = new UserAccount();
        userAccount.setUser(userRepository.findById(userId).get());
        userAccount.setMoney(money);
        log.info("The user account for user with id {} successfully created", userId);
        return userAccount;
    }

    private void throwRuntimeExceptionIfUserNotExist(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("The user with id " + userId + " does not exist");
        }
    }
}
