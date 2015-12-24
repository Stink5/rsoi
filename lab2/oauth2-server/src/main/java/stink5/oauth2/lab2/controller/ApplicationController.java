package stink5.oauth2.lab2.controller;

import static java.util.Objects.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import stink5.oauth2.lab2.model.User;
import stink5.oauth2.lab2.repository.UserRepository;

@Controller
public class ApplicationController {

    private final UserRepository userRepository;

    @Autowired
    public ApplicationController(final UserRepository repo) {
        this.userRepository = requireNonNull(repo, "userRepository can't be null");
    }

    private String connected() {
        return ((UserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal()).getUsername();
    }

    @RequestMapping(method = RequestMethod.GET, path = {"/", "/login"})
    public String loginPage(final Model model,
        @RequestParam(value = "error", required = false) final String error,
        @RequestParam(value = "logout", required = false) final String logout
    ) {
        if (error != null) {
            model.addAttribute("error",  "Invalid username and password!");
        }
        if (logout != null) {
            model.addAttribute("msg", "You've been logged out successfully.");
        }
        return "login";
    }

    @RequestMapping(method = RequestMethod.GET, path = "/user")
    public String userPage(final Model model) {
        final String username = connected();
        final User user = findUser(username);
        model.addAttribute("connected", username);
        model.addAttribute("user", user);
        return "user";
    }

    @RequestMapping(method = RequestMethod.POST, path = "/user/save/{username}")
    public String userSave(
        final Model model,
        final RedirectAttributes redir,
        @PathVariable("username") final String username,
        @RequestParam("email") final String email
    ) {
        final User user = findUser(username);
        user.setEmail(email);
        this.userRepository.saveAndFlush(user);

        redir.addFlashAttribute("msg", "User changed");
        return "redirect:/user";
    }

    private User findUser(final String username) {
        final User user = this.userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User '" + username + "' hasn't been found.");
        }
        return user;
    }
}
