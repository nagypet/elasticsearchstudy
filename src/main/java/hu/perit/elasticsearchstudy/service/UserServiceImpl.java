package hu.perit.elasticsearchstudy.service;

import hu.perit.elasticsearchstudy.db.elasticsearch.document.User;
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
    public User createUser(CreateUserRequest request)
    {
        log.info("createUser({})", request);

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .build();

        User result = this.elasticsearchOperations.save(user);

        log.info("Creating user - End");

        return result;
    }

    @Override
    public List<User> searchUser(SearchUserRequest request)
    {
        log.debug("searchUser({})", request);

        final List<User> users = new ArrayList<>();

        try
        {
            SearchHits<User> results = this.elasticsearchOperations.search(getCriteriaQuery(request), User.class, IndexCoordinates.of("user"));
            users.addAll(results.stream().map(SearchHit::getContent).toList());
        }
        catch (NoSuchIndexException e)
        {
            // just do nothing, return empty list
            log.error(e.toString());
        }

        return users;
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
