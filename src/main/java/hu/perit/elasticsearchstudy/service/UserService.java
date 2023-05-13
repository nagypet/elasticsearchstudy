package hu.perit.elasticsearchstudy.service;

import hu.perit.elasticsearchstudy.model.CreateUserRequest;
import hu.perit.elasticsearchstudy.db.elasticsearch.document.User;
import hu.perit.elasticsearchstudy.model.SearchUserRequest;

import java.util.List;

public interface UserService
{
    User createUser(CreateUserRequest request);

    List<User> searchUser(SearchUserRequest request);
}
