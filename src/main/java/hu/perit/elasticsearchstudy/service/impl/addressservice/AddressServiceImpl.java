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

package hu.perit.elasticsearchstudy.service.impl.addressservice;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import hu.perit.elasticsearchstudy.db.elasticsearch.table.AddressEntity;
import hu.perit.elasticsearchstudy.mapper.AddressMapper;
import hu.perit.elasticsearchstudy.model.AddressFilters;
import hu.perit.elasticsearchstudy.model.Filters;
import hu.perit.elasticsearchstudy.model.SearchAddressResponse;
import hu.perit.elasticsearchstudy.service.api.AddressService;
import hu.perit.elasticsearchstudy.service.impl.CriteriaQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.elasticsearch.NoSuchIndexException;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressServiceImpl implements AddressService
{
    private final ElasticsearchOperations elasticsearchOperations;
    private final AddressMapper addressMapper;

    public void loadFromFile(String fileName, String city) throws Exception
    {
        readLineByLine(fileName, city);
    }


    private void readLineByLine(String fileName, String city) throws Exception
    {
        try (InputStream resourceAsStream = this.getClass().getResourceAsStream("/" + fileName))
        {
            try (Reader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(resourceAsStream))))
            {
                try (CSVReader csvReader = new CSVReaderBuilder(reader)
                        .withCSVParser(new CSVParserBuilder()
                                .withSeparator(';')
                                .build()
                        ).build())
                {
                    String[] line;
                    while ((line = csvReader.readNext()) != null)
                    {
                        processLine(line, city);
                    }
                }
            }
        }
    }

    private void processLine(String[] line, String city)
    {
        if (line.length >= 9)
        {
            AddressEntity entity = new AddressEntity();
            entity.setPostCode(StringUtils.strip(line[0]));
            entity.setCity(city);
            entity.setStreet(StringUtils.strip(line[1]) + " " + StringUtils.strip(line[2]));
            entity.setDistrict(StringUtils.strip(line[8]));
            entity.createSearchField();
            log.debug(entity.toString());
            if (!addressExist(entity))
            {
                this.elasticsearchOperations.save(entity);
            }
        }
    }


    private boolean addressExist(AddressEntity entity)
    {
        SearchAddressResponse searchAddressResponse = searchAddress(AddressFilters.byEntity(entity), MatchType.ALL_MATCH);
        return searchAddressResponse.getTotal() != 0;
    }


    @Override
    public SearchAddressResponse searchTop100Address(String query)
    {
        String[] splits = query.split(" ");
        return searchAddress(AddressFilters.byQueryText(List.of(splits)), MatchType.ANY_MATCH);
    }

    private enum MatchType
    {
        ALL_MATCH,
        ANY_MATCH
    }

    private SearchAddressResponse searchAddress(Filters filters, MatchType matchType)
    {
        log.debug("searchAddress({})", filters);

        final List<AddressEntity> addressEntities = new ArrayList<>();

        try
        {
            CriteriaQuery criteriaQuery;
            if (matchType == MatchType.ALL_MATCH)
            {
                criteriaQuery = CriteriaQueryFactory.allMatch(filters, 100);
            }
            else
            {
                criteriaQuery = CriteriaQueryFactory.anyMatch(filters, 100);
            }
            SearchHits<AddressEntity> results = this.elasticsearchOperations.search(criteriaQuery, AddressEntity.class, IndexCoordinates.of(AddressEntity.INDEX_NAME));
            addressEntities.addAll(results.stream().map(SearchHit::getContent).toList());
        }
        catch (NoSuchIndexException e)
        {
            // just do nothing, return empty list
            log.error(e.toString());
        }

        return createSearchAddressResponse(addressEntities);
    }


    private SearchAddressResponse createSearchAddressResponse(List<AddressEntity> userEntities)
    {
        SearchAddressResponse searchAddressResponse = new SearchAddressResponse();

        searchAddressResponse.setAddresses(
                userEntities.stream()
                        .map(this.addressMapper::mapDtoFromEntity)
                        .collect(Collectors.toList()));

        searchAddressResponse.setTotal(userEntities.size());

        return searchAddressResponse;
    }
}
