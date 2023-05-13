package hu.perit.elasticsearchstudy.rest.controller;

import hu.perit.elasticsearchstudy.config.Constants;
import hu.perit.elasticsearchstudy.db.elasticsearch.document.User;
import hu.perit.elasticsearchstudy.model.CreateUserRequest;
import hu.perit.elasticsearchstudy.model.UserResponse;
import hu.perit.elasticsearchstudy.model.SearchUserRequest;
import hu.perit.elasticsearchstudy.model.UserSearchResponse;
import hu.perit.elasticsearchstudy.rest.api.UserApi;
import hu.perit.elasticsearchstudy.service.UserService;
import hu.perit.spvitamin.spring.restmethodlogger.LoggedRestMethod;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi
{

    private final UserService userService;

    @LoggedRestMethod(eventId = Constants.USER_API_CREATE, subsystem = Constants.SUBSYSTEM_NAME)
    public UserResponse createUser(@RequestBody @Valid CreateUserRequest userRequest)
    {
        User createdUser = userService.createUser(userRequest);

        return new UserResponse(createdUser.getUuid(), createdUser.getFirstName(), createdUser.getLastName(), createdUser.getEmail());
    }

    @LoggedRestMethod(eventId = Constants.USER_API_SEARCH, subsystem = Constants.SUBSYSTEM_NAME)
    public UserSearchResponse searchUsers(@RequestBody @Valid SearchUserRequest searchUserRequest)
    {
        List<User> users = userService.searchUser(searchUserRequest);

        return createUserSearchResponse(users);
    }

    private UserSearchResponse createUserSearchResponse(List<User> users)
    {
        UserSearchResponse userSearchResponse = new UserSearchResponse();

        userSearchResponse.setUsers(
                users.stream()
                        .map(user -> new UserResponse(user.getUuid(), user.getFirstName(), user.getLastName(), user.getEmail()))
                        .collect(Collectors.toList()));

        userSearchResponse.setTotal(users.size());

        return userSearchResponse;
    }
}
