package com.example.authorization;

import com.example.authorization.kafkaProduser.Sender;
import com.example.authorization.models.Users;
import com.example.authorization.repositories.UsersRepository;
import com.example.authorization.tokens.TokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Iterator;

@Controller
@RequestMapping("/authorization")
public class AuthorizationController {
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private TokenManager tokenManager;
    @Autowired
    private Sender sender;

    @GetMapping("/")
    public String authorization() {
        return "redirect:http://localhost:8080/authorization/sign";
    }

    @GetMapping("/registration")
    public String registrationGet() {
        return "registration";
    }

    @PostMapping("/registration")
    public String registrationPost(@RequestParam String login, @RequestParam String password, @RequestParam("fullName") String fullName, @RequestParam("address") String address, Model model) {

        Users user = findUserByLogin(login);
        if (user != null) {
            model.addAttribute("message", "Пользователь с таким именем уже есть!");
            model.addAttribute("toMainURL", "/authorization/registration");
            return "message";
        } else {
            user = new Users(login, password);
            user.setFullName(fullName);
            user.setAddress(address);
            user.setBalance(new Long(0));
            usersRepository.save(user);


            String token = tokenManager.generate(""+user.getId(), 10000000).toString();
            sender.sendMessage(user.getId() + ";" + token);

            return "redirect:http://localhost:8080/profile/" + token + "/";
        }
    }

    @GetMapping("/sign")
    public String signGet() {
        return "sign";
    }

    @PostMapping("/sign")
    public String signPost(@RequestParam String login, @RequestParam String password, Model model) {

        Users user = findUserByLogin(login);

        if (user == null) {
            model.addAttribute("message", "Пользователя с таким именем не существует!");
            model.addAttribute("toMainURL", "/sign");
            return "message";
        } else  {
            if (user.getPassword().equals(password)) {

                String token = tokenManager.generate(""+user.getId(), 10000000).toString();
                sender.sendMessage(user.getId() + ";" + token);

                return "redirect:http://localhost:8080/profile/" + token + "/";
            } else {
                model.addAttribute("message", "Неверный пароль!");
                model.addAttribute("toMainURL", "/sign");
                return "message";
            }
        }
    }

    private Users findUserByLogin(String login){
        Iterator<Users> iterator = usersRepository.findAll().iterator();
        boolean alreadyIs = false;
        Users user = null;
        while (iterator.hasNext()) {
            user = iterator.next();
            if (user.getLogin().equals(login)) {
                alreadyIs = true;
                return user;
            }
        }
        return null;
    }
}
