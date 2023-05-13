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

package hu.perit.elasticsearchstudy.service;

import hu.perit.elasticsearchstudy.db.elasticsearch.table.UserEntity;
import hu.perit.elasticsearchstudy.mapper.UserMapper;
import hu.perit.elasticsearchstudy.model.CreateUserRequest;
import hu.perit.elasticsearchstudy.model.SearchUserRequest;
import hu.perit.elasticsearchstudy.model.UserDTO;
import hu.perit.elasticsearchstudy.model.UserSearchResponse;
import hu.perit.spvitamin.spring.exception.ResourceNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.NoSuchIndexException;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService
{
    private final ElasticsearchOperations elasticsearchOperations;
    private final UserMapper userMapper;

    @Override
    public UserEntity createUser(CreateUserRequest request)
    {
        log.info("createUser({})", request);

        UserEntity userEntity = UserEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .build();

        UserEntity result = this.elasticsearchOperations.save(userEntity);

        log.info("Creating user - End");

        return result;
    }

    @Override
    public UserSearchResponse searchUser(SearchUserRequest request)
    {
        log.debug("searchUser({})", request);

        final List<UserEntity> userEntities = new ArrayList<>();

        try
        {
            SearchHits<UserEntity> results = this.elasticsearchOperations.search(getCriteriaQuery(request), UserEntity.class, IndexCoordinates.of("user"));
            userEntities.addAll(results.stream().map(SearchHit::getContent).toList());
        }
        catch (NoSuchIndexException e)
        {
            // just do nothing, return empty list
            log.error(e.toString());
        }

        return createUserSearchResponse(userEntities);
    }

    private UserSearchResponse createUserSearchResponse(List<UserEntity> userEntities)
    {
        UserSearchResponse userSearchResponse = new UserSearchResponse();

        userSearchResponse.setUsers(
                userEntities.stream()
                        .map(this.userMapper::map)
                        .collect(Collectors.toList()));

        userSearchResponse.setTotal(userEntities.size());

        return userSearchResponse;
    }

    @Override
    public UserDTO getUser(String id) throws ResourceNotFoundException
    {
        UserEntity userEntity = this.elasticsearchOperations.get(id, UserEntity.class, IndexCoordinates.of("user"));
        if (userEntity == null)
        {
            throw new ResourceNotFoundException(MessageFormat.format("User not found by id ''{0}''", id));
        }
        return this.userMapper.map(userEntity);
    }


    private CriteriaQuery getCriteriaQuery(SearchUserRequest request)
    {
        if (StringUtils.isAllBlank(request.getFirstName(), request.getLastName()))
        {
            throw new ValidationException();
        }

        Criteria criteria = new Criteria();

        if (StringUtils.isNotBlank(request.getFirstName()))
        {
            criteria = criteria.or("firstname").contains(request.getFirstName());
        }

        if (StringUtils.isNotBlank(request.getLastName()))
        {
            criteria = criteria.and("lastname").contains(request.getLastName());
        }

        return new CriteriaQuery(criteria).setPageable(Pageable.ofSize(1000));
    }
}
