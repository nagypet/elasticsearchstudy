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

import hu.perit.elasticsearchstudy.db.elasticsearch.document.UserEntity;
import hu.perit.elasticsearchstudy.model.CreateUserRequest;
import hu.perit.elasticsearchstudy.model.SearchUserRequest;
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

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService
{
    private final ElasticsearchOperations elasticsearchOperations;

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
    public List<UserEntity> searchUser(SearchUserRequest request)
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

        return userEntities;
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
