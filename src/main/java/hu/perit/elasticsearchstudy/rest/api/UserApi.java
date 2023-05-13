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

import hu.perit.elasticsearchstudy.model.*;
import hu.perit.spvitamin.spring.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

public interface UserApi
{
    String BASE_URL = "/api/elasticsearch/user";

    @PostMapping(BASE_URL)
    @ResponseStatus(HttpStatus.CREATED)
    ResponseUri createUser(@RequestBody @Valid CreateUserRequest userRequest);

    @GetMapping(BASE_URL + "/{id}")
    UserDTO getUser(@Parameter(name = "User ID", required = true) @PathVariable("id") String id) throws ResourceNotFoundException;

    @PostMapping(path = {BASE_URL + "/search"})
    UserSearchResponse searchUsers(@RequestBody @Valid SearchUserRequest searchUserRequest);
}
