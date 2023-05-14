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

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class Filter
{
    private final String field;
    private final Operation operation;
    // If there are more items each must satisfy the operation. E.g. street like 'kert' AND street like 'sor'
    private final List<String> queries = new ArrayList<>();

    public Filter(String field, Operation operation, String query)
    {
        this.field = field;
        this.operation = operation;
        this.queries.add(query);
    }

    public Filter(String field, Operation operation, List<String> queries)
    {
        this.field = field;
        this.operation = operation;
        this.queries.addAll(queries);
    }

    public boolean isBlank()
    {
        return this.queries.isEmpty() || this.queries.stream().allMatch(StringUtils::isBlank);
    }

    public boolean isMultipleQuery()
    {
        return this.queries.size() > 1;
    }

    public String getSimpleQuery()
    {
        if (this.isBlank())
        {
            throw new IllegalStateException("List of query text is empty!");
        }

        return this.queries.get(0);
    }
}
