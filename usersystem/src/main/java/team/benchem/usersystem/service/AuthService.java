package team.benchem.usersystem.service;

import team.benchem.usersystem.entity.Channel;
import team.benchem.usersystem.entity.User;

import java.util.List;

public interface AuthService {

    String login(String username, String password);

    void logout(String username, String token);

    User getuser();
}
