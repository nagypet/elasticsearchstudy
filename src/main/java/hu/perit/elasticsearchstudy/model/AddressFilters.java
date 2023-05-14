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

import hu.perit.elasticsearchstudy.db.elasticsearch.table.AddressEntity;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class AddressFilters
{
    public static Filters byEntity(AddressEntity entity)
    {
        return of(entity.getPostCode(), entity.getCity(), entity.getStreet(), entity.getDistrict(), Operation.EQUALS);
    }

    public static Filters byQueryText(List<String> queries)
    {
        Filters filters = new Filters();

        if (!queries.isEmpty())
        {
            filters.add(new Filter(AddressEntity.FIELD_SEARCH, Operation.LIKE, queries));
        }

        return filters;
    }

    public static Filters of(String postCode, String city, String street, String district, Operation operation)
    {
        Filters filters = new Filters();

        if (StringUtils.isNotBlank(postCode))
        {
            filters.add(new Filter(AddressEntity.FIELD_POSTCODE, operation, postCode));
        }

        if (StringUtils.isNotBlank(city))
        {
            filters.add(new Filter(AddressEntity.FIELD_CITY, operation, city));
        }

        if (StringUtils.isNotBlank(street))
        {
            filters.add(new Filter(AddressEntity.FIELD_STREET, operation, street));
        }

        if (StringUtils.isNotBlank(district))
        {
            filters.add(new Filter(AddressEntity.FIELD_DISTRICT, operation, district));
        }

        return filters;
    }
}
