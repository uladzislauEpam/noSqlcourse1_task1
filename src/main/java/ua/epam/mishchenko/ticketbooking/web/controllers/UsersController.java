package ua.epam.mishchenko.ticketbooking.web.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.epam.mishchenko.ticketbooking.facade.impl.BookingFacadeImpl;
import ua.epam.mishchenko.ticketbooking.model.User;
import ua.epam.mishchenko.ticketbooking.model.impl.UserImpl;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UsersController {

    private static final Logger log = LoggerFactory.getLogger(TicketsController.class);

    private final BookingFacadeImpl bookingFacade;

    public UsersController(BookingFacadeImpl bookingFacade) {
        this.bookingFacade = bookingFacade;
    }

    @GetMapping("/{id}")
    public String showUserById(@PathVariable long id, Model model) {
        log.info("Showing user by id: {}", id);
        User userById = bookingFacade.getUserById(id);
        if (isNull(userById)) {
            model.addAttribute("message", "Can not to find user by id: " + id);
            log.info("Can not to find user by id: {}", id);
        } else {
            model.addAttribute("user", userById);
            log.info("The user by id: {} successfully found", id);
        }
        return "user";
    }

    private boolean isNull(Object object) {
        return object == null;
    }

    @GetMapping("/name/{name}")
    public String showUsersByName(@PathVariable String name,
                                  @RequestParam int pageSize,
                                  @RequestParam int pageNum,
                                  Model model) {
        log.info("Showing users by name: {}", name);
        List<User> usersByName = bookingFacade.getUsersByName(name, pageSize, pageNum);
        if (isNull(usersByName)) {
            model.addAttribute("message", "Can not to find users by name: " + name);
            log.info("Can not to find users by name: {}", name);
        } else {
            model.addAttribute("users", usersByName);
            log.info("The users by name: {} successfully found", name);
        }
        return "users";
    }

    @GetMapping("/email/{email}")
    public String showUserByEmail(@PathVariable String email, Model model) {
        log.info("Showing the user by email: {}", email);
        User userByEmail = bookingFacade.getUserByEmail(email);
        if (isNull(userByEmail)) {
            model.addAttribute("message", "Can not to find user by email: " + email);
            log.info("Can not to find user by email: {}", email);
        } else {
            model.addAttribute("user", userByEmail);
            log.info("The user by email: {} successfully found", email);
        }
        return "user";
    }

    @PostMapping
    public String createUser(@RequestParam String name,
                             @RequestParam String email,
                             Model model) {
        log.info("Creating user with name={} and email={}", name, email);
        User user = bookingFacade.createUser(createUserEntityWithoutId(name, email));
        if (isNull(user)) {
            model.addAttribute("message",
                    "Can not to create user with name - " + name + " and email - " + email);
            log.info("Can not to create user with name={} and email={}", name, email);
        } else {
            model.addAttribute("user", user);
            log.info("The user successfully created");
        }
        return "user";
    }

    private User createUserEntityWithoutId(String name, String email) {
        User user = new UserImpl();
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    @PutMapping
    public String updateUser(@RequestParam long id,
                             @RequestParam String name,
                             @RequestParam String email,
                             Model model) {
        log.info("Updating user with id: {}", id);
        User user = bookingFacade.updateUser(createUserEntityWithId(id, name, email));
        if (isNull(user)) {
            model.addAttribute("message", "Can not to update user with id: " + id);
            log.info("Can not to update user with id: {}", id);
        } else {
            model.addAttribute("user", user);
            log.info("The user with id: {} successfully update", id);
        }
        return "user";
    }

    private User createUserEntityWithId(long id, String name, String email) {
        User user = createUserEntityWithoutId(name, email);
        user.setId(id);
        return user;
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable long id, Model model) {
        log.info("Deleting the user with id: {}", id);
        boolean isUserRemoved = bookingFacade.deleteUser(id);
        if (isUserRemoved) {
            model.addAttribute("message", "The user with id: " + id + " successfully removed");
            log.info("The user with id: {} successfully removed", id);
        } else {
            model.addAttribute("message", "The user with id: " + id + " not removed");
            log.info("The user with id: {} not removed", id);
        }
        return "user";
    }
}
