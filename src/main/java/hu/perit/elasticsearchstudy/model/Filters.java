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

package hu.perit.elasticsearchstudy.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Filters
{
    public static List<Filter> bySearchRequest(SearchUserRequest request)
    {
        return of(request.getFirstName(), request.getLastName(), request.getEmail(), Operator.LIKE);
    }

    public static List<Filter> byCreateRequest(CreateUserRequest request)
    {
        return of(request.getFirstName(), request.getLastName(), request.getEmail(), Operator.EQUALS);
    }

    public static List<Filter> of(String firstName, String lastName, String email, Operator operator)
    {
        List<Filter> filters = new ArrayList<>();

        if (StringUtils.isNotBlank(firstName))
        {
            filters.add(new Filter(Field.FIRSTNAME, operator, firstName));
        }

        if (StringUtils.isNotBlank(lastName))
        {
            filters.add(new Filter(Field.LASTNAME, operator, lastName));
        }

        if (StringUtils.isNotBlank(email))
        {
            filters.add(new Filter(Field.EMAIL, operator, email));
        }

        return filters;
    }
}
