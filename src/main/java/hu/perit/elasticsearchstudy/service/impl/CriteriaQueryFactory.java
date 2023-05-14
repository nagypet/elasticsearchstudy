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

package hu.perit.elasticsearchstudy.service.impl;

import hu.perit.elasticsearchstudy.model.Filter;
import hu.perit.elasticsearchstudy.model.Filters;
import hu.perit.elasticsearchstudy.model.Operation;
import jakarta.validation.ValidationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CriteriaQueryFactory
{
    public static CriteriaQuery allMatch(Filters filters, int limit)
    {
        validate(filters);

        Criteria criteria = Criteria.and();

        for (Filter filter : filters.getFilters())
        {
            if (!filter.isBlank())
            {
                if (filter.isMultipleQuery())
                {
                    criteria = criteria.subCriteria(getSubCriteria(filter));
                }
                else
                {
                    criteria = criteria.and(getSubCriteria(filter));
                }
            }
        }

        return new CriteriaQuery(criteria).setPageable(Pageable.ofSize(limit));
    }

    private static void validate(Filters filters)
    {
        boolean allFilterIsBlank = filters.getFilters().stream().anyMatch(Filter::isBlank);
        if (allFilterIsBlank)
        {
            throw new ValidationException();
        }
    }

    private static Criteria getSubCriteria(Filter filter)
    {
        if (filter.isMultipleQuery())
        {
            // Only this form works, please do not optimize!
            Criteria criteria = new Criteria(filter.getField());
            for (String query : filter.getQueries())
            {
                criteria = criteria.and(criteriaWithOperation(new Criteria(filter.getField()), filter.getOperation(), query));
            }
            return criteria;
        }
        else
        {
            return criteriaWithOperation(new Criteria(filter.getField()), filter.getOperation(), filter.getSimpleQuery());
        }
    }


    private static Criteria criteriaWithOperation(Criteria criteria, Operation operation, String query)
    {
        if (operation == Operation.EQUALS)
        {
            return criteria.is(query);
        }
        else
        {
            return criteria.contains(query);
        }
    }

    public static CriteriaQuery anyMatch(Filters filters, int limit)
    {
        validate(filters);

        Criteria criteria = Criteria.or();

        for (Filter filter : filters.getFilters())
        {
            if (!filter.isBlank())
            {
                if (filter.isMultipleQuery())
                {
                    criteria = criteria.subCriteria(getSubCriteria(filter));
                }
                else
                {
                    criteria = criteria.or(getSubCriteria(filter));
                }
            }
        }

        return new CriteriaQuery(criteria).setPageable(Pageable.ofSize(limit));
    }

}
