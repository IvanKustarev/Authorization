package com.example.authorization;

import com.example.authorization.models.Users;
import com.example.authorization.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Iterator;

@Controller
@RequestMapping("/")
public class AuthorizationController {
    @Autowired
    private UsersRepository usersRepository;

    @GetMapping("")
    public String login() {
        return "redirect:/sign";
    }

    @GetMapping("/registration")
    public String registrationGet() {
        return "registration";
    }

    @PostMapping("/registration")
    public String registrationPost(@RequestParam String login, @RequestParam String password, Model model) {
        Users user = findUserByLogin(login);
        if (user != null) {
            model.addAttribute("message", "Пользователь с таким именем уже есть!");
            return "registration";
        } else {
            //успешно зарегестрировался
            user = new Users(login, password);
            usersRepository.save(user);
            return "redirect:http://localhost:8082/profile/" + user.getId() + "/";
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
            return "registration";
        } else  {
            if (user.getPassword().equals(password)) {
                //успешно вошёл
                return "redirect:http://localhost:8082/profile/" + user.getId() + "/";
            } else {
                model.addAttribute("message", "Неверный пароль!");
            }
            return "sign";
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
