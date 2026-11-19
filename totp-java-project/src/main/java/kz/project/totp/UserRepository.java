package kz.project.totp;

import java.util.List;

public interface UserRepository {
    void save(User user) throws Exception;
    List<User> findAll() throws Exception;
    User findById(int id) throws Exception;
    User findByUsername(String username) throws Exception;
    int nextId() throws Exception;
}
