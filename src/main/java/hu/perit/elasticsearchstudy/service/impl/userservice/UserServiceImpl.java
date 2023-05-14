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

package hu.perit.elasticsearchstudy.service.impl.userservice;

import hu.perit.elasticsearchstudy.db.elasticsearch.table.UserEntity;
import hu.perit.elasticsearchstudy.mapper.UserMapper;
import hu.perit.elasticsearchstudy.model.*;
import hu.perit.elasticsearchstudy.service.api.UserService;
import hu.perit.elasticsearchstudy.service.impl.CriteriaQueryFactory;
import hu.perit.spvitamin.spring.exception.ResourceAlreadyExistsException;
import hu.perit.spvitamin.spring.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.NoSuchIndexException;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
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
        log.debug("createUser({})", request);

        // Check if user exists
        SearchUserResponse searchUserResponse = searchUser(UserFilters.byCreateRequest(request));
        if (searchUserResponse.getTotal() != 0)
        {
            throw new ResourceAlreadyExistsException("The exact same user already exists!");
        }

        UserEntity userEntity = this.userMapper.mapEntityFromRequest(request);

        return this.elasticsearchOperations.save(userEntity);
    }

    @Override
    public SearchUserResponse searchUser(SearchUserRequest request)
    {
        log.debug("searchUser({})", request);

        return searchUser(UserFilters.bySearchRequest(request));
    }


    private SearchUserResponse searchUser(Filters filters)
    {
        log.debug("searchUser({})", filters);

        final List<UserEntity> userEntities = new ArrayList<>();

        try
        {
            CriteriaQuery criteriaQuery = CriteriaQueryFactory.allMatch(filters, 1000);
            SearchHits<UserEntity> results = this.elasticsearchOperations.search(criteriaQuery, UserEntity.class, IndexCoordinates.of(UserEntity.INDEX_NAME));
            userEntities.addAll(results.stream().map(SearchHit::getContent).toList());
        }
        catch (NoSuchIndexException e)
        {
            // just do nothing, return empty list
            log.error(e.toString());
        }

        return createUserSearchResponse(userEntities);
    }


    private SearchUserResponse createUserSearchResponse(List<UserEntity> userEntities)
    {
        SearchUserResponse searchUserResponse = new SearchUserResponse();

        searchUserResponse.setUsers(
                userEntities.stream()
                        .map(this.userMapper::mapDtoFromEntity)
                        .collect(Collectors.toList()));

        searchUserResponse.setTotal(userEntities.size());

        return searchUserResponse;
    }

    @Override
    public UserDTO getUser(String id) throws ResourceNotFoundException
    {
        UserEntity userEntity = this.elasticsearchOperations.get(id, UserEntity.class, IndexCoordinates.of(UserEntity.INDEX_NAME));
        if (userEntity == null)
        {
            throw new ResourceNotFoundException(MessageFormat.format("User not found by id ''{0}''", id));
        }
        return this.userMapper.mapDtoFromEntity(userEntity);
    }
}
