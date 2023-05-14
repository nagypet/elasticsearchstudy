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
import hu.perit.elasticsearchstudy.model.*;
import hu.perit.spvitamin.spring.exception.ResourceAlreadyExistsException;
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
    public static final String USER_INDEX_NAME = "user";
    private final ElasticsearchOperations elasticsearchOperations;
    private final UserMapper userMapper;

    @Override
    public UserEntity createUser(CreateUserRequest request)
    {
        log.debug("createUser({})", request);

        // Check if user exists
        List<Filter> filters = Filter.of(request.getFirstName(), request.getLastName(), request.getEmail(), Operator.EQUALS);
        SearchUserResponse searchUserResponse = searchUser(filters);
        if (searchUserResponse.getTotal() != 0)
        {
            throw new ResourceAlreadyExistsException("The exact same user already exists!");
        }

        UserEntity userEntity = this.userMapper.mapEntityFromRequest(request);
        UserEntity result = this.elasticsearchOperations.save(userEntity);

        return result;
    }

    @Override
    public SearchUserResponse searchUser(SearchUserRequest request)
    {
        log.debug("searchUser({})", request);

        List<Filter> filters = Filter.of(request.getFirstName(), request.getLastName(), null, Operator.LIKE);
        return searchUser(filters);
    }


    private SearchUserResponse searchUser(List<Filter> filters)
    {
        log.debug("searchUser({})", filters);

        final List<UserEntity> userEntities = new ArrayList<>();

        try
        {
            CriteriaQuery criteriaQuery = getCriteriaQuery(filters);
            SearchHits<UserEntity> results = this.elasticsearchOperations.search(criteriaQuery, UserEntity.class, IndexCoordinates.of(USER_INDEX_NAME));
            userEntities.addAll(results.stream().map(SearchHit::getContent).toList());
        }
        catch (NoSuchIndexException e)
        {
            // just do nothing, return empty list
            log.error(e.toString());
        }

        return createUserSearchResponse(userEntities);
    }

    private CriteriaQuery getCriteriaQuery(List<Filter> filters)
    {
        boolean thereIsAtLeastOneNonNullQuery = filters.stream().anyMatch(i -> StringUtils.isNotBlank(i.getQuery()));
        if (!thereIsAtLeastOneNonNullQuery)
        {
            throw new ValidationException();
        }

        Criteria criteria = new Criteria();

        for (Filter filter : filters)
        {
            if (StringUtils.isNotBlank(filter.getQuery()))
            {
                if (filter.getOperator() == Operator.EQUALS)
                {
                    criteria = criteria.and(filter.getField().getFieldName()).is(filter.getQuery());
                }
                else
                {
                    criteria = criteria.and(filter.getField().getFieldName()).contains(filter.getQuery());
                }
            }
        }

        return new CriteriaQuery(criteria).setPageable(Pageable.ofSize(1000));
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
        UserEntity userEntity = this.elasticsearchOperations.get(id, UserEntity.class, IndexCoordinates.of(USER_INDEX_NAME));
        if (userEntity == null)
        {
            throw new ResourceNotFoundException(MessageFormat.format("User not found by id ''{0}''", id));
        }
        return this.userMapper.mapDtoFromEntity(userEntity);
    }
}
