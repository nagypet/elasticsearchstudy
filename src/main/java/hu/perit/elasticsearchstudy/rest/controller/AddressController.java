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

package hu.perit.elasticsearchstudy.rest.controller;

import hu.perit.elasticsearchstudy.config.Constants;
import hu.perit.elasticsearchstudy.model.SearchAddressResponse;
import hu.perit.elasticsearchstudy.rest.api.AddressApi;
import hu.perit.elasticsearchstudy.service.api.AddressService;
import hu.perit.spvitamin.spring.restmethodlogger.LoggedRestMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AddressController implements AddressApi
{
    private final AddressService addressService;

    @Override
    @LoggedRestMethod(eventId = Constants.ADDRESS_API_SEARCH, subsystem = Constants.SUBSYSTEM_NAME)
    public void loadFromFile() throws Exception
    {
        this.addressService.loadFromFile("addresses_budapest.csv", "Budapest");
    }

    @LoggedRestMethod(eventId = Constants.ADDRESS_API_SEARCH, subsystem = Constants.SUBSYSTEM_NAME)
    public SearchAddressResponse searchAddress(@RequestBody String query)
    {
        return addressService.searchTop100Address(query);
    }
}
