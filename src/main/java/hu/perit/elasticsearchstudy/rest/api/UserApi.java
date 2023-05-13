/*
 * Copyright 2020-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hu.perit.elasticsearchstudy.rest.api;

import hu.perit.elasticsearchstudy.model.CreateUserRequest;
import hu.perit.elasticsearchstudy.model.UserResponse;
import hu.perit.elasticsearchstudy.model.SearchUserRequest;
import hu.perit.elasticsearchstudy.model.UserSearchResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public interface UserApi
{
    String BASE_URL = "/api/elasticsearch";

    @PostMapping(BASE_URL + "/create")
    @ResponseStatus(HttpStatus.CREATED)
    UserResponse createUser(@RequestBody @Valid CreateUserRequest userRequest);

    @PostMapping(path = {BASE_URL + "/search"})
    UserSearchResponse searchUsers(@RequestBody @Valid SearchUserRequest searchUserRequest);
}
