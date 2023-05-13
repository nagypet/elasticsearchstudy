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

package hu.perit.elasticsearchstudy.rest.controller;

import hu.perit.elasticsearchstudy.config.Constants;
import hu.perit.elasticsearchstudy.db.elasticsearch.table.UserEntity;
import hu.perit.elasticsearchstudy.model.*;
import hu.perit.elasticsearchstudy.rest.api.UserApi;
import hu.perit.elasticsearchstudy.service.UserService;
import hu.perit.spvitamin.spring.exception.ResourceNotFoundException;
import hu.perit.spvitamin.spring.restmethodlogger.LoggedRestMethod;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi
{
    private final UserService userService;

    @LoggedRestMethod(eventId = Constants.USER_API_CREATE, subsystem = Constants.SUBSYSTEM_NAME)
    public ResponseUri createUser(@RequestBody @Valid CreateUserRequest userRequest)
    {
        UserEntity userEntity = userService.createUser(userRequest);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(userEntity.getUuid()).toUri();
        return new ResponseUri().location(location.toString());
    }

    @Override
    @LoggedRestMethod(eventId = Constants.USER_API_GET, subsystem = Constants.SUBSYSTEM_NAME)
    public UserDTO getUser(String id) throws ResourceNotFoundException
    {
        return this.userService.getUser(id);
    }

    @LoggedRestMethod(eventId = Constants.USER_API_SEARCH, subsystem = Constants.SUBSYSTEM_NAME)
    public UserSearchResponse searchUsers(@RequestBody @Valid SearchUserRequest searchUserRequest)
    {
        return userService.searchUser(searchUserRequest);
    }
}
