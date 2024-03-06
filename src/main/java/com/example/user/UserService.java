package com.example.user;

import java.util.List;

public interface UserService {
    public abstract User add(User user);
    public abstract List<User> findAll();
    public abstract void deleteByEmail(String email);
    public abstract User findByEmail(String email);
    public abstract User update(User user);    
}
