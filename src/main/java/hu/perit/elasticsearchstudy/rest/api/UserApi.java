package hu.perit.elasticsearchstudy.rest.api;

import hu.perit.elasticsearchstudy.model.CreateUserRequest;
import hu.perit.elasticsearchstudy.model.UserResponse;
import hu.perit.elasticsearchstudy.model.SearchUserRequest;
import hu.perit.elasticsearchstudy.model.UserSearchResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface UserApi
{
    String BASE_URL = "/api/elasticsearch";

    @PostMapping(BASE_URL + "/create")
    UserResponse createUser(@RequestBody @Valid CreateUserRequest userRequest);

    @PostMapping(path = {BASE_URL + "/search"})
    UserSearchResponse searchUsers(@RequestBody @Valid SearchUserRequest searchUserRequest);
}
