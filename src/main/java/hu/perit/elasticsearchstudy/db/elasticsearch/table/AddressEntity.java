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

package hu.perit.elasticsearchstudy.db.elasticsearch.table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@ToString
@Document(indexName = AddressEntity.INDEX_NAME)
public class AddressEntity
{
    public static final String INDEX_NAME = "address";
    public static final String FIELD_POSTCODE = "postcode";
    public static final String FIELD_CITY = "city";
    public static final String FIELD_STREET = "street";
    public static final String FIELD_DISTRICT = "district";
    public static final String FIELD_SEARCH = "search";

    @Id
    private String uuid;

    @Field(type = FieldType.Text, name = FIELD_POSTCODE)
    private String postCode;

    @Field(type = FieldType.Text, name = FIELD_CITY)
    private String city;

    @Field(type = FieldType.Text, name = FIELD_STREET)
    private String street;

    @Field(type = FieldType.Text, name = FIELD_DISTRICT)
    private String district;

    @Field(type = FieldType.Text, name = FIELD_SEARCH)
    private String searchField;

    public void createSearchField()
    {
        this.searchField = textField(this.postCode) + textField(this.city) + textField(this.street) + textField(this.district);
    }

    private String textField(String text)
    {
        if (StringUtils.isBlank(text))
        {
            return "";
        }
        return text.replaceAll("\\s", "");
    }
}
